package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.services.FullTextSearchService;

public class AutosuggestUserControllerTest {

    private FullTextSearchService searchServiceMock;

    private AutosuggestUserController controller;

    private User similiarToUser1;

    private User user1;
    
    @Before
    public void prepare() {
        user1 = new UserBuilder().firstName("Tyler").lastName("Durden").email("tyler@durden.com")
                .enabled(false).build();

        similiarToUser1 = new UserBuilder().firstName("Taylor").lastName("Dordeen")
                .email("taylor@dordeen.com")
                .enabled(false).build();
        
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
