package com.vndat00.springbootboilerplate.repository;

import com.vndat00.springbootboilerplate.domain.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {
  @Query("SELECT n FROM Note n WHERE n.content LIKE %:search%")
  Page<Note> findAllBySearch(Pageable pageable, @Param("search") String search);
}
