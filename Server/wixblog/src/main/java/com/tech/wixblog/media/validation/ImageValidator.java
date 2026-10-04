package com.tech.wixblog.media.validation;

import com.tech.wixblog.common.exception.InvalidRequestException;
import com.tech.wixblog.common.exception.PayloadTooLargeException;
import com.tech.wixblog.common.exception.UnsupportedMediaException;
import com.tech.wixblog.media.config.MediaPolicyResolver;
import com.tech.wixblog.media.config.MediaProperties;
import com.tech.wixblog.media.domain.MediaScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

/**
 * Gatekeeper for every uploaded image.
 * <p>
 * The order of the checks matters. The declared {@code Content-Type} is inspected
 * early only as a cheap pre-filter — it is an attacker-controlled string and is never
 * treated as evidence of anything. The authoritative check is
 * {@link ImageFormatSniffer} reading the file's own magic bytes, followed by an
 * actual decode through {@code ImageIO}. A file called {@code photo.png} that is
 * really a shell script, or a ZIP archive, or an SVG (which is a script vector, not
 * an image) is rejected here.
 * <p>
 * This component is stateless and safe to share.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageValidator {

    private final MediaProperties mediaProperties;
    private final MediaPolicyResolver policyResolver;

    /**
     * Fully validated image, ready to be persisted.
     *
     * @param content  bytes to write, after optional re-encoding
     * @param format   format as determined by the bytes, not by the filename
     */
    public record ValidatedImage(
            byte[] content,
            ImageFormat format,
            String contentType,
            String extension,
            Integer width,
            Integer height,
            boolean sanitised
                               ) {

        public long sizeBytes () {
            return content.length;
        }
    }

    /**
     * Runs the full validation pipeline and returns the bytes to persist.
     *
     * @throws PayloadTooLargeException    when the file exceeds the scope's ceiling
     * @throws UnsupportedMediaException  when the type is not accepted, or the bytes
     *                                    are not the image they claim to be
     */
    public ValidatedImage validate (MultipartFile file, MediaScope scope) {
        requirePresent(file);
        enforceSize(file, scope);
        byte[] content = readBytes(file);

        ImageFormat.Detection detection = sniff(content);
        enforceAcceptedFormat(detection, scope);
        enforceDeclaredTypeAgrees(file, detection, scope);

        BufferedImage decoded = decode(content, detection);
        int width = decoded.getWidth();
        int height = decoded.getHeight();
        enforcePixelBudget(width, height, scope);

        ImageFormat format = detection.format();
        byte[] payload = content;
        boolean sanitised = false;

        if (mediaProperties.sanitizeEnabled() && format.isSanitisable()) {
            byte[] reEncoded = reEncode(decoded, format);
            if (reEncoded != null) {
                payload = reEncoded;
                sanitised = true;
            }
        }

        return new ValidatedImage(
                payload,
                format,
                format.getContentType(),
                format.getExtension(),
                width,
                height,
                sanitised
                             );
    }

    /* ------------------------------------------------------------------ */
    /* Steps                                                */
    /* ------------------------------------------------------------------ */

    /**
     * A present-but-empty part is reported as {@code 400}, not {@code 415}: the
     * endpoint does accept uploads, so there is no unsupported media type here, and
     * the bytes simply carry no type at all.
     */
    private void requirePresent (MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException(
                    "The uploaded file is empty."
            );
        }
    }

    private void enforceSize (MultipartFile file, MediaScope scope) {
        DataSize maxSize = policyResolver.maxSizeFor(scope);
        if (file.getSize() > maxSize.toBytes()) {
            throw new PayloadTooLargeException(
                    "The uploaded file is larger than the %s limit of %s."
                            .formatted(scope, humanReadable(maxSize))
            );
        }
    }

    private byte[] readBytes (MultipartFile file) {
        try (InputStream in = file.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream(
                     Math.max(64, (int) Math.min(file.getSize(), Integer.MAX_VALUE)))) {
            in.transferTo(out);
            return out.toByteArray();
        } catch (IOException exception) {
            log.warn("Failed to read uploaded part '{}'", file.getOriginalFilename(), exception);
            throw new UnsupportedMediaException(
                    "The uploaded file could not be read."
            );
        }
    }

    private ImageFormat.Detection sniff (byte[] content) {
        byte[] header = new byte[ImageFormatSniffer.REQUIRED_HEADER_BYTES];
        int headerLength = Math.min(header.length, content.length);
        System.arraycopy(content, 0, header, 0, headerLength);
        return ImageFormatSniffer.detect(header);
    }

    private void enforceAcceptedFormat (ImageFormat.Detection detection, MediaScope scope) {
        if (!detection.detected()) {
            throw new UnsupportedMediaException(
                    detection.rejection() == null
                            ? "The uploaded file is not a supported image."
                            : detection.rejection()
            );
        }
        Set<String> accepted = policyResolver.acceptedContentTypesFor(scope);
        if (!accepted.contains(detection.format().getContentType())) {
            throw new UnsupportedMediaException(
                    "%s is not accepted for %s. Allowed types: %s."
                            .formatted(
                                    detection.format().getContentType(),
                                    scope,
                                    policyResolver.describeAcceptedContentTypes(scope)
                            )
            );
        }
    }

    /**
     * Cross-checks what the client claimed against what the bytes actually are.
     * A mismatch is treated as an error rather than silently trusted, because it is
     * the signature of either a broken client or an attempt to smuggle content past
     * an upstream filter that keys off the declared type.
     */
    private void enforceDeclaredTypeAgrees (
            MultipartFile file,
            ImageFormat.Detection detection,
            MediaScope scope
                                         ) {
        String declared = file.getContentType();
        if (declared == null || declared.isBlank()) {
            return;
        }
        String normalised = declared.toLowerCase(Locale.ROOT).trim();
        int separator = normalised.indexOf(';');
        if (separator > -1) {
            normalised = normalised.substring(0, separator).trim();
        }
        String actual = detection.format().getContentType();
        if (normalised.equals(actual)) {
            return;
        }
        // Some browsers send application/octet-stream for less common types.
        if ("application/octet-stream".equals(normalised)) {
            return;
        }
        throw new UnsupportedMediaException(
                "The file declares content type '%s' but its bytes are %s."
                        .formatted(normalised, actual)
        );
    }

    private BufferedImage decode (byte[] content, ImageFormat.Detection detection) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(content));
            if (image == null) {
                throw new UnsupportedMediaException(
                        "The uploaded file could not be decoded as a %s image."
                                .formatted(detection.format().getContentType())
                );
            }
            return image;
        } catch (IOException exception) {
            log.warn("Failed to decode uploaded image as {}", detection.format().getContentType(), exception);
            throw new UnsupportedMediaException(
                    "The uploaded file is a corrupt or truncated image."
            );
        }
    }

    /**
     * Guards against decompression bombs: a small, highly compressed file can expand
     * to an enormous bitmap and exhaust heap during decode. Decoding already happened
     * at this point, so this check is a backstop that keeps such files out of storage
     * and out of every downstream resize operation.
     */
    private void enforcePixelBudget (int width, int height, MediaScope scope) {
        long pixels = (long) width * (long) height;
        long maxPixels = 40_000_000L;
        if (pixels > maxPixels) {
            throw new PayloadTooLargeException(
                    "The image is %dx%d pixels, which exceeds the %s limit of %d million pixels."
                            .formatted(width, height, scope, maxPixels / 1_000_000)
            );
        }
    }

    /**
     * Writes the decoded image back out in the same format. This is what strips EXIF
     * metadata — including the GPS coordinates phone cameras embed by default — and
     * collapses files that are valid images and valid archives simultaneously.
     * <p>
     * The output is re-sniffed so that a WebP re-encode which the installed plugin
     * declined to write cannot be stored under a {@code .webp} name while actually
     * containing something else.
     */
    private byte[] reEncode (BufferedImage image, ImageFormat format) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            boolean written = ImageIO.write(image, format.getImageIoFormatName(), out);
            if (!written) {
                log.warn(
                        "No ImageIO writer available for format {}; storing the original bytes instead.",
                        format.getImageIoFormatName()
                );
                return null;
            }
            byte[] reEncoded = out.toByteArray();
            ImageFormat.Detection verification = sniff(reEncoded);
            if (!verification.detected() || verification.format() != format) {
                log.warn(
                        "Re-encoding to {} produced {}; keeping the original bytes.",
                        format,
                        verification.detected() ? verification.format() : "an unrecognised format"
                );
                return null;
            }
            return reEncoded;
        } catch (IOException exception) {
            log.warn("Failed to re-encode image as {}", format.getImageIoFormatName(), exception);
            return null;
        }
    }

    private String humanReadable (DataSize size) {
        return size.toString();
    }
}