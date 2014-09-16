package com.zuehlke.pgadmissions.security;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
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

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class PrismAuthenticationProvider implements AuthenticationProvider {

    private Logger log = LoggerFactory.getLogger(PrismAuthenticationProvider.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EncryptionUtils encryptionUtils;

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
            log.error(e.getMessage(), e);
        }
        return authentication;
    }

    private UserDetails findAndValidateUser(Authentication preProcessToken) throws NoSuchAlgorithmException {
        String username = (String) preProcessToken.getPrincipal();
        String password = (String) preProcessToken.getCredentials();

        User user = (User) userDetailsService.loadUserByUsername(username);
        if (username == null || password == null || user == null || !user.isEnabled() || !checkPassword(user, password)) {
            throw new BadCredentialsException("Bad login attempt");
        }

        return user;
    }

    @Override
    public boolean supports(Class<? extends Object> clazz) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz);
    }

    private boolean checkPassword(User user, String providedPassword) {
        return StringUtils.equals(user.getUserAccount().getPassword(), encryptionUtils.getMD5Hash(providedPassword))
                || checkTemporaryPassword(user, providedPassword);
    }

    private boolean checkTemporaryPassword(User user, String providedPassword) {
        DateTime temporaryPasswordExpiryTimestamp = user.getUserAccount().getTemporaryPasswordExpiryTimestamp();
        return temporaryPasswordExpiryTimestamp != null && new DateTime().isAfter(temporaryPasswordExpiryTimestamp)
                && StringUtils.equals(user.getUserAccount().getTemporaryPassword(), encryptionUtils.getMD5Hash(providedPassword));
    }
}
