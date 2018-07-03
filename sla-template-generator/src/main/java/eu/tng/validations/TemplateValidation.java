/*
 * Copyright (c) 2017 5GTANGO, UPRC ALL RIGHTS RESERVED.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Neither the name of the 5GTANGO, UPRC nor the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * This work has been performed in the framework of the 5GTANGO project, funded by the European
 * Commission under Grant number 761493 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the 5GTANGO partner consortium
 * (www.5gtango.eu).
 *
 * @author Evgenia Kapassa (MSc), UPRC
 * 
 * @author Marios Touloupou (MSc), UPRC
 * 
 */

package eu.tng.validations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class TemplateValidation {

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

		System.out.println(formatter.format(today));
		System.out.println(expireDate);

		/** check if expire date is after today **/
		if (today.compareTo(valid_until) > 0) {
			System.out.println("Today is after valid_until");
			valid_expire_date = false;
		} else if (today.compareTo(valid_until) < 0) {
			System.out.println("Today is before valid_until");
			valid_expire_date = true;
		} else if (today.compareTo(valid_until) == 0) {
			System.out.println("Today is equal to valid_until");
			valid_expire_date = false;
		} else {
			System.out.println("Invalid dates");
			valid_expire_date = false;
		}

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
			System.out.println(date);
			valid_date = true;

		} catch (ParseException e) {

			System.out.println("ERROR validating date..: " + e.getMessage());
			valid_date = false;
		}

		System.out.println("Is expireDate valid? " + valid_date);
		return valid_date;

	}

	/**
	 * Check if the date is valid
	 * 
	 * @param expireDate
	 * @return valid_gt
	 */
	public static boolean checkGuaranteeTerms(ArrayList<String> guarantees) {

		boolean valid_gt = false;

		if (guarantees == null || guarantees.size() == 0) {
			System.out.println("ERROR: You must select at least one guarantee term!");
			valid_gt = false;
		} else {
			/** check for duplicated guarantee id **/
			Set<String> gt_ids = new HashSet<String>();
			for (int i = 0; i < guarantees.size(); i++) {
				String gt_uuid_temp = guarantees.get(i);
				if (gt_ids.contains(gt_uuid_temp)) {
					System.out.println("ERROR: Duplicated guarantee id= " + gt_uuid_temp);
					valid_gt = false;
					continue;
				} else {
					valid_gt = true;
					gt_ids.add(gt_uuid_temp);
				}
			}
		}

		System.out.println("Are guarantee terms valid? " + valid_gt);
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
			System.out.println("ERROR: Template name is empty.");
			valid_name = false;
		}
		else {
			valid_name = true;
		}
		
		System.out.println("Is the template name valid? " + valid_name);
		return valid_name;

	}
}
