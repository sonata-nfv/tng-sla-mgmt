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
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.*;
import org.yaml.snakeyaml.Yaml;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import eu.tng.correlations.db_operations;

public class MqServiceTerminateConsumer implements ServletContextListener {

	static Logger logger = LogManager.getLogger();

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	String timestamps = "";
	String type = "";
	String operation = "";
	String message = "";
	String status = "";

	private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
	// private static final String EXCHANGE_NAME = "son-kernel";

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "W";
		String operation = "RabbitMQ Listener - Service Termination Consumer";
		String message = "[*] Listener Service Termination stopped - Restarting....";
		String status = "";
		logger.warn(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		final Channel channel_service_terminate;
		Connection connection;
		String queueName_service_terminate;

		try {
			RabbitMqConnector connect = new RabbitMqConnector();
			connection = connect.getconnection();

			channel_service_terminate = connection.createChannel();
			channel_service_terminate.exchangeDeclare(EXCHANGE_NAME, "topic");
			queueName_service_terminate = "slas.service.instance.terminate";
			channel_service_terminate.queueDeclare(queueName_service_terminate, true, false, false, null);
			// logging
			timestamp = new Timestamp(System.currentTimeMillis());
			timestamps = timestamp.toString();
			type = "I";
			operation = "RabbitMQ Listener - NS Termination";
			message = "[*] Binding queue to topic...";
			status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			channel_service_terminate.basicQos(1);
			channel_service_terminate.queueBind(queueName_service_terminate, EXCHANGE_NAME,
					"service.instance.terminate");

			// logging
			timestamp = new Timestamp(System.currentTimeMillis());
			timestamps = timestamp.toString();
			type = "I";
			operation = "RabbitMQ Listener - NS Termination";
			message = "[*] Bound to topic \"service.instances.terminate\"\"";
			status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			// logging
			timestamp = new Timestamp(System.currentTimeMillis());
			timestamps = timestamp.toString();
			type = "I";
			operation = "RabbitMQ Listener - NS Termination";
			message = "[*] Waiting for messages.";
			status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			DeliverCallback deliverCallback = new DeliverCallback() {
				@Override
				public void handle(String consumerTag, Delivery delivery) throws IOException {

					JSONObject jsonObjectMessage = null;
					Object correlation_id = null;
					Object status = null;
					Object nsi_uuid = null;

					// Parse message payload
					String message = new String(delivery.getBody(), "UTF-8");

					// Ack the message
					channel_service_terminate.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

					// parse the yaml and convert it to json
					Yaml yaml = new Yaml();
					@SuppressWarnings("unchecked")
					Map<String, Object> map = (Map<String, Object>) yaml.load(message);
					System.out.println("Message for terminating service received (printed as MAP): " + map);
					jsonObjectMessage = new JSONObject(map);

					correlation_id = (String) delivery.getProperties().getCorrelationId();

					/** if message coming from the MANO - contain status key **/
					if (jsonObjectMessage.has("status")) {

						status = map.get("status");

						if (status.equals("READY")) {
							new db_operations();
							db_operations.connectPostgreSQL();
							// termonate agreement
							db_operations.TerminateAgreement("TERMINATED", correlation_id.toString());

							// deactivate license
							db_operations.deactivateLicense(correlation_id.toString(), "inactive");

							// reduce current instances
							org.json.simple.JSONObject linfo = db_operations
									.getLicensePerCorrID(correlation_id.toString());
							String sla_uuid = linfo.get("sla_uuid").toString();
							String ns_uuid = linfo.get("ns_uuid").toString();
							String cust_username = linfo.get("cust_username").toString();
							String current_instances = linfo.get("current_instances").toString();
							String updated_current_instances = "";

							if (current_instances.equals("0")) {
								updated_current_instances = ("0");
							} else {
								int ci_int = Integer.parseInt(current_instances);
								int ci_int_updated = (ci_int - 1);
								updated_current_instances = String.valueOf(ci_int_updated);
							}

							db_operations.UpdateLicenseCurrentInstances(sla_uuid, ns_uuid, cust_username,updated_current_instances);

							db_operations.closePostgreSQL();

							// logging
							timestamp = new Timestamp(System.currentTimeMillis());
							timestamps = timestamp.toString();
							type = "I";
							operation = "RabbitMQ Listener NS Termination";
							message = "[*] Service TERMINATED, DB Updated";
							status = "";
							logger.info(
									"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
									type, timestamps, operation, message, status);
						}
					}
					/** if message coming from the GK - does not contain status key **/
					else {
					    // logging
                        timestamp = new Timestamp(System.currentTimeMillis());
                        timestamps = timestamp.toString();
                        type = "I";
                        operation = "RabbitMQ Listener NS Termination";
                        message = "[*] Message from GK received";
                        status = "";
                        logger.info(
                                "{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
                                type, timestamps, operation, message, status);
                        
                        
						nsi_uuid = map.get("service_instance_uuid");

						new db_operations();
						db_operations.connectPostgreSQL();
						db_operations.createTableCustSla();
						// make update record to change the correlation id - the correlation id of the
						// termination messaging
						db_operations.UpdateCorrelationID(nsi_uuid.toString(), correlation_id.toString());
						db_operations.UpdateLicenseCorrelationIDperNSI(nsi_uuid.toString(), correlation_id.toString());
						db_operations.closePostgreSQL();
					}

				}

			};

			channel_service_terminate.basicConsume(queueName_service_terminate, false, deliverCallback,
					new CancelCallback() {
						@Override
						public void handle(String consumerTag) throws IOException {
						}
					});

		} catch (IOException e) {
			// logging
			timestamp = new Timestamp(System.currentTimeMillis());
			timestamps = timestamp.toString();
			type = "E";
			operation = "RabbitMQ Listener";
			message = "[*] ERROR Connecting to MQ!" + e.getMessage();
			status = "";
			logger.error(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
	}
}
