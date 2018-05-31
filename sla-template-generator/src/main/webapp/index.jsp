<html>
<body>

<h1>SLA Manager End Points</h1>

<h2>Create SLA Template</h2>
<p> URI: /api/slas/v1/templates/{ns_uuid} </p>
<p> Method: POST</p>
<p> Parameters: templateName, expireDate </p>
<p> Body parameters: Key: guaranteeId, Value: selected guarantee id from the list of gurantees (e.g. g1, g3 ..) </p>
<p> Purpose: Generate a new sla template</p>

<h2>Delete SLA Template</h2>
<p> URI: /api/slas/v1/templates/{sla_uuid} </p>
<p>  Method: DELETE</p>
<p>  Purpose: Delete a SLA template</p>

<h2>Get Guarantees List</h2>

<p> URI: api/slas/v1/templates/guaranteesList </p>
<p> Method: GET </p>
<p> Purpose: Get a list of Service Guarantees</p>


</body>
</html>
