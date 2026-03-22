package com.vndat00.springbootboilerplate.storage.provider;


import java.io.InputStream;
import java.time.Duration;

public interface BlobStorageProvider {
    String generateUploadUrl(String containerName, String blobKey, String contentType, Duration expiry);

    String generateViewUrl(String containerName, String blobKey, Duration expiry);

    void upload(String containerName, String blobKey, InputStream inputStream, long size, String contentType);

    boolean exists(String containerName, String blobKey);

    long contentLength(String containerName, String blobKey);

    void deleteIfExists(String containerName, String blobKey);
}
