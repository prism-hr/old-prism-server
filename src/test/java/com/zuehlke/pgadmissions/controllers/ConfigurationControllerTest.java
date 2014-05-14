package com.zuehlke.pgadmissions.controllers;

import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.APPLICATION_COMPLETE_NOTIFICATION;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.FieldError;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;

import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.ScoringDefinitionBuilder;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.ApplicationExportConfigurationDTO;
import com.zuehlke.pgadmissions.dto.ServiceLevelsDTO;
import com.zuehlke.pgadmissions.exceptions.NotificationTemplateException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.JsonPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.services.ApplicationExportConfigurationService;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.ExportQueueService;
import com.zuehlke.pgadmissions.services.NotificationTemplateService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ConfigurationControllerTest {

    private static final String CONFIGURATION_VIEW_NAME = "/private/staff/superAdmin/configuration";
    private static final String CONFIGURATION_SECTION_NAME = "/private/staff/superAdmin/configuration_section";

    private ConfigurationController controller;
    private User superAdmin;
    private JsonPropertyEditor stageDurationPropertyEditorMock;
    private JsonPropertyEditor reminderIntervalPropertyEditorMock;
    private JsonPropertyEditor notificationsDurationPropertyEditorMock;
    private UserService userServiceMock;
    private NotificationTemplateService emailTemplateServiceMock;
    private ApplicationExportConfigurationService throttleserviceMock;
    private ConfigurationService configurationServiceMock;
    private User admin;
    private ExportQueueService queueServiceMock;
    private ProgramService programsServiceMock;
    private ScoringDefinitionParser scoringDefinitionParserMock;
    private ApplicationContext applicationContext;

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundIfNotSuperAdminOrADmin() {
        User applicant = new User().withId(1)
                ;

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant).anyTimes();
        EasyMock.replay(userServiceMock);
        controller.getConfigurationPage();
    }

    @Test
    public void shouldGetConfigurationPageIfSuperAdmin() {
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(superAdmin).anyTimes();
        EasyMock.replay(userServiceMock);
        String view = controller.getConfigurationPage();
        assertEquals(CONFIGURATION_VIEW_NAME, view);
    }

    @Test
    public void shouldGetConfigurationPageIfAdmin() {
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
        EasyMock.replay(userServiceMock);
        String view = controller.getConfigurationPage();
        assertEquals(CONFIGURATION_VIEW_NAME, view);
    }

    @Test
    public void shouldGetConfigurationSectionIfSuperAdmin() {
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(superAdmin).anyTimes();
        EasyMock.replay(userServiceMock);
        String view = controller.getConfigurationSection();
        assertEquals(CONFIGURATION_SECTION_NAME, view);
    }

    @Test
    public void shouldGetSimpleMessageViewIfNotSuperdmin() {
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
        EasyMock.replay(userServiceMock);
        String view = controller.getConfigurationSection();
        assertEquals("/private/common/simpleMessage", view);
    }

    @Test
    public void shouldGetPossibleDurationUnits() {
        org.junit.Assert.assertArrayEquals(DurationUnitEnum.values(), controller.getUnits());
    }

    @Test
    public void shouldExtractConfigurationObjectsAndSave() {

        ServiceLevelsDTO stageDurationDto = new ServiceLevelsDTO();

        configurationServiceMock.saveServiceLevels(stageDurationDto);
        expect(userServiceMock.getCurrentUser()).andReturn(superAdmin).anyTimes();

        replay(configurationServiceMock, userServiceMock);
        String view = controller.submit(stageDurationDto);
        verify(configurationServiceMock, userServiceMock);

        assertEquals("redirect:/configuration/config_section", view);
    }

    @Test
    public void saveTemplateShouldSetPropertiesInMap() {
        Date date = new DateTime(2012, 11, 5, 0, 0).toDate();
        NotificationTemplateVersion version = new NotificationTemplateVersion().withId(1).withCreatedTimestamp(date);
        
        expect(emailTemplateServiceMock.saveTemplateVersion(APPLICATION_COMPLETE_NOTIFICATION, "Some content", "Some subject")).andReturn(version);
        
        EasyMockUnitils.replay();
        Map<String, Object> result = controller.saveTemplate(APPLICATION_COMPLETE_NOTIFICATION, "Some content", "Some subject");
        
        assertEquals("2012/11/5 - 00:00:00", result.get("createdTimestamp"));
        assertEquals(version.getId(), result.get("id"));
    }

    @Test
    public void activateTemplateShouldSetPropertiesInMap() throws Exception {
        NotificationTemplateVersion oldVersion = new NotificationTemplateVersion();
        NotificationTemplate template = new NotificationTemplate().withVersion(oldVersion);
        
        expect(emailTemplateServiceMock.getById(APPLICATION_COMPLETE_NOTIFICATION)).andReturn(template);
        emailTemplateServiceMock.activateTemplateVersion(APPLICATION_COMPLETE_NOTIFICATION, 1);

        replay();
        Map<String, Object> result = controller.activateTemplate("APPLICATION_SUBMIT_CONFIRMATION", 1, false, null, null);

        assertEquals(2, result.get("previousTemplateId"));
    }

    @Test
    public void activateTemplateShouldSaveANewTemplateCopyAndSetPropertiesInMap() throws Exception {
        NotificationTemplateVersion oldVersion = new NotificationTemplateVersion();
        NotificationTemplate template = new NotificationTemplate().withVersion(oldVersion);
        
        NotificationTemplateVersion newVersion = new NotificationTemplateVersion();
                
        expect(emailTemplateServiceMock.saveTemplateVersion(APPLICATION_COMPLETE_NOTIFICATION, "whatever", "new subject")).andReturn(newVersion);
        expect(emailTemplateServiceMock.getById(APPLICATION_COMPLETE_NOTIFICATION)).andReturn(template);
        emailTemplateServiceMock.activateTemplateVersion(APPLICATION_COMPLETE_NOTIFICATION, 3);
        replay(emailTemplateServiceMock);

        Map<String, Object> result = controller.activateTemplate("APPLICATION_SUBMIT_CONFIRMATION", 1, true, "whatever", "new subject");

        verify(emailTemplateServiceMock);
        assertEquals((Long) 2L, (Long) result.get("previousTemplateId"));
        assertEquals((Long) 3L, (Long) result.get("id"));
        assertEquals("2012/11/5 - 00:00:00", result.get("version"));
    }

    @Test
    public void activateTemplateShouldSetErrorInMap() throws Exception {
        NotificationTemplate template = new NotificationTemplate();
        
        expect(emailTemplateServiceMock.getById(APPLICATION_COMPLETE_NOTIFICATION)).andReturn(template);
        emailTemplateServiceMock.activateTemplateVersion(APPLICATION_COMPLETE_NOTIFICATION, 1);
        expectLastCall().andThrow(new NotificationTemplateException("test error"));
        replay(emailTemplateServiceMock);

        Map<String, Object> result = controller.activateTemplate("APPLICATION_SUBMIT_CONFIRMATION", 1, false, null, null);

        verify(emailTemplateServiceMock);
        assertEquals("test error", result.get("error"));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfNotSueradmin() {
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();

        EasyMock.replay(configurationServiceMock, userServiceMock);
        controller.submit(null);
    }

    @Test
    public void shouldGetThrottleAndSetProperties() {
        Map<String, Object> result = controller.getApplicationExportConfiguration();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(true, result.get("enabled"));
        assertEquals(40, result.get("batchSize"));
        assertEquals((short) 3, result.get("processingDelay"));
        assertEquals(DurationUnitEnum.DAYS, result.get("processingDelayUnit"));
        verify(throttleserviceMock);
    }

    @Test
    public void shouldUpdateThrottle() {
        ApplicationExportConfigurationDTO configuration = new ApplicationExportConfigurationDTO();
        throttleserviceMock.updateApplicationExportConfiguration(configuration);

        EasyMock.expect(throttleserviceMock.userTurnedOnThrottle(false)).andReturn(false);

        BindingResult errors = new DirectFieldBindingResult(configuration, "throttle");

        replay(throttleserviceMock);
        Map<String, Object> result = controller.updateApplicationExportConfiguration(configuration, errors);
        verify(throttleserviceMock);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldNotUpdateThrottleAndReturnErrorMessage() {
        ApplicationExportConfigurationDTO configuration = new ApplicationExportConfigurationDTO();

        BindingResult errors = new DirectFieldBindingResult(configuration, "configuration");
        errors.rejectValue("enabled", "code");

        EasyMock.expect(applicationContext.getMessage(EasyMock.isA(FieldError.class), EasyMock.eq(Locale.getDefault()))).andReturn("message");

        replay(applicationContext);
        Map<String, Object> result = controller.updateApplicationExportConfiguration(configuration, errors);
        verify(applicationContext);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("message", result.get("enabled"));
    }

    @Test
    public void shouldTriggerSendingApplicationsToPorticoIfTheSwitchHasBeenSetToTrue() {
        ApplicationExportConfigurationDTO configuration = new ApplicationExportConfigurationDTO();
        throttleserviceMock.updateApplicationExportConfiguration(configuration);
        EasyMock.expect(throttleserviceMock.userTurnedOnThrottle(true)).andReturn(true);
        queueServiceMock.sendQueuedApprovedApplicationsToPortico();

        BindingResult errors = new DirectFieldBindingResult(configuration, "throttle");

        replay(throttleserviceMock, queueServiceMock);
        controller.updateApplicationExportConfiguration(configuration, errors);
        verify(throttleserviceMock, queueServiceMock);
    }

    @Test
    public void shouldReturnCurrentUser() {
        User currentUserMock = EasyMock.createMock(User.class);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
        EasyMock.replay(userServiceMock);
        assertEquals(currentUserMock, controller.getUser());
    }

    @Test
    public void shouldEditScoringDefinition() {
        Program program = new Program();
        HttpServletResponse response = new MockHttpServletResponse();

        EasyMock.expect(programsServiceMock.getProgramByCode("any_code")).andReturn(program);
        programsServiceMock.applyScoringDefinition("any_code", ScoringStage.INTERVIEW, "content");

        EasyMock.replay(programsServiceMock);
        assertEquals(Collections.emptyMap(), controller.editScoringDefinition("any_code", ScoringStage.INTERVIEW, "content", response));
        EasyMock.verify(programsServiceMock);
    }

    @Test
    public void shouldAllowSavingEmptyScoringDefinition() {
        Program program = new Program();
        HttpServletResponse response = new MockHttpServletResponse();

        String programCode = "any_code";
        EasyMock.expect(programsServiceMock.getProgramByCode(programCode)).andReturn(program);
        programsServiceMock.removeScoringDefinition(programCode, ScoringStage.INTERVIEW);

        EasyMock.replay(programsServiceMock);
        assertEquals(Collections.emptyMap(), controller.editScoringDefinition(programCode, ScoringStage.INTERVIEW, "", response));
        EasyMock.verify(programsServiceMock);
    }

    @Test
    public void shouldFailToEditScoringDefinitionDueToIncorrectProgramCode() {
        HttpServletResponse response = new MockHttpServletResponse();

        EasyMock.expect(programsServiceMock.getProgramByCode("any_code")).andReturn(null);

        EasyMock.replay(programsServiceMock);
        assertEquals(Collections.singletonMap("programCode", "Given program code is not valid"),
                controller.editScoringDefinition("any_code", ScoringStage.INTERVIEW, "content", response));
        EasyMock.verify(programsServiceMock);
    }

    @Test
    public void shouldFailToEditScoringDefinitionDueToIncorrectXmlContent() throws Exception {
        Program program = new Program();
        HttpServletResponse response = new MockHttpServletResponse();

        EasyMock.expect(programsServiceMock.getProgramByCode("any_code")).andReturn(program);
        scoringDefinitionParserMock.parseScoringDefinition("content");
        EasyMock.expectLastCall().andThrow(new ScoringDefinitionParseException("ex message", null));

        EasyMock.replay(programsServiceMock, scoringDefinitionParserMock);
        assertEquals(Collections.singletonMap("scoringContent", "ex message"),
                controller.editScoringDefinition("any_code", ScoringStage.INTERVIEW, "content", response));
        EasyMock.verify(programsServiceMock, scoringDefinitionParserMock);
    }

    @Test
    public void shouldGetScoringDefinition() {
        Program program = new Program();
        program.getScoringDefinitions().put(ScoringStage.REVIEW, new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("Mleko").build());

        EasyMock.expect(programsServiceMock.getProgramByCode("any_code")).andReturn(program);

        EasyMock.replay(programsServiceMock);
        assertEquals("Mleko", controller.getScoringDefinition("any_code", ScoringStage.REVIEW));
        EasyMock.verify(programsServiceMock);
    }

    @Test
    public void shouldGetNotDefinedScoringDefinition() {
        Program program = new Program();
        EasyMock.expect(programsServiceMock.getProgramByCode("any_code")).andReturn(program);

        EasyMock.replay(programsServiceMock);
        assertEquals("", controller.getScoringDefinition("any_code", ScoringStage.REVIEW));
        EasyMock.verify(programsServiceMock);
    }

}
