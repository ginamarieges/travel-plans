package com.ginamarieges.travelplans.web;

import com.ginamarieges.travelplans.domain.PlanType;
import com.ginamarieges.travelplans.service.PlanService;
import com.ginamarieges.travelplans.service.ValidationResult;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PlanListServletTest {

  @Test
  void doPost_shouldRedirectToPlans_whenValidationPasses() throws Exception {
    PlanService planService = mock(PlanService.class);
    ValidationResult validResult = new ValidationResult();
    when(planService.createPlan(any(), anyString(), anyString())).thenReturn(validResult);

    PlanListServlet servlet = new PlanListServlet() {
      @Override
      public void init() {
      }
    };

    // Inject mocked PlanService using reflection (simple + no framework)
    TestReflection.setField(servlet, "planService", planService);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("name")).thenReturn("Test");
    when(request.getParameter("type")).thenReturn(PlanType.NORMAL.name());
    when(request.getParameter("totalAdults")).thenReturn("2");
    when(request.getParameter("totalKids")).thenReturn("0");
    when(request.getParameter("origin")).thenReturn("Madrid");
    when(request.getParameter("destination")).thenReturn("Barcelona");
    when(request.getContextPath()).thenReturn("/travel-plans");

    servlet.doPost(request, response);

    verify(response).sendRedirect("/travel-plans/plans");
    verify(request, never()).getRequestDispatcher(anyString());
  }

  @Test
  void doPost_shouldForwardToForm_whenValidationFails() throws Exception {
    PlanService planService = mock(PlanService.class);

    ValidationResult invalidResult = new ValidationResult();
    invalidResult.addFieldError("name", "Name is required");

    when(planService.createPlan(any(), anyString(), anyString())).thenReturn(invalidResult);

    PlanListServlet servlet = new PlanListServlet() {
      @Override
      public void init() {
      }
    };
    TestReflection.setField(servlet, "planService", planService);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    RequestDispatcher dispatcher = mock(RequestDispatcher.class);

    when(request.getParameter("name")).thenReturn("");
    when(request.getParameter("type")).thenReturn("");
    when(request.getParameter("totalAdults")).thenReturn("");
    when(request.getParameter("totalKids")).thenReturn("");
    when(request.getParameter("origin")).thenReturn("");
    when(request.getParameter("destination")).thenReturn("");

    when(request.getRequestDispatcher("/WEB-INF/jsp/plan-form.jsp")).thenReturn(dispatcher);

    servlet.doPost(request, response);

    verify(request).setAttribute(eq("errorsByFieldName"), any());
    verify(dispatcher).forward(request, response);
    verify(response, never()).sendRedirect(anyString());
  }

  @Test
  void parseIntegerOrNull_shouldWork() {
    assertEquals(null, invokeParseIntegerOrNull(null));
    assertEquals(null, invokeParseIntegerOrNull(""));
    assertEquals(null, invokeParseIntegerOrNull("   "));
    assertEquals(12, invokeParseIntegerOrNull("12"));
    assertEquals(12, invokeParseIntegerOrNull(" 12 "));
    assertEquals(null, invokeParseIntegerOrNull("abc"));
  }

  private static Integer invokeParseIntegerOrNull(String rawNumber) {
    try {
      java.lang.reflect.Method method =
      RequestParsers.class.getDeclaredMethod("parseIntegerOrNull", String.class);
      method.setAccessible(true);
      return (Integer) method.invoke(null, rawNumber);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }
}
