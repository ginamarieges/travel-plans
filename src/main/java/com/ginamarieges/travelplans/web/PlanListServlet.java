package com.ginamarieges.travelplans.web;

import com.ginamarieges.travelplans.domain.Plan;
import com.ginamarieges.travelplans.domain.PlanType;
import com.ginamarieges.travelplans.service.CompatibilityGroupingResult;
import com.ginamarieges.travelplans.service.PlanService;
import com.ginamarieges.travelplans.service.ValidationResult;

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
            request.setAttribute("otherPlans", groupingResult.getOtherPlans());
        } else {
            List<Plan> plans = planService.getAllPlans();
            request.setAttribute("isGroupingEnabled", false);
            request.setAttribute("plans", plans);
        }

        request.getRequestDispatcher("/WEB-INF/jsp/plans-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String typeRaw = request.getParameter("type");
        String totalAdultsRaw = request.getParameter("totalAdults");
        String totalKidsRaw = request.getParameter("totalKids");
        String originName = request.getParameter("origin");
        String destinationName = request.getParameter("destination");

        Plan plan = new Plan();
        plan.setName(name);
        plan.setType(parsePlanType(typeRaw));
        plan.setTotalAdults(parseIntegerOrNull(totalAdultsRaw));
        plan.setTotalKids(parseIntegerOrNull(totalKidsRaw));

        ValidationResult validationResult = planService.createPlan(plan, originName, destinationName);

        if (!validationResult.isValid()) {
        request.setAttribute("errorsByFieldName", validationResult.getErrorsByFieldName());

        // Refill values so the user doesn't lose inputs
        request.setAttribute("nameValue", safeString(name));
        request.setAttribute("typeValue", safeString(typeRaw));
        request.setAttribute("totalAdultsValue", safeString(totalAdultsRaw));
        request.setAttribute("totalKidsValue", safeString(totalKidsRaw));
        request.setAttribute("originValue", safeString(originName));
        request.setAttribute("destinationValue", safeString(destinationName));

        request.getRequestDispatcher("/WEB-INF/jsp/plan-form.jsp").forward(request, response);
        return;
        }

        response.sendRedirect(request.getContextPath() + "/plans");
    }

    private static PlanType parsePlanType(String rawType) {
        if (rawType == null || rawType.trim().isEmpty()) {
            return null;
        }
        try {
            return PlanType.valueOf(rawType.trim());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static Integer parseIntegerOrNull(String rawNumber) {
        if (rawNumber == null) {
            return null;
        }
        String trimmed = rawNumber.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(trimmed);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String safeString(String value) {
        return value == null ? "" : value;
    }
}
