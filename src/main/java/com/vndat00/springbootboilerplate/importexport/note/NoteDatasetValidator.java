package com.vndat00.springbootboilerplate.importexport.note;

import com.vndat00.springbootboilerplate.importexport.core.ImportDatasetValidator;
import com.vndat00.springbootboilerplate.importexport.core.ImportValidationMessage;
import com.vndat00.springbootboilerplate.importexport.core.Severity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NoteDatasetValidator implements ImportDatasetValidator<NoteImportRowDto> {
  @Override
  public String entityType() {
    return "note";
  }

  @Override
  public List<ImportValidationMessage> validate(List<NoteImportRowDto> rows) {
    List<ImportValidationMessage> messages = new ArrayList<>();
    Map<String, Integer> seen = new HashMap<>();

    for (int i = 0; i < rows.size(); i++) {
      NoteImportRowDto row = rows.get(i);
      int rowNumber = i + 2;
      String key =
          (row.getTitle() == null ? "" : row.getTitle().trim().toLowerCase())
              + "|"
              + (row.getAuthorEmail() == null ? "" : row.getAuthorEmail().trim().toLowerCase());
      if (seen.containsKey(key)) {
        messages.add(
            new ImportValidationMessage(
                rowNumber,
                "title",
                Severity.WARNING,
                "DUPLICATE_IN_FILE",
                "Duplicate title and author email combination found in same file",
                row.getTitle()));
      } else {
        seen.put(key, rowNumber);
      }
    }

    return messages;
  }
}
