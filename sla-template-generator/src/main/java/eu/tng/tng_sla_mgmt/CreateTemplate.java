package eu.tng.tng_sla_mgmt;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

public class CreateTemplate {
	
	public void createTemplate(String nsId, String providerId, String templateName, String expireDate) {
	    System.out.println(nsId);
	    System.out.println(providerId);
	    System.out.println(templateName);
	    System.out.println(expireDate);
	    
	    // get network service descriptor for the given nsId
	    GetNsd nsd = new GetNsd();
	    nsd.getNSD(nsId);
	    
	    // get policy descriptor for the given nsId
	    GetPolicyRules getPolicyRules = new GetPolicyRules();
	    getPolicyRules.getPolicyRules();
	    
	    /*
	    JSONObject obj = new JSONObject();
        obj.put("name", "mkyong.com");
        obj.put("age", new Integer(100));

        JSONArray list = new JSONArray();
        list.add("msg 1");
        list.add("msg 2");
        list.add("msg 3");

        obj.put("messages", list);

        try (FileWriter file = new FileWriter("c:\\test.json")) {

            file.write(obj.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(obj);
        */
	    
	    
	}

}
