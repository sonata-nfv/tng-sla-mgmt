package eu.tng.tng_sla_mgmt;

/** imports for RestAPI **/
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONObject;

@Path("/templategeneration")
public class TemplateGenerationAPI {

	// api call in order to generate a sla template
	// mendatory input parameters from the user: nsId, providerId, templateName,
	// expireDate
	// e.g.
	// http://localhost:8080/tng-sla-mgmt/slas/templategeneration?nsId=8effe1db-edd3-404f-8a68-92e2ebb2b176&providerId=20&templateName=lala&expireDate=20/02/2018
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIt(@Context UriInfo info) {

		String nsId = info.getQueryParameters().getFirst("nsId");
		String providerId = info.getQueryParameters().getFirst("providerId");
		String templateName = info.getQueryParameters().getFirst("templateName");
		String expireDate = info.getQueryParameters().getFirst("expireDate");

		// call CreateTemplate method
		CreateTemplate ct = new CreateTemplate();
		JSONObject lala = ct.createTemplate(nsId, providerId, templateName, expireDate);		
		return Response.status(200).entity(lala).build();
	}
}
