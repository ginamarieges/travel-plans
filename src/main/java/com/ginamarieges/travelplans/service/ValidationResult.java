package com.ginamarieges.travelplans.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValidationResult {
  private final Map<String, String> errorsByFieldName = new LinkedHashMap<>();

  public void addFieldError(String fieldName, String errorMessage) {
    if (fieldName == null || fieldName.trim().isEmpty()) {
      throw new IllegalArgumentException("FieldName must not be blank");
    }
    if (errorMessage == null || errorMessage.trim().isEmpty()) {
      throw new IllegalArgumentException("ErrorMessage must not be blank");
    }
    // If a field already has an error, keep the first one.
    errorsByFieldName.putIfAbsent(fieldName, errorMessage);
  }

  public boolean isValid() {
    return errorsByFieldName.isEmpty();
  }

  public Map<String, String> getErrorsByFieldName() {
    return Collections.unmodifiableMap(errorsByFieldName);
  }
}
