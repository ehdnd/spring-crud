package io.github.ehdnd.crud.web.error;

import org.springframework.validation.FieldError;

public record FieldErrorResponse(

    String field,
    String message

) {

  static FieldErrorResponse from(FieldError e) {
    return new FieldErrorResponse(e.getField(), e.getDefaultMessage());
  }
}
