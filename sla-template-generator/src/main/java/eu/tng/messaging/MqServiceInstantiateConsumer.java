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

import java.io.IOException;
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
		String type = "I";
		String operation = "RabbitMQ Listener";
		String message = "[*] Listener Service Instances Create stopped - Restarting....";
		String status = "";
		logger.info(
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
			RabbitMqConnector connect = new RabbitMqConnector();
			connection = RabbitMqConnector.MqConnector();

			channel_service_instance = connection.createChannel();
			channel_service_instance.exchangeDeclare(EXCHANGE_NAME, "topic");
			queueName_service_instance = "slas.service.instances.create";
			channel_service_instance.queueDeclare(queueName_service_instance, true, false, false, null);
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "RabbitMQ Listener";
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
			String operation1 = "RabbitMQ Listener";
			String message1 = "[*] Bound to topic \"service.instances.create\"\"";
			String status1 = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type1, timestamps1, operation1, message1, status1);

			Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
			String timestamps2 = timestamp2.toString();
			String type2 = "I";
			String operation2 = "RabbitMQ Listener";
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
					String ns_id = "";
					String network_service_name ="";
					Object sla_id = null;
					ArrayList<String> vc_id_list = new ArrayList<String>();
					ArrayList<String> vnfr_id_list = new ArrayList<String>();
					ArrayList<String> cdu_id_list = new ArrayList<String>();

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

					// logging
					Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
					String timestamps1 = timestamp1.toString();
					String type1 = "I";
					String operation1 = "RabbitMQ Listener - NS Instantiation";
					String message1 = "[*] Correlation_id ==> " + correlation_id;
					String status1 = "";
					logger.info(
							"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
							type1, timestamps1, operation1, message1, status1);

					
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
						String operation = "RabbitMQ Listener - NS Instantiation";
						String message2 = "[*] Message coming from MANO - STATUS= " + status;
						String status2 = "";
						logger.info(
								"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
								type, timestamps, operation, message2, status2);

						if (status.equals("READY")) {

							System.out.println("RabbitMQ Message (when status ready)" + jsonObjectMessage);

							// get info for the monitoring metrics
							if (sla_id != null) {
								
								// Get service uuid (ns_uuid)
								JSONObject nsr = (JSONObject) jsonObjectMessage.getJSONObject("nsr");
								ns_id = (String) nsr.get("id");
								
								// Get network_service_name 
								db_operations dbo = new db_operations();
								db_operations.connectPostgreSQL();
								org.json.simple.JSONObject ns_name_obj = dbo.selectAgreementPerSLA(sla_id.toString());
								network_service_name = (String) ns_name_obj.get("ns_name");
								System.out.println("[*] NS NAME of the service that is being instantiated => " + network_service_name);
								db_operations.closePostgreSQL();
								
								// Get vnfrs
								JSONArray vnfrs = (JSONArray) jsonObjectMessage.getJSONArray("vnfrs");
								for (int i = 0; i < (vnfrs).length(); i++) {

									// Get vdus_reference foreach vnfr
									try {
										JSONArray vdus = (JSONArray) ((JSONObject) vnfrs.getJSONObject(i))
												.getJSONArray("virtual_deployment_units");
										for (int j = 0; j < vdus.length(); j++) {
											String vdu_reference = (String) ((JSONObject) vdus.getJSONObject(j))
													.get("vdu_reference");
											 //if vnfr is the haproxy function - continue to the monitoring creation
											// metrics
											if (vdu_reference.startsWith("haproxy") == true) {
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
									} catch (Exception e) {
										System.out.println(
												"[*] No vdus for this vnfr - use cdus instead. Exception ==> " + e);
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
													|| (cdu_reference.startsWith("vnf-ma") == true)) {
												String vnfr_name = vnfrs.getJSONObject(i).getString("name");
												System.out.println("VNFR NAME ==> " +vnfr_name);
												// get vnfr id
												String vnfr_id = (String) ((JSONObject) vnfrs.get(i)).get("id");
												vnfr_id_list.add(vnfr_id);
												// get cdu id (cdu_id)
												String cdu_id = cdus.getJSONObject(j).getString("id");
												cdu_id_list.add(cdu_id);
											}
										}
									} catch (Exception e) {
										System.out.println("[*] No cdus for this vnfr. Exception ==> " + e);
									}

								}

								// Update NSI Records - to create agreement
								db_operations.connectPostgreSQL();
								db_operations.UpdateRecordAgreement(status, correlation_id, ns_id);

								// create monitoring rules to check sla violations
								// MonitoringRules mr = new MonitoringRules();
								// MonitoringRules.createMonitroingRules(String.valueOf(sla_id), vnfr_id_list,
								// vc_id_list,nsi_id);
								
								/**
								 * call Monitoring Rules for Immersive Media Service
								 */
								MonitoringRulesImmersiveMedia mr_immersive = new MonitoringRulesImmersiveMedia();
								MonitoringRulesImmersiveMedia.createMonitoringRules(String.valueOf(sla_id), vnfr_id_list,
								vc_id_list,ns_id);
								
								/**
								 * call Monitoring Rules for Communication Service
								 */
								MonitoringRulesCommunication mr_communication = new MonitoringRulesCommunication();
								MonitoringRulesCommunication.createMonitoringRules(String.valueOf(sla_id), vnfr_id_list,
								vc_id_list,ns_id);
								
								/**
								 * call Monitoring Rules for Industrial Service
								 */
								MonitoringRulesIndustrial mr_industrial = new MonitoringRulesIndustrial();
								MonitoringRulesIndustrial.createMonitoringRules(String.valueOf(sla_id), vnfr_id_list,
								vc_id_list,ns_id);

								// UPDATE LIcense record with NSI - to create license instance
								// check if there are already instances for this ns_uuid - cust_username
								db_operations.CreateLicenseInstance(correlation_id, "active", ns_id);
								db_operations.closePostgreSQL();

							} else {
								// logging
								Timestamp timestamp3 = new Timestamp(System.currentTimeMillis());
								String timestamps3 = timestamp3.toString();
								String type3 = "I";
								String operation3 = "RabbitMQ Listener - NS Instantiation";
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
						String type4 = "I";
						String operation4 = "RabbitMQ Listener";
						String message4 = "[*] Message coming from Gatekeeper";
						String status4 = "";
						logger.info(
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
						}

						try {
							sla_uuid = (String) customer.get("sla_id");
						} catch (JSONException e) {
							sla_uuid = "";
						}

						// if sla exists create record in database
						if (sla_uuid != null && !sla_uuid.isEmpty()) {

							// CREATE AGREEMENT RECORD IN THE CUST_SLA TABLE
							cust_sla_corr cust_sla = new cust_sla_corr();
							@SuppressWarnings("unchecked")
							ArrayList<String> SLADetails = cust_sla.getSLAdetails(sla_uuid);
							sla_name = (String) SLADetails.get(1);
							sla_status = (String) SLADetails.get(0);
							String inst_status = "PENDING";
							cust_sla_corr.createCustSlaCorr(sla_uuid, sla_name, sla_status, ns_uuid, ns_name,
									cust_username, cust_email, inst_status, correlation_id);

							// CREATE LICENSE RECORD IN THE SLA_LICENSING TABLE
							// get licensing information
							db_operations.connectPostgreSQL();

							org.json.simple.JSONObject LicenseinfoTemplate = db_operations
									.getLicenseinfoTemplates(sla_uuid, ns_uuid);
							String license_type = (String) LicenseinfoTemplate.get("license_type");
							String license_exp_date = (String) LicenseinfoTemplate.get("license_exp_date");
							String allowed_instances = (String) LicenseinfoTemplate.get("allowed_instances");

							// check if there are already instances for this ns_uuid - cust_username
							int active_licenses = db_operations.countActiveLicensePerCustSLA(cust_username, sla_uuid,
									"active");
							String current_instances = String.valueOf(active_licenses + 1);

							// logging
							timestamp1 = new Timestamp(System.currentTimeMillis());
							timestamps1 = timestamp1.toString();
							type1 = "I";
							operation1 = "Instantiation with License";
							message1 = "[*] Active licenses for this customer and ns ==> " + active_licenses;
							status1 = "";
							logger.info(
									"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
									type1, timestamps1, operation1, message1, status1);

							// private licenses
							if (license_type.equals("private")) {
								// in this stage the license status should be "bought"
								// an einai to prwto instantiation enos prwtou private license
								if (active_licenses == 0) {
									db_operations.UpdateLicenseCorrelationID(sla_uuid, ns_uuid, cust_username,
											correlation_id);
									db_operations.UpdateLicenseCurrentInstances(sla_uuid, ns_uuid, cust_username,
											current_instances);
								}
								// an den einai to prwto instantiation enos prwtou private license - prepei n
								// prostethei epipleon instance mesa sto pinaka kai na ginoun ola t arecords
								// update me right current instances
								else {
									db_operations.insertLicenseRecord(sla_uuid, ns_uuid, "", cust_username, cust_email,
											license_type, license_exp_date, allowed_instances, current_instances,
											"bought", correlation_id);
									db_operations.UpdateLicenseCurrentInstances(sla_uuid, ns_uuid, cust_username,
											current_instances);
								}
							}
							// public and trial licenses
							else {
								db_operations.createTableLicensing();
								db_operations.insertLicenseRecord(sla_uuid, ns_uuid, "", cust_username, cust_email,
										license_type, license_exp_date, allowed_instances, current_instances,
										"inactive", correlation_id);
								db_operations.UpdateLicenseCurrentInstances(sla_uuid, ns_uuid, cust_username,
										current_instances);

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
			String operation1 = "RabbitMQ Listener";
			String message1 = "[*] ERROR Connecting to MQ!" + e.getMessage();
			String status1 = "";
			logger.error(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type1, timestamps1, operation1, message1, status1);
		}

	}

}