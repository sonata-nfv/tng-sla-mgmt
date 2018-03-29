<html>
<body>
<!--
	<h2>SLA Management Framework</h2>
	<p>	<a href="api/v1/slas/templategeneration">Template Generation!</a>
	<p>	<a href="api/v1/slas/edit/templates">Edit Template!</a>
    <p>	<a href="api/v1/slas/edit/templates/modify">Customize Template!</a>
-->

---
- uri: "/api/v1/slas/templategeneration" 
  method: GET
  purpose: Generate a new sla template
<p>
- uri: "api/v1/slas/edit/templates" 
  method: GET
  purpose: Edit an existing sla template
</p>
<p>
- uri: "api/v1/slas/edit/templates/modify" 
  method: GET 
  purpose: Modify an existing sla template
</p>
</body>
</html>
