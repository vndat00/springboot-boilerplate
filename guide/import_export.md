# Prompt: Generate reusable import/export module for Spring Boot with concrete implementation for `Note`

You are a senior Java + Spring Boot architect and backend engineer.

I have a Spring Boot project and want you to generate production-grade code for a reusable import/export module that can
be used not only for `Note` but also for other entities later.

Your task is to generate the full code skeleton and key implementation for this feature, following enterprise best
practices.

---

## 1. Business context

I have an entity named `Note` with around 20 fields.

I want an import/export feature similar in spirit to Jira import/export.

### Import flow

1. User uploads file (`csv` and `xlsx` supported).
2. System reads headers and sample rows.
3. User chooses which file column maps to which database field.
4. System validates each row and returns preview result.
5. Preview must show:
    - which row has `warning`
    - which row has `error`
    - the exact reason
    - which field caused the problem
6. When executing import:
    - rows with `ERROR` must be skipped
    - rows with `WARNING` may still be imported
7. The design must be reusable for entities other than `Note`.

### Export flow

1. Export `Note` data to `csv` and `xlsx`.
2. Also support exporting an import template.
3. Export logic should reuse metadata/config as much as possible.

---

## 2. Main technical requirements

### General requirements

- Use Spring Boot.
- Use clean architecture / layered architecture.
- Use reusable design, not a `Note-only` hardcoded service.
- Code must be structured so that later I can add:
    - `TaskImportDefinition`
    - `CustomerImportDefinition`
    - etc.
- Follow production best practices.
- Use DTOs, not binding import rows directly to JPA entities.
- Support large-file-friendly design.
- Avoid loading everything into memory if unnecessary.
- Use transactional boundaries appropriately.
- Make code readable and extensible.

### File formats

Support:

- CSV
- XLSX

### Libraries

Use these:

- Apache Commons CSV for CSV parsing/writing
- Apache POI for Excel parsing/writing
- Jakarta Bean Validation / Spring Validation for row validation
- Spring Data JPA for persistence
- Jackson for JSON payload storage if needed

If batch execution is needed, you may optionally design with Spring Batch or implement a simpler chunk-based service
execution. Prefer pragmatic design. Do not overengineer unless clearly useful.

---

## 3. Design direction to follow

Implement this as a reusable import/export framework with a concrete `Note` implementation.

### Core architectural idea

Split into these layers/modules:

#### A. File parsing layer

Responsible for:

- detecting file type
- parsing headers
- reading rows
- returning a neutral tabular representation

#### B. Import definition metadata layer

Each entity defines:

- what fields are importable/exportable
- display name
- data type
- required or optional
- enum/reference handling
- conversion rules
- validators
- export column order

#### C. Mapping layer

Responsible for:

- accepting user-selected mapping
- mapping file columns into DTO fields
- type conversion
- parse error collection

#### D. Validation layer

Must support:

- parse/type validation
- bean validation
- business validation
- cross-row validation
- file-level validation

#### E. Staging / preview layer

Do not import directly after upload.

Use a preview/import job concept:

- upload file
- create import job
- save mapping
- validate preview
- store row-level issues
- execute import later

#### F. Execution layer

When confirm import is triggered:

- import only valid/warning rows
- skip error rows
- return summary counts

#### G. Export layer

Support:

- export data
- export import-template
- reuse field metadata where possible

---

## 4. Required core abstractions

Generate reusable interfaces/classes like these. You may improve naming if needed.

### File parser abstraction

- `TabularFileParser`
- `CsvTabularFileParser`
- `ExcelTabularFileParser`

### Parsed model

- `ParsedFile`
- `ParsedRow`
- `RawCellValue` if useful

### Import definition abstraction

- `ImportDefinition<TImportDto, TEntity>`
- `ImportFieldDefinition`
- `DataType` enum

### Export definition abstraction

- `ExportDefinition<TEntity>`
- `ExportColumnDefinition<TEntity>`

### Mapping

- `ColumnMappingRequest`
- `ColumnMappingItem`
- mapping service to convert row -> DTO

### Validation

- `ImportValidationMessage`
- `ImportRowIssue`
- `Severity` enum (`WARNING`, `ERROR`)
- `ImportRowValidator<T>`
- `ImportDatasetValidator<T>`
- wrapper validator for Bean Validation

### Import job / preview / execution

- `ImportJob`
- `ImportJobStatus`
- `ImportJobRow`
- `ImportJobRowMessage`
- services for:
    - upload/create job
    - save mapping
    - preview validation
    - execute import
    - get job result

### Export services

- data export service
- import template export service

---

## 5. Validation rules to support

Design validation in a reusable pipeline.

### Validation categories

#### 5.1 File-level validation

Examples:

- unsupported format
- duplicate header
- empty file
- missing required mapping
- one target field mapped multiple times

#### 5.2 Parse/type validation

Examples:

- invalid integer
- invalid date
- invalid enum literal
- invalid boolean

#### 5.3 Bean validation

Use Jakarta Bean Validation on row DTOs.

Examples:

- `@NotBlank`
- `@Size`
- `@Min`
- `@Max`
- `@Email`
- custom constraint if needed

#### 5.4 Business validation

Examples:

- referenced user/category not found
- duplicate business key in DB
- permission restrictions
- invalid state transition

#### 5.5 Cross-row validation

Examples:

- duplicate value inside same import file
- conflicting rows in same file

### Validation output

