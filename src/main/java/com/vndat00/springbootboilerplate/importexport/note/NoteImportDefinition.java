package com.vndat00.springbootboilerplate.importexport.note;

import com.vndat00.springbootboilerplate.domain.enums.NoteStatus;
import com.vndat00.springbootboilerplate.domain.model.Note;
import com.vndat00.springbootboilerplate.importexport.common.MappedValueHelper;
import com.vndat00.springbootboilerplate.importexport.converter.DateTimeImportConverter;
import com.vndat00.springbootboilerplate.importexport.core.DataType;
import com.vndat00.springbootboilerplate.importexport.core.ImportDefinition;
import com.vndat00.springbootboilerplate.importexport.core.ImportFieldDefinition;
import com.vndat00.springbootboilerplate.repository.NoteRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoteImportDefinition implements ImportDefinition<NoteImportRowDto> {
  private final NoteRepository noteRepository;

  @Override
  public String entityType() {
    return "note";
  }

  @Override
  public Class<NoteImportRowDto> dtoType() {
    return NoteImportRowDto.class;
  }

  @Override
  public List<ImportFieldDefinition> fields() {
    return List.of(
        new ImportFieldDefinition(
            "title", "title", DataType.STRING, true, true, true, List.of(), null),
        new ImportFieldDefinition(
            "note", "note", DataType.STRING, true, true, true, List.of(), null),
        new ImportFieldDefinition(
            "priority", "priority", DataType.INTEGER, true, true, true, List.of(), null),
        new ImportFieldDefinition(
            "authorEmail", "author_email", DataType.STRING, false, true, true, List.of(), null),
        new ImportFieldDefinition(
            "category", "category", DataType.STRING, false, true, true, List.of(), null),
        new ImportFieldDefinition(
            "estimatedTime",
            "estimated_time",
            DataType.DECIMAL,
            false,
            true,
            true,
            List.of(),
            null),
        new ImportFieldDefinition(
            "isCompleted", "is_completed", DataType.BOOLEAN, false, true, true, List.of(), null),
        new ImportFieldDefinition(
            "isArchived", "is_archived", DataType.BOOLEAN, false, true, true, List.of(), null),
        new ImportFieldDefinition(
            "viewCount", "view_count", DataType.INTEGER, false, true, true, List.of(), null),
        new ImportFieldDefinition(
            "tags", "tags", DataType.STRING, false, true, true, List.of(), null),
        new ImportFieldDefinition(
            "status",
            "status",
            DataType.ENUM,
            false,
            true,
            true,
            java.util.Arrays.stream(NoteStatus.values()).map(Enum::name).toList(),
            NoteStatus.class),
        new ImportFieldDefinition(
            "dueDate", "due_date", DataType.DATE_TIME, false, true, true, List.of(), null),
        new ImportFieldDefinition(
            "description", "description", DataType.STRING, false, true, true, List.of(), null));
  }

  @Override
  public NoteImportRowDto toDto(Map<String, Object> mappedValues) {
    NoteImportRowDto dto = new NoteImportRowDto();
    dto.setTitle(MappedValueHelper.getString(mappedValues.get("title")));
    dto.setNote(MappedValueHelper.getString(mappedValues.get("note")));
    dto.setPriority(MappedValueHelper.getInteger(mappedValues.get("priority"), "priority"));
    dto.setAuthorEmail(MappedValueHelper.getString(mappedValues.get("authorEmail")));
    dto.setCategory(MappedValueHelper.getString(mappedValues.get("category")));
    dto.setEstimatedTime(
        MappedValueHelper.getBigDecimal(mappedValues.get("estimatedTime"), "estimatedTime"));
    dto.setIsCompleted(
        MappedValueHelper.getBoolean(mappedValues.get("isCompleted"), "isCompleted"));
    dto.setIsArchived(MappedValueHelper.getBoolean(mappedValues.get("isArchived"), "isArchived"));
    dto.setViewCount(MappedValueHelper.getInteger(mappedValues.get("viewCount"), "viewCount"));
    dto.setTags(MappedValueHelper.getString(mappedValues.get("tags")));
    dto.setStatus(
        MappedValueHelper.getEnum(mappedValues.get("status"), "status", NoteStatus.class));
    // TODO: Handle with ImportValueConverter for best practice in mapping layer
    dto.setDueDate(DateTimeImportConverter.toTimestamp(mappedValues.get("dueDate"), "dueDate"));
    dto.setDescription(MappedValueHelper.getString(mappedValues.get("description")));
    return dto;
  }

  @Override
  public void saveChunk(List<NoteImportRowDto> rows) {
    List<Note> notes =
        rows.stream()
            .map(
                row -> {
                  Note note = new Note();
                  note.setContent(row.getNote());
                  note.setPriority(row.getPriority());
                  note.setTitle(row.getTitle());
                  note.setAuthorEmail(row.getAuthorEmail());
                  note.setCategory(row.getCategory());
                  note.setEstimatedTime(row.getEstimatedTime());
                  note.setIsCompleted(row.getIsCompleted());
                  note.setIsArchived(row.getIsArchived());
                  note.setViewCount(row.getViewCount());
                  note.setTags(row.getTags());
                  note.setStatus(row.getStatus());
                  note.setDueDate(row.getDueDate());
                  note.setDescription(row.getDescription());
                  return note;
                })
            .toList();
    noteRepository.saveAll(notes);
  }
}
