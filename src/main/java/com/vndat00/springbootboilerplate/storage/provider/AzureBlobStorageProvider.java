package com.vndat00.springbootboilerplate.storage.provider;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.vndat00.springbootboilerplate.config.properties.StorageAzureProperties;
import com.vndat00.springbootboilerplate.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.time.Duration;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class AzureBlobStorageProvider implements BlobStorageProvider {

  private final StorageAzureProperties properties;

  @Override
  public String generateUploadUrl(
      String containerName, String blobKey, String contentType, Duration expiry) {
    BlobClient blobClient = getBlobClient(containerName, blobKey);
    BlobSasPermission permission =
        new BlobSasPermission().setCreatePermission(true).setWritePermission(true);
    BlobServiceSasSignatureValues values =
        new BlobServiceSasSignatureValues(OffsetDateTime.now().plus(expiry), permission);
    if (StringUtils.hasText(contentType)) {
      values.setContentType(contentType);
    }
    String sasToken = blobClient.generateSas(values);
    return blobClient.getBlobUrl() + "?" + sasToken;
  }

  @Override
  public String generateViewUrl(String containerName, String blobKey, Duration expiry) {
    BlobClient blobClient = getBlobClient(containerName, blobKey);
    BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
    BlobServiceSasSignatureValues values =
        new BlobServiceSasSignatureValues(OffsetDateTime.now().plus(expiry), permission);
    String sasToken = blobClient.generateSas(values);
    return blobClient.getBlobUrl() + "?" + sasToken;
  }

  @Override
  public void upload(
      String containerName,
      String blobKey,
      InputStream inputStream,
      long size,
      String contentType) {
    BlobClient blobClient = getBlobClient(containerName, blobKey);
    blobClient.upload(inputStream, size, true);
    if (StringUtils.hasText(contentType)) {
      blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(contentType));
    }
  }

  @Override
  public boolean exists(String containerName, String blobKey) {
    return getBlobClient(containerName, blobKey).exists();
  }

  @Override
  public long contentLength(String containerName, String blobKey) {
    BlobClient blobClient = getBlobClient(containerName, blobKey);
    if (Boolean.FALSE.equals(blobClient.exists())) {
      throw new NotFoundException("storage_object_not_found");
    }
    return blobClient.getProperties().getBlobSize();
  }

  @Override
  public void deleteIfExists(String containerName, String blobKey) {
    getBlobClient(containerName, blobKey).deleteIfExists();
  }

  private BlobClient getBlobClient(String containerName, String blobKey) {
    BlobContainerClient containerClient = getBlobContainerClient(containerName);
    return containerClient.getBlobClient(blobKey);
  }

  private BlobContainerClient getBlobContainerClient(String containerName) {
    BlobServiceClient blobServiceClient = createBlobServiceClient();
    BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
    if (!containerClient.exists()) {
      try {
        containerClient.create();
      } catch (BlobStorageException ex) {
        if (ex.getStatusCode() != 409) {
          throw ex;
        }
      }
    }
    return containerClient;
  }

  private BlobServiceClient createBlobServiceClient() {
    BlobServiceClientBuilder builder = new BlobServiceClientBuilder();
    if (StringUtils.hasText(properties.getConnectionString())) {
      return builder.connectionString(properties.getConnectionString()).buildClient();
    }
    if (!StringUtils.hasText(properties.getEndpoint())
        || !StringUtils.hasText(properties.getAccountName())
        || !StringUtils.hasText(properties.getAccountKey())) {
      throw new IllegalStateException(
          "Azure storage is not configured. Set storage.azure.connection-string or endpoint/account-name/account-key");
    }
    return builder
        .endpoint(properties.getEndpoint())
        .credential(
            new com.azure.storage.common.StorageSharedKeyCredential(
                properties.getAccountName(), properties.getAccountKey()))
        .buildClient();
  }
}
