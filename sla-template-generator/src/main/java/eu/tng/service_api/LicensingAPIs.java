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
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
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

		new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();
		JSONArray all_licenses = db_operations.getAllLicenses();
		db_operations.closePostgreSQL();

		apiresponse = Response.ok((Object) all_licenses);
		apiresponse.header("Content-Length", all_licenses.toJSONString().length());

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

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/{nsi_uuid}")
	public Response getSpecificLicense(@PathParam("nsi_uuid") String nsi_uuid) {
		ResponseBuilder apiresponse = null;

		new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();
		JSONObject specific_license = db_operations.getSpecificLicense(nsi_uuid);
		db_operations.closePostgreSQL();

		apiresponse = Response.ok((Object) specific_license);
		apiresponse.header("Content-Length", specific_license.toJSONString().length());

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
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes("application/x-www-form-urlencoded")
	@DELETE
	public Response deletecCustSlaCorrelation(final MultivaluedMap<String, String> formParams) {
		ResponseBuilder apiresponse = null;

		List<String> sla_uuid = formParams.get("sla_uuid");
		List<String> cust_username = formParams.get("cust_username");
		List<String> ns_uuid = formParams.get("ns_uuid");

		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		boolean delete = db.deleteLicenseRecord(sla_uuid.get(0), cust_username.get(0), ns_uuid.get(0));
		db_operations.closePostgreSQL();

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

			String response = "Success: License deleted Succesfully";
			apiresponse = Response.ok((response));
			apiresponse.header("Content-Length", response.length());
			return apiresponse.status(200).build();
		} else {

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Error: Delete license record failed";
			String message = ("[*] Error: License record was not deleted!");
			String status = "404";
			logger.error(
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
	@Path("/status/{sla_uuid}/{ns_uuid}")
	public Response getLicenseInfoPerCustomer(@PathParam("sla_uuid") String sla_uuid,
			@PathParam("ns_uuid") String ns_uuid, @Context HttpHeaders headers) {

		String cust_username = "";
		try {
			cust_username = headers.getRequestHeader("X-User-Name").get(0);
			
		} 
		catch (JSONException | NullPointerException e) {
			cust_username = "";
		}
		

		ResponseBuilder apiresponse = null;
		new db_operations();
		boolean connect = db_operations.connectPostgreSQL();

		JSONObject license_info_response = new JSONObject();

		if (connect == true) {

			// check if this customer has already a license for this SLA
			db_operations.createTableLicensing();
			int count_licenses = db_operations.countLicensePerCustSLA(cust_username, sla_uuid);
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

				JSONObject license_info_template = db_operations.getLicenseinfoTemplates(sla_uuid, ns_uuid);

				if (license_info_template == null || license_info_template.isEmpty()) {
					// close db connection
					db_operations.closePostgreSQL();
					// logging
					timestamp = new Timestamp(System.currentTimeMillis());
					timestamps = timestamp.toString();
					type = "W";
					operation = "Check if instantiation is allowed based on license status";
					message = "[*] Warning: Invalid sla_uuid or ns_uuid";
					status = String.valueOf(400);
					logger.error(
							"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
							type, timestamps, operation, message, status);

					// API Response
					JSONObject error = new JSONObject();
					error.put("ERROR", "Invalid parameters");
					apiresponse = Response.ok((Object) error);
					apiresponse.header("Content-Length", error.toString().length());
					return apiresponse.status(400).build();
				} 
				
				else {
					boolean allowed_to_instantiate = false;
					String license_type = (String) license_info_template.get("license_type");
					
					if (license_type.equals("public")) {
						allowed_to_instantiate = true;
						license_info_template.put("allowed_to_instantiate", allowed_to_instantiate);
						license_info_template.put("current_instances", 0);
					}
					if (license_type.equals("trial")) {
						allowed_to_instantiate = true;
						license_info_template.put("allowed_to_instantiate", allowed_to_instantiate);
						license_info_template.put("current_instances", 0);
					}
					if (license_type.equals("private")) {
						allowed_to_instantiate = false;
						license_info_template.put("allowed_to_instantiate", allowed_to_instantiate);
						license_info_template.put("current_instances", 0);
					}
					license_info_response = license_info_template;

					// close db connection
					db_operations.closePostgreSQL();
					
					// logging
					timestamp = new Timestamp(System.currentTimeMillis());
					timestamps = timestamp.toString();
					type = "I";
					operation = "Check if instantiation is allowed based on license status";
					message = ("[*] Allowed to instantiate ? ==> " + allowed_to_instantiate);
					status = "";
					logger.info(
							"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
							type, timestamps, operation, message, status);
					
					// API Response
					apiresponse = Response.ok(license_info_response);
					apiresponse.header("Content-Length", license_info_response.toString().length());
					return apiresponse.status(200).build();
				}

			}
			// if customer has already a license instance
			else {
				JSONObject license_info_record = db_operations.getLicenseInfo(sla_uuid, cust_username, ns_uuid);
				
				if (license_info_record == null || license_info_record.isEmpty()) {
					// close db connection
					db_operations.closePostgreSQL();
					// logging
					timestamp = new Timestamp(System.currentTimeMillis());
					timestamps = timestamp.toString();
					type = "W";
					operation = "Check if instantiation is allowed based on license status";
					message = "[*] Warning: Invalid sla_uuid or ns_uuid";
					status = String.valueOf(400);
					logger.error(
							"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
							type, timestamps, operation, message, status);

					// API Response
					JSONObject error = new JSONObject();
					error.put("ERROR", "Invalide parameters");
					apiresponse = Response.ok((Object) error);
					apiresponse.header("Content-Length", error.toString().length());
					return apiresponse.status(400).build();
				} 
				else {
					String license_type = (String) license_info_record.get("license_type");
					String license_status = (String) license_info_record.get("license_status");
					int license_allowed_instances = (int) license_info_record.get("allowed_instances");
					int license_current_instances = (int) license_info_record.get("current_instances");

					boolean allowed_to_instantiate = allowedToInstantiate(license_status, license_type,
							license_allowed_instances, license_current_instances);
					license_info_record.put("allowed_to_instantiate", allowed_to_instantiate);
					license_info_response = license_info_record;

					// logging
					timestamp = new Timestamp(System.currentTimeMillis());
					timestamps = timestamp.toString();
					type = "I";
					operation = "Check if instantiation is allowed based on license status";
					message = ("[*] License Type: " + license_type + " License Status: " + license_status
							+ "Allowed instances: " + license_allowed_instances + " Current instances: "
							+ license_current_instances + " Allowed to be instantiated?? "
							+ String.valueOf(allowed_to_instantiate));
					status = "";
					logger.info(
							"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
							type, timestamps, operation, message, status);

					// close db connection
					db_operations.closePostgreSQL();
					// API Response
					apiresponse = Response.ok(license_info_response);
					apiresponse.header("Content-Length", license_info_response.toString().length());
					return apiresponse.status(200).build();
				}
			}

		} else {
			db_operations.closePostgreSQL();

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Check if instantiation is allowed based on license status";
			String message = ("[*] Error connecting to database");
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

	private boolean allowedToInstantiate(String license_status, String license_type, int license_allowed_instances,
			int license_current_instances) {
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

	private boolean isInstancesOK(int license_allowed_instances, int license_current_instances) {
		boolean instancesOK = false;

		if (license_current_instances < license_allowed_instances) {
			instancesOK = true;
		} else {
			instancesOK = false;
		}
		return instancesOK;
	}
	
	/**
	 * api for buying a private license
	 */
	@SuppressWarnings({ "null", "unchecked" })
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/x-www-form-urlencoded")
	@Path("/buy")
	@POST
	public Response LicenseBought(final MultivaluedMap<String, String> formParams, @Context HttpHeaders headers) {

		String cust_username = "";
		String cust_email = "";
		try {
			cust_username = headers.getRequestHeader("X-User-Name").get(0);
			cust_email = headers.getRequestHeader("X-User-Email").get(0);
		} catch (JSONException e) {
			cust_username = "";
			cust_email = "";
		}

		ResponseBuilder apiresponse = null;

		List<String> ns_uuid = formParams.get("ns_uuid");
		List<String> sla_uuid = formParams.get("sla_uuid");

		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();

		JSONObject LicenseinfoTemplate = db_operations.getLicenseinfoTemplates(sla_uuid.get(0), ns_uuid.get(0));
		String license_type = (String) LicenseinfoTemplate.get("license_type");
		String license_exp_date = (String) LicenseinfoTemplate.get("license_exp_date");
		int allowed_instances =  (int) LicenseinfoTemplate.get("allowed_instances");
		String current_instances = "0";
		
		db_operations.createTableLicensing();
		db_operations.insertLicenseRecord(sla_uuid.get(0), ns_uuid.get(0), null, cust_username, cust_email,
				license_type, license_exp_date, String.valueOf(allowed_instances), current_instances, "", "");

		
		boolean update = db_operations.UpdateLicenseStatus(sla_uuid.get(0), ns_uuid.get(0), cust_username, "bought");
		db_operations.closePostgreSQL();

		if (update == true) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Updating Licensing status";
			String message = ("[*] Update License status? ==> " + update);
			String status = "200";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			JSONObject success = new JSONObject();
			success.put("Succes", "License bought!");
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
			error.put("Error", "License could not be bought!");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());
			return apiresponse.status(404).build();
		}

	}

	/**
	 * Nº Licenses Utilized
	 */
	@SuppressWarnings("unchecked")
	@Path("/utilized")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLicensesUtilized() {

		ResponseBuilder apiresponse = null;

		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();
		int licenses_utilized_number = db.countUtilizedLicense();
		db_operations.closePostgreSQL();

		JSONObject licenses_utilized = new JSONObject();
		licenses_utilized.put("utilized_licenses", String.valueOf(licenses_utilized_number));

		JSONObject response = licenses_utilized;
		apiresponse = Response.ok((response));
		apiresponse.header("Content-Length", response.toJSONString().length());
		return apiresponse.status(200).build();

	}

	/**
	 * Nº Licenses Acquired
	 */
	@SuppressWarnings("unchecked")
	@Path("/acquired")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLicensesAcquired() {

		ResponseBuilder apiresponse = null;

		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();
		int licenses_acquired_number = db.countAcquiredLicense();
		db_operations.closePostgreSQL();

		JSONObject licenses_acquired = new JSONObject();
		licenses_acquired.put("acquired_licenses", String.valueOf(licenses_acquired_number));

		JSONObject response = licenses_acquired;
		apiresponse = Response.ok((response));
		apiresponse.header("Content-Length", response.toJSONString().length());
		return apiresponse.status(200).build();

	}

	/**
	 * Nº Licenses Expired
	 */
	@SuppressWarnings("unchecked")
	@Path("/expired")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLicensesExpired() {

		ResponseBuilder apiresponse = null;

		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();
		int licenses_expired_number = db.countExpiredLicense();
		db_operations.closePostgreSQL();

		JSONObject licenses_expired = new JSONObject();
		licenses_expired.put("expired_licenses", String.valueOf(licenses_expired_number));

		JSONObject response = licenses_expired;
		apiresponse = Response.ok((response));
		apiresponse.header("Content-Length", response.toJSONString().length());
		return apiresponse.status(200).build();

	}

}
