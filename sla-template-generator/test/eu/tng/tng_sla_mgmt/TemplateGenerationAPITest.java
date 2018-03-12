package eu.tng.tng_sla_mgmt;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

public class TemplateGenerationAPITest {

	@Test
	public void testGetIt() {
		try {
			String nsId = "0a8b7a22-23a2-11e8-b467-0ed5f89f718b";
			String providerId = "0a8b7ef0-23a2-11e8-b467-0ed5f89f718b";
			String templateName = "This is a test template";
			String expireDate = "8, March 2022";

			// call CreateTemplate method
			CreateTemplate ct = new CreateTemplate();
			JSONObject sla_template_test = ct.createTemplate(nsId, providerId, templateName, expireDate);
		} catch (Exception e) {
			assertTrue(e.getMessage().contains("404"));
		}

	}

}
