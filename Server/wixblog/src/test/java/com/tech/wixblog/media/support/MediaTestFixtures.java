package com.tech.wixblog.media.support;

import com.tech.wixblog.media.config.MediaProperties;
import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.domain.MediaStorageType;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

/**
 * Shared fixtures for the media test suite.
 */
public final class MediaTestFixtures {

    private MediaTestFixtures () {
    }

    public static MediaProperties properties (
            boolean sanitizeEnabled,
            Map<MediaScope, MediaProperties.ScopeOverrides> scopes
                                        ) {
        return new MediaProperties(
                MediaStorageType.LOCAL,
                "./target/test-media",
                "/media",
                "http://localhost:8080/api/v1",
                false,
                sanitizeEnabled,
                Duration.ofDays(30),
                scopes,
                new MediaProperties.Cleanup(false, Duration.ofHours(24), Duration.ofHours(24), 100)
                                  );
    }

    public static MediaProperties defaultProperties () {
        return properties(true, Map.of());
    }

    public static byte[] pngBytes (int width, int height) {
        return encode(width, height, "png");
    }

    public static byte[] jpegBytes (int width, int height) {
        return encode(width, height, "jpeg");
    }

    private static byte[] encode (int width, int height, String format) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(0, 0, width, height);
        } finally {
            graphics.dispose();
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (!ImageIO.write(image, format, out)) {
                throw new IllegalStateException("No ImageIO writer for " + format);
            }
            return out.toByteArray();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    /**
     * A syntactically valid PNG signature followed by bytes that are not image data,
     * i.e. exactly what a polyglot or simply corrupt upload looks like.
     */
    public static byte[] corruptPngBytes () {
        byte[] header = {
                (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A
        };
        byte[] junk = new byte[512];
        for (int i = 0; i < junk.length; i++) {
            junk[i] = (byte) (i % 251);
        }
        byte[] combined = new byte[header.length + junk.length];
        System.arraycopy(header, 0, combined, 0, header.length);
        System.arraycopy(junk, 0, combined, header.length, junk.length);
        return combined;
    }

    public static MediaProperties.ScopeOverrides scopeOverride (
            org.springframework.util.unit.DataSize maxSize,
            Set<String> contentTypes
                                                      ) {
        return new MediaProperties.ScopeOverrides(maxSize, contentTypes);
    }
}