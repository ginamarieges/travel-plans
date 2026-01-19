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

@WebServlet("/plans/new")
public class PlanFormServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(PlanFormServlet.class.getName());

    private final PlanService planService = ApplicationContext.getPlanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

      String idParam = request.getParameter("id");
      if (idParam != null && !idParam.trim().isEmpty()) {
        Integer id = Integer.valueOf(idParam);
        planService.getPlanById(id).ifPresent(plan -> request.setAttribute("plan", plan));
        request.setAttribute("isEdit", true);
      } else {
        request.setAttribute("isEdit", false);
      }

      request.getRequestDispatcher("/WEB-INF/jsp/plan-form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

      String idParam = request.getParameter("id");
      String name = request.getParameter("name");
      String typeRaw = request.getParameter("type");
      String totalAdultsRaw = request.getParameter("totalAdults");
      String totalKidsRaw = request.getParameter("totalKids");
      String originName = request.getParameter("origin");
      String destinationName = request.getParameter("destination");

      Plan plan = new Plan();
      if (idParam != null && !idParam.trim().isEmpty()) {
        plan.setId(Integer.valueOf(idParam));  
      }
      plan.setName(name);
      plan.setType(RequestParsers.parsePlanType(typeRaw));
      plan.setTotalAdults(RequestParsers.parseIntegerOrNull(totalAdultsRaw));
      plan.setTotalKids(RequestParsers.parseIntegerOrNull(totalKidsRaw));

      ValidationResult validation;
      if (plan.getId() == null) {
        validation = planService.createPlan(plan, originName, destinationName);
      } else {
        plan.setOrigin(planService.getPlanById(plan.getId())
                .map(Plan::getOrigin).orElse(null));
        plan.setDestination(planService.getPlanById(plan.getId())
                .map(Plan::getDestination).orElse(null));
        validation = planService.updatePlan(plan, originName, destinationName);
      }

      if (!validation.isValid()) {
        LOG.warning("Plan validation failed for: " + name + 
                      ". Errors: " + validation.getErrorsByFieldName().size());
        request.setAttribute("errorsByFieldName", validation.getErrorsByFieldName());
        request.setAttribute("plan", plan);
        request.setAttribute("isEdit", plan.getId() != null);
        request.getRequestDispatcher("/WEB-INF/jsp/plan-form.jsp").forward(request, response);
        return;
      }

      LOG.info("Plan updated successfully: " + name);
      response.sendRedirect(request.getContextPath() + "/plans");
    }
}
