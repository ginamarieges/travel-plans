package com.ginamarieges.travelplans.web;

import com.ginamarieges.travelplans.domain.City;
import com.ginamarieges.travelplans.domain.Plan;
import com.ginamarieges.travelplans.domain.PlanType;
import com.ginamarieges.travelplans.service.PlanService;
import com.ginamarieges.travelplans.service.ValidationResult;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PlanFormServletTest {

  @Test
  void doGet_shouldForwardToForm_withIsEditFalse_whenNoId() throws Exception {
    PlanService planService = mock(PlanService.class);

    PlanFormServlet servlet = new PlanFormServlet() {
      @Override
      public void init() {
      }
    };
    TestReflection.setField(servlet, "planService", planService);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    RequestDispatcher dispatcher = mock(RequestDispatcher.class);

    when(request.getParameter("id")).thenReturn(null);
    when(request.getRequestDispatcher("/WEB-INF/jsp/plan-form.jsp")).thenReturn(dispatcher);

    servlet.doGet(request, response);

    verify(request).setAttribute("isEdit", false);
    verify(request, never()).setAttribute(eq("plan"), any());
    verify(dispatcher).forward(request, response);
    verifyNoInteractions(response);
  }

  @Test
  void doGet_shouldForwardToForm_withIsEditTrueAndPlan_whenIdProvidedAndPlanExists() throws Exception {
    PlanService planService = mock(PlanService.class);

    Plan existingPlan = new Plan();
    existingPlan.setId(10);
    existingPlan.setName("Existing");
    existingPlan.setType(PlanType.NORMAL);

    when(planService.getPlanById(10)).thenReturn(Optional.of(existingPlan));

    PlanFormServlet servlet = new PlanFormServlet() {
      @Override
      public void init() {
      }
    };
    TestReflection.setField(servlet, "planService", planService);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    RequestDispatcher dispatcher = mock(RequestDispatcher.class);

    when(request.getParameter("id")).thenReturn("10");
    when(request.getRequestDispatcher("/WEB-INF/jsp/plan-form.jsp")).thenReturn(dispatcher);

    servlet.doGet(request, response);

    verify(planService).getPlanById(10);
    verify(request).setAttribute("plan", existingPlan);
    verify(request).setAttribute("isEdit", true);
    verify(dispatcher).forward(request, response);
  }

  @Test
  void doPost_shouldCallCreatePlan_andRedirect_whenNoIdAndValidationPasses() throws Exception {
    PlanService planService = mock(PlanService.class);
    ValidationResult validResult = new ValidationResult();
    when(planService.createPlan(any(Plan.class), anyString(), anyString())).thenReturn(validResult);

    PlanFormServlet servlet = new PlanFormServlet() {
      @Override
      public void init() {
      }
    };
    TestReflection.setField(servlet, "planService", planService);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("id")).thenReturn(null);
    when(request.getParameter("name")).thenReturn("Trip");
    when(request.getParameter("type")).thenReturn("NORMAL");
    when(request.getParameter("totalAdults")).thenReturn("2");
    when(request.getParameter("totalKids")).thenReturn("0");
    when(request.getParameter("origin")).thenReturn("Madrid");
    when(request.getParameter("destination")).thenReturn("Barcelona");
    when(request.getContextPath()).thenReturn("/travel-plans");

    servlet.doPost(request, response);

    verify(planService).createPlan(any(Plan.class), eq("Madrid"), eq("Barcelona"));
    verify(response).sendRedirect("/travel-plans/plans");
    verify(request, never()).getRequestDispatcher(anyString());
  }

  @Test
  void doPost_shouldForwardToForm_withErrors_whenCreateValidationFails() throws Exception {
    PlanService planService = mock(PlanService.class);

    ValidationResult invalidResult = new ValidationResult();
    invalidResult.addFieldError("name", "Name is required");

    when(planService.createPlan(any(Plan.class), anyString(), anyString())).thenReturn(invalidResult);

    PlanFormServlet servlet = new PlanFormServlet() {
      @Override
      public void init() {
      }
    };
    TestReflection.setField(servlet, "planService", planService);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    RequestDispatcher dispatcher = mock(RequestDispatcher.class);

    when(request.getParameter("id")).thenReturn(null);
    when(request.getParameter("name")).thenReturn("");
    when(request.getParameter("type")).thenReturn("");
    when(request.getParameter("totalAdults")).thenReturn("");
    when(request.getParameter("totalKids")).thenReturn("");
    when(request.getParameter("origin")).thenReturn("");
    when(request.getParameter("destination")).thenReturn("");

    when(request.getRequestDispatcher("/WEB-INF/jsp/plan-form.jsp")).thenReturn(dispatcher);

    servlet.doPost(request, response);

    verify(request).setAttribute(eq("errorsByFieldName"), any());
    verify(request).setAttribute(eq("plan"), any(Plan.class));
    verify(request).setAttribute("isEdit", false);
    verify(dispatcher).forward(request, response);
    verify(response, never()).sendRedirect(anyString());
  }

  @Test
  void doPost_shouldCallUpdatePlan_andRedirect_whenIdProvidedAndValidationPasses() throws Exception {
    PlanService planService = mock(PlanService.class);

    ValidationResult validResult = new ValidationResult();
    when(planService.updatePlan(any(Plan.class), anyString(), anyString())).thenReturn(validResult);

    City originCity = new City(1, "Madrid");
    City destinationCity = new City(2, "Barcelona");

    Plan existingPlan = new Plan();
    existingPlan.setId(99);
    existingPlan.setOrigin(originCity);
    existingPlan.setDestination(destinationCity);

    when(planService.getPlanById(99)).thenReturn(Optional.of(existingPlan));

    PlanFormServlet servlet = new PlanFormServlet() {
      @Override
      public void init() {
      }
    };
    TestReflection.setField(servlet, "planService", planService);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("id")).thenReturn("99");
    when(request.getParameter("name")).thenReturn("Updated");
    when(request.getParameter("type")).thenReturn("NORMAL");
    when(request.getParameter("totalAdults")).thenReturn("2");
    when(request.getParameter("totalKids")).thenReturn("0");
    when(request.getParameter("origin")).thenReturn("Madrid");
    when(request.getParameter("destination")).thenReturn("Barcelona");
    when(request.getContextPath()).thenReturn("/travel-plans");

    servlet.doPost(request, response);

    // En update, el servlet llama getPlanById() para recuperar origin/destination existentes.
    verify(planService, atLeastOnce()).getPlanById(99);
    verify(planService).updatePlan(any(Plan.class), eq("Madrid"), eq("Barcelona"));
    verify(response).sendRedirect("/travel-plans/plans");
  }

  @Test
  void doPost_shouldForwardToForm_withErrors_whenUpdateValidationFails() throws Exception {
    PlanService planService = mock(PlanService.class);

    ValidationResult invalidResult = new ValidationResult();
    invalidResult.addFieldError("name", "Name is required");

    when(planService.updatePlan(any(Plan.class), anyString(), anyString())).thenReturn(invalidResult);

    City originCity = new City(1, "Madrid");
    City destinationCity = new City(2, "Barcelona");
    Plan existingPlan = new Plan();
    existingPlan.setId(99);
    existingPlan.setOrigin(originCity);
    existingPlan.setDestination(destinationCity);

    when(planService.getPlanById(99)).thenReturn(Optional.of(existingPlan));

    PlanFormServlet servlet = new PlanFormServlet() {
      @Override
      public void init() {
      }
    };
    TestReflection.setField(servlet, "planService", planService);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    RequestDispatcher dispatcher = mock(RequestDispatcher.class);

    when(request.getParameter("id")).thenReturn("99");
    when(request.getParameter("name")).thenReturn("");
    when(request.getParameter("type")).thenReturn("NORMAL");
    when(request.getParameter("totalAdults")).thenReturn("2");
    when(request.getParameter("totalKids")).thenReturn("0");
    when(request.getParameter("origin")).thenReturn("Madrid");
    when(request.getParameter("destination")).thenReturn("Barcelona");

    when(request.getRequestDispatcher("/WEB-INF/jsp/plan-form.jsp")).thenReturn(dispatcher);

    servlet.doPost(request, response);

    verify(planService, atLeastOnce()).getPlanById(99);
    verify(request).setAttribute(eq("errorsByFieldName"), any());
    verify(request).setAttribute(eq("plan"), any(Plan.class));
    verify(request).setAttribute("isEdit", true);
    verify(dispatcher).forward(request, response);
    verify(response, never()).sendRedirect(anyString());
  }
}
