package eu.tng.service_api;

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
import eu.tng.correlations.cust_sla_corr;
import eu.tng.correlations.db_operations;

@Path("/agreements")
@Consumes(MediaType.APPLICATION_JSON)
public class AgreementsAPIs {

	/**
	 * api call in order to get a list with all the existing agreements
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getAgreements() {

		ResponseBuilder apiresponse = null;

		db_operations dbo = new db_operations();

		dbo.connectPostgreSQL();
		dbo.createTableCustSla();
		JSONObject correlations = dbo.selectAllRecords("cust_sla");
		dbo.closePostgreSQL();

		apiresponse = Response.ok((Object) correlations);
		apiresponse.header("Content-Length", correlations.toString().length());
		return apiresponse.status(200).build();
	}

	/**
	 * delete an agreement
	 */
	@DELETE
	@Path("/{sla_uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAgreement(@PathParam("sla_uuid") String sla_uuid) {

		ResponseBuilder apiresponse = null;

		new cust_sla_corr();
		cust_sla_corr.deleteCorr(sla_uuid);

		String dr = "Agreement deleted";
		apiresponse = Response.ok(dr);
		apiresponse.header("Content-Length", dr.toString().length());
		return apiresponse.status(200).build();

	}

	/**
	 * api call in order to get a list with all the existing agreements per NS
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("service/{ns_uuid}")
	public Response getAgreementsPerNS(@PathParam("ns_uuid") String ns_uuid) {

		ResponseBuilder apiresponse = null;

		db_operations dbo = new db_operations();

		dbo.connectPostgreSQL();
		// dbo.createTableCustSla();
		JSONObject agrPerNs = dbo.selectAgreementPerNS(ns_uuid);
		dbo.closePostgreSQL();

		apiresponse = Response.ok(agrPerNs);
		apiresponse.header("Content-Length", agrPerNs.toString().length());
		return apiresponse.status(200).build();
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
		dbo.connectPostgreSQL();

		// dbo.createTableCustSla();
		JSONObject agrPerNs = dbo.selectAgreementPerCustomer(cust_uuid);
		dbo.closePostgreSQL();

		apiresponse = Response.ok(agrPerNs);
		apiresponse.header("Content-Length", agrPerNs.toString().length());
		return apiresponse.status(200).build();
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
		
		apiresponse = Response.ok(gt);
		apiresponse.header("Content-Length", gt.toString().length());
		return apiresponse.status(200).build();
		
	}

}
