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
import java.util.Date;

public class TemplateValidation {

	public static boolean checkExpireDate(String expireDate) {

		boolean valid_expire_date = false;
				
		/** current date */
		Date today = new Date();

		/** valid until date */
		Date valid_until = null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		expireDate = "15/07/2020";
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
		} 
		else if (today.compareTo(valid_until) < 0) {
			System.out.println("Today is before valid_until");
			valid_expire_date = true;
		} 
		else if (today.compareTo(valid_until) == 0) {
			System.out.println("Today is equal to valid_until");
			valid_expire_date = false;
		} 
		else {
			System.out.println("Invalid dates");
			valid_expire_date = false;
		}

		return valid_expire_date;

	}

	public static void main(String[] args) {
		//checkExpireDate("2/07/2018");
	}

}
