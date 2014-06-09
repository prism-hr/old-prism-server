package com.zuehlke.pgadmissions.security;

import static org.unitils.easymock.EasyMockUnitils.replay;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class PgAdmissionAuthenticationProviderTest {

    @Mock
    @InjectIntoByType
    private UserDetailsService userDetailsServiceMock;

    @Mock
    @InjectIntoByType
    private EncryptionUtils encryptionUtilsMock;

    @TestedObject
    private PgAdmissionAuthenticationProvider authenticationProvider;

    @Test
    public void shouldSupportUsernamePasswordAuthenticationToken() {
        Assert.assertTrue(authenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void shouldReturnPopulatedAuthenticationForValidCredentials() throws NoSuchAlgorithmException {
        Role roleOne = new Role().withId(Authority.APPLICATION_CREATOR);
        Role roleTwo = new Role().withId(Authority.PROGRAM_ADMINISTRATOR);
        User user = new User().withAccount(new UserAccount().withEnabled(true)).withId(1);
        EasyMock.expect(userDetailsServiceMock.loadUserByUsername("bob")).andReturn(user).anyTimes();
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("secret")).andReturn("secret");

        UsernamePasswordAuthenticationToken preProcessAuthenticationToken = new UsernamePasswordAuthenticationToken("bob", "secret");

        replay();
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
        User user = new User().withAccount(new UserAccount().withEnabled(true)).withId(1);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("bob", "notsecret");

        EasyMock.expect(userDetailsServiceMock.loadUserByUsername("bob")).andReturn(user).anyTimes();

        replay();
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
        
        replay();
        authenticationProvider.authenticate(authenticationToken);
    }

    @Test(expected = DisabledException.class)
    public void shouldThrowDisabledExceptionForDisabledAccount() {
        User user = new User().withAccount(new UserAccount().withEnabled(false)).withId(1);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("bob", "secret");
        
        EasyMock.expect(userDetailsServiceMock.loadUserByUsername("bob")).andReturn(user).anyTimes();
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("secret")).andReturn("secret");
        
        replay();
        authenticationProvider.authenticate(authenticationToken);
    }

}
