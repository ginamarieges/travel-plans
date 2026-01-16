package com.ginamarieges.travelplans.repository;

import java.util.List;
import java.util.Optional;

import com.ginamarieges.travelplans.domain.Plan;

public interface PlanRepository {
  
    Plan save(Plan plan);
    Optional<Plan> findById(Integer planId);
    List<Plan> findAll();
    boolean deleteById(Integer planId);
}
