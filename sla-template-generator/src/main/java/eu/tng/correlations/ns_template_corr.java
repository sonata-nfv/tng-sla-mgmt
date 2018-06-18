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

package eu.tng.correlations;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ns_template_corr {

	/**
	 * Create a correlation between a network service and a sla template
	 */
	public void createNsTempCorr(String ns_uuid, String sla_uuid) {

		String tablename = "ns_template";

		db_operations dbo = new db_operations();

		dbo.connectPostgreSQL();
		dbo.createTableNSTemplate();
		dbo.insertRecord(tablename, ns_uuid, sla_uuid);
		dbo.closePostgreSQL();

	}

	/**
	 * Delete a correlation between a network service and a sla template
	 */
	public static void deleteNsTempCorr(String sla_uuid) {
		String tablename = "ns_template";
		db_operations dbo = new db_operations();
		dbo.connectPostgreSQL();
		dbo.deleteRecord(tablename, sla_uuid);
		dbo.closePostgreSQL();
	}

	/**
	 * Get an array with the ns uuids that have already accosiated an sla template
	 */
	@SuppressWarnings("unchecked")
	public JSONArray nsWithTemplate() {

		ArrayList<String> correlatedNS = new ArrayList<String>();

		db_operations dbo = new db_operations();

		// get the stored correlations
		dbo.connectPostgreSQL();
		JSONObject correlations = dbo.selectAllRecords("ns_template");
		JSONArray ns_template = (JSONArray) correlations.get("ns_template");
		for (int i = 0; i < ns_template.size(); i++) {
			JSONObject corr = (JSONObject) ns_template.get(i);
			correlatedNS.add((String) corr.get("ns_uuid"));
		}

		// remove duplicates
		Set<String> ns_uuids = new HashSet<String>();
		JSONArray tempArray = new JSONArray();
		for (int i = 0; i < correlatedNS.size(); i++) {

			String ns_uuid_temp = correlatedNS.get(i);
			if (ns_uuids.contains(ns_uuid_temp)) {
				continue;
			} else {
				ns_uuids.add(ns_uuid_temp);
				tempArray.add(correlatedNS.get(i));
			}
		}

		correlatedNS = tempArray; // assign temp to original
		System.out.println(correlatedNS);

		return (JSONArray) correlatedNS;

	}

	/**
	 * Get an array with the ns uuids that do not have accosiated an sla template
	 */
	public ArrayList<String> nsWithoutTemplate() {

		JSONArray existingNSArray = null;
		ArrayList<String> existingNSIDs = new ArrayList<String>();
		ArrayList<String> nsWithoutTemplate = new ArrayList<String>();

		// get the ns uuids that have correlated an sla template
		ArrayList<String> correlatedNSArray = new ArrayList<String>();
		correlatedNSArray = nsWithTemplate();

		// get all the available ns from the catalogue
		try {
			String url = "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/network-services";
			//String url = System.getenv("CATALOGUES_URL") + "network-services";
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
			existingNSArray = (JSONArray) parser.parse(response.toString());

			for (int i = 0; i < existingNSArray.size(); i++) {
				JSONObject ns_obj = (JSONObject) existingNSArray.get(i);
				existingNSIDs.add((String) ns_obj.get("uuid"));
			}
		} catch (Exception e) {
		}

		// create array list with ns uuids that have not sla templates yet
		for (int i = 0; i < existingNSIDs.size(); i++) {
			for (int j = 0; j < correlatedNSArray.size(); j++) {
				if (existingNSIDs.get(i).equals(correlatedNSArray.get(j))) {
					continue;
				} else {
					nsWithoutTemplate.add(existingNSIDs.get(i));
				}
			}

		}

		// remove duplicates
		Set<String> ns_uuids = new HashSet<String>();
		JSONArray tempArray = new JSONArray();
		for (int i = 0; i < nsWithoutTemplate.size(); i++) {
			String ns_uuid_temp = nsWithoutTemplate.get(i);
			if (ns_uuids.contains(ns_uuid_temp)) {
				continue;
			} else {
				ns_uuids.add(ns_uuid_temp);
				tempArray.add(nsWithoutTemplate.get(i));
			}
		}

		nsWithoutTemplate = tempArray; // assign temp to original

		return nsWithoutTemplate;
	}

}
