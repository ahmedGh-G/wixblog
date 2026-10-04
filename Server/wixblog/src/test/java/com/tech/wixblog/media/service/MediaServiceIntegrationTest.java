package com.tech.wixblog.media.service;

import com.tech.wixblog.common.exception.InvalidRequestException;
import com.tech.wixblog.media.domain.MediaAsset;
import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.dto.MediaUploadResponse;
import com.tech.wixblog.media.repository.MediaAssetRepository;
import com.tech.wixblog.media.support.MediaTestFixtures;
import com.tech.wixblog.user.domain.Role;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.domain.UserStatus;
import com.tech.wixblog.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * End-to-end test of the upload path against the real database and a real filesystem.
 * <p>
 * This exists because the unit tests could not see the two defects that shipped in the
 * first version of this module:
 * <ol>
 *     <li>{@code MediaAsset} declared {@code created_at} as {@code nullable = false}
 *         but had no {@code @PrePersist}, so every INSERT violated the not-null
 *         constraint and rolled back. A mocked repository never touches the schema, so
 *         the unit tests were green.</li>
 *     <li>The compensating delete used {@code save()}, which only queues the INSERT.
 *         Constraints are evaluated at flush or commit, after the method had returned,
 *         so the failure never reached the {@code catch} block and the uploaded file
 *         was orphaned on disk while the caller received a 409. A Mockito mock cannot
 *         reproduce deferred flush semantics, so the unit test for compensation passed
 *         while production was broken.</li>
 * </ol>
 * Both defects produced the same user-visible symptom as the reported bug: a 409
 * Conflict together with a file left behind in the media folder.
 */
@SpringBootTest
class MediaServiceIntegrationTest {

    private static final Path STORAGE_ROOT = Path.of("target", "it-media").toAbsolutePath();

    @DynamicPropertySource
    static void mediaProperties (DynamicPropertyRegistry registry) {
        registry.add("app.media.local-root", () -> STORAGE_ROOT.toString());
        // Keep the served URL deterministic so assertions do not depend on the host.
        registry.add("app.media.public-base-url", () -> "http://localhost:8080/api/v1");
    }

    @Autowired
    private MediaService mediaService;
    @Autowired
    private MediaAssetRepository mediaAssetRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocalFilesystemMediaStorage localStorage;

    private UUID userId;

    @BeforeEach
    void setUp () {
        User user = new User(
                "media-it-" + UUID.randomUUID() + "@example.com",
                "media-it-" + UUID.randomUUID().toString().substring(0, 8),
                "{noop}",
                Role.USER,
                UserStatus.ACTIVE
                            );
        userId = userRepository.saveAndFlush(user).getId();
    }

    @AfterEach
    void tearDown () throws IOException {
        for (MediaAsset asset : mediaAssetRepository.findAll()) {
            if (asset.isOwnedBy(userId)) {
                localStorage.delete(asset.getStorageKey());
                mediaAssetRepository.delete(asset);
            }
        }
        userRepository.deleteById(userId);
    }

    @Test
    @DisplayName("persists a ledger row and leaves the file on disk")
    void persistsRowAndFile () throws IOException {
        MediaUploadResponse response = upload(
                MediaScope.AVATAR, "avatar.png", MediaTestFixtures.pngBytes(60, 40));

        MediaAsset persisted = mediaAssetRepository.findByStorageKey(response.key())
                                                  .orElseThrow();

        assertThat(persisted.getId()).isNotNull();
        assertThat(persisted.getScope()).isEqualTo(MediaScope.AVATAR);
        assertThat(persisted.getPublicUrl()).isEqualTo(response.url());
        assertThat(persisted.getContentType()).isEqualTo("image/png");
        assertThat(persisted.getSizeBytes()).isPositive();
        assertThat(persisted.getWidth()).isEqualTo(60);
        assertThat(persisted.getHeight()).isEqualTo(40);
        assertThat(persisted.isDeleted()).isFalse();
        assertThat(localStorage.exists(response.key()))
                .as("the stored file must exist on disk")
                .isTrue();
    }

