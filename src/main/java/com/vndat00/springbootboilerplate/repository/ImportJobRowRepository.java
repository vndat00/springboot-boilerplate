package com.vndat00.springbootboilerplate.repository;

import com.vndat00.springbootboilerplate.domain.model.ImportJob;
import com.vndat00.springbootboilerplate.domain.model.ImportJobRow;
import com.vndat00.springbootboilerplate.importexport.core.ImportRowStatus;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportJobRowRepository extends JpaRepository<ImportJobRow, UUID> {
  List<ImportJobRow> findAllByJobOrderByRowNumberAsc(ImportJob job);

  List<ImportJobRow> findAllByJobAndRowStatusInOrderByRowNumberAsc(
      ImportJob job, Collection<ImportRowStatus> statuses);

  void deleteAllByJob(ImportJob job);
}
