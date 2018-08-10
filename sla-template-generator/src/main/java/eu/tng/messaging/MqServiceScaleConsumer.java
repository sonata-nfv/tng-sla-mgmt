package eu.tng.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eu.tng.correlations.db_operations;

/**
 * Application Lifecycle Listener implementation class MqServiceScaleConsumer
 *
 */
public class MqServiceScaleConsumer implements ServletContextListener {

	private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
	// private static final String EXCHANGE_NAME = "son-kernel";

	/**
	 * Servlet Destroyed
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println(" [*] Listener Service-Instance-Scale Consumer stopped!");
	}

	/**
	 * Servlet Initialized
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		Channel channel_scale;
		final Connection connection;
		String queueName_scale;

		try {
			RabbitMqConnector connect = new RabbitMqConnector();
			connection = connect.getconnection();

			channel_scale = connection.createChannel();
			channel_scale.exchangeDeclare(EXCHANGE_NAME, "topic");
			queueName_scale = "slas.service.instance.scale";
			channel_scale.queueDeclare(queueName_scale, true, false, false, null);
			System.out.println(" [*]  Binding queue to topic...");
			channel_scale.queueBind(queueName_scale, EXCHANGE_NAME, "service.instance.scale");
			System.out.println(" [*] Bound to topic \"service.instance.scale\"");
			System.out.println(" [*] Waiting for messages.");

			Consumer consumer_scale = new DefaultConsumer(channel_scale) {

				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					
					// Initialize variables
					String correlation_id = null;
					String nsi_uuid = "";
					String scaling_status = "null";
					JSONObject jsonObjectMessage = null;
				
					// Parse message payload
					String message = new String(body, "UTF-8");
					// parse the yaml and convert it to json
					Yaml yaml = new Yaml();
					Map<String, Object> map = (Map<String, Object>) yaml.load(message);
					jsonObjectMessage = new JSONObject(map);

					System.out.println("START READING HEADERS FROM MESSAGE.....");
					correlation_id = (String) properties.getCorrelationId();
					System.out.println(" [*] Correlation_id (from service.instance.scale queue) ==> " + correlation_id);
					
					/** if message coming from the MANO - contain status key **/
					if (jsonObjectMessage.has("status")) {
						System.out.println(" [*] Message coming from MANO .....");
						System.out.println(" [*] Message as JSONObject ==> " + jsonObjectMessage);
						scaling_status = (String) jsonObjectMessage.get("status");
						System.out.println(" [*] Scaling Status ==> " + scaling_status);
					}
					
					/** if message coming from the Policy Manager - doesn't contain status key **/
					else {
						System.out.println(" [*] Message coming from Policy Manager.....");
						System.out.println(" [*] Message as JSONObject ==> " + jsonObjectMessage);
						System.out.println(" [*] Scaling Status ==> " + scaling_status);
						
						nsi_uuid = (String) jsonObjectMessage.get("service_instance_id");
						System.out.println(" [*] NSI_UUID for scaling ==> " + nsi_uuid);
						
						db_operations.connectPostgreSQL();
						db_operations.createTableLicenseScaling();
						db_operations.UpdateCorrelationIdLicenseTable(nsi_uuid, correlation_id);
						System.out.println("[*] Correlation id in license scaling table updated succesfully!");
						db_operations.closePostgreSQL();
						
					}		

				}

			};

			// consumer
			channel_scale.basicConsume(queueName_scale, true, consumer_scale);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR Connecting to MQ!" + e.getMessage());
		}

	}

}
