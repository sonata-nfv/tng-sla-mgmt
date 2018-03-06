package eu.tng.tng_sla_mgmt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetNsd {
	public void getNSD(String nsId) {

		try {
			//api call to catalogue in order to get the nsd with specific id = c490d183-0abc-4927-bf40-072233e12497
			String url_string = "http://pre-int-sp-ath.5gtango.eu:4002/catalogues/api/v2/network-services/"+nsId ; 
			URL url = new URL(url_string);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br.readLine()) != null) {
				JSONParser parser = new JSONParser();
				try {
					Object obj = parser.parse(output);
					JSONObject jsonObject = (JSONObject) obj;
					if (jsonObject.containsKey("nsd")) {
						JSONObject nsd = (JSONObject) jsonObject.get("nsd");
						// get nsd name
						String name = (String) nsd.get("name");
						System.out.println(name);
						// get nsd description
						String description = (String) nsd.get("description");
						System.out.println(description);
						// get monitoring_parameters-desc,metric,unit
						if (nsd.containsKey("monitoring_parameters")) {
							JSONArray monitoring_parameters = (JSONArray) nsd.get("monitoring_parameters");
							for (int i = 0; i < monitoring_parameters.size(); i++) {
								JSONObject monitoring_parameter = (JSONObject) monitoring_parameters.get(i);
								String mon_desc = (String) monitoring_parameter.get("desc");
								String mon_metric = (String) monitoring_parameter.get("metric");
								String mon_unit = (String) monitoring_parameter.get("unit");
								System.out.println("Monitoring Parameter:" + mon_desc + " Metric:" + mon_metric
										+ " Unit:" + mon_unit);
							}
						}
						// get soft_constrains
						if (nsd.containsKey("soft_constraints")) {
							JSONArray soft_constraints = (JSONArray) nsd.get("soft_constraints");
							for (int i = 0; i < soft_constraints.size(); i++) {
								String soft_constraint = (String) soft_constraints.get(i);
								System.out.println(soft_constraint);
							}
						}
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
			conn.disconnect();
		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}
