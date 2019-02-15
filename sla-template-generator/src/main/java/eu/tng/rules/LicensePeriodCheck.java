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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import eu.tng.correlations.db_operations;

/**
 * Application Lifecycle Listener implementation class LicensePeriodCheck
 *
 */
public class LicensePeriodCheck implements ServletContextListener {

	static Logger logger = LogManager.getLogger();

	/**
	 * Default constructor.
	 */
	public LicensePeriodCheck() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @contextDestroyed
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
	 * contextInitialized
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("[*] License Check Listener started!!");

		// run every 24h - 24*60*60*1000 add 24 hours delay between job executions.
		final long timeInterval = 24 * 60 * 60 * 1000;
		Runnable runnable = new Runnable() {

			public void run() {
				while (true) {
					// code for task to run
					DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

					Date currentDate = new Date();

					// get expiration date for all licenses
					db_operations dbo = new db_operations();
					db_operations.connectPostgreSQL();
					db_operations.createTableLicensing();
					org.json.simple.JSONArray licenses = db_operations.getAllLicenses();
					db_operations.closePostgreSQL();

					Date license_exp_date = null;

					if (licenses.size() == 0) {
						System.out.println("[*] No licenses yet.");
					} else {
						for (int i = 0; i < licenses.size(); i++) {
							JSONObject license_item = (JSONObject) licenses.get(i);
							String license_exp_date_string = (String) ((JSONObject) license_item)
									.get("license_exp_date");

							if (license_exp_date_string != null || license_exp_date_string != "") {
								try {
									license_exp_date = format.parse(license_exp_date_string);
								} catch (ParseException e) {
									System.out.println("Error formating the expiration date ==> " + e);
								}

								String license_nsi_uuid = (String) ((JSONObject) license_item).get("nsi_uuid");

								if (currentDate.after(license_exp_date)) {
									db_operations.connectPostgreSQL();
									db_operations.deactivateLicenseForNSI(license_nsi_uuid, "inactive");
									System.out.println("[*] License de-activated");
									db_operations.closePostgreSQL();		
									
									
									// send termination request for the service
									//HttpClient httpClient = new DefaultHttpClient(); 
									HttpClient httpClient = HttpClientBuilder.create().build();
									try {
									    HttpPost request = new HttpPost("http://pre-int-sp-ath.5gtango.eu:32002/api/v3/requests");
									    StringEntity params =new StringEntity("{\"instance_uuid\":\"41297d25-d925-4bc5-a555-9ee1f0c84219\",\"request_type\":\"TERMINATE_SERVICE\"}");
									    request.addHeader("content-type", "application/json");
									    request.setEntity(params);
									    HttpResponse response = httpClient.execute(request);

									    System.out.println("RESPONSE CODE ==> " +response.getStatusLine());

									}catch (Exception ex) {
										System.out.println("ERROR in the api request for terminating the service");
									}
									
									
									
									
//									try {
//										//String url = System.getenv("CATALOGUES_URL") + "slas/template-descriptors";
//										String url = "http://pre-int-sp-ath.5gtango.eu:32002/api/v3/requests";
//										URL object = new URL(url);
//										HttpURLConnection con = (HttpURLConnection) object.openConnection();
//										con.setDoOutput(true);
//										con.setDoInput(true);
//										con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//										con.setRequestProperty("Accept", "application/json");
//										con.setRequestMethod("POST");
//										
//										JSONObject body   = new JSONObject();
//										body.put("instance_uuid","41297d25-d925-4bc5-a555-9ee1f0c84219");
//										body.put("request_type", "TERMINATE_SERVICE");
//										System.out.println("[*] TERMINATION BODY ==> " + body.toString());	
//										OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
//										wr.write(body.toString());
//										wr.flush();
//										
//										int HttpResult = con.getResponseCode();
//										System.out.println("[*] Termination request response code ==> " + HttpResult);
//									}
//									catch (Exception e) {
//										System.out.println("ERROR in the api request for terminating the service");
//									}
									

								} else {
									System.out.println("[*] License not expired! ");
								}
							}

						}

					}

					// code for task to run ends here
					try {
						Thread.sleep(timeInterval);
					} catch (InterruptedException e) {
						System.out.println("ERROR!!!! ==> " + e);
					}
				}
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();

	}

}
