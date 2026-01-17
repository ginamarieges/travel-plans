package com.ginamarieges.travelplans.web;

import com.ginamarieges.travelplans.repository.InMemoryPlanRepository;
import com.ginamarieges.travelplans.repository.PlanRepository;
import com.ginamarieges.travelplans.service.PlanService;
import com.ginamarieges.travelplans.service.PlanValidator;

public final class ApplicationContext {
    private static final PlanRepository PLAN_REPOSITORY = new InMemoryPlanRepository();
    private static final PlanValidator PLAN_VALIDATOR = new PlanValidator();
    private static final PlanService PLAN_SERVICE = new PlanService(PLAN_REPOSITORY, PLAN_VALIDATOR);

    private ApplicationContext() {
    }

    public static PlanService getPlanService() {
        return PLAN_SERVICE;
    }
}
