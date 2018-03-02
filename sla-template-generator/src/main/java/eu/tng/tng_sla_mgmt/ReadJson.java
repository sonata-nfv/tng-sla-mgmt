package eu.tng.tng_sla_mgmt;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadJson {

    public static void main(String[] args) {

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("src/main/resources/recursive-example.json"));

            JSONObject jsonObject = (JSONObject) obj;
            //System.out.println(jsonObject);

            String descriptor_schema = (String) jsonObject.get("descriptor_schema");
            System.out.println(descriptor_schema);

            String name = (String) jsonObject.get("name");
            System.out.println(name);
            

            JSONArray network_functions = (JSONArray) jsonObject.get("network_functions");
            for (int i = 0; i < network_functions.size(); i++) {
              JSONObject network_function = (JSONObject) network_functions.get(i); 
              String vnf_id = (String) network_function.get("vnf_id");

              System.out.println(vnf_id);
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