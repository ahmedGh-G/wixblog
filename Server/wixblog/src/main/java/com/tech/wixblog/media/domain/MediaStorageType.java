package com.tech.wixblog.media.domain;

/**
 * Backing store for uploaded binaries.
 * <p>
 * {@link #LOCAL} is the only implementation shipped today. The interface is
 * intentionally narrow (three methods) so that an S3/GCS implementation is a
 * mechanical translation: no {@code read} method exists because local files are
 * served statically by Spring and remote buckets are served over HTTPS, so nothing
 * in the application ever needs to stream a stored object back out.
 */
public enum MediaStorageType {
    LOCAL
}