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
package eu.tng.modify_sla_template;

import java.util.ArrayList;

import org.json.simple.JSONObject;



public class Edit_Json_Value {

	public static void Edit_value(String sla_uuid, ArrayList<String> fields, ArrayList<String> values) {

		Modify_Sla modifier = new Modify_Sla();

		Get_Sla_Template get_sla_template = new Get_Sla_Template();
		JSONObject sla_template = get_sla_template.Get_Sla(sla_uuid);

		String state = (String) sla_template.get("state");

		String uuid = (String) sla_template.get("uuid");

		if (state.equals("published")) {
			int response = modifier.switchState(uuid);
			if (response == 200) {
				modifier.editField(sla_template, sla_uuid, fields, values);
			}
		} else {
			modifier.editField(sla_template, sla_uuid, fields, values);
		}
	}

	public static void main(String[] args) {

		
		ArrayList<String> fields = new ArrayList();
		ArrayList<String> values = new ArrayList();

		fields.add("name");
		fields.add("slo_name");
		fields.add("parameter_value=70");

		values.add("testing1");
		values.add("testing2");
		values.add("90");

		Edit_value("d3fdd2ea-e816-4776-b2a2-c6dde9d9b764", fields, values);
	}

}
