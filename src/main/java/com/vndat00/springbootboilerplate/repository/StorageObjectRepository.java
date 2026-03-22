package com.vndat00.springbootboilerplate.repository;

import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageStatus;
import com.vndat00.springbootboilerplate.domain.model.StorageObject;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageObjectRepository extends JpaRepository<StorageObject, UUID> {
  List<StorageObject> findByStatusIn(Collection<StorageStatus> statuses);

  List<StorageObject> findAllByIdIn(Collection<UUID> ids);

  List<StorageObject> findAllByNoteId(UUID noteId);

  List<StorageObject> findAllByNoteIdIn(Collection<UUID> noteIds);
}
