package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.FullTextSearchService;

public class AutosuggestUserControllerTest {

    private FullTextSearchService searchServiceMock;

    private AutosuggestUserController controller;

    private RegisteredUser similiarToUser1;

    private RegisteredUser user1;
    
    @Before
    public void prepare() {
        user1 = new RegisteredUserBuilder().firstName("Tyler").lastName("Durden").email("tyler@durden.com")
                .username("tyler@durden.com").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();

        similiarToUser1 = new RegisteredUserBuilder().firstName("Taylor").lastName("Dordeen")
                .email("taylor@dordeen.com").username("taylor@durden.com").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        
        searchServiceMock = EasyMock.createMock(FullTextSearchService.class);
        controller = new AutosuggestUserController(searchServiceMock);
    }
    
    @Test
    public void shouldReturnCorrectJsonForAMatchOnFirstname() {
        EasyMock.expect(searchServiceMock.getMatchingUsersWithFirstnameLike("Tyler")).andReturn(Arrays.asList(user1, similiarToUser1));
        EasyMock.replay(searchServiceMock);
        String json = controller.provideSuggestionsForFirstname("Tyler");
        assertEquals("[{\"k\":\"Tyler\",\"v\":\"Durden\",\"d\":\"tyler@durden.com\"},{\"k\":\"Taylor\",\"v\":\"Dordeen\",\"d\":\"taylor@dordeen.com\"}]", json);
        EasyMock.verify(searchServiceMock);
    }
    
    @Test
    public void shouldReturnCorrectJsonForAMatchOnLastname() {
        EasyMock.expect(searchServiceMock.getMatchingUsersWithLastnameLike("Tyler")).andReturn(Arrays.asList(user1, similiarToUser1));
        EasyMock.replay(searchServiceMock);
        String json = controller.provideSuggestionsForLastname("Tyler");
        assertEquals("[{\"k\":\"Tyler\",\"v\":\"Durden\",\"d\":\"tyler@durden.com\"},{\"k\":\"Taylor\",\"v\":\"Dordeen\",\"d\":\"taylor@dordeen.com\"}]", json);
        EasyMock.verify(searchServiceMock);
    }
    
    @Test
    public void shouldReturnCorrectJsonForAMatchOnEmail() {
        EasyMock.expect(searchServiceMock.getMatchingUsersWithEmailLike("Tyler")).andReturn(Arrays.asList(user1, similiarToUser1));
        EasyMock.replay(searchServiceMock);
        String json = controller.provideSuggestionsForEmail("Tyler");
        assertEquals("[{\"k\":\"Tyler\",\"v\":\"Durden\",\"d\":\"tyler@durden.com\"},{\"k\":\"Taylor\",\"v\":\"Dordeen\",\"d\":\"taylor@dordeen.com\"}]", json);
        EasyMock.verify(searchServiceMock);
    }
    
}
