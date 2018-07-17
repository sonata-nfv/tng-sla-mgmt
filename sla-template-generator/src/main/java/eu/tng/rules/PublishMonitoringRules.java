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

import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import eu.tng.correlations.ns_template_corr;

public class PublishMonitoringRules {

	/**
	 * Publish monitoring rules to the Monitoring Manager Y1 : Communication via
	 * RestAPI
	 * 
	 * @param ns_id
	 */

	public JSONObject publishMonitringRules(JSONObject body, String ns_id) {

		System.out.println(body);
		String service_id = ns_id;
		System.out.println("NS ID" + ns_id);

		try {

			String url = "http://pre-int-sp-ath.5gtango.eu:8000/api/v1/slamng/rules/service/" + service_id
					+ "/configuration ";
			URL object = new URL(url);

			HttpURLConnection con = (HttpURLConnection) object.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("POST");
			
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(body.toString());
			wr.flush();

			StringBuilder sb = new StringBuilder();
			int HttpResult = con.getResponseCode();

			if (HttpResult == HttpURLConnection.HTTP_OK) {
				System.out.println("SLA rules were send");
				System.out.println(con.getResponseMessage());

			} else {
				// ERROR sending the monitoring rules 
				System.out.println("ERROR sending the monitoring rules : " + con.getResponseMessage());
			}
		} catch (Exception e) {
			System.out.println("ERROR connecting with monitoring api  : " + e.getMessage());
		}

		return null;
	}

}
