package eu.tng.rules;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import eu.tng.correlations.db_operations;

/**
 * Application Lifecycle Listener implementation class StatisticInfoListener
 *
 */
public class StatisticInfoListener implements ServletContextListener {

	static Logger logger = LogManager.getLogger();

    /**
     * Default constructor. 
     */
    public StatisticInfoListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event)  { 
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
    public void contextInitialized(ServletContextEvent arg0)  { 
		System.out.println("[*] StatisticInfoListener started!!");
		
		// run every 1 minute 60*1000 - add 60 sec delay between job executions.
		final long timeInterval = 60 * 1000;
		Runnable runnable = new Runnable() {

			public void run() {
				while (true) {
					// code for task to run
					System.out.println("[*] StatisticInfoListener runnable method triggered");

					// call StatisticInfo.java class to send data to prometheus
					StatisticInfo si = new StatisticInfo();
					
					si.violationspercentageAll();
					//si.getLicensesAcquired();
					//si.getLicensesExpired();
					//si.getLicensesUtilized();

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
