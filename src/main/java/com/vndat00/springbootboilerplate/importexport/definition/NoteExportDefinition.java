package com.vndat00.springbootboilerplate.importexport.definition;

import com.vndat00.springbootboilerplate.domain.enums.importexport.DataType;
import com.vndat00.springbootboilerplate.domain.model.Note;
import com.vndat00.springbootboilerplate.importexport.core.ExportColumnDefinition;
import com.vndat00.springbootboilerplate.importexport.core.ExportDefinition;
import com.vndat00.springbootboilerplate.repository.NoteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoteExportDefinition implements ExportDefinition<Note> {
  private final NoteRepository noteRepository;

  @Override
  public String entityType() {
    return "note";
  }

  @Override
  public List<ExportColumnDefinition<Note>> columns() {
    return List.of(
        new ExportColumnDefinition<>("id", "id", DataType.UUID, Note::getId),
        new ExportColumnDefinition<>("title", "title", DataType.STRING, Note::getTitle),
        new ExportColumnDefinition<>("content", "note", DataType.STRING, Note::getContent),
        new ExportColumnDefinition<>("priority", "priority", DataType.INTEGER, Note::getPriority),
        new ExportColumnDefinition<>(
            "authorEmail", "author_email", DataType.STRING, Note::getAuthorEmail),
        new ExportColumnDefinition<>("category", "category", DataType.STRING, Note::getCategory),
        new ExportColumnDefinition<>(
            "estimatedTime", "estimated_time", DataType.DECIMAL, Note::getEstimatedTime),
        new ExportColumnDefinition<>(
            "isCompleted", "is_completed", DataType.BOOLEAN, Note::getIsCompleted),
        new ExportColumnDefinition<>(
            "isArchived", "is_archived", DataType.BOOLEAN, Note::getIsArchived),
        new ExportColumnDefinition<>(
            "viewCount", "view_count", DataType.INTEGER, Note::getViewCount),
        new ExportColumnDefinition<>("tags", "tags", DataType.STRING, Note::getTags),
        new ExportColumnDefinition<>("status", "status", DataType.ENUM, Note::getStatus),
        new ExportColumnDefinition<>("dueDate", "due_date", DataType.DATE_TIME, Note::getDueDate),
        new ExportColumnDefinition<>(
            "description", "description", DataType.STRING, Note::getDescription),
        new ExportColumnDefinition<>(
            "createdAt", "created_at", DataType.DATE_TIME, Note::getCreatedAt),
        new ExportColumnDefinition<>(
            "updatedAt", "updated_at", DataType.DATE_TIME, Note::getUpdatedAt));
  }

  @Override
  public List<Note> fetchAll() {
    return noteRepository.findAll();
  }
}
