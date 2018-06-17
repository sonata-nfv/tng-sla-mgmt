package eu.tng.messaging;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * Application Lifecycle Listener implementation class TestListener2
 *
 */
public class RabbitMqConsumer implements ServletContextListener {

	private final static String QUEUE_NAME = "service.instance.create";

	/**
	 * Default constructor.
	 */
	public RabbitMqConsumer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("Server stopped");
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("Server started");
		RabbitMqConnector connect = new RabbitMqConnector();

		try {
			Connection connection = connect.MqConnector();
			Channel channel = connection.createChannel();

			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");

					YamlReader reader = new YamlReader(message);
					Object object = reader.read();
					Map map = (Map) object;
					System.out.println(map.get("NSD"));

				}
			};
			channel.basicConsume(QUEUE_NAME, true, consumer);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