For each issue, return:

- row number
- field name
- severity
- code
- message
- rejected value if possible

---

## 6. Preview/result behavior

The system must support a preview response like this conceptually:

```json
{
  "jobId": "uuid",
  "summary": {
    "totalRows": 120,
    "validRows": 100,
    "warningRows": 12,
    "errorRows": 8
  },
  "rows": [
    {
      "rowNumber": 2,
      "status": "ERROR",
      "messages": [
        {
          "field": "priority",
          "severity": "ERROR",
          "code": "INVALID_NUMBER",
          "message": "Priority must be a number from 1 to 5"
        }
      ]
    }
  ]
}
```

Execution result must include:

- total rows
- imported rows
- skipped rows
- warning rows
- error rows

---

## 7. API design to generate

Generate REST APIs for this flow.

### Import APIs

- `POST /api/import/jobs`
    - upload file
    - create import job
    - return headers + sample rows + suggested mappings

- `GET /api/import/definitions/{entityType}`
    - return importable fields metadata

- `POST /api/import/jobs/{jobId}/mapping`
    - save user-selected mapping

- `POST /api/import/jobs/{jobId}/preview`
    - perform preview validation

- `GET /api/import/jobs/{jobId}/preview`
    - get preview result

- `POST /api/import/jobs/{jobId}/execute`
    - execute import

- `GET /api/import/jobs/{jobId}/result`
    - get import result summary

### Export APIs

- `GET /api/export/{entityType}`
    - export actual data

- `GET /api/export/{entityType}/template`
    - export import template

Support query params like:

- `format=csv`
- `format=xlsx`

---

## 8. Concrete implementation for `Note`

Implement the reusable framework and also provide a concrete `Note` example.

### For `Note`, generate:

- `Note` JPA entity integration
- `NoteImportRowDto`
- `NoteImportDefinition`
- `NoteBusinessRowValidator`
- `NoteDatasetValidator` if useful
- `NoteExportDefinition`
- mapping from DTO -> entity
- persistence logic for `Note`

### Important

Do not bind import file rows directly into the JPA entity.

Use `NoteImportRowDto`.

### Assume `Note` has around 20 fields

If exact fields are not provided, create a realistic example with fields such as:

- id
- title
- description
- priority
- status
- category
- tags
- assigneeEmail
- dueDate
- createdAt
- updatedAt
- etc.

Make the code easy to adapt when I replace these with my real fields.

---

## 9. Database design to generate

Generate JPA entities and migrations/schema proposal for staging/import tracking tables such as:

- `import_job`
- `import_job_column_mapping`
- `import_job_row`
- `import_job_row_message`

### Suggested fields

#### `import_job`

- id
- entityType
- fileName
- fileType
- status
- totalRows
- validRows
- warningRows
- errorRows
- importedRows
- skippedRows
- createdBy
- createdAt
- updatedAt

#### `import_job_column_mapping`

- id
- jobId
- sourceColumn
- targetField

#### `import_job_row`

- id
- jobId
- rowNumber
- rawPayloadJson
- mappedPayloadJson
- rowStatus

#### `import_job_row_message`

- id
- jobRowId
- fieldName
- severity
- code
- message
- rejectedValue

If useful, store JSON as `text` or `jsonb` depending on DB preference.

---

## 10. Implementation style requirements

Generate code with these standards:

- Java 17+
- Spring Boot style
- constructor injection
- clean DTO/request/response models
- proper enum usage
- avoid field injection
- meaningful naming
- reusable abstractions
- comments only where necessary
- good separation of concerns
- no massive god-class
- avoid hardcoded `if entityType == NOTE` everywhere; use registry/strategy pattern where appropriate

Use:

- strategy pattern / registry for entity-specific import/export definition lookup
- helper utilities for conversion and normalization
- clear error code conventions
- chunked persistence where suitable

---

## 11. What code I want you to output

Generate all of the following.

### A. High-level design section

Before code, briefly explain the architecture.

### B. Full code skeleton with meaningful implementation

Include:

- enums
- DTOs
- interfaces
- services
- controllers
- repositories
- entity classes
- parser implementations
- validator implementations
- mapping service
- note-specific classes
- export service
- response models

### C. Example validation flow

Show how one row moves through:

- raw row
- mapped DTO
- bean validation
- business validation
- final row status

### D. Example API request/response payloads

For:

- upload
- mapping
- preview
- execute
- export

### E. Database schema / migration draft

Provide SQL or Flyway migration drafts for import job tables.

### F. Extension guide

At the end, explain how to add another entity later, for example `Task`.

---

## 12. Important implementation constraints

- Do not make the solution `Note-only`.
- Do not skip row-level preview logic.
- Do not skip mapping phase.
- Do not skip warning/error distinction.
- Do not import invalid rows.
- Do not use a simplistic one-endpoint upload-and-import design.
- Do not tightly couple controller to parsing/validation details.
- Do not overcomplicate with unnecessary distributed architecture.

---

## 13. Output format

Please output in this order:

1. Architecture overview
2. Package structure
3. Core shared abstractions
4. Import job persistence model
5. File parser implementations
6. Mapping logic
7. Validation pipeline
8. `Note` concrete implementation
9. REST controllers
10. Export implementation
11. SQL/Flyway migration draft
12. How to extend for another entity

All code should be complete enough that I can copy and adapt into my project.

Where exact business fields are unknown, use reasonable placeholders but keep the design extensible.
