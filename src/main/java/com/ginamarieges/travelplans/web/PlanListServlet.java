package com.ginamarieges.travelplans.web;

import com.ginamarieges.travelplans.domain.Plan;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/plans")
public class PlanListServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(PlanListServlet.class.getName());

    private final PlanService planService = ApplicationContext.getPlanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOG.info("Processing GET request to /plans");

        String groupingParam = request.getParameter("grouping");
        boolean isGroupingEnabled = "true".equalsIgnoreCase(groupingParam);

        if (isGroupingEnabled) {
            LOG.info("Grouping plans by compatibility");
            CompatibilityGroupingResult groupingResult = planService.groupPlansByCompatibility();
            request.setAttribute("isGroupingEnabled", true);
            request.setAttribute("compatiblePlans", groupingResult.getCompatiblePlans());
            request.setAttribute("otherPlans", groupingResult.getOtherPlans());
            LOG.info("Found " + groupingResult.getCompatiblePlans().size() + 
                    " compatible plans and " + groupingResult.getOtherPlans().size() + " other plans");
        } else {
            List<Plan> plans = planService.getAllPlans();
            request.setAttribute("isGroupingEnabled", false);
            request.setAttribute("plans", plans);
            LOG.info("Retrieved " + plans.size() + " plans");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/plans-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOG.info("Processing POST request to /plans");
        
        String action = request.getParameter("action");
        LOG.fine("Action parameter: " + action);
        
        if ("delete".equals(action)) {
            handleDelete(request, response);
            return;
        }
        
        handleCreate(request, response);
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        LOG.info("Handling delete action");
        String planId = request.getParameter("id");
        
        if (planId != null && !planId.isEmpty()) {
            try {
                Integer id = Integer.parseInt(planId);
                planService.deletePlan(id);
                LOG.info("Plan deleted successfully with ID: " + id);
            } catch (NumberFormatException e) {
                LOG.log(Level.WARNING, "Invalid plan ID format: " + planId, e);
            }
        } else {
            LOG.warning("Delete request received with empty or null plan ID");
        }
        
        response.sendRedirect(request.getContextPath() + "/plans");
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOG.info("Handling create action");
        
        String name = request.getParameter("name");
        String typeRaw = request.getParameter("type");
        String totalAdultsRaw = request.getParameter("totalAdults");
        String totalKidsRaw = request.getParameter("totalKids");
        String originName = request.getParameter("origin");
        String destinationName = request.getParameter("destination");

        LOG.fine("Creating plan with name: " + name);

        Plan plan = new Plan();
        plan.setName(name);
        plan.setType(RequestParsers.parsePlanType(typeRaw));
        plan.setTotalAdults(RequestParsers.parseIntegerOrNull(totalAdultsRaw));
        plan.setTotalKids(RequestParsers.parseIntegerOrNull(totalKidsRaw));

        ValidationResult validationResult = planService.createPlan(plan, originName, destinationName);

        if (!validationResult.isValid()) {
            LOG.warning("Plan validation failed for: " + name + 
                       ". Errors: " + validationResult.getErrorsByFieldName().size());
            
            request.setAttribute("errorsByFieldName", validationResult.getErrorsByFieldName());

            request.setAttribute("nameValue", RequestParsers.safeString(name));
            request.setAttribute("typeValue", RequestParsers.safeString(typeRaw));
            request.setAttribute("totalAdultsValue", RequestParsers.safeString(totalAdultsRaw));
            request.setAttribute("totalKidsValue", RequestParsers.safeString(totalKidsRaw));
            request.setAttribute("originValue", RequestParsers.safeString(originName));
            request.setAttribute("destinationValue", RequestParsers.safeString(destinationName));

            request.getRequestDispatcher("/WEB-INF/jsp/plan-form.jsp").forward(request, response);
            return;
        }

        LOG.info("Plan created successfully: " + name);
        response.sendRedirect(request.getContextPath() + "/plans");
    }
}
