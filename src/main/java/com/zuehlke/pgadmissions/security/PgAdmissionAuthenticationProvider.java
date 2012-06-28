package com.zuehlke.pgadmissions.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
			e.printStackTrace();
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
		if (!userDetails.getPassword().equals(createHash((String) preProcessToken.getCredentials()))) {
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

	public String createHash(String password) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(password.getBytes());
		byte byteData[] = md5.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
	
	@Override
	public boolean supports(Class<? extends Object> clazz) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz);
	}

}
