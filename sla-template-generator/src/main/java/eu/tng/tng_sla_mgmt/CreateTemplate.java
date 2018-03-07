package eu.tng.tng_sla_mgmt;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

public class CreateTemplate {
	Nsd getNsd = new Nsd();
	PolicyRule getPolicyRule = new PolicyRule();

	
	public void createTemplate(String nsId, String providerId, String templateName, String expireDate) {
		System.out.println(nsId);
		System.out.println(providerId);
		System.out.println(templateName);
		System.out.println(expireDate);

		// get network service descriptor for the given nsId
		GetNsd nsd = new GetNsd();
		nsd.getNSD(nsId);

		System.out.println("NS Name: " + getNsd.getName());
		System.out.println("NS Description: " + getNsd.getDescription());
		System.out.println("Monitoring Descriptions" + getNsd.GetMonDesc().toString());
		System.out.println("Monitoring Metrics" + getNsd.GetMonMetric().toString());
		System.out.println("Monitoring Units" + getNsd.GetMonUnit().toString());
		System.out.println("Soft Constraints" + getNsd.GetSoftCon().toString());

		// get policy descriptor for the given nsId
		GetPolicyRules getPolicyRules = new GetPolicyRules();
		getPolicyRules.getPolicyRules();
		System.out.println("Policy Rule Name: " + getPolicyRule.getName().toString() );
		System.out.println("Policy Rule Field: " + getPolicyRule.getField().toString() );
		System.out.println("Policy Rule Operator: " + getPolicyRule.getOperator().toString());
		System.out.println("Policy Rule Type: " + getPolicyRule.getType().toString());
		System.out.println("Policy Rule Name: " + getPolicyRule.getValue().toString() );

		/*
		 * JSONObject obj = new JSONObject(); obj.put("name", "mkyong.com");
		 * obj.put("age", new Integer(100));
		 * 
		 * JSONArray list = new JSONArray(); list.add("msg 1"); list.add("msg 2");
		 * list.add("msg 3");
		 * 
		 * obj.put("messages", list);
		 * 
		 * try (FileWriter file = new FileWriter("c:\\test.json")) {
		 * 
		 * file.write(obj.toJSONString()); file.flush();
		 * 
		 * } catch (IOException e) { e.printStackTrace(); }
		 * 
		 * System.out.print(obj);
		 */

	}

}
