package eu.tng.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;

import eu.tng.correlations.cust_sla_corr;
import eu.tng.correlations.db_operations;
import eu.tng.rules.MonitoringRules;

public class RabbitMqConsumer implements ServletContextListener {

	// private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
	private static final String EXCHANGE_NAME = "son-kernel";

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("Server stopped");
	}

	/**
	 * Default constructor.
	 */
	public RabbitMqConsumer() {}

	/**
	 * 
	 * 
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {

		Channel channel_service_instance;
		Connection connection;
		String queueName_service_instance;

		try {
			RabbitMqConnector connect = new RabbitMqConnector();
			connection = RabbitMqConnector.MqConnector();

			channel_service_instance = connection.createChannel();
			channel_service_instance.exchangeDeclare(EXCHANGE_NAME, "topic");
			queueName_service_instance = "slas.service.instances.create";
			channel_service_instance.queueDeclare(queueName_service_instance, true, false, false, null);
			System.out.println(" [*]  Binding queue to topics...");
			channel_service_instance.queueBind(queueName_service_instance, EXCHANGE_NAME, "service.instances.create");
			System.out.println(" [*] Bound to topic \"service.instances.create\"");
			System.out.println(" [*] Waiting for messages.");

			Consumer consumer_service_instance = new DefaultConsumer(channel_service_instance) {

				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					// Initialize variables
					String status = "test";
					JSONObject jsonObjectMessage = null;
					ArrayList<String> vnfrs_list = new ArrayList<String>();
					ArrayList<String> vdus_list = new ArrayList<String>();
					String correlation_id = null;

					// Parse message payload
					String message = new String(body, "UTF-8");
					// parse the yaml and convert it to json
					Yaml yaml = new Yaml();
					Map<String, Object> map = (Map<String, Object>) yaml.load(message);
					jsonObjectMessage = new JSONObject(map);

					System.out.println("START READING HEADERS FROM MESSAGE.....");
					correlation_id = (String) properties.getCorrelationId();
					System.out.println(" [*] Correlation_id ==> " + correlation_id);

					/** if message coming from the MANO - contain status key **/
					if (jsonObjectMessage.has("status")) {
						System.out.println(" [*] Message coming from MANO.....");
						System.out.println(" [*] Message as JSONObject ==> " + jsonObjectMessage);
						status = (String) jsonObjectMessage.get("status");
						System.out.println(" [*] STATUS ==> " + status);

						if (status.equals("READY")) {
							db_operations dbo = new db_operations();
							db_operations.connectPostgreSQL();
							db_operations.UpdateRecordAgreement(status, correlation_id);

							// get info for the monitoring metrics
							String sla_id = "";
							String ns_id = "";
							// Get sla_id
							sla_id = (String) jsonObjectMessage.get("sla_id");
							// Get service uuid
							JSONObject nsr = (JSONObject) jsonObjectMessage.getJSONObject("nsr");
							ns_id = (String) nsr.get("id");
							// Get vnfrs
							JSONArray vnfrs = (JSONArray) jsonObjectMessage.getJSONArray("vnfrs");
							for (int i = 0; i < (vnfrs).length(); i++) {
								String vnf_id = (String) ((JSONObject) vnfrs.getJSONObject(i)).get("id");
								vnfrs_list.add(vnf_id);
								System.out.println("[*] VNfrs List from MANO message ==> " + vnfrs_list);
								// Get vdus foreach vnfr
								JSONArray vdus = (JSONArray) ((JSONObject) vnfrs.getJSONObject(i))
										.getJSONArray("virtual_deployment_units");
								for (int j = 0; j < vdus.length(); j++) {
									String vdu_id = (String) ((JSONObject) vdus.getJSONObject(j)).get("id");
									vdus_list.add(vdu_id);
									System.out.println(" [*] VDUs List from MANO message ==> " + vdus_list);
								}

							}

							// call the create rules method
							MonitoringRules mr = new MonitoringRules();
							MonitoringRules.createMonitroingRules(sla_id, vnfrs_list, vdus_list, ns_id);

						}

					}
					/** if message coming from the GK - doesn't contain status key **/
					else {
						System.out.println(" [*] Message coming from Gatekeeper.....");
						System.out.println(" [*] Message as JSONObject ==> " + jsonObjectMessage);
						// status = (String) jsonObjectMessage.get("status");
						System.out.println(" [*] STATUS ==> " + status);

						// Initialize valiables
						String sla_uuid = null;
						String ns_uuid = null;
						String ns_name = null;
						String cust_uuid = null;
						String cust_email = null;
						String sla_name = null;
						String sla_status = null;

						// Get nsd data
						JSONObject nsd = jsonObjectMessage.getJSONObject("NSD");
						ns_name = (String) nsd.get("name");
						ns_uuid = (String) nsd.get("uuid");
						System.out.println(" NS NAME ==> " + ns_name);
						System.out.println(" NS UUID ==> " + ns_uuid);

						// Parse customer data + sla uuid
						JSONObject user_data = (JSONObject) jsonObjectMessage.getJSONObject("user_data");
						JSONObject customer = (JSONObject) user_data.getJSONObject("customer");
						cust_uuid = (String) customer.get("uuid");
						cust_email = (String) customer.get("email");
						sla_uuid = (String) customer.get("sla_id");
						System.out.println(" Cust id  ==> " + cust_uuid);
						System.out.println("Cust email  ==> " + cust_email);
						System.out.println("SLA uuid  ==> " + sla_uuid);

						// if sla exists create record in database
						if (sla_uuid != null && !sla_uuid.isEmpty()) {

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
									cust_email, inst_status, "test_correlation");
						}

					}

				}

			};

			// service instantiation consumer
			channel_service_instance.basicConsume(queueName_service_instance, true, consumer_service_instance);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(" [*] ERROR Connecting to MQ!" + e.getMessage());
		}

	}

}