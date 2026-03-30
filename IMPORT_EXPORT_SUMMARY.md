# Import/Export Feature - Implementation Summary

## Overview

A complete, reusable import/export module has been successfully implemented for Spring Boot. The implementation follows
enterprise best practices with clean architecture layering, allowing easy extension to other entities beyond `Note`.

## Architecture

### Layer 1: File Parsing (`importexport/parser`)

- **CsvTabularFileParser**: Parses CSV files using Apache Commons CSV
- **ExcelTabularFileParser**: Parses XLSX files using Apache POI
- **TabularFileParserResolver**: Factory to select appropriate parser

### Layer 2: Core Abstractions (`importexport/core`)

- **ImportDefinition**: Strategy for entity-specific import metadata and persistence
- **ExportDefinition**: Strategy for entity-specific export configuration
- **ImportFieldDefinition**: Metadata for each importable field
- **ExportColumnDefinition**: Metadata for each exportable column
- **ImportRowValidator** / **ImportDatasetValidator**: Validation strategies
- **ParsedFile** / **ParsedRow**: Neutral tabular data representation

### Layer 3: Mapping & Validation (`importexport/service`)

- **ImportMappingService**: Maps file columns to entity fields with type conversion
- **DataTypeConverter**: Converts string values to target types (Integer, Decimal, DateTime, Enum, etc.)
- **BeanValidationWrapper**: Wraps Jakarta Bean Validation
- **ImportValidatorRegistry** / **ExportDefinitionRegistry**: Dynamic strategy registries

### Layer 4: Import Job Orchestration (`importexport/service/ImportJobService`)

- **createJob**: Upload file, parse, return sample data and suggested mappings
- **saveMapping**: Validate and persist column mappings
- **preview**: Run full validation pipeline on all rows
- **execute**: Import VALID and WARNING rows, skip ERROR rows
- **getResult**: Retrieve import summary

### Layer 5: Export Service (`importexport/service/ExportService`)

- **exportData**: Export actual entity data as CSV/XLSX
- **exportTemplate**: Export import template with headers

### Layer 6: Persistence (`domain/model`, `repository`)

- **ImportJob**: Main import session tracker
- **ImportJobColumnMapping**: Stores file→field mappings
- **ImportJobRow**: Persisted row with raw and mapped payloads (jsonb)
- **ImportJobRowMessage**: Per-field validation issues

### Layer 7: REST Controllers

- **ImportController**: `/import/*` endpoints for full import workflow
- **ExportController**: `/export/*` endpoints for data and template export

## Validation Pipeline

Each row flows through:

1. **Mapping Phase**: Raw cell values → mapped DTO fields with type conversion
2. **Bean Validation**: Jakarta `@NotBlank`, `@Email`, `@Min`, `@Max`, `@Size`, etc.
3. **Row Validators**: Business logic per row (e.g., non-negative view count)
4. **Dataset Validators**: Cross-row checks (e.g., duplicates in same file)

**Result**: Each row marked as `VALID`, `WARNING`, or `ERROR`

## Persisted Data Model

### import_job

- id, entityType, fileName, fileType, sourceFilePath
- status (CREATED, MAPPING_SAVED, PREVIEWED, EXECUTED)
- totalRows, validRows, warningRows, errorRows, importedRows, skippedRows
- createdBy, createdAt, updatedAt

### import_job_column_mapping

- id, jobId, sourceColumn, targetField

### import_job_row

- id, jobId, rowNumber
- rawPayloadJson (jsonb): Original file values
- mappedPayloadJson (jsonb): Type-converted field values
- rowStatus (VALID, WARNING, ERROR, IMPORTED, SKIPPED)

### import_job_row_message

- id, jobRowId, fieldName, severity, code, message, rejectedValue

## Note Implementation

All of the following are complete and integrated:

- **NoteImportRowDto**: DTO with Bean Validation annotations
- **NoteImportDefinition**: Maps `NoteImportRowDto` → `Note` entity
- **NoteBusinessRowValidator**: Business checks (e.g., non-completed archived notes warn)
- **NoteDatasetValidator**: Checks for duplicate titles+emails in same file
- **NoteExportDefinition**: Extracts `Note` data with 16 export columns

## API Endpoints

### Import

```
POST   /api/import/jobs
       Query: entityType=note
       Body: multipart file
       Returns: jobId, headers, sampleRows, suggestedMappings

GET    /api/import/definitions/{entityType}
       Returns: Field metadata for entity type

POST   /api/import/jobs/{jobId}/mapping
       Body: ColumnMappingRequest with source→target mappings

POST   /api/import/jobs/{jobId}/preview
       Returns: All rows validated with status and messages

GET    /api/import/jobs/{jobId}/preview
       Returns: Stored preview (if available)

POST   /api/import/jobs/{jobId}/execute
       Imports VALID and WARNING rows, skips ERROR rows
       Returns: Summary (imported, skipped counts)

GET    /api/import/jobs/{jobId}/result
       Returns: Final import summary
```

### Export

```
GET    /api/export/{entityType}
       Query: format=csv|xlsx
       Returns: File download with all entity data

GET    /api/export/{entityType}/template
       Query: format=csv|xlsx
       Returns: File download with import template
```

## Extending for Another Entity

To add `Task` import/export:

### 1. Create DTO

```java

@Getter
@Setter
public class TaskImportRowDto {
    @NotBlank
    private String title;
    @NotNull
    private LocalDateTime dueDate;
    // ... more fields
}
```

### 2. Create ImportDefinition

