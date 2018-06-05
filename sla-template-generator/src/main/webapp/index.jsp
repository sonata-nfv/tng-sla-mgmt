<html>
<body>

<h1>SLA Manager End Points</h1>

<h2>SLA Templates</h2>

<h3>Get existing SLA Templates</h3>
<p> URI: /api/slas/v1/templates </p>
<p> Method: GET</p>
<p> Purpose: Generate a new sla template</p>

<h3>Create SLA Template</h3>
<p> URI: /api/slas/v1/templates/{ns_uuid} </p>
<p> Method: POST</p>
<p> Parameters: templateName, expireDate </p>
<p> Body parameters: Key: guaranteeId, Value: selected guarantee id from the list of gurantees (e.g. g1, g3 ..) </p>
<p> Purpose: Generate a new sla template</p>

<h3>Delete SLA Template</h3>
<p>  URI: /api/slas/v1/templates/{sla_uuid} </p>
<p>  Method: DELETE</p>
<p>  Purpose: Delete a SLA template</p>

<h2>SLA Management</h2>

<h3>Get Guarantees List</h3>
<p> URI: api/slas/v1/mgmt/guaranteesList </p>
<p> Method: GET </p>
<p> Purpose: Get a list of Service Guarantees</p>

<h3>Get a list with correlations between NS and SLA Template</h3>
<p> URI: api/slas/v1/mgmt/services/templates</p>
<p> Method: GET </p>
<p> Purpose: Get a list of ns-template correlations</p>

<h3>Get a list with ns uuids with SLA Templates</h3>
<p> URI: api/slas/v1/mgmt/services/templates/true</p>
<p> Method: GET </p>
<p> Purpose: Get a list with ns_uuids with already associated SLA Templates</p>

<h3>Get a list with ns uuids without SLA Templates</h3>
<p> URI: api/slas/v1/mgmt/services/templates/true</p>
<p> Method: GET </p>
<p> Purpose: Get a list with ns_uuids that do not have associated SLA Templates yet</p>


</body>
</html>
