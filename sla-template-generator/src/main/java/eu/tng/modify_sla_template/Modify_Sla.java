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
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

public class Modify_Sla {

	public int switchState(String uuid) {
		int HttpResult = 0;
		try {
			URL url = new URL("http://83.212.238.144:4011/catalogues/api/v2/sla/template-descriptors/" + uuid
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
	
	public int switchStatus(String uuid) {
		int HttpResult = 0;
		try {
			URL url = new URL("http://83.212.238.144:4011/catalogues/api/v2/sla/template-descriptors/" + uuid
					+ "?status=inactive");
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

	public void editField(JSONObject sla_template, String sla_uuid, ArrayList<String> fields,
			ArrayList<String> values) {

		// Remove root element inserted by the Catalogue
		Object slad = sla_template.get("slad");
		JSONObject jsonObject = (JSONObject) slad;

		// Increase versioning
		double version = Double.parseDouble((String) jsonObject.get("version"));
		version += 0.1;

		DocumentContext doc = JsonPath.parse(jsonObject).set(
				".sla_template.ns.objectives[*].metric[*].expression.parameters[*].[?(@.parameter_value == 70)].parameter_value",
				"90");

		String newJson = new Gson().toJson(doc.read("$"));

		System.out.println(newJson);

		/*
		 * jsonObject.put("version", String.valueOf(version));
		 * 
		 * jsonObject.put(field, value);
		 * 
		 * System.out.println(jsonObject);
		 * 
		 * List<String> authors = JsonPath.read(jsonObject,
		 * ".sla_template.ns.objectives[*].metric[*].expression.parameters[*].parameter_value"
		 * );
		 * 
		 * System.out.println(authors.toString());
		 * 
		 * 
		 * for (String field : fields) { System.out.println(field); }
		 * 
		 * for (String value : values) { System.out.println(value); }
		 */

	}

	public void PUTsla(JSONObject edited_sla, String sla_uuid) {

		try {
			String url = "http://83.212.238.144:4011/catalogues/api/v2/sla/template-descriptors/" + sla_uuid;
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
				System.out.println("" + sb.toString());
			} else {
				System.out.println(con.getResponseMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
