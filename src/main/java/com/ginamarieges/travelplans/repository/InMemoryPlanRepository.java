package com.ginamarieges.travelplans.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.ginamarieges.travelplans.domain.Plan;

public class InMemoryPlanRepository implements PlanRepository {
  
  private final AtomicInteger planIdSequence = new AtomicInteger(0);    //Creates a safe id
  private final Map<Integer, Plan> plansById = new LinkedHashMap<>();

  //This methods are synchronized to avoid inconsistences in a multi-thread environment
  @Override
  public synchronized Plan save(Plan plan) {
    if (plan == null) {
      throw new IllegalArgumentException("Plan must not be null");
    }

    LocalDateTime now = LocalDateTime.now();
    //If the plan id is null then create new id and set id and createdAt
    if (plan.getId() == null) {
      Integer newPlanId = planIdSequence.incrementAndGet();
      plan.setId(newPlanId);
      plan.setCreatedAt(now);
    } else {
      // If there is no id then it's an update
      // If we update, keep original createdAt if present
      Plan existingPlan = plansById.get(plan.getId());
      if (existingPlan != null && existingPlan.getCreatedAt() != null) {
          plan.setCreatedAt(existingPlan.getCreatedAt());
      } else if (plan.getCreatedAt() == null) {
          plan.setCreatedAt(now);
      }
    }

    plan.setUpdatedAt(now);

    plansById.put(plan.getId(), plan);
    return plan;
  }

  @Override
  public synchronized Optional<Plan> findById(Integer planId) {
    if (planId == null) {
        return Optional.empty();
    }
    return Optional.ofNullable(plansById.get(planId));
  }
  
  // Return a defensive copy of the stored plans wrapped in an unmodifiable list
  // so callers cannot change the internal repository state.
  @Override
  public synchronized List<Plan> findAll() {
      return Collections.unmodifiableList(
          plansById.values().stream()
              .map(Plan::new)  // ← Lo esencial: copias de Plan
              .collect(Collectors.toList())
      );
  }

  @Override
  public synchronized boolean deleteById(Integer planId) {
    if (planId == null) {
        return false;
    }
    return plansById.remove(planId) != null;
  }
}
