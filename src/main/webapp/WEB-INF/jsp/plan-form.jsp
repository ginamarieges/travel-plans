<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.ginamarieges.travelplans.domain.Plan" %>
<%@ page import="com.ginamarieges.travelplans.domain.PlanType" %>
<%
    Plan plan = (Plan) request.getAttribute("plan");
    boolean isEdit = Boolean.TRUE.equals(request.getAttribute("isEdit"));
    String title = isEdit ? "Editar plan" : "Crear plan";
%>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title><%= title %></title>
  <link rel="stylesheet"
        href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
</head>
<body class="container" style="margin-top: 20px;">

<div class="page-header">
  <h1><%= title %></h1>
</div>

<form method="post" action="<%= request.getContextPath() %>/plans/new">
  <% if (isEdit && plan != null) { %>
    <input type="hidden" name="id" value="<%= plan.getId() %>"/>
  <% } %>

  <div class="form-group">
    <label>Nombre</label>
    <input class="form-control" name="name"
           value="<%= plan == null ? "" : plan.getName() %>"/>
  </div>

  <div class="form-group">
    <label>Tipo</label>
    <select class="form-control" name="type" id="planType">
      <option value="">Seleccionar tipo de plan</option>
      <option value="NORMAL"
        <%= plan != null && plan.getType() == PlanType.NORMAL ? "selected" : "" %>>
        Normal
      </option>
      <option value="TRABAJO"
        <%= plan != null && plan.getType() == PlanType.TRABAJO ? "selected" : "" %>>
        Trabajo
      </option>
    </select>
  </div>

  <div class="form-group">
    <label>Total adultos</label>
    <input class="form-control" name="totalAdults" type="number"
           value="<%= plan == null || plan.getTotalAdults() == null ? "" : plan.getTotalAdults() %>"/>
  </div>

  <div class="form-group">
    <label>Total niños</label>
    <input class="form-control" name="totalKids" type="number" id="totalKids"
           value="<%= plan == null || plan.getTotalKids() == null ? "" : plan.getTotalKids() %>"/>
  </div>

  <div class="form-group">
    <label>Origen</label>
    <input class="form-control" name="origin"
           value="<%= plan == null || plan.getOrigin() == null ? "" : plan.getOrigin().getName() %>"/>
  </div>

  <div class="form-group">
    <label>Destino</label>
    <input class="form-control" name="destination"
           value="<%= plan == null || plan.getDestination() == null ? "" : plan.getDestination().getName() %>"/>
  </div>

  <button class="btn btn-success" type="submit">
    <%= isEdit ? "Guardar" : "Crear" %>
  </button>
  <a class="btn btn-default" href="<%= request.getContextPath() %>/plans">Atrás</a>
</form>

<script>
  (function () {
    var typeSelect = document.getElementById("planType");
    var kidsInput = document.getElementById("totalKids");
    function updateKidsState() {
      if (typeSelect.value === "TRABAJO") {
        kidsInput.value = "";
        kidsInput.disabled = true;
      } else {
        kidsInput.disabled = false;
      }
    }
    updateKidsState();
    typeSelect.addEventListener("change", updateKidsState);
  })();
</script>
</body>
</html>
