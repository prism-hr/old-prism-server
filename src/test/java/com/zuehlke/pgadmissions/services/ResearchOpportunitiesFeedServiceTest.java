package com.zuehlke.pgadmissions.services;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ResearchOpportunitiesFeedDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ResearchOpportunitiesFeedBuilder;
import com.zuehlke.pgadmissions.domain.enums.FeedFormat;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ResearchOpportunitiesFeedServiceTest {

    private UserService userServiceMock;

    private ResearchOpportunitiesFeedDAO daoMock;

    private ProgramsService programServiceMock;

    private FreeMarkerConfigurer freeMarkerConfigurerMock;

    private static final String HOST = "http://localhost:8080";

    private ResearchOpportunitiesFeedService service;

    @Before
    public void prepare() {
        daoMock = EasyMock.createMock(ResearchOpportunitiesFeedDAO.class);
        programServiceMock = EasyMock.createMock(ProgramsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        freeMarkerConfigurerMock = EasyMock.createMock(FreeMarkerConfigurer.class);
        service = new ResearchOpportunitiesFeedService(daoMock, programServiceMock, userServiceMock, freeMarkerConfigurerMock, HOST);
    }

    @Test
    public void shouldReturnSmallIframeCodeByFeedId() throws IOException, TemplateException {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).feedFormat(FeedFormat.SMALL).programs(program).title("Hello Feed")
                .user(user).build();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("host", HOST);
        dataMap.put("feedKey", "OPPORTUNITIESBYFEEDID");
        dataMap.put("feedKeyValue", 1);

        Configuration configurationMock = EasyMock.createMock(Configuration.class);
        Template templateMock = EasyMock.createMock(Template.class);

        EasyMock.expect(freeMarkerConfigurerMock.getConfiguration()).andReturn(configurationMock);
        EasyMock.expect(configurationMock.getTemplate(ResearchOpportunitiesFeedService.SMALL_IFRAME)).andReturn(templateMock);
        templateMock.process(EasyMock.eq(dataMap), EasyMock.isA(StringWriter.class));

        EasyMock.replay(freeMarkerConfigurerMock, configurationMock, templateMock);
        service.getIframeHtmlCode(feed);
        EasyMock.verify(freeMarkerConfigurerMock, configurationMock, templateMock);
    }

    @Test
    public void shouldReturnLargeDefaultIframeCodeForCurrentUser() throws IOException, TemplateException {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(-2).feedFormat(FeedFormat.LARGE).programs(program).title("Hello Feed")
                .user(user).build();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("host", HOST);
        dataMap.put("feedKey", "OPPORTUNITIESBYUSERUSERNAME");
        dataMap.put("feedKeyValue", "fooBarZ@fooBarZ.com");

        Configuration configurationMock = EasyMock.createMock(Configuration.class);
        Template templateMock = EasyMock.createMock(Template.class);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(freeMarkerConfigurerMock.getConfiguration()).andReturn(configurationMock);
        EasyMock.expect(configurationMock.getTemplate(ResearchOpportunitiesFeedService.LARGE_IFRAME)).andReturn(templateMock);
        templateMock.process(EasyMock.eq(dataMap), EasyMock.isA(StringWriter.class));

        EasyMock.replay(freeMarkerConfigurerMock, configurationMock, templateMock, userServiceMock);
        service.getIframeHtmlCode(feed);
        EasyMock.verify(freeMarkerConfigurerMock, configurationMock, templateMock, userServiceMock);
    }

    @Test
    public void shouldSaveNewFeed() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").build();

        EasyMock.expect(programServiceMock.getById(1)).andReturn(program);
        daoMock.save(EasyMock.anyObject(ResearchOpportunitiesFeed.class));

        EasyMock.replay(programServiceMock, daoMock);

        ResearchOpportunitiesFeed saveNewFeed = service.saveNewFeed(Arrays.asList(1), user, FeedFormat.LARGE, "hello");

        EasyMock.verify(programServiceMock, daoMock);

        Assert.assertEquals(saveNewFeed.getPrograms().get(0), program);
        Assert.assertEquals(saveNewFeed.getTitle(), "hello");
        Assert.assertEquals(saveNewFeed.getFeedFormat(), FeedFormat.LARGE);
    }

    @Test
    public void shouldGetAllFeedsForUser() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        EasyMock.expect(programServiceMock.getProgramsForWhichCanManageProjects(user)).andReturn(null).times(2);
        EasyMock.expect(daoMock.getAllFeedsForUser(user)).andReturn(Collections.<ResearchOpportunitiesFeed> emptyList());

        EasyMock.replay(daoMock, programServiceMock);
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
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).feedFormat(FeedFormat.SMALL).programs(program).title("Hello Feed")
                .user(user).build();
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
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).feedFormat(FeedFormat.SMALL).programs(program).title("Hello Feed")
                .user(user).build();

        EasyMock.expect(daoMock.getById(1)).andReturn(feed).times(1);
        EasyMock.replay(daoMock);
        service.getById(1);
        EasyMock.verify(daoMock);
    }

    @Test
    public void shouldUpdateFeed() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").id(1).username("fooBarZ@fooBarZ.com").build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().id(1).feedFormat(FeedFormat.LARGE).programs(program).title("Hello Feed")
                .user(user).build();

        EasyMock.expect(programServiceMock.getById(1)).andReturn(program);
        EasyMock.expect(daoMock.getById(1)).andReturn(feed);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);

        EasyMock.replay(programServiceMock, daoMock, userServiceMock);

        ResearchOpportunitiesFeed saveNewFeed = service.updateFeed(1, Arrays.asList(1), user, FeedFormat.SMALL, "hello1");

        EasyMock.verify(programServiceMock, daoMock, userServiceMock);

        assertEquals(saveNewFeed.getPrograms().get(0), program);
        assertEquals(saveNewFeed.getTitle(), "hello1");
        assertEquals(saveNewFeed.getFeedFormat(), FeedFormat.SMALL);
    }
    
    @Test
    public void shouldGetDefaultOpportunitiesFeedsByUsername(){
        RegisteredUser primaryUser = new RegisteredUserBuilder().id(1).build();
        RegisteredUser secondaryUser = new RegisteredUserBuilder().id(2).primaryAccount(primaryUser).build();
        RegisteredUser ternaryUser = new RegisteredUserBuilder().id(3).primaryAccount(primaryUser).build();
        
        primaryUser.getLinkedAccounts().addAll(Lists.newArrayList(secondaryUser, ternaryUser));
        
        EasyMock.expect(programServiceMock.getProgramsForWhichCanManageProjects(primaryUser)).andReturn(null);
        EasyMock.expect(programServiceMock.getProgramsForWhichCanManageProjects(secondaryUser)).andReturn(null);
        EasyMock.expect(programServiceMock.getProgramsForWhichCanManageProjects(ternaryUser)).andReturn(null);
        EasyMock.expect(userServiceMock.getUserByUsername("pojebe")).andReturn(secondaryUser);

        EasyMock.replay(userServiceMock, programServiceMock);
        List<ResearchOpportunitiesFeed> feeds = service.getDefaultOpportunitiesFeedsByUsername("pojebe", null);
        EasyMock.verify(userServiceMock, programServiceMock);
        
        assertEquals(3, feeds.size());
    }
}
