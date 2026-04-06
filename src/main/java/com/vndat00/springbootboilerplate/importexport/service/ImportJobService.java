package com.vndat00.springbootboilerplate.importexport.service;

import com.vndat00.springbootboilerplate.constant.MessageConstant;
import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageProvider;
import com.vndat00.springbootboilerplate.domain.enums.object_storage.StorageUseCase;
import com.vndat00.springbootboilerplate.domain.model.ImportJob;
import com.vndat00.springbootboilerplate.domain.model.ImportJobColumnMapping;
import com.vndat00.springbootboilerplate.domain.model.ImportJobRow;
import com.vndat00.springbootboilerplate.domain.model.ImportJobRowMessage;
import com.vndat00.springbootboilerplate.exception.BadRequestException;
import com.vndat00.springbootboilerplate.exception.NotFoundException;
import com.vndat00.springbootboilerplate.importexport.core.ImportDefinition;
import com.vndat00.springbootboilerplate.importexport.core.ImportFieldDefinition;
import com.vndat00.springbootboilerplate.importexport.core.ImportJobStatus;
import com.vndat00.springbootboilerplate.importexport.core.ImportRowStatus;
import com.vndat00.springbootboilerplate.importexport.core.ImportValidationMessage;
import com.vndat00.springbootboilerplate.importexport.core.ParsedFile;
import com.vndat00.springbootboilerplate.importexport.core.ParsedRow;
import com.vndat00.springbootboilerplate.importexport.core.Severity;
import com.vndat00.springbootboilerplate.importexport.definition.ImportDefinitionRegistry;
import com.vndat00.springbootboilerplate.importexport.parser.TabularFileParserResolver;
import com.vndat00.springbootboilerplate.payload.request.importexport.ColumnMappingItem;
import com.vndat00.springbootboilerplate.payload.request.importexport.ColumnMappingRequest;
import com.vndat00.springbootboilerplate.payload.request.storage.BackendUploadRequest;
import com.vndat00.springbootboilerplate.payload.response.importexport.ImportDefinitionResponse;
import com.vndat00.springbootboilerplate.payload.response.importexport.ImportFieldMetadataResponse;
import com.vndat00.springbootboilerplate.payload.response.importexport.ImportJobCreateResponse;
import com.vndat00.springbootboilerplate.payload.response.importexport.ImportJobSummaryResponse;
import com.vndat00.springbootboilerplate.payload.response.importexport.ImportPreviewMessageResponse;
import com.vndat00.springbootboilerplate.payload.response.importexport.ImportPreviewResponse;
import com.vndat00.springbootboilerplate.payload.response.importexport.ImportPreviewRowResponse;
import com.vndat00.springbootboilerplate.payload.response.importexport.SuggestedMappingResponse;
import com.vndat00.springbootboilerplate.payload.response.storage.StorageObjectResponse;
import com.vndat00.springbootboilerplate.repository.ImportJobColumnMappingRepository;
import com.vndat00.springbootboilerplate.repository.ImportJobRepository;
import com.vndat00.springbootboilerplate.repository.ImportJobRowMessageRepository;
import com.vndat00.springbootboilerplate.repository.ImportJobRowRepository;
import com.vndat00.springbootboilerplate.service.StorageObjectService;
import com.vndat00.springbootboilerplate.storage.provider.BlobStorageProvider;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImportJobService {
  private static final int CHUNK_SIZE = 200;
  private static final String BLOB_SOURCE_PREFIX = "abs://";

  private final ImportJobRepository importJobRepository;
  private final ImportJobColumnMappingRepository mappingRepository;
  private final ImportJobRowRepository rowRepository;
  private final ImportJobRowMessageRepository rowMessageRepository;
  private final ImportDefinitionRegistry definitionRegistry;
  private final ImportValidatorRegistry validatorRegistry;
  private final TabularFileParserResolver parserResolver;
  private final ImportMappingService importMappingService;
  private final BeanValidationWrapper beanValidationWrapper;
  private final StorageObjectService storageObjectService;
  private final BlobStorageProvider blobStorageProvider;

  @Transactional
  public ImportJobCreateResponse createJob(String entityType, MultipartFile file) {
    ImportDefinition<?> definition = definitionRegistry.get(entityType);
    String sourcePath = uploadSourceFile(file);
    ParsedFile parsedFile = readParsedFile(sourcePath, file.getOriginalFilename());

    ImportJob job = new ImportJob();
    job.setEntityType(definition.entityType());
    job.setFileName(file.getOriginalFilename());
    job.setFileType(parsedFile.fileFormat());
    job.setSourceFilePath(sourcePath);
    job.setStatus(ImportJobStatus.CREATED);
    resetSummary(job);

    ImportJob saved = importJobRepository.save(job);

    List<SuggestedMappingResponse> suggestedMappings =
        suggestMappings(parsedFile.headers(), definition);

    return new ImportJobCreateResponse(
        saved.getId(),
        saved.getEntityType(),
        saved.getFileType(),
        parsedFile.headers(),
        parsedFile.sampleRows(20).stream().map(ParsedRow::cells).toList(),
        suggestedMappings);
  }

  public ImportDefinitionResponse getDefinition(String entityType) {
    ImportDefinition<?> definition = definitionRegistry.get(entityType);
    return new ImportDefinitionResponse(
        definition.entityType(),
        definition.fields().stream()
            .map(
                field ->
                    new ImportFieldMetadataResponse(
                        field.fieldName(),
                        field.displayName(),
                        field.dataType(),
                        field.required(),
                        field.importable(),
                        field.exportable(),
                        field.enumValues()))
            .toList());
  }

  @Transactional
  public void saveMapping(UUID jobId, ColumnMappingRequest request) {
    ImportJob job = getJobOrThrow(jobId);
    ParsedFile parsedFile = readParsedFile(job.getSourceFilePath(), job.getFileName());
    ImportDefinition<?> definition = definitionRegistry.get(job.getEntityType());

    Map<String, String> mapping =
        request.mappings().stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    ColumnMappingItem::sourceColumn, ColumnMappingItem::targetField));

    List<ImportValidationMessage> mappingErrors =
        importMappingService.validateMappings(definition.fields(), mapping, parsedFile.headers());
    if (!mappingErrors.isEmpty()) {
      throw new BadRequestException(MessageConstant.BAD_REQUEST);
    }

    mappingRepository.deleteAllByJob(job);
    List<ImportJobColumnMapping> mappings =
        request.mappings().stream()
            .map(
                item -> {
                  ImportJobColumnMapping map = new ImportJobColumnMapping();
                  map.setJob(job);
                  map.setSourceColumn(item.sourceColumn());
                  map.setTargetField(item.targetField());
                  return map;
                })
            .toList();
    mappingRepository.saveAll(mappings);

    job.setStatus(ImportJobStatus.MAPPING_SAVED);
    importJobRepository.save(job);
  }

  @Transactional
  public ImportPreviewResponse preview(UUID jobId) {
    ImportJob job = getJobOrThrow(jobId);
    ImportDefinition<?> definition = definitionRegistry.get(job.getEntityType());
    Map<String, String> mapping = getMapping(job);
    if (mapping.isEmpty()) {
      throw new BadRequestException(MessageConstant.BAD_REQUEST);
    }

    ParsedFile parsedFile = readParsedFile(job.getSourceFilePath(), job.getFileName());

    clearRows(job);

    List<ImportJobRow> rowEntities = new ArrayList<>();
    List<ImportJobRowMessage> messageEntities = new ArrayList<>();
    List<Object> dtos = new ArrayList<>();
    Map<Integer, ImportJobRow> rowByNumber = new HashMap<>();

    for (ParsedRow row : parsedFile.rows()) {
      List<ImportValidationMessage> messages = new ArrayList<>();

      Map<String, Object> mappedPayload =
          importMappingService.mapRow(
              row.rowNumber(), row.cells(), mapping, definition.fields(), messages);

      Object dto = toDto(definition, mappedPayload);
      dtos.add(dto);

      messages.addAll(beanValidationWrapper.validate(dto, row.rowNumber()));
      messages.addAll(runRowValidators(job.getEntityType(), dto, row.rowNumber()));

      ImportRowStatus status = resolveStatus(messages);

      ImportJobRow rowEntity = new ImportJobRow();
      rowEntity.setJob(job);
      rowEntity.setRowNumber(row.rowNumber());
      rowEntity.setRawPayloadJson(new HashMap<>(row.cells()));
      rowEntity.setMappedPayloadJson(mappedPayload);
      rowEntity.setRowStatus(status);
      rowEntities.add(rowEntity);
    }

    rowEntities = rowRepository.saveAll(rowEntities);
    rowEntities.forEach(row -> rowByNumber.put(row.getRowNumber(), row));

    List<ImportValidationMessage> datasetMessages = runDatasetValidators(job.getEntityType(), dtos);
    for (ImportValidationMessage datasetMessage : datasetMessages) {
      ImportJobRow row = rowByNumber.get(datasetMessage.rowNumber());
      if (row != null) {
        messageEntities.add(toMessageEntity(row, datasetMessage));
        if (datasetMessage.severity() == Severity.ERROR
            && row.getRowStatus() != ImportRowStatus.ERROR) {
          row.setRowStatus(ImportRowStatus.ERROR);
        }
        if (datasetMessage.severity() == Severity.WARNING
            && row.getRowStatus() == ImportRowStatus.VALID) {
          row.setRowStatus(ImportRowStatus.WARNING);
        }
      }
    }

    for (ImportJobRow rowEntity : rowEntities) {
      Object rawPayload = rowEntity.getRawPayloadJson();
      if (rawPayload instanceof Map<?, ?> map) {
        @SuppressWarnings("unchecked")
        Map<String, String> stringMap = (Map<String, String>) map;
        List<ImportValidationMessage> messages = new ArrayList<>();
        Map<String, Object> mappedPayload = rowEntity.getMappedPayloadJson();
        Object dto = toDto(definition, mappedPayload);
        messages.addAll(beanValidationWrapper.validate(dto, rowEntity.getRowNumber()));
        messages.addAll(runRowValidators(job.getEntityType(), dto, rowEntity.getRowNumber()));
        importMappingService.mapRow(
            rowEntity.getRowNumber(), stringMap, mapping, definition.fields(), messages);
        for (ImportValidationMessage message : messages) {
          messageEntities.add(toMessageEntity(rowEntity, message));
        }
      }
    }

    rowRepository.saveAll(rowEntities);
    rowMessageRepository.saveAll(messageEntities);

    applySummary(job, rowEntities);
    job.setStatus(ImportJobStatus.PREVIEWED);
    importJobRepository.save(job);

    return buildPreviewResponse(job, rowEntities, messageEntities);
  }

  public ImportPreviewResponse getPreview(UUID jobId) {
    ImportJob job = getJobOrThrow(jobId);
    List<ImportJobRow> rows = rowRepository.findAllByJobOrderByRowNumberAsc(job);
    List<ImportJobRowMessage> messages = rowMessageRepository.findAllByJobRowIn(rows);
    return buildPreviewResponse(job, rows, messages);
  }

  @Transactional
  public ImportJobSummaryResponse execute(UUID jobId) {
    ImportJob job = getJobOrThrow(jobId);
    ImportDefinition<?> definition = definitionRegistry.get(job.getEntityType());

    List<ImportJobRow> importableRows =
        rowRepository.findAllByJobAndRowStatusInOrderByRowNumberAsc(
            job, List.of(ImportRowStatus.VALID, ImportRowStatus.WARNING));

    int imported = 0;
    for (int i = 0; i < importableRows.size(); i += CHUNK_SIZE) {
      List<ImportJobRow> chunk =
          importableRows.subList(i, Math.min(i + CHUNK_SIZE, importableRows.size()));
      List<Object> dtos =
          chunk.stream().map(row -> toDto(definition, row.getMappedPayloadJson())).toList();
      saveChunk(definition, dtos);
      chunk.forEach(row -> row.setRowStatus(ImportRowStatus.IMPORTED));
      imported += chunk.size();
    }

    List<ImportJobRow> skippedRows =
        rowRepository.findAllByJobAndRowStatusInOrderByRowNumberAsc(
            job, Set.of(ImportRowStatus.ERROR));
    skippedRows.forEach(row -> row.setRowStatus(ImportRowStatus.SKIPPED));

    rowRepository.saveAll(importableRows);
    rowRepository.saveAll(skippedRows);

    job.setImportedRows(imported);
    job.setSkippedRows(skippedRows.size());
    job.setStatus(ImportJobStatus.EXECUTED);
    importJobRepository.save(job);

    return toSummary(job);
  }

  public ImportJobSummaryResponse getResult(UUID jobId) {
    ImportJob job = getJobOrThrow(jobId);
    return toSummary(job);
  }

  private ImportJob getJobOrThrow(UUID jobId) {
    return importJobRepository
        .findById(jobId)
        .orElseThrow(() -> new NotFoundException(MessageConstant.PAGE_NOT_FOUND));
  }

  private ParsedFile readParsedFile(String sourcePath, String fileName) {
    try (InputStream inputStream = openSourceInputStream(sourcePath)) {
      return parserResolver.resolve(fileName).parse(inputStream);
    } catch (IOException ex) {
      throw new BadRequestException(MessageConstant.BAD_REQUEST);
    }
  }

  private InputStream openSourceInputStream(String sourcePath) throws IOException {
    if (!StringUtils.hasText(sourcePath)) {
      throw new BadRequestException(MessageConstant.BAD_REQUEST);
    }

    if (sourcePath.startsWith(BLOB_SOURCE_PREFIX)) {
      BlobLocation blobLocation = parseBlobLocation(sourcePath);
      return blobStorageProvider.openInputStream(blobLocation.containerName(), blobLocation.blobKey());
    }

    // Backward compatibility for jobs created before blob migration.
    return Files.newInputStream(Path.of(sourcePath));
  }

  private String uploadSourceFile(MultipartFile file) {
    try {
      BackendUploadRequest request = new BackendUploadRequest();
      request.setStorageProvider(StorageProvider.AZURE_BLOB_STORAGE);
      request.setStorageUseCase(StorageUseCase.IMPORT_FILE);

      StorageObjectResponse uploaded = storageObjectService.uploadViaBackend(file, request);
      return BLOB_SOURCE_PREFIX + uploaded.getContainerName() + "/" + uploaded.getBlobKey();
    } catch (RuntimeException ex) {
      throw new BadRequestException(MessageConstant.BAD_REQUEST);
    }
  }

  private BlobLocation parseBlobLocation(String sourcePath) {
    String payload = sourcePath.substring(BLOB_SOURCE_PREFIX.length());
    int separatorIndex = payload.indexOf('/');
    if (separatorIndex <= 0 || separatorIndex == payload.length() - 1) {
      throw new BadRequestException(MessageConstant.BAD_REQUEST);
    }

    return new BlobLocation(payload.substring(0, separatorIndex), payload.substring(separatorIndex + 1));
  }

  private record BlobLocation(String containerName, String blobKey) {}

  private List<SuggestedMappingResponse> suggestMappings(
      List<String> headers, ImportDefinition<?> definition) {
    Map<String, String> normalizedFieldMap =
        definition.fields().stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    field -> normalize(field.fieldName()), ImportFieldDefinition::fieldName));

    List<SuggestedMappingResponse> suggestions = new ArrayList<>();
    for (String header : headers) {
      String targetField = normalizedFieldMap.get(normalize(header));
      if (targetField != null) {
        suggestions.add(new SuggestedMappingResponse(header, targetField));
      }
    }
    return suggestions;
  }

  private String normalize(String value) {
    return value == null
        ? ""
        : value.replace("_", "").replace(" ", "").toLowerCase(Locale.ROOT).trim();
  }

  private Map<String, String> getMapping(ImportJob job) {
    return mappingRepository.findAllByJob(job).stream()
        .collect(
            java.util.stream.Collectors.toMap(
                ImportJobColumnMapping::getSourceColumn, ImportJobColumnMapping::getTargetField));
  }

  private void clearRows(ImportJob job) {
    List<ImportJobRow> rows = rowRepository.findAllByJobOrderByRowNumberAsc(job);
    if (!rows.isEmpty()) {
      rowMessageRepository.deleteAllByJobRowIn(rows);
      rowRepository.deleteAllByJob(job);
    }
  }

  private ImportRowStatus resolveStatus(List<ImportValidationMessage> messages) {
    boolean hasError = messages.stream().anyMatch(m -> m.severity() == Severity.ERROR);
    if (hasError) {
      return ImportRowStatus.ERROR;
    }
    boolean hasWarning = messages.stream().anyMatch(m -> m.severity() == Severity.WARNING);
    return hasWarning ? ImportRowStatus.WARNING : ImportRowStatus.VALID;
  }

  private void applySummary(ImportJob job, List<ImportJobRow> rows) {
    int valid = 0;
    int warning = 0;
    int error = 0;
    for (ImportJobRow row : rows) {
      if (row.getRowStatus() == ImportRowStatus.VALID) {
        valid++;
      } else if (row.getRowStatus() == ImportRowStatus.WARNING) {
        warning++;
      } else if (row.getRowStatus() == ImportRowStatus.ERROR) {
        error++;
      }
    }

    job.setTotalRows(rows.size());
    job.setValidRows(valid);
    job.setWarningRows(warning);
    job.setErrorRows(error);
    job.setImportedRows(0);
    job.setSkippedRows(0);
  }

  private ImportPreviewResponse buildPreviewResponse(
      ImportJob job, List<ImportJobRow> rows, List<ImportJobRowMessage> messages) {
    Map<UUID, List<ImportJobRowMessage>> messagesByRowId =
        messages.stream()
            .collect(
                java.util.stream.Collectors.groupingBy(message -> message.getJobRow().getId()));

    List<ImportPreviewRowResponse> previewRows =
        rows.stream()
            .map(
                row ->
                    new ImportPreviewRowResponse(
                        row.getRowNumber(),
                        row.getRowStatus(),
                        messagesByRowId.getOrDefault(row.getId(), List.of()).stream()
                            .map(
                                message ->
                                    new ImportPreviewMessageResponse(
                                        message.getFieldName(),
                                        message.getSeverity(),
                                        message.getCode(),
                                        message.getMessage(),
                                        message.getRejectedValue()))
                            .toList()))
            .toList();

    return new ImportPreviewResponse(job.getId(), toSummary(job), previewRows);
  }

  private ImportJobSummaryResponse toSummary(ImportJob job) {
    return new ImportJobSummaryResponse(
        nullable(job.getTotalRows()),
        nullable(job.getValidRows()),
        nullable(job.getWarningRows()),
        nullable(job.getErrorRows()),
        nullable(job.getImportedRows()),
        nullable(job.getSkippedRows()));
  }

  private int nullable(Integer value) {
    return value == null ? 0 : value;
  }

  private void resetSummary(ImportJob job) {
    job.setTotalRows(0);
    job.setValidRows(0);
    job.setWarningRows(0);
    job.setErrorRows(0);
    job.setImportedRows(0);
    job.setSkippedRows(0);
  }

  @SuppressWarnings("unchecked")
  private Object toDto(ImportDefinition<?> definition, Map<String, Object> mappedPayload) {
    return ((ImportDefinition<Object>) definition).toDto(mappedPayload);
  }

  @SuppressWarnings("unchecked")
  private void saveChunk(ImportDefinition<?> definition, List<Object> dtos) {
    ((ImportDefinition<Object>) definition).saveChunk(dtos);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private List<ImportValidationMessage> runRowValidators(
      String entityType, Object dto, int rowNumber) {
    List<ImportValidationMessage> messages = new ArrayList<>();
    for (var validator : validatorRegistry.rowValidators(entityType)) {
      messages.addAll(
          ((com.vndat00.springbootboilerplate.importexport.core.ImportRowValidator) validator)
              .validate(dto, rowNumber));
    }
    return messages;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private List<ImportValidationMessage> runDatasetValidators(String entityType, List<Object> dtos) {
    List<ImportValidationMessage> messages = new ArrayList<>();
    for (var validator : validatorRegistry.datasetValidators(entityType)) {
      messages.addAll(
          ((com.vndat00.springbootboilerplate.importexport.core.ImportDatasetValidator) validator)
              .validate(dtos));
    }
    return messages;
  }

  private ImportJobRowMessage toMessageEntity(ImportJobRow row, ImportValidationMessage message) {
    ImportJobRowMessage entity = new ImportJobRowMessage();
    entity.setJobRow(row);
    entity.setFieldName(message.field());
    entity.setSeverity(message.severity());
    entity.setCode(message.code());
    entity.setMessage(message.message());
    entity.setRejectedValue(message.rejectedValue());
    return entity;
  }
}
