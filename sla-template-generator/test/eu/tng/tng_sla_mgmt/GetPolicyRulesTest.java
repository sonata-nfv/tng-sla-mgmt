package eu.tng.tng_sla_mgmt;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

public class GetPolicyRulesTest {

	@Test
	public void testGetPolicyRules() {
		try {
			String url_string = "https://api.myjson.com/bins/virrd";
			URL url = new URL(url_string);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");

			assertTrue(conn.getResponseCode() == 200);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
