package com.ginamarieges.travelplans.service;

import java.util.Collections;
import java.util.List;

import com.ginamarieges.travelplans.domain.Plan;

public class CompatibilityGroupingResult {
  private final List<Plan> compatiblePlans;
  private final List<Plan> otherPlans;

  public CompatibilityGroupingResult(List<Plan> compatiblePlans, List<Plan> otherPlans) {
    this.compatiblePlans = compatiblePlans == null ? Collections.emptyList() : Collections.unmodifiableList(compatiblePlans);
    this.otherPlans = otherPlans == null ? Collections.emptyList() : Collections.unmodifiableList(otherPlans);
  }

  public List<Plan> getCompatiblePlans() {
    return compatiblePlans;
  }

  public List<Plan> getOtherPlans() {
    return otherPlans;
  }
}
