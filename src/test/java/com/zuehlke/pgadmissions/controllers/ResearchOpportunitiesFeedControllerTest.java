package com.zuehlke.pgadmissions.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ResearchOpportunitiesFeedBuilder;
import com.zuehlke.pgadmissions.domain.enums.FeedFormat;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.ResearchOpportunitiesFeedService;
import com.zuehlke.pgadmissions.services.UserService;

public class ResearchOpportunitiesFeedControllerTest {

    private ProgramService programsServiceMock;
    
    private ResearchOpportunitiesFeedService feedServiceMock;
    
    private MessageSource messageSourceMock;
    
    private UserService userServiceMock;
    
    private ResearchOpportunitiesFeedController controller;
    
    private User currentUser;
    
    @Before
    public void prepare() {
        currentUser = new UserBuilder().build();
        programsServiceMock = EasyMock.createMock(ProgramService.class);
        feedServiceMock = EasyMock.createMock(ResearchOpportunitiesFeedService.class);
        messageSourceMock = EasyMock.createNiceMock(MessageSource.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        controller = new ResearchOpportunitiesFeedController(userServiceMock, programsServiceMock, feedServiceMock, messageSourceMock) {
            @Override
            protected User getCurrentUser() {
                return currentUser;
            }
        };
    }
    
    @Test
    public void shouldReturnProgrammesAsJson() {
        Program program = new ProgramBuilder().id(1).title("foo").build();
        EasyMock.expect(programsServiceMock.getProgramsForWhichCanManageProjects(currentUser)).andReturn(Arrays.asList(program));
        EasyMock.replay(programsServiceMock);
        
        List<Map<String,Object>> response = controller.getProgrammes();

        EasyMock.verify(programsServiceMock);
        Assert.assertEquals(1, response.size());
        Map<String, Object> map = response.get(0);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(program.getId(), map.get("id"));
        Assert.assertEquals(program.getTitle(), map.get("title"));
    }
    
    @Test
    public void shouldReturnAllResearchOpportunitiesFeedForCurrentUser() {
        Program program = new ProgramBuilder().id(1).title("foo").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).title("foobar").feedFormat(FeedFormat.LARGE).programs(program).build();
        EasyMock.expect(feedServiceMock.getAllFeedsForUser(currentUser)).andReturn(Arrays.asList(feed));
        EasyMock.expect(feedServiceMock.getIframeHtmlCode(feed)).andReturn(StringUtils.EMPTY);
        EasyMock.replay(feedServiceMock);
        
        List<Map<String,Object>> response = controller.getAllResearchOpportunitiesFeedForCurrentUser();
        
        EasyMock.verify(feedServiceMock);
        Assert.assertEquals(1, response.size());
        
        Map<String, Object> map = response.get(0);
        Assert.assertEquals(5, map.size());
        Assert.assertEquals(feed.getId(), map.get("id"));
        Assert.assertEquals(feed.getTitle(), map.get("title"));
        Assert.assertEquals(FeedFormat.LARGE, map.get("feedSize"));
        Assert.assertEquals(Arrays.asList(1), map.get("selectedPrograms"));
        Assert.assertEquals(StringUtils.EMPTY, map.get("iframeCode"));
    }
    
    @Test
    public void shouldDeleteByFeedId() {
        feedServiceMock.deleteById(1);
        EasyMock.replay(feedServiceMock);
        
        Map<String, Object> map = controller.deleteFeedById(1);
        
        EasyMock.verify(feedServiceMock);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(true, map.get("success"));
    }
    
    @Test
    public void shouldUpdateFeedById() {
        Program program = new ProgramBuilder().id(1).title("foo").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).title("foobar").feedFormat(FeedFormat.LARGE).programs(program).build();
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("selectedPrograms", Arrays.asList(1));
        json.put("feedSize", "SMALL");
        json.put("feedTitle", "foobar2");
     
        EasyMock.expect(feedServiceMock.updateFeed(feed.getId(), Arrays.asList(1), currentUser, FeedFormat.SMALL, "foobar2")).andReturn(feed);
        EasyMock.expect(feedServiceMock.getIframeHtmlCode(feed)).andReturn(StringUtils.EMPTY);
        EasyMock.replay(feedServiceMock);
        
        Map<String, Object> map = controller.updateFeedById(feed.getId(), json);