    @Test
    @DisplayName("stamps created_at, which the column declares NOT NULL")
    void stampsCreatedAt () {
        MediaUploadResponse response = upload(
                MediaScope.STORY_COVER, "cover.png", MediaTestFixtures.pngBytes(30, 30));

        MediaAsset persisted = mediaAssetRepository.findByStorageKey(response.key())
                                                  .orElseThrow();

        // The original defect: created_at was never populated, so the INSERT was
        // rejected by the not-null constraint and the transaction rolled back.
        assertThat(persisted.getCreatedAt())
                .as("created_at must be populated by @PrePersist")
                .isNotNull();
    }

    @Test
    @DisplayName("counts the row exactly once, proving no silent rollback")
    void rowSurvivesTheTransaction () {
        long before = mediaAssetRepository.count();

        upload(MediaScope.AVATAR, "counted.png", MediaTestFixtures.pngBytes(20, 20));

        assertThat(mediaAssetRepository.count()).isEqualTo(before + 1);
    }

    @Test
    @DisplayName("stores an inline image under the inline scope")
    void storesInlineImage () {
        MediaUploadResponse response = upload(
                MediaScope.INLINE_IMAGE, "body.png", MediaTestFixtures.pngBytes(80, 60));

        assertThat(response.key()).startsWith("inline/");
        assertThat(response.scope()).isEqualTo(MediaScope.INLINE_IMAGE);
        assertThat(localStorage.exists(response.key())).isTrue();
    }

    @Test
    @DisplayName("rejects an empty file as 400-worthy rather than leaving a file behind")
    void rejectsEmptyFile () throws IOException {
        long filesBefore = countFilesUnder(STORAGE_ROOT);

        assertThatThrownBy(() -> upload(MediaScope.AVATAR, "empty.png", new byte[0]))
                .isInstanceOf(InvalidRequestException.class);

        // Compared as a delta rather than an absolute count so the assertion does not
        // depend on test execution order sharing this storage root.
        assertThat(countFilesUnder(STORAGE_ROOT))
                .as("a request rejected before storage must not leave bytes on disk")
                .isEqualTo(filesBefore);
    }

    @Test
    @DisplayName("retiring an asset deletes the file and marks the row retired")
    void retireRemovesFileAndMarksRow () throws IOException {
        MediaUploadResponse response = upload(
                MediaScope.AVATAR, "retire.png", MediaTestFixtures.pngBytes(20, 20));
        assertThat(localStorage.exists(response.key())).isTrue();

        MediaAsset asset = mediaAssetRepository.findByStorageKey(response.key())
                                              .orElseThrow();
        mediaService.retireAndDelete(asset);

        assertThat(localStorage.exists(response.key()))
                .as("the file must be gone from disk once retired")
                .isFalse();

        // The row is intentionally retained, marked retired, rather than deleted. That
        // is what lets the cleanup sweep retry a failed physical delete, and what makes
        // a stale reference resolve to a definitive "not valid" instead of "unknown".
        MediaAsset retained = mediaAssetRepository.findByStorageKey(response.key())
                                                 .orElseThrow();
        assertThat(retained.isDeleted()).isTrue();
        assertThat(retained.getDeletedAt()).isNotNull();

        // And crucially: a retired asset must no longer be resolvable as a live
        // reference, which is what makes re-attaching an old avatar fail with a 400.
        assertThat(mediaService.findActiveByPublicUrl(response.url())).isEmpty();
    }

    private MediaUploadResponse upload (MediaScope scope, String filename, byte[] content) {
        return mediaService.store(
                new MockMultipartFile("file", filename, "image/png", content),
                scope,
                userId
                             );
    }

    private long countFilesUnder (Path root) throws IOException {
        if (!java.nio.file.Files.exists(root)) {
            return 0L;
        }
        try (var walk = java.nio.file.Files.walk(root)) {
            return walk.filter(java.nio.file.Files::isRegularFile).count();
        }
    }
}