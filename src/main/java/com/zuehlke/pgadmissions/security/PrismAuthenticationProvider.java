package com.zuehlke.pgadmissions.security;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

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
        if (preProcessToken.getPrincipal() == null || preProcessToken.getCredentials() == null) {
            throw new BadCredentialsException("missing username or password");
        }
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

        User user = (User) userDetailsService.loadUserByUsername(username);
        if (!user.isEnabled()) {
            throw new DisabledException("account \"" + username + "\" disabled");
        }
        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("account \"" + username + "\" expired");
        }
        String password = (String) preProcessToken.getCredentials();
        if (!checkPassword(password, user.getPassword())) {
            DateTime temporaryPasswordExpiryTimestamp = user.getUserAccount().getTemporaryPasswordExpiryTimestamp();
            if (temporaryPasswordExpiryTimestamp == null || new DateTime().isAfter(temporaryPasswordExpiryTimestamp) || !checkPassword(password, user.getUserAccount().getTemporaryPassword()))
                throw new BadCredentialsException("Invalid username/password combination");
        }
        if (!user.isAccountNonLocked()) {
            throw new LockedException("account \"" + username + "\" locked");
        }
        if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("credentials for \"" + username + "\" expired");
        }
        return user;
    }

    @Override
    public boolean supports(Class<? extends Object> clazz) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz);
    }

    private boolean checkPassword(String providedPassword, String storedPassword) {
        return StringUtils.equals(storedPassword, encryptionUtils.getMD5Hash(providedPassword));
    }
}
