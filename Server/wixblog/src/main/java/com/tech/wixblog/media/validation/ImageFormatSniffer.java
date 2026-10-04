package com.tech.wixblog.media.validation;

import java.nio.charset.StandardCharsets;

/**
 * Reads image container formats from a file's leading bytes.
 * <p>
 * This deliberately does not use {@code ImageIO.getImageReaders()} or a
 * {@code Files.probeContentType()} call, because both can be influenced by
 * classpath plugins in ways that are hard to audit. A short, explicit table of
 * magic numbers is easier to verify and cannot be talked into accepting a format
 * the operator did not intend to allow.
 * <p>
 * Every value here is a fixed offset comparison against bytes the client supplied,
 * so the only thing an attacker controls is which branch is taken, never what the
 * comparison is.
 */
public final class ImageFormatSniffer {

    private ImageFormatSniffer () {
    }

    /**
     * Number of leading bytes needed to identify every format in the table.
     * A file shorter than this cannot be a valid image.
     */
    public static final int REQUIRED_HEADER_BYTES = 16;

    public static ImageFormat.Detection detect (byte[] header) {
        if (header == null || header.length < REQUIRED_HEADER_BYTES) {
            return ImageFormat.Detection.unrecognised();
        }
        if (isAvif(header)) {
            return ImageFormat.Detection.unsupported(
                    null,
                    "AVIF images are not supported. Please upload JPEG, PNG, WebP or GIF."
            );
        }
        if (isJpeg(header)) {
            return ImageFormat.Detection.of(ImageFormat.JPEG);
        }
        if (isPng(header)) {
            return ImageFormat.Detection.of(ImageFormat.PNG);
        }
        if (isGif(header)) {
            return ImageFormat.Detection.of(ImageFormat.GIF);
        }
        if (isWebp(header)) {
            return ImageFormat.Detection.of(ImageFormat.WEBP);
        }
        return ImageFormat.Detection.unrecognised();
    }

    private static boolean isJpeg (byte[] header) {
        // FF D8 FF
        return unsigned(header[0]) == 0xFF
                && unsigned(header[1]) == 0xD8
                && unsigned(header[2]) == 0xFF;
    }

    private static boolean isPng (byte[] header) {
        // 89 'P' 'N' 'G' 0D 0A 1A 0A
        return unsigned(header[0]) == 0x89
                && header[1] == 'P'
                && header[2] == 'N'
                && header[3] == 'G'
                && unsigned(header[4]) == 0x0D
                && unsigned(header[5]) == 0x0A
                && unsigned(header[6]) == 0x1A
                && unsigned(header[7]) == 0x0A;
    }

    private static boolean isGif (byte[] header) {
        // 'GIF87a' or 'GIF89a'
        if (header[0] != 'G' || header[1] != 'I' || header[2] != 'F') {
            return false;
        }
        String version = new String(header, 3, 3, StandardCharsets.US_ASCII);
        return "87a".equals(version) || "89a".equals(version);
    }

    private static boolean isWebp (byte[] header) {
        // 'RIFF' <4-byte little-endian size> 'WEBP'
        if (!matchesAscii(header, 0, "RIFF") || !matchesAscii(header, 8, "WEBP")) {
            return false;
        }
        // The declared chunk size must at least cover the 12-byte RIFF header.
        int chunkSize = (unsigned(header[4])
                | (unsigned(header[5]) << 8)
                | (unsigned(header[6]) << 16)
                | (unsigned(header[7]) << 24));
        return chunkSize >= 4;
    }

    private static boolean isAvif (byte[] header) {
        // ISO base media file: <4-byte box size> 'ftyp' <major brand>
        if (!matchesAscii(header, 4, "ftyp")) {
            return false;
        }
        String brand = new String(header, 8, 4, StandardCharsets.US_ASCII);
        return "avif".equals(brand) || "avis".equals(brand);
    }

    private static boolean matchesAscii (byte[] header, int offset, String expected) {
        if (header.length < offset + expected.length()) {
            return false;
        }
        for (int i = 0; i < expected.length(); i++) {
            if (header[offset + i] != (byte) expected.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static int unsigned (byte value) {
        return value & 0xFF;
    }
}