package com.zuehlke.pgadmissions.security;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserResource {

    @Resource(name = "pgAdmissionUserDetailsService")
    private UserDetailsService userService;

    @Autowired
    @Named("authenticationManager")
    private AuthenticationManager authManager;

    /**
     * Retrieves the currently logged in user.
     * 
     * @return A transfer containing the username and the roles.
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof String && ((String) principal).equals("anonymousUser")) {
            throw new WebApplicationException(401);
        }
        UserDetails userDetails = (UserDetails) principal;

        return userDetails.getUsername();
    }

    /**
     * Authenticates a user and creates an authentication token.
     * 
     * @param username
     *            The name of the user.
     * @param password
     *            The password of the user.
     * @return A transfer containing the authentication token.
     */
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST, produces = "application/json")
    public String authenticate(@RequestParam(required = false, value = "username") String username,
            @RequestParam(required = false, value = "password") String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = this.authManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload user as password of authentication principal will be null after authorisation and password is needed for token generation
        UserDetails userDetails = this.userService.loadUserByUsername(username);

        return TokenUtils.createToken(userDetails);
    }

}