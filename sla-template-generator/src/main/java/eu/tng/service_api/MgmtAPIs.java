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

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import eu.tng.correlations.*;

@Path("/mgmt")
@Consumes(MediaType.APPLICATION_JSON)
public class MgmtAPIs {

	/**
	 * Get all ns- templates correlations
	 */
	@Path("/services/templates/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTemplateNsCorrelations() {

		ResponseBuilder apiresponse = null;
		db_operations dbo = new db_operations();
		dbo.connectPostgreSQL();
		dbo.createTableNSTemplate();
		JSONObject correlations = dbo.selectAllRecords("ns_template");
		dbo.closePostgreSQL();

		apiresponse = Response.ok((Object) correlations);
		apiresponse.header("Content-Length", correlations.toString().length());
		return apiresponse.status(200).build();
	}

	/**
	 * Get all ns with associated sla template
	 */
	@Path("/services/templates/true")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNSwithTemplate() {
		ResponseBuilder apiresponse = null;
		ns_template_corr nstemplcorr = new ns_template_corr();
		JSONArray correlatedNS = nstemplcorr.nsWithTemplate();

		apiresponse = Response.ok((Object) correlatedNS);
		apiresponse.header("Content-Length", correlatedNS.toString().length());
		return apiresponse.status(200).build();
	}

	/**
	 * Get all ns with associated sla template
	 */
	@Path("/services/templates/false")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNSwithoutTemplate() {
		ResponseBuilder apiresponse = null;
		ns_template_corr nstemplcorr = new ns_template_corr();
		ArrayList<String> correlatedNS = nstemplcorr.nsWithoutTemplate();

		apiresponse = Response.ok((Object) correlatedNS);
		apiresponse.header("Content-Length", correlatedNS.toString().length());
		return apiresponse.status(200).entity(correlatedNS).build();
	}

