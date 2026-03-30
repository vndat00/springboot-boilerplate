package com.vndat00.springbootboilerplate.repository;

import com.vndat00.springbootboilerplate.domain.model.ImportJob;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportJobRepository extends JpaRepository<ImportJob, UUID> {}
