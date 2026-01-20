<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.ginamarieges.travelplans.domain.Plan" %>
<%@ page import="com.ginamarieges.travelplans.domain.PlanType" %>
<%@ page import="java.util.Map" %>
<%
    Plan plan = (Plan) request.getAttribute("plan");
    boolean isEdit = Boolean.TRUE.equals(request.getAttribute("isEdit"));
    String title = isEdit ? "Editar plan" : "Crear nuevo plan";
    
    @SuppressWarnings("unchecked")
    Map<String, String> errors = (Map<String, String>) request.getAttribute("errorsByFieldName");
    if (errors == null) {
        errors = java.util.Collections.emptyMap();
    }
%>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><%= title %></title>
  <link rel="icon" type="image/x-icon" href="<%= request.getContextPath() %>/favicon.ico">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
  <style>
    body {
      min-height: 100vh;
      padding: 40px 0;
    }
    
    .form-container {
      background: white;
      border-radius: 12px;
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
      padding: 0;
      margin-top: 20px;
      overflow: hidden;
    }
    
    .form-header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 30px;
      text-align: center;
    }
    
    .form-header h1 {
      margin: 0;
      font-size: 28px;
      font-weight: 600;
    }
    
    .form-header p {
      margin: 10px 0 0 0;
      opacity: 0.9;
      font-size: 14px;
    }
    
    .form-body {
      padding: 40px;
    }
    
    .form-group label {
      font-weight: 600;
      color: #333;
      margin-bottom: 8px;
      font-size: 14px;
    }
    
    .form-control {
      border: 2px solid #e0e0e0;
      border-radius: 8px;
      padding: 12px 15px;
      font-size: 14px;
      transition: all 0.3s ease;
      height: auto;
    }
    
    .form-control:focus {
      border-color: #667eea;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
      outline: none;
    }
    
    .form-control:disabled {
      background-color: #f5f5f5;
      cursor: not-allowed;
      opacity: 0.6;
    }
    
    .input-icon-wrapper {
      position: relative;
    }
    
    .input-icon-wrapper .fa {
      position: absolute;
      left: 15px;
      top: 50%;
      transform: translateY(-50%);
      color: #999;
      z-index: 10;
    }
    
    .input-icon-wrapper .form-control {
      padding-left: 45px;
    }
    
    .has-error .form-control {
      border-color: #d9534f;
    }
    
    .has-error .help-block {
      color: #d9534f;
      font-size: 13px;
      margin-top: 5px;
      margin-bottom: 0;
    }
    
    .btn-primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      border-radius: 8px;
      padding: 12px 30px;
      font-weight: 600;
      font-size: 15px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
    }
    
    .btn-primary:hover,
    .btn-primary:focus,
    .btn-primary:active {
      background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }
    
    .btn-default {
      background: white;
      border: 2px solid #e0e0e0;
      border-radius: 8px;
      padding: 12px 30px;
      font-weight: 600;
      font-size: 15px;
      color: #666;
      transition: all 0.3s ease;
    }
    
    .btn-default:hover {
      border-color: #667eea;
      color: #667eea;
      background: #f8f9ff;
    }
    
    .form-actions {
      margin-top: 30px;
      padding-top: 30px;
      border-top: 2px solid #f0f0f0;
      text-align: right;
    }
    
    .form-section {
      margin-bottom: 30px;
    }
    
    .form-section-title {
      font-size: 16px;
      font-weight: 600;
      color: #667eea;
      margin-bottom: 20px;
      padding-bottom: 10px;
      border-bottom: 2px solid #f0f0f0;
    }
    
    .disabled-info {
      background: #fff3cd;
      border: 1px solid #ffc107;
      border-radius: 8px;
      padding: 12px 15px;
      margin-top: 10px;
      font-size: 13px;
      color: #856404;
      display: none;
    }
    
    .disabled-info .fa {
      margin-right: 8px;
    }
    
    @media (max-width: 768px) {
      .form-body {
        padding: 30px 20px;
      }
      
      .form-actions {
        text-align: center;
      }
      
      .form-actions .btn {
        display: block;
        width: 100%;
        margin-bottom: 10px;
      }
    }
  </style>
</head>
<body>

