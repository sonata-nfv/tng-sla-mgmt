package eu.tng.messaging;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.JSONException;
import org.json.JSONObject;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MqSlaViolationsConsumer implements ServletContextListener {

    // private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
    private static final String EXCHANGE_NAME = "son-kernel";

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("Server stopped");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        Channel channel_violations;
        Connection connection;
        String queueName_sla_violations;

        try {
            RabbitMqConnector connect = new RabbitMqConnector();
            connection = connect.getconnection();

            channel_violations = connection.createChannel();
            channel_violations.exchangeDeclare(EXCHANGE_NAME, "topic");
            queueName_sla_violations = "slas.tng.sla.violation";
            channel_violations.queueDeclare(queueName_sla_violations, true, false, false, null);
            System.out.println(" [*]  Binding queue to topic...");
            channel_violations.queueBind(queueName_sla_violations, EXCHANGE_NAME, "tng.sla.violation");
            System.out.println(" [*] Bound to topic \"tng.sla.violation\"");
            System.out.println(" [*] Waiting for messages.");

            Consumer consumer_sla_violations = new DefaultConsumer(channel_violations) {

                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                        byte[] body) throws IOException {

                    JSONObject jmessage = null;                  
                    try {
                        String message = new String(body, "UTF-8");
                        jmessage = new JSONObject(message);
                        System.out.println("VIOLATION MESSAGE " + jmessage);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        System.out.println("ERROR: " + e.getMessage());
                    }
                }

            };

            // consumer
            channel_violations.basicConsume(queueName_sla_violations, true, consumer_sla_violations);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR Connecting to MQ!" + e.getMessage());
        }
    }

}
