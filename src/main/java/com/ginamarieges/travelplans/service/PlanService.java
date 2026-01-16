package com.ginamarieges.travelplans.service;

import com.ginamarieges.travelplans.domain.Plan;
import com.ginamarieges.travelplans.repository.PlanRepository;

import java.util.List;
import java.util.Optional;

public class PlanService {

  private final PlanRepository planRepository;
  private final PlanValidator planValidator;

  public PlanService(PlanRepository planRepository, PlanValidator planValidator) {
    if (planRepository == null) {
      throw new IllegalArgumentException("PlanRepository must not be null");
    }
    if (planValidator == null) {
      throw new IllegalArgumentException("PlanValidator must not be null");
    }
    this.planRepository = planRepository;
    this.planValidator = planValidator;
  }

  public ValidationResult createPlan(Plan plan) {
    ValidationResult validationResult = planValidator.validate(plan);
    if (!validationResult.isValid()) {
      return validationResult;
    }
    // When create a plan the plan id should be null
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
}
