package com.ginamarieges.travelplans.service;

import com.ginamarieges.travelplans.domain.City;
import com.ginamarieges.travelplans.domain.Plan;
import com.ginamarieges.travelplans.domain.PlanType;
import com.ginamarieges.travelplans.repository.CityRepository;
import com.ginamarieges.travelplans.repository.PlanRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service layer for travel plan business logic.
 * Handles plan CRUD operations, validation, and compatibility grouping.
 */
public class PlanService {

  private final PlanRepository planRepository;
  private final PlanValidator planValidator;
  private final CityRepository cityRepository;

  /**
   * Constructor with dependency injection.
   * All dependencies are required - fails fast if any are null.
   */
  public PlanService(PlanRepository planRepository, PlanValidator planValidator, CityRepository cityRepository) {
    if (planRepository == null) {
      throw new IllegalArgumentException("PlanRepository must not be null");
    }
    if (planValidator == null) {
      throw new IllegalArgumentException("PlanValidator must not be null");
    }
    if (cityRepository == null) {
      throw new IllegalArgumentException("CityRepository must not be null");
    }
    this.planRepository = planRepository;
    this.planValidator = planValidator;
    this.cityRepository = cityRepository;
  }

  /**
   * Creates a new plan with automatic city resolution.
   * Cities are looked up or created on-the-fly to avoid manual city management.
   */
  public ValidationResult createPlan(Plan plan, String originCityName, String destinationCityName) {
    
    // Resolve city names to City objects (creates cities if they don't exist)
    plan.setOrigin(resolveCity(originCityName));
    plan.setDestination(resolveCity(destinationCityName));

    ValidationResult validationResult = planValidator.validate(plan);
    if (!validationResult.isValid()) {
      return validationResult;
    }

    // Clear any ID to ensure the repository generates a new one
    plan.setId(null);

    planRepository.save(plan);
    return validationResult;
  }

  /**
   * Updates an existing plan.
   * Validates that the plan exists before attempting update.
   */
  public ValidationResult updatePlan(Plan plan, String originCityName, String destinationCityName) {
    if (plan == null) {
      throw new IllegalArgumentException("Plan must not be null");
    }

    // Resolve cities (creates if needed)
    plan.setOrigin(resolveCity(originCityName));
    plan.setDestination(resolveCity(destinationCityName));

    ValidationResult validationResult = planValidator.validate(plan);
    if (!validationResult.isValid()) {
      return validationResult;
    }

    // Ensure ID is present for update operation
    if (plan.getId() == null) {
      validationResult.addFieldError("id", "Plan id is required for update.");
      return validationResult;
    }

    // Verify plan exists before updating
    Optional<Plan> existingPlan = planRepository.findById(plan.getId());
    if (!existingPlan.isPresent()) {
      validationResult.addFieldError("id", "Plan not found.");
      return validationResult;
    }

    planRepository.save(plan);
    return validationResult;
  }

  public boolean deletePlan(Integer planId) {
    return planRepository.deleteById(planId);
  }

  public Optional<Plan> getPlanById(Integer planId) {
    return planRepository.findById(planId);
  }

  public List<Plan> getAllPlans() {
    return planRepository.findAll();
  }

  /**
   * Groups plans by compatibility criteria (type, origin, destination).
   * Plans are compatible if they share the same type and route - useful for
   * finding potential travel companions or combining bookings.
   */
  public CompatibilityGroupingResult groupPlansByCompatibility() {
    List<Plan> allPlans = planRepository.findAll();

    // Group plans by compatibility key - LinkedHashMap preserves insertion order
    Map<CompatibilityKey, List<Plan>> plansByKey = new LinkedHashMap<>();

    for (Plan plan : allPlans) {
      // Create compatibility key based on type and route
      CompatibilityKey key = CompatibilityKey.fromPlan(plan);
      // Group plans with matching keys
      plansByKey.computeIfAbsent(key, ignoredKey -> new ArrayList<>()).add(plan);
    }

    List<Plan> compatiblePlans = new ArrayList<>();
    List<Plan> otherPlans = new ArrayList<>();

    // Separate plans: compatible (2+ in group) vs standalone (only 1 in group)
    for (List<Plan> group : plansByKey.values()) {
      if (group.size() >= 2) {
        compatiblePlans.addAll(group);
      } else {
        otherPlans.addAll(group);
      }
    }

    return new CompatibilityGroupingResult(compatiblePlans, otherPlans);
  } 

  /**
   * Immutable key for grouping plans by compatibility.
   * Plans are compatible if they have the same type, origin, and destination.
   * Uses normalized text (trimmed, uppercase) for case-insensitive matching.
   */
  private static final class CompatibilityKey {

    private final PlanType planType;
    private final String originCity;
    private final String destinationCity;

    private CompatibilityKey(PlanType planType, String originCityNameNormalized, String destinationCityNameNormalized) {
      this.planType = planType;
      this.originCity = originCityNameNormalized;
      this.destinationCity = destinationCityNameNormalized;
    }

    static CompatibilityKey fromPlan(Plan plan) {
      City originCity = plan == null ? null : plan.getOrigin();
      City destinationCity = plan == null ? null : plan.getDestination();

      String originName = originCity == null ? null : originCity.getName();
      String destinationName = destinationCity == null ? null : destinationCity.getName();

      return new CompatibilityKey(
        plan == null ? null : plan.getType(),
        normalizeText(originName),
        normalizeText(destinationName)
      );
    }

    // Normalize for case-insensitive comparison (e.g., "Barcelona" == "BARCELONA")
    private static String normalizeText(String rawText) {
      return rawText == null ? "" : rawText.trim().toUpperCase();
    }

    @Override
    public boolean equals(Object otherObject) {
      if (this == otherObject) {
        return true;
      }
      if (!(otherObject instanceof CompatibilityKey)) {
        return false;
      }
      CompatibilityKey otherKey = (CompatibilityKey) otherObject;
      return planType == otherKey.planType
        && originCity.equals(otherKey.originCity)
        && destinationCity.equals(otherKey.destinationCity);
    }

    @Override
    public int hashCode() {
      int result = planType == null ? 0 : planType.hashCode();
      result = 31 * result + originCity.hashCode();
      result = 31 * result + destinationCity.hashCode();
      return result;
    }
  }

  /**
   * Resolves a city name to a City object.
   * Looks up existing city or creates a new one - simplifies city management
   * by allowing users to type city names without pre-registration.
   */
  private City resolveCity(String cityNameRaw) {
    if (cityNameRaw == null || cityNameRaw.trim().isEmpty()) {
      return null;
    }

    String cityName = cityNameRaw.trim();

    return cityRepository.findByName(cityName)
        .orElseGet(() -> cityRepository.save(new City(null, cityName)));
  }

}
