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
