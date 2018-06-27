package eu.tng.messaging;

import com.rabbitmq.client.Connection;
import java.io.IOException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.rabbitmq.client.Channel;

public class ViolationsProducer {

      private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
    //private static final String EXCHANGE_NAME = "son-kernel";

    public static void publishViolationMessage(JSONObject payload, Connection connection) throws Exception {
        try {

            Channel channel_test = connection.createChannel();

            channel_test.exchangeDeclare(EXCHANGE_NAME, "topic");

            String routingKey = "tng.sla.violation";

            String message = payload.toString();

            try {
                channel_test.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Error publishing message " + e.getMessage());
        }
    }

    public static JSONObject createViolationMessage(String ns_uuid, String sla_uuid, String alert_time,
            String alert_state, String cust_uuid, Connection connection) throws Exception {

        JSONObject payload = new JSONObject();
        try {
            payload.put("ns_uuid", ns_uuid);
            payload.put("violated_sla", sla_uuid);
            payload.put("cust_uuid", cust_uuid);
            payload.put("violation_time", alert_time);
            payload.put("alert_state", alert_state);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            publishViolationMessage(payload, connection);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return payload;
    }

}
