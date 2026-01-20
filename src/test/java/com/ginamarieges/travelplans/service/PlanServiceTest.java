package com.ginamarieges.travelplans.service;

import com.ginamarieges.travelplans.domain.City;
import com.ginamarieges.travelplans.domain.Plan;
import com.ginamarieges.travelplans.domain.PlanType;
import com.ginamarieges.travelplans.repository.InMemoryCityRepository;
import com.ginamarieges.travelplans.repository.InMemoryPlanRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlanServiceTest {

  @Test
  void createPlan_shouldCreatePlanAndCities() {
    InMemoryPlanRepository planRepository = new InMemoryPlanRepository();
    InMemoryCityRepository cityRepository = new InMemoryCityRepository();
    PlanValidator validator = new PlanValidator();

    PlanService planService = new PlanService(
        planRepository,
        validator,
        cityRepository
    );

    Plan plan = new Plan();
    plan.setName("Trip");
    plan.setType(PlanType.NORMAL);
    plan.setTotalAdults(2);
    plan.setTotalKids(0);

    ValidationResult result = planService.createPlan(
        plan,
        "Madrid",
        "Barcelona"
    );

    assertTrue(result.isValid());

    List<Plan> plans = planService.getAllPlans();
    assertEquals(1, plans.size());

    Plan savedPlan = plans.get(0);
    assertNotNull(savedPlan.getId());
    assertEquals("Madrid", savedPlan.getOrigin().getName());
    assertEquals("Barcelona", savedPlan.getDestination().getName());
  }

  @Test
  void createPlan_shouldReuseExistingCity() {
    InMemoryPlanRepository planRepository = new InMemoryPlanRepository();
    InMemoryCityRepository cityRepository = new InMemoryCityRepository();
    PlanService planService = new PlanService(
        planRepository,
        new PlanValidator(),
        cityRepository
    );

    Plan firstPlan = new Plan();
    firstPlan.setName("First");
    firstPlan.setType(PlanType.NORMAL);
    firstPlan.setTotalAdults(1);

    planService.createPlan(firstPlan, "Madrid", "Barcelona");

    Plan secondPlan = new Plan();
    secondPlan.setName("Second");
    secondPlan.setType(PlanType.NORMAL);
    secondPlan.setTotalAdults(1);

    planService.createPlan(secondPlan, "  madrid ", "BARCELONA");

    City origin1 = firstPlan.getOrigin();
    City origin2 = secondPlan.getOrigin();

    assertEquals(origin1.getId(), origin2.getId());
  }

  @Test
  void createPlan_shouldNotSave_whenValidationFails() {
    PlanService planService = new PlanService(
        new InMemoryPlanRepository(),
        new PlanValidator(),
        new InMemoryCityRepository()
    );

    Plan invalidPlan = new Plan();
    invalidPlan.setType(PlanType.NORMAL); // name missing

    ValidationResult result = planService.createPlan(
        invalidPlan,
        "Madrid",
        "Barcelona"
    );

    assertFalse(result.isValid());
    assertTrue(planService.getAllPlans().isEmpty());
  }
  @Test
  void groupPlansByCompatibility_shouldPutPlansInCompatible_whenTheyShareSameKey() {
    InMemoryPlanRepository planRepository = new InMemoryPlanRepository();
    InMemoryCityRepository cityRepository = new InMemoryCityRepository();
    PlanValidator planValidator = new PlanValidator();
    PlanService planService = new PlanService(planRepository, planValidator, cityRepository);

    Plan planOne = new Plan();
    planOne.setName("Plan One");
    planOne.setType(PlanType.NORMAL);
    planOne.setTotalAdults(2);
    planOne.setTotalKids(0);
    assertTrue(planService.createPlan(planOne, "Madrid", "Barcelona").isValid());

    Plan planTwo = new Plan();
    planTwo.setName("Plan Two");
    planTwo.setType(PlanType.NORMAL);
    planTwo.setTotalAdults(1);
    planTwo.setTotalKids(1);
    assertTrue(planService.createPlan(planTwo, " MADRID ", "BARCELONA").isValid());

    CompatibilityGroupingResult result = planService.groupPlansByCompatibility();

    assertEquals(2, result.getCompatiblePlans().size());
    assertEquals(0, result.getOtherPlans().size());
  }

  @Test
  void groupPlansByCompatibility_shouldPutSinglePlansInOtherPlans() {
    InMemoryPlanRepository planRepository = new InMemoryPlanRepository();
    InMemoryCityRepository cityRepository = new InMemoryCityRepository();
    PlanValidator planValidator = new PlanValidator();
    PlanService planService = new PlanService(planRepository, planValidator, cityRepository);

    Plan planOne = new Plan();
    planOne.setName("Solo");
    planOne.setType(PlanType.NORMAL);
    planOne.setTotalAdults(1);
    planOne.setTotalKids(0);
    assertTrue(planService.createPlan(planOne, "Madrid", "Barcelona").isValid());

    CompatibilityGroupingResult result = planService.groupPlansByCompatibility();

    assertEquals(0, result.getCompatiblePlans().size());
    assertEquals(1, result.getOtherPlans().size());
  }

  @Test
  void groupPlansByCompatibility_shouldSeparateGroups_byTypeOrCities() {
    InMemoryPlanRepository planRepository = new InMemoryPlanRepository();
    InMemoryCityRepository cityRepository = new InMemoryCityRepository();
    PlanValidator planValidator = new PlanValidator();
    PlanService planService = new PlanService(planRepository, planValidator, cityRepository);

    // Group A (NORMAL, Madrid -> Barcelona) : 2 plans => compatible
    Plan planOne = new Plan();
    planOne.setName("A1");
    planOne.setType(PlanType.NORMAL);
    planOne.setTotalAdults(2);
    planOne.setTotalKids(0);
    assertTrue(planService.createPlan(planOne, "Madrid", "Barcelona").isValid());

    Plan planTwo = new Plan();
    planTwo.setName("A2");
    planTwo.setType(PlanType.NORMAL);
    planTwo.setTotalAdults(3);
    planTwo.setTotalKids(0);
    assertTrue(planService.createPlan(planTwo, "MADRID", "BARCELONA").isValid());

    // Group B (TRABAJO, Madrid -> Barcelona) : 1 plan => otherPlans (different type)
    Plan planThree = new Plan();
    planThree.setName("B1");
    planThree.setType(PlanType.TRABAJO);
    planThree.setTotalAdults(1);
    planThree.setTotalKids(null);
    assertTrue(planService.createPlan(planThree, "Madrid", "Barcelona").isValid());

    // Group C (NORMAL, Madrid -> Valencia) : 1 plan => otherPlans (different destination)
    Plan planFour = new Plan();
    planFour.setName("C1");
    planFour.setType(PlanType.NORMAL);
    planFour.setTotalAdults(1);
    planFour.setTotalKids(0);
    assertTrue(planService.createPlan(planFour, "Madrid", "Valencia").isValid());

    CompatibilityGroupingResult result = planService.groupPlansByCompatibility();

    assertEquals(2, result.getCompatiblePlans().size());
    assertEquals(2, result.getOtherPlans().size());
  }

  @Test
  void updatePlan_shouldUpdatePlan_whenValidAndExists_andResolveCities() {
    InMemoryPlanRepository planRepository = new InMemoryPlanRepository();
    InMemoryCityRepository cityRepository = new InMemoryCityRepository();
    PlanValidator validator = new PlanValidator();
    PlanService planService = new PlanService(planRepository, validator, cityRepository);

    // Create initial plan
    Plan createdPlan = new Plan();
    createdPlan.setName("Original");
    createdPlan.setType(PlanType.NORMAL);
    createdPlan.setTotalAdults(2);
    createdPlan.setTotalKids(0);

    ValidationResult createResult = planService.createPlan(createdPlan, "Madrid", "Barcelona");
    assertTrue(createResult.isValid());

    Integer existingPlanId = planService.getAllPlans().get(0).getId();
    assertNotNull(existingPlanId);

    // Update same plan
    Plan updatedPlan = new Plan();
    updatedPlan.setId(existingPlanId);
    updatedPlan.setName("Updated");
    updatedPlan.setType(PlanType.NORMAL);
    updatedPlan.setTotalAdults(3);
    updatedPlan.setTotalKids(1);

    ValidationResult updateResult = planService.updatePlan(updatedPlan, "Valencia", "Sevilla");
    assertTrue(updateResult.isValid());

    Plan savedPlan = planService.getPlanById(existingPlanId)
    .orElseThrow(() -> new AssertionError("Expected plan to exist"));
    assertEquals("Updated", savedPlan.getName());
    assertEquals(3, savedPlan.getTotalAdults());
    assertEquals(1, savedPlan.getTotalKids());

    assertNotNull(savedPlan.getOrigin());
    assertNotNull(savedPlan.getDestination());
    assertEquals("Valencia", savedPlan.getOrigin().getName());
    assertEquals("Sevilla", savedPlan.getDestination().getName());
  }

  @Test
  void updatePlan_shouldFail_whenPlanIsNull() {
    PlanService planService = new PlanService(
        new InMemoryPlanRepository(),
        new PlanValidator(),
        new InMemoryCityRepository()
    );

    assertThrows(IllegalArgumentException.class, () ->
        planService.updatePlan(null, "Madrid", "Barcelona")
    );
  }

  @Test
  void updatePlan_shouldFail_whenIdIsMissing() {
    PlanService planService = new PlanService(
        new InMemoryPlanRepository(),
        new PlanValidator(),
        new InMemoryCityRepository()
    );

    Plan plan = new Plan();
    plan.setName("No id");
    plan.setType(PlanType.NORMAL);
    plan.setTotalAdults(1);
    plan.setTotalKids(0);

    ValidationResult result = planService.updatePlan(plan, "Madrid", "Barcelona");

    assertFalse(result.isValid());
    assertTrue(result.getErrorsByFieldName().containsKey("id"));
  }

  @Test
  void updatePlan_shouldFail_whenPlanDoesNotExist() {
    PlanService planService = new PlanService(
        new InMemoryPlanRepository(),
        new PlanValidator(),
        new InMemoryCityRepository()
    );

    Plan plan = new Plan();
    plan.setId(999);
    plan.setName("Missing");
    plan.setType(PlanType.NORMAL);
    plan.setTotalAdults(1);
    plan.setTotalKids(0);

    ValidationResult result = planService.updatePlan(plan, "Madrid", "Barcelona");

    assertFalse(result.isValid());
    assertTrue(result.getErrorsByFieldName().containsKey("id"));
  }

  @Test
  void updatePlan_shouldNotOverwriteExisting_whenValidationFails() {
    InMemoryPlanRepository planRepository = new InMemoryPlanRepository();
    InMemoryCityRepository cityRepository = new InMemoryCityRepository();
    PlanService planService = new PlanService(planRepository, new PlanValidator(), cityRepository);

    // Create initial valid plan
    Plan initialPlan = new Plan();
    initialPlan.setName("Initial");
    initialPlan.setType(PlanType.NORMAL);
    initialPlan.setTotalAdults(1);
    initialPlan.setTotalKids(0);

    assertTrue(planService.createPlan(initialPlan, "Madrid", "Barcelona").isValid());
    Integer existingPlanId = planService.getAllPlans().get(0).getId();

    // Invalid update: missing name
    Plan invalidUpdate = new Plan();
    invalidUpdate.setId(existingPlanId);
    invalidUpdate.setName(""); // invalid
    invalidUpdate.setType(PlanType.NORMAL);
    invalidUpdate.setTotalAdults(2);
    invalidUpdate.setTotalKids(0);

    ValidationResult result = planService.updatePlan(invalidUpdate, "Valencia", "Sevilla");
    assertFalse(result.isValid());

    // Ensure plan remains unchanged
    Plan savedPlan = planService.getPlanById(existingPlanId)
    .orElseThrow(() -> new AssertionError("Expected plan to exist"));
    assertEquals("Initial", savedPlan.getName());
    assertEquals("Madrid", savedPlan.getOrigin().getName());
    assertEquals("Barcelona", savedPlan.getDestination().getName());
  }

  @Test
  void deletePlan_shouldReturnTrue_whenPlanExists() {
    PlanService planService = new PlanService(
        new InMemoryPlanRepository(),
        new PlanValidator(),
        new InMemoryCityRepository()
    );

    Plan plan = new Plan();
    plan.setName("To delete");
    plan.setType(PlanType.NORMAL);
    plan.setTotalAdults(1);
    plan.setTotalKids(0);

    assertTrue(planService.createPlan(plan, "Madrid", "Barcelona").isValid());
    Integer planId = planService.getAllPlans().get(0).getId();

    boolean deleted = planService.deletePlan(planId);

    assertTrue(deleted);
    assertTrue(planService.getAllPlans().isEmpty());
    assertFalse(planService.getPlanById(planId).isPresent());
  }

  @Test
  void deletePlan_shouldReturnFalse_whenPlanDoesNotExist() {
    PlanService planService = new PlanService(
        new InMemoryPlanRepository(),
        new PlanValidator(),
        new InMemoryCityRepository()
    );

    boolean deleted = planService.deletePlan(999);

    assertFalse(deleted);
  }
}
