package com.zuehlke.pgadmissions.security;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class PgAdmissionAuthenticationProviderTest {

	private PgAdmissionAuthenticationProvider authenticationProvider;
	private UserDetailsService userDetailsServiceMock;
	private EncryptionUtils encryptionUtilsMock;
	
	@Test
	public void shouldSupportUsernamePasswordAuthenticationToken() {
		Assert.assertTrue(authenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	public void shouldReturnPopulatedAuthenticationForValidCredentials() throws NoSuchAlgorithmException {
		Role roleOne = new RoleBuilder().id(Authority.APPLICANT).build();
		Role roleTwo = new RoleBuilder().id(Authority.ADMINISTRATOR).build();
		RegisteredUser user = new RegisteredUserBuilder().username("bob").password("secret").id(1).build();
		EasyMock.expect(userDetailsServiceMock.loadUserByUsername("bob")).andReturn(user).anyTimes();
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("secret")).andReturn("secret");
        EasyMock.replay(userDetailsServiceMock, encryptionUtilsMock);

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
		RegisteredUser user = new RegisteredUserBuilder().username("bob").password("secret").id(1).build();
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
		RegisteredUser user = new RegisteredUserBuilder().username("bob").password("secret").enabled(false).id(1).build();
		EasyMock.expect(userDetailsServiceMock.loadUserByUsername("bob")).andReturn(user).anyTimes();
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("secret")).andReturn("secret");
        EasyMock.replay(userDetailsServiceMock, encryptionUtilsMock);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("bob", "secret");
		authenticationProvider.authenticate(authenticationToken);
	}

	@Before
	public void setup() {
		userDetailsServiceMock = EasyMock.createMock(UserDetailsService.class);
		encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
		authenticationProvider = new PgAdmissionAuthenticationProvider(userDetailsServiceMock, encryptionUtilsMock);
	}
}
