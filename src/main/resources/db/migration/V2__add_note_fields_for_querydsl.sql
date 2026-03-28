-- Add 10 new columns to notes table for QueryDSL testing
ALTER TABLE notes ADD COLUMN title VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE notes ADD COLUMN author_email VARCHAR(255);
ALTER TABLE notes ADD COLUMN category VARCHAR(100);
ALTER TABLE notes ADD COLUMN estimated_time DECIMAL(10, 2);
ALTER TABLE notes ADD COLUMN is_completed BOOLEAN DEFAULT FALSE;
ALTER TABLE notes ADD COLUMN is_archived BOOLEAN DEFAULT FALSE;
ALTER TABLE notes ADD COLUMN view_count INTEGER DEFAULT 0;
ALTER TABLE notes ADD COLUMN tags TEXT;
ALTER TABLE notes ADD COLUMN status VARCHAR(50) DEFAULT 'DRAFT';
ALTER TABLE notes ADD COLUMN due_date TIMESTAMP;
ALTER TABLE notes ADD COLUMN description TEXT;

-- Create indexes on frequently searched columns for better query performance
CREATE INDEX IF NOT EXISTS idx_notes_status ON notes (status);
CREATE INDEX IF NOT EXISTS idx_notes_category ON notes (category);
CREATE INDEX IF NOT EXISTS idx_notes_is_completed ON notes (is_completed);
CREATE INDEX IF NOT EXISTS idx_notes_author_email ON notes (author_email);
CREATE INDEX IF NOT EXISTS idx_notes_due_date ON notes (due_date);

