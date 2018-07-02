package eu.tng.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.rabbitmq.client.*;

import eu.tng.correlations.cust_sla_corr;
import eu.tng.correlations.db_operations;
import eu.tng.rules.MonitoringRules;

/**
 * Application Lifecycle Listener implementation class TestListener2
 *
 */
public class RabbitMqConsumer implements ServletContextListener {

	//private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
	private static final String EXCHANGE_NAME = "son-kernel";

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
		final Connection connection = connect.MqConnector();
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
		String queueName_service_instance = "slas.service.instances.create";
		String queueName_son_sla = "slas.son.monitoring.SLA";
		String queueName_sla_violation = "slas.tng.sla.violation";
		String queueName_service_terminate = "slas.service.instance.terminate";

		try {
		    /*
			queueName_service_instance = channel.queueDeclare().getQueue();
			queueName_son_sla = channel.queueDeclare().getQueue();
			queueName_sla_violation = channel.queueDeclare().getQueue();
			queueName_service_terminate = channel.queueDeclare().getQueue();
			*/
		    channel.queueDeclare(queueName_service_instance, true, false, false, null);
		    channel.queueDeclare(queueName_son_sla, true, false, false, null);
		    channel.queueDeclare(queueName_sla_violation, true, false, false, null);
		    channel.queueDeclare(queueName_service_terminate, true, false, false, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR!" + e.getMessage());
		}

		try {
			channel.queueBind(queueName_service_instance, EXCHANGE_NAME, "service.instances.create");
			channel.queueBind(queueName_son_sla, EXCHANGE_NAME, "son.monitoring.SLA");
			channel.queueBind(queueName_sla_violation, EXCHANGE_NAME, "tng.sla.violation");
			channel.queueBind(queueName_service_terminate, EXCHANGE_NAME, "service.instance.terminate");

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
				ArrayList<String> vnfrs_list = new ArrayList<String>();
				ArrayList<String> vdus_list = new ArrayList<String>();

				// Parse message payload
				String message = new String(body, "UTF-8");

				try {
					Yaml yaml = new Yaml();
					Map<String, Object> map = (Map<String, Object>) yaml.load(message);

					JSONObject jsonObject = new JSONObject(map);
					jmessage = jsonObject;

				} catch (Exception e) {
					System.out.print("Cannot Parse yml object " + e.getMessage());
				}

				// Get instantiation request status
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
				if (status == null) {
					System.out.println("Message from  GK received: " + jmessage);

					// Get nsd data
					try {
						JSONObject nsd = (JSONObject) jmessage.get("NSD");
						ns_name = (String) nsd.get("name");
						ns_uuid = (String) nsd.get("uuid");

						System.out.println(" NS NAME ==> " + ns_name);
						System.out.println(" NS UUID ==> " + ns_uuid);

					} catch (Exception e) {
						System.out.println("ERROR: " + e.getMessage());
					}

					// Parse customer data + sla uuid
					try {
						JSONObject user_data = (JSONObject) jmessage.get("user_data");
						JSONObject customer = (JSONObject) user_data.get("customer");

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

					String sla_id = null;
					String ns_id = null;
					
					// get data for the monitoring rules
					try {
						// Get sla_id
						sla_id = (String) jmessage.get("sla_id");
						System.out.println(" SLA ID " + sla_id);

						// Get service uuid 
						JSONObject nsr = (JSONObject) jmessage.get("nsr");
						ns_id = (String)nsr.get("id");


						// Get vnfrs
						JSONArray vnfrs = (JSONArray) jmessage.get("vnfrs");
						for (int i = 0; i < (vnfrs).length(); i++) {
							String vnf_id = (String) ((JSONObject) vnfrs.get(i)).get("id");
							vnfrs_list.add(vnf_id);
							System.out.println(" VNfrs List " + vnfrs_list);

							// Get vdus foreach vnfr
							JSONArray vdus = (JSONArray) ((JSONObject) vnfrs.get(i)).get("virtual_deployment_units");
							for (int j = 0; j < vdus.length(); j++) {
								String vdu_id = (String) ((JSONObject) vdus.get(j)).get("id");
								vdus_list.add(vdu_id);
								System.out.println(" VDUs List " + vdus_list);
							}

						}

					} catch (Exception e) {
						System.out.println("ERROR sto catc: " + e.getMessage());
					}
					
					// call the create rules method
					MonitoringRules mr = new MonitoringRules();
					MonitoringRules.createMonitroingRules(sla_id, vnfrs_list, vdus_list, ns_id);

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

					try {
						JSONObject violationMessage = ViolationsProducer.createViolationMessage(ns_uuid, sla_uuid,
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

		Consumer consumer4 = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println("TERMINATING MESSAGE RECIEVED" + message);
			}
		};
		try {
			channel.basicConsume(queueName_service_instance, true, consumer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR 1!" + e.getMessage());
		}

		try {
			channel.basicConsume(queueName_son_sla, true, consumer2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR 2!" + e.getMessage());
		}

		try {
			channel.basicConsume(queueName_sla_violation, true, consumer3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR 3!" + e.getMessage());
		}

		try {
			channel.basicConsume(queueName_service_terminate, true, consumer4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR!" + e.getMessage());
		}
	}
}
