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

public class GetNsdTest {

    @Test
    public void testGetNSD() {
        String nsfield1 = "eu.tango-nfv.service-descriptor";
        String nsfield2 = "Stefan Schneider, Paderborn University";
        String nsfield3 = "recursive-sonata";

        JSONObject nsD = null;
        Configuration conf = Configuration.defaultConfiguration();

        JSONParser parser = new JSONParser();
        try {
            nsD = (JSONObject) parser.parse(new FileReader("src/main/resources/ns_descriptor.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String nsvendor = JsonPath.using(conf).parse(nsD).read("vendor");
        String nsauthor = JsonPath.using(conf).parse(nsD).read("author");
        String nsname = JsonPath.using(conf).parse(nsD).read("name");

        assertTrue(nsfield1.equals(nsvendor) && nsfield2.equals(nsauthor) && nsfield3.equals(nsname));

    }

}
