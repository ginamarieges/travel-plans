package com.ginamarieges.travelplans.service;

import com.ginamarieges.travelplans.domain.City;
import com.ginamarieges.travelplans.domain.Plan;
import com.ginamarieges.travelplans.domain.PlanType;

public class PlanValidator {
  public ValidationResult validate(Plan plan) {
    ValidationResult validationResult = new ValidationResult();

    if (plan == null) {
      validationResult.addFieldError("plan", "Plan must not be null.");
      return validationResult;
    }

    validateText(validationResult, "name", plan.getName());
    validatePlanType(validationResult, plan.getType());

    validateInteger(validationResult, "totalAdults", plan.getTotalAdults());
    validateKids(validationResult, plan.getType(), plan.getTotalKids());

    validateCity(validationResult, "originCity", plan.getOrigin());
    validateCity(validationResult, "destinationCity", plan.getDestination());

    // Optional: origin and destination should not be the same.
    // If you think they could be the same in your domain, remove this.
    if (plan.getOrigin() != null && plan.getDestination() != null) {
      String originName = plan.getOrigin().getName();
      String destinationName = plan.getDestination().getName();
      if (originName != null && destinationName != null && originName.trim().equalsIgnoreCase(destinationName.trim())) {
          validationResult.addFieldError("destinationCity", "Destination city must be different from origin city.");
      }
    }

    return validationResult;
}

private void validateText(ValidationResult validationResult, String fieldName, String textValue) {
  if (textValue == null || textValue.trim().isEmpty()) {
    validationResult.addFieldError(fieldName, "This field is required.");
  }
}

private void validatePlanType(ValidationResult validationResult, PlanType planType) {
  if (planType == null) {
    validationResult.addFieldError("planType", "Plan type is required.");
  }
}

private void validateInteger(ValidationResult validationResult, String fieldName, Integer numericValue) {
  if (numericValue == null) {
    validationResult.addFieldError(fieldName, "This field is required.");
    return;
  }
  if (numericValue < 0) {
    validationResult.addFieldError(fieldName, "Value must not be negative.");
  }
}

private void validateKids(ValidationResult validationResult, PlanType planType, Integer totalKids) {
  if (planType == null) {
    // If type is missing we cannot apply the rule reliably. The planType error is already added.
    return;
  }

  if (planType == PlanType.TRABAJO) {
    // Work plans must not have kids field filled in.
    if (totalKids != null) {
      validationResult.addFieldError("totalKids", "Kids can't go to work plans.");
    }
    return;
  }

  validateInteger(validationResult, "totalKids", totalKids);
}

private void validateCity(ValidationResult validationResult, String fieldName, City city) {
  if (city == null) {
    validationResult.addFieldError(fieldName, "This field is required.");
    return;
  }
  if (city.getName() == null || city.getName().trim().isEmpty()) {
    validationResult.addFieldError(fieldName, "City name is required.");
  }
}
}
