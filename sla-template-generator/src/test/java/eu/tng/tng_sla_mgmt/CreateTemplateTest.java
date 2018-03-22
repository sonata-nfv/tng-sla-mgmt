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

import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

public class CreateTemplateTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateTemplate() {

        Date offered_date = new Date();
        String nsd_uuid = "054cc864-238d-11e8-b467-0ed5f89f718b";
        String templateName = "test_template_1";
        String expireDate = "Mar 8, 2018";
        String ns_name = "test_name";
        String ns_description = "test Description";

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
        sla_template.put("valid_until", expireDate);
        sla_template.put("sla_template_version", "0.1");
        sla_template.put("service_provider_id", "sp001");
        root.put("sla_template", sla_template);

        // ns object
        JSONObject ns = new JSONObject();
        ns.put("nsd_uuid", nsd_uuid);
        ns.put("ns_name", ns_name);
        ns.put("description", ns_description);
        sla_template.put("ns", ns);

        // objectives array
        JSONArray objectives = new JSONArray();
        objectives.add("msg 1");
        objectives.add("msg 2");
        ns.put("objectives", objectives);

        assertTrue(root.isEmpty() == false);

    }

}
