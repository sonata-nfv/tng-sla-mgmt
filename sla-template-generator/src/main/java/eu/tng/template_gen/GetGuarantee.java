package eu.tng.template_gen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetGuarantee {

	public ArrayList<JSONObject> getGuarantee(ArrayList<String> guarantees) {

		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		JSONObject guarantee = null;
		ArrayList<JSONObject> guaranteeArr = new ArrayList<JSONObject>();

		try {
			File testf = new File(this.getClass().getResource("/slos_list_release1.json").toURI());
			jsonObject = (JSONObject) parser.parse(new FileReader(testf));

			JSONObject guaranteeObject = (JSONObject) jsonObject;
			JSONArray guaranteeTerms = (JSONArray) guaranteeObject.get("guaranteeTerms");

			for (int j = 0; j < guarantees.size(); j++) {
				for (int i = 0; i < guaranteeTerms.size(); i++) {
					JSONObject guaranteeTermsObject = (JSONObject) guaranteeTerms.get(i);
					String guaranteeId = (String) guaranteeTermsObject.get("guaranteeID");

					if (guaranteeId.equals(guarantees.get(j))) {
						// System.out.println(guaranteeId);
						if (j == 0) {
							guarantee = (JSONObject) guaranteeTerms.get(i);
							guaranteeArr.add(guarantee);
						} else {
							guarantee = (JSONObject) guaranteeTerms.get(i);
							guaranteeArr.add(guarantee);
						}
					}

				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(guaranteeArr);

		return guaranteeArr;

	}

}
