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
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import eu.tng.correlations.db_operations;
import eu.tng.correlations.ns_template_corr;

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
		db_operations.insertLicenseRecord(sla_uuid, ns_uuid, nsi_uuid, cust_uuid, cust_email, license_type,
				license_exp_date, license_period, allowed_instances, current_instances, license_status);
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

	/**
	 * delete Licensing record
	 */
	@SuppressWarnings("static-access")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes("application/x-www-form-urlencoded")
	@DELETE
	public Response deletecCustSlaCorrelation(final MultivaluedMap<String, String> formParams) {
		ResponseBuilder apiresponse = null;

		List<String> sla_uuid = formParams.get("sla_uuid");
		List<String> cust_uuid = formParams.get("cust_uuid");
		List<String> ns_uuid = formParams.get("ns_uuid");

		db_operations db = new db_operations();
		db.connectPostgreSQL();
		boolean delete = db.deleteLicenseRecord(sla_uuid.get(0), cust_uuid.get(0), ns_uuid.get(0));
		db.closePostgreSQL();

		if (delete == true) {

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Delete license record";
			String message = ("[*] Success! License record was deleted!");
			String status = "200";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			String response = "Agreement deleted Succesfully";
			apiresponse = Response.ok((response));
			apiresponse.header("Content-Length", response.length());
			return apiresponse.status(200).build();
		} else {

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Delete license record";
			String message = ("[*] Error! License record was not deleted!");
			String status = "404";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			String response = "License was not deleted";
			apiresponse = Response.ok((response));
			apiresponse.header("Content-Length", response.length());
			return apiresponse.status(404).build();
		}

	}

	/**
	 * delete cust-ns-sla correlation based on sla uuid
	 */

	@SuppressWarnings({ "static-access", "unchecked" })
	@Path("/licenses/status/{sla_uud}/{cust_uuid}/{ns_uuid}")
	@Produces(MediaType.TEXT_PLAIN)
	@GET
	public Response getLicense(@PathParam("sla_uuid") String sla_uuid, @PathParam("cust_uuid") String cust_uuid,
			@PathParam("ns_uuid") String ns_uuid) {

		ResponseBuilder apiresponse = null;

		db_operations dbo = new db_operations();
		db_operations.connectPostgreSQL();
		String license_status = db_operations.getLicenseStatus(sla_uuid, cust_uuid, ns_uuid);
		dbo.closePostgreSQL();

		if (license_status.isEmpty() == true) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get License Status";
			String message = ("Error getting license status for cust_uuid=" + cust_uuid + " and ns_uuid=" + ns_uuid );
			String status = "404";
			logger.error(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			JSONObject success = new JSONObject();
			success.put("Error", "License status not received");
			apiresponse = Response.ok((Object) success);
			apiresponse.header("Content-Length", license_status.length());
			return apiresponse.status(404).build();
		} 
		else {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Get License Status";
			String message = ("License status for cust_uuid=" + cust_uuid + " and ns_uuid=" + ns_uuid + "==> "
					+ license_status);
			String status = "200";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);


			JSONObject success = new JSONObject();
			success.put("Licence status: ", license_status);
			apiresponse = Response.ok((Object) success);
			apiresponse.header("Content-Length", license_status.length());
			return apiresponse.status(200).build();
		}

	}

}
