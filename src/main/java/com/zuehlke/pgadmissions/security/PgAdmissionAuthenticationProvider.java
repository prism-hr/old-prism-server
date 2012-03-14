package com.zuehlke.pgadmissions.security;

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

public class PgAdmissionAuthenticationProvider implements AuthenticationProvider {

	private final UserDetailsService userDetailsService;

	public PgAdmissionAuthenticationProvider(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	@Transactional
	public Authentication authenticate(Authentication preProcessToken) throws AuthenticationException {
		if (preProcessToken.getPrincipal() == null || preProcessToken.getCredentials() == null) {
			throw new BadCredentialsException("missing username or password");
		}
		UserDetails user = findAndValidateUser(preProcessToken);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(preProcessToken.getPrincipal(),
				preProcessToken.getCredentials(), user.getAuthorities());

		authentication.setDetails(user);

		return authentication;
	}

	private UserDetails findAndValidateUser(Authentication preProcessToken) {
		String username = (String) preProcessToken.getPrincipal();
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (!userDetails.isEnabled()) {
			throw new DisabledException("account \"" + username + "\" disabled");
		}
		if (!userDetails.isAccountNonExpired()) {
			throw new AccountExpiredException("account \"" + username + "\" expired");
		}
		if (!userDetails.getPassword().equals(preProcessToken.getCredentials())) {
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
