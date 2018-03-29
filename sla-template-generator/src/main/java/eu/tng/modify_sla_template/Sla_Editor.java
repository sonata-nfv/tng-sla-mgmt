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

import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Sla_Editor {

	/**
	 * Part of the EDIT TEMPLATE Rest AAPI The Edit_value method is called in order
	 * to change a specific field in the sla template descriptor. The desired
	 * template is defined through the sla_uuid
	 **/

	public static String Edit_value(String sla_uuid, String field, String value) {

		String new_uuid = "";
		Modify_Sla modifier = new Modify_Sla();

		/** get the desired sla template that is going to be updated **/
		Get_Sla_Template get_sla_template = new Get_Sla_Template();
		JSONObject sla_template = get_sla_template.Get_Sla(sla_uuid);

		/** get the current state {published/unpublished} of the template **/
		String state = (String) sla_template.get("state");

		/**
		 * if published we are not allowed to make changes - make first the template
		 * state=unpublished
		 **/
		if (state.equals("published")) {
			int response = modifier.switchState(sla_uuid);
			if (response == 200) {
				new_uuid = modifier.editField(get_sla_template.Get_Sla(sla_uuid), sla_uuid, field, value);
			}
		} else {
			new_uuid = modifier.editField(sla_template, sla_uuid, field, value);

		}
		sla_template = null;
		return new_uuid;
	}

	/**
	 * Part of the MODIFY TEMPLATE Rest AAPI The Add_Fields method is called in
	 * order to add an entire slo into a specific sla template descriptor. The
	 * desired template is defined through the sla_uuid
	 **/

	@SuppressWarnings("unchecked")
	public static String Add_Fields(String sla_uuid, List<String> objectives, List<String> slo_value,
			List<String> slo_definition, List<String> slo_unit, List<String> metric_list, List<String> expression_list,
			List<String> expression_unit_list, List<String> rate_list, List<String> parameter_unit,
			List<String> parameter_definition, List<String> parameter_value, List<String> parameter_name) {

		/** get the desired sla template that is going to be updated **/

		Modify_Sla ms = new Modify_Sla();
		Get_Sla_Template get_sla_template = new Get_Sla_Template();
		JSONObject root = get_sla_template.Get_Sla(sla_uuid);

		/** Remove root element inserted by the Catalogue **/
		Object slad = root.get("slad");
		JSONObject jsonObject = (JSONObject) slad;

		/** Increase the template version **/
		double version = Double.parseDouble((String) jsonObject.get("version"));
		version += 0.1;
		String new_version = String.valueOf(version);
		jsonObject.put("version", String.valueOf(new_version));

		JSONObject sla_template = (JSONObject) jsonObject.get("sla_template");
		JSONObject ns = (JSONObject) sla_template.get("ns");
		JSONArray objective = (JSONArray) ns.get("objectives");

		/** get the old siZe in order to manage the new IDs accordingly **/
		int old_objectives_size = objective.size();

		for (int i = 0; i < objectives.size(); i++) {

			/**
			 * manage the empty arraylists Use Case: may not all the fields are completed by
			 * the user. In that case we create emty fields into the slo to be further
			 * edited in a later phase
			 **/

			try {
				objectives.get(i);
			} catch (IndexOutOfBoundsException e) {
				objectives.add(i, "");
			}

			try {
				slo_definition.get(i);
			} catch (IndexOutOfBoundsException e) {
				slo_definition.add(i, "");
			}

			try {
				slo_unit.get(i);
			} catch (IndexOutOfBoundsException e) {
				slo_unit.add(i, "");
			}

			try {
				slo_value.get(i);
			} catch (IndexOutOfBoundsException e) {
				slo_value.add(i, "");
			}

			try {
				metric_list.get(i);
			} catch (IndexOutOfBoundsException e) {
				metric_list.add(i, "");
			}

			try {
				rate_list.get(i);
			} catch (IndexOutOfBoundsException e) {
				rate_list.add(i, "");
			}

			try {
				expression_list.get(i);
			} catch (IndexOutOfBoundsException e) {
				expression_list.add(i, "");
			}

			try {
				expression_unit_list.get(i);
			} catch (IndexOutOfBoundsException e) {
				expression_unit_list.add(i, "");
			}

			try {
				parameter_unit.get(i);
			} catch (IndexOutOfBoundsException e) {
				parameter_unit.add(i, "");
			}

			try {
				parameter_definition.get(i);
			} catch (IndexOutOfBoundsException e) {
				parameter_definition.add(i, "");
			}

			try {
				parameter_value.get(i);
			} catch (IndexOutOfBoundsException e) {
				parameter_value.add(i, "");
			}

			try {
				parameter_name.get(i);
			} catch (IndexOutOfBoundsException e) {
				parameter_name.add(i, "");
			}

			/** build the slo block **/
			JSONObject slo_obj = new JSONObject();
			slo_obj.put("slo_id", "slo" + (old_objectives_size + (i + 1)));
			slo_obj.put("slo_name", objectives.get(i));
			slo_obj.put("slo_definition", slo_definition.get(i));
			slo_obj.put("slo_unit", slo_unit.get(i));
			slo_obj.put("slo_value", slo_value.get(i));

			JSONArray metric = new JSONArray();
			JSONObject metric_obj = new JSONObject();
			JSONObject rate_obj = new JSONObject();
			JSONObject expression_obj = new JSONObject();
			JSONArray parameters = new JSONArray();
			JSONObject parameter_obj = new JSONObject();

			int old_metric_size = metric.size();
			int old_parameters_size = parameters.size();

			metric_obj.put("metric_id", "mtr" + (old_metric_size + (i + 1)));
			metric_obj.put("metric_definition", metric_list.get(i));
			metric.add(metric_obj);
			slo_obj.put("metric", (Object) metric);

			rate_obj.put("parameterWindow", rate_list.get(i));
			metric_obj.put("rate", (Object) rate_obj);

			expression_obj.put("expression_statement", expression_list.get(i));
			expression_obj.put("expression_language", "ISO80000");
			expression_obj.put("expression_unit", expression_unit_list.get(i));

			metric_obj.put("expression", (Object) expression_obj);

			parameter_obj.put("parameter_unit", parameter_unit.get(i));
			parameter_obj.put("parameter_definition", parameter_definition.get(i));
			parameter_obj.put("parameter_value", parameter_value.get(i));
			parameter_obj.put("parameter_name", parameter_name.get(i));
			parameter_obj.put("parameter_id", "prmtr" + (old_parameters_size + (i + 1)));
			parameters.add(parameter_obj);
			expression_obj.put("parameters", (Object) parameters);

			objective.add(slo_obj);

		}
		/** replace old template with the new one **/
		ns.replace(objectives, objective);
		sla_template.replace(ns, ns);
		root.replace(sla_template, sla_template);
		/** call Putsla method to push (PUT) new template to the 5GTANGO Catalogue **/
		String new_uuid = ms.PUTsla(jsonObject.toString(), sla_uuid);

		return new_uuid;

	}

}