	/**
	 * api call in order to get a predifined list with Service Guarantees
	 */
	@GET
	@Path("/guaranteesList")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGuarantees() {
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;

		ResponseBuilder apiresponse = null;
		try {
			File testf = new File(this.getClass().getResource("/slos_list_release1.json").toURI());
			jsonObject = (JSONObject) parser.parse(new FileReader(testf));
			System.out.println(jsonObject.toJSONString().length());

			apiresponse = Response.ok(jsonObject);
			apiresponse.header("Content-Length", jsonObject.toJSONString().length() - 3);
			return apiresponse.status(200).build();

		} catch (Exception e) {
			JSONObject error = new JSONObject();
			error.put("ERROR: ", "Guarantees List Not Found");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());
			return apiresponse.status(404).build();

		}
	}

	/**
	 * Get all ns with associated sla agreement
	 */
	@Path("/services/agreements/true")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNSwithAgreement() {

		ResponseBuilder apiresponse = null;
		cust_sla_corr custslacorr = new cust_sla_corr();
		JSONArray correlatedNS = custslacorr.nsWithAgreement();

		apiresponse = Response.ok((Object) correlatedNS);
		apiresponse.header("Content-Length", correlatedNS.toString().length());
		return apiresponse.status(200).build();

	}

	/**
	 * Get all ns with associated sla agreement
	 */
	@Path("/services/agreements/false")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNSwithoutAgreement() {

		ResponseBuilder apiresponse = null;

		cust_sla_corr custslacorr = new cust_sla_corr();
		ArrayList<String> correlatedNS = custslacorr.nsWithoutAgreement();

		apiresponse = Response.ok((Object) correlatedNS);
		apiresponse.header("Content-Length", correlatedNS.toString().length());
		return apiresponse.status(200).build();

	}

	/**
	 * delete cust-ns-sla correlation based on sla uuid
	 */
	@SuppressWarnings("static-access")
	@Path("/agreements/{sla_uuid}")
	@Produces(MediaType.TEXT_PLAIN)
	@DELETE
	public Response deletecCustSlaCorrelation(@PathParam("sla_uuid") String sla_uuid) {
		ResponseBuilder apiresponse = null;

		db_operations db = new db_operations();
		db.connectPostgreSQL();
		boolean delete = db.deleteRecord("cust_sla", sla_uuid);
		db.closePostgreSQL();

		if (delete == true) {
			String response = "Agreement deleted Succesfully";
			apiresponse = Response.ok((response));
			apiresponse.header("Content-Length", response.length());
			return apiresponse.status(200).build();
		} else {
			String response = "Agreement was not deleted. sla_uuid Not Found";
			apiresponse = Response.ok((response));
			apiresponse.header("Content-Length", response.length());
			return apiresponse.status(404).build();
		}

	}
	/**
	 * api in order to insert dummy violation records
	 */
	@Path("/violation")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/x-www-form-urlencoded")
	@POST
	public Response insertViolationData(final MultivaluedMap<String, String> formParams) {
		ResponseBuilder apiresponse = null;
		List<String> v_number = formParams.get("v_number");
		boolean insert = addMultipleDummyViolations(Integer.parseInt(v_number.get(0)));
		if (insert == true) {
			JSONObject success = new JSONObject();
			success.put("OK: ", "Dummy violation data uploaded to db.");
			apiresponse = Response.ok((Object) success);
			apiresponse.header("Content-Length", success.toJSONString().length());
			return apiresponse.status(200).build();
		} else {
			JSONObject fail = new JSONObject();
			fail.put("ERROR: ", "Dummy violation data failed to be uploaded to db.");
			apiresponse = Response.ok((Object) fail);
			apiresponse.header("Content-Length", fail.toJSONString().length());
			return apiresponse.status(200).build();
		}
	}

	public static boolean addMultipleDummyViolations(int v_number) {
		boolean success = false;
		int insertion = 0;
		db_operations dbo = new db_operations();
		db_operations.connectPostgreSQL();
		for (int i = 0; i < v_number; i++) {
			String random_nsi_uuid = UUID.randomUUID().toString();
			String random_sla_uuid = UUID.randomUUID().toString();
			String random_cust_uuid = UUID.randomUUID().toString();

			long offset = Timestamp.valueOf("2018-06-01 00:00:00 ").getTime();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			@SuppressWarnings("deprecation")
			long end = Timestamp.parse(dateFormat.format(date));
			long diff = end - offset + 1;
			String random_violation_time = new Timestamp(offset + (long) (Math.random() * diff)).toString();

			String[] parts = random_violation_time.split(" ");
			String part1 = parts[0];
			String part2 = parts[1];
			part1 = part1.concat("T");
			part2 = part2.concat("Z");

			random_violation_time = part1 + part2;

			System.out.println(random_violation_time);
			db_operations.createTableViolations();
			db_operations.insertRecordViolation(random_nsi_uuid, random_sla_uuid, random_violation_time, "firing",random_cust_uuid);
			insertion++;
		}
		db_operations.closePostgreSQL();
		if (insertion == v_number) {
			success = true;
		}
		return success;
	}

	public static void main(String[] args) {
		addMultipleDummyViolations(1);
	}

	/**
	 * delete all violations
	 */
	@SuppressWarnings("static-access")
	@Path("/violation")
	@Produces(MediaType.TEXT_PLAIN)
	@DELETE
	public Response deleteViolations() {
		ResponseBuilder apiresponse = null;

		db_operations db = new db_operations();
		db.connectPostgreSQL();
		boolean delete = db.deleteAllViolations();
		db.closePostgreSQL();

		if (delete == true) {
			String response = "Violations deleted Succesfully";
			apiresponse = Response.ok((response));
			apiresponse.header("Content-Length", response.length());
			return apiresponse.status(200).build();
		} else {
			String response = "Error deleting violations.";
			apiresponse = Response.ok((response));
			apiresponse.header("Content-Length", response.length());
			return apiresponse.status(404).build();
		}

	}
	
	/**
	 * Get all license records
	 */
	@Path("/licenses")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLicenses() {

		ResponseBuilder apiresponse = null;
		db_operations dbo = new db_operations();
		dbo.connectPostgreSQL();
		dbo.createTableLicenseScaling();
		JSONArray licenses = dbo.getLicenses();
		dbo.closePostgreSQL();

		apiresponse = Response.ok((Object) licenses);
		apiresponse.header("Content-Length", licenses.toString().length());
		return apiresponse.status(200).build();
	}

}
