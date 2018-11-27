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

package eu.tng.service_api;

import java.sql.Timestamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import eu.tng.correlations.db_operations;

@Path("/licenses")
@Consumes(MediaType.APPLICATION_JSON)
public class LicensingAPIs {

	final static Logger logger = LogManager.getLogger();

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getTemplates() {
		ResponseBuilder apiresponse = null;

		String cust_uuid = "cust_uuid_test";
		String cust_email = "cust_email_test";
		String sla_uuid = "sla_uuid_test";
		String ns_uuid = "ns_uuid_test";
		String nsi_uuid = "nsi_uuid_test";
		String license_type = "license_type_type";
		String license_exp_date = "license_exp_date_test";
		String license_period = "license_period_test";
		String allowed_instances = "allowed_instances_test";
		String current_instances = "current_instances_test";
		String license_status = "license_status_test";
		
		db_operations dbo = new db_operations();
		dbo.connectPostgreSQL();
		dbo.createTableLicensing();
		dbo.insertLicenseRecord(sla_uuid, ns_uuid, nsi_uuid, cust_uuid, cust_email, license_type, license_exp_date, license_period, allowed_instances, current_instances, license_status);
		JSONArray all_licenses = dbo.getAllLicenses();
		dbo.closePostgreSQL();

		apiresponse = Response.ok((Object) all_licenses);
		apiresponse.header("Content-Length", all_licenses.toString().length());

		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "I";
		String operation = "Getting all licensing information";
		String message = ("[*] Success! all licensing information received ==> " + all_licenses.toString());
		String status = "200";
		logger.info(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);

		return apiresponse.status(200).build();
	}
	

}
