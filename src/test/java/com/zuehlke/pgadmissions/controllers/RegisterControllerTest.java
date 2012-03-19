
package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.validation.constraints.AssertTrue;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicantRecord;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicantRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ApplicantRecordDTO;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.ApplicantRecordService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApplicantRecordValidator;

public class RegisterControllerTest {

	private RegisterController registerController;
	private ApplicantRecordService applicantRecordServiveMock;
	private UserService userServiceMock;
	private ApplicantRecordValidator validatorMock;
	
	
	@Before
	public void setUp() {
		validatorMock = EasyMock.createMock(ApplicantRecordValidator.class);
		applicantRecordServiveMock = EasyMock.createMock(ApplicantRecordService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		registerController = new RegisterController(applicantRecordServiveMock, validatorMock, userServiceMock);
	}
	
	@Test
	public void shouldReturnRegisterPage() throws NoSuchAlgorithmException{
		ModelAndView modelAndView = registerController.getRegisterPage();
		assertEquals("public/register/register_applicant", modelAndView.getViewName());
	}
	
	@Ignore
	@Test
	public void shouldSaveNewUser() throws NoSuchAlgorithmException{
		RegisteredUser user = new RegisteredUserBuilder().email("meuston@gmail.com").firstName("Mark").lastName("Euston").password("1234").accountNonExpired(true).accountNonLocked(true).enabled(false).credentialsNonExpired(true).toUser();
		ApplicantRecordDTO recordDTO = new ApplicantRecordDTO();
		recordDTO.setFirstname("Mark");
		recordDTO.setLastname("Euston");
		recordDTO.setEmail("meuston@gmail.com");
		recordDTO.setPassword("1234");
		recordDTO.setConfirmPassword("1234");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(recordDTO, "record");
		Role role = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		EasyMock.expect(userServiceMock.getRoleById(2)).andReturn(role);
		userServiceMock.save(user);
		EasyMock.replay(userServiceMock);
		ModelAndView modelAndView = registerController.getRegisterSubmitPage(recordDTO, mappingResult);
		EasyMock.verify(userServiceMock);
		assertEquals("public/register/register_applicant", modelAndView.getViewName());
		assertEquals("email@email.com", ((RegisterPageModel)modelAndView.getModel().get("model")).getUser().getEmail());
		System.out.println(((RegisterPageModel)modelAndView.getModel().get("model")).getRecord().getPassword());
	}

	@Ignore
	@Test
	public void shouldHashPassword() throws NoSuchAlgorithmException{
		RegisteredUser user = new RegisteredUserBuilder().email("email@email.com").firstName("firstname").lastName("lastname").password("1234").toUser();
		ApplicantRecordDTO recordDTO = new ApplicantRecordDTO();
		Role role = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		EasyMock.expect(userServiceMock.getRoleById(2)).andReturn(role);
		recordDTO.setFirstname("Mark");
		recordDTO.setLastname("Euston");
		recordDTO.setEmail("meuston@gmail.com");
		recordDTO.setPassword("1234");
		recordDTO.setConfirmPassword("1234");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(recordDTO, "record");
		userServiceMock.save(user);
		ModelAndView modelAndView = registerController.getRegisterSubmitPage(recordDTO, mappingResult);
		assertEquals("public/register/register_applicant", modelAndView.getViewName());
		assertFalse(((RegisterPageModel)modelAndView.getModel().get("model")).getUser().getPassword().equals("1234"));
	}
	
	@Test
	public void shouldGenereteRandomCode(){
		SecureRandom random = new SecureRandom();
		System.out.println(new BigInteger(80, random).toString(32));
	}
	
	@Test
	public void shouldActivateAccount(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).activationCode("ul5oaij68186jbcg").enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(userServiceMock.getUserByUsername(user.getUsername())).andReturn(user);
		String activationCode = "ul5oaij68186jbcg";
		userServiceMock.save(user);
		EasyMock.replay(userServiceMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit(user, activationCode);
		EasyMock.verify(userServiceMock);
		assertTrue(((RegisterPageModel)modelAndView.getModel().get("model")).getUser().isEnabled());
	}
	
	@Test
	public void shouldNotActivateAccount(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).activationCode("ul5oaij68186jbcg").enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(userServiceMock.getUserByUsername(user.getUsername())).andReturn(user);
		String activationCode = "differentactivationcode";
		EasyMock.replay(userServiceMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit(user, activationCode);
		assertFalse(((RegisterPageModel)modelAndView.getModel().get("model")).getUser().isEnabled());
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
