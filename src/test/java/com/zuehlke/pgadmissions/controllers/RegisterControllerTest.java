package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.security.NoSuchAlgorithmException;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicantRecord;
import com.zuehlke.pgadmissions.domain.builders.ApplicantRecordBuilder;
import com.zuehlke.pgadmissions.dto.ApplicantRecordDTO;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.ApplicantRecordService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApplicantRecordValidator;

public class RegisterControllerTest {

	private RegisterController registerController;
	private ApplicantRecordService applicantRecordServiveMock;
	private UserService userServiceMock;
	private ApplicantRecord record;
	private ApplicantRecordValidator validatorMock;
	
	
	@Before
	public void setUp() {
		validatorMock = EasyMock.createMock(ApplicantRecordValidator.class);
		applicantRecordServiveMock = EasyMock.createMock(ApplicantRecordService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		registerController = new RegisterController(applicantRecordServiveMock, validatorMock, userServiceMock);
		record = new ApplicantRecordBuilder().id(1).toApplicantRecord();
	}
	
	@Test
	public void shouldReturnRegisterPage() throws NoSuchAlgorithmException{
		ModelAndView modelAndView = registerController.getRegisterPage();
		assertEquals("public/register/register_applicant", modelAndView.getViewName());
	}
	
	@Ignore
	@Test
	public void shouldSaveNewUser() throws NoSuchAlgorithmException{
		ApplicantRecord record  = new ApplicantRecordBuilder().id(1).email("email@email.com").firstname("firstamee").lastname("lastname").password("1234").toApplicantRecord();
		ApplicantRecordDTO recordDTO = new ApplicantRecordDTO();
		recordDTO.setFirstname("Mark");
		recordDTO.setLastname("Euston");
		recordDTO.setEmail("meuston@gmail.com");
		recordDTO.setPassword("1234");
		recordDTO.setConfirmPassword("1234");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "record");
		applicantRecordServiveMock.save(record);
		EasyMock.replay(applicantRecordServiveMock);
		ModelAndView modelAndView = registerController.getRegisterSubmitPage(recordDTO, mappingResult);
		EasyMock.verify(applicantRecordServiveMock);
		assertEquals("public/register/register_applicant", modelAndView.getViewName());
		assertEquals("email@email.com", ((RegisterPageModel)modelAndView.getModel().get("model")).getRecord().getEmail());
		System.out.println(((RegisterPageModel)modelAndView.getModel().get("model")).getRecord().getPassword());
	}
	
	@Ignore
	@Test
	public void shouldHashPassword() throws NoSuchAlgorithmException{
		ApplicantRecord record  = new ApplicantRecordBuilder().email("email@email.com").firstname("firstamee").lastname("lastname").password("1234").toApplicantRecord();
		ApplicantRecordDTO recordDTO = new ApplicantRecordDTO();
		recordDTO.setFirstname("Mark");
		recordDTO.setLastname("Euston");
		recordDTO.setEmail("meuston@gmail.com");
		recordDTO.setPassword("1234");
		recordDTO.setConfirmPassword("1234");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "record");
		applicantRecordServiveMock.save(record);
		ModelAndView modelAndView = registerController.getRegisterSubmitPage(recordDTO, mappingResult);
		assertEquals("public/register/register_applicant", modelAndView.getViewName());
		assertFalse(((RegisterPageModel)modelAndView.getModel().get("model")).getRecord().getPassword().equals("1234"));
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
