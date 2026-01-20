<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ginamarieges.travelplans.domain.Plan" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Plans</title>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
</head>
<body class="container" style="margin-top: 20px;">

  <div class="page-header">
    <h1>Gestor de planes</h1>
  </div>

  <%
    Boolean isGroupingEnabled = (Boolean) request.getAttribute("isGroupingEnabled");
    if (isGroupingEnabled == null) {
      isGroupingEnabled = false;
    }
  %>

  <div style="margin-bottom: 15px;">
    <% if (!isGroupingEnabled) { %>
      <a class="btn btn-primary" href="<%= request.getContextPath() %>/plans?grouping=true">Planes compatibles</a>
    <% } else { %>
      <a class="btn btn-default" href="<%= request.getContextPath() %>/plans">Todos los planes</a>
    <% } %>
    <a class="btn btn-success" href="<%= request.getContextPath() %>/plans/new">Crear plan</a>
  </div>

  <% if (!isGroupingEnabled) { %>
    <%
      List<Plan> plans = (List<Plan>) request.getAttribute("plans");
      if (plans == null) {
        plans = java.util.Collections.emptyList();
      }
    %>

    <h3>Planes</h3>
    <table class="table table-striped table-bordered">
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
      <% if (plans.isEmpty()) { %>
        <tr>
          <td colspan="8" class="text-muted">Aún no hay planes</td>
        </tr>
      <% } else { %>
        <% for (Plan plan : plans) { %>
          <tr>
            <td><%= plan.getName() %></td>
            <td><%= plan.getType() %></td>
            <td><%= plan.getTotalAdults() %></td>
            <td><%= plan.getTotalKids() == null ? "0" : plan.getTotalKids() %></td>
            <td><%= plan.getOrigin() == null ? "" : plan.getOrigin().getName() %></td>
            <td><%= plan.getDestination() == null ? "" : plan.getDestination().getName() %></td>
            <td>
              <a href="<%= request.getContextPath() %>/plans/new?id=<%= plan.getId() %>" 
                 class="btn btn-primary btn-xs">
                Editar
              </a>
              <form method="post" action="<%= request.getContextPath() %>/plans" style="display: inline;" class="delete-form">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="<%= plan.getId() %>">
                <input type="hidden" name="planName" value="<%= plan.getName() %>">
                <button type="submit" class="btn btn-danger btn-xs">
                  Borrar
                </button>
              </form>
            </td>
          </tr>
        <% } %>
      <% } %>
      </tbody>
    </table>

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

    <h3>Planes compatibles</h3>
    <table class="table table-striped table-bordered">
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
      <% if (compatiblePlans.isEmpty()) { %>
        <tr>
          <td colspan="8" class="text-muted">No se han encontrado planes compatibles</td>
        </tr>
      <% } else { %>
        <% for (Plan plan : compatiblePlans) { %>
          <tr>
            <td><%= plan.getName() %></td>
            <td><%= plan.getType() %></td>
            <td><%= plan.getTotalAdults() %></td>
            <td><%= plan.getTotalKids() == null ? "0" : plan.getTotalKids() %></td>
            <td><%= plan.getOrigin() == null ? "" : plan.getOrigin().getName() %></td>
            <td><%= plan.getDestination() == null ? "" : plan.getDestination().getName() %></td>
            <td>
              <a href="<%= request.getContextPath() %>/plans/new?id=<%= plan.getId() %>" 
                 class="btn btn-primary btn-xs">
                Editar
              </a>
              <form method="post" action="<%= request.getContextPath() %>/plans" style="display: inline;" class="delete-form">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="<%= plan.getId() %>">
                <input type="hidden" name="planName" value="<%= plan.getName() %>">
                <button type="submit" class="btn btn-danger btn-xs">
                  Borrar
                </button>
              </form>
            </td>
          </tr>
        <% } %>
      <% } %>
      </tbody>
    </table>

    <h3>Otros planes</h3>
    <table class="table table-striped table-bordered">
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
      <% if (otherPlans.isEmpty()) { %>
        <tr>
          <td colspan="8" class="text-muted">No hay otros planes</td>
        </tr>
      <% } else { %>
        <% for (Plan plan : otherPlans) { %>
          <tr>
            <td><%= plan.getName() %></td>
            <td><%= plan.getType() %></td>
            <td><%= plan.getTotalAdults() %></td>
            <td><%= plan.getTotalKids() == null ? "0" : plan.getTotalKids() %></td>
            <td><%= plan.getOrigin() == null ? "" : plan.getOrigin().getName() %></td>
            <td><%= plan.getDestination() == null ? "" : plan.getDestination().getName() %></td>
            <td>
              <a href="<%= request.getContextPath() %>/plans/new?id=<%= plan.getId() %>" 
                 class="btn btn-primary btn-xs">
                Editar
              </a>
              <form method="post" action="<%= request.getContextPath() %>/plans" style="display: inline;" class="delete-form">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="<%= plan.getId() %>">
                <input type="hidden" name="planName" value="<%= plan.getName() %>">
                <button type="submit" class="btn btn-danger btn-xs">
                  Borrar
                </button>
              </form>
            </td>
          </tr>
        <% } %>
      <% } %>
      </tbody>
    </table>

  <% } %>
<!-- Modal -->
  <div class="modal fade" id="deleteModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">
            <span>&times;</span>
          </button>
          <h4 class="modal-title">Confirmar acción</h4>
        </div>
        <div class="modal-body">
          <p>Estás seguro de que deseas eliminar el plan "<strong id="planNameToDelete"></strong>"? </p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
          <button type="button" class="btn btn-danger" id="confirmDelete">Borrar</button>
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
