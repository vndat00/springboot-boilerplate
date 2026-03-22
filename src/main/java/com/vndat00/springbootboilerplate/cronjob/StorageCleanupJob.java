package com.vndat00.springbootboilerplate.cronjob;

import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageStatus;
import com.vndat00.springbootboilerplate.domain.model.StorageObject;
import com.vndat00.springbootboilerplate.repository.StorageObjectRepository;
import com.vndat00.springbootboilerplate.storage.provider.BlobStorageProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageCleanupJob {

  private final StorageObjectRepository storageObjectRepository;
  private final BlobStorageProvider blobStorageProvider;

  @Scheduled(cron = "0 */30 * * * *")
  @Transactional
  public void cleanupExpiredTemporaryObjects() {
    // TODO: Check if the object is actually expired based on createdAt and policy duration, not
    // just by status
    List<StorageObject> needCleaningObjects =
        storageObjectRepository.findByStatusIn(
            List.of(StorageStatus.INITIATED, StorageStatus.UPLOADED));

    needCleaningObjects.forEach(
        object -> {
          blobStorageProvider.deleteIfExists(object.getContainerName(), object.getBlobKey());
          object.setStatus(StorageStatus.DELETED);
        });

    if (!needCleaningObjects.isEmpty()) {
      storageObjectRepository.saveAll(needCleaningObjects);
      log.info("Storage cleanup marked {} objects as expired", needCleaningObjects.size());
    }
  }
}