<div class="container">
  <div class="row">
    <div class="col-md-8 col-md-offset-2 col-lg-6 col-lg-offset-3">
      <div class="form-container">
        <div class="form-header">
          <h1>
            <i class="fa fa-<%= isEdit ? "edit" : "plus-circle" %>"></i>
            <%= title %>
          </h1>
          <p><%= isEdit ? "Modifica los detalles de tu plan de viaje" : "Completa los detalles para crear tu plan de viaje" %></p>
        </div>
        
        <div class="form-body">
          <form method="post" action="<%= request.getContextPath() %>/plans/new">
            <% if (isEdit && plan != null) { %>
              <input type="hidden" name="id" value="<%= plan.getId() %>"/>
            <% } %>

            <!-- Basic Information Section -->
            <div class="form-section">
              <div class="form-section-title">
                <i class="fa fa-info-circle"></i> Información básica
              </div>
              
              <div class="form-group <%= errors.containsKey("name") ? "has-error" : "" %>">
                <label for="name">Nombre del plan</label>
                <div class="input-icon-wrapper">
                  <i class="fa fa-tag"></i>
                  <input class="form-control" 
                         id="name"
                         name="name"
                         type="text"
                         placeholder="ej. Vacaciones en familia"
                         value="<%= plan == null ? "" : (plan.getName() == null ? "" : plan.getName()) %>"/>
                </div>
                <% if (errors.containsKey("name")) { %>
                  <span class="help-block"><%= errors.get("name") %></span>
                <% } %>
              </div>

              <div class="form-group <%= errors.containsKey("planType") ? "has-error" : "" %>">
                <label for="planType">Tipo de plan</label>
                <div class="input-icon-wrapper">
                  <i class="fa fa-suitcase"></i>
                  <select class="form-control" name="type" id="planType">
                    <option value="">-- Selecciona el tipo de plan --</option>
                    <option value="NORMAL"
                      <%= plan != null && plan.getType() == PlanType.NORMAL ? "selected" : "" %>>
                      <i class="fa fa-sun-o"></i> Vacaciones / Ocio
                    </option>
                    <option value="TRABAJO"
                      <%= plan != null && plan.getType() == PlanType.TRABAJO ? "selected" : "" %>>
                      Viaje de trabajo
                    </option>
                  </select>
                </div>
                <% if (errors.containsKey("planType")) { %>
                  <span class="help-block"><%= errors.get("planType") %></span>
                <% } %>
              </div>
            </div>

            <!-- Travelers Section -->
            <div class="form-section">
              <div class="form-section-title">
                <i class="fa fa-users"></i> Viajeros
              </div>
              
              <div class="row">
                <div class="col-sm-6">
                  <div class="form-group <%= errors.containsKey("totalAdults") ? "has-error" : "" %>">
                    <label for="totalAdults">Adultos</label>
                    <div class="input-icon-wrapper">
                      <i class="fa fa-user"></i>
                      <input class="form-control" 
                             id="totalAdults"
                             name="totalAdults" 
                             type="number"
                             min="0"
                             placeholder="0"
                             value="<%= plan == null || plan.getTotalAdults() == null ? "" : plan.getTotalAdults() %>"/>
                    </div>
                    <% if (errors.containsKey("totalAdults")) { %>
                      <span class="help-block"><%= errors.get("totalAdults") %></span>
                    <% } %>
                  </div>
                </div>
                
                <div class="col-sm-6">
                  <div class="form-group <%= errors.containsKey("totalKids") ? "has-error" : "" %>">
                    <label for="totalKids">Niños</label>
                    <div class="input-icon-wrapper">
                      <i class="fa fa-child"></i>
                      <input class="form-control" 
                             id="totalKids"
                             name="totalKids" 
                             type="number"
                             min="0"
                             placeholder="0"
                             value="<%= plan == null || plan.getTotalKids() == null ? "" : plan.getTotalKids() %>"/>
                    </div>
                    <% if (errors.containsKey("totalKids")) { %>
                      <span class="help-block"><%= errors.get("totalKids") %></span>
                    <% } %>
                    <div class="disabled-info" id="kidsWarning">
                      <i class="fa fa-info-circle"></i>
                      Los viajes de trabajo no permiten niños
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Route Section -->
            <div class="form-section">
              <div class="form-section-title">
                <i class="fa fa-map-marker"></i> Ruta del viaje
              </div>
              
              <div class="form-group <%= errors.containsKey("originCity") ? "has-error" : "" %>">
                <label for="origin">Ciudad de origen</label>
                <div class="input-icon-wrapper">
                  <i class="fa fa-plane"></i>
                  <input class="form-control" 
                         id="origin"
                         name="origin"
                         type="text"
                         placeholder="ej. Barcelona"
                         value="<%= plan == null || plan.getOrigin() == null ? "" : plan.getOrigin().getName() %>"/>
                </div>
                <% if (errors.containsKey("originCity")) { %>
                  <span class="help-block"><%= errors.get("originCity") %></span>
                <% } %>
              </div>

              <div class="form-group <%= errors.containsKey("destinationCity") ? "has-error" : "" %>">
                <label for="destination">Ciudad de destino</label>
                <div class="input-icon-wrapper">
                  <i class="fa fa-map-pin"></i>
                  <input class="form-control" 
                         id="destination"
                         name="destination"
                         type="text"
                         placeholder="ej. París"
                         value="<%= plan == null || plan.getDestination() == null ? "" : plan.getDestination().getName() %>"/>
                </div>
                <% if (errors.containsKey("destinationCity")) { %>
                  <span class="help-block"><%= errors.get("destinationCity") %></span>
                <% } %>
              </div>
            </div>

            <!-- Form Actions -->
            <div class="form-actions">
              <a class="btn btn-default" href="<%= request.getContextPath() %>/plans">
                <i class="fa fa-arrow-left"></i> Cancelar
              </a>
              <button class="btn btn-primary" type="submit">
                <i class="fa fa-<%= isEdit ? "save" : "check" %>"></i>
                <%= isEdit ? "Guardar cambios" : "Crear plan" %>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
  (function () {
    var typeSelect = document.getElementById("planType");
    var kidsInput = document.getElementById("totalKids");
    var kidsWarning = document.getElementById("kidsWarning");
    
    function updateKidsState() {
      if (typeSelect.value === "TRABAJO") {
        kidsInput.value = "";
        kidsInput.disabled = true;
        kidsWarning.style.display = "block";
      } else {
        kidsInput.disabled = false;
        kidsWarning.style.display = "none";
      }
    }
    
    // Initialize state on page load
    updateKidsState();
    
    // Update state when plan type changes
    typeSelect.addEventListener("change", updateKidsState);
  })();
</script>

</body>
</html>
