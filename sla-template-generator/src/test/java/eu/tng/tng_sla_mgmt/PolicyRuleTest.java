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
package eu.tng.tng_sla_mgmt;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class PolicyRuleTest {

	@Test
	public void testGetName() {
		PolicyRule policy_rules = new PolicyRule();

		ArrayList<String> rules_names = new ArrayList<String>();
		rules_names.add("ApplyFlavour");
		rules_names.add("ProvideDedicatedBandwidth");

		policy_rules.setName(rules_names);
		assertTrue(policy_rules.getName().containsAll(rules_names));
	}

	@Test
	public void testSetName() {
		PolicyRule policy_rules = new PolicyRule();

		ArrayList<String> rules_names = new ArrayList<String>();
		rules_names.add("ApplyFlavour");
		rules_names.add("ProvideDedicatedBandwidth");

		policy_rules.setName(rules_names);
		assertTrue(policy_rules.getName().containsAll(rules_names));
	}

	@Test
	public void testGetField() {
		PolicyRule policy_rules = new PolicyRule();

		ArrayList<String> rules_Fields = new ArrayList<String>();
		rules_Fields.add("vnf1.CPULoad");
		rules_Fields.add("vnf2.RAM");

		policy_rules.setField(rules_Fields);
		assertTrue(policy_rules.getField().containsAll(rules_Fields));
	}

	@Test
	public void testSetField() {
		PolicyRule policy_rules = new PolicyRule();

		ArrayList<String> rules_Fields = new ArrayList<String>();
		rules_Fields.add("vnf1.CPULoad");
		rules_Fields.add("vnf2.RAM");

		policy_rules.setField(rules_Fields);
		assertTrue(policy_rules.getField().containsAll(rules_Fields));
	}

	@Test
	public void testGetOperator() {
		PolicyRule policy_rules = new PolicyRule();

		ArrayList<String> rules_Operators = new ArrayList<String>();
		rules_Operators.add("greater");
		rules_Operators.add("less");

		policy_rules.setOperator(rules_Operators);
		assertTrue(policy_rules.getOperator().containsAll(rules_Operators));
	}

	@Test
	public void testSetOperator() {
		PolicyRule policy_rules = new PolicyRule();

		ArrayList<String> rules_Operators = new ArrayList<String>();
		rules_Operators.add("greater");
		rules_Operators.add("less");

		policy_rules.setOperator(rules_Operators);
		assertTrue(policy_rules.getOperator().containsAll(rules_Operators));
	}

	@Test
	public void testGetType() {
		PolicyRule policy_rules = new PolicyRule();

		ArrayList<String> rules_type = new ArrayList<String>();
		rules_type.add("greater");
		rules_type.add("less");

		policy_rules.setType(rules_type);
		assertTrue(policy_rules.getType().containsAll(rules_type));
	}

	@Test
	public void testSetType() {
		PolicyRule policy_rules = new PolicyRule();

		ArrayList<String> rules_type = new ArrayList<String>();
		rules_type.add("greater");
		rules_type.add("less");

		policy_rules.setType(rules_type);

		assertTrue(policy_rules.getType().containsAll(rules_type));
	}

	@Test
	public void testGetValue() {
		PolicyRule policy_rules = new PolicyRule();

		ArrayList<String> rules_Value = new ArrayList<String>();
		rules_Value.add("9");
		rules_Value.add("70");

		policy_rules.setValue(rules_Value);
		assertTrue(policy_rules.getValue().containsAll(rules_Value));
	}

	@Test
	public void testSetValue() {
		PolicyRule policy_rules = new PolicyRule();

		ArrayList<String> rules_Value = new ArrayList<String>();
		rules_Value.add("9");
		rules_Value.add("70");

		policy_rules.setValue(rules_Value);
		assertTrue(policy_rules.getValue().containsAll(rules_Value));
	}

}
