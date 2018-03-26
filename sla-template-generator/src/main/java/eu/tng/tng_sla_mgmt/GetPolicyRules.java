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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetPolicyRules {

    public void getPolicyRules() {
        PolicyRule setPolicyRuleFields = new PolicyRule();
        ArrayList<String> name_list = new ArrayList<String>();
        ArrayList<String> field_list = new ArrayList<String>();
        ArrayList<String> operator_list = new ArrayList<String>();
        ArrayList<String> type_list = new ArrayList<String>();
        ArrayList<String> value_list = new ArrayList<String>();
        ArrayList<String> duration_list = new ArrayList<String>();

        try {
            // test url to call the policy descriptor - when ready call the Policy Manager
            // to access the policy descriptors
            URL url = new URL("https://api.myjson.com/bins/p92zz");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            if (conn.getResponseCode() != 200) {
                System.out.println("Failed : HTTP error code : PolicyD not FOUND");
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;

                    JSONArray policyRules = (JSONArray) jsonObject.get("policyRules");
                    for (int i = 0; i < policyRules.size(); i++) {
                        JSONObject policyRule = (JSONObject) policyRules.get(i);
                        // get policy rule name
                        if (policyRule.containsKey("actions")) {
                            name_list.add((String) policyRule.get("name"));
                            setPolicyRuleFields.setName(name_list);

                        }
                        if (policyRule.containsKey("duration")) {
                            JSONObject duration = (JSONObject) policyRule.get("duration");

                            String value = duration.get("value").toString();
                            String dur_unit = duration.get("duration_unit").toString();
                            duration_list.add(value + " " + dur_unit);
                            setPolicyRuleFields.setDuration(duration_list);

                        }

                        // get actual policy rules

                        JSONObject conditions = (JSONObject) policyRule.get("conditions");

                        JSONArray rules = (JSONArray) conditions.get("rules");
                        for (int k = 0; k < rules.size(); k++) {
                            JSONObject rule = (JSONObject) rules.get(k);
                            if (rule.containsKey("rules")) {
                                JSONArray rules2 = (JSONArray) rule.get("rules");
                                for (int l = 0; l < rules2.size(); l++) {
                                    JSONObject rule2 = (JSONObject) rules2.get(l);
                                    field_list.add((String) rule2.get("field") + "-obj-" + i);
                                    operator_list.add((String) rule2.get("operator"));
                                    type_list.add((String) rule2.get("type"));
                                    value_list.add((String) rule2.get("value"));
                                }
                            } else {
                                field_list.add((String) rule.get("field") + "-obj-" + i);
                                operator_list.add((String) rule.get("operator"));
                                type_list.add((String) rule.get("type"));
                                value_list.add((String) rule.get("value"));
                            }
                            setPolicyRuleFields.setField(field_list);
                            setPolicyRuleFields.setOperator(operator_list);
                            setPolicyRuleFields.setType(type_list);
                            setPolicyRuleFields.setValue(value_list);

                        }

                    } // end of for loop for policyRules array

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public void createExpression() {

        PolicyRule setPolicyRuleFields = new PolicyRule();
        ArrayList<String> expression_list = new ArrayList<String>();

        try {
            // test url to call the policy descriptor - when ready call the Policy Manager
            // to access the policy descriptors
            URL url = new URL("https://api.myjson.com/bins/virrd");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(output);
                    JSONObject jsonObject = (JSONObject) obj;

                    JSONArray policyRules = (JSONArray) jsonObject.get("policyRules");
                    for (int i = 0; i < policyRules.size(); i++) {
                        JSONObject policyRule = (JSONObject) policyRules.get(i);
                        String expression = "";
                        String operator = "";
                        String value = "";
                        String field = "";

                        JSONObject conditions_obj = (JSONObject) policyRule.get("conditions");
                        String condition = conditions_obj.get("condition").toString();

                        JSONArray rules = (JSONArray) conditions_obj.get("rules");
                        for (int k = 0; k < rules.size(); k++) {
                            JSONObject rule = (JSONObject) rules.get(k);
                            if (rule.containsKey("rules")) {
                                String condition2 = rule.get("condition").toString();

                                JSONArray rules2 = (JSONArray) rule.get("rules");
                                for (int l = 0; l < rules2.size(); l++) {
                                    JSONObject rule2 = (JSONObject) rules2.get(l);

                                    field = rule2.get("field").toString();
                                    value = rule2.get("value").toString();
                                    if (rule2.get("operator").toString() == "greater") {
                                        operator = ">";
                                    } else {
                                        operator = "<";
                                    }
                                    expression = expression.concat(field + operator + value);
                                    expression = expression.concat(condition2);

                                }
                            }

                            else {
                                field = rule.get("field").toString();
                                value = rule.get("value").toString();
                                if (rule.get("operator").toString() == "greater") {
                                    operator = ">";
                                } else {
                                    operator = "<";
                                }
                                expression = expression.concat(field + operator + value);
                                expression = expression.concat(condition);

                            }
                        }

                        if (expression.substring(expression.length() - 1, expression.length()).equals("D")) {
                            expression = expression.substring(0, expression.length() - 3);
                        } else {
                            expression = expression.substring(0, expression.length() - 2);
                        }

                        expression_list.add(expression);
                        setPolicyRuleFields.setExpression(expression_list);

                    } // end of for loop for policyRules array

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}
