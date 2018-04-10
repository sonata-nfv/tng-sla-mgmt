<html>
<body>
<!--
	<h2>SLA Management Framework</h2>
	<p>	<a href="api/slas/v1/templates">Template Generation!</a>
	<p>	<a href="api/slas/v1/edit/templates">Edit Template!</a>
    <p>	<a href="api/slas/v1/edit/templates/modify">Customize Template!</a>
-->

---
- uri: "/api/slas/v1/templates" 
  method: POST
  purpose: Generate a new sla template
<p>
- uri: "api/slas/v1/edit/templates" 
  method: GET
  purpose: Edit an existing sla template
</p>
<p>
- uri: "api/slas/v1/edit/templates/modify" 
  method: GET 
  purpose: Modify an existing sla template
</p>
</body>
</html>
