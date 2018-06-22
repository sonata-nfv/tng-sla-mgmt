package eu.tng.messaging;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.rabbitmq.client.Channel;

public class ViolationsProducer {

	private final static String QUEUE_NAME_violations = "tng.sla.violation";
	
	public static void publishViolationMessage(JSONObject payload) {
		try {
			RabbitMqConnector connect = new RabbitMqConnector();
			Connection connection = connect.MqConnector();
			Channel violations_channel = connection.createChannel();
			
			violations_channel.queueDeclare(QUEUE_NAME_violations, true, false, false, null);
			JSONObject message = payload;
			violations_channel.basicPublish("", QUEUE_NAME_violations, null, message.toString().getBytes());
			System.out.println(" [x] Sent '" + message + "'");
			
			violations_channel.close();
			connection.close();
		} 
		catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static JSONObject createViolationMessage(String ns_uuid, String sla_uuid, String alert_time, String alert_state,String cust_uuid) {
		
		JSONObject payload = new JSONObject();
		try {
			payload.put("ns_uuid", ns_uuid);
			payload.put("violated_sla", sla_uuid);
			payload.put("cust_uuid", cust_uuid);
			payload.put("violation_time", alert_time);
			payload.put("alert_state", alert_state);
		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		publishViolationMessage(payload);
		return payload;
	}


}
