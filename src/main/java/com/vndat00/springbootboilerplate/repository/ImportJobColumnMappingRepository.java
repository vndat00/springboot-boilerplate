package com.vndat00.springbootboilerplate.repository;

import com.vndat00.springbootboilerplate.domain.model.ImportJob;
import com.vndat00.springbootboilerplate.domain.model.ImportJobColumnMapping;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportJobColumnMappingRepository
    extends JpaRepository<ImportJobColumnMapping, UUID> {
  List<ImportJobColumnMapping> findAllByJob(ImportJob job);

  void deleteAllByJob(ImportJob job);
}
