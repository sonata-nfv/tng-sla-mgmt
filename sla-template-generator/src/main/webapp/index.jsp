<!--
/*
 * 
 *  Copyright (c) 2015 SONATA-NFV, 2017 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 *  ALL RIGHTS RESERVED.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  Neither the name of the SONATA-NFV, 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 *  nor the names of its contributors may be used to endorse or promote
 *  products derived from this software without specific prior written
 *  permission.
 *  
 *  This work has been performed in the framework of the SONATA project,
 *  funded by the European Commission under Grant number 671517 through
 *  the Horizon 2020 and 5G-PPP programmes. The authors would like to
 *  acknowledge the contributions of their colleagues of the SONATA
 *  partner consortium (www.sonata-nfv.eu).
 *  
 *  This work has been performed in the framework of the 5GTANGO project,
 *  funded by the European Commission under Grant number 761493 through
 *  the Horizon 2020 and 5G-PPP programmes. The authors would like to
 *  acknowledge the contributions of their colleagues of the 5GTANGO
 *  partner consortium (www.5gtango.eu).
 * 
 */
-->

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
- URI: /api/slas/v1/agreements/{nsi_uuid}<br>
&nbsp;&nbsp;Method: DELETE<br>
&nbsp;&nbsp;Purpose: Delete a SLA agreement for a specific NSI

<h3>Get agreement guarantee terms</h3>
- URI: /api/slas/v1/agreements/guarantee-terms/{sla_uuid}<br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Path Parameters: sla_uuid<br>
&nbsp;&nbsp;Purpose: Get the guarantee terms per sla agreement<br>

------------------------------------------------------------------

<h3>Get all Violations</h3>
- URI: api/slas/v1/violations <br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Purpose: Get all sla violations

<h3>Get specific Violations</h3>
- URI: api/slas/v1/violations/{ns_uuid}/{sla_uuid} <br>
&nbsp;&nbsp;Method: GET<br>
&nbsp;&nbsp;Purpose: Get a specific sla violation baased on sla_uuid and ns_uuid

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
