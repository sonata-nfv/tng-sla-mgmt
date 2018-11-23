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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CreateTemplate {

	static Logger logger = LogManager.getLogger();

	Nsd getNsd = new Nsd();

	@SuppressWarnings("unchecked")
	public JSONObject createTemplate(String nsd_uuid, String templateName, String expireDate,
			ArrayList<String> guarantees) {

		GetGuarantee guarantee = new GetGuarantee();
		ArrayList<JSONObject> guaranteeArr = guarantee.getGuarantee(guarantees);

		/** get network service descriptor for the given nsId */
		GetNsd nsd = new GetNsd();
		boolean correctNsUuid = nsd.getNSD(nsd_uuid);

		if (nsd.getNSD(nsd_uuid) == false) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "D";
			String operation = "Create SLA Template";
			String message = "Wrong NSD";
			String status = "";
			logger.debug(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			return null;
		} else {
			/** GENERATE THE TEMPLATE **/
			/**************************/

			/** useful variables **/
			TimeZone tz = TimeZone.getTimeZone("UTC");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // iso date format yyyy-MM-dd'T'HH:mm'Z'
			df.setTimeZone(tz);

			/** current date */
			Date date = new Date();
			String offered_date = df.format(date);

			/** valid until date */
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String dateInString = expireDate;
			System.out.println(dateInString);
			Date date2 = null;
			try {
				date2 = formatter.parse(dateInString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String validUntil = df.format(date2);

			/** generate the template */
			// ** root element **/
			JSONObject root = new JSONObject();
			root.put("descriptor_schema",
					"https://raw.githubusercontent.com/sonata-nfv/tng-schema/master/service-descriptor/nsd-schema.yml");
			root.put("vendor", "tango-sla-mgmt");
			root.put("name", templateName);
			root.put("version", "0.1");
			root.put("author", "Evgenia Kapassa, Marios Touloupou");
			root.put("description", "");
			/** sla_template object **/
			JSONObject sla_template = new JSONObject();
			sla_template.put("template_name", templateName);
			sla_template.put("offer_date", offered_date);
			sla_template.put("expiration_date", validUntil);
			root.put("sla_template", sla_template);
			/** ns object **/
			JSONObject ns = new JSONObject();
			ns.put("ns_uuid", nsd_uuid);
			ns.put("ns_name", getNsd.getName());
			ns.put("ns_vendor", getNsd.getVendor());
			ns.put("ns_version", getNsd.getVersion());
			ns.put("ns_description", getNsd.getDescription());
			sla_template.put("ns", ns);
			/** guaranteeTerms array **/
			JSONArray guaranteeTerms = new JSONArray();
			for (int counter = 0; counter < guaranteeArr.size(); counter++) {
				guaranteeTerms.add(guaranteeArr.get(counter));
			}
			ns.put("guaranteeTerms", guaranteeTerms);

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "I";
			String operation = "Create SLA Template";
			String message = "Succesfully created the template";
			String status = "";
			logger.info(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			return root;

		}

	}

}
