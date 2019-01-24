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

import com.rabbitmq.client.Connection;
import java.io.IOException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.rabbitmq.client.Channel;

public class ViolationsProducer {

	static Logger logger = LogManager.getLogger();

	private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
	// private static final String EXCHANGE_NAME = "son-kernel";

	public static void publishViolationMessage(JSONObject payload, Connection connection) throws Exception {
		try {

			Channel channel_test = connection.createChannel();

			channel_test.exchangeDeclare(EXCHANGE_NAME, "topic");

			String routingKey = "tng.sla.violation";

			String message = payload.toString();

			try {
				channel_test.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
			} catch (Exception e) {
				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "D";
				String operation = "RabbitMQ - Violation Producer";
				String messageLOG = e.getMessage();
				String status = "";
				logger.debug(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, messageLOG, status);
			}

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "D";
			String operation = "RabbitMQ - Violation Producer";
			String messageLOG = "[*] Sent message " + routingKey + " : " + message;
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, messageLOG, status);

		} catch (IOException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "D";
			String operation = "RabbitMQ - Violation Producer";
			String messageLOG = e.getMessage();
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, messageLOG, status);
		}
	}

	public static JSONObject createViolationMessage(String ns_uuid, String sla_uuid, String alert_time,
			String alert_state, String cust_username, Connection connection) throws Exception {

		JSONObject payload = new JSONObject();
		try {
			payload.put("ns_uuid", ns_uuid);
			payload.put("violated_sla", sla_uuid);
			payload.put("cust_username", cust_username);
			payload.put("violation_time", alert_time);
			payload.put("alert_state", alert_state);
		} catch (JSONException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "D";
			String operation = "RabbitMQ - Violation Producer";
			String messageLOG = e.getMessage();
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, messageLOG, status);
		}
		try {
			publishViolationMessage(payload, connection);
		} catch (Exception e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "D";
			String operation = "RabbitMQ - Violation Producer";
			String messageLOG = e.getMessage();
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, messageLOG, status);
		}
		return payload;
	}

}
