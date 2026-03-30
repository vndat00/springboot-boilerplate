package com.vndat00.springbootboilerplate.repository;

import com.vndat00.springbootboilerplate.domain.model.ImportJobRow;
import com.vndat00.springbootboilerplate.domain.model.ImportJobRowMessage;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportJobRowMessageRepository extends JpaRepository<ImportJobRowMessage, UUID> {
  List<ImportJobRowMessage> findAllByJobRowIn(Collection<ImportJobRow> rows);

  void deleteAllByJobRowIn(Collection<ImportJobRow> rows);
}
