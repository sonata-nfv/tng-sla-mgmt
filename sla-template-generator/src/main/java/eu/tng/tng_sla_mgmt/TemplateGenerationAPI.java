package eu.tng.tng_sla_mgmt;

/** imports for RestAPI **/
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/templategeneration")
public class TemplateGenerationAPI {

	// api call in order to generate a sla template
	// mendatory input parameters from the user: nsId, providerId, templateName,
	// expireDate
	// e.g.
	// http://localhost:8080/tng-sla-mgmt/slas/templategeneration?nsId=10&providerId=20&templateName=lala&expireDate=20/02/2018
	@GET
	@Produces("text/plain")
	public Response getIt(@Context UriInfo info) {

		String nsId = info.getQueryParameters().getFirst("nsId");
		String providerId = info.getQueryParameters().getFirst("providerId");
		String templateName = info.getQueryParameters().getFirst("templateName");
		String expireDate = info.getQueryParameters().getFirst("expireDate");

		// call CreateTemplate method
		CreateTemplate ct = new CreateTemplate();
		ct.createTemplate(nsId, providerId, templateName, expireDate);

		return Response.status(200).entity("NS ID : " + nsId + ", from Service Provider : " + providerId
				+ ", Template Name: " + templateName + ", Expiration Date:" + expireDate).build();
	}
}
