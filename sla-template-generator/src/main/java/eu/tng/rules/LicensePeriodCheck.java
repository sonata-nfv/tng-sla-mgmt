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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.simple.JSONArray;

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
					System.out.println("Hello Thread scheduler !!");

					/*
					 * try { licenseExpirationDate = dateFormat.parse("15/01/2019"); } catch
					 * (ParseException e1) { e1.printStackTrace(); }
					 * System.out.println("License Expiration date" +
					 * dateFormat.format(licenseExpirationDate)); // 2016/11/16
					 */

					// get expiration date for all licenses
					db_operations dbo = new db_operations();
					db_operations.connectPostgreSQL();
					db_operations.createTableLicensing();
					JSONArray licenses = db_operations.getAllLicenses();
					db_operations.closePostgreSQL();
					System.out.println("[*] Licenses ==> " + licenses);

					checkExpDate(licenses);

					// code for task to run ends here
					try {
						Thread.sleep(timeInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			private void checkExpDate(JSONArray licenses) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

				Date currentDate = new Date();
				Date license_exp_date = null;
				System.out.println("Licences pinakas size: " + licenses.size());
				if (licenses.size() == 0) {
					System.out.print("[*] No license instances yet.");
				}
				else {
					for (int i = 0; i < licenses.size(); i++) {
						Object license_item = licenses.get(i);
						String license_exp_date_string = ((JSONObject) license_item).getString("license_exp_date");
						String license_nsi_uuid = ((JSONObject) license_item).getString("nsi_uuid");

						try {
							license_exp_date = dateFormat.parse(license_exp_date_string);
							System.out.println(dateFormat.format(license_exp_date_string));

							if (currentDate.after(license_exp_date)) {
								System.out.println("[*] currentDate >  licenseExpirationDate");
								db_operations dbo = new db_operations();
								db_operations.connectPostgreSQL();
								db_operations.deactivateLicenseForNSI(license_nsi_uuid, "inactive");
								db_operations.closePostgreSQL();
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();

	}

}
