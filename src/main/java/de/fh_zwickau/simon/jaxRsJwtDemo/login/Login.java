package de.fh_zwickau.simon.jaxRsJwtDemo.login;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.fh_zwickau.simon.jaxRsJwtDemo.util.SimpleKeyGenerator;
import de.fh_zwickau.simon.jaxRsJwtDemo.util.UserDatabase;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Path("login")
//@Transactional
public class Login {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String echo() {
		String response = "<form action='login' method='post'>username:<br><input type='text' name='login'><br>";
		response += "password:<br><input type='text' name='password'><br>";
		response += "<input type='submit' value='Login'/></form>";
        return response;
    }	

    //@Inject
    //private KeyGenerator keyGenerator;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response authenticateUser(@FormParam("login") String login,
                                     @FormParam("password") String password, @Context UriInfo uriInfo) {
        try {

            // Authenticate the user using the credentials provided
        	String userID = UserDatabase.getUserID(login, password);
        	boolean isAdmin = false;
        	
        	// check if user was found
            if (!userID.equals(""))
            {
            	// check if user is admin
            	isAdmin = UserDatabase.isAdmin(userID);
            }
            else
            {
            	// user was not found
            	throw new Exception();
            }            

            // Issue a token for the user
            String token = issueToken(userID, isAdmin, uriInfo);

            // Return the token on the response
            return Response.ok(token).header(HttpHeaders.AUTHORIZATION, token).build();

        } catch (Exception e) {
        	// return 401 if login failed
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    private String issueToken(String login, boolean isAdmin, UriInfo uriInfo) {
    	// create key for encryption
    	Key key = SimpleKeyGenerator.generateKey();
    	
    	// set expiration time to 60 minutes
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(60L);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        
        String jwtToken = Jwts.builder()
                .setSubject(login)
                .claim("isAdmin", String.valueOf(isAdmin))
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        return jwtToken;
    }
    
}
