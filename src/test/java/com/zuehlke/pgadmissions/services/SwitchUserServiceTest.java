package com.zuehlke.pgadmissions.services;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class SwitchUserServiceTest {
    
    private SwitchUserService authenticationProvider;
    
    private User user1;
    
    private User user2;
    
    private Role role1;
    
    private Role role2;
    
    @Before
    public void setup() {
        authenticationProvider = new SwitchUserService();
        
        role1 = new Role().withId(Authority.APPLICATION_CREATOR);
        
        role2 = new Role().withId(Authority.PROGRAM_ADMINISTRATOR);
        
        user1 = new User().withId(5).withFirstName("Jane").withLastName("Doe").withEmail("jane@doe.com")
                .withAccount(new UserAccount().withEnabled(true))
//                .roles(role1)
                ;
        
        user2 = new User().withId(6).withFirstName("John").withLastName("Doe").withEmail("john@doe.com")
                .withAccount(new UserAccount().withEnabled(true))
//                .roles(role1, role2)
                ;
        
        user2.setParentUser(user1);
        user1.getLinkedAccounts().add(user2);
    }
    
    @Test
    public void shouldReturnPopulatedAuthenticationForValidCredentials() throws NoSuchAlgorithmException {
        UsernamePasswordAuthenticationToken preProcessAuthenticationToken = new UsernamePasswordAuthenticationToken(user1, user2);
        Authentication authentication = authenticationProvider.authenticate(preProcessAuthenticationToken);
        Assert.assertSame(user2, authentication.getDetails());
        Assert.assertEquals(2, authentication.getAuthorities().size());
        Assert.assertTrue(authentication.getAuthorities().containsAll(Arrays.asList(role1, role2)));
        Assert.assertTrue(authentication.isAuthenticated());
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldThrowBadCredentialsExceptionForMismatchingPassword() throws NoSuchAlgorithmException {
        user1.getLinkedAccounts().clear();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user1, user2);
        authenticationProvider.authenticate(authenticationToken);
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldThrowBadCredentialsExceptionForNullValues() {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
        authenticationProvider.authenticate(authenticationToken);
    }
    
    @Test(expected = DisabledException.class)
    public void shouldThrowDisabledExceptionForDisabledAccount() {
        user2.getAccount().setEnabled(false);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user1, user2);
        authenticationProvider.authenticate(authenticationToken);
    }

}
