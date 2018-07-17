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

package eu.tng.messaging;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ViolationsProducerTest {

    @Test
    public void testCreateViolationMessage() {
        
        
        JSONObject payload = new JSONObject();
        try {
            payload.put("ns_uuid", "aaa-bbb-ccc-ddd");
            payload.put("violated_sla", "eee-fff-ggg-hhh-jjj");
            payload.put("cust_uuid", "kkk-lll-ooo-iii");
            payload.put("violation_time", "19:34");
            payload.put("alert_state", "firing");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        boolean result = false;
        
        try {
            String ns_uuid = payload.getString("ns_uuid");
            String violated_sla = payload.getString("violated_sla");
            String cust_uuid = payload.getString("cust_uuid");
            String violation_time = payload.getString("violation_time");
            String alert_state =payload.getString("alert_state");
            result = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        
        assertTrue(result);
    }

}
