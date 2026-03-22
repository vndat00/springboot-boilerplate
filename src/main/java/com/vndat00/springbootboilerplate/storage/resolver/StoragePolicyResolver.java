package com.vndat00.springbootboilerplate.storage.resolver;

import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageUseCase;
import com.vndat00.springbootboilerplate.domain.record.StoragePolicy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class StoragePolicyResolver {

  private final Map<StorageUseCase, StoragePolicy> policies =
      Map.of(
          StorageUseCase.FORM_ATTACHMENT,
          new StoragePolicy(
              20L * 1024 * 1024, Set.of("image/png", "image/jpeg", "application/pdf"), 10, true),
          StorageUseCase.USER_AVATAR,
          new StoragePolicy(5L * 1024 * 1024, Set.of("image/png", "image/jpeg"), 1, true),
          StorageUseCase.IMPORT_FILE,
          new StoragePolicy(
              200L * 1024 * 1024, Set.of("text/csv", "application/vnd.ms-excel"), 3, false),
          StorageUseCase.EXPORT_FILE,
          new StoragePolicy(200L * 1024 * 1024, Set.of("application/zip", "text/csv"), 3, false),
          StorageUseCase.COMMON_DOCUMENT,
          new StoragePolicy(
              50L * 1024 * 1024,
              Set.of(
                  "application/pdf",
                  "application/msword",
                  "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
              10,
              true));

  public StoragePolicy resolve(StorageUseCase useCase) {
    return policies.get(useCase);
  }
}
