package eu.tng.tng_sla_mgmt;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetNsd {
	public static void main(String[] args) {

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("src/main/resources/recursive-example-2.json"));

            JSONObject jsonObject = (JSONObject) obj;
            //System.out.println(jsonObject);
            
            //get nsd name
            String name = (String) jsonObject.get("name");
            System.out.println(name);
            //get nsd description
            String description = (String) jsonObject.get("description");
            System.out.println(description);
            //get monitoring_parameters-desc,metric,unit
            if(jsonObject.containsKey("monitoring_parameters")){
            	JSONArray monitoring_parameters = (JSONArray) jsonObject.get("monitoring_parameters");
                for (int i = 0; i < monitoring_parameters.size(); i++) {
                  JSONObject monitoring_parameter = (JSONObject) monitoring_parameters.get(i); 
                  String mon_desc = (String) monitoring_parameter.get("desc");
                  String mon_metric = (String) monitoring_parameter.get("metric");
                  String mon_unit = (String) monitoring_parameter.get("unit");
                  System.out.println("Monitoring Parameter:" +mon_desc+" Metric:"+mon_metric+" Unit:"+mon_unit);
                }  
            }    
            //get soft_constrains
            if(jsonObject.containsKey("soft_constraints")){
            	JSONArray soft_constraints = (JSONArray) jsonObject.get("soft_constraints");
                for (int i = 0; i < soft_constraints.size(); i++) {
                  String soft_constraint = (String) soft_constraints.get(i); 
                  
                  System.out.println(soft_constraint);
                }  
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

	}

}
