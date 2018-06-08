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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

		db_operations dbo = new db_operations();

		dbo.connectPostgreSQL();
		dbo.createTableNSTemplate();
		JSONObject correlations = dbo.selectAllRecords("ns_template");
		dbo.closePostgreSQL();
		
		return Response.status(200).entity(correlations).build();
	}
	
	/**
	 * Get all ns with associated sla template
	 */
	@Path("/services/templates/true")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNSwithTemplate() {
		ns_template_corr nstemplcorr = new ns_template_corr();
		JSONArray correlatedNS = nstemplcorr.nsWithTemplate();
		return Response.status(200).entity(correlatedNS).build();
	}
	
	/**
	 * Get all ns with associated sla template 
	 */
	@Path("/services/templates/false")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNSwithoutTemplate() {
		ns_template_corr nstemplcorr = new ns_template_corr();
		ArrayList<String> correlatedNS = nstemplcorr.nsWithoutTemplate();
		return Response.status(200).entity(correlatedNS).build();
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
		try {
			File testf = new File(this.getClass().getResource("/slos_list_release1.json").toURI());
			jsonObject = (JSONObject) parser.parse(new FileReader(testf));
			System.out.println(jsonObject);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(200).entity(jsonObject).build();
	}
	
	/**
	 * Get all ns with associated sla agreement
	 */
	@Path("/services/agreements/true")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNSwithAgreement() {
		cust_sla_corr custslacorr = new cust_sla_corr();
		JSONArray correlatedNS = custslacorr.nsWithAgreement();
		return Response.status(200).entity(correlatedNS).build();

	}
	

	/**
	 * Get all ns with associated sla agreement
	 */
	@Path("/services/agreements/false")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNSwithoutAgreement() {
		cust_sla_corr custslacorr = new cust_sla_corr();
		ArrayList<String> correlatedNS = custslacorr.nsWithoutAgreement();
		return Response.status(200).entity(correlatedNS).build();

	}

}
