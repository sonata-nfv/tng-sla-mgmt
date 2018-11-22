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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.jersey.core.util.Base64;

import eu.tng.template_gen.*;
import eu.tng.validations.TemplateValidation;
import eu.tng.correlations.*;

@Path("/templates")
@Consumes(MediaType.APPLICATION_JSON)
public class templatesAPIs {

	final Logger logger = LogManager.getLogger("SLAM_Logger");

	public static void main(String[] args) {
		final Logger log = LogManager.getLogger();
		JSONObject jsonObject = null;
		JSONParser parser = new JSONParser();

		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println("Timestamp ===> " + timestamp);
		ThreadContext.put("type", "I");
		ThreadContext.put("timestamp", timestamp.toString());
		ThreadContext.put("operation", "Get SLA Templates");
		ThreadContext.put("status", "200");

		log.info("Available SLA Templates1");
		
		try {
			File file = new File("src\\main\\resources\\logs.log");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append("\n");
			}
			fileReader.close();
			System.out.println(stringBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		//log.warn("Available SLA Templates1");

		//log.error("Available SLA Templates1");


		
		//log.error("Available SLA Templates2");
		//log.warn("Available SLA Templates3");

		ThreadContext.clearAll();

	}

	/**
	 * api call in order to get a list with all the existing sla templates
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getTemplates() {
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
			System.out.println("Timestamp ===> " + timestamp);
			ThreadContext.put("type", "I");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get SLA Templates");
			ThreadContext.put("status", "200");
			logger.info("Available SLA Templates1");
			logger.error("Available SLA Templates2");
			logger.warn("Available SLA Templates3");
			ThreadContext.clearAll();

			JSONParser parser = new JSONParser();
			Object existingTemplates = parser.parse(response.toString());
			apiresponse = Response.ok((Object) existingTemplates);
			apiresponse.header("Content-Length", response.length());
			return apiresponse.status(200).build();

		} catch (Exception e) {

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			ThreadContext.put("type", "E");
			ThreadContext.put("timestamp", timestamp.toString());
			ThreadContext.put("operation", "Get SLA Templates");
			ThreadContext.put("status", apiresponse.status(404).toString());
			logger.error("Not found");
			ThreadContext.clearAll();

			JSONObject error = new JSONObject();
			error.put("ERROR: ", "Not Found");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());
			return apiresponse.status(404).build();
		}

	}

	/**
	 * api call in order to get specific sla
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/{sla_uuid}")
	public Response getTemplate(@PathParam("sla_uuid") String sla_uuid) {
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
			JSONParser parser = new JSONParser();
			Object existingTemplates = parser.parse(response.toString());
			apiresponse = Response.ok((Object) existingTemplates);
			apiresponse.header("Content-Length", response.length());
			return apiresponse.status(200).build();

		} catch (Exception e) {
			JSONObject error = new JSONObject();
			error.put("ERROR: ", "Not Found");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());
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
	public Response createTemplate(final MultivaluedMap<String, String> formParams) {

		ResponseBuilder apiresponse = null;

		List<String> nsd_uuid = formParams.get("nsd_uuid");
		List<String> expireDate = formParams.get("expireDate");
		List<String> templateName = formParams.get("templateName");

		ArrayList<String> guarantees = new ArrayList<String>();
		guarantees.addAll(formParams.get("guaranteeId"));

		// call CreateTemplate method
		CreateTemplate ct = new CreateTemplate();
		JSONObject template = ct.createTemplate(nsd_uuid.get(0), templateName.get(0), expireDate.get(0), guarantees);

		if (template == null) {
			String dr = null;
			JSONObject error = new JSONObject();
			error.put("ERROR: ", "NSD don't found");
			apiresponse = Response.ok((Object) error);
			apiresponse.header("Content-Length", error.toJSONString().length());
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
				error.put("ERROR: ", "Invalid expire date format. The format should be dd/mm/YYY");
				apiresponse = Response.ok((Object) error);
				apiresponse.header("Content-Length", error.toJSONString().length());
				return apiresponse.status(400).build();

			} else if (valid_create_template.get(1) == false) {
				// invalid expire date
				String dr = null;
				JSONObject error = new JSONObject();
				error.put("ERROR: ", "The expire date is not a future date.");
				apiresponse = Response.ok((Object) error);
				apiresponse.header("Content-Length", error.toJSONString().length());
				return apiresponse.status(400).build();

			} else if (valid_create_template.get(2) == false) {
				// invalid guarantee terms
				String dr = null;
				JSONObject error = new JSONObject();
				error.put("ERROR: ",
						"There is a problem with the guarantee terms. You should select at least one guarantee id, and avoid duplicates.");
				apiresponse = Response.ok((Object) error);
				apiresponse.header("Content-Length", error.toJSONString().length() - 2);
				return apiresponse.status(400).build();
			} else if (valid_create_template.get(3) == false) {
				// invalid template name
				String dr = null;
				JSONObject error = new JSONObject();
				error.put("ERROR: ", "Define a SLA Template Name");
				apiresponse = Response.ok((Object) error);
				apiresponse.header("Content-Length", error.toJSONString().length() - 2);
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

						// create correlation between ns and sla template
						JSONParser parser = new JSONParser();
						createdTemplate = parser.parse(sb.toString());
						JSONObject responseSLA = (JSONObject) createdTemplate;
						String sla_uuid = (String) responseSLA.get("uuid");

						ns_template_corr nstemplcorr = new ns_template_corr();
						nstemplcorr.createNsTempCorr(nsd_uuid.get(0), sla_uuid);

						br.close();

						apiresponse = Response.ok(responseSLA);
						apiresponse.header("Content-Length", response_length);
						return apiresponse.status(201).build();

					} else {
						// conflict in uploading sla template to the catalogue
						System.out.println(con.getResponseMessage());
						JSONObject error = new JSONObject();
						error.put("ERROR: ", con.getResponseMessage());
						apiresponse = Response.ok((Object) error);
						apiresponse.header("Content-Length", error.toJSONString().length());
						return apiresponse.status(400).build();
					}
				} catch (Exception e) {
					String dr = null;
					JSONObject error = new JSONObject();
					error.put("ERROR: ", "while uploding SLA Template");
					apiresponse = Response.ok((Object) error);
					apiresponse.header("Content-Length", error.toJSONString().length());
					return apiresponse.status(404).build();
				}
			}

		}

	}

	/**
	 * api call in order to edit an already existing sla template
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
				System.out.println(httpURLConnection.getResponseCode());

				if (httpURLConnection.getResponseCode() == 404) {
					return Response.status(404).entity("SLA uuid Not Found").build();

				} else {
					// delete all correlations with the deleted sla template from postgreSQL table
					ns_template_corr nstemplcorr = new ns_template_corr();
					nstemplcorr.deleteNsTempCorr(sla_uuid);
					dr = ("SLA: " + sla_uuid + " deleted succesfully");
					apiresponse = Response.ok();
					apiresponse.header("Content-Length", (dr.length()));
					return apiresponse.status(200).entity(dr).build();

				}

			} catch (Exception e) {
				JSONObject error = new JSONObject();
				error.put("ERROR: ", "URL Not Found");
				apiresponse = Response.ok((Object) error);
				apiresponse.header("Content-Length", error.toJSONString().length());
				return apiresponse.status(404).build();

			}

		}

	}

}