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
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

public class GetPolicyRulesTest {

    @Test
    public void testGetPolicyRules() {
        String rule1field1 = "vnf1.LogMetric";
        String rule1id1 = "vnf1.LogMetric";
        String rule1input1 = "text";
        String rule1operator1 = "equal";
        String rule1type1 = "string";
        JSONObject policyD = null;
        Configuration conf = Configuration.defaultConfiguration();

        JSONParser parser = new JSONParser();
        try {
            policyD = (JSONObject) parser.parse(new FileReader("src/main/resources/policy_descriptor.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String policyfield1 = JsonPath.using(conf).parse(policyD).read("policyRules[0].conditions.rules[0].field");
        String policyid1 = JsonPath.using(conf).parse(policyD).read("policyRules[0].conditions.rules[0].id");
        String policyinput1 = JsonPath.using(conf).parse(policyD).read("policyRules[0].conditions.rules[0].input");
        String policyperator1 = JsonPath.using(conf).parse(policyD).read("policyRules[0].conditions.rules[0].operator");
        String policytype1 = JsonPath.using(conf).parse(policyD).read("policyRules[0].conditions.rules[0].type");

       
        assertTrue(rule1field1.equals(policyfield1) && rule1id1.equals(policyid1) && rule1input1.equals(policyinput1)
                && rule1operator1.equals(policyperator1) && rule1type1.equals(policytype1));
                

    }
}
