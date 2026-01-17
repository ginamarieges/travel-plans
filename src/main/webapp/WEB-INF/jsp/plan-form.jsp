<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Create plan</title>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
</head>
<body class="container" style="margin-top: 20px;">

  <div class="page-header">
    <h1>Create plan</h1>
  </div>

  <form method="post" action="<%= request.getContextPath() %>/plans">
    <div class="form-group">
      <label>Name</label>
      <input class="form-control" name="name" />
    </div>

    <div class="form-group">
      <label>Type</label>
      <select class="form-control" name="type" id="planType">
        <option value="">-- select --</option>
        <option value="NORMAL">Normal</option>
        <option value="TRABAJO">Work</option>
      </select>
    </div>

    <div class="form-group">
      <label>Total adults</label>
      <input class="form-control" name="totalAdults" type="number" />
    </div>

    <div class="form-group">
      <label>Total kids</label>
      <input class="form-control" name="totalKids" type="number" id="totalKids" />
    </div>

    <div class="form-group">
      <label>Origin</label>
      <input class="form-control" name="origin" />
    </div>

    <div class="form-group">
      <label>Destination</label>
      <input class="form-control" name="destination" />
    </div>

    <button class="btn btn-success" type="submit">Create</button>
    <a class="btn btn-default" href="<%= request.getContextPath() %>/plans">Back</a>
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
