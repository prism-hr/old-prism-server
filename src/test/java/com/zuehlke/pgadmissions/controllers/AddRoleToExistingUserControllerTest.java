package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageProficiency;
import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.RolePropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

public class AddRoleToExistingUserControllerTest {

	private AddRoleToExistingUserController controller;
	private UserService userServiceMock;
	private RegisteredUser currentUser;
	private ProgramsService programsServiceMock;
	private UserDAO userDAOMock;
	private RolePropertyEditor rolePropertyEditorMock;
	
	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.registerCustomEditor(Role.class, rolePropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}
	
	
	@Test
	public void shouldUserRoleAndProgramRole(){
		Program program = new ProgramBuilder().id(1).toProgram();
		Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		RegisteredUser user = new RegisteredUserBuilder().role(reviewerRole).id(1).toUser();
		Role approver = new RoleBuilder().authorityEnum(Authority.APPROVER).toRole();
		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(1);
		userDTO.setRoles(Arrays.asList(approver));
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(programsServiceMock.getProgramById(1)).andReturn(program);
		userServiceMock.save(user);
		programsServiceMock.save(program);
		EasyMock.replay(userServiceMock, programsServiceMock);
		ModelAndView modelAndView = controller.addRoleToExistingUser(program.getId(), userDTO);
		EasyMock.verify(userServiceMock, programsServiceMock);
	}
	
	@Test
	public void shouldSaveProgramRoleButNotUserRole(){
		Program program = new ProgramBuilder().id(1).toProgram();
		Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		RegisteredUser user = new RegisteredUserBuilder().role(reviewerRole).id(1).toUser();
		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(1);
		userDTO.setRoles(Arrays.asList(reviewerRole));
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(programsServiceMock.getProgramById(1)).andReturn(program);
		programsServiceMock.save(program);
		EasyMock.replay(userServiceMock, programsServiceMock);
		ModelAndView modelAndView = controller.addRoleToExistingUser(program.getId(), userDTO);
		EasyMock.verify(programsServiceMock);
	}
	
	@Test
	public void shouldSaveReviewerRoleToAnAdminOfTheProgram(){
		Program program = new ProgramBuilder().id(1).toProgram();
		Role adminToProgram = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole();
		RegisteredUser user = new RegisteredUserBuilder().role(adminToProgram).id(1).toUser();
		program.setAdministrators(Arrays.asList(user));
		Role reviewer = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(1);
		userDTO.setRoles(Arrays.asList(reviewer));
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(programsServiceMock.getProgramById(1)).andReturn(program);
		userServiceMock.save(user);
		programsServiceMock.save(program);
		EasyMock.replay(userServiceMock, programsServiceMock);
		controller.addRoleToExistingUser(program.getId(), userDTO);
		EasyMock.verify(programsServiceMock,userServiceMock);
	}
	
	@Test
	public void shouldNotSaveUserAndProgram(){
		Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		RegisteredUser user = new RegisteredUserBuilder().role(reviewerRole).id(1).toUser();
		Program program = new ProgramBuilder().reviewers(user).id(1).toProgram();
		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(1);
		userDTO.setRoles(Arrays.asList(reviewerRole));
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(programsServiceMock.getProgramById(1)).andReturn(program);
		EasyMock.replay(userServiceMock, programsServiceMock);
		ModelAndView modelAndView = controller.addRoleToExistingUser(program.getId(), userDTO);
		EasyMock.verify(programsServiceMock);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldTrowExceptionIfUserNotFound(){
		Role reviewerRole = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		RegisteredUser user = new RegisteredUserBuilder().role(reviewerRole).toUser();
		Program program = new ProgramBuilder().toProgram();
		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(17);
		userDTO.setRoles(Arrays.asList(reviewerRole));
		ModelAndView modelAndView = controller.addRoleToExistingUser(program.getId(), userDTO);
	}
	
	
	@Before
	public void setup() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		programsServiceMock = EasyMock.createMock(ProgramsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		controller = new AddRoleToExistingUserController(userServiceMock, programsServiceMock, rolePropertyEditorMock);

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}
	
	
}
