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

import eu.tng.correlations.db_operations;

public class MqMonitoringConsumer implements ServletContextListener {

    private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
    //private static final String EXCHANGE_NAME = "son-kernel";

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("Server stopped");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        Channel channel_monitor;
        final Connection connection;
        String queueName_monitor;

        try {
            RabbitMqConnector connect = new RabbitMqConnector();
            connection = connect.getconnection();

            channel_monitor = connection.createChannel();
            channel_monitor.exchangeDeclare(EXCHANGE_NAME, "topic");
            queueName_monitor = "slas.son.monitoring.SLA";
            channel_monitor.queueDeclare(queueName_monitor, true, false, false, null);
            System.out.println(" [*]  Binding queue to topic...");
            channel_monitor.queueBind(queueName_monitor, EXCHANGE_NAME, "son.monitoring.SLA");
            System.out.println(" [*] Bound to topic \"son.monitoring.SLA\"");
            System.out.println(" [*] Waiting for messages.");

            Consumer consumer_monitor = new DefaultConsumer(channel_monitor) {

                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                        byte[] body) throws IOException {

                    // Initialize variables
                    JSONObject jmessage = null;
                    String nsi_uuid = null;
                    String alert_time = null;
                    String alert_name = null;
                    String alert_state = null;
                    String sla_uuid = null;
                    String cust_uuid = null;

                    // Parse headers
                    try {
                        String message = new String(body, "UTF-8");
                        jmessage = new JSONObject(message);
                        System.out.println(jmessage);

                        nsi_uuid = jmessage.getString("serviceID"); // this is the service instantce id
                        alert_time = jmessage.getString("time");
                        alert_name = jmessage.getString("alertname");
                        alert_state = jmessage.getString("alertstate");

                        db_operations dbo = new db_operations();
                        db_operations.connectPostgreSQL();
                        db_operations.createTableViolations();

                        org.json.simple.JSONObject violated_sla = dbo.getViolatedSLA(nsi_uuid);
                        sla_uuid = (String) violated_sla.get("sla_uuid");
                        cust_uuid = (String) violated_sla.get("cust_uuid");

                        // insert the violation in the violation database
                        db_operations.insertRecordViolation(nsi_uuid, sla_uuid, alert_time, alert_state, cust_uuid);
                        // update the agreement status to 'violated'
                        db_operations.UpdateAgreementStatus(nsi_uuid, "VIOLATED");
                        try {
                            JSONObject violationMessage = ViolationsProducer.createViolationMessage(nsi_uuid, sla_uuid,
                                    alert_time, alert_state, cust_uuid, connection);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        System.out.println("ERROR: " + e.getMessage());
                    }
                }

            };

            // consumer
            channel_monitor.basicConsume(queueName_monitor, true, consumer_monitor);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR Connecting to MQ!" + e.getMessage());
        }
    }

}
