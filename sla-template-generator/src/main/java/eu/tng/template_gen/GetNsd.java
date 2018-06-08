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
package eu.tng.template_gen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetNsd {

	public void getNSD(String nsId) {
		Nsd setNsdFields = new Nsd();
		ArrayList<String> mon_desc_list = new ArrayList<String>();
		ArrayList<String> mon_metric_list = new ArrayList<String>();
		try {
			URL url = new URL(System.getenv("CATALOGUES_URL")+"/network-services/" + nsId);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() != 200) {
				System.out.println("Failed : HTTP error code : NSD not FOUND");
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;

			while ((output = br.readLine()) != null) {
				JSONParser parser = new JSONParser();
				try {

					Object obj = parser.parse(output);
					JSONObject jsonObject = (JSONObject) obj;
					if (jsonObject.containsKey("nsd")) {
						JSONObject nsd = (JSONObject) jsonObject.get("nsd");

						// get nsd name
						String name = (String) nsd.get("name");
						setNsdFields.setName(name);

						// get nsd vendor
						String vendor = (String) nsd.get("vendor");
						setNsdFields.setVendor(vendor);

						// get nsd version
						String version = (String) nsd.get("version");
						setNsdFields.setVersion(version);

						// get nsd description
						String description = (String) nsd.get("description");
						setNsdFields.setDescription(description);

					}

				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
			conn.disconnect();
		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}
