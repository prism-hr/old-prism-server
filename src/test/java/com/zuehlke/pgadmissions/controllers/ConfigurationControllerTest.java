package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.dto.RegistryUserDTO;
import com.zuehlke.pgadmissions.dto.StageDurationDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.StageDurationPropertyEditor;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.UserService;

public class ConfigurationControllerTest {

	private ConfigurationController controller;

	private RegisteredUser superAdmin;
	private StageDurationPropertyEditor stageDurationPropertyEditorMock;

	private static final String CONFIGURATION_VIEW_NAME = "/private/staff/superAdmin/configuration";


	private PersonPropertyEditor registryPropertyEditorMock;
	private UserService userServiceMock;

	private ConfigurationService configurationServiceMock;

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundIfNotSuperAdmin() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).username("aa").email("aa@gmail.com").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();

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
		StageDuration validationDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).toStageDuration();
		map.put(ApplicationFormStatus.VALIDATION,  validationDuration );
		StageDuration approvalDuration = new StageDurationBuilder().stage(ApplicationFormStatus.APPROVAL).toStageDuration();
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
		Person personOne = new PersonBuilder().id(1).toPerson();
		Person personTwo = new PersonBuilder().id(4).toPerson();

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
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(superAdmin).anyTimes();
		EasyMock.replay(userServiceMock);
		StageDuration validationDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).duration(1).unit(DurationUnitEnum.HOURS)
				.toStageDuration();
		StageDuration interviewDuration = new StageDurationBuilder().stage(ApplicationFormStatus.INTERVIEW).duration(3).unit(DurationUnitEnum.WEEKS)
				.toStageDuration();

		StageDurationDTO stageDurationDto = new StageDurationDTO();
		List<StageDuration> stageDurationList = Arrays.asList(validationDuration, interviewDuration);
		stageDurationDto.setStagesDuration(stageDurationList);
	
		Person registryUserOne = new PersonBuilder().id(1).toPerson();
		Person registryUserTwo = new PersonBuilder().id(2).toPerson();

		RegistryUserDTO registryUserDTO = new RegistryUserDTO();
		List<Person> registryContactList = Arrays.asList(registryUserOne, registryUserTwo);
		registryUserDTO.setRegistryUsers(registryContactList);
		
		
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		
		configurationServiceMock.saveConfigurations(stageDurationList, registryContactList, reminderInterval);
		
		EasyMock.replay(configurationServiceMock);
		String view = controller.submit(stageDurationDto, registryUserDTO , reminderInterval );
		EasyMock.verify(configurationServiceMock);
		assertEquals(CONFIGURATION_VIEW_NAME, view);

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
		configurationServiceMock = EasyMock.createMock(ConfigurationService.class);

		controller = new ConfigurationController(stageDurationPropertyEditorMock, registryPropertyEditorMock, userServiceMock, configurationServiceMock);

		superAdmin = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();

	}
}
