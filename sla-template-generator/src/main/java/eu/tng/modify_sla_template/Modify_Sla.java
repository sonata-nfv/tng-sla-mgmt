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
package eu.tng.modify_sla_template;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.google.gson.Gson;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class Modify_Sla {

	/**
	 * Switch the state {published/unpublished} of a specific sla template when
	 * necessary
	 */
	public int switchState(String uuid) {
		int HttpResult = 0;
		try {
			URL url = new URL(
					"http://http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors/" + uuid
							+ "?state=unpublished");
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestProperty("Content-Type", "application/json");
			httpCon.setRequestMethod("PUT");
			HttpResult = httpCon.getResponseCode();

			OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
			out.write("Resource content");
			out.close();
			httpCon.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return HttpResult;
	}

	/*
	 * Method to switch state of the sla descriptorbefore editing : active->inactive
	 * 
	 */

	public int switchStatus(String uuid) {
		int HttpResult = 0;
		try {
			URL url = new URL("http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors/"
					+ uuid + "?status=inactive");
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestProperty("Content-Type", "application/json");
			httpCon.setRequestMethod("PUT");
			HttpResult = httpCon.getResponseCode();

			OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
			out.write("Resource content");
			out.close();
			httpCon.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return HttpResult;
	}

	/*
	 * Method for editing a field in the sla template descriptor
	 */
	@SuppressWarnings("unchecked")
	public String editField(JSONObject sla_template, String sla_uuid, String field, String value) {

		String FieldPath = "";
		// Remove root element inserted by the Catalogue
		Object slad = sla_template.get("slad");
		JSONObject jsonObject = (JSONObject) slad;

		// Increase versioning
		double version = Double.parseDouble((String) jsonObject.get("version"));
		version += 0.1;

		String new_version = String.valueOf(version);

		jsonObject.put("version", String.valueOf(new_version));

		String[] field_to_edit = field.split("=");
		String old_field = field_to_edit[0];
		String old_value = field_to_edit[1];

		switch (old_field) {
		case "name":
			FieldPath = ".name";
			break;
		case "description":
			FieldPath = ".description";
			break;
		case "valid_until":
			FieldPath = ".sla_template.valid_until";
			break;
		case "slo_name":
			FieldPath = ".sla_template.ns.objectives[*].[?(@.slo_name == " + old_value + ")].slo_name";
			break;
		case "slo_definition":
			FieldPath = ".sla_template.ns.objectives[*].[?(@.slo_definition == " + old_value + ")].slo_definition";
			break;
		case "slo_value":
			FieldPath = ".sla_template.ns.objectives[*].[?(@.slo_value == " + old_value + ")].slo_value";
			break;
		case "metric_definition":
			FieldPath = ".sla_template.ns.objectives[*].metric[*][?(@.metric_definition == " + old_value
					+ ")].metric_definition";
			break;
		case "parameter_name":
			FieldPath = ".sla_template.ns.objectives[*].metric[*].expression.parameters[*].[?(@.parameter_name == "
					+ old_value + ")].parameter_name";
			break;
		case "parameter_unit":
			FieldPath = ".sla_template.ns.objectives[*].metric[*].expression.parameters[*].[?(@.parameter_unit == "
					+ old_value + ")].parameter_unit";
			break;
		case "parameter_definition":
			FieldPath = ".sla_template.ns.objectives[*].metric[*].expression.parameters[*].[?(@.parameter_definition == "
					+ old_value + ")].parameter_definition";
			break;
		case "parameter_value":
			FieldPath = ".sla_template.ns.objectives[*].metric[*].expression.parameters[*].[?(@.parameter_value == "
					+ old_value + ")].parameter_value";
			break;
		}

		DocumentContext doc = JsonPath.parse(jsonObject).set(FieldPath, value);
		String newJson = new Gson().toJson(doc.read("$"));

		String new_uuid = PUTsla(newJson, sla_uuid);
		return new_uuid;
	}

	/*
	 * Method for modyfying a sla template descriptor (e.x Add a complete objective
	 */
	public String PUTsla(String jsonObj, String sla_uuid) {
		String new_uuid = "";
		JSONParser parser = new JSONParser();
		try {
			JSONObject edited_sla = (JSONObject) parser.parse(jsonObj);

			String url = "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors/"
					+ sla_uuid;
			URL object = new URL(url);

			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("PUT");

			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(edited_sla.toString());
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
				// System.out.println("" + sb.toString());
				JSONObject json_to_return = (JSONObject) parser.parse(sb.toString());
				new_uuid = (String) json_to_return.get("uuid");
			} else {
				System.out.println(con.getResponseMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new_uuid;
	}

}
