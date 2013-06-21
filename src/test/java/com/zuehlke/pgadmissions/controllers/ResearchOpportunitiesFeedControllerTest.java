package com.zuehlke.pgadmissions.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ResearchOpportunitiesFeedBuilder;
import com.zuehlke.pgadmissions.domain.enums.FeedFormat;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.ResearchOpportunitiesFeedService;
import com.zuehlke.pgadmissions.services.UserService;

public class ResearchOpportunitiesFeedControllerTest {

    private ProgramsService programsServiceMock;
    
    private ResearchOpportunitiesFeedService feedServiceMock;
    
    private MessageSource messageSourceMock;
    
    private UserService userServiceMock;
    
    private ResearchOpportunitiesFeedController controller;
    
    private RegisteredUser currentUser;
    
    @Before
    public void prepare() {
        currentUser = new RegisteredUserBuilder().build();
        programsServiceMock = EasyMock.createMock(ProgramsService.class);
        feedServiceMock = EasyMock.createMock(ResearchOpportunitiesFeedService.class);
        messageSourceMock = EasyMock.createMock(MessageSource.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        controller = new ResearchOpportunitiesFeedController(userServiceMock, programsServiceMock, feedServiceMock, messageSourceMock) {
            @Override
            protected RegisteredUser getCurrentUser() {
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
}
