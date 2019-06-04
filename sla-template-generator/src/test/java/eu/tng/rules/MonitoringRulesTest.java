/*
 * 
 *  Copyright (c) 2015 SONATA-NFV, 2017 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 *  ALL RIGHTS RESERVED.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *              http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  Neither the name of the SONATA-NFV, 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 *  nor the names of its contributors may be used to endorse or promote
 *  products derived from this software without specific prior written
 *  permission.
 *  
 *  This work has been performed in the framework of the SONATA project,
 *  funded by the European Commission under Grant number 671517 through
 *  the Horizon 2020 and 5G-PPP programmes. The authors would like to
 *  acknowledge the contributions of their colleagues of the SONATA
 *  partner consortium (www.sonata-nfv.eu).
 *  
 *  This work has been performed in the framework of the 5GTANGO project,
 *  funded by the European Commission under Grant number 761493 through
 *  the Horizon 2020 and 5G-PPP programmes. The authors would like to
 *  acknowledge the contributions of their colleagues of the 5GTANGO
 *  partner consortium (www.5gtango.eu).
 * 
 *
 */

package eu.tng.rules;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Test;

public class MonitoringRulesTest {

    @Test
    public void testGetSloDetails() {

        JSONObject slo_details = new JSONObject();
        MonitoringRulesTest obj = new MonitoringRulesTest();
        JSONObject jsonObj = new JSONObject(obj.getFile("sla_template_example.json"));

        JSONObject slad = (JSONObject) jsonObj.get("slad");
        JSONObject sla_template = (JSONObject) slad.get("sla_template");
        JSONObject ns = (JSONObject) sla_template.get("service");
        JSONArray guaranteeTerms = (JSONArray) ns.get("guaranteeTerms");

        JSONArray slos = new JSONArray();
        for (int i = 0; i < guaranteeTerms.length(); i++) {
            JSONObject slo = new JSONObject();
            String name = (String) ((JSONObject) guaranteeTerms.get(i)).get("guarantee_name");
            JSONArray target_slo = (JSONArray)((JSONObject) guaranteeTerms.get(i)).get("target_slo");
            for (int j = 0; j < target_slo.length(); j++) {
            	String target_kpi = (String)((JSONObject) target_slo.get(j)).get("target_kpi");
            	String target_duration = (String)((JSONObject) target_slo.get(j)).get("target_duration");
            	String target_value = (String)((JSONObject) target_slo.get(j)).get("target_value");
    
                slo.put("target_kpi", target_kpi);
                slo.put("target_duration", target_duration);
                slo.put("target_value", target_value);
			}
            slos.put(slo);
        }

        slo_details.put("slos", slos);

        JSONArray slos_count = new JSONArray();
        slos_count = slo_details.getJSONArray("slos");
        assertTrue(slos_count.length() == 1);
    }

    private String getFile(String fileName) {

        StringBuilder result = new StringBuilder("");

        // Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();

    }

}
