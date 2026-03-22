CREATE TABLE IF NOT EXISTS notes (
    id UUID PRIMARY KEY,
    content TEXT NOT NULL,
    priority INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT chk_notes_priority_range CHECK (priority IS NULL OR (priority BETWEEN 1 AND 10))
);

CREATE TABLE IF NOT EXISTS storage_object (
    id UUID PRIMARY KEY,
    blob_key TEXT NOT NULL UNIQUE,
    container_name VARCHAR(255) NOT NULL,
    file_name TEXT NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    declared_size BIGINT NOT NULL,
    actual_size BIGINT,
    storage_provider VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    upload_method VARCHAR(50) NOT NULL,
    storage_use_case VARCHAR(50) NOT NULL,
    note_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_storage_object_note FOREIGN KEY (note_id) REFERENCES notes (id)
);

CREATE INDEX IF NOT EXISTS idx_storage_object_note_id ON storage_object (note_id);
CREATE INDEX IF NOT EXISTS idx_storage_object_status ON storage_object (status);
CREATE INDEX IF NOT EXISTS idx_storage_object_storage_use_case ON storage_object (storage_use_case);
