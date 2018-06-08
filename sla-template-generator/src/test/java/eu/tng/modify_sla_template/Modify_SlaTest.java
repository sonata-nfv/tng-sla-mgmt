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

import static org.junit.Assert.*;
import java.io.FileReader;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

public class Modify_SlaTest {

	@Test
	public void testSwitchState() {
		String state = "unpublished";

		JSONObject slaD = null;
		Configuration conf = Configuration.defaultConfiguration();

		JSONParser parser = new JSONParser();
		try {
			slaD = (JSONObject) parser.parse(new FileReader("src/main/resources/sla_template_example.json"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String sla_state = JsonPath.using(conf).parse(slaD).read("state");

		assertTrue(state.equals(sla_state));
	}

	@Test
	public void testSwitchStatus() {
		String status = "active";

		JSONObject slaD = null;
		Configuration conf = Configuration.defaultConfiguration();

		JSONParser parser = new JSONParser();
		try {
			slaD = (JSONObject) parser.parse(new FileReader("src/main/resources/sla_template_example.json"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String sla_status = JsonPath.using(conf).parse(slaD).read("status");

		assertTrue(status.equals(sla_status));
	}

	@Test
	public void testEditField() {

		JSONObject slaD = null;
		Configuration conf = Configuration.defaultConfiguration();

		JSONParser parser = new JSONParser();
		try {
			slaD = (JSONObject) parser.parse(new FileReader("src/main/resources/sla_template_example.json"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String Field1 = JsonPath.using(conf).parse(slaD).read("slad.name");
		String Field2 = JsonPath.using(conf).parse(slaD).read("slad.description");
		String Field3 = JsonPath.using(conf).parse(slaD).read("slad.sla_template.valid_until");
		List<String> Field4 = JsonPath.using(conf).parse(slaD)
				.read("slad.sla_template.ns.objectives[*].[?(@.slo_name == 'Bandwidth')].slo_name");
		List<String> Field5 = JsonPath.using(conf).parse(slaD)
				.read("slad.sla_template.ns.objectives[*].[?(@.slo_definition == 'Bandwidth Testing')].slo_definition");
		List<String> Field6 = JsonPath.using(conf).parse(slaD)
				.read("slad.sla_template.ns.objectives[*].[?(@.slo_value == 'Slo value testing')].slo_value");
		List<String> Field7 = JsonPath.using(conf).parse(slaD).read(
				"slad.sla_template.ns.objectives[*].metric[*][?(@.metric_definition == 'lowTranscodingRateRule')].metric_definition");
		List<String> Field8 = JsonPath.using(conf).parse(slaD).read(
				"slad.sla_template.ns.objectives[*].metric[*].expression.parameters[*].[?(@.parameter_name == 'price')].parameter_name");
		List<String> Field9 = JsonPath.using(conf).parse(slaD).read(
				"slad.sla_template.ns.objectives[*].metric[*].expression.parameters[*].[?(@.parameter_unit == 'Euro')].parameter_unit");
		List<String> Field10 = JsonPath.using(conf).parse(slaD).read(
				"slad.sla_template.ns.objectives[*].metric[*].expression.parameters[*].[?(@.parameter_definition == 'Test Price')].parameter_definition");
		List<String> Field11 = JsonPath.using(conf).parse(slaD).read(
				"slad.sla_template.ns.objectives[*].metric[*].expression.parameters[*].[?(@.parameter_value == 3000)].parameter_value");

		assertTrue(Field1.toString().equals("Test2") && Field2.toString().equals("Testing Testing Testing")
				&& Field3.toString().equals("20/02/2020") && Field4.toString().equals("[\"Bandwidth\"]")
				&& Field5.toString().equals("[\"Bandwidth Testing\"]")
				&& Field6.toString().equals("[\"Slo value testing\"]")
				&& Field7.toString().equals("[\"lowTranscodingRateRule\"]") && Field8.toString().equals("[\"price\"]")
				&& Field9.toString().equals("[\"Euro\"]") && Field10.toString().equals("[\"Test Price\"]")
				&& Field11.toString().equals("[\"3000\"]"));
	}

	@Test
	public void testPUTsla() {

		String uuid = "a03e8ca7-ebe6-4cb4-ba78-cf9f36656faf";
		Configuration conf = Configuration.defaultConfiguration();
		JSONObject slaD = null;
		JSONParser parser = new JSONParser();

		try {
			slaD = (JSONObject) parser.parse(new FileReader("src/main/resources/sla_template_example.json"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String sla_uuid = JsonPath.using(conf).parse(slaD).read("uuid");

		assertTrue(uuid.equals(sla_uuid));

	}

}
