<html>
<body>

<h1>SLA Manager End Points</h1>

<h2>SLA Templates</h2>

<h3>Get existing SLA Templates</h3>
<p> URI: /api/slas/v1/templates </p>
<p> Method: GET</p>
<p> Purpose: Get a list with the existing sla templates</p>

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






<h2>SLA Agreements</h2>

<h3>Get existing SLA Agreeemnts</h3>
<p> URI: /api/slas/v1/agreements </p>
<p> Method: GET</p>
<p> Purpose: Get a list with the existing sla agreements</p>

<h3>Get Get Agreements per ns</h3>
<p> URI: /api/slas/v1/agreements/service/{ns_uuid} </p>
<p> Method: GET</p>
<p> Purpose: Get a list with the existing sla agreements per NS</p>

<h3>Get Agreements per customer</h3>
<p> URI: /api/slas/v1/agreements/customer/{cust_uuid} </p>
<p> Method: GET</p>
<p> Purpose: Get a list with the existing sla agreements per customer</p>

<h3>Delete SLA Agreement</h3>
<p>  URI: /api/slas/v1/agreements/{sla_uuid}</p>
<p>  Method: DELETE</p>
<p>  Purpose: Delete a SLA agreement</p>

<h3>Get agreement guarantee terms</h3>
<p> URI: /api/slas/v1/agreements/guarantee-terms/{sla_uuid}</p>
<p> Method: GET</p>
<p> Purpose: Get the guarantee terms per sla agreement</p>


<h2>SLA Management</h2>

<h3>Get Guarantees List</h3>
<p> URI: api/slas/v1/mgmt/guaranteesList </p>
<p> Method: GET </p>
<p> Purpose: Get a list of Service Guarantees</p>

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

<h3>Get a list with instatiated NS with SLA Agreement</h3>
<p> URI: api/slas/v1/mgmt/services/agreements/true</p>
<p> Method: GET </p>
<p> Purpose: Get a list with insatatiated ns_uuids that have associated SLA Agreement</p>

<h3>Get a list with instatiated NS without SLA Agreement</h3>
<p> URI: api/slas/v1/mgmt/services/agreements/false</p>
<p> Method: GET </p>
<p> Purpose: Get a list with insatatiated ns_uuids that do not have associated SLA Agreement yet</p>


</body>
</html>
