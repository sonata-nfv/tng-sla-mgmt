<html>
<body>

<h2>5GTANGO SLA Manager APIs</h2>

------------------------------------------------------------------


<h3>Get existing SLA Templates</h3>
- URI: /api/slas/v1/templates<br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Purpose: Get a list with the existing sla templates

<h3>Create SLA Template</h3>
- URI: /api/slas/v1/templates/{ns_uuid}<br>
&nbsp;&nbsp;Method: POST<br>
&nbsp;&nbsp;Parameters: templateName, expireDate<br>
&nbsp;&nbsp;Body parameters: Key: guaranteeId, Value: selected guarantee id from the list of gurantees (e.g. g1, g3 ..)<br>
&nbsp;&nbsp;Headers: Content-Type : application/x-www-form-urlencoded<br>
&nbsp;&nbsp;Purpose: Generate a new sla template

<h3>Delete SLA Template</h3>
&nbsp;&nbsp;- URI: /api/slas/v1/templates/{sla_uuid}<br>
&nbsp;&nbsp;Method: DELETE<br>
&nbsp;&nbsp;Purpose: Delete a SLA template<br>

------------------------------------------------------------------



<h3>Get existing SLA Agreeemnts</h3>
<p> - URI: /api/slas/v1/agreements <br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Purpose: Get a list with the existing sla agreements<br>

<h3>Get Get Agreements per ns</h3>
- URI: /api/slas/v1/agreements/service/{ns_uuid} <br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Purpose: Get a list with the existing sla agreements per NS

<h3>Get Agreements per customer</h3>
- URI: /api/slas/v1/agreements/customer/{cust_uuid} <br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Purpose: Get a list with the existing sla agreements per customer

<h3>Delete SLA Agreement</h3>
- URI: /api/slas/v1/agreements/{sla_uuid}<br>
&nbsp;&nbsp;Method: DELETE<br>
&nbsp;&nbsp;Purpose: Delete a SLA agreement

<h3>Get agreement guarantee terms</h3>
- URI: /api/slas/v1/agreements/guarantee-terms/{sla_uuid}<br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Purpose: Get the guarantee terms per sla agreement<br>

------------------------------------------------------------------


<h3>Get Guarantees List</h3>
- URI: api/slas/v1/mgmt/guaranteesList <br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Purpose: Get a list of Service Guarantees

<h3>Get a list with correlations between NS and SLA Template</h3>
- URI: api/slas/v1/mgmt/services/templates<br>
&nbsp;&nbsp;Method: GET <br>
&nbsp;&nbsp;Purpose: Get a list of ns-template correlations<br>

<h3>Get a list with ns uuids with SLA Templates</h3>
- URI: api/slas/v1/mgmt/services/templates/true<br>
&nbsp;&nbsp;Method: GET <br>
&nbsp;&nbsp;Purpose: Get a list with ns_uuids with already associated SLA Templates<br>

<h3>Get a list with ns uuids without SLA Templates</h3>
- URI: api/slas/v1/mgmt/services/templates/false<br>
&nbsp;&nbsp;Method: GET </p>
&nbsp;&nbsp;Purpose: Get a list with ns_uuids that do not have associated SLA Templates yet

<h3>Get a list with instatiated NS with SLA Agreement</h3>
- URI: api/slas/v1/mgmt/services/agreements/true<br>
&nbsp;&nbsp;Method: GET <br>
&nbsp;&nbsp;Purpose: Get a list with insatatiated ns_uuids that have associated SLA Agreement

<h3>Get a list with instatiated NS without SLA Agreement</h3>
- URI: api/slas/v1/mgmt/services/agreements/false<br>
&nbsp;&nbsp;Method: GET <br>
&nbsp;&nbsp;Purpose: Get a list with insatatiated ns_uuids that do not have associated SLA Agreement yet


</body>
</html>
