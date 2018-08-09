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

package eu.tng.correlations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import eu.tng.template_gen.GetNsd;
import eu.tng.template_gen.Nsd;

public class cust_sla_corr {

	/**
	 * Create a correlation between an instatiated network service, a customer and a
	 * sla
	 * 
	 * @param correlation_id
	 */
	public static void createCustSlaCorr(String sla_uuid, String sla_name, String sla_status, String ns_uuid,
			String ns_name, String cust_uuid, String cust_email, String inst_status, String correlation_id) {

		db_operations dbo = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableCustSla();
		dbo.insertRecordAgreement(ns_uuid, ns_name, sla_uuid, sla_name, sla_status, cust_email, cust_uuid, inst_status,
				correlation_id);
		dbo.closePostgreSQL();

	}

	public ArrayList getSLAdetails(String sla_uuid) {
		ArrayList<String> details = new ArrayList<String>();

		try {
//			String url = "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors/"
//					+ sla_uuid;
//			URL object = new URL(url);

			 URL url = new URL(System.getenv("CATALOGUES_URL") +
			 "slas/template-descriptors/" + sla_uuid);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() != 200) {
				System.out.println("Failed : HTTP error code : SLA not FOUND");
				details = null;
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;

				while ((output = br.readLine()) != null) {
					JSONParser parser = new JSONParser();
					try {
						Object obj = parser.parse(output);
						JSONObject jsonObject = (JSONObject) obj;
						// get slad status
						String status = (String) jsonObject.get("status");
						details.add(status);
						if (jsonObject.containsKey("slad")) {
							JSONObject slad = (JSONObject) jsonObject.get("slad");
							// get slad name
							String name = (String) slad.get("name");
													
							if (slad.containsKey("sla_template")) {
								JSONObject sla_template = (JSONObject) slad.get("sla_template");
								if (sla_template.containsKey("licensing")) {
									JSONObject licensing = (JSONObject) sla_template.get("licensing");
									// get number of licenses
									String vms_number = (String) licensing.get("vms_number");
									details.add(vms_number);
								}
							}				
							
						}

					} catch (ParseException e) {
						e.printStackTrace();
					}

				}
				conn.disconnect();
			}

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}
		return details;
	}

	/**
	 * Delete a correlation between a network service and a sla template
	 */
	public static int deleteCorr(String sla_uuid) {
		String tablename = "cust_sla";
		db_operations dbo = new db_operations();
		boolean connect = db_operations.connectPostgreSQL();
		int status = 0;
		if (connect == true) {
			status = 200;
			dbo.deleteRecord(tablename, sla_uuid);
		} else {
			// failed to connect to database
			status = 404; 
		}
        dbo.closePostgreSQL();
		return status;
		
	}

	public static JSONArray getGuaranteeTerms(String sla_uuid) {

		JSONArray guaranteeTerms = null;
		try {
			String url = System.getenv("CATALOGUES_URL") + "slas/template-descriptors/" + sla_uuid + "\r\n";
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
			JSONObject slad_obj = (JSONObject) parser.parse(response.toString());
			JSONObject slad = (JSONObject) slad_obj.get("slad");
			JSONObject sla_template = (JSONObject) slad.get("sla_template");
			JSONObject ns = (JSONObject) sla_template.get("ns");
			guaranteeTerms = (JSONArray) ns.get("guaranteeTerms");
			System.out.println(guaranteeTerms);
			return guaranteeTerms;
		} 
		catch (Exception e) {
			System.out.println("SLA Agreement not found");
			return null;
		}

	}

	public JSONArray nsWithAgreement() {
		ArrayList<String> correlatedNS = new ArrayList<String>();

		db_operations dbo = new db_operations();

		// get the stored correlations
		db_operations.connectPostgreSQL();
		db_operations.createTableCustSla();
		JSONObject correlations = dbo.selectAllRecords("cust_sla");
		JSONArray cust_sla = (JSONArray) correlations.get("cust_sla");
		for (int i = 0; i < cust_sla.size(); i++) {
			JSONObject corr = (JSONObject) cust_sla.get(i);
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
	    db_operations.closePostgreSQL();

		return (JSONArray) correlatedNS;

	}

	public ArrayList<String> nsWithoutAgreement() {
		JSONArray existingNSArray = null;
		ArrayList<String> existingNSIDs = new ArrayList<String>();
		ArrayList<String> nsWithoutAgreement = new ArrayList<String>();

		// get the ns uuids that have correlated an sla template
		ArrayList<String> correlatedNSArray = new ArrayList<String>();
		correlatedNSArray = nsWithAgreement();

		// get all the available ns from the catalogue
		try {
			String url = System.getenv("CATALOGUES_URL") + "network-services";
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
				if (existingNSIDs.get(i) == correlatedNSArray.get(j)) {
					continue;
				} else {
					nsWithoutAgreement.add(existingNSIDs.get(i));
				}
			}

		}

		// remove duplicates
		Set<String> ns_uuids = new HashSet<String>();
		JSONArray tempArray = new JSONArray();
		for (int i = 0; i < nsWithoutAgreement.size(); i++) {
			String ns_uuid_temp = nsWithoutAgreement.get(i);
			if (ns_uuids.contains(ns_uuid_temp)) {
				continue;
			} else {
				ns_uuids.add(ns_uuid_temp);
				tempArray.add(nsWithoutAgreement.get(i));
			}
		}

		nsWithoutAgreement = tempArray; // assign temp to original

		return nsWithoutAgreement;
	}

}
