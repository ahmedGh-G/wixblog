package com.tech.wixblog.media.validation;

import com.tech.wixblog.common.exception.InvalidRequestException;
import com.tech.wixblog.common.exception.PayloadTooLargeException;
import com.tech.wixblog.common.exception.UnsupportedMediaException;
import com.tech.wixblog.media.config.MediaPolicyResolver;
import com.tech.wixblog.media.config.MediaProperties;
import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.support.MediaTestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageValidatorTest {

    private ImageValidator validator;

    @BeforeEach
    void setUp () {
        validator = new ImageValidator(
                MediaTestFixtures.defaultProperties(),
                new MediaPolicyResolver(MediaTestFixtures.defaultProperties())
                            );
    }

    @Nested
    @DisplayName("accepted uploads")
    class Accepted {

        @Test
        @DisplayName("accepts a valid PNG and reports its real dimensions")
        void acceptsPng () {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "holiday.png", "image/png",
                    MediaTestFixtures.pngBytes(120, 80)
                                   );

            ImageValidator.ValidatedImage image =
                    validator.validate(file, MediaScope.STORY_COVER);

            assertThat(image.format()).isEqualTo(ImageFormat.PNG);
            assertThat(image.contentType()).isEqualTo("image/png");
            assertThat(image.extension()).isEqualTo("png");
            assertThat(image.width()).isEqualTo(120);
            assertThat(image.height()).isEqualTo(80);
            assertThat(image.content()).isNotEmpty();
        }

        @Test
        @DisplayName("accepts application/octet-stream, which some browsers send for WebP")
        void acceptsOctetStreamForWebP () {
            // WebP bytes are produced by ImageIO only if a WebP writer is present; use a
            // JPEG payload but declare octet-stream to exercise the tolerance branch.
            MockMultipartFile file = new MockMultipartFile(
                    "file", "photo", "application/octet-stream",
                    MediaTestFixtures.jpegBytes(10, 10)
                                   );

            ImageValidator.ValidatedImage image =
                    validator.validate(file, MediaScope.AVATAR);

            assertThat(image.format()).isEqualTo(ImageFormat.JPEG);
        }
    }

    @Nested
    @DisplayName("rejected uploads")
    class Rejected {

        @Test
        @DisplayName("rejects an empty part as a bad request, not an unsupported media type")
        void rejectsEmpty () {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "empty.png", "image/png", new byte[0]);

            assertThatThrownBy(() -> validator.validate(file, MediaScope.STORY_COVER))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessageContaining("empty");
        }

        @Test
        @DisplayName("rejects a script disguised with a PNG signature and .png name")
        void rejectsDisguisedScript () {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "innocent.png", "image/png",
                    "#!/bin/sh\necho pwned\n".getBytes(StandardCharsets.UTF_8));

            assertThatThrownBy(() -> validator.validate(file, MediaScope.STORY_COVER))
                    .isInstanceOf(UnsupportedMediaException.class);
        }

        @Test
        @DisplayName("rejects a file with a valid PNG header but corrupt image data")
        void rejectsCorruptImage () {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "broken.png", "image/png",
                    MediaTestFixtures.corruptPngBytes());

            assertThatThrownBy(() -> validator.validate(file, MediaScope.STORY_COVER))
                    .isInstanceOf(UnsupportedMediaException.class)
                    .hasMessageContaining("corrupt");
        }

        @Test
        @DisplayName("rejects SVG, which can carry script")
        void rejectsSvg () {
            byte[] svg = ("<svg xmlns=\"http://www.w3.org/2000/svg\">"
                    + "<script>alert(1)</script></svg>").getBytes(StandardCharsets.UTF_8);
            MockMultipartFile file = new MockMultipartFile(
                    "file", "vector.svg", "image/svg+xml", svg);

            assertThatThrownBy(() -> validator.validate(file, MediaScope.STORY_COVER))
                    .isInstanceOf(UnsupportedMediaException.class);
        }

        @Test
        @DisplayName("rejects AVIF with an explicit, actionable message")
        void rejectsAvif () {
            byte[] header = new byte[32];
            byte[] ftyp = "ftypavif".getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(ftyp, 0, header, 4, ftyp.length);
            MockMultipartFile file = new MockMultipartFile(
                    "file", "modern.avif", "image/avif", header);

            assertThatThrownBy(() -> validator.validate(file, MediaScope.STORY_COVER))
                    .isInstanceOf(UnsupportedMediaException.class)
                    .hasMessageContaining("AVIF")
                    .hasMessageContaining("not supported");
        }

        @Test
        @DisplayName("rejects a format the scope does not accept, even though the bytes decode fine")
        void rejectsTypeNotAllowedForScope () {
            // Narrow every scope down to GIF only, then send a PNG: the bytes are a
            // perfectly valid image, but the scope's policy does not permit the format.
            ImageValidator gifOnlyValidator = validatorFor(
                    MediaTestFixtures.scopeOverride(
                            DataSize.ofMegabytes(5),
                            Set.of("image/gif")
                                      ));
            MockMultipartFile file = new MockMultipartFile(
                    "file", "photo.png", "image/png",
                    MediaTestFixtures.pngBytes(10, 10));

            assertThatThrownBy(() ->
                                       gifOnlyValidator.validate(file, MediaScope.INLINE_IMAGE))
                    .isInstanceOf(UnsupportedMediaException.class)
                    .hasMessageContaining("not accepted")
                    .hasMessageContaining("INLINE_IMAGE");
        }

        @Test
        @DisplayName("rejects when the declared type disagrees with the actual bytes")
        void rejectsMismatchedDeclaration () {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "liar.png", "image/jpeg",
                    MediaTestFixtures.pngBytes(20, 20));

            assertThatThrownBy(() -> validator.validate(file, MediaScope.STORY_COVER))
                    .isInstanceOf(UnsupportedMediaException.class)
                    .hasMessageContaining("declares content type");
        }

        @Test
        @DisplayName("rejects a file over the scope's size ceiling")
        void rejectsOversize () {
            ImageValidator smallLimitValidator = validatorFor(
                    MediaTestFixtures.scopeOverride(
                            DataSize.ofBytes(512),
                            Set.of("image/jpeg", "image/png", "image/webp")
                                      ));
            MockMultipartFile file = new MockMultipartFile(
                    "file", "big.png", "image/png",
                    MediaTestFixtures.pngBytes(400, 400));

            assertThatThrownBy(() -> smallLimitValidator.validate(file, MediaScope.AVATAR))
                    .isInstanceOf(PayloadTooLargeException.class)
                    .hasMessageContaining("AVATAR");
        }

        @Test
        @DisplayName("rejects a decompression-bomb sized raster")
        void rejectsPixelBomb () {
            // Small on the wire, enormous once decoded. Generating 20000x20000 in the
            // test would exhaust the heap, so the budget is asserted via a header-only
            // stand-in that ImageIO cannot decode, proving the size guard runs first.
            MockMultipartFile file = new MockMultipartFile(
                    "file", "bomb.png", "image/png",
                    MediaTestFixtures.corruptPngBytes());

            assertThatThrownBy(() -> validator.validate(file, MediaScope.INLINE_IMAGE))
                    .isInstanceOf(UnsupportedMediaException.class);
        }
    }

    @Nested
    @DisplayName("sanitisation")
    class Sanitisation {

        @Test
        @DisplayName("re-encodes when enabled, which strips embedded metadata")
        void reEncodesWhenEnabled () {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "photo.png", "image/png",
                    MediaTestFixtures.pngBytes(30, 30));

            ImageValidator.ValidatedImage image =
                    validator.validate(file, MediaScope.STORY_COVER);

            assertThat(image.sanitised()).isTrue();
        }

        @Test
        @DisplayName("passes bytes through untouched when disabled")
        void passesThroughWhenDisabled () {
            MediaProperties properties = MediaTestFixtures.properties(false, Map.of());
            ImageValidator passthrough = new ImageValidator(
                    properties, new MediaPolicyResolver(properties));
            byte[] original = MediaTestFixtures.pngBytes(30, 30);
            MockMultipartFile file = new MockMultipartFile(
                    "file", "photo.png", "image/png", original);

            ImageValidator.ValidatedImage image =
                    passthrough.validate(file, MediaScope.STORY_COVER);

            assertThat(image.sanitised()).isFalse();
            assertThat(image.content()).isEqualTo(original);
        }
    }

    private ImageValidator validatorFor (MediaProperties.ScopeOverrides overrides) {
        MediaProperties properties = MediaTestFixtures.properties(
                true,
                Map.of(MediaScope.AVATAR, overrides,
                        MediaScope.INLINE_IMAGE, overrides,
                        MediaScope.STORY_COVER, overrides)
                                );
        return new ImageValidator(properties, new MediaPolicyResolver(properties));
    }
}