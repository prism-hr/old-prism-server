package com.zuehlke.pgadmissions.controllers;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.APPROVAL_NOTIFICATION;
import static junit.framework.Assert.assertNotNull;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.builders.EmailTemplateBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.dto.RegistryUserDTO;
import com.zuehlke.pgadmissions.dto.StageDurationDTO;
import com.zuehlke.pgadmissions.exceptions.EmailTemplateException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.StageDurationPropertyEditor;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.services.UserService;

public class ConfigurationControllerTest {

	private ConfigurationController controller;

	private RegisteredUser superAdmin;
	private StageDurationPropertyEditor stageDurationPropertyEditorMock;

	private static final String CONFIGURATION_VIEW_NAME = "/private/staff/superAdmin/configuration";
	private static final String CONFIGURATION_SECTION_NAME = "/private/staff/superAdmin/configuration_section";

	private PersonPropertyEditor registryPropertyEditorMock;
	private UserService userServiceMock;
	
	private EmailTemplateService emailTemplateServiceMock;

	private ConfigurationService configurationServiceMock;

	private RegisteredUser admin;

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
		map.put(ApplicationFormStatus.VALIDATION,  validationDuration );
		StageDuration approvalDuration = new StageDurationBuilder().stage(ApplicationFormStatus.APPROVAL).build();
		map.put(ApplicationFormStatus.APPROVAL,  approvalDuration );
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
	public void shouldGetPossibleDurationUnits(){
		assertArrayEquals(DurationUnitEnum.values(), controller.getUnits());
	}
	@Test
	public void shouldExtractConfigurationObjectsAndSave() {
		expect(userServiceMock.getCurrentUser()).andReturn(superAdmin).anyTimes();
		EasyMock.replay(userServiceMock);
		StageDuration validationDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).duration(1).unit(DurationUnitEnum.HOURS)
				.build();
		StageDuration interviewDuration = new StageDurationBuilder().stage(ApplicationFormStatus.INTERVIEW).duration(3).unit(DurationUnitEnum.WEEKS)
				.build();

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
		
		String view = controller.submit(stageDurationDto, registryUserDTO , reminderInterval );
		
