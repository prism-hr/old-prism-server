package com.zuehlke.pgadmissions.services;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class SwitchUserServiceTest {
    
    private SwitchUserService authenticationProvider;
    
    private RegisteredUser user1;
    
    private RegisteredUser user2;
    
    private Role role1;
    
    private Role role2;
    
    @Before
    public void setup() {
        authenticationProvider = new SwitchUserService();
        
        role1 = new RoleBuilder().id(Authority.APPLICANT).build();
        
        role2 = new RoleBuilder().id(Authority.ADMINISTRATOR).build();
        
        user1 = new RegisteredUserBuilder().id(5).firstName("Jane").lastName("Doe").email("jane@doe.com")
                .username("janeUsername").password("password").enabled(true)
//                .roles(role1)
                .build();
        
        user2 = new RegisteredUserBuilder().id(6).firstName("John").lastName("Doe").email("john@doe.com")
                .username("johnUsername").password("password").enabled(true)
//                .roles(role1, role2)
                .build();
        
        user2.setPrimaryAccount(user1);
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
        user2.setEnabled(false);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user1, user2);
        authenticationProvider.authenticate(authenticationToken);
    }

}
