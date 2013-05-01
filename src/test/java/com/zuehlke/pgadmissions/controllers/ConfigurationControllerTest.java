package com.zuehlke.pgadmissions.controllers;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.APPLICATION_SUBMIT_CONFIRMATION;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.Throttle;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.ScoringDefinitionBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ThrottleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.RegistryUserDTO;
import com.zuehlke.pgadmissions.dto.StageDurationDTO;
import com.zuehlke.pgadmissions.exceptions.EmailTemplateException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.StageDurationPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.PorticoQueueService;
import com.zuehlke.pgadmissions.services.ThrottleService;
import com.zuehlke.pgadmissions.services.UserService;

public class ConfigurationControllerTest {

	private static final String CONFIGURATION_VIEW_NAME = "/private/staff/superAdmin/configuration";
	private static final String CONFIGURATION_SECTION_NAME = "/private/staff/superAdmin/configuration_section";

	private ConfigurationController controller;
	private RegisteredUser superAdmin;
	private StageDurationPropertyEditor stageDurationPropertyEditorMock;
	private PersonPropertyEditor registryPropertyEditorMock;
	private UserService userServiceMock;
	private EmailTemplateService emailTemplateServiceMock;
	private ThrottleService throttleserviceMock;
	private ConfigurationService configurationServiceMock;
	private RegisteredUser admin;
	private PorticoQueueService queueServiceMock;
	private ProgramsService programsServiceMock;
	private ScoringDefinitionParser scoringDefinitionParserMock;

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundIfNotSuperAdminOrADmin() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).username("aa").email("aa@gmail.com").firstName("mark").lastName("ham")
		                .role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();

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
	public void shouldRegistorPropertyEditorForStageDurations() {
		WebDataBinder dataBinderMock = EasyMock.createMock(WebDataBinder.class);
		dataBinderMock.registerCustomEditor(StageDuration.class, stageDurationPropertyEditorMock);
		EasyMock.replay(dataBinderMock);
		controller.registerValidatorsAndPropertyEditors(dataBinderMock);
		EasyMock.verify(dataBinderMock);
	}

	@Test
	public void shouldRegistorPropertyEditorForRegistryUsers() {
		WebDataBinder dataBinderMock = EasyMock.createMock(WebDataBinder.class);
		dataBinderMock.registerCustomEditor(Person.class, registryPropertyEditorMock);
		EasyMock.replay(dataBinderMock);
		controller.registerValidatorsAndPropertyEditorsForRegistryUsers(dataBinderMock);
		EasyMock.verify(dataBinderMock);
	}

	@Test
	public void shouldGetStageDurationsConvertedToStringKeys() {
		Map<ApplicationFormStatus, StageDuration> map = new HashMap<ApplicationFormStatus, StageDuration>();
		StageDuration validationDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).build();
		map.put(ApplicationFormStatus.VALIDATION, validationDuration);
		StageDuration approvalDuration = new StageDurationBuilder().stage(ApplicationFormStatus.APPROVAL).build();
		map.put(ApplicationFormStatus.APPROVAL, approvalDuration);
		EasyMock.expect(configurationServiceMock.getStageDurations()).andReturn(map);
		EasyMock.replay(configurationServiceMock);
		Map<String, StageDuration> stageDurations = controller.getStageDurations();
		assertEquals(2, stageDurations.size());
		assertEquals(validationDuration, stageDurations.get("VALIDATION"));
		assertEquals(approvalDuration, stageDurations.get("APPROVAL"));
	}

	@Test
	public void shouldGetReminderInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);

		EasyMock.expect(configurationServiceMock.getReminderInterval()).andReturn(reminderInterval);
		EasyMock.replay(configurationServiceMock);
		assertEquals(reminderInterval, controller.getReminderInterval());
	}

	@Test
	public void shouldGetAllRegistryUsers() {
		Person personOne = new PersonBuilder().id(1).build();
		Person personTwo = new PersonBuilder().id(4).build();

		EasyMock.expect(configurationServiceMock.getAllRegistryUsers()).andReturn(Arrays.asList(personOne, personTwo));
		EasyMock.replay(configurationServiceMock);
		List<Person> allRegistryUsers = controller.getAllRegistryContacts();
		assertEquals(2, allRegistryUsers.size());
		assertTrue(allRegistryUsers.containsAll(Arrays.asList(personOne, personTwo)));
	}

	@Test
	public void shouldGetPossibleDurationUnits() {
		org.junit.Assert.assertArrayEquals(DurationUnitEnum.values(), controller.getUnits());
	}

	@Test
	public void shouldExtractConfigurationObjectsAndSave() {
		expect(userServiceMock.getCurrentUser()).andReturn(superAdmin).anyTimes();
		EasyMock.replay(userServiceMock);
		StageDuration validationDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).duration(1).unit(DurationUnitEnum.HOURS).build();
		StageDuration interviewDuration = new StageDurationBuilder().stage(ApplicationFormStatus.INTERVIEW).duration(3).unit(DurationUnitEnum.WEEKS).build();

		StageDurationDTO stageDurationDto = new StageDurationDTO();
		List<StageDuration> stageDurationList = Arrays.asList(validationDuration, interviewDuration);
		stageDurationDto.setStagesDuration(stageDurationList);

		Person registryUserOne = new PersonBuilder().id(1).build();
		Person registryUserTwo = new PersonBuilder().id(2).build();

		RegistryUserDTO registryUserDTO = new RegistryUserDTO();
		List<Person> registryContactList = Arrays.asList(registryUserOne, registryUserTwo);
		registryUserDTO.setRegistryUsers(registryContactList);

		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);

		configurationServiceMock.saveConfigurations(stageDurationList, registryContactList, reminderInterval);

		replay(configurationServiceMock);

		String view = controller.submit(stageDurationDto, registryUserDTO, reminderInterval);

		verify(configurationServiceMock);
		assertEquals("redirect:/configuration/config_section", view);

	}

	@Test
	public void getTemplateVersionShouldSetPropertiesInMap() {
		EmailTemplate template = new EmailTemplateBuilder().id(1L).name(APPLICATION_SUBMIT_CONFIRMATION).content("Some content").subject("Some subject")
		                .build();
		expect(emailTemplateServiceMock.getEmailTemplate(1L)).andReturn(template);
		replay(emailTemplateServiceMock);

		Map<String, Object> result = controller.getTemplateVersion(1L);

		verify(emailTemplateServiceMock);
		assertEquals(template.getContent(), result.get("content"));
		assertEquals(template.getVersion(), result.get("version"));
		assertEquals(template.getSubject(), result.get("subject"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getTemplateVersionsShouldSetPropertiesInMap() {
		EmailTemplate template = new EmailTemplateBuilder().id(1L).name(APPLICATION_SUBMIT_CONFIRMATION).content("Some content").subject("Some subject")
		                .active(true).build();
		Map<Long, String> returnedByMock = new HashMap<Long, String>();
		returnedByMock.put(1L, "default");
		returnedByMock.put(2L, "12/12/2012 - 12:12:12");
		returnedByMock.put(3L, "12/12/2013 - 12:12:12");
		expect(emailTemplateServiceMock.getActiveEmailTemplate(APPLICATION_SUBMIT_CONFIRMATION)).andReturn(template);
		expect(emailTemplateServiceMock.getEmailTemplateVersions(APPLICATION_SUBMIT_CONFIRMATION)).andReturn(returnedByMock);
		replay(emailTemplateServiceMock);

		Map<Object, Object> result = controller.getVersionsForTemplate("APPLICATION_SUBMIT_CONFIRMATION");

		verify(emailTemplateServiceMock);
		assertEquals(template.getContent(), result.get("content"));
		assertEquals(template.getId(), result.get("activeVersion"));
		assertEquals(template.getSubject(), result.get("subject"));
		Map<Long, String> actual = (Map<Long, String>) result.get("versions");
		assertNotNull(actual);
		for (Map.Entry<Long, String> expectedEntry : returnedByMock.entrySet()) {
			assertEquals(expectedEntry.getValue(), actual.get(expectedEntry.getKey()));
		}
	}

	@Test
	public void saveTemplateShouldSetPropertiesInMap() {
		DateTime version = new DateTime(2012, 11, 5, 0, 0, 0);
		EmailTemplate template = new EmailTemplateBuilder().id(1L).name(APPLICATION_SUBMIT_CONFIRMATION).version(version.toDate()).build();
		expect(emailTemplateServiceMock.saveNewEmailTemplate(APPLICATION_SUBMIT_CONFIRMATION, "Some content", "Some subject")).andReturn(template);
		replay(emailTemplateServiceMock);

		Map<String, Object> result = controller.saveTemplate(APPLICATION_SUBMIT_CONFIRMATION, "Some content", "Some subject");

		verify(emailTemplateServiceMock);
		assertEquals("2012/11/5 - 00:00:00", result.get("version"));
		assertEquals(template.getId(), result.get("id"));
	}

	@Test
	public void activateTemplateShouldSetPropertiesInMap() throws Exception {
		EmailTemplate activeTemplate = new EmailTemplateBuilder().id(2L).name(APPLICATION_SUBMIT_CONFIRMATION).content("Some content").active(true).build();
		expect(emailTemplateServiceMock.getActiveEmailTemplate(APPLICATION_SUBMIT_CONFIRMATION)).andReturn(activeTemplate);
		emailTemplateServiceMock.activateEmailTemplate(APPLICATION_SUBMIT_CONFIRMATION, 1L);
		replay(emailTemplateServiceMock);

		Map<String, Object> result = controller.activateTemplate("APPLICATION_SUBMIT_CONFIRMATION", 1L, false, null, null);

		verify(emailTemplateServiceMock);
		assertEquals((Long) 2L, (Long) result.get("previousTemplateId"));
	}

	@Test
	public void activateTemplateShouldSaveANewTemplatecopyAndSetPropertiesInMap() throws Exception {
		EmailTemplate activeTemplate = new EmailTemplateBuilder().id(2L).name(APPLICATION_SUBMIT_CONFIRMATION).content("Some content").active(true).build();
		DateTime version = new DateTime(2012, 11, 5, 0, 0, 0);
		EmailTemplate newTemplate = new EmailTemplateBuilder().id(3L).name(APPLICATION_SUBMIT_CONFIRMATION).content("whatever").active(false)
		                .version(version.toDate()).build();
		expect(emailTemplateServiceMock.saveNewEmailTemplate(APPLICATION_SUBMIT_CONFIRMATION, "whatever", "new subject")).andReturn(newTemplate);
		expect(emailTemplateServiceMock.getActiveEmailTemplate(APPLICATION_SUBMIT_CONFIRMATION)).andReturn(activeTemplate);
		emailTemplateServiceMock.activateEmailTemplate(APPLICATION_SUBMIT_CONFIRMATION, 3L);
		replay(emailTemplateServiceMock);

		Map<String, Object> result = controller.activateTemplate("APPLICATION_SUBMIT_CONFIRMATION", 1L, true, "whatever", "new subject");

		verify(emailTemplateServiceMock);
		assertEquals((Long) 2L, (Long) result.get("previousTemplateId"));
		assertEquals((Long) 3L, (Long) result.get("id"));
		assertEquals("2012/11/5 - 00:00:00", result.get("version"));
	}

	@Test
	public void activateTemplateShouldSetErrorInMap() throws Exception {
		EmailTemplate activeTemplate = new EmailTemplateBuilder().id(2L).name(APPLICATION_SUBMIT_CONFIRMATION).content("Some content").active(true).build();
		expect(emailTemplateServiceMock.getActiveEmailTemplate(APPLICATION_SUBMIT_CONFIRMATION)).andReturn(activeTemplate);
		emailTemplateServiceMock.activateEmailTemplate(APPLICATION_SUBMIT_CONFIRMATION, 1L);
		expectLastCall().andThrow(new EmailTemplateException("test error"));
		replay(emailTemplateServiceMock);

		Map<String, Object> result = controller.activateTemplate("APPLICATION_SUBMIT_CONFIRMATION", 1L, false, null, null);

		verify(emailTemplateServiceMock);
		assertEquals("test error", result.get("error"));
	}

	@Test
	public void deleteTemplateShouldSetErrorInMap() throws Exception {
		DateTime version = new DateTime(2012, 11, 5, 0, 0, 0);
		EmailTemplate toDeleteTemplate = new EmailTemplateBuilder().id(1L).name(APPLICATION_SUBMIT_CONFIRMATION).content("Some content").active(false)
		                .version(version.toDate()).build();
		EmailTemplate activeTemplate = new EmailTemplateBuilder().id(2L).name(APPLICATION_SUBMIT_CONFIRMATION).content("Some content").active(true).build();
		expect(emailTemplateServiceMock.getEmailTemplate(1L)).andReturn(toDeleteTemplate);
		expect(emailTemplateServiceMock.getActiveEmailTemplate(APPLICATION_SUBMIT_CONFIRMATION)).andReturn(activeTemplate);
		emailTemplateServiceMock.deleteTemplateVersion(toDeleteTemplate);
		expectLastCall().andThrow(new EmailTemplateException("test error"));
		replay(emailTemplateServiceMock);

		Map<String, Object> result = controller.deleteTemplate(1L);

		verify(emailTemplateServiceMock);
		assertEquals("test error", result.get("error"));

	}

	@Test
	public void deleteTemplateShouldSetPropertiesInMap() throws Exception {
		DateTime version = new DateTime(2012, 11, 5, 0, 0, 0);
		EmailTemplate toDeleteTemplate = new EmailTemplateBuilder().id(1L).name(APPLICATION_SUBMIT_CONFIRMATION).content("Some content").active(false)
		                .version(version.toDate()).build();
		EmailTemplate activeTemplate = new EmailTemplateBuilder().id(2L).name(APPLICATION_SUBMIT_CONFIRMATION).content("Some active content").active(true)
		                .build();
		expect(emailTemplateServiceMock.getEmailTemplate(1L)).andReturn(toDeleteTemplate);
		expect(emailTemplateServiceMock.getActiveEmailTemplate(APPLICATION_SUBMIT_CONFIRMATION)).andReturn(activeTemplate);
		emailTemplateServiceMock.deleteTemplateVersion(toDeleteTemplate);
		replay(emailTemplateServiceMock);

		Map<String, Object> result = controller.deleteTemplate(1L);

		verify(emailTemplateServiceMock);
		assertEquals(2L, result.get("activeTemplateId"));
		assertEquals("Some active content", result.get("activeTemplateContent"));

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfNotSueradmin() {
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
		EasyMock.replay(userServiceMock);
		StageDuration validationDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).duration(1).unit(DurationUnitEnum.HOURS).build();
		StageDuration interviewDuration = new StageDurationBuilder().stage(ApplicationFormStatus.INTERVIEW).duration(3).unit(DurationUnitEnum.WEEKS).build();

		StageDurationDTO stageDurationDto = new StageDurationDTO();
		List<StageDuration> stageDurationList = Arrays.asList(validationDuration, interviewDuration);
		stageDurationDto.setStagesDuration(stageDurationList);

		Person registryUserOne = new PersonBuilder().id(1).build();
		Person registryUserTwo = new PersonBuilder().id(2).build();

		RegistryUserDTO registryUserDTO = new RegistryUserDTO();
		List<Person> registryContactList = Arrays.asList(registryUserOne, registryUserTwo);
		registryUserDTO.setRegistryUsers(registryContactList);

		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);

		EasyMock.replay(configurationServiceMock);

		controller.submit(stageDurationDto, registryUserDTO, reminderInterval);

		EasyMock.verify(configurationServiceMock);

	}

	@Test
	public void shouldGetThrottleAndSetProperties() {
		Throttle throttle = new ThrottleBuilder().id(12).enabled(true).batchSize(40).build();
		expect(throttleserviceMock.getThrottle()).andReturn(throttle);
		replay(throttleserviceMock);

		Map<String, Object> result = controller.getThrottle();

		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals(12, result.get("throttleId"));
		assertEquals(true, result.get("enabled"));
		assertEquals(40, result.get("batchSize"));
		verify(throttleserviceMock);
	}

	@Test
	public void shouldGetNoThrottleAndSetNoProperties() {
		expect(throttleserviceMock.getThrottle()).andReturn(null);
		replay(throttleserviceMock);

		Map<String, Object> result = controller.getThrottle();

		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(throttleserviceMock);
	}

	@Test
	public void shouldUpdateThrottle() {
		throttleserviceMock.updateThrottleWithNewValues(false, "15");
		EasyMock.expect(throttleserviceMock.userTurnedOnThrottle(false)).andReturn(false);
		replay(throttleserviceMock);

		Map<String, String> result = controller.updateThrottle(15, false, "15");

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void shouldNotUpdateThrottleAndReturnErrorMessage() {
		String errorMessage = "The throttling batch size must be a valid positive number";
		throttleserviceMock.updateThrottleWithNewValues(false, "15khg");
		EasyMock.expectLastCall().andThrow(new NumberFormatException());
		EasyMock.expect(throttleserviceMock.userTurnedOnThrottle(false)).andReturn(false);
		replay(throttleserviceMock);

		Map<String, String> result = controller.updateThrottle(15, false, "15khg");

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(errorMessage, result.get("error"));
	}

	@Test
	public void shouldTriggerSendingApplicationsToPorticoIfTheSwitchHasBeenSetToTrue() {
		throttleserviceMock.updateThrottleWithNewValues(true, "15");
		EasyMock.expect(throttleserviceMock.userTurnedOnThrottle(true)).andReturn(true);
		queueServiceMock.sendQueuedApprovedApplicationsToPortico();

		replay(throttleserviceMock, queueServiceMock);

		controller.updateThrottle(15, true, "15");

		verify(throttleserviceMock, queueServiceMock);
	}

	@Test
	public void shouldReturnCurrentUser() {
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		assertEquals(currentUserMock, controller.getUser());
	}

	@Test
	public void shouldEditScoringDefinition() {
		Program program = new ProgramBuilder().build();
		HttpServletResponse response = new MockHttpServletResponse();

		EasyMock.expect(programsServiceMock.getProgramByCode("any_code")).andReturn(program);
		programsServiceMock.applyScoringDefinition("any_code", ScoringStage.INTERVIEW, "content");

		EasyMock.replay(programsServiceMock);
		assertEquals(Collections.emptyMap(), controller.editScoringDefinition("any_code", ScoringStage.INTERVIEW, "content", response));
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
		Program program = new ProgramBuilder().build();
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
		Program program = new ProgramBuilder().build();
		program.getScoringDefinitions().put(ScoringStage.REVIEW, new ScoringDefinitionBuilder().stage(ScoringStage.REVIEW).content("Mleko").build());

		EasyMock.expect(programsServiceMock.getProgramByCode("any_code")).andReturn(program);

		EasyMock.replay(programsServiceMock);
		assertEquals("Mleko", controller.getScoringDefinition("any_code", ScoringStage.REVIEW));
		EasyMock.verify(programsServiceMock);
	}

	@Test
	public void shouldGetNotDefinedScoringDefinition() {
		Program program = new ProgramBuilder().build();
		EasyMock.expect(programsServiceMock.getProgramByCode("any_code")).andReturn(program);

		EasyMock.replay(programsServiceMock);
		assertNull(controller.getScoringDefinition("any_code", ScoringStage.REVIEW));
		EasyMock.verify(programsServiceMock);
	}

	@Before
	public void setUp() {
		stageDurationPropertyEditorMock = EasyMock.createMock(StageDurationPropertyEditor.class);

		registryPropertyEditorMock = EasyMock.createMock(PersonPropertyEditor.class);

		userServiceMock = EasyMock.createMock(UserService.class);

		emailTemplateServiceMock = EasyMock.createMock(EmailTemplateService.class);

		throttleserviceMock = EasyMock.createMock(ThrottleService.class);

		configurationServiceMock = EasyMock.createMock(ConfigurationService.class);

		queueServiceMock = EasyMock.createMock(PorticoQueueService.class);

		programsServiceMock = EasyMock.createMock(ProgramsService.class);

		scoringDefinitionParserMock = EasyMock.createMock(ScoringDefinitionParser.class);

		controller = new ConfigurationController(stageDurationPropertyEditorMock, registryPropertyEditorMock, userServiceMock, configurationServiceMock,
		                emailTemplateServiceMock, throttleserviceMock, queueServiceMock, programsServiceMock, scoringDefinitionParserMock);

		superAdmin = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
		                .role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).build();

		admin = new RegisteredUserBuilder().id(3).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
		                .role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).build();
	}
}
