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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import eu.tng.correlations.cust_sla_corr;
import eu.tng.correlations.db_operations;
import eu.tng.correlations.ns_template_corr;
import eu.tng.template_gen.CreateTemplate;
import eu.tng.validations.TemplateValidation;

@Path("/agreements")
@Consumes(MediaType.APPLICATION_JSON)
public class AgreementsAPIs {

	final static Logger logger = LogManager.getLogger();

	/**
	 * api call in order to get a list with the active agreements
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getActiveAgreements() {

		ResponseBuilder apiresponse = null;

		db_operations dbo = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableCustSla();
		JSONObject correlations = db_operations.getActiveAgreements();
		dbo.closePostgreSQL();

		apiresponse = Response.ok((Object) correlations);
		apiresponse.header("Content-Length", correlations.toString().length());

		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "I";
		String operation = "Get active Agreements";
		String message = ("Succesfully getting active agreements");
		String status = "200";
		logger.info(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);

		return apiresponse.status(200).build();
	}

	/**
	 * api call in order to get a list with all the existing agreements
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all")
	public Response getAgreements() {

		ResponseBuilder apiresponse = null;

		db_operations dbo = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableCustSla();
		JSONObject correlations = db_operations.getAgreements();
		dbo.closePostgreSQL();

		apiresponse = Response.ok((Object) correlations);
		apiresponse.header("Content-Length", correlations.toString().length());

		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "I";
		String operation = "Get all Agreements";
		String message = ("Succesfully getting agreements");
		String status = "200";
		logger.info(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);

		return apiresponse.status(200).build();
	}

	/**
	 * Delete an agreement if violated
	 */
	@DELETE
	@Path("/{nsi_uuid}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteAgreement(@PathParam("nsi_uuid") String nsi_uuid) {
		ResponseBuilder apiresponse = null;
		String dr = null;
		HttpURLConnection httpURLConnection = null;
		URL url = null;

		db_operations dbo = new db_operations();
		dbo.connectPostgreSQL();
		JSONObject agrPerNs = dbo.selectAgreementPerNSI(nsi_uuid);
		String sla_status = (String) agrPerNs.get("sla_status");
		dbo.closePostgreSQL();

		if (sla_status.equals("VIOLATED")) {

			dbo.connectPostgreSQL();
			boolean deleted = dbo.deleteAgreement(nsi_uuid);
			dbo.closePostgreSQL();

			if (deleted == true) {
				JSONObject success_delete = new JSONObject();
				success_delete.put("OK: ", "Agreement was deleted successfully.");
				apiresponse = Response.ok((Object) success_delete);
				apiresponse.header("Content-Length", success_delete.toJSONString().length());

				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "I";
				String operation = "Delete Agreement";
				String message = ("Succesfully deleting agreement because it was violated");
				String status = "200";
				logger.info(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);

				return apiresponse.status(200).build();
			} else {
				JSONObject error_deleting = new JSONObject();
				error_deleting.put("ERROR: ", "Agreement cannot be deleted.");
				apiresponse = Response.ok((Object) error_deleting);
				apiresponse.header("Content-Length", error_deleting.toJSONString().length());

				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "W";
				String operation = "Delete Agreement";
				String message = ("Error. Agreement cannot be deleted");
				String status = "404";
				logger.warn(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);

				return apiresponse.status(404).build();
			}
		} else {
			JSONObject error_activesla = new JSONObject();
			error_activesla.put("ERROR: ", "Agreement cannot be deleted because it's active.");
			apiresponse = Response.ok((Object) error_activesla);
			apiresponse.header("Content-Length", error_activesla.toJSONString().length());

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Delete Agreement";
			String message = ("Error. Agreement cannot be deleted because it's active.");
			String status = "400";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			return apiresponse.status(400).build();
		}
	}

