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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import eu.tng.template_gen.*;
import eu.tng.correlations.db_operations;
import eu.tng.correlations.ns_template_corr;
import eu.tng.modify_sla_template.*;

@Path("/templates")
@Consumes(MediaType.APPLICATION_JSON)
public class templatesAPIs {

	/**
	 * api call in order to get a list with all the existing sla templates
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getTemplates() {
		ResponseBuilder apiresponse = null;
		try {
			String url = System.getenv("CATALOGUES_URL")+"slas/template-descriptors";
			//String url = "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors";
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
			return apiresponse.build();

		} catch (Exception e) {

			return apiresponse.status(400).build();
		}

	}
	
	/**
	 * api call in order to get a list with all the existing sla templates
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/{sla_uuid}")
	public Response getTemplate(@PathParam("sla_uuid") String sla_uuid) {
		ResponseBuilder apiresponse = null;
		try {
			String url = System.getenv("CATALOGUES_URL")+"slas/template-descriptors/"+sla_uuid;
			//String url = "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors/"+sla_uuid;
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
			return apiresponse.build();

		} catch (Exception e) {

			return apiresponse.status(400).build();
		}

	}

	/**
	 * api call in order to generate a sla template
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/x-www-form-urlencoded")
	@POST
	public Response createTemplate(final MultivaluedMap<String, String> formParams) {

		ResponseBuilder apiresponse = null;
		
		List<String> nsd_uuid = formParams.get("nsd_uuid") ;
		List<String> expireDate = formParams.get("expireDate") ;
		List<String> templateName = formParams.get("templateName") ;

		ArrayList<String> guarantees = new ArrayList<String>();
		guarantees.addAll(formParams.get("guaranteeId"));

		// call CreateTemplate method
		CreateTemplate ct = new CreateTemplate();
		JSONObject template = ct.createTemplate(nsd_uuid.get(0), templateName.get(0), expireDate.get(0), guarantees);
		System.out.println("Created SLA Template: " + template);

		Object createdTemplate = null;
		try {
			//String url = "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors";
			String url = System.getenv("CATALOGUES_URL") + "slas/template-descriptors";
			URL object = new URL(url);

			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("POST");
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(template.toString());
			wr.flush();

			StringBuilder sb = new StringBuilder();
			int HttpResult = con.getResponseCode();

			if (HttpResult == HttpURLConnection.HTTP_CREATED) {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");

				}
				System.out.println("UPLOADED TO CAT:" + sb.toString());

				// create correlation between ns and sla template
				JSONParser parser = new JSONParser();
				createdTemplate = parser.parse(sb.toString());
				JSONObject responseSLA = (JSONObject) createdTemplate;
				String sla_uuid = (String) responseSLA.get("uuid");
				ns_template_corr nstemplcorr = new ns_template_corr();
				nstemplcorr.createNsTempCorr(nsd_uuid.get(0), sla_uuid);

				br.close();
				
				apiresponse = Response.ok(responseSLA);
				apiresponse.header("Content-Length", responseSLA.toString().length());
				return apiresponse.status(201).build();

			} else {
				System.out.println(con.getResponseMessage());
			}
		} 
		catch (Exception e) {
		}
		//return apiresponse.status(400).build();
		return null;
		
	}

	/**
	 * api call in order to edit an already existing sla template
	 */

	@DELETE
	@Path("/{sla_uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTemplate(@PathParam("sla_uuid") String sla_uuid) {

		ResponseBuilder apiresponse = null;
		String dr = null;

		URL url = null;
		try {
			url = new URL("http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors/" + sla_uuid);
			//url = new URL(System.getenv("CATALOGUES_URL") + "slas/template-descriptors/" + sla_uuid);
		} catch (MalformedURLException exception) {
			exception.printStackTrace();
		}
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.setRequestMethod("DELETE");
			System.out.println(httpURLConnection.getResponseCode());

			// delete all correlations with the deleted sla template from postgreSQL table
			ns_template_corr nstemplcorr = new ns_template_corr();
			nstemplcorr.deleteNsTempCorr(sla_uuid);
			dr = "SLA Template with uuid: " + sla_uuid + " deleted";

		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		apiresponse = Response.ok();
		apiresponse.header("Content-Length", (dr.length()));
		return apiresponse.status(200).entity(dr).build();

	}

	/**
	 * api call in order to edit an already existing sla template
	 * 
	 */

	@SuppressWarnings("static-access")
	@PUT
	@Path("/{sla_uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response EditTemplate(@PathParam("sla_uuid") String sla_uuid, @Context UriInfo info) {

		// String uuid = info.getQueryParameters().getFirst("uuid");
		String field = info.getQueryParameters().getFirst("field");
		String old_value = info.getQueryParameters().getFirst("old_value");
		String value = info.getQueryParameters().getFirst("value");

		String field_to_edit = (field + "=" + old_value);

		Sla_Editor se = new Sla_Editor();
		String new_sla_uuid = se.Edit_value(sla_uuid, field_to_edit, value);

		Get_Sla_Template mt = new Get_Sla_Template();
		JSONObject edited_template = mt.Get_Sla(new_sla_uuid);

		return Response.status(200).entity(edited_template).build();
	}

	/**
	 * api call in order to modify an already existing sla template
	 */

	@PUT
	@Path("/customize/{sla_uuid}")
	public Response CustomizeTemplate(@PathParam("sla_uuid") String sla_uuid,
			@QueryParam("objectives") List<String> objectives, @QueryParam("slo_value") List<String> slo_value,
			@QueryParam("slo_value") List<String> slo_definition, @QueryParam("slo_unit") List<String> slo_unit,
			@QueryParam("metric") List<String> metric, @QueryParam("expression") List<String> expression,
			@QueryParam("expression_unit") List<String> expression_unit, @QueryParam("rate") List<String> rate,
			@QueryParam("parameter_unit") List<String> parameter_unit,
			@QueryParam("parameter_definition") List<String> parameter_definition,
			@QueryParam("parameter_value") List<String> parameter_value,
			@QueryParam("parameter_name") List<String> parameter_name) {

		@SuppressWarnings("unused")
		Sla_Editor se = new Sla_Editor();
		String new_uuid = Sla_Editor.Add_Fields(sla_uuid, objectives, slo_value, slo_definition, slo_unit, metric,
				expression, expression_unit, rate, parameter_unit, parameter_definition, parameter_value,
				parameter_name);

		Get_Sla_Template mt = new Get_Sla_Template();
		JSONObject modified_template = mt.Get_Sla(new_uuid);

		return Response.status(200).entity(modified_template).build();
	}
}