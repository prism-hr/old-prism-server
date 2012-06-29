package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.dao.ReminderIntervalDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
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
import com.zuehlke.pgadmissions.services.PersonService;
import com.zuehlke.pgadmissions.services.UserService;

public class ConfigurationControllerTest {

	private ConfigurationController controller;
	private StageDurationDAO stateDurationDAOMock;
	private RegisteredUser superAdmin;
	private StageDurationPropertyEditor stageDurationPropertyEditorMock;

	private static final String CONFIGURATION_VIEW_NAME = "/private/staff/superAdmin/configuration";
	private ReminderIntervalDAO reminderIntervalDAOMock;
	private PersonService regisrtyUserServiceMock;
	private PersonPropertyEditor registryPropertyEditorMock;
	private UserService userServiceMock;
	

	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundIfNotSuperAdmin() {
		ModelMap modelMap = new ModelMap();
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).username("aa").email("aa@gmail.com").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();

		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant).anyTimes();
		EasyMock.replay(userServiceMock);
		String view = controller.getAssignStagesDurationStage(modelMap);
		assertEquals(CONFIGURATION_VIEW_NAME, view);
	}
	
	@Test
	public void shouldGetAssignDurationOfStagesPageIfSuperAdmin() {
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(superAdmin).anyTimes();
		EasyMock.replay(userServiceMock);
		ModelMap modelMap = new ModelMap();
		String view = controller.getAssignStagesDurationStage(modelMap);
		assertEquals(CONFIGURATION_VIEW_NAME, view);
	}
	
	@Test
	public void shouldSaveStageDurations() {
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(superAdmin).anyTimes();
		EasyMock.replay(userServiceMock);
		StageDuration validationDuration = new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).duration(1).unit(DurationUnitEnum.HOURS).toStageDuration();
		StageDuration interviewDuration = new StageDurationBuilder().stage(ApplicationFormStatus.INTERVIEW).duration(3).unit(DurationUnitEnum.WEEKS).toStageDuration();
		StageDuration approvalDuration = new StageDurationBuilder().stage(ApplicationFormStatus.APPROVAL).duration(2).unit(DurationUnitEnum.DAYS).toStageDuration();
		StageDuration reviewDuration = new StageDurationBuilder().stage(ApplicationFormStatus.REVIEW).duration(1).unit(DurationUnitEnum.HOURS).toStageDuration();
		StageDurationDTO stageDurationDto = new StageDurationDTO();
		stageDurationDto.setStagesDuration(Arrays.asList(validationDuration, interviewDuration, approvalDuration, reviewDuration));
		stateDurationDAOMock.save(validationDuration);
		stateDurationDAOMock.save(interviewDuration);
		stateDurationDAOMock.save(approvalDuration);
		stateDurationDAOMock.save(reviewDuration);
		EasyMock.replay(stateDurationDAOMock);
		controller.submitStagesDurations(stageDurationDto, new ModelMap());
		EasyMock.verify(stateDurationDAOMock);
		
	}
	
	
	@Test
	public void shouldSaveRegistryUsers() {
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(superAdmin).anyTimes();
		EasyMock.replay(userServiceMock);
		Person registryUserOne = new PersonBuilder().id(1).toPerson();
		Person registryUserTwo = new PersonBuilder().id(2).toPerson();
		Person registryUserThree = new PersonBuilder().id(3).toPerson();
		RegistryUserDTO registryUserDTO = new RegistryUserDTO();
		registryUserDTO.setRegistryUsers(Arrays.asList(registryUserOne, registryUserTwo, registryUserThree));
		EasyMock.expect(regisrtyUserServiceMock.getAllRegistryUsers()).andReturn(Arrays.asList(registryUserOne, registryUserTwo, registryUserThree));
		regisrtyUserServiceMock.save(registryUserOne);
		regisrtyUserServiceMock.save(registryUserTwo);
		regisrtyUserServiceMock.save(registryUserThree);
		EasyMock.replay(regisrtyUserServiceMock);
		controller.submitregistryUsers(registryUserDTO, new ModelMap());
		EasyMock.verify(regisrtyUserServiceMock);
		
	}
	
	@Test
	public void shouldSaveReminderInterval() {
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(superAdmin).anyTimes();
		EasyMock.replay(userServiceMock);
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		reminderIntervalDAOMock.save(reminderInterval);
		EasyMock.replay(reminderIntervalDAOMock);
		controller.submitReminderInterval(reminderInterval, new ModelMap());
		EasyMock.verify(reminderIntervalDAOMock);
		
	}
	
	@Test
	public void shouldReturnCurrentUser() {
		RegisteredUser currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		assertEquals(currentUserMock, controller.getUser());
	}
	
	@Before
	public void setUp(){
		stateDurationDAOMock = EasyMock.createMock(StageDurationDAO.class);
		stageDurationPropertyEditorMock = EasyMock.createMock(StageDurationPropertyEditor.class);
		reminderIntervalDAOMock = EasyMock.createMock(ReminderIntervalDAO.class);
		regisrtyUserServiceMock = EasyMock.createMock(PersonService.class);
		registryPropertyEditorMock = EasyMock.createMock(PersonPropertyEditor.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		controller = new ConfigurationController(stateDurationDAOMock, stageDurationPropertyEditorMock, reminderIntervalDAOMock, regisrtyUserServiceMock, registryPropertyEditorMock,userServiceMock);

		
		superAdmin = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();

	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
