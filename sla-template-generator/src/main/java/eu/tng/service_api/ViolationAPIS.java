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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import eu.tng.correlations.db_operations;

@Path("/violations")
@Consumes(MediaType.APPLICATION_JSON)
public class ViolationAPIS {

	final static Logger logger = LogManager.getLogger();

	/**
	 * api call in order to get a JSONObject with violations per
	 * agreement-service-instance
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{nsi_uuid}/{sla_uuid}")
	public Response getViolation(@PathParam("nsi_uuid") String nsi_uuid, @PathParam("sla_uuid") String sla_uuid) {

		ResponseBuilder apiresponse = null;

		new db_operations();
		boolean connect = db_operations.connectPostgreSQL();
		if (connect == true) {
			db_operations.createTableViolations();
			JSONObject violations = db_operations.getViolationData(nsi_uuid, sla_uuid);
			db_operations.closePostgreSQL();

			apiresponse = Response.ok(violations);
			apiresponse.header("Content-Length", violations.toString().length());
			return apiresponse.status(200).build();

		} else {
			db_operations.closePostgreSQL();

			JSONObject error = new JSONObject();
			error.put("ERROR", "connecting to database");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());
			return apiresponse.status(404).build();

		}
	}

	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllViolationData() {

		ResponseBuilder apiresponse = null;

		new db_operations();
		boolean connect = db_operations.connectPostgreSQL();
		if (connect == true) {
			db_operations.createTableViolations();
			org.json.simple.JSONArray violations = db_operations.getAllViolationData();
			db_operations.closePostgreSQL();

			apiresponse = Response.ok(violations);
			apiresponse.header("Content-Length", violations.toString().length());
			return apiresponse.status(200).build();

		} else {
			db_operations.closePostgreSQL();

			JSONObject error = new JSONObject();
			error.put("ERROR: ", "connecting to database");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());
			return apiresponse.status(404).build();

		}
	}
	
	/**
	 * Get Nº SLA Agreements vs. Violations (in the last 24h / 7 days / 30 days)
	 */
	@SuppressWarnings({ "unchecked" })
	@Path("violationspercentage")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAgreementsVsViolationsPercentage(@QueryParam("d") int d) {

		ResponseBuilder apiresponse = null;
		JSONObject percentages = new JSONObject();

		// without date range
		if (d == 0) {
			db_operations db = new db_operations();
			db_operations.connectPostgreSQL();
			double totalAgreements = db.countTotalAgreements();
			double activeAgreements = db.countActiveAgreements();
			double violatedAgreements = db.countViolatedAgreements();
			db_operations.closePostgreSQL();

			if (totalAgreements > 0) {
				double percentage_violated = (violatedAgreements * 100) / totalAgreements ;
				double percentage_active = (activeAgreements * 100) / totalAgreements ;

				percentages.put("total_agreements", String.format("%.2f", totalAgreements));
				percentages.put("percentage_violated",String.format("%.2f", percentage_violated));
				percentages.put("percentage_active", String.format("%.2f", percentage_active));
			} else {
				percentages.put("total_agreements", String.valueOf(totalAgreements));
			}
		}

		// with date range
		else {
			db_operations db = new db_operations();
			db_operations.connectPostgreSQL();
			double totalAgreements = db.countTotalAgreementsDateRange(d);
			double activeAgreements = db.countActiveAgreementsDateRange(d);
			double violatedAgreements = db.countViolatedAgreementsDateRange(d);
			db_operations.closePostgreSQL();

			if (totalAgreements > 0) {
				double percentage_violated = (violatedAgreements * 100) / totalAgreements ;
				double percentage_active = (activeAgreements * 100) / totalAgreements ;
				
				percentages.put("total_agreements", String.format("%.2f", totalAgreements));
				percentages.put("percentage_violated",String.format("%.2f", percentage_violated));
				percentages.put("percentage_active", String.format("%.2f", percentage_active));
			} else {
				percentages.put("total_agreements", String.valueOf(totalAgreements));
			}
		}

		apiresponse = Response.ok((percentages));
		apiresponse.header("Content-Length", percentages.toJSONString().length());
		return apiresponse.status(200).build();

	}

}
