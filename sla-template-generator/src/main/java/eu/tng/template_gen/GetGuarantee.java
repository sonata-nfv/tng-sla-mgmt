package eu.tng.template_gen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetGuarantee {
	
	public JSONObject getGuarantee(String guaranteeID) {
		
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		JSONObject guarantee = null;
        try {
        	File testf = new File( this.getClass().getResource( "/slos_list_release1.json" ).toURI() );
        	jsonObject = (JSONObject) parser.parse(new FileReader(testf));
        	
        	JSONObject guaranteeObject = (JSONObject) jsonObject;
            JSONArray guaranteeTerms = (JSONArray) guaranteeObject.get("guaranteeTerms");
            
            for (int i = 0; i < guaranteeTerms.size(); i++) {
                JSONObject guaranteeTermsObject = (JSONObject) guaranteeTerms.get(i);
                String guaranteeId = (String) guaranteeTermsObject.get("guaranteeID");
                              
                if (guaranteeId.equals("g1")) {
                    //System.out.println(guaranteeId);
                    guarantee = (JSONObject) guaranteeTerms.get(i);
                   // System.out.println(guarantee);
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
        
		return guarantee;
		
	}
	

}
