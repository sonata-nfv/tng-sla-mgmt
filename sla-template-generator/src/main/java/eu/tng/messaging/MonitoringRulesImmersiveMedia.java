package eu.tng.messaging;

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

import eu.tng.rules.PublishMonitoringRules;

public class MonitoringRulesImmersiveMedia {

	final static Logger logger = LogManager.getLogger();

	@SuppressWarnings("unchecked")
	public static JSONObject createMonitoringRules(String sla_uuid, ArrayList<String> vnfr_id_list,
			ArrayList<String> vnfr_name_list, ArrayList<String> deployment_unit_id_list, String ns_id) {

		System.out.println("start creating rules for immersive media");
		System.out.println(sla_uuid);
		System.out.println(vnfr_id_list);
		System.out.println(vnfr_name_list);
		System.out.println(deployment_unit_id_list);
		System.out.println(ns_id);
		
		JSONObject root = new JSONObject();

		if (sla_uuid != null && !sla_uuid.isEmpty()) {
			System.out.println("SLA != NULL");
			// call function getSlaDetails
			JSONObject slo_list = getSloDetails(sla_uuid);
			JSONArray slos = (JSONArray) slo_list.get("slos");
			System.out.println("Monitroed SLOs: " + slos);

			/**
			 * Create the rules
			 **/
			for (int i = 0; i < slos.size(); i++) {
				JSONObject slo = (JSONObject) slos.get(i);
				String slo_name = (String) slo.get("name");

				System.out.println("SLOs Name" + slo_name);

				if (slo_name.equals("input_connections")) {
					for (int vnfr_name_iteration = 0; vnfr_name_iteration < vnfr_name_list.size(); vnfr_name_iteration++) {
						if (vnfr_name_list.get(vnfr_name_iteration).equals("vnf-mse")) {
							
							
							
							/**
							 * Create the rules
							 **/
							// ** sla id field **/
							root.put("sla_cnt", sla_uuid);

							// ** vnfs array **/
							JSONArray vnfs = new JSONArray();
							for (int  vnfr_id_iteration= 0; vnfr_id_iteration < vnfr_id_list.size(); vnfr_id_iteration++) {
								JSONObject nvfid = new JSONObject();
								// get the id in the same porition with the name
								nvfid.put("nvfid", vnfr_id_list.get(vnfr_name_iteration));
								vnfs.add(nvfid);

								// ** cdus array **/
								JSONArray vdus = new JSONArray();
								for (int deployment_unit_iteration = 0; deployment_unit_iteration < deployment_unit_id_list.size(); deployment_unit_iteration++) {
									JSONObject vduid = new JSONObject();
									vduid.put("vdu_id", deployment_unit_id_list.get(deployment_unit_iteration));
									vdus.add(vduid);

									// ** rules array **/
									JSONArray rules = new JSONArray();
									JSONObject rule = new JSONObject();
									String name = (String) ((JSONObject) slos.get(i)).get("name");
									String target_period = (String) ((JSONObject) slos.get(i)).get("target_period");
									String target_value = (String) ((JSONObject) slos.get(i)).get("target_value");
									
									// call function createCondition
									@SuppressWarnings("rawtypes")
									ArrayList dc = createCondition(name, target_period, target_value, deployment_unit_id_list.get(deployment_unit_iteration));
									System.out.println("[*] Condition created ==> " + dc.toString());
									String description = (String) dc.get(0);
									String condition = (String) dc.get(1);

									rule.put("name", "sla_rule_" + name + "_" + ns_id.replaceAll("-", "_"));
									rule.put("duration", "10s");
									rule.put("description", description);
									rule.put("condition", condition);
									rule.put("summary", "");
									JSONObject notification_type = new JSONObject();
									notification_type.put("id", "2");
									notification_type.put("type", "rabbitmq");
									rule.put("notification_type", notification_type);

									rules.add(rule);


									vduid.put("rules", rules);
								}
								nvfid.put("vdus", vdus);

							}
							root.put("vnfs", vnfs);							
						}
					}
					
					System.out.println("Connection monitoring rule ==> " + root);
					
					//PublishMonitoringRules mr = new PublishMonitoringRules();
					//mr.publishMonitringRules(root, ns_id);
				}
				else {
					System.out.println("No connecrtion slo");
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

							JSONObject target_slo = (JSONObject) ((JSONObject) guaranteeTerms.get(i)).get("target_slo");
							String target_name = (String) target_slo.get("target_kpi");
							String target_duration = (String) target_slo.get("target_duration");
							String target_value = (String) target_slo.get("target_value");
							String target_period = (String) target_slo.get("target_period");

							slo.put("name", target_name);
							slo.put("duration", target_duration);
							slo.put("target_value", target_value);
							slo.put("target_period", target_period);

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
	
	private static ArrayList<String> createCondition(String name, String target_period, String target_value,
			String vdu_id) {
		String trimed_target_value = target_value.substring(0, target_value.length() - 1);
		ArrayList<String> dc = new ArrayList<String>();
		String vdu_id_quotes = "\"" + vdu_id + "\"";
		if (name.equals("input_connection")) {
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
	}

}
