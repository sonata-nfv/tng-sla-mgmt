package eu.tng.rules;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Test;

public class MonitoringRulesTest {

    @Test
    public void testGetSloDetails() {

        JSONObject slo_details = new JSONObject();
        MonitoringRulesTest obj = new MonitoringRulesTest();
        JSONObject jsonObj = new JSONObject(obj.getFile("sla_template_example.json"));

        JSONObject slad = (JSONObject) jsonObj.get("slad");
        JSONObject sla_template = (JSONObject) slad.get("sla_template");
        JSONObject ns = (JSONObject) sla_template.get("ns");
        JSONArray guaranteeTerms = (JSONArray) ns.get("guaranteeTerms");

        JSONArray slos = new JSONArray();
        for (int i = 0; i < guaranteeTerms.length(); i++) {
            JSONObject slo = new JSONObject();
            String name = (String) ((JSONObject) guaranteeTerms.get(i)).get("name");
            JSONObject serviceLevelObjetive = (JSONObject) ((JSONObject) guaranteeTerms.get(i))
                    .get("serviceLevelObjetive");
            String duration = (String) serviceLevelObjetive.get("duration");
            String target_value = (String) serviceLevelObjetive.get("target_value");
            slo.put("name", name);
            slo.put("duration", duration);
            slo.put("target_value", target_value);

            slos.put(slo);

        }

        slo_details.put("slos", slos);

        JSONArray slos_count = new JSONArray();
        slos_count = slo_details.getJSONArray("slos");

        assertTrue(slos_count.length() == 2);
    }

    private String getFile(String fileName) {

        StringBuilder result = new StringBuilder("");

        // Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();

    }

}
