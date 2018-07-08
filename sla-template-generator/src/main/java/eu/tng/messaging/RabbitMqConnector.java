package eu.tng.messaging;

import com.rabbitmq.client.*;

public class RabbitMqConnector {

	static Connection connection = null;

	/** RabbitMQ Connector **/
	public static Connection MqConnector() {

		String connector_url = System.getenv("MQSERVER");
		//String connector_url = "amqp://guest:guest@83.212.238.159:5672";

		ConnectionFactory factory = new ConnectionFactory();
		try {
			factory.setUri(connector_url);
			factory.setConnectionTimeout(60);

			factory.setAutomaticRecoveryEnabled(true);

			connection = factory.newConnection();
			setconnection(connection);

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return connection;
	}

	/** Getter for Connection **/
	public Connection getconnection() {
		return connection;
	}

	/** Setter for Connection **/
	public static void setconnection(Connection connection) {
		RabbitMqConnector.connection = connection;
	}

	/** RabbitMQ Close Connection **/
	public boolean CloseConnection() {
		boolean result = false;
		try {
			connection.close();
			result = true;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}

}
