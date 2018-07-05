package eu.tng.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.simple.JSONObject;
import org.yaml.snakeyaml.Yaml;

import com.rabbitmq.client.*;

import eu.tng.correlations.cust_sla_corr;
import eu.tng.correlations.db_operations;
import eu.tng.rules.MonitoringRules;

public class RabbitMqConsumer implements ServletContextListener {

	private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
	// private static final String EXCHANGE_NAME = "son-kernel";

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("Server stopped");
	}
	
    /**
     * Default constructor.
     */
    public RabbitMqConsumer() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * 
	 * 
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {

		Channel channel_service_instance;
		Connection connection;
		String queueName_service_instance;
		String exchangeName;

		try {
			RabbitMqConnector connect = new RabbitMqConnector();
			connection = connect.MqConnector();
			channel_service_instance = connection.createChannel();
			channel_service_instance.exchangeDeclare(EXCHANGE_NAME, "topic");
			queueName_service_instance = "slas.service.instances.create";
			channel_service_instance.queueDeclare(queueName_service_instance, true, false, false, null);
			System.out.println(" [*]  Binding queue to topics...");
			channel_service_instance.queueBind(queueName_service_instance, EXCHANGE_NAME, "service.instances.create");
			System.out.println(" [*] Bound to topic \"service.instances.create\"");
			System.out.println(" [*] Waiting for messages.");

			Consumer consumer_service_instance = new DefaultConsumer(channel_service_instance) {
				
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,byte[] body) throws IOException {
					// Initialize variables
					String sla_uuid = "";
					String ns_uuid = null;
					String ns_name = null;
					String cust_uuid = null;
					String cust_email = null;
					String sla_name = null;
					String sla_status = null;
					String correlation_id = null;
					String status = null;
					JSONObject jsonObjectMessage  = null;
					ArrayList<String> vnfrs_list = new ArrayList<String>();
					ArrayList<String> vdus_list = new ArrayList<String>();
					
					
					// Parse message payload
	                String message = new String(body, "UTF-8");
	                // parse the yaml and convert it to json
	                Yaml yaml= new Yaml();
	                Map<String,Object> map= (Map<String, Object>) yaml.load(message);
	                jsonObjectMessage = new JSONObject(map);
	    			System.out.println(" [*] Message as JSONObject ==> " + jsonObjectMessage);

	                // if message coming from the MANO - contain status key
	    			if (jsonObjectMessage.containsKey("status")){
		    			System.out.println(" [*] Message coming from MANO.....");

	                    status = (String) jsonObjectMessage.get("status");
		    			System.out.println(" [*] STATUS ==> " + status);

	                }
	                // if message coming from the GK - doesn't contain status key
	    			else {
		    			System.out.println(" [*] Message coming from Gatekeeper.....");
		    			System.out.println(" [*] STATUS ==> " + status);

	    			}

				}

			};
			
			// consumer
			channel_service_instance.basicConsume(queueName_service_instance, true, consumer_service_instance);

			
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR Connecting to MQ!" + e.getMessage());
		}

	}

}