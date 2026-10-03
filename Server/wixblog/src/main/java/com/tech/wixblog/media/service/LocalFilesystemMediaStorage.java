package com.tech.wixblog.media.service;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.media.config.MediaProperties;
import com.tech.wixblog.media.domain.MediaStorageType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Stores uploaded images on the local filesystem.
 * <p>
 * Selected with {@code app.media.storage=local}.
 */
@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "app.media",
        name = "storage",
        havingValue = "local",
        matchIfMissing = true
                            )
public class LocalFilesystemMediaStorage
        implements MediaStorage {

    private final MediaProperties mediaProperties;

    private Path root;

    public LocalFilesystemMediaStorage (MediaProperties mediaProperties) {
        this.mediaProperties = mediaProperties;
    }

    /**
     * Resolves the configured root to an absolute, normalised path once at startup.
     * <p>
     * This matters more than it looks: a relative {@code file:} resource location is
     * resolved by the servlet container against the process working directory, which
     * is the project root under an IDE, the jar's directory under {@code java -jar},
     * and {@code /} under a systemd unit. Pinning the absolute path here means uploads
     * and the handler that serves them always agree, and it lets the directory be
     * created and permission-checked at boot instead of on the first upload.
     */
    @PostConstruct
    void initialise () {
        this.root = Path.of(mediaProperties.localRoot())
                .toAbsolutePath()
                .normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not create media storage directory at %s. Check app.media.local-root."
                            .formatted(root),
                    exception
            );
        }
        if (!Files.isWritable(root)) {
            throw new IllegalStateException(
                    "Media storage directory %s is not writable by this process.".formatted(root)
            );
        }
        log.info("Media storage initialised at {} ({})", root, MediaStorageType.LOCAL);
    }

    @Override
    public void store (String key, byte[] content) throws IOException {
        Path target = resolveSafely(key);
        Files.createDirectories(target.getParent());
        // Write to a sibling temp file then move into place, so a crash mid-write can
        // never leave a truncated image sitting at a key that is already published.
        Path temp = Files.createTempFile(target.getParent(), ".upload-", ".part");
        try {
            Files.write(temp, content);
            try {
                Files.move(temp, target,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (java.nio.file.AtomicMoveNotSupportedException exception) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    @Override
    public void delete (String key) throws IOException {
        Files.deleteIfExists(resolveSafely(key));
    }

    @Override
    public boolean exists (String key) {
        return Files.isRegularFile(resolveSafely(key));
    }

    /**
     * Maps a storage key to an absolute path, refusing anything that could escape the
     * storage root.
     * <p>
     * Keys are generated server-side from a UUID, so this is a defence-in-depth check
     * rather than the primary control — but it costs three lines and it means a future
     * caller that ever passes client input through here cannot traverse out of the
     * root or read an arbitrary file.
     */
    private Path resolveSafely (String key) {
        if (!StringUtils.hasText(key)) {
            throw new BusinessRuleException("A media storage key is required.");
        }
        if (key.startsWith("/") || key.contains("\\") || key.contains("..")) {
            throw new BusinessRuleException("Invalid media storage key.");
        }
        Path resolved = root.resolve(key).normalize();
        if (!resolved.startsWith(root)) {
            throw new BusinessRuleException("Invalid media storage key.");
        }
        return resolved;
    }

    /**
     * Absolute storage root, used by the resource handler that serves these files.
     */
    public Path getRoot () {
        return root;
    }
}