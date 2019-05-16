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

		final long timeInterval = 60 * 60 * 1000;
		Runnable runnable = new Runnable() {
			public void run() {
				while (true) {
					System.out.println("[*] statistic info run!! ");
					// code for task to run ends here

					// define the push gateway registry
					CollectorRegistry registry = new CollectorRegistry();

					// open connection to database
					db_operations db = new db_operations();
					
					/**
					 ***
					 * Nº SLA Agreements vs. Violations
					 ***
					 */
					db_operations.connectPostgreSQL();
					db_operations.createTableCustSla();
					double totalAgreements = db.countTotalAgreements();
					double violatedAgreements = db.countViolatedAgreements();
					db_operations.closePostgreSQL();
					
					double percentage_violated = 0;
					if (totalAgreements > 0) {
						percentage_violated = (violatedAgreements * 100) / totalAgreements;
					}
					// create the gauge metric for the push gatewayl
					Gauge gauge_percentage_violated = Gauge.build().name("percentage_violated_agreements")
							.help("The percentage of the violated agreements").register(registry);
					try {
						// Set the value of the metric
						gauge_percentage_violated.set(percentage_violated);
					} finally {
						String pg_url = System.getenv("MONITORING_PUSH_GATEWAY");
						System.out.println("PG URL ==> " + pg_url);
						PushGateway pg = new PushGateway(pg_url);
						try {
							pg.pushAdd(registry, "SLA_job");
							System.out.print("[*] Success! Statistic info pushed to PushGateway");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("[*] Error to push gateway => " + e);
						}
					}
					System.out.println("[*] violations percntage ==> " + percentage_violated);
					

					/**
					 ***
					 * Nº Licenses Utilized
					 ***
					 */
					db_operations.connectPostgreSQL();
					db_operations.createTableLicensing();
					double licenses_utilized_number = db.countUtilizedLicense();
					db_operations.closePostgreSQL();

					// create the gauge metric for the push gateway
					Gauge gauge_licenses_utilized_number = Gauge.build().name("licenses_utilized_number")
							.help("The number of utiized licenses").register(registry);
					try {
						// Set the value of the metric
						gauge_licenses_utilized_number.set(licenses_utilized_number);
					} finally {

						String pg_url = System.getenv("MONITORING_PUSH_GATEWAY");
						System.out.println("PG URL ==> " + pg_url);
						PushGateway pg = new PushGateway(pg_url);
						try {
							pg.pushAdd(registry, "SLA_job");
							System.out.print("[*] Success! Statistic info pushed to PushGateway");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("[*] Error to push gateway => " + e);
						}
					}
					System.out.println("[*] getLicensesUtilized ==> " + licenses_utilized_number);
					
					/**
					 ***
					 * Nº Licenses Expired 
					 ***
					 */
					db_operations.connectPostgreSQL();
					double licenses_expired_number = db.countExpiredLicense();		
					db_operations.closePostgreSQL();
					
					// create the gauge metric for the push gateway
			        Gauge gauge_licenses_expired_number = Gauge.build().name("licenses_expired_number").help("The number of expired licenses").register(registry);
					try {
						// Set the value of the metric
						gauge_licenses_expired_number.set(licenses_expired_number);

					} finally {

						String pg_url = System.getenv("MONITORING_PUSH_GATEWAY");
						PushGateway pg = new PushGateway(pg_url);
						System.out.println("PG URL ==> " + pg_url);
						try {
							pg.pushAdd(registry, "SLA_job");
							System.out.print("[*] Success! Statistic info pushed to PushGateway");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("[*] Error to push gateway => " + e);
						}
					}
					System.out.println("[*] getLicensesExpired ==> " + gauge_licenses_expired_number);
					
					/**
					 ***
					 * Nº Licenses Acquired
					 ***
					 */
					db_operations.connectPostgreSQL();
					double licenses_acquired_number = db.countAcquiredLicense();
					db_operations.closePostgreSQL();
					// create the gauge metric for the push gateway
			        Gauge gauge_licenses_acquired_number = Gauge.build().name("licenses_acquired_number").help("The number of acquired licenses").register(registry);
					try {
						// Set the value of the metric
						gauge_licenses_acquired_number.set(licenses_acquired_number);

					} finally {
						String pg_url = System.getenv("MONITORING_PUSH_GATEWAY");
						System.out.println("PG URL ==> " + pg_url);
						PushGateway pg = new PushGateway(pg_url);
						try {
							pg.pushAdd(registry, "SLA_job");
							System.out.print("[*] Success! Statistic info pushed to PushGateway");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("[*] Error to push gateway => " + e);
						}
					}
					System.out.println("[*] getLicensesAcquired ==> " + licenses_acquired_number);

					
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
