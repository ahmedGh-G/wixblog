package com.tech.wixblog.media.service;

import java.io.IOException;

/**
 * Abstraction over the binary store holding uploaded images.
 * <p>
 * Intentionally three methods. There is no read operation because callers never need
 * one: local files are served straight from disk by Spring's resource handler, and an
 * object store would be served over HTTPS by the bucket itself. Keeping the surface
 * this small is what makes an S3 or GCS implementation a mechanical translation rather
 * than a redesign.
 */
public interface MediaStorage {

    /**
     * Writes the bytes for {@code key}, replacing any existing object at that key.
     *
     * @param key     storage-relative key such as {@code covers/2026/10/<uuid>.webp}
     * @param content validated bytes
     * @throws IOException if the underlying store rejects the write
     */
    void store (String key, byte[] content) throws IOException;

    /**
     * Removes the object at {@code key}. Must not fail when the key is already absent,
     * so that a retried cleanup is safe.
     *
     * @throws IOException if the underlying store rejects the deletion
     */
    void delete (String key) throws IOException;

    boolean exists (String key);
}