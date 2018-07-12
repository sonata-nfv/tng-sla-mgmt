package eu.tng.service_api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.simple.JSONObject;

import eu.tng.correlations.db_operations;

@Path("/violations")
@Consumes(MediaType.APPLICATION_JSON)
public class ViolationAPIS {

	/**
	 * api call in order to get a JSONObject with violations per
	 * agreement-service-instance
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{ns_uuid}/{sla_uuid}")
	public Response getViolation(@PathParam("ns_uuid") String ns_uuid, @PathParam("sla_uuid") String sla_uuid) {

		ResponseBuilder apiresponse = null;
		
		db_operations dbo = new db_operations();
		boolean connect = db_operations.connectPostgreSQL();
		if (connect == true) {
			db_operations.createTableViolations();
			JSONObject violations = db_operations.getViolationData(ns_uuid, sla_uuid);
			System.out.println("VIOLATIONS FROM VIOLATION API CLASS ==> " + violations);
			
			dbo.closePostgreSQL();
			apiresponse = Response.ok(violations);
			apiresponse.header("Content-Length", violations.toString().length());
			return apiresponse.status(200).build();

		} else {
			JSONObject error = new JSONObject();
			error.put("ERROR: ", "connecting to database");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());
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
			return apiresponse.status(200).build();

		} else {
			JSONObject error = new JSONObject();
			error.put("ERROR: ", "connecting to database");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());
			return apiresponse.status(404).build();

		}
	}

}
