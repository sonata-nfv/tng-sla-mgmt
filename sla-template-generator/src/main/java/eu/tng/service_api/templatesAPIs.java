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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import eu.tng.template_gen.*;
import eu.tng.validations.TemplateValidation;
import eu.tng.correlations.*;

@Path("/templates")
@Consumes(MediaType.APPLICATION_JSON)
public class templatesAPIs {

	final static Logger logger = LogManager.getLogger();

	/**
	 * api call in order to get a list with all the existing sla templates
	 */
	@Produces(MediaType.TEXT_PLAIN)
	@GET
	public Response getTemplates(@Context HttpHeaders headers) {

		// Get Authorization Token
		try {
			// get jwt token
			String Authorization = headers.getRequestHeader("Authorization").get(0);
			String token = Authorization.substring(7);

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Get Authorization token";
			String message = "Authorization token feched succesfully! --> " + token;
			String status = String.valueOf(200);
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
			// decode jwt token
			JSONObject auth_info = JwtTokenDecode.DecodeToken(token);
			String template_initiator = (String) auth_info.get("username");

		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Get Authorization token";
			String message = "Authorization token not included in thw request --> " + e;
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

		ResponseBuilder apiresponse = null;
		try {
			String url = System.getenv("CATALOGUES_URL") + "slas/template-descriptors";
			// String url =
			// "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors";
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

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Getting SLA Templates";
			String message = "SLA Templates feched succesfully";
			String status = String.valueOf(200);
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			String sresponse = response.toString();

			apiresponse = Response.ok(sresponse);
			apiresponse.header("Content-Length", sresponse.length());
			return apiresponse.status(200).build();

		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Getting SLA Templates";
			String message = "Error Not Found";
			String status = String.valueOf(404);
			logger.error(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			JSONObject error = new JSONObject();
			error.put("ERROR:", "Not Found");
			apiresponse = Response.ok(error.toString());
			apiresponse.header("Content-Length", error.toString().length());
			return apiresponse.status(404).build();
		}
	}

	/**
	 * api call in order to get specific sla
	 */
	@Produces(MediaType.TEXT_PLAIN)
	@GET
	@Path("/{sla_uuid}")
	public Response getTemplate(@PathParam("sla_uuid") String sla_uuid, @Context HttpHeaders headers) {

		ResponseBuilder apiresponse = null;
		try {
			String url = System.getenv("CATALOGUES_URL") + "slas/template-descriptors/" + sla_uuid;
			// String url
			// ="http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors/"+sla_uuid;
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

			String cat_response = response.toString();
			apiresponse = Response.ok(cat_response);
			apiresponse.header("Content-Length", cat_response.length());

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Get specific SLA Template";
			String message = "SLA Template with uuid=" + sla_uuid + " found succesfully!";
			String status = String.valueOf(200);
			logger.error(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			return apiresponse.status(200).build();

		} catch (Exception e) {
			JSONObject error = new JSONObject();
			error.put("ERROR: ", "Not Found");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toString().length());

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String operation = "Get specific SLA Template";
			String type = "E";
			String message = "SLA Template with uuid=" + sla_uuid + " NOT Found";
			String status = String.valueOf(404);
			logger.error(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			return apiresponse.status(404).build();
		}

	}

	
	/**
	 * api call in order to generate a sla template
	 */
	@SuppressWarnings({ "null", "unchecked" })
	@Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
	@Consumes({"application/x-www-form-urlencoded", "application/json"})
	@POST
	public Response createTemplate(final MultivaluedMap<String, String> formParams, final CreateTemplateModel bodyParams) {

//		// Get Authorization Token
//		try {
//			// get jwt token
//			String Authorization = headers.getRequestHeader("Authorization").get(0);
//			String token = Authorization.substring(7);
//
//			// logging
//			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//			String timestamps = timestamp.toString();
//			String type = "I";
//			String operation = "Get Authorization token";
//			String message = "Authorization token feched succesfully! --> " + token;
//			String status = String.valueOf(200);
//			logger.info(
//					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
//					type, timestamps, operation, message, status);
//
//			// decode jwt token
//			JSONObject auth_info = JwtTokenDecode.DecodeToken(token);
//
//			System.out.println("[***] Auth info ==> " + auth_info);
//			try {
//				template_initiator = (String) auth_info.get("username");
//				System.out.println("[***] Auth info: Username  ==> " + template_initiator);
//
//			} catch (JSONException e) {
//				System.out.println(e);
//			}
//		} catch (Exception e) {
//			// logging
//			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//			String timestamps = timestamp.toString();
//			String type = "W";
//			String operation = "Get Authorization token";
//			String message = "Authorization token not included in thw request --> " + e;
//			String status = "";
//			logger.info(
//					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
//					type, timestamps, operation, message, status);
//		}

		ResponseBuilder apiresponse = null;

		String customer_name="admin";
		System.out.println(" [***] PARAMETERS FROM FORM ==> " + formParams.toString());
		System.out.println(" [***] PARAMETERS FROM BODY ==> " + bodyParams.toString());


//		String customer_name1 = "admin";		
//		customer_name1 = formParams.get("customer_name").get(0);
//		System.out.println("[1] USERNAME==> " + customer_name1);
//		List<String> customer_email = formParams.get("customer_email");
//		System.out.println("[2] customer_email==> " + customer_email);
		
		
		System.out.println("customer_name = " + bodyParams.param1);
		customer_name= bodyParams.param1 ;
	    System.out.println("customer_email = " + bodyParams.param2);
		
		
		List<String> nsd_uuid = formParams.get("nsd_uuid");
		List<String> templateName = formParams.get("templateName");
		System.out.println("[3] template name ==> " + templateName.get(0));
		List<String> expireDate = formParams.get("expireDate");

		List<String> service_licence_type = formParams.get("service_licence_type");
		List<String> allowed_service_instances = formParams.get("allowed_service_instances");
		List<String> service_licence_expiration_date = formParams.get("service_licence_expiration_date");

		// optional provider name
		String provider_name = "";
		try {
			List<String> provider_name_list = formParams.get("provider_name");
			provider_name = provider_name_list.get(0);
			System.out.println("[4] Provider name  ==> " + provider_name);
		} catch (Exception e) {
			provider_name = "default";
			System.out.println("[4] Provider name  ==> " + provider_name);
		}
		// optional flavour_name
		String dflavour_name = "";
		try {
			List<String> dflavour_name_list = formParams.get("dflavour_name");
			dflavour_name = dflavour_name_list.get(0);
			System.out.println("[5] Selected Deployment Flavour Name ==> " + dflavour_name);
		} catch (Exception e) {
			dflavour_name = "default";
			System.out.println("[5] Selected Deployment Flavour Name ==> " + dflavour_name);
		}

		ArrayList<String> guarantees = new ArrayList<String>();
		guarantees.addAll(formParams.get("guaranteeId"));

		CreateTemplate ct = new CreateTemplate();
		System.out.println("[6] templates API --> provider name: " + provider_name);
		JSONObject template = ct.createTemplate(nsd_uuid.get(0), templateName.get(0), expireDate.get(0), guarantees,
				service_licence_type.get(0), allowed_service_instances.get(0), service_licence_expiration_date.get(0),
				provider_name, customer_name);

		if (template == null) {
			String dr = null;
			JSONObject error = new JSONObject();
			error.put("ERROR:", "NSD don't found");
			apiresponse = Response.ok(error.toString());
			apiresponse.header("Content-Length", error.toString().length());

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "D";
			String operation = "Generate SLA Template";
			String message = "ERROR: Corresponding NSD = " + nsd_uuid.get(0) + " Not Found!";
			String status = String.valueOf(404);
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			return apiresponse.status(404).build();
		} else {

			// Make the validations
			TemplateValidation validation = new TemplateValidation();
			ArrayList<Boolean> valid_create_template = validation.validateCreateTemplate(templateName.get(0),
					expireDate.get(0), guarantees);

			if (valid_create_template.get(0) == false) {
				// invalid date format
				String dr = null;
				JSONObject error = new JSONObject();
				error.put("ERROR", "Invalid expire date format. The format should be dd/mm/YYY");
				apiresponse = Response.ok((Object) error);
				apiresponse.header("Content-Length", error.toString().length());

				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "D";
				String operation = "Validating the SLA Template";
				String message = "Error: Invalid expire date format. The format should be dd/mm/YYY";
				String status = String.valueOf(400);
				logger.debug(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);

				return apiresponse.status(400).build();

			} else if (valid_create_template.get(1) == false) {
				// invalid expire date
				String dr = null;
				JSONObject error = new JSONObject();
				error.put("ERROR", "The expire date is not a future date.");
				apiresponse = Response.ok(error.toString());
				apiresponse.header("Content-Length", error.toString().length());

				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "D";
				String operation = "Validating the SLA Template";
				String message = "Error: The expire date is not a future date.";
				String status = String.valueOf(400);
				logger.debug(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);

				return apiresponse.status(400).build();

			} else if (valid_create_template.get(2) == false) {
				// invalid guarantee terms
				String dr = null;
				JSONObject error = new JSONObject();
				error.put("ERROR",
						"There is a problem with the guarantee terms. You should select at least one guarantee id, and avoid duplicates.");
				apiresponse = Response.ok(error.toString());
				apiresponse.header("Content-Length", error.toString().length());

				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "D";
				String operation = "Validating the SLA Template";
				String message = "Error: There is a problem with the guarantee terms. You should select at least one guarantee id, and avoid duplicates.";
				String status = String.valueOf(400);
				logger.debug(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);

				return apiresponse.status(400).build();

			} else if (valid_create_template.get(3) == false) {
				// invalid template name
				String dr = null;
				JSONObject error = new JSONObject();
				error.put("ERROR", "Define a SLA Template Name");
				apiresponse = Response.ok(error.toString());
				apiresponse.header("Content-Length", error.toString().length());

				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "D";
				String operation = "Validating the SLA Template";
				String message = "Error: No SLA name specified.";
				String status = String.valueOf(400);
				logger.debug(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);

				return apiresponse.status(400).build();
			} else {
				/**
				 * If all the parameters are valid - continue to the creation of the template
				 **/
				Object createdTemplate = null;
				try {
					// String url =
					// "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors";
					String url = System.getenv("CATALOGUES_URL") + "slas/template-descriptors";
					URL object = new URL(url);

					HttpURLConnection con = (HttpURLConnection) object.openConnection();
					con.setDoOutput(true);
					con.setDoInput(true);
					con.setRequestProperty("Content-Type", "application/json");
					con.setRequestProperty("Accept", "application/json; charset=utf-8");
					con.setRequestMethod("POST");
					OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

					wr.write(template.toString());
					wr.flush();

					StringBuilder sb = new StringBuilder();
					int HttpResult = con.getResponseCode();

					if (HttpResult == HttpURLConnection.HTTP_CREATED) {
						BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
						int response_length = con.getInputStream().available();
						String line = null;
						while ((line = br.readLine()) != null) {
							sb.append(line + "\n");
						}

						// create correlation between ns and sla template among with licensing
						// information
						JSONParser parser = new JSONParser();
						createdTemplate = parser.parse(sb.toString());
						JSONObject responseSLA = (JSONObject) createdTemplate;
						String sla_uuid = (String) responseSLA.get("uuid");
						ns_template_corr nstemplcorr = new ns_template_corr();
						System.out.println("[*] sla_uuid (templatesAPI.class) ==> " + sla_uuid);
						System.out.println("[*] dflavour_name (templatesAPI.class) ==> " + dflavour_name);

						nstemplcorr.createNsTempCorr(nsd_uuid.get(0), sla_uuid, service_licence_type.get(0),
								service_licence_expiration_date.get(0), allowed_service_instances.get(0), "inactive", dflavour_name);

						br.close();

						// logging
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						String timestamps = timestamp.toString();
						String type = "I";
						String operation = "Generate the SLA Template";
						String message = "SLA was generated succesfully";
						String status = String.valueOf(201);
						logger.info(
								"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
								type, timestamps, operation, message, status);

						String jsonForResponse = "{\"uuid\" : \"" + sla_uuid + "\" , " + "\"Status\" : \"Success\"}";
						apiresponse = Response.ok(jsonForResponse);
						apiresponse.header("Content-Length", jsonForResponse.length());
						return apiresponse.status(201).build();

					} else {
						// conflict in uploading sla template to the catalogue
						JSONObject error = new JSONObject();
						error.put("ERROR", con.getResponseMessage());
						apiresponse = Response.ok(error.toString());
						apiresponse.header("Content-Length", error.toString().length());

						// logging
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						String timestamps = timestamp.toString();
						String type = "I";
						String operation = "Generate the SLA Template";
						String message = "Error uploading to Catalogue : " + con.getResponseMessage();
						String status = String.valueOf(400);
						logger.warn(
								"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
								type, timestamps, operation, message, status);

						return apiresponse.status(400).build();
					}
				} catch (Exception e) {
					String dr = null;
					JSONObject error = new JSONObject();
					error.put("ERROR", "while uploading SLA Template");
					apiresponse = Response.ok(error.toString());
					apiresponse.header("Content-Length", error.toString().length());

					// logging
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					String timestamps = timestamp.toString();
					String type = "W";
					String operation = "Generate the SLA Template";
					String message = "Error uploading to Catalogue : URL invalid";
					String status = String.valueOf(404);
					logger.warn(
							"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
							type, timestamps, operation, message, status);

					return apiresponse.status(404).build();
				}
			}

		}

	}

	/**
	 * api call in order to delete an sla template
	 */
	@SuppressWarnings("static-access")
	@Path("/{sla_uuid}")
	@Produces(MediaType.TEXT_PLAIN)
	@DELETE
	public Response deleteTemplate(@PathParam("sla_uuid") String sla_uuid) {

		ResponseBuilder apiresponse = null;
		String dr = null;
		HttpURLConnection httpURLConnection = null;
		URL url = null;

		db_operations dbo = new db_operations();
		dbo.connectPostgreSQL();
		dbo.createTableCustSla();
		int counter = dbo.countAgreementCorrelationPeriD(sla_uuid);
		dbo.closePostgreSQL();

		if (counter != 0) {
			dr = ("ERROR: SLA Template cannot be deleted because it is associated with an instantiated NS.");
			apiresponse = Response.ok();
			apiresponse.header("Content-Length", (dr.length()));

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Delete an SLA Template";
			String message = "ERROR: This SLA Template cannot be deleted because it is associated with an instantiated NS.";
			String status = String.valueOf(400);
			logger.warn(
					"{\"type\":\"I\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					timestamps, operation, message, status);

			return apiresponse.status(400).entity(dr).build();
		} else {
			try {
				/*
				 * url = new URL(
				 * "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors/"
				 * + sla_uuid);
				 */
				url = new URL(System.getenv("CATALOGUES_URL") + "slas/template-descriptors/" + sla_uuid);

				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestProperty("Content-Type", "application/json");
				httpURLConnection.setRequestMethod("DELETE");

				if (httpURLConnection.getResponseCode() == 404) {
					return Response.status(404).entity("SLA uuid Not Found").build();

				} else {
					// delete all correlations with the deleted sla template from postgreSQL table
					ns_template_corr nstemplcorr = new ns_template_corr();
					nstemplcorr.deleteNsTempCorr(sla_uuid);
					dr = ("SLA: " + sla_uuid + " deleted succesfully");
					apiresponse = Response.ok();
					apiresponse.header("Content-Length", (dr.length()));

					// logging
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					String timestamps = timestamp.toString();
					String type = "I";
					String operation = "Delete an SLA Template";
					String message = "SLA Template with uuid = " + sla_uuid + " deleted succesfully";
					String status = String.valueOf(200);
					logger.info(
							"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
							type, timestamps, operation, message, status);

					return apiresponse.status(200).entity(dr).build();

				}

			} catch (Exception e) {
				JSONObject error = new JSONObject();
				error.put("ERROR", "URL Not Found");
				apiresponse = Response.ok(error.toString());
				apiresponse.header("Content-Length", error.toString().length());

				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "W";
				String operation = "Delete an SLA Template";
				String message = "Error uploading to Catalogue : URL invalid";
				String status = String.valueOf(404);
				logger.warn(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);

				return apiresponse.status(404).build();

			}

		}

	}

}