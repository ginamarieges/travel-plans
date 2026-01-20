package com.ginamarieges.travelplans.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ginamarieges.travelplans.domain.Plan;
import com.ginamarieges.travelplans.service.PlanService;
import com.ginamarieges.travelplans.service.ValidationResult;

/**
 * Servlet responsible for displaying and processing the plan creation/edit form.
 * Handles both new plan creation and existing plan updates through the same endpoint.
 */
@WebServlet("/plans/new")
public class PlanFormServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(PlanFormServlet.class.getName());

    // Service layer dependency for business logic and data access
    private final PlanService planService = ApplicationContext.getPlanService();

    /**
     * Displays the plan form for either creating a new plan or editing an existing one.
     * Mode is determined by the presence of an 'id' parameter.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

      // Check if we're editing an existing plan
      String idParam = request.getParameter("id");
      if (idParam != null && !idParam.trim().isEmpty()) {
        // Edit mode: load existing plan data to populate the form
        Integer id = Integer.valueOf(idParam);
        planService.getPlanById(id).ifPresent(plan -> request.setAttribute("plan", plan));
        request.setAttribute("isEdit", true);
      } else {
        // Create mode: display empty form
        request.setAttribute("isEdit", false);
      }

      // Forward to form JSP - WEB-INF prevents direct access
      request.getRequestDispatcher("/WEB-INF/jsp/plan-form.jsp").forward(request, response);
    }

    /**
     * Processes form submission for both plan creation and updates.
     * Validates input and either saves the plan or redisplays the form with errors.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

      // Extract form parameters
      String idParam = request.getParameter("id");
      String name = request.getParameter("name");
      String typeRaw = request.getParameter("type");
      String totalAdultsRaw = request.getParameter("totalAdults");
      String totalKidsRaw = request.getParameter("totalKids");
      String originName = request.getParameter("origin");
      String destinationName = request.getParameter("destination");

      // Build plan object from form data
      Plan plan = new Plan();
      if (idParam != null && !idParam.trim().isEmpty()) {
        // Existing plan - set ID for update operation
        plan.setId(Integer.valueOf(idParam));  
      }
      plan.setName(name);
      plan.setType(RequestParsers.parsePlanType(typeRaw));
      plan.setTotalAdults(RequestParsers.parseIntegerOrNull(totalAdultsRaw));
      plan.setTotalKids(RequestParsers.parseIntegerOrNull(totalKidsRaw));

      // Route to appropriate service method based on create vs update
      ValidationResult validation;
      if (plan.getId() == null) {
        // New plan - delegate to create service method
        validation = planService.createPlan(plan, originName, destinationName);
      } else {
        // Existing plan - preserve origin/destination if not changed
        // This prevents losing location data when only other fields are updated
        plan.setOrigin(planService.getPlanById(plan.getId())
                .map(Plan::getOrigin).orElse(null));
        plan.setDestination(planService.getPlanById(plan.getId())
                .map(Plan::getDestination).orElse(null));
        validation = planService.updatePlan(plan, originName, destinationName);
      }

      if (!validation.isValid()) {
        // Validation failed: redisplay form with errors and user input preserved
        LOG.warning("Plan validation failed for: " + name + 
                      ". Errors: " + validation.getErrorsByFieldName().size());
        request.setAttribute("errorsByFieldName", validation.getErrorsByFieldName());
        request.setAttribute("plan", plan);
        request.setAttribute("isEdit", plan.getId() != null);
        request.getRequestDispatcher("/WEB-INF/jsp/plan-form.jsp").forward(request, response);
        return;
      }

      // Success: redirect to list page (PRG pattern prevents duplicate submissions)
      LOG.info("Plan updated successfully: " + name);
      response.sendRedirect(request.getContextPath() + "/plans");
    }
}
