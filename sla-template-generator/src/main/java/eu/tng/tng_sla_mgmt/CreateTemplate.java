package eu.tng.tng_sla_mgmt;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CreateTemplate {
	Nsd getNsd = new Nsd();
	PolicyRule getPolicyRule = new PolicyRule();

	@SuppressWarnings("unchecked")
	public JSONObject createTemplate(String nsId, String providerId, String templateName, String expireDate) {
		// System.out.println(nsId);
		// System.out.println(providerId);
		// System.out.println(templateName);
		// System.out.println(expireDate);

		// get network service descriptor for the given nsId
		GetNsd nsd = new GetNsd();
		nsd.getNSD(nsId);

		System.out.println("NS Name: " + getNsd.getName());
		System.out.println("NS Description: " + getNsd.getDescription());
		System.out.println("Monitoring Descriptions" + getNsd.GetMonDesc().toString());
		System.out.println("Monitoring Metrics" + getNsd.GetMonMetric().toString());
		System.out.println("Monitoring Units" + getNsd.GetMonUnit().toString());

		// get policy descriptor for the given nsId
		GetPolicyRules getPolicyRules = new GetPolicyRules();
		getPolicyRules.getPolicyRules();
		System.out.println("Policy Rule Name: " + getPolicyRule.getName().toString());
		System.out.println("Policy Rule Field: " + getPolicyRule.getField().toString());
		System.out.println("Policy Rule Operator: " + getPolicyRule.getOperator().toString());
		System.out.println("Policy Rule Type: " + getPolicyRule.getType().toString());
		System.out.println("Policy Rule Name: " + getPolicyRule.getValue().toString());

		/* generate the template */

		/* useful variables */

		// current date
		final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date offered_date = new Date();
		// System.out.println(sdf.format(offered_date));

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
		ns.put("ns_id", nsId);
		ns.put("ns_name", getNsd.getName());
		ns.put("description", getNsd.getDescription());
		sla_template.put("ns", ns);
		// objectives array
		JSONArray objectives = new JSONArray();
		objectives.add("msg 1");
		objectives.add("msg 2");
		ns.put("objectives", objectives);

		/*
		 * 
		 * try (FileWriter file = new FileWriter("c:\\test.json")) {
		 * 
		 * file.write(obj.toJSONString()); file.flush();
		 * 
		 * } catch (IOException e) { e.printStackTrace(); }
		 */
		// System.out.print(root);

		return root;

	}

}
