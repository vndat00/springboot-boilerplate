package com.vndat00.springbootboilerplate.domain.model;

import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageProvider;
import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageStatus;
import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageUseCase;
import com.vndat00.springbootboilerplate.domain.enums.object_storage.UploadMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "storage_object")
public class StorageObject extends AbstractEntity {

  @Id @GeneratedValue private UUID id;

  @Column(nullable = false, unique = true, columnDefinition = "text")
  private String blobKey;

  @Column(nullable = false)
  private String containerName;

  @Column(nullable = false, columnDefinition = "text")
  private String fileName;

  @Column(nullable = false)
  private String contentType;

  @Column(nullable = false)
  private Long declaredSize;

  private Long actualSize;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StorageProvider storageProvider;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StorageStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UploadMethod uploadMethod;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StorageUseCase storageUseCase;

  @ManyToOne
  @JoinColumn(name = "note_id", referencedColumnName = "id")
  private Note note;
}
