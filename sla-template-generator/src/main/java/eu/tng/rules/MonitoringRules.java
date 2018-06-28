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

package eu.tng.rules;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MonitoringRules {

	/**
	 * Create monitoring rules based on a instantiated NS and the selected SLA
	 **/
	public static JSONObject createMonitroingRules(String sla_uuid, ArrayList<String> vnfrs_list,
			ArrayList<String> vdus_list) {

		ArrayList<String> slo_list = new ArrayList<String>();
		slo_list.add("A");

		System.out.println(sla_uuid);
		System.out.println(vnfrs_list);
		System.out.println(vdus_list);

		if (sla_uuid != null) {
			/**
			 * Create the rules
			 **/
			// ** root **/
			JSONObject root = new JSONObject();

			// ** sla id field **/
			root.put("sla_cnt", sla_uuid);

			// ** vnfs array **/
			JSONArray vnfs = new JSONArray();
			for (int i = 0; i < vnfrs_list.size(); i++) {
				JSONObject nvfid = new JSONObject();
				nvfid.put("nvfid", vnfrs_list.get(i));
				vnfs.add(nvfid);

				// ** vdus array **/
				JSONArray vdus = new JSONArray();
				for (int j = 0; j < vdus_list.size(); j++) {
					JSONObject vduid = new JSONObject();
					vduid.put("vdu_id", vdus_list.get(j));
					vdus.add(vduid);

					// ** rules array **/
					JSONArray rules = new JSONArray();
					// JSONObject rule = new JSONObject();
					for (int k = 0; k < slo_list.size(); k++) {
						JSONObject rule = new JSONObject();
						rule.put("name", "sla:rule:" + "name");
						rule.put("duration", "duration");
						rule.put("description", "description");
						rule.put("condition", "condition");
						JSONObject notification_type = new JSONObject();
						notification_type.put("id", "2");
						notification_type.put("type", "rabbitmq");
						rule.put("notification_type", notification_type);
						
						rules.add(rule);
					}

					vduid.put("rules", rules);
				}
				nvfid.put("vdus", vdus);

			}
			root.put("vnfs", vnfs);
			System.out.println("root ==> " + root);

		} else {
			System.out.println("ERROR: Unable to create rules. SLA ID is null");
		}

		return null;
	}

}
