<html>
<body>

<h2>5GTANGO SLA Manager APIs</h2>

------------------------------------------------------------------


<h3>Get all existing SLA Templates</h3>
- URI: /api/slas/v1/templates<br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Purpose: Get a list with the existing sla templates

<h3>Get specific SLA Template</h3>
- URI: /api/slas/v1/templates/{sla_uuid}<br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Purpose: List an sla template using the UUID to get all its details

<h3>Create SLA Template</h3>
 curl -v --raw -X POST -H "Content-type:application/x-www-form-urlencoded" -d "guaranteeId=g1&expireDate=02/02/2020&templateName=Normal6" http://localhost:8080/ROOT/tng-sla-mgmt/api/slas/v1/templates/<ns_uuid>

- URI: /api/slas/v1/templates<br>
&nbsp;&nbsp;Method: POST<br>
&nbsp;&nbsp;Path Parameters: ns_uuid<br>
&nbsp;&nbsp;Body parameters: templateName, expireDate,  guaranteeId<br>
&nbsp;&nbsp;Key: ns_uuid, Value: the ns uuid for which we create the sla template<br>
&nbsp;&nbsp;Key: templateName, Value: Premium/Normal.....etc<br>
&nbsp;&nbsp;Key: expireDate, Value: the template expiration date e.g. 20/03/2020<br>
&nbsp;&nbsp;Key: guaranteeId, Value: selected guarantee id from the list of gurantees (e.g. g1, g3 ..)<br>
&nbsp;&nbsp;Headers: Content-Type : application/x-www-form-urlencoded<br>
&nbsp;&nbsp;Purpose: Generate a new sla template

<h3>Delete SLA Template</h3>
&nbsp;&nbsp;- URI: /api/slas/v1/templates/{sla_uuid}<br>
&nbsp;&nbsp;Path Parameters: sla_uuid<br>
&nbsp;&nbsp;Method: DELETE<br>
&nbsp;&nbsp;Purpose: Delete a SLA template<br>

------------------------------------------------------------------



<h3>Get existing SLA Agreeemnts</h3>
<p> - URI: /api/slas/v1/agreements <br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Purpose: Get a list with the existing sla agreements<br>

<h3>Get specific Agreement (by uuid)</h3>
<p> - URI: /api/slas/v1/agreements/{sla_uuid}/{ns_uuid} <br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Purpose: List agreement using the UUID and the corresponding ns_uuid to get all its details<br>

<h3>Get Get Agreements per ns</h3>
- URI: /api/slas/v1/agreements/service/{ns_uuid} <br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Path Parameters: ns_uuid<br>
&nbsp;&nbsp;Purpose: Get a list with the existing sla agreements per NS

<h3>Get Agreements per customer</h3>
- URI: /api/slas/v1/agreements/customer/{cust_uuid} <br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Path Parameters: cust_uuid<br>
&nbsp;&nbsp;Purpose: Get a list with the existing sla agreements per customer

<h3>Delete SLA Agreement</h3>
- URI: /api/slas/v1/agreements/{sla_uuid}<br>
&nbsp;&nbsp;Method: DELETE<br>
&nbsp;&nbsp;Purpose: Delete a SLA agreement

<h3>Get agreement guarantee terms</h3>
- URI: /api/slas/v1/agreements/guarantee-terms/{sla_uuid}<br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Path Parameters: sla_uuid<br>
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
&nbsp;&nbsp;Method: GET
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
