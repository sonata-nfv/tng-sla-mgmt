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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CreateTemplate {
    Nsd getNsd = new Nsd();
    PolicyRule getPolicyRule = new PolicyRule();

    @SuppressWarnings("unchecked")
    public JSONObject createTemplate(String nsd_uuid, String templateName, String expireDate) {

        // get network service descriptor for the given nsId
        GetNsd nsd = new GetNsd();
        nsd.getNSD(nsd_uuid);
        // get policy descriptor for the given uuid
        GetPolicyRules getPolicyRules = new GetPolicyRules();
        getPolicyRules.getPolicyRules();
        // get the expression that need to be expressed into the template
        getPolicyRules.createExpression();

        /* generate the template */

        /* useful variables */
        // current date
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");
        Date date = new Date();
        String offered_date = dateFormat.format(date); // 2016/11/16 12:08:43
        // valid until date
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateInString = expireDate;
        Date date2 = null;
        try {
            date2 = formatter.parse(dateInString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String validUntil = formatter.format(date2);

        /* generate the template */
        // root element
        JSONObject root = new JSONObject();
        root.put("descriptor_schema",
                "https://raw.githubusercontent.com/sonata-nfv/tng-schema/master/service-descriptor/nsd-schema.yml");
        root.put("vendor", "tango-sla-template");
        root.put("name", templateName);
        root.put("version", "0.1");
        root.put("author", "Evgenia Kapassa, Marios Touloupou");
        root.put("description", "");
        // sla_template object
        JSONObject sla_template = new JSONObject();
        sla_template.put("offered_date", offered_date);
        sla_template.put("valid_until", validUntil);
        sla_template.put("service_provider_id", "sp001");
        root.put("sla_template", sla_template);
        // ns object
        JSONObject ns = new JSONObject();
        ns.put("nsd_uuid", nsd_uuid);
        ns.put("ns_name", getNsd.getName());
        ns.put("description", getNsd.getDescription());
        sla_template.put("ns", ns);
        // objectives array
        JSONArray objectives = new JSONArray();
        // for each monitoring_parameter create a slo_obj
        for (int i = 0; i < getNsd.GetMonMetric().size(); i++) {
            JSONObject slo_obj = new JSONObject();
            slo_obj.put("slo_id", "slo" + (i + 1));
            slo_obj.put("slo_name", getNsd.GetMonMetric().get(i));
            slo_obj.put("slo_definition", getNsd.GetMonDesc().get(i));
            slo_obj.put("slo_unit", getNsd.GetMonUnit().get(i));
            slo_obj.put("slo_value", "");

            JSONArray metric = new JSONArray();
            JSONObject metric_obj = new JSONObject();
            metric_obj.put("metric_id", "mtr" + (i + 1));
            metric_obj.put("metric_definition", getPolicyRule.getName().get(i));

            JSONObject rate = new JSONObject();

            try {
                getPolicyRule.getDuration().get(i);
                rate.put("parameterWindow", getPolicyRule.getDuration().get(i));
            } catch (IndexOutOfBoundsException e) {
                rate.put("parameterWindow", "");
            }

            JSONObject expression = new JSONObject();
            expression.put("expression_statement", getPolicyRule.getExpression().get(i));
            expression.put("expression_language", "ISO80000");
            expression.put("expression_unit", "");

            JSONArray parameters = new JSONArray();
            for (int k = 0; k < getPolicyRule.getField().size(); k++) {
                if (getPolicyRule.getField().get(k).contains("-obj-" + i)) {
                    JSONObject parameters_obj = new JSONObject();
                    parameters_obj.put("parameter_id", "prmtr" + (k + 1));

                    StringBuilder sb = new StringBuilder(getPolicyRule.getField().get(k));

                    sb.delete(sb.length() - 6, sb.length());
                    String result = sb.toString();

                    parameters_obj.put("parameter_name", result);
                    parameters_obj.put("parameter_definition", "");
                    parameters_obj.put("parameter_unit", "");
                    parameters_obj.put("parameter_value", getPolicyRule.getValue().get(k));

                    parameters.add(parameters_obj);
                }
            }

            expression.put("parameters", (Object) parameters);
            metric_obj.put("rate", (Object) rate);
            metric_obj.put("expression", (Object) expression);
            metric.add(metric_obj);

            slo_obj.put("metric", (Object) metric);
            objectives.add(slo_obj);
        }

        ns.put("objectives", objectives);

        return root;

    }

}
