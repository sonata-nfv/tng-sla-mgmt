package eu.tng.service_api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
		db_operations.connectPostgreSQL();
		db_operations.createTableCustSla();
		JSONObject correlations = db_operations.getAgreements();
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
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteAgreement(@PathParam("sla_uuid") String sla_uuid) {

		ResponseBuilder apiresponse = null;
		new cust_sla_corr();
		int status = cust_sla_corr.deleteCorr(sla_uuid);
		if (status == 200) {
			String dr = "Agreement deleted";
			apiresponse = Response.ok(dr);
			apiresponse.header("Content-Length", dr.toString().length());
			return apiresponse.status(200).build();
		} else {
			String dr = "Error connecting to database";
			apiresponse = Response.ok(dr);
			apiresponse.header("Content-Length", dr.toString().length());
			return apiresponse.status(404).build();
		}

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
		boolean connect = db_operations.connectPostgreSQL();
		if (connect == true) {
			db_operations.createTableCustSla();
			JSONObject agrPerNs = dbo.selectAgreementPerNS(ns_uuid);
			dbo.closePostgreSQL();
			apiresponse = Response.ok(agrPerNs);
			apiresponse.header("Content-Length", agrPerNs.toString().length());
			return apiresponse.status(200).build();

		} else {
			apiresponse = Response.ok(null);
			apiresponse.header("Content-Length", 0);
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
			apiresponse = Response.ok(null);
			apiresponse.header("Content-Length", 0);
			return apiresponse.status(404).build();
		}

	}

	/**
	 * api call in order to get a list with all the existing agreements per Customer
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/{sla_uuid}")
	public Response getAgreementDetails(@PathParam("sla_uuid") String sla_uuid) {

		ResponseBuilder apiresponse = null;
		try {
			 String url = System.getenv("CATALOGUES_URL") + "slas/template-descriptors/" +
			 sla_uuid;
//			String url = "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors/"
//					+ sla_uuid;
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
			JSONParser parser = new JSONParser();
			Object existingTemplates = parser.parse(response.toString());
			apiresponse = Response.ok((Object) existingTemplates);
			apiresponse.header("Content-Length", response.length());
			return apiresponse.status(200).build();

		} catch (Exception e) {
			String nf = "SLA Not Found";
			apiresponse = Response.ok(nf);
			apiresponse.header("Content-Length", nf.length());
			return apiresponse.status(404).entity(nf).build();
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

		apiresponse = Response.ok(gt);
		apiresponse.header("Content-Length", gt.toString().length());
		return apiresponse.status(200).build();

	}

}
