package com.zuehlke.pgadmissions.services;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.zuehlke.pgadmissions.dao.ProgramExportDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramExport;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ProgramExportFormat;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ProgramExportServiceTest {

    private UserService userServiceMock;

    private ProgramExportDAO daoMock;

    private ProgramService programServiceMock;

    private FreeMarkerConfigurer freeMarkerConfigurerMock;

    private static final String HOST = "http://localhost:8080";

    private ProgramExportService service;

    @Before
    public void prepare() {
        daoMock = EasyMock.createMock(ProgramExportDAO.class);
        programServiceMock = EasyMock.createMock(ProgramService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        freeMarkerConfigurerMock = EasyMock.createMock(FreeMarkerConfigurer.class);
        service = new ProgramExportService(daoMock, programServiceMock, userServiceMock, freeMarkerConfigurerMock, HOST);
    }

    @Test
    public void shouldReturnSmallIframeCodeByFeedId() throws IOException, TemplateException {
        User user = new User().withEmail("fooBarZ@fooBarZ.com");
        Program program = new Program().withCode("XXXXXXXXXXX").withTitle("Program1");
        ProgramExport feed = new ProgramExport().withId(1).withFormat(ProgramExportFormat.SMALL).withPrograms(program).withTitle("Hello Feed")
                .withUser(user);
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("host", HOST);
        dataMap.put("feedKey", "OPPORTUNITIESBYFEEDID");
        dataMap.put("feedKeyValue", 1);

        Configuration configurationMock = EasyMock.createMock(Configuration.class);
        Template templateMock = EasyMock.createMock(Template.class);

        EasyMock.expect(freeMarkerConfigurerMock.getConfiguration()).andReturn(configurationMock);
        EasyMock.expect(configurationMock.getTemplate(ProgramExportService.SMALL_IFRAME)).andReturn(templateMock);
        templateMock.process(EasyMock.eq(dataMap), EasyMock.isA(StringWriter.class));

        EasyMock.replay(freeMarkerConfigurerMock, configurationMock, templateMock);
        service.getIframeHtmlCode(feed);
        EasyMock.verify(freeMarkerConfigurerMock, configurationMock, templateMock);
    }

    @Test
    public void shouldReturnLargeDefaultIframeCodeForCurrentUser() throws IOException, TemplateException {
        User user = new User().withEmail("fooBarZ@fooBarZ.com");
        Program program = new Program().withCode("XXXXXXXXXXX").withTitle("Program1");
        ProgramExport feed = new ProgramExport().withId(-2).withFormat(ProgramExportFormat.LARGE).withPrograms(program).withTitle("Hello Feed")
                .withUser(user);
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("host", HOST);
        dataMap.put("feedKey", "OPPORTUNITIESBYUSERUSERNAME");
        dataMap.put("feedKeyValue", "fooBarZ@fooBarZ.com");

        Configuration configurationMock = EasyMock.createMock(Configuration.class);
        Template templateMock = EasyMock.createMock(Template.class);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(freeMarkerConfigurerMock.getConfiguration()).andReturn(configurationMock);
        EasyMock.expect(configurationMock.getTemplate(ProgramExportService.LARGE_IFRAME)).andReturn(templateMock);
        templateMock.process(EasyMock.eq(dataMap), EasyMock.isA(StringWriter.class));

        EasyMock.replay(freeMarkerConfigurerMock, configurationMock, templateMock, userServiceMock);
        service.getIframeHtmlCode(feed);
        EasyMock.verify(freeMarkerConfigurerMock, configurationMock, templateMock, userServiceMock);
    }

    @Test
    public void shouldSaveNewFeed() {
        User user = new User().withEmail("fooBarZ@fooBarZ.com");
        Program program = new Program().withCode("XXXXXXXXXXX").withTitle("Program1");

        EasyMock.expect(programServiceMock.getById(1)).andReturn(program);
        daoMock.save(EasyMock.anyObject(ProgramExport.class));

        EasyMock.replay(programServiceMock, daoMock);

        ProgramExport saveNewFeed = service.saveNewFeed(Arrays.asList(1), user, ProgramExportFormat.LARGE, "hello");

        EasyMock.verify(programServiceMock, daoMock);

        Assert.assertEquals(saveNewFeed.getPrograms().get(0), program);
        Assert.assertEquals(saveNewFeed.getTitle(), "hello");
        Assert.assertEquals(saveNewFeed.getFormat(), ProgramExportFormat.LARGE);
    }

    @Test
    public void shouldGetAllFeedsForUser() {
        User user = new User().withEmail("fooBarZ@fooBarZ.com");
        EasyMock.expect(programServiceMock.getProgramsForWhichCanManageProjects(user)).andReturn(null).times(2);
        EasyMock.expect(daoMock.getAllFeedsForUser(user)).andReturn(Collections.<ProgramExport> emptyList());

        EasyMock.replay(daoMock, programServiceMock);
        service.getAllFeedsForUser(user);
        EasyMock.verify(daoMock);
    }

    @Test
    public void shouldAskIsUniqueFeedTitleForUser() {
        User user = new User().withEmail("fooBarZ@fooBarZ.com");
        EasyMock.expect(daoMock.isUniqueFeedTitleForUser("hello", user)).andReturn(false);
        EasyMock.replay(daoMock);
        service.isUniqueFeedTitleForUser("hello", user);
        EasyMock.verify(daoMock);
    }

    @Test
    public void shouldDeleteFeedById() {
        User user = new User().withId(1).withEmail("fooBarZ@fooBarZ.com");
        Program program = new Program().withCode("XXXXXXXXXXX").withTitle("Program1");
        ProgramExport feed = new ProgramExport().withId(1).withFormat(ProgramExportFormat.SMALL).withPrograms(program).withTitle("Hello Feed")
                .withUser(user);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);

        daoMock.deleteById(1);
        EasyMock.expect(daoMock.getById(1)).andReturn(feed);
        EasyMock.replay(daoMock, userServiceMock);
        service.deleteById(1);
        EasyMock.verify(daoMock, userServiceMock);
    }

    @Test
    public void shouldGetFeedById() {
        User user = new User().withId(1).withEmail("fooBarZ@fooBarZ.com");
        Program program = new Program().withCode("XXXXXXXXXXX").withTitle("Program1");
        ProgramExport feed = new ProgramExport().withId(1).withFormat(ProgramExportFormat.SMALL).withPrograms(program).withTitle("Hello Feed")
                .withUser(user);

        EasyMock.expect(daoMock.getById(1)).andReturn(feed).times(1);
        EasyMock.replay(daoMock);
        service.getById(1);
        EasyMock.verify(daoMock);
    }

    @Test
    public void shouldUpdateFeed() {
        User user = new User().withEmail("fooBarZ@fooBarZ.com").withId(1);
        Program program = new Program().withCode("XXXXXXXXXXX").withTitle("Program1");
        ProgramExport feed = new ProgramExport().withId(1).withFormat(ProgramExportFormat.LARGE).withPrograms(program).withTitle("Hello Feed")
                .withUser(user);

        EasyMock.expect(programServiceMock.getById(1)).andReturn(program);
        EasyMock.expect(daoMock.getById(1)).andReturn(feed);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);

        EasyMock.replay(programServiceMock, daoMock, userServiceMock);

        ProgramExport saveNewFeed = service.updateFeed(1, Arrays.asList(1), user, ProgramExportFormat.SMALL, "hello1");

        EasyMock.verify(programServiceMock, daoMock, userServiceMock);

        assertEquals(saveNewFeed.getPrograms().get(0), program);
        assertEquals(saveNewFeed.getTitle(), "hello1");
        assertEquals(saveNewFeed.getFormat(), ProgramExportFormat.SMALL);
    }

}
