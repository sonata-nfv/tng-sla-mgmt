package eu.tng.tng_sla_mgmt;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CreateTemplate {
	Nsd getNsd = new Nsd();
	PolicyRule getPolicyRule = new PolicyRule();

	@SuppressWarnings("unchecked")
	public JSONObject createTemplate(String nsId, String providerId, String templateName, String expireDate) {
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
		System.out.println("Policy Rule Value: " + getPolicyRule.getValue().toString());
		System.out.println("Policy Rule Duration: " + getPolicyRule.getDuration().toString());

		/* generate the template */

		/* useful variables */

		// current date
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
			rate.put("parameterWindow", getPolicyRule.getDuration().get(i));
			rate.put("sampleInterval", "");

			JSONObject expression = new JSONObject();
			expression.put("expression_statement", "");
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
		
		/*
		ObjectMapper mapper = new ObjectMapper();

	    try {  
	        // Writing to a file   
	        mapper.writeValue(new File("test.txt"), root );
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	    */
		return root;

	}

}
