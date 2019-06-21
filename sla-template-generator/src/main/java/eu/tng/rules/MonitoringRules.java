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
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MonitoringRules {

	final static Logger logger = LogManager.getLogger();

	/**
	 * Create monitoring rules based on a instantiated NS and the selected SLA
	 **/
	@SuppressWarnings("unchecked")
	public static JSONObject createMonitroingRules(String sla_uuid, ArrayList<String> vnfr_id_list,
			ArrayList<String> vdus_id_list, String nsi_id) throws IOException {

		JSONObject root = new JSONObject();

		if (sla_uuid != null) {
			// call function getSlaDetails
			JSONObject slo_list = getSloDetails(sla_uuid);
			JSONArray slos = (JSONArray) slo_list.get("slos");

			/**
			 * Create the rules
			 **/
			// ** sla id field **/
			root.put("sla_cnt", sla_uuid);

			// ** vnfs array **/
			JSONArray vnfs = new JSONArray();
			for (int i = 0; i < vnfr_id_list.size(); i++) {
				JSONObject nvfid = new JSONObject();
				nvfid.put("nvfid", vnfr_id_list.get(i));
				vnfs.add(nvfid);

				// ** vdus array **/
				JSONArray vdus = new JSONArray();
				for (int j = 0; j < vdus_id_list.size(); j++) {
					JSONObject vduid = new JSONObject();
					vduid.put("vdu_id", vdus_id_list.get(j));
					vdus.add(vduid);

					// ** rules array **/
					JSONArray rules = new JSONArray();
					for (int k = 0; k < slos.size(); k++) {
						JSONObject rule = new JSONObject();
						String name = (String) ((JSONObject) slos.get(k)).get("name");
						String target_period = (String) ((JSONObject) slos.get(k)).get("target_period");
						String target_value = (String) ((JSONObject) slos.get(k)).get("target_value");

						// call function createCondition
						@SuppressWarnings("rawtypes")
						ArrayList dc = createCondition(name, target_period, target_value, vdus_id_list.get(j));
						String description = (String) dc.get(0);
						String condition = (String) dc.get(1);

						rule.put("name", "sla_rule_" + name + "_" + nsi_id.replaceAll("-", "_"));
						rule.put("duration", "10s");
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
			PublishMonitoringRules mr = new PublishMonitoringRules();
			mr.publishMonitringRules(root, nsi_id);
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Publishing monitoring rule for SLA violation checks";
			String message = "[*] Rule published succesfully!";
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

		} else {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "W";
			String operation = "Publishing monitoring rule for SLA violation checks";
			String message = "[*] ERROR: Unable to create rules. SLA ID is null";
			String status = "";
			logger.warn(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}

		return root;
	}

	private static ArrayList<String> createCondition(String name, String target_period, String target_value,
			String vdu_id) {
		String trimed_target_value = target_value.substring(0, target_value.length() - 1);
		ArrayList<String> dc = new ArrayList<String>();
		String vdu_id_quotes = "\"" + vdu_id + "\"";
		if (name.equals("Availability")) {
			String description_availability = "Trigger events if VM is down more than " + target_value
					+ " seconds in window of: 10 second";
			String condition_avalability = "delta(haproxy_backend_downtime{resource_id=" + vdu_id_quotes + "}["
					+ target_period + "]) > " + trimed_target_value;
			// String condition_avalability = "delta(haproxy_backend_downtime{resource_id="
			// + vdu_id_quotes + "}[1h]) > -1";
			dc.add(description_availability);
			dc.add(condition_avalability);
		}
		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "D";
		String operation = "Publishing monitoring rule for SLA violation checks";
		String message = "[*] Monitoring condition ==> " + dc.toString();
		String status = "";
		logger.debug(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);
		return dc;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getSloDetails(String sla_uuid) {

		JSONObject slo_details = new JSONObject();

		// String url =
		// "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors/"
		// + sla_uuid;
		try {
			// URL object = new URL(url);
			URL url = new URL(System.getenv("CATALOGUES_URL") + "slas/template-descriptors/" + sla_uuid);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() != 200) {
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
							String target_period = (String) serviceLevelObjetive.get("period");

							slo.put("name", name);
							slo.put("duration", duration);
							slo.put("target_value", target_value);
							slo.put("target_period", target_period);

							slos.add(slo);

						}

						slo_details.put("slos", slos);

					} catch (ParseException e) {
					}

				}
				conn.disconnect();
			}

		} catch (IOException e) {
		}
		return slo_details;

	}

}
