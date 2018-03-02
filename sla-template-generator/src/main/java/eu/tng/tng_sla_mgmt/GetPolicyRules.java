package eu.tng.tng_sla_mgmt;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetPolicyRules {
	public static void main(String[] args) {

		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader("src/main/resources/policy-example.json"));

			JSONObject jsonObject = (JSONObject) obj;
			// System.out.println(jsonObject);

			if (jsonObject.containsKey("policyRules")) {
				JSONArray policyRules = (JSONArray) jsonObject.get("policyRules");
				for (int i = 0; i < policyRules.size(); i++) {
					System.out.println("Rule " + i);

					JSONObject policyRule = (JSONObject) policyRules.get(i);
					// GET POLICY RULE NAME
					if (policyRule.containsKey("actions")) {
						JSONArray actions = (JSONArray) policyRule.get("actions");
						for (int j = 0; j < actions.size(); j++) {
							JSONObject action = (JSONObject) actions.get(j);
							String name = (String) action.get("name");
							System.out.println("Name:" + name);
						}
					}
					// GET POLICY RULES
					if (policyRule.containsKey("conditions")) {
						JSONObject conditions = (JSONObject) policyRule.get("conditions");
						if (conditions.containsKey("rules")) {
							JSONArray rules = (JSONArray) conditions.get("rules");
							for (int k = 0; k < rules.size(); k++) {
								JSONObject rule = (JSONObject) rules.get(k);
								if (rule.containsKey("rules")) {
									JSONArray rules2 = (JSONArray) rule.get("rules");
									for (int l = 0; l < rules2.size(); l++) {
										JSONObject rule2 = (JSONObject) rules2.get(l);
										System.out.println("lala");
										String field = (String) rule2.get("field");
										String operator = (String) rule2.get("operator");
										String type = (String) rule2.get("type");
										String value = (String) rule2.get("value");
										System.out.println("Field:" + field + " Operator:" + operator + "Type:" + type
												+ " Value:" + value);
									}
								} else {
									String field = (String) rule.get("field");
									String operator = (String) rule.get("operator");
									String type = (String) rule.get("type");
									String value = (String) rule.get("value");
									System.out.println("Field:" + field + " Operator:" + operator + "Type:" + type
											+ " Value:" + value);
								}
							}
						}
					}

				} // end of for loop for policyRules array
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

}
