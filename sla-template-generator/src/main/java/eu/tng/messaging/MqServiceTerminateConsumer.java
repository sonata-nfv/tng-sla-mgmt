package eu.tng.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.simple.JSONObject;
import org.yaml.snakeyaml.Yaml;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MqServiceTerminateConsumer implements ServletContextListener {

    // private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
    private static final String EXCHANGE_NAME = "son-kernel";

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
            channel_service_terminate.queueBind(queueName_service_terminate, EXCHANGE_NAME, "service.instance.terminate");
            System.out.println(" [*] Bound to topic \"service.instances.terminate\"");
            System.out.println(" [*] Waiting for messages.");

            Consumer consumer_service_terminate = new DefaultConsumer(channel_service_terminate) {

                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                        byte[] body) throws IOException {

                    // Parse message payload
                    String message = new String(body, "UTF-8");

                    System.out.println("Message for terminating service received: " + message);
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
