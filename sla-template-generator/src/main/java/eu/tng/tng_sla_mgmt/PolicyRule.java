package eu.tng.tng_sla_mgmt;

import java.util.ArrayList;

public class PolicyRule {
	private static ArrayList<String> name = new ArrayList();
	private static ArrayList<String> field = new ArrayList();
	private static ArrayList<String> operator = new ArrayList();
	private static ArrayList<String> type = new ArrayList();
	private static ArrayList<String> value = new ArrayList();

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

}
