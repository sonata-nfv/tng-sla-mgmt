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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{nsi_uuid}/{sla_uuid}")
	public Response getViolation(@PathParam("nsi_uuid") String nsi_uuid, @PathParam("sla_uuid") String sla_uuid) {

		ResponseBuilder apiresponse = null;

		db_operations dbo = new db_operations();
		boolean connect = db_operations.connectPostgreSQL();
		if (connect == true) {
			db_operations.createTableViolations();
			JSONObject violations = db_operations.getViolationData(nsi_uuid, sla_uuid);
			System.out.println("VIOLATIONS FROM VIOLATION API CLASS ==> " + violations);
			dbo.closePostgreSQL();

			apiresponse = Response.ok(violations);
			apiresponse.header("Content-Length", violations.toString().length());

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Getting Violations";
			String message = "SLA Violations feched succesfully";
			String status = String.valueOf(200);
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			return apiresponse.status(200).build();

		} else {
			dbo.closePostgreSQL();

			JSONObject error = new JSONObject();
			error.put("ERROR: ", "connecting to database");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Getting Violations";
			String message = "Error connecting to Database in order to get violation records";
			String status = String.valueOf(404);
			logger.error(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			return apiresponse.status(404).build();

		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllViolationData() {

		ResponseBuilder apiresponse = null;

		db_operations dbo = new db_operations();
		boolean connect = db_operations.connectPostgreSQL();
		if (connect == true) {
			db_operations.createTableViolations();
			org.json.simple.JSONArray violations = db_operations.getAllViolationData();
			System.out.println("VIOLATIONS FROM VIOLATION API CLASS ==> " + violations);
			dbo.closePostgreSQL();

			apiresponse = Response.ok(violations);
			apiresponse.header("Content-Length", violations.toString().length());

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Getting Violations";
			String message = "SLA Violations feched succesfully";
			String status = String.valueOf(200);
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			return apiresponse.status(200).build();

		} else {
			dbo.closePostgreSQL();

			JSONObject error = new JSONObject();
			error.put("ERROR: ", "connecting to database");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Getting Violations";
			String message = "Error connecting to Database in order to get violation records";
			String status = String.valueOf(404);
			logger.error(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			return apiresponse.status(404).build();

		}
	}

}
