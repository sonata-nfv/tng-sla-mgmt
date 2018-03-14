
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

import java.util.ArrayList;

public class PolicyRule {
	private static ArrayList<String> name = new ArrayList<String>();
	private static ArrayList<String> field = new ArrayList<String>();
	private static ArrayList<String> operator = new ArrayList<String>();
	private static ArrayList<String> type = new ArrayList<String>();
	private static ArrayList<String> value = new ArrayList<String>();
	private static ArrayList<String> duration = new ArrayList<String>();

	private static ArrayList<String> expression = new ArrayList<String>();

	// public method to get the ns mon_desc
	public ArrayList<String> getName() {
		return name;
	}

	// public method to set the ns mon_desc
	public void setName(ArrayList<String> mon_desc) {
		this.name = mon_desc;
	}

	// public method to get the policy rule field
	public ArrayList<String> getField() {
		return field;
	}

	// public method to set the ns field
	public void setField(ArrayList<String> field) {
		this.field = field;
	}

	// public method to get the policy rule operator
	public ArrayList<String> getOperator() {
		return operator;
	}

	// public method to set the ns operator
	public void setOperator(ArrayList<String> operator) {
		this.operator = operator;
	}

	// public method to get the policy rule type
	public ArrayList<String> getType() {
		return type;
	}

	// public method to set the ns type
	public void setType(ArrayList<String> type) {
		this.type = type;
	}

	// public method to get the policy rule value
	public ArrayList<String> getValue() {
		return value;
	}

	// public method to set the ns value
	public void setValue(ArrayList<String> value) {
		this.value = value;
	}

	// public method to get the policy rule value
	public ArrayList<String> getDuration() {
		return duration;
	}

	// public method to set the ns value
	public void setDuration(ArrayList<String> duration) {
		this.duration = duration;
	}

	// public method to get the expression
	public ArrayList<String> getExpression() {
		return expression;
	}

	// public method to set the expression
	public void setExpression(ArrayList<String> expression) {
		this.expression = expression;
	}

}
