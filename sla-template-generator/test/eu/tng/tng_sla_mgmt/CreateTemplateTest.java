package eu.tng.tng_sla_mgmt;

import static org.junit.Assert.*;

import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

public class CreateTemplateTest {

	@Test
	public void testCreateTemplate() {
		
		Date offered_date = new Date();
		String nsId = "054cc864-238d-11e8-b467-0ed5f89f718b";
		String providerId = "238d";
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
		ns.put("ns_id", nsId);
		ns.put("ns_name", ns_name);
		ns.put("description", ns_description);
		sla_template.put("ns", ns);
		
		// objectives array
		JSONArray objectives = new JSONArray();
		objectives.add("msg 1");
		objectives.add("msg 2");
		ns.put("objectives", objectives);
		
		assertFalse(root.isEmpty());
	
	}

}
