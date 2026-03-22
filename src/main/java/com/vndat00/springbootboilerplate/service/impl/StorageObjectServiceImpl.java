package com.vndat00.springbootboilerplate.service.impl;

import com.vndat00.springbootboilerplate.config.properties.StorageAzureProperties;
import com.vndat00.springbootboilerplate.constant.CommonConstant;
import com.vndat00.springbootboilerplate.constant.MessageConstant;
import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageStatus;
import com.vndat00.springbootboilerplate.domain.enums.object_storage.UploadMethod;
import com.vndat00.springbootboilerplate.domain.model.Note;
import com.vndat00.springbootboilerplate.domain.model.StorageObject;
import com.vndat00.springbootboilerplate.domain.record.StoragePolicy;
import com.vndat00.springbootboilerplate.exception.BadRequestException;
import com.vndat00.springbootboilerplate.exception.NotFoundException;
import com.vndat00.springbootboilerplate.mapper.StorageObjectMapper;
import com.vndat00.springbootboilerplate.payload.request.storage.BackendUploadRequest;
import com.vndat00.springbootboilerplate.payload.request.storage.BindObjectsRequest;
import com.vndat00.springbootboilerplate.payload.request.storage.CompleteUploadRequest;
import com.vndat00.springbootboilerplate.payload.request.storage.PrepareUploadRequest;
import com.vndat00.springbootboilerplate.payload.response.storage.PrepareUploadResponse;
import com.vndat00.springbootboilerplate.payload.response.storage.StorageObjectResponse;
import com.vndat00.springbootboilerplate.repository.NoteRepository;
import com.vndat00.springbootboilerplate.repository.StorageObjectRepository;
import com.vndat00.springbootboilerplate.service.StorageObjectService;
import com.vndat00.springbootboilerplate.storage.provider.BlobStorageProvider;
import com.vndat00.springbootboilerplate.storage.resolver.BlobKeyResolver;
import com.vndat00.springbootboilerplate.storage.resolver.StoragePolicyResolver;
import com.vndat00.springbootboilerplate.validator.StorageObjectValidator;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@EnableConfigurationProperties(StorageAzureProperties.class)
public class StorageObjectServiceImpl implements StorageObjectService {
  private final StorageObjectRepository storageObjectRepository;
  private final NoteRepository noteRepository;

  private final StorageObjectMapper storageMapper;
  private final StoragePolicyResolver storagePolicyResolver;
  private final BlobKeyResolver blobKeyResolver;
  private final BlobStorageProvider blobStorageProvider;
  private final StorageObjectValidator storageObjectValidator;
  private final StorageAzureProperties storageAzureProperties;

  @Override
  public PrepareUploadResponse prepareClientUpload(PrepareUploadRequest request) {
    // Validate request against policy
    StoragePolicy policy = storagePolicyResolver.resolve(request.getStorageUseCase());
    storageObjectValidator.validate(request, policy);

    StorageObject storageObject = storageMapper.toEntity(request);
    storageObject.setContainerName(storageAzureProperties.getContainer());
    storageObject.setUploadMethod(UploadMethod.ABS_DIRECT_SAS);
    storageObject.setStatus(StorageStatus.INITIATED);
    storageObject.setBlobKey(
        blobKeyResolver.resolve(request.getStorageUseCase(), null, request.getFileName()));
    storageObjectRepository.save(storageObject);

    Duration sasExpiry = Duration.ofMinutes(storageAzureProperties.getSasExpiryMinutes());
    String uploadUrl =
        blobStorageProvider.generateUploadUrl(
            storageObject.getContainerName(),
            storageObject.getBlobKey(),
            storageObject.getContentType(),
            sasExpiry);
    Timestamp expiresAt = Timestamp.from(Instant.now().plus(sasExpiry));

    return PrepareUploadResponse.builder()
        .storageObjectId(storageObject.getId())
        .uploadUrl(uploadUrl)
        .method(CommonConstant.PUT_METHOD)
        .requiredHeaders(
            Map.of("x-ms-blob-type", "BlockBlob", "Content-Type", storageObject.getContentType()))
        .blobKey(storageObject.getBlobKey())
        .container(storageObject.getContainerName())
        .maxSize(policy.maxFileSize())
        .contentType(storageObject.getContentType())
        .expiresAt(expiresAt)
        .build();
  }

