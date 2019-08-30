/*
 * 
 *  Copyright (c) 2015 SONATA-NFV, 2017 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 *  ALL RIGHTS RESERVED.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  Neither the name of the SONATA-NFV, 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 *  nor the names of its contributors may be used to endorse or promote
 *  products derived from this software without specific prior written
 *  permission.
 *  
 *  This work has been performed in the framework of the SONATA project,
 *  funded by the European Commission under Grant number 671517 through
 *  the Horizon 2020 and 5G-PPP programmes. The authors would like to
 *  acknowledge the contributions of their colleagues of the SONATA
 *  partner consortium (www.sonata-nfv.eu).
 *  
 *  This work has been performed in the framework of the 5GTANGO project,
 *  funded by the European Commission under Grant number 761493 through
 *  the Horizon 2020 and 5G-PPP programmes. The authors would like to
 *  acknowledge the contributions of their colleagues of the 5GTANGO
 *  partner consortium (www.5gtango.eu).
 * 
 */

package eu.tng.rules;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import eu.tng.correlations.db_operations;

/**
 * Application Lifecycle Listener implementation class SlaPeriodCheck
 *
 */
public class SlaPeriodCheck implements ServletContextListener {
	
	static Logger logger = LogManager.getLogger();

	// LOGS VARIABLES
	String timestamps = "";
	String type = "";
	String operation = "";
	String message = "";
	String status = "";
	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    
    /**
	 * @contextDestroyed
	 */
    public void contextDestroyed(ServletContextEvent event)  { 
		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "I";
		String operation = "SLA Period Check Listener";
		String message = "[*] Listener SLA Period Check Stopped! - Restarting....";
		String status = "";
		logger.info(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);

		contextInitialized(event);
    }

    /**
	 * @contextInitialized
	 */
    public void contextInitialized(ServletContextEvent arg0)  { 
		// logging
		timestamp = new Timestamp(System.currentTimeMillis());
		timestamps = timestamp.toString();
		type = "I";
		operation = "SLA Period Check Listener";
		message = ("[*] SLA Period Check Listener started!!");
		status = "";
		logger.info(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);

		// run every 24h - 24*60*60*1000 add 24 hours delay between job executions.
		final long timeInterval = 24 * 60 * 60 * 1000;
		Runnable runnable = new Runnable() {

			public void run() {
				while (true) {
					
					// code for task to run ends here
					try {
						Thread.sleep(6000);
					} 
					catch (InterruptedException e) {
						// logging
						timestamp = new Timestamp(System.currentTimeMillis());
						timestamps = timestamp.toString();
						type = "I";
						operation = "SLA Period Check Listener";
						message = ("[*] Thread Error ==> " + e);
						status = "";
						logger.info(
								"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
								type, timestamps, operation, message, status);
					}
					
					
					// code for task to run
					Date currentDate = new Date();
					Date exp_date = new Date();

					db_operations db = new db_operations();
					db_operations.connectPostgreSQL();
					db.createTableNSTemplate();
					JSONArray templates = db_operations.getAllTemplates();
					db_operations.closePostgreSQL();
					
					if (templates.size() == 0) {
						// logging
						timestamp = new Timestamp(System.currentTimeMillis());
						timestamps = timestamp.toString();
						type = "I";
						operation = "SLA Period Check Listener";
						message = ("[*] No templates yet.");
						status = "";
						logger.info(
								"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
								type, timestamps, operation, message, status);
					} 
					else {
						for (int i = 0; i < templates.size(); i++) {
							JSONObject template_item = (JSONObject) templates.get(i);
							String sla_uuid = (String) ((JSONObject) template_item).get("sla_uuid");
							String sla_exp_date = getSlaExpiration(sla_uuid);
							
							if (sla_exp_date != null || sla_exp_date != "") {
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
								String dateInString = sla_exp_date;
								try {
									exp_date = formatter.parse(dateInString.replaceAll("Z$", "+0000"));
								} catch (ParseException e) {

									// logging
									timestamp = new Timestamp(System.currentTimeMillis());
									timestamps = timestamp.toString();
									type = "I";
									operation = "SLA Period Check Listener";
									message = ("[*] Error formating date: " + e.getMessage());
									status = "";
									logger.info(
											"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
											type, timestamps, operation, message, status);
								}
								
								if (currentDate.after(exp_date)) {
									
									// logging
									timestamp = new Timestamp(System.currentTimeMillis());
									timestamps = timestamp.toString();
									type = "I";
									operation = "SLA Period Check Listener";
									message = ("[*] SLA Template Expired: Current date after SLA expiration date");
									status = "";
									logger.info(
											"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
											type, timestamps, operation, message, status);								
									
								} 
								else {
									// logging
									timestamp = new Timestamp(System.currentTimeMillis());
									timestamps = timestamp.toString();
									type = "I";
									operation = "SLA Period Check Listener";
									message = ("[*] SLA Template not expired yet.");
									status = "";
									logger.info(
											"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
											type, timestamps, operation, message, status);
								}
							}

						}

					}

					// code for task to run ends here
					try {
						Thread.sleep(timeInterval);
					} 
					catch (InterruptedException e) {
						// logging
						timestamp = new Timestamp(System.currentTimeMillis());
						timestamps = timestamp.toString();
						type = "I";
						operation = "License Check Listener";
						message = ("[*] Thread Error ==> " + e);
						status = "";
						logger.info(
								"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
								type, timestamps, operation, message, status);
					}
				}
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();
    }


	protected String getSlaExpiration(String sla_uuid) {
		String expiration_date = null;
		try {
			String url = System.getenv("CATALOGUES_URL") + "slas/template-descriptors/" + sla_uuid;
			URL object = new URL(url);

			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("GET");

			con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String sresponse = response.toString();
			org.json.JSONObject jsonObj = new org.json.JSONObject(sresponse);

			try {
				org.json.JSONObject slad = jsonObj.getJSONObject("slad");
				org.json.JSONObject sla_template = slad.getJSONObject("sla_template");
				expiration_date = sla_template.getString("expiration_date");

			} catch (Exception e2) {
				// logging
				timestamp = new Timestamp(System.currentTimeMillis());
				timestamps = timestamp.toString();
				type = "I";
				operation = "Getting sla expiration date. Class: " + this.getClass().getSimpleName();
				message = "[*] This SLA has not expiration date..";
				status = String.valueOf(404);
				logger.info(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);
			}

		} catch (Exception e) {
			// logging
			timestamp = new Timestamp(System.currentTimeMillis());
			timestamps = timestamp.toString();
			type = "E";
			operation = "Getting sla expiration date. Class: " + this.getClass().getSimpleName();
			message = "[*] Error SLA Not Found";
			status = String.valueOf(404);
			logger.error(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return expiration_date;
	}
	
}
