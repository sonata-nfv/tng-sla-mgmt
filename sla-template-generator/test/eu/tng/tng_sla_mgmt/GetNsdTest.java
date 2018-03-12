package eu.tng.tng_sla_mgmt;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

public class GetNsdTest {

	@Test
	public void testGetNSD() {
		try {
			String url_string = "http://pre-int-sp-ath.5gtango.eu:4002/catalogues/api/v2/network-services/";
			URL url = new URL(url_string);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");

			assertTrue(conn.getResponseCode() == 200);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
