package eu.tng.modify_sla_template;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response ModifyTemplate(@Context UriInfo info) {

        String uuid = info.getQueryParameters().getFirst("uuid");
        String field = info.getQueryParameters().getFirst("field");
        String old_value = info.getQueryParameters().getFirst("old_value");
        String value = info.getQueryParameters().getFirst("value");

        String field_to_edit = (field + "=" + old_value);

        Sla_Editor se = new Sla_Editor();
        se.Edit_value(uuid, field_to_edit, value);

        Get_Sla_Template mt = new Get_Sla_Template();
        JSONObject edited_template = mt.Get_Sla(uuid);

        return Response.status(200).entity(edited_template).build();
    }

}