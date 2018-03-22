/*
 * Copyright (c) 2017 5GTANGO, UPRC ALL RIGHTS RESERVED.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Neither the name of the 5GTANGO, UPRC nor the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * This work has been performed in the framework of the 5GTANGO project, funded by the European
 * Commission under Grant number 761493 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the 5GTANGO partner consortium
 * (www.5gtango.eu).
 *
 * @author Evgenia Kapassa (MSc), UPRC
 * 
 * @author Marios Touloupou (MSc), UPRC
 * 
 */

package eu.tng.tng_sla_mgmt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONObject;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

/**
 *
 * @author Evgenia Kapassa <ekapassa@unipi.gr>
 * @author Marios Touloupou <mtouloup@unipi.gr>
 */

@Path("/templategeneration")
public class TemplateGenerationAPI {

    /**
     * api call in order to generate a sla template mendatory input parameters from
     * the user: nsId, providerId, templateName, expireDate e.g.
     * http://localhost:8080/tng-sla-mgmt/slas/templategeneration?uuid=8effe1db-edd3-404f-8a68-92e2ebb2b176&providerId=20&templateName=lala&expireDate=20/02/2018
     * 
     */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIt(@Context UriInfo info) {

        String nsd_uuid = info.getQueryParameters().getFirst("nsd_uuid");
        String templateName = info.getQueryParameters().getFirst("templateName");
        String expireDate = info.getQueryParameters().getFirst("expireDate");

        Configuration conf = Configuration.defaultConfiguration();

        // call CreateTemplate method
        CreateTemplate ct = new CreateTemplate();
        JSONObject template = ct.createTemplate(nsd_uuid, templateName, expireDate);

        List<String> metrics = JsonPath.using(conf).parse(template).read("sla_template.ns.objectives[0].metric[*]");

        if (metrics.size() > 0) {
            try {
                String url = "http://pre-int-sp-ath.5gtango.eu:4011/catalogues/api/v2/slas/template-descriptors";
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(template.toString());
                wr.flush();

                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println("" + sb.toString());
                } else {
                    System.out.println(con.getResponseMessage());
                }
            } catch (Exception e) {
            }
            return Response.status(200).entity(template).build();

        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("NSD and/or policyD NOT FOUND").build();
        }
    }
}
