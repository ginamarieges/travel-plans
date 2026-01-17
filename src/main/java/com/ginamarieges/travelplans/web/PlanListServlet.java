package com.ginamarieges.travelplans.web;

import com.ginamarieges.travelplans.domain.Plan;
import com.ginamarieges.travelplans.service.CompatibilityGroupingResult;
import com.ginamarieges.travelplans.service.PlanService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/plans")
public class PlanListServlet extends HttpServlet {

    private final PlanService planService = ApplicationContext.getPlanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String groupingParam = request.getParameter("grouping");
        boolean isGroupingEnabled = "true".equalsIgnoreCase(groupingParam);

        if (isGroupingEnabled) {
            CompatibilityGroupingResult groupingResult = planService.groupPlansByCompatibility();
            request.setAttribute("isGroupingEnabled", true);
            request.setAttribute("compatiblePlans", groupingResult.getCompatiblePlans());
            request.setAttribute("nonCompatiblePlans", groupingResult.getNonCompatiblePlans());
        } else {
            List<Plan> plans = planService.getAllPlans();
            request.setAttribute("isGroupingEnabled", false);
            request.setAttribute("plans", plans);
        }

        request.getRequestDispatcher("/WEB-INF/jsp/plans-list.jsp").forward(request, response);
    }
}