  @Override
  public StorageObjectResponse completeClientUpload(CompleteUploadRequest request) {
    StorageObject storageObject =
        storageObjectRepository
            .findById(request.getStorageObjectId())
            .orElseThrow(() -> new NotFoundException(MessageConstant.STORAGE_OBJECT_NOT_FOUND));

    if (!blobStorageProvider.exists(storageObject.getContainerName(), storageObject.getBlobKey())) {
      storageObject.setStatus(StorageStatus.FAILED);
      storageObjectRepository.save(storageObject);
      throw new BadRequestException(MessageConstant.STORAGE_UPLOAD_NOT_FOUND_ON_BLOB);
    }

    storageObject.setActualSize(
        blobStorageProvider.contentLength(
            storageObject.getContainerName(), storageObject.getBlobKey()));
    storageObject.setStatus(StorageStatus.UPLOADED);
    storageObjectRepository.save(storageObject);

    return storageMapper.toResponse(storageObject);
  }

  @Override
  public StorageObjectResponse uploadViaBackend(MultipartFile file, BackendUploadRequest request) {
    StoragePolicy policy = storagePolicyResolver.resolve(request.getStorageUseCase());
    storageObjectValidator.validate(request, file, policy);

    StorageObject storageObject = storageMapper.toEntity(request);
    storageObject.setId(UUID.randomUUID());
    String originalFilename = file.getOriginalFilename();
    storageObject.setFileName(
        originalFilename == null || originalFilename.isBlank()
            ? storageObject.getId() + "-file"
            : originalFilename);
    storageObject.setContentType(file.getContentType());
    storageObject.setContainerName(storageAzureProperties.getContainer());
    storageObject.setDeclaredSize(file.getSize());
    storageObject.setActualSize(file.getSize());
    storageObject.setUploadMethod(UploadMethod.ABS_BACKEND_PROXY);
    storageObject.setStatus(StorageStatus.UPLOADED);
    storageObject.setBlobKey(
        blobKeyResolver.resolve(request.getStorageUseCase(), null, storageObject.getFileName()));

    try {
      blobStorageProvider.upload(
          storageObject.getContainerName(),
          storageObject.getBlobKey(),
          file.getInputStream(),
          file.getSize(),
          file.getContentType());
      StorageObject saved = storageObjectRepository.save(storageObject);
      return storageMapper.toResponse(saved);
    } catch (IOException ex) {
      throw new BadRequestException(MessageConstant.STORAGE_BACKEND_UPLOAD_FAILED);
    }
  }

  @Override
  public List<StorageObjectResponse> bindObjects(BindObjectsRequest request) {
    List<StorageObject> objects = storageObjectRepository.findAllById(request.getObjectIds());
    if (objects.size() != request.getObjectIds().size()) {
      throw new NotFoundException(MessageConstant.STORAGE_OBJECT_NOT_FOUND);
    }

    objects.forEach(
        object -> {
          if (object.getStatus() != StorageStatus.UPLOADED) {
            throw new BadRequestException(MessageConstant.STORAGE_OBJECT_NOT_UPLOADED);
          }
          object.setStatus(StorageStatus.ATTACHED);
          Note linkedNote =
              noteRepository
                  .findById(UUID.fromString(request.getBusinessId()))
                  .orElseThrow(() -> new NotFoundException(MessageConstant.NOTE_NOT_FOUND));
          object.setNote(linkedNote);
        });

    return storageObjectRepository.saveAll(objects).stream()
        .map(storageMapper::toResponse)
        .toList();
  }

  @Override
  public boolean deleteObject(UUID id) {
    return storageObjectRepository
        .findById(id)
        .map(
            object -> {
              blobStorageProvider.deleteIfExists(object.getContainerName(), object.getBlobKey());
              storageObjectRepository.delete(object);
              return true;
            })
        .orElse(false);
  }

  @Override
  @Transactional(readOnly = true)
  public String getPreviewUrl(String containerName, String blobKey) {
    containerName =
        !StringUtils.hasText(containerName) ? storageAzureProperties.getContainer() : containerName;
    if (!StringUtils.hasText(blobKey)) {
      throw new BadRequestException(MessageConstant.INVALID_REQUEST);
    }
    if (!blobStorageProvider.exists(containerName, blobKey)) {
      throw new NotFoundException(MessageConstant.STORAGE_OBJECT_NOT_FOUND);
    }
    return blobStorageProvider.generateViewUrl(
        containerName, blobKey, Duration.ofMinutes(storageAzureProperties.getSasExpiryMinutes()));
  }
}
