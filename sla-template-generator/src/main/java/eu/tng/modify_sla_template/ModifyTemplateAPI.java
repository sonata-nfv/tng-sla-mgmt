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
package eu.tng.modify_sla_template;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONObject;

@Path("/templates")
public class ModifyTemplateAPI {

	/**
	 * api call in order to modify an already existing sla template mendatory input
	 * parameters from the user: uuid, field, old_value, value.
	 * http://localhost:8080/tng-sla-mgmt/slas/templates?uuid=<>&field=<>&old-value=<>&value=<>
	 * 
	 */

	@SuppressWarnings("static-access")
    @GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response ModifyTemplate(@Context UriInfo info) {

		String uuid = info.getQueryParameters().getFirst("uuid");
		String field = info.getQueryParameters().getFirst("field");
		String old_value = info.getQueryParameters().getFirst("old_value");
		String value = info.getQueryParameters().getFirst("value");

		String field_to_edit = (field + "=" + old_value);

		Sla_Editor se = new Sla_Editor();
		String new_sla_uuid = se.Edit_value(uuid, field_to_edit, value);

		Get_Sla_Template mt = new Get_Sla_Template();
		JSONObject edited_template = mt.Get_Sla(new_sla_uuid);

		return Response.status(200).entity(edited_template).build();
	}

	@GET
	@Path("/customize")
	public Response CustomizeTemplate(@QueryParam("objectives") List<String> objectives,
			@QueryParam("uuid") String sla_uuid, @QueryParam("slo_value") List<String> slo_value,
			@QueryParam("slo_value") List<String> slo_definition, @QueryParam("slo_unit") List<String> slo_unit,
			@QueryParam("metric") List<String> metric, @QueryParam("expression") List<String> expression,
			@QueryParam("expression_unit") List<String> expression_unit, @QueryParam("rate") List<String> rate,
			@QueryParam("parameter_unit") List<String> parameter_unit,
			@QueryParam("parameter_definition") List<String> parameter_definition,
			@QueryParam("parameter_value") List<String> parameter_value,
			@QueryParam("parameter_name") List<String> parameter_name) {

		@SuppressWarnings("unused")
        Sla_Editor se = new Sla_Editor();
		String new_uuid = Sla_Editor.Add_Fields(sla_uuid, objectives, slo_value, slo_definition, slo_unit,
				metric, expression, expression_unit, rate,parameter_unit,parameter_definition,parameter_value,parameter_name);
		
		Get_Sla_Template mt = new Get_Sla_Template();
        JSONObject modified_template = mt.Get_Sla(new_uuid);
        
		return Response.status(200).entity(modified_template).build();
		
	}
}