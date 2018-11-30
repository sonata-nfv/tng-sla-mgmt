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
			System.out.println("[**] Number of licenses ==> "+count_licenses);		
			// if the customer does not have a license instance already - 1st instantiation
			if (count_licenses == 0) {
				
				System.out.println("First instantiation for this customer");
				JSONObject license_info_template = db_operations.getLicenseinfoTemplates(sla_uuid, ns_uuid);
				String license_type = (String) license_info_template.get("license_type");
				

				System.out.println("License Type ==> " + license_type);
				if (license_type.equals("public")) {
					license_info_template.put("allowed_to_instantiate", "true");
				}
				if (license_type.equals("trial")) {
					license_info_template.put("allowed_to_instantiate", "true");
				}
				if (license_type.equals("private")){
					license_info_template.put("allowed_to_instantiate", "false");
				}
				license_info_response = license_info_template;
			
				System.out.println("Response ==> " + license_info_response.toString());

			}			
			// if customer has already a license instance
			else {
				System.out.println("Not the first instantiation for this");   
				JSONObject license_info_record = db_operations.getLicenseInfo(sla_uuid, cust_uuid, ns_uuid);
				String license_type = (String) license_info_record.get("license_type");
				String license_status = (String) license_info_record.get("license_status");
				String license_allowed_instances = (String) license_info_record.get("allowed_instances");
				//String license_current_instances = (String) license_info_record.get("current_instances");
				String license_current_instances = "1";
				
				System.out.println("DEBUG  LICENSE TYPE " + license_type);
				System.out.println("DEBUG  LICENSE STATUS " +license_status);
				System.out.println("DEBUG  LICENSE ALL INST " +license_allowed_instances);
				System.out.println("DEBUG  LICENSE CURR INST " +license_current_instances);

				
				boolean allowed_to_instantiate = allowedToInstantiate(license_status, license_type, license_allowed_instances, license_current_instances);
				license_info_record.put("allowed_to_instantiate", String.valueOf(allowed_to_instantiate));
				license_info_response = license_info_record;
				System.out.println("Response ==> " + license_info_response.toString());

			}	
			
			db_operations.closePostgreSQL();
			
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Get License Status";
			String message = ("License status received");   
			String status = "200";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			// API Response
			apiresponse = Response.ok((Object) license_info_response);
			apiresponse.header("Content-Length", license_info_response.toJSONString().length());
			return apiresponse.status(200).build();

		} else {
			dbo.closePostgreSQL();

			JSONObject error = new JSONObject();
			error.put("ERROR: ", "connecting to database");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());
			return apiresponse.status(404).build();
		}

	}
	
	private boolean allowedToInstantiate(String license_status, String license_type, String license_allowed_instances, String license_current_instances) {
		boolean allowed_to_instantiate = false;   
		
		boolean statusOK = isStatusOK(license_status, license_type);
		boolean instancesOK = isInstancesOK(license_allowed_instances, license_current_instances);
		
		if (statusOK && instancesOK) {
			allowed_to_instantiate = true;
		} else {
			allowed_to_instantiate = false;
		}
		System.out.println("[*] Is instantiation allowed?? " + allowed_to_instantiate);

		return allowed_to_instantiate;
	}

	private boolean isStatusOK(String license_status, String license_type) {
		boolean statusOK = false;	
		if ((license_status.equals("inactive") || (license_status.equals("active"))  && license_type.equals("public"))) {
			statusOK = true;
		}
		if ((license_status.equals("inactive") || (license_status.equals("active"))  && license_type.equals("trial"))) {
			statusOK = true;
		}
		if ((license_status.equals("bought") || (license_status.equals("active"))  && license_type.equals("private"))) {
			statusOK = true;
		}
		System.out.println("[*] Is status ok??? " + statusOK);

		return statusOK;
	}
	
	private boolean isInstancesOK(String license_allowed_instances, String license_current_instances) {
		boolean instancesOK = false;
		int ai = Integer.parseInt(license_allowed_instances);
		int ci = Integer.parseInt(license_current_instances);
		
		System.out.println("[*] INT ALL INS " + ai);
		System.out.println("[*] int ci " + ci);
	
		if (ci < ai) {
			instancesOK = true;
		} 
		else {
			instancesOK = false;
		}
		System.out.println("[*] Are instances ok??? " + instancesOK);
		return instancesOK;
	}

}
