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

}
