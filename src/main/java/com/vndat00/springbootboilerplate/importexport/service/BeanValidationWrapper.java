package com.vndat00.springbootboilerplate.importexport.service;

import com.vndat00.springbootboilerplate.importexport.core.ImportValidationMessage;
import com.vndat00.springbootboilerplate.importexport.core.Severity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BeanValidationWrapper {
  private final Validator validator;

  public <T> List<ImportValidationMessage> validate(T dto, int rowNumber) {
    return validator.validate(dto).stream()
        .map(this::toMessage)
        .map(
            msg ->
                new ImportValidationMessage(
                    rowNumber,
                    msg.field(),
                    msg.severity(),
                    msg.code(),
                    msg.message(),
                    msg.rejectedValue()))
        .toList();
  }

  private ImportValidationMessage toMessage(ConstraintViolation<?> violation) {
    String field = violation.getPropertyPath().toString();
    String code =
        violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
    Object value = violation.getInvalidValue();
    return new ImportValidationMessage(
        0,
        field,
        Severity.ERROR,
        code,
        violation.getMessage(),
        value == null ? null : value.toString());
  }
}
