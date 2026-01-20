package com.ginamarieges.travelplans.service;

import com.ginamarieges.travelplans.domain.City;
import com.ginamarieges.travelplans.domain.Plan;
import com.ginamarieges.travelplans.domain.PlanType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlanValidatorTest {

  private final PlanValidator planValidator = new PlanValidator();

  @Test
  void validate_shouldFail_whenNameIsMissing() {
    Plan plan = new Plan();
    plan.setType(PlanType.NORMAL);
    plan.setTotalAdults(2);

    ValidationResult result = planValidator.validate(plan);

    assertFalse(result.isValid());
    assertTrue(result.getErrorsByFieldName().containsKey("name"));
  }

  @Test
  void validate_shouldFail_whenTypeIsMissing() {
    Plan plan = new Plan();
    plan.setName("Trip");
    plan.setTotalAdults(2);

    ValidationResult result = planValidator.validate(plan);

    assertFalse(result.isValid());
    assertTrue(result.getErrorsByFieldName().containsKey("planType"));
  }

  @Test
  void validate_shouldFail_whenAdultsIsMissing() {
    Plan plan = new Plan();
    plan.setName("Trip");
    plan.setType(PlanType.NORMAL);

    ValidationResult result = planValidator.validate(plan);

    assertFalse(result.isValid());
    assertTrue(result.getErrorsByFieldName().containsKey("totalAdults"));
  }

  @Test
  void validate_shouldFail_whenWorkPlanHasKids() {
    Plan plan = new Plan();
    plan.setName("Work trip");
    plan.setType(PlanType.TRABAJO);
    plan.setTotalAdults(1);
    plan.setTotalKids(1);

    ValidationResult result = planValidator.validate(plan);

    assertFalse(result.isValid());
    assertTrue(result.getErrorsByFieldName().containsKey("totalKids"));
  }

  @Test
  void validate_shouldPass_whenWorkPlanHasNoKids() {
    Plan plan = new Plan();
    plan.setName("Work trip");
    plan.setType(PlanType.TRABAJO);
    plan.setTotalAdults(1);
    plan.setTotalKids(null);
    plan.setOrigin(new City(2, "Madrid"));
    plan.setDestination(new City(1, "Barcelona"));

    ValidationResult result = planValidator.validate(plan);

    assertTrue(result.isValid());
  }

  @Test
  void validate_shouldFail_whenOriginOrDestinationMissing() {
    Plan plan = new Plan();
    plan.setName("Trip");
    plan.setType(PlanType.NORMAL);
    plan.setTotalAdults(2);
    plan.setOrigin(null);
    plan.setDestination(new City(1, "Barcelona"));

    ValidationResult result = planValidator.validate(plan);

    assertFalse(result.isValid());
    assertTrue(result.getErrorsByFieldName().containsKey("originCity"));
  }
}
