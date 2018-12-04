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
import javax.ws.rs.POST;
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
	public Response getLicenses() {
		ResponseBuilder apiresponse = null;

		db_operations dbo = new db_operations();
		dbo.connectPostgreSQL();
		dbo.createTableLicensing();
		JSONArray all_licenses = dbo.getAllLicenses();
		dbo.closePostgreSQL();

		apiresponse = Response.ok((Object) all_licenses);
		apiresponse.header("Content-Length", all_licenses.toString().length());

		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "I";
		String operation = "Getting all licensing information";
		String message = ("[*] Success! all licensing information received");
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
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			String response = "License was not deleted";
			apiresponse = Response.ok((response));
			apiresponse.header("Content-Length", response.length());
			return apiresponse.status(404).build();
		}

	}

	/**
	 * Check if instantiation is allowed based on license status
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/status/{sla_uuid}/{cust_uuid}/{ns_uuid}")
	public Response getLicenseInfoPerCustomer(@PathParam("sla_uuid") String sla_uuid,
			@PathParam("cust_uuid") String cust_uuid, @PathParam("ns_uuid") String ns_uuid) {

		ResponseBuilder apiresponse = null;
		db_operations dbo = new db_operations();
		boolean connect = db_operations.connectPostgreSQL();

		JSONObject license_info_response = new JSONObject();

		if (connect == true) {

			// check if this customer has already a license for this SLA
			db_operations.createTableLicensing();
			int count_licenses = db_operations.countLicensePerCustSLA(cust_uuid, sla_uuid);
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Check if instantiation is allowed based on license status";
			String message = ("[*] Number of instances for this customer/ns/sla ==> " + count_licenses);
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			// if the customer does not have a license instance already - 1st instantiation
			if (count_licenses == 0) {
				// logging
				timestamp = new Timestamp(System.currentTimeMillis());
				timestamps = timestamp.toString();
				type = "I";
				operation = "Check if instantiation is allowed based on license status";
				message = ("First instantiation for this customer and this NS");
				status = "";
				logger.info(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);

				JSONObject license_info_template = db_operations.getLicenseinfoTemplates(sla_uuid, ns_uuid);
				String license_type = (String) license_info_template.get("license_type");

				if (license_type.equals("public")) {
					license_info_template.put("allowed_to_instantiate", "true");
				}
				if (license_type.equals("trial")) {
					license_info_template.put("allowed_to_instantiate", "true");
				}
				if (license_type.equals("private")) {
					license_info_template.put("allowed_to_instantiate", "false");
				}
				license_info_response = license_info_template;

				// logging
				timestamp = new Timestamp(System.currentTimeMillis());
				timestamps = timestamp.toString();
				type = "I";
				operation = "Check if instantiation is allowed based on license status";
				message = ("License Type: " + license_type);
				status = "";
				logger.info(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);
			}
			// if customer has already a license instance
			else {
				JSONObject license_info_record = db_operations.getLicenseInfo(sla_uuid, cust_uuid, ns_uuid);
				String license_type = (String) license_info_record.get("license_type");
				String license_status = (String) license_info_record.get("license_status");
				String license_allowed_instances = (String) license_info_record.get("allowed_instances");
				String license_current_instances = (String) license_info_record.get("current_instances");

				boolean allowed_to_instantiate = allowedToInstantiate(license_status, license_type,
						license_allowed_instances, license_current_instances);
				license_info_record.put("allowed_to_instantiate", String.valueOf(allowed_to_instantiate));
				license_info_response = license_info_record;

				// logging
				timestamp = new Timestamp(System.currentTimeMillis());
				timestamps = timestamp.toString();
				type = "I";
				operation = "Check if instantiation is allowed based on license status";
				message = ("License Type: " + license_type + " License Status: " + license_status
						+ "Allowed instances: " + license_allowed_instances + " Current instances: "
						+ license_current_instances + " Allowed to be instantiated?? "
						+ String.valueOf(allowed_to_instantiate));
				status = "";
				logger.info(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);

			}

			// close db connection
			db_operations.closePostgreSQL();
			// API Response
			apiresponse = Response.ok((Object) license_info_response);
			apiresponse.header("Content-Length", license_info_response.toJSONString().length());
			return apiresponse.status(200).build();

		} else {
			dbo.closePostgreSQL();

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Check if instantiation is allowed based on license status";
			String message = ("Error connecting to database");
			String status = "";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			JSONObject error = new JSONObject();
			error.put("ERROR: ", "connecting to database");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());
			return apiresponse.status(404).build();
		}

	}

	private boolean allowedToInstantiate(String license_status, String license_type, String license_allowed_instances,
			String license_current_instances) {
		boolean allowed_to_instantiate = false;

		boolean statusOK = isStatusOK(license_status, license_type);
		boolean instancesOK = isInstancesOK(license_allowed_instances, license_current_instances);

		if (statusOK && instancesOK) {
			allowed_to_instantiate = true;
		} else {
			allowed_to_instantiate = false;
		}
		return allowed_to_instantiate;
	}

	/**
	 * api for buying a private license
	 */
	@SuppressWarnings("null")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/x-www-form-urlencoded")
	@Path("/buy")
	@POST
	public Response LicenseBought(final MultivaluedMap<String, String> formParams) {

		ResponseBuilder apiresponse = null;

		List<String> ns_uuid = formParams.get("ns_uuid");
		List<String> sla_uuid = formParams.get("sla_uuid");
		List<String> cust_uuid = formParams.get("cust_uuid");
		List<String> cust_email = formParams.get("cust_email");

		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();
		
		JSONObject LicenseinfoTemplate = db_operations.getLicenseinfoTemplates(sla_uuid.get(0), ns_uuid.get(0));
		String license_type = (String) LicenseinfoTemplate.get("license_type");
		String license_exp_date = (String) LicenseinfoTemplate.get("license_exp_date");
		String license_period = (String) LicenseinfoTemplate.get("license_period");
		String allowed_instances = (String) LicenseinfoTemplate.get("allowed_instances");
		String current_instances = "0";
		
		db_operations.createTableLicensing();		
		db_operations.insertLicenseRecord(sla_uuid.get(0), ns_uuid.get(0), "", cust_uuid.get(0), cust_email.get(0), license_type, license_exp_date, license_period, allowed_instances, current_instances, "", "");
		
		boolean update = db_operations.UpdateLicenseStatus(sla_uuid.get(0), ns_uuid.get(0), cust_uuid.get(0), "bought");
		db_operations.closePostgreSQL();

		if (update == true) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Updating Licensing status";
			String message = ("Update License status? ==> " + update);
			String status = "200";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			JSONObject success = new JSONObject();
			success.put("Succes: ", "License status updated");
			apiresponse = Response.ok((Object) success);
			apiresponse.header("Content-Length", success.toJSONString().length());
			return apiresponse.status(200).build();
		} else {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Updating Licensing status";
			String message = ("Update License status? ==> " + update);
			String status = "404";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			JSONObject error = new JSONObject();
			error.put("Error: ", "License status was not updated");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());
			return apiresponse.status(404).build();
		}

	}

	private boolean isStatusOK(String license_status, String license_type) {
		boolean statusOK = false;
		if ((license_status.equals("inactive") || (license_status.equals("active")) && license_type.equals("public"))) {
			statusOK = true;
		}
		if ((license_status.equals("inactive") || (license_status.equals("active")) && license_type.equals("trial"))) {
			statusOK = true;
		}
		if ((license_status.equals("bought") || (license_status.equals("active")) && license_type.equals("private"))) {
			statusOK = true;
		}
		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "I";
		String operation = "Check if instantiation is allowed based on license status";
		String message = ("[*] Is status ok??? " + statusOK);
		String status = "";
		logger.info(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);

		return statusOK;
	}

	private boolean isInstancesOK(String license_allowed_instances, String license_current_instances) {
		boolean instancesOK = false;
		int ai = Integer.parseInt(license_allowed_instances);
		int ci = Integer.parseInt(license_current_instances);
		if (ci < ai) {
			instancesOK = true;
		} else {
			instancesOK = false;
		}
		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "I";
		String operation = "Check if instantiation is allowed based on license status";
		String message = ("[*] Are instances ok??? " + instancesOK);
		String status = "";
		logger.info(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);

		return instancesOK;
	}

}
