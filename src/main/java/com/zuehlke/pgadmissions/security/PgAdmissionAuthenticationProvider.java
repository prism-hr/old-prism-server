package com.zuehlke.pgadmissions.security;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class PgAdmissionAuthenticationProvider implements AuthenticationProvider {

    private Logger log = LoggerFactory.getLogger(PgAdmissionAuthenticationProvider.class);
    
	private final UserDetailsService userDetailsService;
	
	private final EncryptionUtils encryptionUtils;

	@Autowired
	public PgAdmissionAuthenticationProvider(UserDetailsService userDetailsService, EncryptionUtils encryptionUtils) {
		this.userDetailsService = userDetailsService;
		this.encryptionUtils = encryptionUtils;
	}

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
			authentication = new UsernamePasswordAuthenticationToken(preProcessToken.getPrincipal(),
					preProcessToken.getCredentials(), user.getAuthorities());
			authentication.setDetails(user);
		} catch (NoSuchAlgorithmException e) {		
		    log.error(e.getMessage(), e);
		}
		return authentication;
	}

	private UserDetails findAndValidateUser(Authentication preProcessToken) throws NoSuchAlgorithmException {
		String username = (String) preProcessToken.getPrincipal();

		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (!userDetails.isEnabled()) {
			throw new DisabledException("account \"" + username + "\" disabled");
		}
		if (!userDetails.isAccountNonExpired()) {
			throw new AccountExpiredException("account \"" + username + "\" expired");
		}
		if (!StringUtils.equals(userDetails.getPassword(), encryptionUtils.getMD5Hash((String) preProcessToken.getCredentials()))) {
			throw new BadCredentialsException("invalid username/password combination");
		}
		if (!userDetails.isAccountNonLocked()) {
			throw new LockedException("account \"" + username + "\" locked");
		}
		if (!userDetails.isCredentialsNonExpired()) {
			throw new CredentialsExpiredException("credentials for \"" + username + "\" expired");
		}
		return userDetails;
	}
	
	@Override
	public boolean supports(Class<? extends Object> clazz) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz);
	}
}
