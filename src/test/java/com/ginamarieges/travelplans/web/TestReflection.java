package com.ginamarieges.travelplans.web;

import java.lang.reflect.Field;

final class TestReflection {

  private TestReflection() {
  }

  static void setField(Object targetObject, String fieldName, Object value) {
    try {
      Field field = targetObject.getClass().getSuperclass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(targetObject, value);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }
}
