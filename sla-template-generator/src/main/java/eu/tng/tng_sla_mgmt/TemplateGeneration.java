package eu.tng.tng_sla_mgmt;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
/** imports for RestAPI **/
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/templategeneration")
public class TemplateGeneration {
	
	@GET 
	@Produces("text/plain")
	public String getIt() {
        return "Template Generation in progress!";
    }
	

}
