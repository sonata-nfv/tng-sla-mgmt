package eu.tng.service_api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.owlike.genson.Context;
import com.rabbitmq.client.Connection;

import eu.tng.correlations.db_operations;
import eu.tng.messaging.RabbitMqConnector;

@Path("/ping")
@Consumes(MediaType.APPLICATION_JSON)
public class HealthAPI {

	/**
	 * Check SLAM availability in terms of micro-services's health
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getTemplates() throws InterruptedException {
		ResponseBuilder apiresponse = null;
		boolean postgresqlOK = false;
		boolean rabbitmqOK = false;
		boolean serverOK = false;
		boolean catalogueOK = false;

		// check PostgreSQL connectivity and then close the connection
		db_operations dbo = new db_operations();
		postgresqlOK = db_operations.connectPostgreSQL();
		db_operations.closePostgreSQL();
		System.out.println("OK. PostgreSQL is connected!");

		// check RabbitMQ connectivity
		RabbitMqConnector connect = new RabbitMqConnector();
		Connection connection = RabbitMqConnector.MqConnector();
		if (connection != null) {
			rabbitmqOK = true;
			System.out.println("OK. RabbitMQ is connected!");
		}

		// check Catalogue connectivity
		try {
			String url = System.getenv("CATALOGUES_URL") + "ping";
			// String url =
			// "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/ping";
			URL object = new URL(url);

			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("GET");
			int HttpResult = con.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				catalogueOK = true;
				System.out.println("OK. Catalogue is connected!");

			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// check server connectivity
		RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
		long uptime_one = rb.getUptime();
		Thread.sleep(1000 * 2); // wait a little to see if actually running
		long uptime_two = rb.getUptime();
		long up = uptime_two - uptime_one;
		if (up > 0) {
			serverOK = true;
			System.out.println("OK. SLAM server is up!");
		}
		JSONObject response = new JSONObject();
		if (rabbitmqOK == true & postgresqlOK == true && catalogueOK == true && serverOK == true) {
			// returns the current time in milliseconds
			long curentTimeInMS = System.currentTimeMillis();
			System.out.print("Current Time in milliseconds = " + curentTimeInMS);
			long alive = curentTimeInMS - rb.getUptime();
			System.out.print("SLAM alive since = " + alive);
			response.put("OK: ", "SLA Manager is available since: " + alive );
			apiresponse = Response.ok((Object) response);
			apiresponse.header("Content-Length", response.toJSONString().length());
			return apiresponse.status(200).build();
		} else {
			response.put("ERROR: ", "SLA Manager is not available.");
			apiresponse = Response.ok((Object) response);
			apiresponse.header("Content-Length", response.toJSONString().length());
			return apiresponse.status(400).build();
		}

	}

}
