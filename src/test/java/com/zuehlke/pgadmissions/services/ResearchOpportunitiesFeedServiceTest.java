package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ResearchOpportunitiesFeedDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ResearchOpportunitiesFeedBuilder;
import com.zuehlke.pgadmissions.domain.enums.FeedFormat;

public class ResearchOpportunitiesFeedServiceTest {

    private UserService userServiceMock;
    
    private ResearchOpportunitiesFeedDAO daoMock;
    
    private ProgramDAO programDAOMock;
    
    private static final String HOST = "http://localhost:8080";
    
    private ResearchOpportunitiesFeedService service;
    
    private static final String LARGE_IFRAME = ""
            + "<html> "
            + "<body> "
            + "<iframe src=\"http://localhost:8080/pgadmissions/adverts/standaloneAdverts?feed=1\" " 
            + "width=\"430\" " 
            + "height=\"514\" " 
            + "style=\"border:none;\"> "
            + "</body> "
            + "</html> ";
    
    private static final String SMALL_IFRAME = ""
            + "<html> "
            + "<body> "
            + "<iframe src=\"http://localhost:8080/pgadmissions/adverts/standaloneAdverts?feed=1\" " 
            + "width=\"210\" " 
            + "height=\"514\" " 
            + "style=\"border:none;\"> "
            + "</body> "
            + "</html> ";
    
    @Before
    public void prepare() {
        daoMock = EasyMock.createMock(ResearchOpportunitiesFeedDAO.class);
        programDAOMock = EasyMock.createMock(ProgramDAO.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        service = new ResearchOpportunitiesFeedService(daoMock, programDAOMock, userServiceMock, HOST);
    }
    
    @Test
    public void shouldReturnSmallIframeCodeForFeed() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).feedFormat(FeedFormat.SMALL).programs(program).title("Hello Feed").user(user).build();
        String iframeHtmlCode = service.getIframeHtmlCode(feed);
        Assert.assertEquals(SMALL_IFRAME, iframeHtmlCode);
    }
    
    @Test
    public void shouldReturnLargeIframeCodeForFeed() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).feedFormat(FeedFormat.LARGE).programs(program).title("Hello Feed").user(user).build();
        String iframeHtmlCode = service.getIframeHtmlCode(feed);
        Assert.assertEquals(LARGE_IFRAME, iframeHtmlCode);
    }
    
    @Test
    public void shouldSaveNewFeed() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").build();
        
        EasyMock.expect(programDAOMock.getProgramById(1)).andReturn(program);
        daoMock.save(EasyMock.anyObject(ResearchOpportunitiesFeed.class));
        
        EasyMock.replay(programDAOMock, daoMock);
        
        ResearchOpportunitiesFeed saveNewFeed = service.saveNewFeed(Arrays.asList(1), user, FeedFormat.LARGE, "hello");
        
        EasyMock.verify(programDAOMock, daoMock);
        
        Assert.assertEquals(saveNewFeed.getPrograms().get(0), program);
        Assert.assertEquals(saveNewFeed.getTitle(), "hello");
        Assert.assertEquals(saveNewFeed.getFeedFormat(), FeedFormat.LARGE);
    }
    
    @Test
    public void shouldGetAllFeedsForUser() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        EasyMock.expect(daoMock.getAllFeedsForUser(user)).andReturn(Collections.<ResearchOpportunitiesFeed>emptyList());
        EasyMock.replay(daoMock);
        service.getAllFeedsForUser(user);
        EasyMock.verify(daoMock);
    }
    
    @Test
    public void shouldAskIsUniqueFeedTitleForUser() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        EasyMock.expect(daoMock.isUniqueFeedTitleForUser("hello", user)).andReturn(false);
        EasyMock.replay(daoMock);
        service.isUniqueFeedTitleForUser("hello", user);
        EasyMock.verify(daoMock);
    }
    
    @Test
    public void shouldDeleteFeedById() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).feedFormat(FeedFormat.SMALL).programs(program).title("Hello Feed").user(user).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        
        daoMock.deleteById(1);
        EasyMock.expect(daoMock.getById(1)).andReturn(feed);
        EasyMock.replay(daoMock, userServiceMock);
        service.deleteById(1);
        EasyMock.verify(daoMock, userServiceMock);
    }
    
    @Test
    public void shouldGetFeedById() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).feedFormat(FeedFormat.SMALL).programs(program).title("Hello Feed").user(user).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        
        EasyMock.expect(daoMock.getById(1)).andReturn(feed).times(2);
        EasyMock.replay(daoMock, userServiceMock);
        service.getById(1);
        EasyMock.verify(daoMock, userServiceMock);
    }
    
    @Test
    public void shouldUpdateFeed() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").id(1).username("fooBarZ@fooBarZ.com").build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).feedFormat(FeedFormat.LARGE).programs(program).title("Hello Feed").user(user).build();
        
        EasyMock.expect(programDAOMock.getProgramById(1)).andReturn(program);
        EasyMock.expect(daoMock.getById(1)).andReturn(feed).times(2);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        
        EasyMock.replay(programDAOMock, daoMock, userServiceMock);
        
        ResearchOpportunitiesFeed saveNewFeed = service.updateFeed(1, Arrays.asList(1), user, FeedFormat.SMALL, "hello1");
        
        EasyMock.verify(programDAOMock, daoMock, userServiceMock);
        
        Assert.assertEquals(saveNewFeed.getPrograms().get(0), program);
        Assert.assertEquals(saveNewFeed.getTitle(), "hello1");
        Assert.assertEquals(saveNewFeed.getFeedFormat(), FeedFormat.SMALL);
    }
}
