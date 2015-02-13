package com.zuehlke.pgadmissions.security;

import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.user.User;

public class PrismAuthenticationProvider implements AuthenticationProvider {

    private static Logger LOGGER = LoggerFactory.getLogger(PrismAuthenticationProvider.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Override
    @Transactional
    public Authentication authenticate(Authentication preProcessToken) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authentication = null;
        UserDetails user;
        try {
            user = findAndValidateUser(preProcessToken);
            authentication = new UsernamePasswordAuthenticationToken(preProcessToken.getPrincipal(), preProcessToken.getCredentials(), user.getAuthorities());
            authentication.setDetails(user);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return authentication;
    }

    private UserDetails findAndValidateUser(Authentication preProcessToken) throws NoSuchAlgorithmException {
        String username = (String) preProcessToken.getPrincipal();
        String password = (String) preProcessToken.getCredentials();

        User user = (User) userDetailsService.loadUserByUsername(username);
        boolean validCredentials = userAuthenticationService.validateCredentials(user, password);
        if (!validCredentials) {
            throw new BadCredentialsException("Bad credentials");
        }

        return user;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz);
    }
    
}
