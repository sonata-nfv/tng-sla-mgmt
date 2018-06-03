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
import javax.ws.rs.core.UriInfo;

import org.codehaus.jettison.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import eu.tng.template_gen.*;
import eu.tng.modify_sla_template.*;

@Path("/templates")
@Consumes(MediaType.APPLICATION_JSON)
public class templatesAPIs {

	/**
	 * api call in order to get a list twith all the existing sla templates
	 * http://localhost:8080/tng-sla-mgmt/api/slas/v1/templates
	 * 
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getTemplates() {

		try {
			String url = "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors";
			URL object = new URL(url);

			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("GET");

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

			return Response.status(200).entity(existingTemplates).build();

		} catch (Exception e) {

			return Response.status(400).build();
		}

	}

	/**
	 * api call in order to generate a sla template mendatory input parameters from
	 * the user: nsId, providerId, templateName, expireDate e.g. *
	 * http://localhost:8080/tng-sla-mgmt/api/slas/v1/templates/{ns_uuid}?templateName=<>&expireDate=<>
	 * 
	 */

	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/x-www-form-urlencoded")
	@POST
	@Path("/{nsd_uuid}")
	public Response createTemplate(@PathParam("nsd_uuid") String nsd_uuid, @Context UriInfo info,
			final MultivaluedMap<String, String> formParams) {

		String templateName = info.getQueryParameters().getFirst("templateName");
		String expireDate = info.getQueryParameters().getFirst("expireDate");

		ArrayList<String> guarantees = new ArrayList<String>();
		guarantees.addAll(formParams.get("guaranteeId"));

		// call CreateTemplate method
		CreateTemplate ct = new CreateTemplate();
		JSONObject template = ct.createTemplate(nsd_uuid, templateName, expireDate, guarantees);
		System.out.println("FINAL" + template);

		try {
			String url = "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors";
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
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				System.out.println("" + sb.toString());
			} else {
				System.out.println(con.getResponseMessage());
			}
		} catch (Exception e) {
		}
		return Response.status(200).entity(template).build();

	}

	/**
	 * api call in order to edit an already existing sla template mendatory input
	 * parameters from the user: uuid, field, old_value, value.
	 * http://localhost:8080/tng-sla-mgmt/api/slas/v1/edit/templates/{sla_uuid}?field=<>&old_value=<>&value=<>
	 * 
	 */

	@SuppressWarnings("static-access")
	@DELETE
	@Path("/{sla_uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTemplate(@PathParam("sla_uuid") String sla_uuid) {
		// Modify_Sla ms = new Modify_Sla();
		// Modify_Sla.switchStatus(sla_uuid);

		// String request =
		// "http://pre-int-sp-ath.5gtango.eu:4011/api/catalogues/v2/slas/template-descriptors/"+sla_uuid;
		// System.out.println(request);

		URL url = null;
		try {
			url = new URL(
					"http://pre-int-sp-ath.5gtango.eu:4011/api/catalogues/v2/slas/template-descriptors/" + sla_uuid);
		} catch (MalformedURLException exception) {
			exception.printStackTrace();
		}
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.setRequestMethod("DELETE");
			System.out.println(httpURLConnection.getResponseCode());
		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		return null;

	}

	/**
	 * api call in order to edit an already existing sla template mendatory input
	 * parameters from the user: uuid, field, old_value, value.
	 * http://localhost:8080/tng-sla-mgmt/api/slas/v1/edit/templates/{sla_uuid}?field=<>&old_value=<>&value=<>
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
	 * api call in order to modify an already existing sla template by adding new
	 * slo mendatory input parameters from the user: uuid, field, old_value, value.
	 * http://localhost:8080/tng-sla-mgmt/api/slas/v1/edit/stemplates/modify/{uuid}?objectives=[]&slo_value=[]&slo_definition=[]&slo_unit=[]&metric=[]&expression=[]&expression_unit=[]&rate=[]&parameter_name=[]&parameter_value=[]&parameter_definition=[]&parameter_unit=[]
	 * 
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

	/**
	 * api call in order to get a predifined list with Service Guarantees
	 * http://localhost:8080/tng-sla-mgmt/api/slas/v1/templates/guaranteesList
	 * 
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
}
