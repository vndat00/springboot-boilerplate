package com.vndat00.springbootboilerplate.importexport.note;

import com.vndat00.springbootboilerplate.importexport.core.ImportRowValidator;
import com.vndat00.springbootboilerplate.importexport.core.ImportValidationMessage;
import com.vndat00.springbootboilerplate.importexport.core.Severity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class NoteBusinessRowValidator implements ImportRowValidator<NoteImportRowDto> {
  @Override
  public String entityType() {
    return "note";
  }

  @Override
  public List<ImportValidationMessage> validate(NoteImportRowDto row, int rowNumber) {
    List<ImportValidationMessage> messages = new ArrayList<>();

    if (row.getIsArchived() != null
        && row.getIsArchived()
        && row.getIsCompleted() != null
        && !row.getIsCompleted()) {
      messages.add(
          new ImportValidationMessage(
              rowNumber,
              "isArchived",
              Severity.WARNING,
              "ARCHIVED_NOT_COMPLETED",
              "Archived note is not marked as completed",
              String.valueOf(row.getIsArchived())));
    }

    if (row.getViewCount() != null && row.getViewCount() < 0) {
      messages.add(
          new ImportValidationMessage(
              rowNumber,
              "viewCount",
              Severity.ERROR,
              "NEGATIVE_VIEW_COUNT",
              "View count cannot be negative",
              String.valueOf(row.getViewCount())));
    }

    return messages;
  }
}