        EasyMock.verify(feedServiceMock);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(true, map.get("success"));
        Assert.assertEquals(StringUtils.EMPTY, map.get("iframeCode"));
    }
    
    @Test
    public void shouldNotValidateIfTitleIsEmpty() {
        Program program = new ProgramBuilder().id(1).title("foo").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).title("foobar").feedFormat(FeedFormat.LARGE).programs(program).build();
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("selectedPrograms", Arrays.asList(1));
        json.put("feedSize", "SMALL");
        json.put("feedTitle", StringUtils.EMPTY);
     
        Map<String, Object> map = controller.updateFeedById(feed.getId(), json);

        Assert.assertEquals(2, map.size());
        Assert.assertEquals(false, map.get("success"));
        Assert.assertTrue(map.containsKey("feedTitle"));
    }
    
    @Test
    public void shouldNotValidateIfFeedFormatIsEmpty() {
        Program program = new ProgramBuilder().id(1).title("foo").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).title("foobar").feedFormat(FeedFormat.LARGE).programs(program).build();
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("selectedPrograms", Arrays.asList(1));
        json.put("feedSize", StringUtils.EMPTY);
        json.put("feedTitle", "foobar2");
     
        Map<String, Object> map = controller.updateFeedById(feed.getId(), json);

        Assert.assertEquals(2, map.size());
        Assert.assertEquals(false, map.get("success"));
        Assert.assertTrue(map.containsKey("feedSize"));
    }
    
    @Test
    public void shouldNotValidateIfProgramsIsEmpty() {
        Program program = new ProgramBuilder().id(1).title("foo").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).title("foobar").feedFormat(FeedFormat.LARGE).programs(program).build();
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("feedSize", "LARGE");
        json.put("feedTitle", "foobar2");
     
        EasyMock.expect(messageSourceMock.getMessage(EasyMock.anyObject(String.class), EasyMock.anyObject(Object[].class), EasyMock.anyObject(Locale.class))).andReturn(StringUtils.EMPTY);
        EasyMock.replay(messageSourceMock);
        
        Map<String, Object> map = controller.updateFeedById(feed.getId(), json);
        EasyMock.verify(messageSourceMock);

        Assert.assertEquals(2, map.size());
        Assert.assertEquals(false, map.get("success"));
        Assert.assertTrue(map.containsKey("selectedPrograms"));
    }
    
    @Test
    public void shouldReturnFeedById() {
        Program program = new ProgramBuilder().id(1).title("foo").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).title("foobar").feedFormat(FeedFormat.LARGE).programs(program).build();
        EasyMock.expect(feedServiceMock.getById(feed.getId())).andReturn(feed);
        EasyMock.expect(feedServiceMock.getIframeHtmlCode(feed)).andReturn(StringUtils.EMPTY);
        EasyMock.replay(feedServiceMock);
        
        List<Map<String,Object>> response = controller.getFeedById(feed.getId());
        
        EasyMock.verify(feedServiceMock);
        
        Assert.assertEquals(1, response.size());
        
        Map<String, Object> map = response.get(0);
        Assert.assertEquals(5, map.size());
        Assert.assertEquals(feed.getId(), map.get("id"));
        Assert.assertEquals(feed.getTitle(), map.get("title"));
        Assert.assertEquals(FeedFormat.LARGE, map.get("feedSize"));
        Assert.assertEquals(Arrays.asList(1), map.get("selectedPrograms"));
        Assert.assertEquals(StringUtils.EMPTY, map.get("iframeCode"));
    }
    
    @Test
    public void shouldSaveFeed() {
        Program program = new ProgramBuilder().id(1).title("foo").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).title("foobar").feedFormat(FeedFormat.LARGE).programs(program).build();
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("selectedPrograms", Arrays.asList(1));
        json.put("feedSize", "LARGE");
        json.put("feedTitle", "foobar2");
     
        EasyMock.expect(feedServiceMock.saveNewFeed(Arrays.asList(1), currentUser, FeedFormat.LARGE, "foobar2")).andReturn(feed);
        EasyMock.expect(feedServiceMock.isUniqueFeedTitleForUser("foobar2", currentUser)).andReturn(true);
        EasyMock.expect(feedServiceMock.getIframeHtmlCode(feed)).andReturn(StringUtils.EMPTY);
        EasyMock.replay(feedServiceMock);
        
        Map<String, Object> map = controller.saveFeed(json);

        EasyMock.verify(feedServiceMock);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals(true, map.get("success"));
        Assert.assertEquals(StringUtils.EMPTY, map.get("iframeCode"));
        Assert.assertEquals(1, map.get("id"));
    }
    
    @Test
    public void shouldNotSaveFeedIfUserHasSimiliarFeedWithSameTitle() {
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("selectedPrograms", Arrays.asList(1));
        json.put("feedSize", "LARGE");
        json.put("feedTitle", "foobar2");
     
        EasyMock.expect(feedServiceMock.isUniqueFeedTitleForUser("foobar2", currentUser)).andReturn(false);
        EasyMock.replay(feedServiceMock);
        
        Map<String, Object> map = controller.saveFeed(json);

        EasyMock.verify(feedServiceMock);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(false, map.get("success"));
        Assert.assertTrue(map.containsKey("feedTitle"));
    }
}
