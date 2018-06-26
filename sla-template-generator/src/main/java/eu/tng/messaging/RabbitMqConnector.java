package eu.tng.messaging;


import com.rabbitmq.client.*;


public class RabbitMqConnector {
	
	 Connection connection = null;
	
	/** RabbitMQ Connector **/
	public Connection MqConnector() {

		String connector_url = System.getenv("MQSERVER");
		//String connector_url = "amqp://guest:guest@83.212.238.159:5672";
		
		ConnectionFactory factory = new ConnectionFactory();
		try {
		factory.setUri(connector_url);
		factory.setConnectionTimeout(60);
		
		connection = factory.newConnection();
		
		}catch (Exception e)
		{
			System.out.println(e.toString());
		}
	
		return connection;
	}
	
	

	/** RabbitMQ Close Connection **/
	public boolean CloseConnection() {
		boolean result = false;
		try {
			connection.close();
			result = true;
		}catch(Exception e)
		{
			System.out.println(e.toString());
		}		
		return result;
	}
}
