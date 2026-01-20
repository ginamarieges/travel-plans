<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ginamarieges.travelplans.domain.Plan" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Gestor de Planes</title>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
  <style>
    body {
      min-height: 100vh;
      padding: 30px 0;
    }
    
    .main-container {
      background: white;
      border-radius: 12px;
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
      overflow: hidden;
      margin-bottom: 30px;
    }
    
    .page-header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 40px 30px;
      margin: 0;
      border: none;
    }
    
    .page-header h1 {
      margin: 0;
      font-size: 32px;
      font-weight: 600;
    }
    
    .page-header p {
      margin: 10px 0 0 0;
      opacity: 0.9;
      font-size: 15px;
    }
    
    .content-wrapper {
      padding: 30px;
    }
    
    .action-bar {
      margin-bottom: 30px;
      padding-bottom: 20px;
      border-bottom: 2px solid #f0f0f0;
    }
    
    .btn {
      border-radius: 8px;
      font-weight: 600;
      padding: 10px 20px;
      transition: all 0.3s ease;
      border: none;
    }
    
    .btn-primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
    }
    
    .btn-primary:hover,
    .btn-primary:focus {
      background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }
    
    .btn-success {
      background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
      box-shadow: 0 4px 15px rgba(56, 239, 125, 0.3);
    }
    
    .btn-success:hover,
    .btn-success:focus {
      background: linear-gradient(135deg, #38ef7d 0%, #11998e 100%);
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(56, 239, 125, 0.4);
    }
    
    .btn-default {
      background: white;
      border: 2px solid #e0e0e0;
      color: #666;
    }
    
    .btn-default:hover {
      border-color: #667eea;
      color: #667eea;
      background: #f8f9ff;
    }
    
    .section-title {
      font-size: 20px;
      font-weight: 600;
      color: #333;
      margin-bottom: 20px;
      padding-bottom: 10px;
      border-bottom: 2px solid #f0f0f0;
    }
    
    .section-title .fa {
      color: #667eea;
      margin-right: 8px;
    }
    
    .table-wrapper {
      background: white;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
      margin-bottom: 30px;
    }
    
    .table {
      margin-bottom: 0;
    }
    
    .table thead {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    .table thead th {
      color: white;
      font-weight: 600;
      border: none;
      padding: 15px;
      font-size: 13px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    
    .table tbody tr {
      transition: all 0.3s ease;
    }
    
    .table tbody tr:hover {
      background-color: #f8f9ff;
      transform: scale(1.01);
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
    }
    
    .table tbody td {
      padding: 15px;
      vertical-align: middle;
      border-color: #f0f0f0;
    }
    
    .table .text-muted {
      text-align: center;
      padding: 40px;
      font-style: italic;
      color: #999;
    }
    
    .badge-type {
      display: inline-block;
      padding: 5px 12px;
      border-radius: 20px;
      font-size: 11px;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    
    .badge-normal {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      color: white;
    }
    
    .badge-trabajo {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      color: white;
    }
    
    .btn-xs {
      padding: 5px 12px;
      font-size: 12px;
      border-radius: 6px;
      margin: 2px;
    }
    
    .btn-primary.btn-xs {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    .btn-danger.btn-xs {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }
    
    .empty-state {
      text-align: center;
      padding: 60px 20px;
    }
    
    .empty-state .fa {
      font-size: 64px;
      color: #e0e0e0;
      margin-bottom: 20px;
    }
    
    .empty-state h3 {
      color: #999;
      font-weight: 600;
      margin-bottom: 10px;
    }
    
    .empty-state p {
      color: #bbb;
    }
    
    /* Modal Styles */
    .modal-content {
      border-radius: 12px;
      border: none;
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
    }
    
    .modal-header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-radius: 12px 12px 0 0;
      border: none;
      padding: 20px 25px;
    }
    
    .modal-header .close {
      color: white;
      opacity: 0.8;
      text-shadow: none;
    }
    
    .modal-header .close:hover {
      opacity: 1;
    }
    
    .modal-title {
      font-weight: 600;
    }
    
    .modal-body {
      padding: 30px 25px;
      font-size: 15px;
    }
    
    .modal-footer {
      border-top: 2px solid #f0f0f0;
      padding: 20px 25px;
    }
    
    /* Responsive Cards for Mobile */
    @media (max-width: 768px) {
      .content-wrapper {
        padding: 20px 15px;
      }
      
      .action-bar .btn {
        display: block;
        width: 100%;
        margin-bottom: 10px;
      }
      
      .table-wrapper {
        box-shadow: none;
        border-radius: 0;
      }
      
      .table thead {
        display: none;
      }
      
      .table tbody tr {
        display: block;
        margin-bottom: 15px;
        border: 2px solid #f0f0f0;
        border-radius: 8px;
        background: white;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
      }
      
      .table tbody tr:hover {
        transform: none;
      }
      
      .table tbody td {
        display: block;
        text-align: right;
        padding: 10px 15px;
        border: none;
        border-bottom: 1px solid #f5f5f5;
      }
      
      .table tbody td:last-child {
        border-bottom: none;
        text-align: center;
        padding: 15px;
      }
      
      .table tbody td:before {
        content: attr(data-label);
        float: left;
        font-weight: 600;
        color: #667eea;
      }
      
      .table tbody td.text-muted {
        text-align: center;
      }
      
      .table tbody td.text-muted:before {
        display: none;
      }
    }
    
    @media (max-width: 480px) {
      .page-header h1 {
        font-size: 24px;
      }
      
      .section-title {
        font-size: 18px;
      }
    }
  </style>
</head>
<body>

  <%
    Boolean isGroupingEnabled = (Boolean) request.getAttribute("isGroupingEnabled");
    if (isGroupingEnabled == null) {
      isGroupingEnabled = false;
    }
  %>

  <div class="container">
    <div class="main-container">
      <div class="page-header">
        <h1>
          <i class="fa fa-plane"></i>
          Gestor de Planes de Viaje
        </h1>
      </div>

      <div class="content-wrapper">
        <div class="action-bar">
          <% if (!isGroupingEnabled) { %>
            <a class="btn btn-primary" href="<%= request.getContextPath() %>/plans?grouping=true">
              <i class="fa fa-link"></i> Ver planes compatibles
            </a>
          <% } else { %>
            <a class="btn btn-default" href="<%= request.getContextPath() %>/plans">
              <i class="fa fa-list"></i> Ver todos los planes
            </a>
          <% } %>
          <a class="btn btn-success" href="<%= request.getContextPath() %>/plans/new">
            <i class="fa fa-plus-circle"></i> Crear nuevo plan
          </a>
        </div>

        <% if (!isGroupingEnabled) { %>
          <%
            List<Plan> plans = (List<Plan>) request.getAttribute("plans");
            if (plans == null) {
              plans = java.util.Collections.emptyList();
            }
          %>

          <div class="section-title">
            <i class="fa fa-list-ul"></i> Todos los planes
          </div>

          <% if (plans.isEmpty()) { %>
            <div class="empty-state">
              <i class="fa fa-calendar-times-o"></i>
              <h3>No hay planes todavía</h3>
            </div>
          <% } else { %>
            <div class="table-wrapper">
              <table class="table table-striped">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Tipo</th>
                    <th>Adultos</th>
                    <th>Niños</th>
                    <th>Origen</th>
                    <th>Destino</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <% for (Plan plan : plans) { %>
                    <tr>
                      <td data-label="Nombre"><strong><%= plan.getName() %></strong></td>
                      <td data-label="Tipo">
                        <span class="badge-type badge-<%= plan.getType().toString().toLowerCase() %>">
                          <%= plan.getType() == com.ginamarieges.travelplans.domain.PlanType.NORMAL ? "Vacaciones" : "Trabajo" %>
                        </span>
                      </td>
                      <td data-label="Adultos"><i class="fa fa-user"></i> <%= plan.getTotalAdults() %></td>
                      <td data-label="Niños"><i class="fa fa-child"></i> <%= plan.getTotalKids() == null ? "0" : plan.getTotalKids() %></td>
                      <td data-label="Origen"><i class="fa fa-map-marker"></i> <%= plan.getOrigin() == null ? "-" : plan.getOrigin().getName() %></td>
                      <td data-label="Destino"><i class="fa fa-map-pin"></i> <%= plan.getDestination() == null ? "-" : plan.getDestination().getName() %></td>
                      <td data-label="Acciones">
                        <a href="<%= request.getContextPath() %>/plans/new?id=<%= plan.getId() %>" 
                           class="btn btn-primary btn-xs">
                          <i class="fa fa-edit"></i> Editar
                        </a>
                        <form method="post" action="<%= request.getContextPath() %>/plans" style="display: inline;" class="delete-form">
                          <input type="hidden" name="action" value="delete">
                          <input type="hidden" name="id" value="<%= plan.getId() %>">
                          <input type="hidden" name="planName" value="<%= plan.getName() %>">
                          <button type="submit" class="btn btn-danger btn-xs">
                            <i class="fa fa-trash"></i> Borrar
                          </button>
                        </form>
                      </td>
                    </tr>
                  <% } %>
                </tbody>
              </table>
            </div>
          <% } %>

        <% } else { %>

          <%
            List<Plan> compatiblePlans = (List<Plan>) request.getAttribute("compatiblePlans");
            List<Plan> otherPlans = (List<Plan>) request.getAttribute("otherPlans");

            if (compatiblePlans == null) {
              compatiblePlans = java.util.Collections.emptyList();
            }
            if (otherPlans == null) {
              otherPlans = java.util.Collections.emptyList();
            }
          %>

          <!-- Compatible Plans Section -->
          <div class="section-title">
            <i class="fa fa-link"></i> Planes compatibles
          </div>

          <% if (compatiblePlans.isEmpty()) { %>
            <div class="empty-state">
              <i class="fa fa-unlink"></i>
              <h3>No hay planes compatibles</h3>
              <p>Los planes compatibles comparten el mismo tipo, origen y destino</p>
            </div>
          <% } else { %>
            <div class="table-wrapper">
              <table class="table table-striped">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Tipo</th>
                    <th>Adultos</th>
                    <th>Niños</th>
                    <th>Origen</th>
                    <th>Destino</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <% for (Plan plan : compatiblePlans) { %>
                    <tr>
                      <td data-label="Nombre"><strong><%= plan.getName() %></strong></td>
                      <td data-label="Tipo">
                        <span class="badge-type badge-<%= plan.getType().toString().toLowerCase() %>">
                          <%= plan.getType() == com.ginamarieges.travelplans.domain.PlanType.NORMAL ? "Vacaciones" : "Trabajo" %>
                        </span>
                      </td>
                      <td data-label="Adultos"><i class="fa fa-user"></i> <%= plan.getTotalAdults() %></td>
                      <td data-label="Niños"><i class="fa fa-child"></i> <%= plan.getTotalKids() == null ? "0" : plan.getTotalKids() %></td>
                      <td data-label="Origen"><i class="fa fa-map-marker"></i> <%= plan.getOrigin() == null ? "-" : plan.getOrigin().getName() %></td>
                      <td data-label="Destino"><i class="fa fa-map-pin"></i> <%= plan.getDestination() == null ? "-" : plan.getDestination().getName() %></td>
                      <td data-label="Acciones">
                        <a href="<%= request.getContextPath() %>/plans/new?id=<%= plan.getId() %>" 
                           class="btn btn-primary btn-xs">
                          <i class="fa fa-edit"></i> Editar
                        </a>
                        <form method="post" action="<%= request.getContextPath() %>/plans" style="display: inline;" class="delete-form">
                          <input type="hidden" name="action" value="delete">
                          <input type="hidden" name="id" value="<%= plan.getId() %>">
                          <input type="hidden" name="planName" value="<%= plan.getName() %>">
                          <button type="submit" class="btn btn-danger btn-xs">
                            <i class="fa fa-trash"></i> Borrar
                          </button>
                        </form>
                      </td>
                    </tr>
                  <% } %>
                </tbody>
              </table>
            </div>
          <% } %>

          <!-- Other Plans Section -->
          <div class="section-title" style="margin-top: 40px;">
            <i class="fa fa-th-list"></i> Otros planes
          </div>

          <% if (otherPlans.isEmpty()) { %>
            <div class="empty-state">
              <i class="fa fa-check-circle"></i>
              <h3>No hay otros planes</h3>
              <p>Todos tus planes tienen compañeros de viaje compatibles</p>
            </div>
          <% } else { %>
            <div class="table-wrapper">
              <table class="table table-striped">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Tipo</th>
                    <th>Adultos</th>
                    <th>Niños</th>
                    <th>Origen</th>
                    <th>Destino</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <% for (Plan plan : otherPlans) { %>
                    <tr>
                      <td data-label="Nombre"><strong><%= plan.getName() %></strong></td>
                      <td data-label="Tipo">
                        <span class="badge-type badge-<%= plan.getType().toString().toLowerCase() %>">
                          <%= plan.getType() == com.ginamarieges.travelplans.domain.PlanType.NORMAL ? "Vacaciones" : "Trabajo" %>
                        </span>
                      </td>
                      <td data-label="Adultos"><i class="fa fa-user"></i> <%= plan.getTotalAdults() %></td>
                      <td data-label="Niños"><i class="fa fa-child"></i> <%= plan.getTotalKids() == null ? "0" : plan.getTotalKids() %></td>
                      <td data-label="Origen"><i class="fa fa-map-marker"></i> <%= plan.getOrigin() == null ? "-" : plan.getOrigin().getName() %></td>
                      <td data-label="Destino"><i class="fa fa-map-pin"></i> <%= plan.getDestination() == null ? "-" : plan.getDestination().getName() %></td>
                      <td data-label="Acciones">
                        <a href="<%= request.getContextPath() %>/plans/new?id=<%= plan.getId() %>" 
                           class="btn btn-primary btn-xs">
                          <i class="fa fa-edit"></i> Editar
                        </a>
                        <form method="post" action="<%= request.getContextPath() %>/plans" style="display: inline;" class="delete-form">
                          <input type="hidden" name="action" value="delete">
                          <input type="hidden" name="id" value="<%= plan.getId() %>">
                          <input type="hidden" name="planName" value="<%= plan.getName() %>">
                          <button type="submit" class="btn btn-danger btn-xs">
                            <i class="fa fa-trash"></i> Borrar
                          </button>
                        </form>
                      </td>
                    </tr>
                  <% } %>
                </tbody>
              </table>
            </div>
          <% } %>

        <% } %>
      </div>
    </div>
  </div>

  <!-- Delete Confirmation Modal -->
  <div class="modal fade" id="deleteModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">
            <span>&times;</span>
          </button>
          <h4 class="modal-title">
            <i class="fa fa-exclamation-triangle"></i> Confirmar eliminación
          </h4>
        </div>
        <div class="modal-body">
          <p>¿Estás seguro de que deseas eliminar el plan "<strong id="planNameToDelete"></strong>"?</p>
          <p style="color: #999; font-size: 13px; margin-top: 10px;">
            <i class="fa fa-info-circle"></i> Esta acción no se puede deshacer.
          </p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">
            <i class="fa fa-times"></i> Cancelar
          </button>
          <button type="button" class="btn btn-danger" id="confirmDelete">
            <i class="fa fa-trash"></i> Eliminar plan
          </button>
        </div>
      </div>
    </div>
  </div>

  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
  <script>
    document.addEventListener('DOMContentLoaded', function() {
      const deleteForms = document.querySelectorAll('.delete-form');
      let formToSubmit = null;
      
      deleteForms.forEach(function(form) {
        form.addEventListener('submit', function(e) {
          e.preventDefault();
          
          const planName = form.querySelector('input[name="planName"]').value;
          document.getElementById('planNameToDelete').textContent = planName;
          
          formToSubmit = form;
          $('#deleteModal').modal('show');
        });
      });
      
      document.getElementById('confirmDelete').addEventListener('click', function() {
        if (formToSubmit) {
          $('#deleteModal').modal('hide');
          formToSubmit.submit();
        }
      });
    });
  </script>

</body>
</html>
