/*
 * 
 *  Copyright (c) 2015 SONATA-NFV, 2017 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 *  ALL RIGHTS RESERVED.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *              http://www.apache.org/licenses/LICENSE-2.0
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

package eu.tng.validations;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemplateValidation {

	final static Logger logger = LogManager.getLogger();

	static String class_name = TemplateValidation.class.getSimpleName();

	/**
	 * Validate if expireDate is a future date
	 * 
	 * @param expireDate
	 * @return valid_expire_date
	 */
	public static boolean checkExpireDate(String expireDate) {

		boolean valid_expire_date = false;

		/** current date */
		Date today = new Date();

		/** valid until date */
		Date valid_until = null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			valid_until = formatter.parse(expireDate);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		/** check if expire date is after today **/
		if (today.compareTo(valid_until) > 0) {

			valid_expire_date = false;

		} else if (today.compareTo(valid_until) < 0) {

			valid_expire_date = true;

		} else if (today.compareTo(valid_until) == 0) {

			valid_expire_date = false;

		} else {

			valid_expire_date = false;

		}

		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "I";
		String operation = "Validating SLA Template. Class: " + class_name;
		String message = ("[*] Valid sla template exiration date? " + valid_expire_date);
		String status = "";
		logger.info(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);
		
		return valid_expire_date;

	}

	/**
	 * Check if the date is valid
	 * 
	 * @param expireDate
	 * @return valid_date
	 */
	public static boolean checkValidDate(String expireDate) {

		boolean valid_date = false;

		if (expireDate == null) {
			valid_date = false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setLenient(false);

		try {
			/** if not valid, it will throw ParseException **/
			Date date = sdf.parse(expireDate);
			valid_date = true;

		} catch (ParseException e) {

			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Validating SLA Template. Class: " + class_name;
			String message = ("[*] ERROR validating date! " + e.getMessage());
			String status = "";
			logger.error(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			valid_date = false;
		}
		
		//logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "I";
		String operation = "Validating SLA Template. Class: " + class_name;
		String message = ("[*] SLA date format ? " + valid_date);
		String status = "";
		logger.info(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);
		
		return valid_date;

	}

	/**
	 * Check if Guarantee Terms are valid
	 * 
	 * @param expireDate
	 * @return valid_gt
	 */
	public static boolean checkGuaranteeTerms(ArrayList<String> guarantees) {

		boolean valid_gt = false;


		/** check for duplicated guarantee id **/
		Set<String> gt_ids = new HashSet<String>();
		for (int i = 0; i < guarantees.size(); i++) {
			String gt_uuid_temp = guarantees.get(i);
			if (gt_ids.contains(gt_uuid_temp)) {
				// logging
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String timestamps = timestamp.toString();
				String type = "E";
				String operation = "Validating SLA Template. Class: "+ class_name;
				String message = ("[*] Error: Duplicated guarantee id= " + gt_uuid_temp);
				String status = "";
				logger.error(
						"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
						type, timestamps, operation, message, status);

				valid_gt = false;
				continue;
			} else {
				valid_gt = true;
				gt_ids.add(gt_uuid_temp);
			}
		}

		return valid_gt;

	}

	/**
	 * 
	 * @param templateName
	 * @return valid_name
	 */
	public static boolean checkName(String templateName) {

		boolean valid_name = false;
		templateName = templateName.trim();

		if (templateName == null || templateName.isEmpty()) {
			// logging
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timestamps = timestamp.toString();
			String type = "E";
			String operation = "Validating SLA Template. Class: " + class_name;
			String message = ("[*]ERROR: Template name is empty.");
			String status = "";
			logger.error(
					"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
					type, timestamps, operation, message, status);

			valid_name = false;
		} else {
			valid_name = true;
		}

		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "I";
		String operation = "Validating SLA Template. Class: " + class_name;
		String message = ("[*] Is the template name valid? " + valid_name);
		String status = "";
		logger.info(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);
		
		return valid_name;

	}

	/**
	 * Validate the creation of an SLA Template
	 * 
	 * @param templateName
	 * @param expireDate
	 * @param guarantees
	 * @return valid_create_template
	 */
	public ArrayList<Boolean> validateCreateTemplate(String templateName, String expireDate,
			ArrayList<String> guarantees) {
		ArrayList<Boolean> valid_create_template = new ArrayList<>();

		boolean checkValidDate = checkValidDate(expireDate);
		boolean checkExpireDate = checkExpireDate(expireDate);
		boolean checkGuaranteeTerms = checkGuaranteeTerms(guarantees);
		boolean checkName = checkName(templateName);

		valid_create_template.add(checkValidDate);
		valid_create_template.add(checkExpireDate);
		valid_create_template.add(checkGuaranteeTerms);
		valid_create_template.add(checkName);

		// logging
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestamps = timestamp.toString();
		String type = "I";
		String operation = "Validating SLA Template. Class: " + class_name;
		String message = ("[*] Are the SLA Template parameters valid? " + valid_create_template);
		String status = "";
		logger.info(
				"{\"type\":\"{}\",\"timestamp\":\"{}\",\"start_stop\":\"\",\"component\":\"tng-sla-mgmt\",\"operation\":\"{}\",\"message\":\"{}\",\"status\":\"{}\",\"time_elapsed\":\"\"}",
				type, timestamps, operation, message, status);
		
		return valid_create_template;

	}
}
