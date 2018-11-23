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

package eu.tng.template_gen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetGuarantee {

	static Logger logger = LogManager.getLogger();

	public ArrayList<JSONObject> getGuarantee(ArrayList<String> guarantees) {

		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		JSONObject guarantee = null;
		ArrayList<JSONObject> guaranteeArr = new ArrayList<JSONObject>();

		try {
			File testf = new File(this.getClass().getResource("/slos_list_Y2.json").toURI());
			jsonObject = (JSONObject) parser.parse(new FileReader(testf));

			JSONObject guaranteeObject = (JSONObject) jsonObject;
			JSONArray guaranteeTerms = (JSONArray) guaranteeObject.get("guaranteeTerms");

			for (int j = 0; j < guarantees.size(); j++) {
				for (int i = 0; i < guaranteeTerms.size(); i++) {
					JSONObject guaranteeTermsObject = (JSONObject) guaranteeTerms.get(i);
					String guaranteeId = (String) guaranteeTermsObject.get("guaranteeID");

					if (guaranteeId.equals(guarantees.get(j))) {
						// System.out.println(guaranteeId);
						if (j == 0) {
							guarantee = (JSONObject) guaranteeTerms.get(i);
							guaranteeArr.add(guarantee);
						} else {
							guarantee = (JSONObject) guaranteeTerms.get(i);
							guaranteeArr.add(guarantee);
						}
					}

				}
			}

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Get Guarantee Terms";
			String message = "Guarantee Terms received succesfully";
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

		} catch (FileNotFoundException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "D";
			String operation = "Get Guarantee Terms";
			String message = e.getMessage();
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		} catch (IOException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "D";
			String operation = "Get Guarantee Terms";
			String message = e.getMessage();
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		} catch (ParseException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "D";
			String operation = "Get Guarantee Terms";
			String message = e.getMessage();
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		} catch (URISyntaxException e) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "D";
			String operation = "Get Guarantee Terms";
			String message = e.getMessage();
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);
		}
		return guaranteeArr;

	}

}
