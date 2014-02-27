package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertSame;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class AuthenticationServiceTest {
    
    @Mock
    @InjectIntoByType
    private UserDAO userDAOMock;
    
    @TestedObject
    private AuthenticationService authenticationService;

    @Test
    public void shouldGetUserFromSecurityContextAndRefresh() {
        RegisteredUser refreshedUser = new RegisteredUser();
        EasyMock.expect(userDAOMock.get(8)).andReturn(refreshedUser);

        replay();
        RegisteredUser user = authenticationService.getCurrentUser();
        verify();

        assertSame(refreshedUser, user);
    }

    @Before
    public void setUp() {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
        RegisteredUser currentUser = new RegisteredUserBuilder().id(8).username("bob").role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        authenticationToken.setDetails(currentUser);
        SecurityContextImpl secContext = new SecurityContextImpl();
        secContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(secContext);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
    
    
}
