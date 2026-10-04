package com.tech.wixblog.media.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ImageFormatSnifferTest {

    @Test
    @DisplayName("detects PNG from its 8-byte signature")
    void detectsPng () {
        byte[] header = pad(new byte[]{
                (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A
        });
        ImageFormat.Detection detection = ImageFormatSniffer.detect(header);

        assertThat(detection.detected()).isTrue();
        assertThat(detection.format()).isEqualTo(ImageFormat.PNG);
    }

    @Test
    @DisplayName("detects JPEG from its FF D8 FF prefix")
    void detectsJpeg () {
        byte[] header = pad(new byte[]{
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0
        });
        ImageFormat.Detection detection = ImageFormatSniffer.detect(header);

        assertThat(detection.detected()).isTrue();
        assertThat(detection.format()).isEqualTo(ImageFormat.JPEG);
    }

    @Test
    @DisplayName("detects both GIF versions")
    void detectsGif () {
        for (String version : Arrays.asList("GIF87a", "GIF89a")) {
            byte[] header = pad(version.getBytes(StandardCharsets.US_ASCII));
            ImageFormat.Detection detection = ImageFormatSniffer.detect(header);

            assertThat(detection.detected())
                    .as("version %s", version)
                    .isTrue();
            assertThat(detection.format()).isEqualTo(ImageFormat.GIF);
        }
    }

    @Test
    @DisplayName("detects WebP from the RIFF/WEBP container")
    void detectsWebp () {
        byte[] header = pad(new byte[]{
                'R', 'I', 'F', 'F',
                0x20, 0x00, 0x00, 0x00,   // chunk size, little endian, >= 4
                'W', 'E', 'B', 'P',
                'V', 'P', '8', ' '
        });
        ImageFormat.Detection detection = ImageFormatSniffer.detect(header);

        assertThat(detection.detected()).isTrue();
        assertThat(detection.format()).isEqualTo(ImageFormat.WEBP);
    }

    @Test
    @DisplayName("rejects a RIFF container whose chunk size is too small to be WebP")
    void rejectsTruncatedRiff () {
        byte[] header = pad(new byte[]{
                'R', 'I', 'F', 'F',
                0x00, 0x00, 0x00, 0x00,
                'W', 'E', 'B', 'P'
        });

        assertThat(ImageFormatSniffer.detect(header).detected()).isFalse();
    }

    @Test
    @DisplayName("recognises AVIF and rejects it with an explicit reason")
    void detectsAndRejectsAvif () {
        byte[] header = pad(new byte[]{
                0x00, 0x00, 0x00, 0x20,
                'f', 't', 'y', 'p',
                'a', 'v', 'i', 'f'
        });
        ImageFormat.Detection detection = ImageFormatSniffer.detect(header);

        assertThat(detection.detected()).isFalse();
        assertThat(detection.rejection())
                .contains("AVIF")
                .contains("not supported");
    }

    @Test
    @DisplayName("rejects a file whose magic bytes claim PNG but which is really text")
    void rejectsDisguisedScript () {
        // The classic attack: a shell script or HTML document named photo.png.
        byte[] header = pad("#!/bin/sh\nrm -rf /\n".getBytes(StandardCharsets.UTF_8));

        assertThat(ImageFormatSniffer.detect(header).detected()).isFalse();
    }

    @Test
    @DisplayName("rejects SVG, which is a script vector rather than a raster image")
    void rejectsSvg () {
        byte[] header = pad(
                "<svg xmlns=\"http://www.w3.org/2000/svg\">"
                        .getBytes(StandardCharsets.UTF_8));

        assertThat(ImageFormatSniffer.detect(header).detected()).isFalse();
    }

    @Test
    @DisplayName("rejects a header too short to identify")
    void rejectsShortHeader () {
        assertThat(ImageFormatSniffer.detect(new byte[]{(byte) 0x89, 'P'}).detected()).isFalse();
        assertThat(ImageFormatSniffer.detect(new byte[0]).detected()).isFalse();
        assertThat(ImageFormatSniffer.detect(null).detected()).isFalse();
    }

    private byte[] pad (byte[] source) {
        byte[] header = new byte[ImageFormatSniffer.REQUIRED_HEADER_BYTES];
        System.arraycopy(source, 0, header, 0, Math.min(source.length, header.length));
        return header;
    }
}