		verify(configurationServiceMock);
		assertEquals( "redirect:/configuration/config_section", view);

	}
	
	@Test
	public void getTemplateVersionShouldSetPropertiesInMap() {
		EmailTemplate template = new EmailTemplateBuilder().id(1L).name(APPROVAL_NOTIFICATION)
				.content("Some content").build();
		expect(emailTemplateServiceMock.getEmailTemplate(1L)).andReturn(template);
		replay(emailTemplateServiceMock);
		
		Map<String, Object> result = controller.getTemplateVersion(1L);
		
		verify(emailTemplateServiceMock);
		assertEquals(template.getContent(), result.get("content"));
		assertEquals(template.getVersion(), result.get("version"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getTemplateVersionsShouldSetPropertiesInMap() {
		EmailTemplate template = new EmailTemplateBuilder().id(1L).name(APPROVAL_NOTIFICATION)
				.content("Some content").active(true).build();
		Map<Long, String> returnedByMock = new HashMap<Long, String>();
		returnedByMock.put(1L, "default");
		returnedByMock.put(2L, "12/12/2012 - 12:12:12");
		returnedByMock.put(3L, "12/12/2013 - 12:12:12");
		expect(emailTemplateServiceMock.getActiveEmailTemplate(APPROVAL_NOTIFICATION)).andReturn(template);
		expect(emailTemplateServiceMock.getEmailTemplateVersions(APPROVAL_NOTIFICATION)).andReturn(returnedByMock);
		replay(emailTemplateServiceMock);
		
		Map<Object, Object> result = controller.getVersionsForTemplate("APPROVAL_NOTIFICATION");
		
		verify(emailTemplateServiceMock);
		assertEquals(template.getContent(), result.get("content"));
		assertEquals(template.getId(), result.get("activeVersion"));
		Map<Long, String> actual = (Map<Long, String>) result.get("versions");
		assertNotNull(actual);
		for (Map.Entry<Long, String> expectedEntry : returnedByMock.entrySet()) {
			assertEquals(expectedEntry.getValue(), actual.get(expectedEntry.getKey()));
		}
	}
	
	@Test
	public void saveTemplateShouldSetPropertiesInMap() {
		DateTime version = new DateTime(2012, 11, 5, 0, 0, 0);
		EmailTemplate template = new EmailTemplateBuilder().id(1L).name(APPROVAL_NOTIFICATION)
				.content("Some content").active(false).version(version.toDate()).build();
		expect(emailTemplateServiceMock.saveNewEmailTemplate(APPROVAL_NOTIFICATION, "Some content")).andReturn(template);
		replay(emailTemplateServiceMock);
		
		Map<String, Object> result = controller.saveTemplate(APPROVAL_NOTIFICATION, "Some content");
		
		verify(emailTemplateServiceMock);
		assertEquals("2012/11/5 - 00:00:00", result.get("version"));
		assertEquals(template.getId(), result.get("id"));
	}
	
	@Test
	public void activateTemplateShouldSetPropertiesInMap() throws Exception {
		emailTemplateServiceMock.activateEmailTemplate(APPROVAL_NOTIFICATION, 1L);
				replay(emailTemplateServiceMock);
		
		Map<String, String> result = controller.activateTemplate("APPROVAL_NOTIFICATION", 1L);
		
		verify(emailTemplateServiceMock);
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void activateTemplateShouldSetErrorInMap() throws Exception {
		emailTemplateServiceMock.activateEmailTemplate(APPROVAL_NOTIFICATION, 1L);
		expectLastCall().andThrow(new EmailTemplateException("test error"));
		replay(emailTemplateServiceMock);
		
		Map<String, String> result = controller.activateTemplate("APPROVAL_NOTIFICATION", 1L);
		
		verify(emailTemplateServiceMock);
		assertEquals("test error", result.get("error"));
	}
	
	@Test
	public void deleteTemplateShouldSetErrorInMap() throws Exception {
		DateTime version = new DateTime(2012, 11, 5, 0, 0, 0);
		EmailTemplate toDeleteTemplate = new EmailTemplateBuilder().id(1L).name(APPROVAL_NOTIFICATION)
				.content("Some content").active(false).version(version.toDate()).build();
		EmailTemplate activeTemplate = new EmailTemplateBuilder().id(2L).name(APPROVAL_NOTIFICATION)
				.content("Some content").active(true).build();
		expect(emailTemplateServiceMock.getEmailTemplate(1L)).andReturn(toDeleteTemplate);
		expect(emailTemplateServiceMock.getActiveEmailTemplate(APPROVAL_NOTIFICATION)).andReturn(activeTemplate);
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
		EmailTemplate toDeleteTemplate = new EmailTemplateBuilder().id(1L).name(APPROVAL_NOTIFICATION)
				.content("Some content").active(false).version(version.toDate()).build();
		EmailTemplate activeTemplate = new EmailTemplateBuilder().id(2L).name(APPROVAL_NOTIFICATION)
				.content("Some active content").active(true).build();
		expect(emailTemplateServiceMock.getEmailTemplate(1L)).andReturn(toDeleteTemplate);
		expect(emailTemplateServiceMock.getActiveEmailTemplate(APPROVAL_NOTIFICATION)).andReturn(activeTemplate);
		emailTemplateServiceMock.deleteTemplateVersion(toDeleteTemplate);
		replay(emailTemplateServiceMock);
		
		Map<String, Object> result = controller.deleteTemplate(1L);
		
		verify(emailTemplateServiceMock);
		assertEquals(2L, result.get("activeTemplateId"));
		assertEquals("Some active content", result.get("activeTemplateContent"));
	}

	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfNotSueradmin() {
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(admin).anyTimes();
		EasyMock.replay(userServiceMock);
		StageDuration validationDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).duration(1).unit(DurationUnitEnum.HOURS)
				.build();
		StageDuration interviewDuration = new StageDurationBuilder().stage(ApplicationFormStatus.INTERVIEW).duration(3).unit(DurationUnitEnum.WEEKS)
				.build();

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
		
		controller.submit(stageDurationDto, registryUserDTO , reminderInterval );
		
		EasyMock.verify(configurationServiceMock);

	}

	

	@Test
	public void shouldReturnCurrentUser() {
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		assertEquals(currentUserMock, controller.getUser());
	}

	@Before
	public void setUp() {

		stageDurationPropertyEditorMock = EasyMock.createMock(StageDurationPropertyEditor.class);

		registryPropertyEditorMock = EasyMock.createMock(PersonPropertyEditor.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		emailTemplateServiceMock = createMock(EmailTemplateService.class);
		configurationServiceMock = EasyMock.createMock(ConfigurationService.class);

		controller = new ConfigurationController(stageDurationPropertyEditorMock, registryPropertyEditorMock, userServiceMock, configurationServiceMock, emailTemplateServiceMock);

		superAdmin = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).build()).build();
		
		admin = new RegisteredUserBuilder().id(3).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).build();

	}
}
