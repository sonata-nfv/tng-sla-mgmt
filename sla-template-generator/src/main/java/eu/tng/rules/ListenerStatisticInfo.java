package eu.tng.rules;

import java.sql.Timestamp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
