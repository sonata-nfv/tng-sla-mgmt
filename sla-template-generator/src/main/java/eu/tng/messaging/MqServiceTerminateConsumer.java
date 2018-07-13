package eu.tng.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.*;
import org.yaml.snakeyaml.Yaml;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eu.tng.correlations.db_operations;

public class MqServiceTerminateConsumer implements ServletContextListener {

	private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
	// private static final String EXCHANGE_NAME = "son-kernel";

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("Server stopped");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Channel channel_service_terminate;
		Connection connection;
		String queueName_service_terminate;

		try {
			RabbitMqConnector connect = new RabbitMqConnector();
			connection = connect.getconnection();

			channel_service_terminate = connection.createChannel();
			channel_service_terminate.exchangeDeclare(EXCHANGE_NAME, "topic");
			queueName_service_terminate = "slas.service.instance.terminate";
			channel_service_terminate.queueDeclare(queueName_service_terminate, true, false, false, null);
			System.out.println(" [*]  Binding queue to topic...");
			channel_service_terminate.queueBind(queueName_service_terminate, EXCHANGE_NAME,
					"service.instance.terminate");
			System.out.println(" [*] Bound to topic \"service.instances.terminate\"");
			System.out.println(" [*] Waiting for messages.");

			Consumer consumer_service_terminate = new DefaultConsumer(channel_service_terminate) {

				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {

					JSONObject jsonObjectMessage = null;
					String correlation_id = null;
					String status = null;
					String nsi_uuid = null;

					// Parse message payload
					String message = new String(body, "UTF-8");
					System.out.println("Message for terminating service received: " + message);
					// parse the yaml and convert it to json
					Yaml yaml = new Yaml();
					Map<String, Object> map = (Map<String, Object>) yaml.load(message);
					jsonObjectMessage = new JSONObject(map);

					System.out.println("START READING HEADERS FROM MESSAGE.....");
					correlation_id = (String) properties.getCorrelationId();
					System.out.println(" [*] Correlation_id ==> " + correlation_id);

					// get status
					status = (String) jsonObjectMessage.get("status");

					/** if message coming from the MANO - contain status key **/
					if (jsonObjectMessage.has("status")) {
						if (status.equals("READY")) {

							// make the agreement status 'TERMINATED'
							db_operations dbo = new db_operations();
							db_operations.connectPostgreSQL();
							db_operations.TerminateAgreement("TERMINATED", correlation_id);
						}

					}
					/** if message coming from the GK - does not contain status key **/
					else {

						System.out.println(" [*] Message coming from Gatekeeper.....");
						System.out.println(" [*] Message as JSONObject ==> " + jsonObjectMessage);
						nsi_uuid = (String) jsonObjectMessage.get("instance_id");
						System.out.println(" [*] instance_id  ==> " + nsi_uuid);
						
						
						// make the agreement status 'TERMINATED'
						db_operations dbo = new db_operations();
						db_operations.connectPostgreSQL();
						// make update record to change the correlation id  -  the correlation id of the termination messaging
						db_operations.UpdateCorrelationID(nsi_uuid, correlation_id);
				
					}

				}

			};

			// consumer
			channel_service_terminate.basicConsume(queueName_service_terminate, true, consumer_service_terminate);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR Connecting to MQ!" + e.getMessage());
		}
	}
}
