package com.tech.wixblog.media.validation;

/**
 * Image container formats this application is able to recognise.
 * <p>
 * {@link #WEBP} is readable because TwelveMonKeys {@code imageio-webp} is on the
 * classpath; the JDK's own {@code ImageIO} has no WebP reader.
 * <p>
 * {@link #AVIF} is recognised but deliberately not supported: there is no TwelveMonKeys
 * AVIF plugin, so {@code ImageIO} cannot decode it. It is enumerated anyway so that
 * an AVIF upload receives an explicit "not supported" message rather than a generic
 * "unreadable image".
 */
public enum ImageFormat {

    JPEG(
            "jpeg",
            "image/jpeg",
            "jpg",
            true
                    ),
    PNG(
            "png",
            "image/png",
            "png",
            true
                    ),
    GIF(
            "gif",
            "image/gif",
            "gif",
            false
                    ),
    WEBP(
            "webp",
            "image/webp",
            "webp",
            true
                    );

    private final String imageIoFormatName;
    private final String contentType;
    private final String extension;
    private final boolean sanitisable;

    ImageFormat (
            String imageIoFormatName,
            String contentType,
            String extension,
            boolean sanitisable
                              ) {
        this.imageIoFormatName = imageIoFormatName;
        this.contentType = contentType;
        this.extension = extension;
        this.sanitisable = sanitisable;
    }

    public String getImageIoFormatName () {
        return imageIoFormatName;
    }

    public String getContentType () {
        return contentType;
    }

    public String getExtension () {
        return extension;
    }

    /**
     * Whether re-encoding is safe for this format. GIF is excluded because decoding
     * to a single {@code BufferedImage} would discard the animation.
     */
    public boolean isSanitisable () {
        return sanitisable;
    }

    /**
     * Result of sniffing a file's leading bytes.
     *
     * @param format     the detected format, or {@code null} when unrecognised
     * @param detected   whether the format is one this application can actually decode
     * @param rejection  human-readable reason when {@code detected} is {@code false}
     */
    public record Detection(ImageFormat format, boolean detected, String rejection) {

        public static Detection of (ImageFormat format) {
            return new Detection(format, true, null);
        }

        public static Detection unsupported (ImageFormat format, String rejection) {
            return new Detection(format, false, rejection);
        }

        public static Detection unrecognised () {
            return new Detection(null, false, "The file is not a recognised image format.");
        }
    }
}