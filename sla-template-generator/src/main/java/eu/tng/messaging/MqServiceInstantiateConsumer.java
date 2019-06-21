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

package eu.tng.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;
import com.rabbitmq.client.*;

import eu.tng.correlations.cust_sla_corr;
import eu.tng.correlations.db_operations;
import eu.tng.rules.MonitoringRulesCommunication;
import eu.tng.rules.MonitoringRulesImmersiveMedia;

public class MqServiceInstantiateConsumer implements ServletContextListener {

    static Logger logger = LogManager.getLogger();

    private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
    // private static final String EXCHANGE_NAME = "son-kernel";

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event) {

        // logging
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String timestamps = timestamp.toString();
        String type = "W";
        String operation = "RabbitMQ Listener - Instantiation Consumer";
        String message = "[*] Listener Service Instances Create stopped - Restarting....";
        String status = "";
        logger.warn(
                "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                type, timestamps, operation, message, status);

        contextInitialized(event);
    }

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event) {

        final Channel channel_service_instance;
        Connection connection;
        String queueName_service_instance;

        try {
            new RabbitMqConnector();
            connection = RabbitMqConnector.MqConnector();

            channel_service_instance = connection.createChannel();
            channel_service_instance.exchangeDeclare(EXCHANGE_NAME, "topic");
            queueName_service_instance = "slas.service.instances.create";
            channel_service_instance.queueDeclare(queueName_service_instance, true, false, false, null);
            // logging
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String timestamps = timestamp.toString();
            String type = "I";
            String operation = "RabbitMQ Listener - Instantiation Consumer";
            String message = "[*] Binding queue to topic...";
            String status = "";
            logger.info(
                    "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                    type, timestamps, operation, message, status);

            channel_service_instance.basicQos(1);
            channel_service_instance.queueBind(queueName_service_instance, EXCHANGE_NAME, "service.instances.create");

            // logging
            Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
            String timestamps1 = timestamp1.toString();
            String type1 = "I";
            String operation1 = "RabbitMQ Listener - Instantiation Consumer";
            String message1 = "[*] Bound to topic \"service.instances.create\"\"";
            String status1 = "";
            logger.info(
                    "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                    type1, timestamps1, operation1, message1, status1);

            Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
            String timestamps2 = timestamp2.toString();
            String type2 = "I";
            String operation2 = "RabbitMQ Listener - Instantiation Consumer";
            String message2 = "[*] Waiting for messages.";
            String status2 = "";
            logger.info(
                    "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                    type2, timestamps2, operation2, message2, status2);

            DeliverCallback deliverCallback = new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery delivery) throws IOException {
                    // Initialize variables
                    String status = "";
                    String correlation_id = null;
                    JSONObject jsonObjectMessage = null;
                    String nsi_id = "";
                    String network_service_name = "";
                    Object sla_id = null;
                    ArrayList<String> vc_id_list = new ArrayList<String>();
                    ArrayList<String> vnfr_id_list = new ArrayList<String>();
                    ArrayList<String> cdu_id_list = new ArrayList<String>();
                    ArrayList<String> vnfr_name_list = new ArrayList<String>();

                    // Parse message payload
                    String message = new String(delivery.getBody(), "UTF-8");

                    // Ack the message
                    channel_service_instance.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                    // parse the yaml and convert it to json
                    Yaml yaml = new Yaml();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) yaml.load(message);

                    sla_id = map.get("sla_id");

                    jsonObjectMessage = new JSONObject(map);

                    correlation_id = (String) delivery.getProperties().getCorrelationId();

                    /**
                     * if message coming from the MANO - contain status key
                     * 
                     **/
                    if (jsonObjectMessage.has("status")) {
                        status = (String) jsonObjectMessage.get("status");

                        // logging
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        String timestamps = timestamp.toString();
                        String type = "I";
                        String operation = "NS Instantiation";
                        String message2 = "[*] Message coming from MANO - STATUS = " + status;
                        String status2 = "";
                        logger.info(
                                "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                                type, timestamps, operation, message2, status2);

                        if (status.equals("READY")) {

                            // logging
                            timestamp = new Timestamp(System.currentTimeMillis());
                            timestamps = timestamp.toString();
                            type = "I";
                            String mano_msg = jsonObjectMessage.toMap().toString();
                            operation = "Netork service instantiation";
                            message = "MANO Message when instantiation status:READY ==> " + mano_msg;
                            status = "";
                            logger.info(
                                    "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                                    type, timestamps, operation, message, status);

                            // get info for the monitoring metrics
                            if (sla_id != null) {

                                // Get service uuid (ns_uuid)
                                JSONObject nsr = (JSONObject) jsonObjectMessage.getJSONObject("nsr");
                                nsi_id = (String) nsr.get("id");

                                // Get network_service_name
                                db_operations dbo = new db_operations();
                                db_operations.connectPostgreSQL();

                                org.json.simple.JSONObject ns_name_obj = dbo.selectAgreementPerSLA(sla_id.toString());


                                network_service_name = (String) ns_name_obj.get("ns_name");
                                db_operations.closePostgreSQL();
                                

                                // Get vnfrs
                                JSONArray vnfrs = (JSONArray) jsonObjectMessage.getJSONArray("vnfrs");
                                for (int i = 0; i < (vnfrs).length(); i++) {

                                    // Get vdus_reference foreach vnfr
                                    try {
                                        String vnfr_name = vnfrs.getJSONObject(i).getString("name");
                                        vnfr_name_list.add(vnfr_name);

                                        JSONArray vdus = (JSONArray) ((JSONObject) vnfrs.getJSONObject(i))
                                                .getJSONArray("virtual_deployment_units");

                                        for (int j = 0; j < vdus.length(); j++) {
                                            String vdu_reference = (String) ((JSONObject) vdus.getJSONObject(j))
                                                    .get("vdu_reference");

                                            if (vdu_reference.startsWith("ms-vnf") == true) {

                                                // get vnfr id
                                                String vnfr_id = (String) ((JSONObject) vnfrs.get(i)).get("id");

                                                vnfr_id_list.add(vnfr_id);
                                                // get vdu id (vc_id)
                                                JSONArray vnfc_instance = (JSONArray) ((JSONObject) vdus
                                                        .getJSONObject(j)).getJSONArray("vnfc_instance");

                                                for (int k = 0; k < vnfc_instance.length(); k++) {
                                                    String vc_id = (String) ((JSONObject) vnfc_instance
                                                            .getJSONObject(j)).get("vc_id");

                                                    vc_id_list.add(vc_id);
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        // logging
                                        timestamp = new Timestamp(System.currentTimeMillis());
                                        timestamps = timestamp.toString();
                                        type = "D";
                                        String error_msg = e.getMessage().toString();
                                        message = "Debug: " + error_msg;
                                        status = "";
                                        logger.debug(
                                                "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                                                type, timestamps, operation, message, status);
                                    }

                                    // Get cdus_reference foreach vnfr
                                    try {
                                        JSONArray cdus = (JSONArray) ((JSONObject) vnfrs.getJSONObject(i))
                                                .getJSONArray("cloudnative_deployment_units");

                                        for (int j = 0; j < cdus.length(); j++) {
                                            String cdu_reference = (String) ((JSONObject) cdus.getJSONObject(j))
                                                    .get("cdu_reference");

                                            if ((cdu_reference.startsWith("vnf-mse") == true)
                                                    || (cdu_reference.startsWith("vnf-cms") == true)
                                                    || (cdu_reference.startsWith("vnf-ma") == true)
													|| (cdu_reference.startsWith("vnf-twitter") == true)) {
                                                String vnfr_name = vnfrs.getJSONObject(i).getString("name");
                                                vnfr_name_list.add(vnfr_name);
                                                // get vnfr id
                                                String vnfr_id = (String) ((JSONObject) vnfrs.get(i))
                                                        .get("descriptor_reference");

                                                vnfr_id_list.add(vnfr_id);
                                                // get cdu id (cdu_id)
                                                String cdu_id = (String) ((JSONObject) vnfrs.get(i)).get("id");

                                                cdu_id_list.add(cdu_id);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        // logging
                                        timestamp = new Timestamp(System.currentTimeMillis());
                                        timestamps = timestamp.toString();
                                        type = "D";
                                        operation = "Netork service instantiation";
                                        String error_msg = e.getMessage().toString();
                                        message = "Debug: " + error_msg;
                                        status = "";
                                        logger.debug(
                                                "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                                                type, timestamps, operation, message, status);
                                    }

                                }

                                // Update NSI Records - to create agreement
                                db_operations.connectPostgreSQL();
                                db_operations.UpdateRecordAgreement("READY", correlation_id, nsi_id);
                                db_operations.closePostgreSQL();

                                // UPDATE LIcense record with NSI - to create license instance
                                // check if there are already instances for this ns_uuid - cust_username
                                db_operations.connectPostgreSQL();
                                db_operations.CreateLicenseInstance(correlation_id, "active", nsi_id);
                                db_operations.closePostgreSQL();
                                
                                
                                /**
                                 *  Check if this SLA has guarantees - if it doesn't do not create the mnitoring rules
                                 */
                                
                        		try {
                        			String url = System.getenv("CATALOGUES_URL") + "slas/template-descriptors/"+String.valueOf(sla_id);
                        			// String url =
                        			// "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors";
                        			URL object = new URL(url);

                        			HttpURLConnection con = (HttpURLConnection) object.openConnection();
                        			con.setDoOutput(true);
                        			con.setDoInput(true);
                        			con.setRequestProperty("Content-Type", "application/json");
                        			con.setRequestProperty("Accept", "application/json");
                        			con.setRequestMethod("GET");

                        			con.getResponseCode();
                        			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        			String inputLine;
                        			StringBuffer response = new StringBuffer();
                        			while ((inputLine = in.readLine()) != null) {
                        				response.append(inputLine);
                        			}
                        			in.close();
                        			String sresponse = response.toString();
                        			JSONObject jsonObj = new JSONObject(sresponse);

                        			try {
                        				JSONObject slad = jsonObj.getJSONObject("slad");
                            			JSONObject sla_template = slad.getJSONObject("sla_template");
                            			JSONObject service = sla_template.getJSONObject("service");
                            			
										JSONArray guaranteeTerms = service.getJSONArray("guaranteeTerms"); 
																			
										// Monitoring rules for Immersive Media
		                                if (network_service_name.equals("mediapilot-service")) {
		                                    new MonitoringRulesImmersiveMedia();
		                                    MonitoringRulesImmersiveMedia.createMonitoringRules(String.valueOf(sla_id),
		                                            vnfr_id_list, vnfr_name_list, cdu_id_list, nsi_id);
		                                }

		                                // Monitoring rules for Communications Pilot
		                                if (network_service_name.equals("communication-pilot")) {
		                                    new MonitoringRulesCommunication();
		                                    MonitoringRulesCommunication.createMonitoringRules(String.valueOf(sla_id),
		                                            vnfr_id_list, vnfr_name_list, vc_id_list, nsi_id);
		                                }
		                                
									} 
                        			catch (Exception e2) {
                        				// logging
                            			timestamp = new Timestamp(System.currentTimeMillis());
                            			timestamps = timestamp.toString();
                            			type = "I";
                            			operation = "Checking SLOs. Class: " + this.getClass().getSimpleName();
                            			message = "[*] This SLA has not guarantees. Creation of rules aborted.";
                            			status = String.valueOf(404);
                            			logger.info(
                            					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                            					type, timestamps, operation, message, status);
									}
                        			
                        			
                        		} catch (Exception e) {
                        			// logging
                        			timestamp = new Timestamp(System.currentTimeMillis());
                        			timestamps = timestamp.toString();
                        			type = "E";
                        			operation = "Getting SLA Template. Class: " + this.getClass().getSimpleName();
                        			message = "[*] Error SLA Not Found";
                        			status = String.valueOf(404);
                        			logger.error(
                        					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                        					type, timestamps, operation, message, status);
                        		}
                                

                            } else {
                                // logging
                                Timestamp timestamp3 = new Timestamp(System.currentTimeMillis());
                                String timestamps3 = timestamp3.toString();
                                String type3 = "I";
                                String operation3 = "NS Instantiation";
                                String message3 = "[*] Instantiation without SLA. Message aborted.";
                                String status3 = "";
                                logger.info(
                                        "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                                        type3, timestamps3, operation3, message3, status3);
                            }

                        }

                    }

                    /** if message coming from the GK - doesn't contain status key **/
                    else {
                        // logging
                        Timestamp timestamp4 = new Timestamp(System.currentTimeMillis());
                        String timestamps4 = timestamp4.toString();
                        String type4 = "D";
                        String operation4 = "RabbitMQ Listener";
                        String gk_msg = jsonObjectMessage.toString();
                        String message4 = "[*] Message coming from Gatekeeper ==> " + gk_msg;
                        String status4 = "";
                        logger.debug(
                                "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                                type4, timestamps4, operation4, message4, status4);

                        // Initialize valiables
                        String sla_uuid = null;
                        String ns_uuid = null;
                        String ns_name = null;
                        String cust_username = null;
                        String cust_email = null;
                        String sla_name = null;
                        String sla_status = null;

                        // Get nsd data
                        JSONObject nsd = jsonObjectMessage.getJSONObject("NSD");
                        ns_name = (String) nsd.get("name");
                        ns_uuid = (String) nsd.get("uuid");

                        // Parse customer data + sla uuid
                        JSONObject user_data = (JSONObject) jsonObjectMessage.getJSONObject("user_data");
                        JSONObject customer = (JSONObject) user_data.getJSONObject("customer");
                        

                        try {
                            cust_username = (String) customer.get("name");
                            cust_email = (String) customer.get("email");
                        } catch (JSONException e) {
                            cust_username = "";
                            cust_email = "";
                            // logging
                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            String timestamps = timestamp.toString();
                            String type = "E";
                            String operation = "Netork service instantiation";
                            message = "Error: " + e.getMessage();
                            status = "";
                            logger.error(
                                    "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                                    type, timestamps, operation, message, status);
                        }

                        try {
                            sla_uuid = (String) customer.get("sla_id");
                        } catch (JSONException e) {
                            sla_uuid = "";

                            // logging
                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            String timestamps = timestamp.toString();
                            String type = "E";
                            String operation = "Netork service instantiation";
                            message = "Error: " + e.getMessage();
                            status = "";
                            logger.error(
                                    "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                                    type, timestamps, operation, message, status);

                        }

                        // if sla exists create record in database
                        if (sla_uuid != null && !sla_uuid.isEmpty()) {

                            // CREATE AGREEMENT RECORD IN THE CUST_SLA TABLE
                            cust_sla_corr cust_sla = new cust_sla_corr();
                            ArrayList<String> SLADetails = cust_sla.getSLAdetails(sla_uuid);
                            sla_name = (String) SLADetails.get(1);
                            sla_status = (String) SLADetails.get(0);
                            String inst_status = "PENDING";
                            cust_sla_corr.createCustSlaCorr(sla_uuid, sla_name, sla_status, ns_uuid, ns_name,
                                    cust_username, cust_email, inst_status, correlation_id);

                            // get licensing information
                            db_operations.connectPostgreSQL();


                            org.json.simple.JSONObject LicenseinfoTemplate = db_operations.getLicenseinfoTemplates(sla_uuid, ns_uuid);

                            
                            String license_type = (String) LicenseinfoTemplate.get("license_type");
                            String license_exp_date = (String) LicenseinfoTemplate.get("license_exp_date");
                            int allowed_instances = (int) LicenseinfoTemplate.get("allowed_instances");

                            
                            // check if there are already instances for this ns_uuid - cust_username
                            int active_licenses = db_operations.countActiveLicensePerCustSLA(cust_username, sla_uuid,"active");

                            String current_instances = String.valueOf(active_licenses + 1);


                            // logging
                            Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
                            String timestamps1 = timestamp1.toString();
                            String type1 = "I";
                            String operation1 = "Instantiation with License";
                            String message1 = "[*] Active licenses for this customer and ns ==> " + active_licenses;
                            String status1 = "";
                            logger.info(
                                    "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                                    type1, timestamps1, operation1, message1, status1);

                            // private licenses
                            if (license_type.equals("private")) {
                                if (active_licenses == 0) {
                                    db_operations.UpdateLicenseCorrelationID(sla_uuid, ns_uuid, cust_username,
                                            correlation_id);
                                    try {
                                        db_operations.UpdateLicenseCurrentInstances(sla_uuid, ns_uuid, cust_username,
                                                current_instances);
                                    } catch (SQLException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    db_operations.insertLicenseRecord(sla_uuid, ns_uuid, "", cust_username, cust_email,
                                            license_type, license_exp_date, String.valueOf(allowed_instances), current_instances,
                                            "bought", correlation_id);
                                    try {
                                        db_operations.UpdateLicenseCurrentInstances(sla_uuid, ns_uuid, cust_username,
                                                current_instances);
                                    } catch (SQLException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                            // public and trial licenses
                            else {

                                db_operations.createTableLicensing();
                                db_operations.insertLicenseRecord(sla_uuid, ns_uuid, "", cust_username, cust_email,
                                        license_type, license_exp_date, String.valueOf(allowed_instances), current_instances,
                                        "inactive", correlation_id);
                                try {
                                    db_operations.UpdateLicenseCurrentInstances(sla_uuid, ns_uuid, cust_username,
                                            current_instances);
                                } catch (SQLException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            }
                            db_operations.closePostgreSQL();
                        }
                    }

                }
            };

            channel_service_instance.basicConsume(queueName_service_instance, false, deliverCallback,
                    new CancelCallback() {
                        @Override
                        public void handle(String consumerTag) throws IOException {
                        }
                    });

        } catch (IOException e) {

            // logging
            Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
            String timestamps1 = timestamp1.toString();
            String type1 = "E";
            String operation1 = "RabbitMQ Listener - NS Instantiation";
            String message1 = "[*] ERROR Connecting to MQ!" + e.getMessage();
            String status1 = "";
            logger.error(
                    "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                    type1, timestamps1, operation1, message1, status1);
        }

    }

}