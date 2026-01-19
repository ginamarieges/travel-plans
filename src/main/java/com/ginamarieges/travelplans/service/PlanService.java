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

public class PlanService {

  private final PlanRepository planRepository;
  private final PlanValidator planValidator;
  private final CityRepository cityRepository;

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

  public ValidationResult createPlan(Plan plan, String originCityName, String destinationCityName) {
    
    plan.setOrigin(resolveCity(originCityName));
    plan.setDestination(resolveCity(destinationCityName));

    ValidationResult validationResult = planValidator.validate(plan);
    if (!validationResult.isValid()) {
      return validationResult;
    }

    // On create, we always let the repository assign the ID.
    plan.setId(null);

    planRepository.save(plan);
    return validationResult;
  }

  public ValidationResult updatePlan(Plan plan) {
    ValidationResult validationResult = planValidator.validate(plan);
    if (!validationResult.isValid()) {
      return validationResult;
    }

    if (plan.getId() == null) {
      validationResult.addFieldError("id", "Plan id is required for update.");
      return validationResult;
    }

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

  public CompatibilityGroupingResult groupPlansByCompatibility() {
    List<Plan> allPlans = planRepository.findAll();

    Map<CompatibilityKey, List<Plan>> plansByKey = new LinkedHashMap<>();

    for (Plan plan : allPlans) {
      //Create Compatibility keys for each plan
      CompatibilityKey key = CompatibilityKey.fromPlan(plan);
      //Add list of plans for each key
      plansByKey.computeIfAbsent(key, ignoredKey -> new ArrayList<>()).add(plan);
    }

    List<Plan> compatiblePlans = new ArrayList<>();
    List<Plan> otherPlans = new ArrayList<>();

    for (List<Plan> group : plansByKey.values()) {
      if (group.size() >= 2) {
        compatiblePlans.addAll(group);
      } else {
        otherPlans.addAll(group);
      }
    }

    return new CompatibilityGroupingResult(compatiblePlans, otherPlans);
  } 

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
  private City resolveCity(String cityNameRaw) {
    if (cityNameRaw == null || cityNameRaw.trim().isEmpty()) {
      return null;
    }

    String cityName = cityNameRaw.trim();

    return cityRepository.findByName(cityName)
        .orElseGet(() -> cityRepository.save(new City(null, cityName)));
  }

}
