package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.dao.ReminderIntervalDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RegistryUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegistryUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.dto.RegistryUserDTO;
import com.zuehlke.pgadmissions.dto.StageDurationDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.RegistryUserPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.StageDurationPropertyEditor;
import com.zuehlke.pgadmissions.services.RegistryUserService;

public class AssignStagesDurationControllerTest {

	private AssignStagesDurationController controller;
	private StageDurationDAO stateDurationDAOMock;
	private RegisteredUser superAdmin;
	private StageDurationPropertyEditor stageDurationPropertyEditorMock;
	private UsernamePasswordAuthenticationToken authenticationToken;
	private static final String CHANGE_STATES_DURATION_VIEW_NAME = "/private/staff/superAdmin/assign_stages_duration";
	private ReminderIntervalDAO reminderIntervalDAOMock;
	private RegistryUserService regisrtyUserServiceMock;
	private RegistryUserPropertyEditor registryPropertyEditorMock;
	

	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundIfNotSuperAdmin() {
		ModelMap modelMap = new ModelMap();
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).username("aa").email("aa@gmail.com").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(applicant);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		String view = controller.getAssignStagesDurationStage(modelMap);
		assertEquals(CHANGE_STATES_DURATION_VIEW_NAME, view);
	}
	
	@Test
	public void shouldGetAssignDurationOfStagesPageIfSuperAdmin() {
		ModelMap modelMap = new ModelMap();
		String view = controller.getAssignStagesDurationStage(modelMap);
		assertEquals(CHANGE_STATES_DURATION_VIEW_NAME, view);
	}
	
	@Test
	public void shouldSaveStageDurations() {
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
		RegistryUser registryUserOne = new RegistryUserBuilder().id(1).toRegistryUser();
		RegistryUser registryUserTwo = new RegistryUserBuilder().id(2).toRegistryUser();
		RegistryUser registryUserThree = new RegistryUserBuilder().id(3).toRegistryUser();
		RegistryUserDTO registryUserDTO = new RegistryUserDTO();
		registryUserDTO.setRegistryUsers(Arrays.asList(registryUserOne, registryUserTwo, registryUserThree));
		regisrtyUserServiceMock.save(registryUserOne);
		regisrtyUserServiceMock.save(registryUserTwo);
		regisrtyUserServiceMock.save(registryUserThree);
		EasyMock.replay(regisrtyUserServiceMock);
		controller.submitregistryUsers(registryUserDTO, new ModelMap());
		EasyMock.verify(regisrtyUserServiceMock);
		
	}
	
	@Test
	public void shouldSaveReminderInterval() {
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		reminderIntervalDAOMock.save(reminderInterval);
		EasyMock.replay(reminderIntervalDAOMock);
		controller.submitReminderInterval(reminderInterval, new ModelMap());
		EasyMock.verify(reminderIntervalDAOMock);
		
	}
	
	
	@Before
	public void setUp(){
		
		stateDurationDAOMock = EasyMock.createMock(StageDurationDAO.class);
		stageDurationPropertyEditorMock = EasyMock.createMock(StageDurationPropertyEditor.class);
		reminderIntervalDAOMock = EasyMock.createMock(ReminderIntervalDAO.class);
		regisrtyUserServiceMock = EasyMock.createMock(RegistryUserService.class);
		registryPropertyEditorMock = EasyMock.createMock(RegistryUserPropertyEditor.class);
		controller = new AssignStagesDurationController(stateDurationDAOMock, stageDurationPropertyEditorMock, reminderIntervalDAOMock, regisrtyUserServiceMock, registryPropertyEditorMock);

		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		superAdmin = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham")
				.role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();
		authenticationToken.setDetails(superAdmin);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