	/**
	 * api call in order to get a list with all the existing agreements per NSI
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("service/{nsi_uuid}")
	public Response getAgreementsPerNS(@PathParam("nsi_uuid") String nsi_uuid) {

		ResponseBuilder apiresponse = null;

		db_operations dbo = new db_operations();
		boolean connect = db_operations.connectPostgreSQL();
		if (connect == true) {
			db_operations.createTableCustSla();
			JSONObject agrPerNs = dbo.selectAgreementPerNSI(nsi_uuid);
			dbo.closePostgreSQL();

			apiresponse = Response.ok(agrPerNs);
			apiresponse.header("Content-Length", agrPerNs.toString().length());
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

	/**
	 * api call in order to get a list with all the existing agreements per Customer
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("customer/{cust_uuid}")
	public Response getAgreementsPerCustonmer(@PathParam("cust_uuid") String cust_uuid) {

		ResponseBuilder apiresponse = null;
		db_operations dbo = new db_operations();
		boolean connect = db_operations.connectPostgreSQL();

		if (connect == true) {
			db_operations.createTableCustSla();
			JSONObject agrPerNs = dbo.selectAgreementPerCustomer(cust_uuid);
			dbo.closePostgreSQL();

			apiresponse = Response.ok(agrPerNs);
			apiresponse.header("Content-Length", agrPerNs.toString().length());
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

	/**
	 * api call in order to get a specific sla agreement
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{sla_uuid}/{nsi_uuid}")
	public Response getAgreementDetails(@PathParam("sla_uuid") String sla_uuid,
			@PathParam("nsi_uuid") String nsi_uuid) {

		ResponseBuilder apiresponse = null;
		try {
			String url = System.getenv("CATALOGUES_URL") + "slas/template-descriptors/" + sla_uuid;
			// String url =
			// "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors/"
			// + sla_uuid;
			URL object = new URL(url);

			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("GET");

			@SuppressWarnings("unused")
			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// get the core sla from the catalogue
			JSONParser parser = new JSONParser();
			Object existingTemplates = parser.parse(response.toString());
			JSONObject agreement = (JSONObject) parser.parse(response.toString());

			// get customer details from db
			db_operations dbo = new db_operations();
			dbo.connectPostgreSQL();
			db_operations.createTableCustSla();
			JSONObject agrPerSlaNs = dbo.selectAgreementPerSlaNs(sla_uuid, nsi_uuid);
			dbo.closePostgreSQL();

			String cust_uuid = (String) agrPerSlaNs.get("cust_uuid");
			String cust_email = (String) agrPerSlaNs.get("cust_email");
			String sla_date = (String) agrPerSlaNs.get("sla_date");

			// update the template with the necessary customer info - convert it to
			// agreement
			JSONObject slad = (JSONObject) agreement.get("slad");
			JSONObject sla_template = (JSONObject) slad.get("sla_template");

			/** change the offered date to the date the agreement was created */
			sla_template.put("offered_date", sla_date);

			/** add the customer information */
			JSONObject customer_info = new JSONObject();
			customer_info.put("cust_uuid", cust_uuid);
			customer_info.put("cust_email", cust_email);
			sla_template.put("customer_info", customer_info);

			System.out.println(agreement);
			existingTemplates = agreement;

			apiresponse = Response.ok((Object) existingTemplates);
			apiresponse.header("Content-Length", agreement.toJSONString().length() - 7);

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Get Agreement";
			String message = ("Succesfully get the agreement among with customer info");
			String status = "200";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			return apiresponse.status(200).build();

		} catch (Exception e) {

			JSONObject error = new JSONObject();
			error.put("ERROR: ", "SLA Not Found");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Get Agreement";
			String message = ("SLA Not Found");
			String status = "404";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			return apiresponse.status(404).build();

		}

	}

	/**
	 * get the garantee terms for a specific agreement
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("guarantee-terms/{sla_uuid}")
	public Response getAgreementTerms(@PathParam("sla_uuid") String sla_uuid) {

		ResponseBuilder apiresponse = null;

		new cust_sla_corr();
		JSONArray gt = cust_sla_corr.getGuaranteeTerms(sla_uuid);
		if (gt != null) {
			apiresponse = Response.ok(gt);
			System.out.println(gt.toString().length());
			apiresponse.header("Content-Length", gt.toString().length() - 1);

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Get Guarantee terms";
			String message = ("Succesfully get Guarantee terms");
			String status = "200";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			return apiresponse.status(200).build();
		} else {
			JSONObject error = new JSONObject();
			error.put("ERROR: ", "guarantee terms are null");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Get Guarantee terms";
			String message = ("Error getting Guarantee terms");
			String status = "404";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
			return apiresponse.status(404).build();
		}

	}

	/**
	 * api call in order to generate a sla template
	 */
	@SuppressWarnings("null")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/x-www-form-urlencoded")
	@POST
	@Path("create")
	public Response createAgreement(final MultivaluedMap<String, String> formParams) {

		List<String> ns_uuid1 = formParams.get("ns_uuid");
		List<String> ns_name1 = formParams.get("ns_name");
		List<String> sla_uuid1 = formParams.get("sla_uuid");
		List<String> sla_name1 = formParams.get("sla_name");
		List<String> sla_status1 = formParams.get("sla_status");
		List<String> cust_name1 = formParams.get("cust_name");
		List<String> cust_uuid1 = formParams.get("cust_uuid");
		List<String> inst_status1 = formParams.get("inst_status");
		List<String> correlation_id1 = formParams.get("correlation_id");

		ResponseBuilder apiresponse = null;

		db_operations dbo = new db_operations();
		boolean connect = db_operations.connectPostgreSQL();

		if (connect == true) {
			db_operations.createTableCustSla();
			dbo.insertRecordAgreement(ns_uuid1.get(0).toString(), ns_name1.toString(), sla_uuid1.get(0).toString(),
					sla_name1.get(0).toString(), sla_status1.get(0).toString(), cust_name1.get(0).toString(),
					cust_uuid1.get(0).toString(), inst_status1.get(0).toString(), correlation_id1.get(0).toString());
			db_operations.UpdateRecordAgreement("READY", "123", "4fbb748a-4c2e-441b-b387-0f3da2c77ca7");
			dbo.closePostgreSQL();

			apiresponse = Response.ok();
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

}
