<html>
<body>

---
- uri: "/api/slas/v1/templates/{ns_uuid}" 
  method: POST
  purpose: Generate a new sla template
<p>
- uri: "api/slas/v1/templates/{sla_uuid}" 
  method: PUT
  purpose: Edit an existing sla template by changing specific key-value pairs
</p>
<p>
- uri: "api/slas/v1/templates/customize/{sla_uuid}" 
  method: PUT 
  purpose: Customize an existing sla template by adding objectives
</p>

============================================================================
<p>
- uri: "api/slas/v1/templates/guaranteesList" 
  method: GET 
  purpose: Get a predifined list with Service Guarantees
</p>

</body>
</html>
