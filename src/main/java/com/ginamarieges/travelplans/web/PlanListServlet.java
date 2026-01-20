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

/**
 * Servlet responsible for handling travel plan listing, creation, and deletion.
 * Mapped to /plans endpoint and supports both regular and grouped views.
 */
@WebServlet("/plans")
public class PlanListServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(PlanListServlet.class.getName());

    // Service layer dependency - retrieved from application context for loose coupling
    private final PlanService planService = ApplicationContext.getPlanService();

    /**
     * Handles GET requests to display plans.
     * Supports two modes: regular list view and compatibility-grouped view.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOG.info("Processing GET request to /plans");

        // Check if grouping mode is requested via query parameter
        String groupingParam = request.getParameter("grouping");
        boolean isGroupingEnabled = "true".equalsIgnoreCase(groupingParam);

        if (isGroupingEnabled) {
            // Grouping mode: separate plans into compatible and other groups
            LOG.info("Grouping plans by compatibility");
            CompatibilityGroupingResult groupingResult = planService.groupPlansByCompatibility();
            request.setAttribute("isGroupingEnabled", true);
            request.setAttribute("compatiblePlans", groupingResult.getCompatiblePlans());
            request.setAttribute("otherPlans", groupingResult.getOtherPlans());
            LOG.info("Found " + groupingResult.getCompatiblePlans().size() + 
                    " compatible plans and " + groupingResult.getOtherPlans().size() + " other plans");
        } else {
            // Default mode: retrieve all plans in a single list
            List<Plan> plans = planService.getAllPlans();
            request.setAttribute("isGroupingEnabled", false);
            request.setAttribute("plans", plans);
            LOG.info("Retrieved " + plans.size() + " plans");
        }

        // Forward to JSP for rendering - using WEB-INF to prevent direct access
        request.getRequestDispatcher("/WEB-INF/jsp/plans-list.jsp").forward(request, response);
    }

    /**
     * Handles POST requests for plan creation and deletion.
     * Action is determined by the 'action' parameter.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOG.info("Processing POST request to /plans");
        
        String action = request.getParameter("action");
        LOG.fine("Action parameter: " + action);
        
        // Route to appropriate handler based on action
        if ("delete".equals(action)) {
            handleDelete(request, response);
            return;
        }
        
        // Default action is create
        handleCreate(request, response);
    }

    /**
     * Deletes a plan by ID.
     * Uses POST-Redirect-GET pattern to prevent duplicate submissions.
     */
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
                // Log but don't fail - invalid IDs are silently ignored
                LOG.log(Level.WARNING, "Invalid plan ID format: " + planId, e);
            }
        } else {
            LOG.warning("Delete request received with empty or null plan ID");
        }
        
        // Redirect to list page (PRG pattern prevents resubmission on refresh)
        response.sendRedirect(request.getContextPath() + "/plans");
    }

    /**
     * Creates a new plan from form data.
     * On validation failure, redisplays the form with errors and user input preserved.
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOG.info("Handling create action");
        
        // Extract form parameters
        String name = request.getParameter("name");
        String typeRaw = request.getParameter("type");
        String totalAdultsRaw = request.getParameter("totalAdults");
        String totalKidsRaw = request.getParameter("totalKids");
        String originName = request.getParameter("origin");
        String destinationName = request.getParameter("destination");

        LOG.fine("Creating plan with name: " + name);

        // Build plan object from request parameters
        Plan plan = new Plan();
        plan.setName(name);
        plan.setType(RequestParsers.parsePlanType(typeRaw));
        plan.setTotalAdults(RequestParsers.parseIntegerOrNull(totalAdultsRaw));
        plan.setTotalKids(RequestParsers.parseIntegerOrNull(totalKidsRaw));

        // Delegate validation and persistence to service layer
        ValidationResult validationResult = planService.createPlan(plan, originName, destinationName);

        if (!validationResult.isValid()) {
            // Validation failed: preserve input and display errors
            LOG.warning("Plan validation failed for: " + name + 
                       ". Errors: " + validationResult.getErrorsByFieldName().size());
            
            request.setAttribute("errorsByFieldName", validationResult.getErrorsByFieldName());

            // Preserve user input to avoid data loss
            request.setAttribute("nameValue", RequestParsers.safeString(name));
            request.setAttribute("typeValue", RequestParsers.safeString(typeRaw));
            request.setAttribute("totalAdultsValue", RequestParsers.safeString(totalAdultsRaw));
            request.setAttribute("totalKidsValue", RequestParsers.safeString(totalKidsRaw));
            request.setAttribute("originValue", RequestParsers.safeString(originName));
            request.setAttribute("destinationValue", RequestParsers.safeString(destinationName));

            // Forward back to form with validation errors
            request.getRequestDispatcher("/WEB-INF/jsp/plan-form.jsp").forward(request, response);
            return;
        }

        // Success: redirect to list page (PRG pattern)
        LOG.info("Plan created successfully: " + name);
        response.sendRedirect(request.getContextPath() + "/plans");
    }
}
