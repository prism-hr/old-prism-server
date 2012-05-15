package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;

public class AssignStagesDurationControllerTest {

	private AssignStagesDurationController controller;
	private StageDurationDAO stateDurationDAOMock;
	private RegisteredUser superAdmin;
	private UsernamePasswordAuthenticationToken authenticationToken;
	private static final String CHANGE_STATES_DURATION_VIEW_NAME = "/private/staff/superAdmin/assign_stages_duration";
	

	
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
	
	@Before
	public void setUp(){
		
		stateDurationDAOMock = EasyMock.createMock(StageDurationDAO.class);
		controller = new AssignStagesDurationController(stateDurationDAOMock);

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
