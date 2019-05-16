package eu.tng.rules;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.tng.correlations.db_operations;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;

/**
 * Application Lifecycle Listener implementation class ListenerStatisticInfo
 *
 */
public class ListenerStatisticInfo implements ServletContextListener {

	static Logger logger = LogManager.getLogger();

	public ListenerStatisticInfo() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "I";
		String operation = "License Check Listener";
		String message = "[*] Listener License Check Stopped! - Restarting....";
		String status = "";
		logger.info(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);

		contextInitialized(event);
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("[*] Statistic Info Listener started!!");

		final long timeInterval = 60 * 1000;
		Runnable runnable = new Runnable() {
			public void run() {
				while (true) {
					System.out.println("[*] statistic info run!! ");
					// code for task to run ends here

					// define the push gateway registry
					CollectorRegistry registry = new CollectorRegistry();
					
					// open connection to database
					db_operations db = new db_operations();
					db_operations.connectPostgreSQL();
					
					/**
					 * Nº SLA Agreements vs. Violations
					 */
					double totalAgreements = db.countTotalAgreements();
					double activeAgreements = db.countActiveAgreements();
					double violatedAgreements = db.countViolatedAgreements();
					double percentage_violated = 0;
					double percentage_active = 0;
					if (totalAgreements > 0) {
						percentage_violated = (violatedAgreements * 100) / totalAgreements;
						percentage_active = (activeAgreements * 100) / totalAgreements;
					}
					// create the gauge metric for the push gatewayl
					Gauge gauge_percentage_violated = Gauge.build().name("percentage_violated_agreements")
							.help("The percentage of the violated agreements").register(registry);
					System.out.println("[*] gauge_percentage_violated ==> " + gauge_percentage_violated);
					try {
						// Set the value of the metric
						gauge_percentage_violated.set(percentage_violated);
						System.out.println("[*] gauge_percentage_violated ==> " + gauge_percentage_violated);
						System.out.println("[*] gauge_percentage_violated with value ==> " + gauge_percentage_violated);
					} 
					finally {
						
						String pg_url = System.getenv("MONITORING_PUSH_GATEWAY");
						System.out.println("PG URL ==> " + pg_url);
						PushGateway pg = new PushGateway(pg_url);
												
						System.out.println("[*] pg => " + pg.toString());
						try {
							pg.pushAdd(registry, "SLA_job");
							System.out.print("[*] Success! Statistic info pushed to PushGateway");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("[*] Error to push gateway => " + e);
						}
					}
					System.out.println("[*] violationspercentageAll ==> " + percentage_violated);
					
					
					// close connection to databse
					db_operations.closePostgreSQL();

					try {
						Thread.sleep(timeInterval);
					} catch (InterruptedException e) {
						System.out.println("[*] Thread Error ==> " + e);
					}
				}
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();

	}

}
