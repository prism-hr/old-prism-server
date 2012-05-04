package com.zuehlke.pgadmissions.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class PgAdmissionAuthenticationProviderTest {

	private PgAdmissionAuthenticationProvider authenticationProvider;
	private UserDetailsService userDetailsServiceMock;

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
	
	@Test
	public void shouldSupportUsernamePasswordAuthenticationToken() {
		Assert.assertTrue(authenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	public void shouldReturnPopulatedAuthenticationForValidCredentials() throws NoSuchAlgorithmException {
		Role roleOne = new RoleBuilder().id(1).authorityEnum(Authority.APPLICANT).toRole();
		Role roleTwo = new RoleBuilder().id(2).authorityEnum(Authority.ADMINISTRATOR).toRole();
		RegisteredUser user = new RegisteredUserBuilder().username("bob").password(createHash("secret")).roles(roleOne, roleTwo).id(1).toUser();
		EasyMock.expect(userDetailsServiceMock.loadUserByUsername("bob")).andReturn(user).anyTimes();
		EasyMock.replay(userDetailsServiceMock);

		UsernamePasswordAuthenticationToken preProcessAuthenticationToken = new UsernamePasswordAuthenticationToken("bob", "secret");

		Authentication authentication = authenticationProvider.authenticate(preProcessAuthenticationToken);

		Assert.assertSame(user, authentication.getDetails());

		Assert.assertEquals("bob", authentication.getPrincipal());
		Assert.assertEquals("secret", authentication.getCredentials());
		Assert.assertEquals("bob", authentication.getName());
		Assert.assertEquals(2, authentication.getAuthorities().size());
		Assert.assertTrue(authentication.getAuthorities().containsAll(Arrays.asList(roleOne, roleTwo)));
		Assert.assertTrue(authentication.isAuthenticated());
	}

	@Test(expected = BadCredentialsException.class)
	public void shouldThrowBadCredentialsExceptionForMismatchingPassword() throws NoSuchAlgorithmException {
		RegisteredUser user = new RegisteredUserBuilder().username("bob").password(createHash("secret")).id(1).toUser();
		EasyMock.expect(userDetailsServiceMock.loadUserByUsername("bob")).andReturn(user).anyTimes();
		EasyMock.replay(userDetailsServiceMock);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("bob", "notsecret");
		authenticationProvider.authenticate(authenticationToken);
	}

	@Test(expected = BadCredentialsException.class)
	public void shouldThrowBadCredentialsExceptionForMissingUsername() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, "something");
		authenticationProvider.authenticate(authenticationToken);
	}

	@Test(expected = BadCredentialsException.class)
	public void shouldThrowBadCredentialsExceptionForMissingPassword() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("bob", null);
		authenticationProvider.authenticate(authenticationToken);
	}

	@Test(expected = UsernameNotFoundException.class)
	public void shouldThrowUsernameNotFoundExceptionForNonExistingUser() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("fred", "something");
		EasyMock.expect(userDetailsServiceMock.loadUserByUsername("fred")).andThrow(new UsernameNotFoundException("fred"));
		EasyMock.replay(userDetailsServiceMock);
		authenticationProvider.authenticate(authenticationToken);
	}

	@Test(expected = DisabledException.class)
	public void shouldThrowDisabledExceptionForDisabledAccount() {
		RegisteredUser user = new RegisteredUserBuilder().username("bob").password("secret").enabled(false).id(1).toUser();
		EasyMock.expect(userDetailsServiceMock.loadUserByUsername("bob")).andReturn(user).anyTimes();
		EasyMock.replay(userDetailsServiceMock);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("bob", "secret");
		authenticationProvider.authenticate(authenticationToken);
	}

	@Test(expected = AccountExpiredException.class)
	public void shouldThrowAccountExpiredExceptionForExpiredAccount() {
		RegisteredUser user = new RegisteredUserBuilder().username("bob").password("secret").accountNonExpired(false).id(1).toUser();
		EasyMock.expect(userDetailsServiceMock.loadUserByUsername("bob")).andReturn(user).anyTimes();
		EasyMock.replay(userDetailsServiceMock);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("bob", "secret");
		authenticationProvider.authenticate(authenticationToken);
	}

	@Test(expected = CredentialsExpiredException.class)
	public void shouldThrowCredentialsExpiredExceptionForExpiredCredentials() throws NoSuchAlgorithmException {
		RegisteredUser user = new RegisteredUserBuilder().username("bob").password(createHash("secret")).credentialsNonExpired(false).id(1).toUser();
		EasyMock.expect(userDetailsServiceMock.loadUserByUsername("bob")).andReturn(user).anyTimes();
		EasyMock.replay(userDetailsServiceMock);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("bob", "secret");
		authenticationProvider.authenticate(authenticationToken);
	}

	@Test(expected = LockedException.class)
	public void shouldThrowLockedExceptionForLockedAccount() throws NoSuchAlgorithmException {
		RegisteredUser user = new RegisteredUserBuilder().username("bob").password(createHash("secret")).accountNonLocked(false).id(1).toUser();
		EasyMock.expect(userDetailsServiceMock.loadUserByUsername("bob")).andReturn(user).anyTimes();
		EasyMock.replay(userDetailsServiceMock);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("bob", "secret");
		authenticationProvider.authenticate(authenticationToken);
	}

	@Before
	public void setup() {
		userDetailsServiceMock = EasyMock.createMock(UserDetailsService.class);
		authenticationProvider = new PgAdmissionAuthenticationProvider(userDetailsServiceMock);
	}
}