```java

@Component
public class TaskImportDefinition implements ImportDefinition<TaskImportRowDto> {
    public String entityType() {
        return "task";
    }

    public List<ImportFieldDefinition> fields() {
        return List.of(
                new ImportFieldDefinition("title", "Title", STRING, true, true, true, null, null),
                // ... more fields
        );
    }

    public TaskImportRowDto toDto(Map<String, Object> values) { /* ... */ }

    public void saveChunk(List<TaskImportRowDto> rows) {
        taskRepository.saveAll(rows.stream().map(...).toList());
    }
}
```

### 3. Create Validators (Optional)

```java

@Component
public class TaskBusinessRowValidator implements ImportRowValidator<TaskImportRowDto> {
    public String entityType() {
        return "task";
    }

    public List<ImportValidationMessage> validate(TaskImportRowDto row, int rowNumber) {
        // Business logic
    }
}
```

### 4. Create ExportDefinition

```java

@Component
public class TaskExportDefinition implements ExportDefinition<Task> {
    public String entityType() {
        return "task";
    }

    public List<ExportColumnDefinition<Task>> columns() {
        return List.of(...);
    }

    public List<Task> fetchAll() {
        return taskRepository.findAll();
    }
}
```

Register in Spring → automatically discovered via `@Component` and added to registries.

## Key Design Decisions

1. **Staging before execution**: Import jobs are persisted in preview state before execution, enabling UI-driven
   workflows.
2. **jsonb storage**: Payload stored as jsonb for queryability and debuggability.
3. **Row-level tracking**: Each row persisted with its status and issues, not just summary counts.
4. **Reusable registries**: Strategy pattern with Spring auto-discovery avoids hardcoded conditionals.
5. **Chunked execution**: 200-row batches prevent memory issues on large files.
6. **Null-safe converter**: DataTypeConverter handles missing values gracefully.
7. **Warning rows imported**: Only ERROR rows skipped; WARNING rows still imported unless overridden by business logic.
8. **Bean Validation on DTO**: Centralized validation rules via standard annotations.

## File Structure

```
importexport/
├── core/                          # Reusable abstractions
│   ├── DataType.java
│   ├── FileFormat.java
│   ├── ImportJobStatus.java
│   ├── ImportRowStatus.java
│   ├── Severity.java
│   ├── ParsedFile.java
│   ├── ParsedRow.java
│   ├── ImportFieldDefinition.java
│   ├── ImportValidationMessage.java
│   ├── ImportRowIssue.java
│   ├── ImportDefinition.java
│   ├── ExportColumnDefinition.java
│   ├── ExportDefinition.java
│   ├── ImportRowValidator.java
│   └── ImportDatasetValidator.java
├── parser/                        # File parsing
│   ├── CsvTabularFileParser.java
│   ├── ExcelTabularFileParser.java
│   └── TabularFileParserResolver.java
├── service/                       # Orchestration & utilities
│   ├── ImportJobService.java
│   ├── ImportMappingService.java
│   ├── DataTypeConverter.java
│   ├── BeanValidationWrapper.java
│   ├── ImportDefinitionRegistry.java
│   ├── ExportDefinitionRegistry.java
│   ├── ImportValidatorRegistry.java
│   ├── ExportService.java
│   └── ExportFormat.java
└── note/                          # Note-specific implementation
    ├── NoteImportRowDto.java
    ├── NoteImportDefinition.java
    ├── NoteBusinessRowValidator.java
    ├── NoteDatasetValidator.java
    └── NoteExportDefinition.java

domain/model/
├── ImportJob.java
├── ImportJobColumnMapping.java
├── ImportJobRow.java
└── ImportJobRowMessage.java

repository/
├── ImportJobRepository.java
├── ImportJobColumnMappingRepository.java
├── ImportJobRowRepository.java
└── ImportJobRowMessageRepository.java

controller/
├── ImportController.java
└── ExportController.java

payload/request/importexport/
├── ColumnMappingItem.java
├── ColumnMappingRequest.java
└── CreateImportJobRequest.java

payload/response/importexport/
├── ImportJobCreateResponse.java
├── ImportJobSummaryResponse.java
├── ImportPreviewRowResponse.java
├── ImportPreviewResponse.java
├── ImportPreviewMessageResponse.java
├── ImportFieldMetadataResponse.java
├── ImportDefinitionResponse.java
└── SuggestedMappingResponse.java
```

## Testing Checklist

- [ ] Upload CSV file for Note import
- [ ] Upload XLSX file for Note import
- [ ] Verify sample rows returned in createJob response
- [ ] Verify suggested mappings detected
- [ ] Save custom mapping
- [ ] Run preview validation
- [ ] Verify row status (VALID/WARNING/ERROR) calculated correctly
- [ ] Verify validation messages include field, code, message, rejectedValue
- [ ] Execute import, verify only VALID+WARNING imported
- [ ] Get result summary, verify counts match
- [ ] Export Note data to CSV
- [ ] Export Note data to XLSX
- [ ] Export Note import template (CSV and XLSX)
- [ ] Verify template has importable headers only

## Deployed Artifacts

1. **Flyway Migration**: `V3__create_import_export_job_tables.sql` - Creates all import job tables with jsonb support
2. **Dependencies**: Added `commons-csv`, `poi-ooxml` to pom.xml
3. **All Java classes**: 40+ classes across layers
4. **API Endpoints**: 7 import + 2 export endpoints
5. **Database**: 4 new tables with proper indexes and constraints

## Next Steps

1. Deploy migration to database
2. Test import/export workflows
3. Add entity-specific Note validators as business requirements emerge
4. Implement `Task` or another entity using the extension guide above
5. Consider adding:
    - Async import execution for very large files
    - Email notifications on import completion
    - Import history and rollback capability
    - Rate limiting on exports

