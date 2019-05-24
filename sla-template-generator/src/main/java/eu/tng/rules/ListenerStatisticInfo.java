package eu.tng.rules;

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

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "W";
		String operation = "Statistic Info Listener";
		String message = "[*] Statistic Info Listener Stopped! - Restarting....";
		String status = "";
		logger.warn(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);

		contextInitialized(event);
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {

		// 60 secs time interval
		final long timeInterval = 60 * 1000;
		Runnable runnable = new Runnable() {
			public void run() {
				while (true) {
					
					calculateViolations();
					calculateAcquiredL();
					calculateExpiredL();
					calculateExpiredL();
					
					try {
						Thread.sleep(timeInterval);
					} 
					catch (InterruptedException e) {
						// logging
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						String timestamps = timestamp.toString();
						String type = "E";
						String operation = "Statistic Info Listener";
						String message = ("[*] Thread Error ==> " + e.getMessage());
						String status = "";
						logger.error(
								"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
								type, timestamps, operation, message, status);
					}
				}
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();

	}

	public static void calculateViolations() {

		CollectorRegistry registry = new CollectorRegistry();

		// open connection to database
		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableCustSla();

		double totalAgreements = db.countTotalAgreements();
		double violatedAgreements = db.countViolatedAgreements();

		db_operations.closePostgreSQL();

		double percentage_violated = 0;
		if (totalAgreements > 0) {
			percentage_violated = (violatedAgreements * 100) / totalAgreements;
		}
		// create the gauge metric for the push gateway
		Gauge gauge_percentage_violated = Gauge.build().name("percentage_violated_agreements")
				.help("The percentage of the violated agreements").register(registry);
		try {
			// Set the value of the metric
			gauge_percentage_violated.set(percentage_violated);
		} finally {
			String pg_url = System.getenv("MONITORING_PUSH_GATEWAY");
			PushGateway pg = new PushGateway(pg_url);
			try {
				pg.pushAdd(registry, "SLA");
			} catch (Exception e) {

				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "W";
				String operation = "Statistic Info Listener";
				String message = "[*] Error sending violations percentage to push gateway => " + e.getMessage();
				String status = "";
				logger.warn(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);
			}
		}

	}

	public static void calculateUtilizedL() {
		// open connection to database
		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();
		double licenses_utilized_number = db.countUtilizedLicense();
		CollectorRegistry registry = new CollectorRegistry();

		// create the gauge metric for the push gateway
		Gauge gauge_licenses_utilized_number = Gauge.build().name("licenses_utilized_number")
				.help("The number of utiized licenses").register(registry);
		try {
			// Set the value of the metric
			gauge_licenses_utilized_number.set(licenses_utilized_number);
		} finally {

			String pg_url = System.getenv("MONITORING_PUSH_GATEWAY");
			PushGateway pg = new PushGateway(pg_url);
			try {
				pg.pushAdd(registry, "SLA");

			} catch (Exception e) {

				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "W";
				String operation = "Statistic Info Listener";
				String message = "[*] Error sending utilized licenses to push gateway => " + e.getMessage();
				String status = "";
				logger.warn(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);

			}
		}

		db_operations.closePostgreSQL();

	}

	public static void calculateAcquiredL() {
		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();
		double licenses_acquired_number = db.countAcquiredLicense();
				
		CollectorRegistry registry = new CollectorRegistry();

		// create the gauge metric for the push gateway
		Gauge gauge_licenses_acquired_number = Gauge.build().name("licenses_acquired_number")
				.help("The number of acquired licenses").register(registry);
		try {
			// Set the value of the metric
			gauge_licenses_acquired_number.set(licenses_acquired_number);

		} finally {
			String pg_url = System.getenv("MONITORING_PUSH_GATEWAY");
			PushGateway pg = new PushGateway(pg_url);
			try {
				pg.pushAdd(registry, "SLA");
			} catch (Exception e) {

				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "W";
				String operation = "Statistic Info Listener";
				String message = "[*] Error sending aquired licenses to push gateway => " + e.getMessage();
				String status = "";
				logger.warn(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);
			}
		}
		
		
		db_operations.closePostgreSQL();

	}

	public static void calculateExpiredL() {

		db_operations db = new db_operations();
		db_operations.connectPostgreSQL();
		db_operations.createTableLicensing();

		double licenses_expired_number = db.countExpiredLicense();

		db_operations.closePostgreSQL();
		
		CollectorRegistry registry = new CollectorRegistry();

		// create the gauge metric for the push gateway
		Gauge gauge_licenses_expired_number = Gauge.build().name("licenses_expired_number")
				.help("The number of expired licenses").register(registry);
		try {
			// Set the value of the metric
			gauge_licenses_expired_number.set(licenses_expired_number);

		} finally {

			String pg_url = System.getenv("MONITORING_PUSH_GATEWAY");
			PushGateway pg = new PushGateway(pg_url);
			try {
				pg.pushAdd(registry, "SLA");
			} catch (Exception e) {

				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "W";
				String operation = "Statistic Info Listener";
				String message = "[*] Error sending expired licenses to push gateway => " + e.getMessage();
				String status = "";
				logger.warn(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);
			}
		}

		db_operations.closePostgreSQL();
	}

}
