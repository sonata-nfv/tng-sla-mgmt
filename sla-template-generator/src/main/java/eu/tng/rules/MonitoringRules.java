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


package eu.tng.rules;

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

public class MonitoringRules {

	/**
	 * Create monitoring rules based on a instantiated NS and the selected SLA
	 **/
	public static JSONObject createMonitroingRules(String sla_uuid, ArrayList<String> vnfrs_list,
			ArrayList<String> vdus_list, String ns_id) throws IOException {

		JSONObject root = new JSONObject();

		if (sla_uuid != null) {
			// call function getSlaDetails
			JSONObject slo_list = getSloDetails(sla_uuid);
			JSONArray slos = (JSONArray) slo_list.get("slos");
			System.out.println("Monitroed SLOs: " + slos);

			/**
			 * Create the rules
			 **/
			// ** sla id field **/
			root.put("sla_cnt", sla_uuid);

			// ** vnfs array **/
			JSONArray vnfs = new JSONArray();
			for (int i = 0; i < vnfrs_list.size(); i++) {
				JSONObject nvfid = new JSONObject();
				nvfid.put("nvfid", vnfrs_list.get(i));
				vnfs.add(nvfid);

				// ** vdus array **/
				JSONArray vdus = new JSONArray();
				for (int j = 0; j < vdus_list.size(); j++) {
					JSONObject vduid = new JSONObject();
					vduid.put("vdu_id", vdus_list.get(j));
					vdus.add(vduid);

					// ** rules array **/
					JSONArray rules = new JSONArray();
					for (int k = 0; k < slos.size(); k++) {
						JSONObject rule = new JSONObject();
						String name = (String) ((JSONObject) slos.get(k)).get("name");
						String duration = (String) ((JSONObject) slos.get(k)).get("duration");
						String target_value = (String) ((JSONObject) slos.get(k)).get("target_value");

						// call function getSlaDetails
						ArrayList dc = createCondition(name, target_value,vdus_list.get(j));
						String description = (String) dc.get(0);
						String condition = (String) dc.get(1);

						rule.put("name", "sla:rule:" + name);
						rule.put("duration", duration);
						rule.put("description", description);
						rule.put("condition", condition);
						rule.put("summary", "");
						JSONObject notification_type = new JSONObject();
						notification_type.put("id", "2");
						notification_type.put("type", "rabbitmq");
						rule.put("notification_type", notification_type);

						rules.add(rule);
					}

					vduid.put("rules", rules);
				}
				nvfid.put("vdus", vdus);

			}
			root.put("vnfs", vnfs);
			System.out.println("monitoring body ==> " + root);
			PublishMonitoringRules mr = new PublishMonitoringRules();
			mr.publishMonitringRules(root, ns_id);

		} else {
			System.out.println("ERROR: Unable to create rules. SLA ID is null");
		}

			
		return root;
	}

	private static ArrayList createCondition(String name, String target_value, String vdu_id) {
		ArrayList<String> dc = new ArrayList<String>();
		switch (name) {
		case "Resilience":
			String description = "Trigger events if VM is down more than " + target_value + " seconds.";
			//String condition = "vm_up{id="+id+"}> " + target_value;
			String condition = "delta(haproxy_backend_downtime{resource_id=" + vdu_id + "}[1h]) > -1";
			
			dc.add(description);
			dc.add(condition);
			break;
		default:
			break;
		}
		return dc;
	}

	public static JSONObject getSloDetails(String sla_uuid) {

		JSONObject slo_details = new JSONObject();

//		 String url =
//		 "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors/"
//		 + sla_uuid;
		try {
			//URL object = new URL(url);
			URL url = new URL(System.getenv("CATALOGUES_URL") + "slas/template-descriptors/" + sla_uuid);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() != 200) {
				System.out.println("Failed : HTTP error code : SLA not FOUND");
				slo_details = null;
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;

				while ((output = br.readLine()) != null) {
					JSONParser parser = new JSONParser();
					try {
						Object obj = parser.parse(output);
						JSONObject jsonObject = (JSONObject) obj;
						JSONObject slad = (JSONObject) jsonObject.get("slad");
						JSONObject sla_template = (JSONObject) slad.get("sla_template");
						JSONObject ns = (JSONObject) sla_template.get("ns");
						JSONArray guaranteeTerms = (JSONArray) ns.get("guaranteeTerms");

						JSONArray slos = new JSONArray();
						for (int i = 0; i < guaranteeTerms.size(); i++) {
							JSONObject slo = new JSONObject();
							String name = (String) ((JSONObject) guaranteeTerms.get(i)).get("name");
							JSONObject serviceLevelObjetive = (JSONObject) ((JSONObject) guaranteeTerms.get(i))
									.get("serviceLevelObjetive");
							String duration = (String) serviceLevelObjetive.get("duration");
							String target_value = (String) serviceLevelObjetive.get("target_value");
							slo.put("name", name);
							slo.put("duration", duration);
							slo.put("target_value", target_value);

							slos.add(slo);

						}

						slo_details.put("slos", slos);

					} catch (ParseException e) {
						e.printStackTrace();
					}

				}
				conn.disconnect();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return slo_details;

	}

}
