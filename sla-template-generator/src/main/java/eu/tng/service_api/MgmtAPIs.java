/*
 * Copyright (c) 2017 5GTANGO, UPRC ALL RIGHTS RESERVED.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Neither the name of the 5GTANGO, UPRC nor the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * This work has been performed in the framework of the 5GTANGO project, funded by the European
 * Commission under Grant number 761493 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the 5GTANGO partner consortium
 * (www.5gtango.eu).
 *
 * @author Evgenia Kapassa (MSc), UPRC
 * 
 * @author Marios Touloupou (MSc), UPRC
 * 
 */

package eu.tng.service_api;

import java.io.File;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import eu.tng.correlations.cust_sla_corr;
import eu.tng.correlations.db_operations;
import eu.tng.correlations.ns_template_corr;

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
			apiresponse.header("Content-Length", jsonObject.toJSONString().length() - 2);
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
		
		String response = "Record deleted Succesfully";
		db_operations db = new db_operations();
		
		db.connectPostgreSQL();
		
		db.deleteRecord("cust_sla", sla_uuid);
		apiresponse = Response.ok((response));
		apiresponse.header("Content-Length", response.length());
		return apiresponse.status(200).build();
	}

}
