package com.tech.wixblog.media.service;

import com.tech.wixblog.media.config.MediaProperties;
import com.tech.wixblog.media.domain.MediaAsset;
import com.tech.wixblog.media.repository.MediaAssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

/**
 * Retries the physical deletion of retired images.
 * <p>
 * Replacing a cover or an avatar marks the old {@link MediaAsset} deleted and asks
 * storage to remove the file. That file deletion can fail for reasons outside our
 * control — a file handle still open on Windows, a transient filesystem error — and a
 * failed delete inside a request thread must never fail the user's update. So the row
 * is marked deleted first and the file removal is retried here until it succeeds, after
 * which the row itself is dropped.
 * <p>
 * <b>Deliberately not implemented: sweeping uploads that were never referenced.</b>
 * Because uploads are decoupled from resource creation, a user can upload an inline
 * image and then abandon the editor, and nothing ever points at that file again.
 * Reclaiming it is tempting, but inline images are referenced only from inside an
 * Editor.js JSON document in {@code Story.content}; there is no cheap, reliable way to
 * ask "is this URL mentioned in any story body?" without loading and parsing every
 * story. A sweep that guesses wrong deletes images out of published articles, so this
 * service leaves unreferenced uploads alone. If storage ever becomes a real problem,
 * the correct fix is to record inline references in a join table as they are written,
 * not to infer them by scanning documents.
 * <p>
 * Disabled by default via {@code app.media.cleanup.enabled}, so a first boot never
 * deletes anything a developer did not expect.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "app.media.cleanup",
        name = "enabled",
        havingValue = "true"
                            )
public class MediaCleanupService {

    private final MediaAssetRepository mediaAssetRepository;
    private final MediaStorage mediaStorage;
    private final MediaProperties mediaProperties;

    /**
     * Offset from the top of the hour so that a fleet of instances does not all scan
     * at the same instant.
     */
    @Scheduled(cron = "${app.media.cleanup.cron:0 17 * * * *}")
    @Transactional
    public void purgeRetiredFiles () {
        CleanupSettings settings = cleanupSettings();
        Instant cutoff = Instant.now().minus(settings.gracePeriod());

        var batch = mediaAssetRepository.findDeletedBefore(
                cutoff,
                PageRequest.of(0, settings.batchSize())
                                                  );

        int purged = 0;
        for (MediaAsset asset : batch) {
            if (purge(asset)) {
                purged++;
            }
        }
        if (purged > 0) {
            log.info("Media cleanup purged {} previously retired file(s).", purged);
        }
    }

    private boolean purge (MediaAsset asset) {
        try {
            if (mediaStorage.exists(asset.getStorageKey())) {
                mediaStorage.delete(asset.getStorageKey());
            }
            // The file is gone and the asset has been retired for longer than the grace
            // period, so the row has no remaining purpose.
            mediaAssetRepository.delete(asset);
            return true;
        } catch (Exception exception) {
            log.warn(
                    "Still unable to delete media key {} after being retired. Will retry on the next sweep.",
                    asset.getStorageKey(),
                    exception
            );
            return false;
        }
    }

    private record CleanupSettings(Duration gracePeriod, int batchSize) {}

    private CleanupSettings cleanupSettings () {
        MediaProperties.Cleanup cleanup =
                mediaProperties.cleanup() == null
                        ? new MediaProperties.Cleanup(false, Duration.ofHours(24), Duration.ofHours(24), 100)
                        : mediaProperties.cleanup();
        return new CleanupSettings(
                cleanup.gracePeriod(),
                Math.max(1, cleanup.batchSize())
                                  );
    }
}