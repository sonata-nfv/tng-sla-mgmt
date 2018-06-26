package eu.tng.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eu.tng.correlations.cust_sla_corr;
import eu.tng.correlations.db_operations;

/**
 * Application Lifecycle Listener implementation class TestListener2
 *
 */
public class RabbitMqConsumer implements ServletContextListener {

    private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");

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

        RabbitMqConnector connect = new RabbitMqConnector();
        Connection connection = connect.MqConnector();
        Channel channel = null;
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR!" + e.getMessage());
        }

        try {
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR!" + e.getMessage());
        }
        String queueName1 = null;
        String queueName2 = null;
        String queueName3 = null;

        try {
            queueName1 = channel.queueDeclare().getQueue();
            queueName2 = channel.queueDeclare().getQueue();
            queueName3 = channel.queueDeclare().getQueue();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR!" + e.getMessage());
        }

        try {
            channel.queueBind(queueName1, EXCHANGE_NAME, "service.instances.create");
            channel.queueBind(queueName2, EXCHANGE_NAME, "son.monitoring.SLA");
            channel.queueBind(queueName3, EXCHANGE_NAME, "tng.sla.violation");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR!" + e.getMessage());
        }

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                    byte[] body) throws IOException {

                // Initialize variables
                String sla_uuid = null;
                String ns_uuid = null;
                String ns_name = null;
                String cust_uuid = null;
                String cust_email = null;
                String sla_name = null;
                String sla_status = null;
                String correlation_id = null;
                String status = null;
                JSONObject jmessage = null;

                // Parse message payload
                String message = new String(body, "UTF-8");
                System.out.println("Message received: ==> " + message);

                try {
                    YamlReader reader = new YamlReader(message);
                    Object object = reader.read();
                    Map map = (Map) object;
                    // Construct a JSONObject from a Map.
                    jmessage = new JSONObject(map);
                } catch (Exception e) {
                    System.out.print("Cannot Parse yml object " + e.getMessage());
                }

                // Get instntiation request status
                try {
                    status = (String) jmessage.get("status");
                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                }

                // Parse headers
                HashMap<String, Object> headers = (HashMap<String, Object>) properties.getHeaders();
                for (Map.Entry<String, Object> header : headers.entrySet()) {
                    if (header.getKey().equals("correlation_id")) {
                        correlation_id = header.getValue().toString();
                        System.out.println("correlation_id ==> " + correlation_id);
                    }
                }

                // if message coming from the GK
                if (status.equals("NEW")) {
                    System.out.println("Message from  GK received: " + jmessage);

                    // Get nsd data
                    try {
                        Object nsd = jmessage.get("NSD");
                        Gson gson = new Gson();
                        String nsdInString = gson.toJson(nsd);
                        JSONObject jsonNsd = new JSONObject(nsdInString);
                        ns_name = (String) jsonNsd.get("name");
                        ns_uuid = (String) jsonNsd.get("uuid");

                        System.out.println(" NS NAME ==> " + ns_name);
                        System.out.println(" NS UUID ==> " + ns_uuid);

                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }

                    // Parse customer data + sla uuid
                    try {
                        Object user_data = jmessage.get("user_data");
                        Gson gson = new Gson();
                        String user_dataInString = gson.toJson(user_data);
                        JSONObject jsonNsd = new JSONObject(user_dataInString);
                        JSONObject customer = (JSONObject) jsonNsd.get("customer");
                        cust_uuid = (String) customer.get("uuid");
                        cust_email = (String) customer.get("email");
                        sla_uuid = (String) customer.get("sla_id");

                        System.out.println(" Cust id  ==> " + cust_uuid);
                        System.out.println("Cust email  ==> " + cust_email);
                        System.out.println("SLA uuid  ==> " + sla_uuid);

                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }

                    if (sla_uuid != null) {
                        cust_sla_corr cust_sla = new cust_sla_corr();
                        @SuppressWarnings("unchecked")
                        ArrayList<String> SLADetails = cust_sla.getSLAdetails(sla_uuid);
                        sla_name = (String) SLADetails.get(1);
                        sla_status = (String) SLADetails.get(0);

                        System.out.println("SLA name  ==> " + sla_name);
                        System.out.println("SLA status  ==> " + sla_status);

                        String inst_status = "PENDING";

                        db_operations.connectPostgreSQL();
                        cust_sla_corr.createCustSlaCorr(sla_uuid, sla_name, sla_status, ns_uuid, ns_name, cust_uuid,
                                cust_email, inst_status, correlation_id);
                    }

                }
                // if message coming from the MANO
                else if (status.equals("READY")) {
                    System.out.println("Message from  MANO received: " + jmessage);
                    System.out.println("status ==> " + status);
                    db_operations dbo = new db_operations();
                    db_operations.connectPostgreSQL();
                    db_operations.UpdateRecordAgreement(status, correlation_id);

                } else if (status.equals("INSTANTIATING")) {
                    System.out.println("SERVICE STATUS IS: " + status);
                } else {
                    System.out.println("SERVICE STATUS IS: " + status);
                }
            }
        };

        Consumer consumer2 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                    byte[] body) throws IOException {
                // Initialize variables
                JSONObject jmessage = null;
                String ns_uuid = null;
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

                    ns_uuid = jmessage.getString("serviceID");
                    alert_time = jmessage.getString("time");
                    alert_name = jmessage.getString("alertname");
                    alert_state = jmessage.getString("alertstate");

                    db_operations dbo = new db_operations();
                    dbo.connectPostgreSQL();
                    dbo.createTableViolations();
                    org.json.simple.JSONArray violated_sla = dbo.getViolatedSLA(ns_uuid);
                    sla_uuid = (String) violated_sla.get(0);
                    cust_uuid = (String) violated_sla.get(1);
                    dbo.insertRecordViolation(ns_uuid, sla_uuid, alert_time, alert_state, cust_uuid);
                    JSONObject violationMessage = ViolationsProducer.createViolationMessage(ns_uuid, sla_uuid,
                            alert_time, alert_state, cust_uuid);
                    System.out.println(violationMessage);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    System.out.println("ERROR: " + e.getMessage());
                }
            }
        };

        Consumer consumer3 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                    byte[] body) throws IOException {

                JSONObject jmessage = null;

                // Parse headers
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
        try {
            channel.basicConsume(queueName1, true, consumer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR!" + e.getMessage());
        }

        try {
            channel.basicConsume(queueName2, true, consumer2);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR!" + e.getMessage());
        }

        try {
            channel.basicConsume(queueName3, true, consumer3);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR!" + e.getMessage());
        }
    }
}
