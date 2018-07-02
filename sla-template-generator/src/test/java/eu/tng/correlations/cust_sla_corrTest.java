package eu.tng.correlations;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONObject;
import org.junit.Test;

import eu.tng.rules.MonitoringRulesTest;

public class cust_sla_corrTest {

    @Test
    public void testGetSLAdetails() {
        JSONObject slo_details = new JSONObject();
        cust_sla_corrTest obj = new cust_sla_corrTest();
        JSONObject jsonObj = new JSONObject(obj.getFile("sla_template_example.json"));

        ArrayList<String> details = new ArrayList<String>();

        // get slad status
        String status = (String) jsonObj.get("status");
        details.add(status);
        JSONObject slad = (JSONObject) jsonObj.get("slad");
        // get slad name
        String name = (String) slad.get("name");
        details.add(name);

        boolean result = false;

        if (details.get(0) == "active" && details.get(1) == "Gold") {
            assertTrue(result);
        } else {
            assertFalse(result);
        }

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
