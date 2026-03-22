package com.vndat00.springbootboilerplate.storage.resolver;

import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageUseCase;
import com.vndat00.springbootboilerplate.utils.FileUtils;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class BlobKeyResolver {

  public String resolve(StorageUseCase useCase, UUID ownerUserId, String originalFileName) {
    LocalDate now = LocalDate.now();
    String owner = ownerUserId == null ? "anonymous" : ownerUserId.toString();
    String safeName = FileUtils.generateFileName(originalFileName);
    return String.format(
        "%s/%s/%d/%02d/%s",
        useCase.name().toLowerCase(), owner, now.getYear(), now.getMonthValue(), safeName);
  }
}
