package de.fh_zwickau.simon.jaxRsJwtDemo.resource;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import de.fh_zwickau.simon.jaxRsJwtDemo.filter.SecurityFilter.User;

@Path("resource")
public class Resource {

	@GET
	@Path("public")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPublicInformation() {
        return "public information accessed";
    }
    
    @GET
    @Path("secure")
    @RolesAllowed({"ADMIN"})
    public String getUsername(@Context SecurityContext securityContext) {
        User user = (User)securityContext.getUserPrincipal();
        return "secure information accessed with user ID " + user.getName();
    } 
}
