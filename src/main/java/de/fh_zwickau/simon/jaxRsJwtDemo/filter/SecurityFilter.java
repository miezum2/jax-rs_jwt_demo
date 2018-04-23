package de.fh_zwickau.simon.jaxRsJwtDemo.filter;

import java.io.IOException;
import java.security.Key;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import de.fh_zwickau.simon.jaxRsJwtDemo.util.SimpleKeyGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
    	
    	// store original security context
        SecurityContext originalContext = requestContext.getSecurityContext();  
        
        // Get the HTTP Authorization header from the request
        String token = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        // initialize roles and userID
        Set<String> roles = new HashSet<>();
        String userID = "";
        
        // check contents of token if not null
        if (token != null)
        {
        	// try to validate the token
        	try {
        		// get key used for encryption
                Key key = SimpleKeyGenerator.generateKey();
                
                // parse claims from token
                Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
                userID = claims.getSubject();
                
                // no exception occurred, token is valid, add USER role
            	roles.add("USER");
                
            	// check if user is admin
                if (claims.get("isAdmin").equals("true"))
                {
                	roles.add("ADMIN");
                }

            } catch (Exception e) {
            	// if token is invalid, do not add roles
            }    
        }
        
        // create new SecurityContext with roles and userID
        Authorizer authorizer = new Authorizer(roles, userID, originalContext.isSecure());
        
        // replace old SecurityContext
        requestContext.setSecurityContext(authorizer);
    }

    public static class Authorizer implements SecurityContext {

        Set<String> roles;
        String username;
        boolean isSecure;
        public Authorizer(Set<String> roles, final String username, 
                                             boolean isSecure) {
            this.roles = roles;
            this.username = username;
            this.isSecure = isSecure;
        }

        @Override
        public Principal getUserPrincipal() {
            return new User(username);
        }

        @Override
        public boolean isUserInRole(String role) {
            return roles.contains(role);
        }

        @Override
        public boolean isSecure() {
            return isSecure;
        }

        @Override
        public String getAuthenticationScheme() {
            return "Your Scheme";
        } 
    } 

    public static class User implements Principal {
        String name;

        public User(String name) {
            this.name = name;
        }

        @Override
        public String getName() { return name; }   
    }
}