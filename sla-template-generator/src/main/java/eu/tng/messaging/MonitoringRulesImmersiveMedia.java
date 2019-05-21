package eu.tng.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import eu.tng.rules.PublishMonitoringRules;

public class MonitoringRulesImmersiveMedia {

	final static Logger logger = LogManager.getLogger();

	@SuppressWarnings("unchecked")
	public static JSONObject createMonitoringRules(String sla_uuid, ArrayList<String> vnfr_id_list,
			ArrayList<String> vnfr_name_list, ArrayList<String> deployment_unit_id_list, String ns_id) {

		
		JSONObject root = new JSONObject();

		if (sla_uuid != null && !sla_uuid.isEmpty()) {
			// call function getSlaDetails
			JSONObject slo_list = getSloDetails(sla_uuid);
			JSONArray slos = (JSONArray) slo_list.get("slos");

			/**
			 * Create the rules
			 **/
			for (int i=0; i<slos.size(); i++) {
				JSONObject curr_slo = (JSONObject) slos.get(i);
				String curr_slo_name = (String) curr_slo.get("name");
				
				System.out.println("CURRENT SLO" + curr_slo);
				if (curr_slo_name.equals("input_connections")) {
					
					System.out.println("CURRENT SLO input_connections");
					root.put("sla_cnt", sla_uuid);
					
					String name = (String) ((JSONObject) slos.get(i)).get("name");
					System.out.println("name" + name);
					String target_period = (String) ((JSONObject) slos.get(i)).get("target_period");
					System.out.println("target_period" + target_period);
					String target_value = (String) ((JSONObject) slos.get(i)).get("target_value");
					System.out.println("target_value" + target_value);

					for (int k=0; k<vnfr_name_list.size(); k++) {
						
						String vnf_name = (String) vnfr_name_list.get(k);
						System.out.println("vnf_name" + vnf_name);
						
						if (vnf_name.equals("vnf_mse"))	{
							
							System.out.println("vnf_mse vnf_mse vnf_mse");
							
							String vnf_id = (String) vnfr_id_list.get(k);
							System.out.println("vnf_id " +vnf_id);

														
							JSONArray vnfs = new JSONArray();
							JSONObject nvfid = new JSONObject();
							nvfid.put("nvfid", vnf_id);
							vnfs.add(nvfid);
		
							org.json.JSONArray jsArray = new org.json.JSONArray(k); // k is the current vnfr 
							org.json.JSONArray current_vdus_array = ((org.json.JSONArray) jsArray).getJSONObject(k).getJSONArray("cloudnative_deployment_units");
									
							System.out.println("current_vdus_array " +current_vdus_array);
							
							JSONArray vdus = new JSONArray();
							JSONObject vduObject = new JSONObject();
							
							for (int j = 0; j < ((List) current_vdus_array).size(); j++) {							
								JSONObject vdu_obj = (JSONObject) current_vdus_array.get(j);
								String vdu_id = (String) vdu_obj.get("id");
								
								System.out.println("vdu_id " +vdu_id);
								
								vduObject.put("vdu_id", vdu_id);

								JSONArray rules = new JSONArray();
								JSONObject json_rule = new JSONObject();
								json_rule.put("name", "sla_rule_" + name + "_cdu01-" + vdu_id);
								json_rule.put("duration", "10s");
								json_rule.put("description", "");
								String vdu_id_quotes = "\"" + vdu_id + "\"";
								String condition = "delta(input_conn{resource_id=" + vdu_id_quotes + "}["+ target_period + "]) > " + target_value;
								json_rule.put("condition", condition);
								json_rule.put("summary", "");
								
								rules.add(json_rule);
								vduObject.put("rules", rules);
								
							}
							vdus.add(vduObject);
														
							vnfs.add(vdus);
							root.put("vnfs", vnfs);							
							
							System.out.println("MONITORING RULE: " + root);
							
						}
						
					}
					
				}
				else if (curr_slo.equals("status")) {
					break;
				}
				else
				{
					System.out.println("SLO NOT SUPPORTED");
				}
			}
			
			

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Publishing monitoring rule for SLA violation checks";
			String message = "Rule published succesfully!";
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

	@SuppressWarnings("unchecked")
	public static JSONObject getSloDetails(String sla_uuid) {

		JSONObject slo_details = new JSONObject();

		System.out.println("getting slo details for ==> " + sla_uuid);

		// String url =
		// "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors/"
		// + sla_uuid;
		try {
			// URL object = new URL(url);
			URL url = new URL(System.getenv("CATALOGUES_URL") + "slas/template-descriptors/" + sla_uuid);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() != 200) {
				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "E";
				String operation = "getSloDetails";
				String message = "SLA not FOUND";
				String status = String.valueOf(conn.getResponseCode());
				logger.error(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);
				slo_details = null;
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;

				while ((output = br.readLine()) != null) {
					JSONParser parser = new JSONParser();
					try {
						Object obj = parser.parse(output);
						JSONObject jsonObject = (JSONObject) obj;
						
						System.out.println("slo ==> " + jsonObject);
						
						JSONObject slad = (JSONObject) jsonObject.get("slad");
						JSONObject sla_template = (JSONObject) slad.get("sla_template");
						JSONObject ns = (JSONObject) sla_template.get("service");
						JSONArray guaranteeTerms = (JSONArray) ns.get("guaranteeTerms");

						JSONArray slos = new JSONArray();
						for (int i = 0; i < guaranteeTerms.size(); i++) {
							JSONObject slo = new JSONObject();

							JSONArray target_slo = (JSONArray) ((JSONObject) guaranteeTerms.get(i)).get("target_slo");
							for (int j = 0; j < target_slo.size(); j++) {
								JSONObject target_slo_obj = (JSONObject) target_slo.get(j);
								String target_name = (String) target_slo_obj.get("target_kpi");
								String target_duration = (String) target_slo_obj.get("target_duration");
								String target_value = (String) target_slo_obj.get("target_value");
								String target_period = (String) target_slo_obj.get("target_period");

								slo.put("name", target_name);
								slo.put("duration", target_duration);
								slo.put("target_value", target_value);
								slo.put("target_period", target_period);
							}
							slos.add(slo);
						}

						slo_details.put("slos", slos);
						System.out.println("slo details ==> " + slo_details);


					} catch (ParseException e) {
						// logging
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						String timestamps = timestamp.toString();
						String type = "D";
						String operation = "getSloDetails";
						String message = e.getMessage();
						String status = String.valueOf(conn.getResponseCode());
						logger.debug(
								"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
								type, timestamps, operation, message, status);
					}

				}
				conn.disconnect();
			}

		} catch (IOException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "D";
			String operation = "getSloDetails";
			String message = e.getMessage();
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return slo_details;

	}
	/*
	private static ArrayList<String> createCondition(String name, String target_period, String target_value,
			String vdu_id) {
		
		String trimed_target_value = target_value.substring(0, target_value.length() - 1);
		
		ArrayList<String> dc = new ArrayList<String>();
		String vdu_id_quotes = "\"" + vdu_id + "\"";
		if (name.equals("input_connections")) {
			System.out.println("[*] Start creating condition for input connections metric.....");
			String description_connections = "Trigger events if VM input connections are more than " + target_value
					+ " seconds in window of: 60 second";
			String condition_connections = "delta(input_conn{resource_id=" + vdu_id_quotes + "}["
					+ target_period + "]) > " + trimed_target_value;
			// String condition_avalability = "delta(haproxy_backend_downtime{resource_id="
			// + vdu_id_quotes + "}[1h]) > -1";
			dc.add(description_connections);
			dc.add(condition_connections);
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
		
		System.out.println("condition ==>" + dc);

		
		return dc;
	}*/

}
