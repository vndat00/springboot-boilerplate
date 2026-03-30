CREATE TABLE IF NOT EXISTS import_job (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    file_name TEXT NOT NULL,
    file_type VARCHAR(20) NOT NULL,
    source_file_path TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    total_rows INTEGER DEFAULT 0,
    valid_rows INTEGER DEFAULT 0,
    warning_rows INTEGER DEFAULT 0,
    error_rows INTEGER DEFAULT 0,
    imported_rows INTEGER DEFAULT 0,
    skipped_rows INTEGER DEFAULT 0,
    created_by VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS import_job_column_mapping (
    id UUID PRIMARY KEY,
    job_id UUID NOT NULL,
    source_column VARCHAR(255) NOT NULL,
    target_field VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_import_job_column_mapping_job FOREIGN KEY (job_id) REFERENCES import_job (id)
);

CREATE TABLE IF NOT EXISTS import_job_row (
    id UUID PRIMARY KEY,
    job_id UUID NOT NULL,
    row_number INTEGER NOT NULL,
    raw_payload_json JSONB,
    mapped_payload_json JSONB,
    row_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_import_job_row_job FOREIGN KEY (job_id) REFERENCES import_job (id)
);

CREATE TABLE IF NOT EXISTS import_job_row_message (
    id UUID PRIMARY KEY,
    job_row_id UUID NOT NULL,
    field_name VARCHAR(255),
    severity VARCHAR(20) NOT NULL,
    code VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    rejected_value TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_import_job_row_message_row FOREIGN KEY (job_row_id) REFERENCES import_job_row (id)
);

CREATE INDEX IF NOT EXISTS idx_import_job_entity_type ON import_job (entity_type);
CREATE INDEX IF NOT EXISTS idx_import_job_status ON import_job (status);
CREATE INDEX IF NOT EXISTS idx_import_job_row_job_id ON import_job_row (job_id);
CREATE INDEX IF NOT EXISTS idx_import_job_row_row_status ON import_job_row (row_status);
CREATE INDEX IF NOT EXISTS idx_import_job_row_message_row_id ON import_job_row_message (job_row_id);

