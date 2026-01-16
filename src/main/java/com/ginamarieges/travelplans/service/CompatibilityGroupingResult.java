package com.ginamarieges.travelplans.service;

import java.util.Collections;
import java.util.List;

import com.ginamarieges.travelplans.domain.Plan;

public class CompatibilityGroupingResult {
  private final List<Plan> compatiblePlans;
  private final List<Plan> nonCompatiblePlans;

  public CompatibilityGroupingResult(List<Plan> compatiblePlans, List<Plan> nonCompatiblePlans) {
    this.compatiblePlans = compatiblePlans == null ? Collections.emptyList() : Collections.unmodifiableList(compatiblePlans);
    this.nonCompatiblePlans = nonCompatiblePlans == null ? Collections.emptyList() : Collections.unmodifiableList(nonCompatiblePlans);
  }

  public List<Plan> getCompatiblePlans() {
    return compatiblePlans;
  }

  public List<Plan> getNonCompatiblePlans() {
    return nonCompatiblePlans;
  }
}
