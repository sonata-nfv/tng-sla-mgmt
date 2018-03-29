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
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import com.google.gson.Gson;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class Sla_EditorTest {

    @Test
    public void testEdit_value() {

        String new_sla_name = "Test3";

        JSONObject slaD = null;
        Configuration conf = Configuration.defaultConfiguration();

        JSONParser parser = new JSONParser();
        try {
            slaD = (JSONObject) parser.parse(new FileReader("src/main/resources/sla_template_example.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        DocumentContext doc = JsonPath.parse(slaD).set(".name", new_sla_name);
        String newJson = new Gson().toJson(doc.read("$"));

        String new_test_sla_name = JsonPath.using(conf).parse(newJson).read("slad.name");

        assertTrue(new_test_sla_name.equals(new_sla_name));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAdd_Fields() {
        
        List<String> objectives = Arrays.asList("objectiveName1", "objectiveName2");
        List<String> slo_value = Arrays.asList("10", "15");
        List<String> slo_definition = Arrays.asList("objectiveDefinition1", "objectiveDefinition2");
        List<String> slo_unit = Arrays.asList("%", "sec");
        List<String> metric_list = Arrays.asList("metricDefinition1", "metricDefinition2");
        List<String> expression_list = Arrays.asList("expression1", "expression2");
        List<String> expression_unit_list = Arrays.asList("%", "sec");
        List<String> rate_list = Arrays.asList("10m", "20m");
        List<String> parameter_unit = Arrays.asList("%","%");
        List<String> parameter_definition = Arrays.asList("parameterDefinition1", "parameterDefinition2");
        List<String> parameter_value = Arrays.asList("parameterValue1", "parameterValue2");
        List<String> parameter_name = Arrays.asList("parameterName1","parameterName2");
        
        JSONObject root = null;
        Configuration conf = Configuration.defaultConfiguration();

        JSONParser parser = new JSONParser();
        try {
            root = (JSONObject) parser.parse(new FileReader("src/main/resources/sla_template_example.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Remove root element inserted by the Catalogue
        Object slad = root.get("slad");
        JSONObject jsonObject = (JSONObject) slad;
        
        //Increase Verions
        double version = Double.parseDouble((String) jsonObject.get("version"));
        version += 0.1;

        String new_version = String.valueOf(version);
        jsonObject.put("version", String.valueOf(new_version));
                

        JSONObject sla_template = (JSONObject) jsonObject.get("sla_template");
        JSONObject ns = (JSONObject) sla_template.get("ns");
        JSONArray objective = (JSONArray) ns.get("objectives");

        for (int i = 0; i < objectives.size(); i++) {
            JSONObject slo_obj = new JSONObject();
            slo_obj.put("slo_id", "sloNEW" + (i + 1));
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

            metric_obj.put("metric_id", "mtrNEW" + (i + 1));
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
            parameter_obj.put("parameter_id", "prmtrNEW" + (i + 1));
            parameters.add(parameter_obj);
            expression_obj.put("parameters", (Object) parameters);

            objective.add(slo_obj);

        }

        ns.replace(objectives, objective);
        sla_template.replace(ns, ns);
        root.replace(sla_template, sla_template);

        List <String> objective1 = JsonPath.using(conf).parse(root).read("slad.sla_template.ns.objectives[*].[?(@.slo_name == 'objectiveName1')].slo_name");
        List <String> objective2 = JsonPath.using(conf).parse(root).read("slad.sla_template.ns.objectives[*].[?(@.slo_name == 'objectiveName2')].slo_name");
        
        assertTrue(objective1.toString().equals("[\"objectiveName1\"]") && objective2.toString().equals("[\"objectiveName2\"]"));
        
    }

}
