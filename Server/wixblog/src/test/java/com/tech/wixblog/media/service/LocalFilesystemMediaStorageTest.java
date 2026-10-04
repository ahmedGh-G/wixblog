package com.tech.wixblog.media.service;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.media.config.MediaProperties;
import com.tech.wixblog.media.support.MediaTestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalFilesystemMediaStorageTest {

    @TempDir
    Path tempDir;

    private LocalFilesystemMediaStorage storage;

    @BeforeEach
    void setUp () {
        MediaProperties properties = new MediaProperties(
                com.tech.wixblog.media.domain.MediaStorageType.LOCAL,
                tempDir.toString(),
                "/media",
                "",
                false,
                true,
                java.time.Duration.ofDays(30),
                java.util.Map.of(),
                null
                                  );
        storage = new LocalFilesystemMediaStorage(properties);
        storage.initialise();
    }

    @Test
    @DisplayName("creates the root directory and resolves it to an absolute path")
    void createsRootDirectory () {
        assertThat(Files.isDirectory(storage.getRoot())).isTrue();
        assertThat(storage.getRoot().isAbsolute()).isTrue();
    }

    @Test
    @DisplayName("stores and reads back content, creating intermediate directories")
    void storesContent () throws IOException {
        byte[] content = "hello".getBytes(StandardCharsets.UTF_8);

        storage.store("covers/2026/10/abc.png", content);

        Path expected = storage.getRoot().resolve("covers/2026/10/abc.png");
        assertThat(Files.readAllBytes(expected)).isEqualTo(content);
        assertThat(storage.exists("covers/2026/10/abc.png")).isTrue();
    }

    @Test
    @DisplayName("delete is idempotent, so a retried cleanup is safe")
    void deleteIsIdempotent () throws IOException {
        storage.store("covers/2026/10/abc.png", new byte[]{1});

        storage.delete("covers/2026/10/abc.png");
        storage.delete("covers/2026/10/abc.png");

        assertThat(storage.exists("covers/2026/10/abc.png")).isFalse();
    }

    @Test
    @DisplayName("rejects a key that tries to escape the storage root")
    void rejectsTraversal () {
        assertThatThrownBy(() -> storage.store("../../evil.png", new byte[]{1}))
                .isInstanceOf(BusinessRuleException.class);
        assertThatThrownBy(() -> storage.delete("covers/../../evil.png"))
                .isInstanceOf(BusinessRuleException.class);
        assertThatThrownBy(() -> storage.exists("../secrets"))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("rejects absolute and empty keys")
    void rejectsAbsoluteAndEmptyKeys () {
        assertThatThrownBy(() -> storage.store("/etc/passwd", new byte[]{1}))
                .isInstanceOf(BusinessRuleException.class);
        assertThatThrownBy(() -> storage.store("   ", new byte[]{1}))
                .isInstanceOf(BusinessRuleException.class);
        assertThatThrownBy(() -> storage.store(null, new byte[]{1}))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("leaves no temp part file behind after a successful write")
    void leavesNoTempFiles () throws IOException {
        storage.store("covers/2026/10/abc.png", new byte[]{1, 2, 3});

        try (var entries = Files.list(storage.getRoot().resolve("covers/2026/10"))) {
            assertThat(entries.map(p -> p.getFileName().toString()))
                    .containsExactly("abc.png");
        }
    }
}