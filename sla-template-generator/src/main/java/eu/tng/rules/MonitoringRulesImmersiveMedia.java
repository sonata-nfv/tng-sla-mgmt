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

public class MonitoringRulesImmersiveMedia {

    final static Logger logger = LogManager.getLogger();

    @SuppressWarnings("unchecked")
    public static JSONObject createMonitoringRules(String sla_uuid, ArrayList<String> vnfr_id_list,
            ArrayList<String> vnfr_name_list, ArrayList<String> deployment_unit_id_list, String nsi_id) {

        JSONObject root = new JSONObject();

        if (sla_uuid != null && !sla_uuid.isEmpty()) {

            // call function getSlaDetails
            JSONObject slo_list = getSloDetails(sla_uuid);
            JSONArray slos = (JSONArray) slo_list.get("slos");

            // for every slo_name in the array slos, check if the current slo is supported
            for (int i = 0; i < slos.size(); i++) {
                // get current slo name
                JSONObject curr_slo = (JSONObject) slos.get(i);
                String curr_slo_name = (String) curr_slo.get("name");
                System.out.println("[*] Current slo name ==> " + curr_slo_name);

                // get information for the slo
                String target_kpi = (String) curr_slo.get("name");
                String target_period = (String) curr_slo.get("target_period");
                String target_value = (String) curr_slo.get("target_value");

                // Define JSONArray vnfs
                JSONArray vnfs = new JSONArray();

                for (int j = 0; j < vnfr_name_list.size(); j++) {
                    String curr_vnf_name = (String) vnfr_name_list.get(j);

                    /**
                     * check if it is the vnf-ma because the input connections metric is supported
                     * only by this vnfr
                     */
                    if (curr_slo_name.equals("input_connections") && curr_vnf_name.equals("vnf-ma")) {

                        JSONObject vnf_obj = new JSONObject();
                        String nvfid = vnfr_id_list.get(j);
                        vnf_obj.put("nvfid", nvfid);

                        // Define JSONArray vdus
                        JSONArray vdus = new JSONArray();
                        JSONObject vdu_obj = new JSONObject();
                        String curr_vdu_id = deployment_unit_id_list.get(j);
                        vdu_obj.put("vdu_id", curr_vdu_id);

                        // Define JSONArray rules
                        JSONArray rules = new JSONArray();
                        JSONObject rule_obj = new JSONObject();

                        rule_obj.put("name", "sla_rule_" + target_kpi);
                        rule_obj.put("duration", "10s");
                        rule_obj.put("description", "");

                        String curr_vdu_id_quotes = "\"cdu01-" + curr_vdu_id + "\"";
                        String condition = "input_conn{container_name=" + curr_vdu_id_quotes + "} > " + target_value;
                        rule_obj.put("condition", condition);
                        rule_obj.put("summary", "");

                        JSONObject notification_type = new JSONObject();
                        notification_type.put("id", "2");
                        notification_type.put("type", "rabbitmq");
                        rule_obj.put("notification_type", notification_type);

                        rules.add(rule_obj);

                        vdu_obj.put("rules", rules);
                        vdus.add(vdu_obj);
                        vnf_obj.put("vdus", vdus);
                        vnfs.add(vnf_obj);
                    }

                    /**
                     * check if it is the vnf-cms because the availability metric is supported only
                     * by this vnfr
                     */
                    else if (curr_slo_name.equals("Downtime") && curr_vnf_name.equals("vnf-cms")) {

                        JSONObject vnf_obj = new JSONObject();
                        String nvfid = vnfr_id_list.get(j);
                        vnf_obj.put("nvfid", nvfid);

                        // Define JSONArray vdus
                        JSONArray vdus = new JSONArray();
                        JSONObject vdu_obj = new JSONObject();
                        String curr_vdu_id = deployment_unit_id_list.get(j);
                        vdu_obj.put("vdu_id", curr_vdu_id);

                        // Define JSONArray rules
                        JSONArray rules = new JSONArray();
                        JSONObject rule_obj = new JSONObject();

                        rule_obj.put("name", "sla_rule_" + target_kpi);
                        rule_obj.put("duration", "10s");
                        rule_obj.put("description", "");

                        String curr_vdu_id_quotes = "\"cdu01-" + curr_vdu_id + "\"";
                        String trimed_target_value = target_value.substring(0, target_value.length() - 1);

                        String condition = "delta(status{container_name=" + curr_vdu_id_quotes + "}[" + target_period + "]) > " + trimed_target_value;

                        rule_obj.put("condition", condition);
                        rule_obj.put("summary", "");

                        JSONObject notification_type = new JSONObject();
                        notification_type.put("id", "2");
                        notification_type.put("type", "rabbitmq");
                        rule_obj.put("notification_type", notification_type);

                        rules.add(rule_obj);

                        vdu_obj.put("rules", rules);
                        vdus.add(vdu_obj);
                        vnf_obj.put("vdus", vdus);
                        vnfs.add(vnf_obj);
                    }
                } // end for loop vnfr names array
                root.put("vnfs", vnfs);

            } // end for loop slos array

            // /**
            // * Create the rules
            // **/
            // for (int i = 0; i < slos.size(); i++) {
            // JSONObject curr_slo = (JSONObject) slos.get(i);
            // String curr_slo_name = (String) curr_slo.get("name");
            // System.out.println("CURRENT SLO: " + curr_slo_name);
            // if (curr_slo_name.equals("input_connections")) {
            //
            // System.out.println("IF TOU input_connections");
            //
            // String name = (String) curr_slo.get("name");
            // String target_period = (String) curr_slo.get("target_period");
            // String target_value = (String) curr_slo.get("target_value");
            //
            // for (int k = 0; k < vnfr_name_list.size(); k++) {
            //
            // String vnf_name = (String) vnfr_name_list.get(k);
            //
            // if (vnf_name.equals("vnf-ma")) {
            //
            // JSONArray vnfs = new JSONArray();
            //
            // JSONObject vnf_obj = new JSONObject();
            // String nvfid = vnfr_id_list.get(k);
            // vnf_obj.put("nvfid", nvfid);
            //
            // JSONArray vdus = new JSONArray();
            //
            // JSONObject vdu_obj = new JSONObject();
            // String vdu_id = deployment_unit_id_list.get(k);
            // vdu_obj.put("vdu_id", vdu_id);
            //
            // JSONArray rules = new JSONArray();
            // JSONObject json_rule = new JSONObject();
            // json_rule.put("name", "sla_rule_conns" + name);
            // json_rule.put("duration", "10s");
            // json_rule.put("description", "");
            // String vdu_id_quotes = "\"cdu01-" + vdu_id + "\"";
            // String condition = "input_conn{container_name=" + vdu_id_quotes + "} > " +
            // target_value;
            // json_rule.put("condition", condition);
            // json_rule.put("summary", "");
            //
            // JSONObject notification_type = new JSONObject();
            // notification_type.put("id", "2");
            // notification_type.put("type", "rabbitmq");
            // json_rule.put("notification_type", notification_type);
            //
            //
            // rules.add(json_rule);
            // vdu_obj.put("rules", rules);
            //
            // vdus.add(vdu_obj);
            // vnf_obj.put("vdus", vdus);
            //
            // vnfs.add(vnf_obj);
            //
            // root.put("vnfs", vnfs);
            // }
            //
            // }
            //
            // }
            //
            // else if (curr_slo_name.equals("Downtime")) {
            // String name = (String) curr_slo.get("name");
            // String target_period = (String) curr_slo.get("target_period");
            // String target_value = (String) curr_slo.get("target_value");
            // JSONArray vnfs = new JSONArray();
            // System.out.println("ELSEIF TOU Downtime");
            //
            // for (int k = 0; k < vnfr_name_list.size(); k++) {
            //
            // String vnf_name = (String) vnfr_name_list.get(k);
            //
            // if (vnf_name.equals("vnf-cms")) {
            // System.out.println("VNF NAME: vnf-cms");
            //
            //
            // JSONObject vnf_obj = new JSONObject();
            // String nvfid = vnfr_id_list.get(k);
            // vnf_obj.put("nvfid", nvfid);
            //
            // JSONArray vdus = new JSONArray();
            //
            // JSONObject vdu_obj = new JSONObject();
            // String vdu_id = deployment_unit_id_list.get(k);
            // vdu_obj.put("vdu_id", vdu_id);
            //
            // JSONArray rules = new JSONArray();
            // JSONObject json_rule = new JSONObject();
            // json_rule.put("name", "sla_rule_status" + name);
            // json_rule.put("duration", "10s");
            // json_rule.put("description", "");
            // String vdu_id_quotes = "\"cdu01-" + vdu_id + "\"";
            // String trimed_target_value = target_value.substring(0, target_value.length()
            // - 1);
            // String condition = "delta(status{container_name=" + vdu_id_quotes + "}[" +
            // target_period + "]) > " + trimed_target_value;
            // json_rule.put("condition", condition);
            // json_rule.put("summary", "");
            //
            // JSONObject notification_type = new JSONObject();
            // notification_type.put("id", "2");
            // notification_type.put("type", "rabbitmq");
            // json_rule.put("notification_type", notification_type);
            //
            //
            // rules.add(json_rule);
            // vdu_obj.put("rules", rules);
            //
            // vdus.add(vdu_obj);
            // vnf_obj.put("vdus", vdus);
            //
            // vnfs.add(vnf_obj);
            // }
            //
            // }
            //
            // root.put("vnfs", vnfs);
            // root.put("sla_cnt", sla_uuid);
            // root.put("sonata_service_id", nsi_id);
            //
            //
            // // logging
            // Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            // String timestamps = timestamp.toString();
            // String type = "I";
            // String operation = "Publishing monitoring rule for SLA violationS";
            // String message = "[*] Created Rule ==> " + root.toJSONString();
            // String status = "";
            // logger.info(
            // "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
            // type, timestamps, operation, message, status);
            //
            //
            // }
            //
            // else {
            // // logging
            // Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            // String timestamps = timestamp.toString();
            // String type = "I";
            // String operation = "Publishing monitoring rule for SLA violationS";
            // String message = "SLO not supported yet";
            // String status = "";
            // logger.info(
            // "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
            // type, timestamps, operation, message, status);
            // }
            // }
            //

            System.out.println("MONITORING RULE: " + root);
            
            // Publish monitoring rule
            PublishMonitoringRules mr = new PublishMonitoringRules();
            mr.publishMonitringRules(root, nsi_id);
            
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

}
