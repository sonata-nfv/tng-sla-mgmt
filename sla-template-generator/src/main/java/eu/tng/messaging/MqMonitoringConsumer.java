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
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import eu.tng.correlations.db_operations;

public class MqMonitoringConsumer implements ServletContextListener {

	static Logger logger = LogManager.getLogger();

	private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
	// private static final String EXCHANGE_NAME = "son-kernel";

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "I";
		String operation = "RabbitMQ Listener";
		String message = "[*] Listener Monitoring Consumer stopped";
		String status = "";
		logger.info(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		final Channel channel_monitor;
		final Connection connection;
		String queueName_monitor;

		try {
			RabbitMqConnector connect = new RabbitMqConnector();
			connection = connect.getconnection();

			channel_monitor = connection.createChannel();
			channel_monitor.exchangeDeclare(EXCHANGE_NAME, "topic");
			queueName_monitor = "slas.son.monitoring.SLA";
			channel_monitor.queueDeclare(queueName_monitor, true, false, false, null);
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

			channel_monitor.basicQos(1);
			channel_monitor.queueBind(queueName_monitor, EXCHANGE_NAME, "son.monitoring.SLA");
			// logging
			Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
			String timestamps1 = timestamp1.toString();
			String type1 = "I";
			String operation1 = "RabbitMQ Listener";
			String message1 = "[*] Bound to topic \"son.monitoring.SLA\"\"";
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
					JSONObject jmessage = null;
					String nsi_uuid = null;
					String alert_time = null;

					String alert_state = null;
					String sla_uuid = null;
					String cust_username = null;

					// Parse headers
					try {
						String message = new String(delivery.getBody(), "UTF-8");
						
						//Ack the message
						channel_monitor.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
						
						jmessage = new JSONObject(message);
						System.out.println(jmessage);

						nsi_uuid = jmessage.getString("serviceID"); // this is the service instantce id
						alert_time = jmessage.getString("startsAt");
						alert_state = jmessage.getString("status");

						db_operations dbo = new db_operations();
						db_operations.connectPostgreSQL();
						db_operations.createTableViolations();

						
						// get the sla agreements details for this violation
						org.json.simple.JSONObject violated_sla = db_operations.getViolatedSLA(nsi_uuid);
						sla_uuid = (String) violated_sla.get("sla_uuid");
						cust_username = (String) violated_sla.get("cust_username");

						// insert the violation in the violation database
						db_operations.insertRecordViolation(nsi_uuid, sla_uuid, alert_time, alert_state, cust_username);
						db_operations.UpdateAgreementStatus(nsi_uuid);
						db_operations.closePostgreSQL();

						try {
							JSONObject violationMessage = ViolationsProducer.createViolationMessage(nsi_uuid, sla_uuid,
									alert_time, alert_state, cust_username, connection);
						} catch (Exception e) {
							Timestamp timestamp = new Timestamp(System.currentTimeMillis());
							String timestamps = timestamp.toString();
							String type = "E";
							String operation = "Parse message from Monitoring through RabbitMQ";
							String messageLog = e.getMessage();
							String status = "";
							logger.error(
									"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
									type, timestamps, operation, messageLog, status);
						}

					} catch (JSONException e) {
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						String timestamps = timestamp.toString();
						String type = "E";
						String operation = "Parse message from Monitoring through RabbitMQ";
						String messageLog = e.getMessage();
						String status = "";
						logger.error(
								"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
								type, timestamps, operation, messageLog, status);
					}
					
					
				}

			};

			channel_monitor.basicConsume(queueName_monitor, false, deliverCallback, new CancelCallback() {
                @Override
                public void handle(String consumerTag) throws IOException { }
            });

		} catch (IOException e) {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Parse message from Monitoring through RabbitMQ";
			String messageLog = e.getMessage();
			String status = "";
			logger.error(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, messageLog, status);
		}
	}

}
