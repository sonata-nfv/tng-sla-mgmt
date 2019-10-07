package eu.tng.rules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MonitoringRulesCommunication {

    final static Logger logger = LogManager.getLogger();

    @SuppressWarnings("unchecked")
    public static JSONObject createMonitoringRules(String sla_uuid, ArrayList<String> vnfr_id_list,
            ArrayList<String> vnfr_name_list, ArrayList<String> deployment_unit_id_list, String nsi_id) {

        vnfr_id_list = new ArrayList<String>(new LinkedHashSet<String>(vnfr_id_list));
        vnfr_name_list = new ArrayList<String>(new LinkedHashSet<String>(vnfr_name_list));
        deployment_unit_id_list = new ArrayList<String>(new LinkedHashSet<String>(deployment_unit_id_list));

        JSONObject root = new JSONObject();

        if (sla_uuid != null && !sla_uuid.isEmpty()) {

            JSONObject slo_list = getSloDetails(sla_uuid);
            JSONArray slos = (JSONArray) slo_list.get("slos");
            JSONArray vnfs = new JSONArray();
            
            root.put("sla_cnt", sla_uuid);
            root.put("sonata_service_id", nsi_id);

            // for every slo_name in the array slos, check if the current slo is supported
            for (int i = 0; i < vnfr_name_list.size(); i++) {

                for (int j = 0; j < slos.size(); j++) {

                    JSONObject curr_slo = (JSONObject) slos.get(j);
                    String curr_slo_name = (String) curr_slo.get("name");
                    // get information for the slo
                    String target_period = (String) curr_slo.get("target_period");
                    String target_value = (String) curr_slo.get("target_value");

                    String curr_vnf_name = (String) vnfr_name_list.get(i);

                    /**
                     * check if it is the ms-vnf because the Packet Loss metric is supported
                     * only by this vnf
                     */
                    if (curr_slo_name.equals("videoPacketLost") && curr_vnf_name.equals("ms-vnf")) {
                    	
                        JSONObject vnf_obj_pl = new JSONObject();
                        JSONArray vdus_pl = new JSONArray();
                        JSONObject vdu_obj_pl = new JSONObject();
                        JSONArray rules_pl = new JSONArray();
                        JSONObject rule_obj_pl = new JSONObject();
                        JSONObject notification_type_pl = new JSONObject();

                        String nvfid = vnfr_id_list.get(i);
                        vnf_obj_pl.put("vnf_id", nvfid);

                        // Define JSONArray vdus

                        String curr_vdu_id = deployment_unit_id_list.get(i);
                        vdu_obj_pl.put("vdu_id", curr_vdu_id);

                        // Define JSONArray rules

                        rule_obj_pl.put("name", "sla_rule_" + curr_slo_name);
                        rule_obj_pl.put("duration", "10s");
                        rule_obj_pl.put("description", "");

                        String curr_vdu_id_quotes = "\"" + curr_vdu_id + "\"";
                        String condition = "videoPacketLost{resource_id=" + curr_vdu_id_quotes + "} > " + target_value;
                        
                        rule_obj_pl.put("condition", condition);
                        rule_obj_pl.put("summary", "");

                        notification_type_pl.put("id", "2");
                        notification_type_pl.put("type", "rabbitmq");
                        rule_obj_pl.put("notification_type", notification_type_pl);

                        rules_pl.add(rule_obj_pl);

                        vdu_obj_pl.put("rules", rules_pl);
                        vdus_pl.add(vdu_obj_pl);
                        vnf_obj_pl.put("vdus", vdus_pl);
                        vnfs.add(vnf_obj_pl);

                        root.put("vnfs", vnfs);
                    }

                    /**
                     * check if it is the rp-vnf because the availability metric is supported only
                     * by this vnf
                     */
                    if (curr_slo_name.equals("Downtime") && curr_vnf_name.equals("rp-vnf")) {
                    	
                        JSONObject vnf_obj_dt = new JSONObject();
                        JSONArray vdus_dt = new JSONArray();
                        JSONObject vdu_obj_dt = new JSONObject();
                        JSONArray rules_dt = new JSONArray();
                        JSONObject rule_obj_dt = new JSONObject();
                        JSONObject notification_type_dt = new JSONObject();

                        vnf_obj_dt = new JSONObject();
                        String nvfid = vnfr_id_list.get(i);
                        vnf_obj_dt.put("vnf_id", nvfid);

                        // Define JSONArray vdus
                        vdus_dt = new JSONArray();
                        vdu_obj_dt = new JSONObject();
                        String curr_vdu_id = deployment_unit_id_list.get(i);
                        vdu_obj_dt.put("vdu_id", curr_vdu_id);

                        // Define JSONArray rules
                        rules_dt = new JSONArray();
                        rule_obj_dt = new JSONObject();

                        rule_obj_dt.put("name", "sla_rule_" + curr_slo_name);
                        rule_obj_dt.put("duration", "10s");
                        rule_obj_dt.put("description", "");

                        String curr_vdu_id_quotes = "\"" + curr_vdu_id + "\"";
                        String trimed_target_value = target_value.substring(0, target_value.length() - 1);

                        String condition = "delta(health{resource_id=" + curr_vdu_id_quotes + "}[" + target_period + "]) > "
                                + trimed_target_value;

                        rule_obj_dt.put("condition", condition);
                        rule_obj_dt.put("summary", "");

                        notification_type_dt = new JSONObject();
                        notification_type_dt.put("id", "2");
                        notification_type_dt.put("type", "rabbitmq");
                        rule_obj_dt.put("notification_type", notification_type_dt);

                        rules_dt.add(rule_obj_dt);

                        vdu_obj_dt.put("rules", rules_dt);
                        vdus_dt.add(vdu_obj_dt);
                        vnf_obj_dt.put("vdus", vdus_dt);
                        vnfs.add(vnf_obj_dt);

                        root.put("vnfs", vnfs);
                    }
                } // end for loop vnfr names array

            } // end for loop slos array

 
            // logging
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String timestamps = timestamp.toString();
            String type = "I";
            String operation = "Create monitoring rules for immersive media service";
            String message = "[*] Monitoring rule to be sent for immersive media service ==> " + root.toString();
            String status = "";
            logger.info(
                    "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                    type, timestamps, operation, message, status);
            
            
            // Publish monitoring rule
            PublishMonitoringRules mr = new PublishMonitoringRules();
            mr.publishMonitringRules(root, nsi_id);

            // logging
            timestamp = new Timestamp(System.currentTimeMillis());
            timestamps = timestamp.toString();
            type = "I";
            operation = "Create monitoring rules for immersive media service";
            message = "Rule published succesfully!";
            status = "";
            logger.info(
                    "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                    type, timestamps, operation, message, status);

        } else {
            // logging
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String timestamps = timestamp.toString();
            String type = "W";
            String operation = "Create monitoring rules for immersive media service";
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
                String operation = "Create monitoring rules for immersive media service";
                String message = "[*] Error: Unable to get sla details. SLA NOT FOUND";
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

                    } catch (ParseException e) {
                        // logging
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        String timestamps = timestamp.toString();
                        String type = "E";
                        String operation = "Create monitoring rules for immersive media service";
                        String message = "[*] Error: " + e.getMessage();
                        String status = String.valueOf(conn.getResponseCode());
                        logger.error(
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
            String type = "E";
            String operation = "Create monitoring rules for immersive media service";
            String message = "[*] Error: " + e.getMessage();
            String status = "";
            logger.error(
                    "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                    type, timestamps, operation, message, status);
        }
        return slo_details;

    }
}
