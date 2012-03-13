package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.dao.PersonalDetailDAO;
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.AddressStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.dto.ApplicationFormDetails;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;

public class SubmitApplicationFormControllerTest {

	private SubmitApplicationFormController applicationController;
	private ApplicationForm applicationForm;
	private ApplicationsService applicationsServiceMock;
	private UserPropertyEditor userPropertyEditorMock;
	private RegisteredUser student;
	private CountryService countryServiceMock;
	private PersonalDetailDAO personalDetailDAOMock;
	private ProgrammeDetailDAO programmeDetailsDAOMock;


	@Test
	@Ignore
	public void shouldLoadApplicationFormByIdAndChangeSubmissionStatusToSubmitted() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		form.setApplicant(student);
		com.zuehlke.pgadmissions.domain.Address address = new com.zuehlke.pgadmissions.domain.Address();
		address.setApplication(form);
		address.setCountry("test");
		address.setLocation("test");
		address.setStartDate(new Date());
		address.setEndDate(new Date());
		address.setPostCode("test");
		address.setPurpose("scholarship");
		address.setContactAddress(AddressStatus.YES);
		
		form.getAddresses().add(address);
		applicationsServiceMock.save(form);
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		ApplicationFormDetails applDetails = new ApplicationFormDetails();
		BindingResult mappingResult = new BeanPropertyBindingResult(applDetails, "applicationFormDetails", true, 100);
		assertEquals(SubmissionStatus.UNSUBMITTED, form.getSubmissionStatus());
		PersonalDetail personalDetail = new PersonalDetail();
		personalDetail.setId(2);
		personalDetail.setApplication(form);
		Country country = new Country();
		country.setId(1); country.setName("ENGLAND"); country.setCode("EN");
		personalDetail.setCountry(country);
		personalDetail.setDateOfBirth(new Date());
		personalDetail.setGender(Gender.FEMALE);
		personalDetail.setFirstName("test");
		personalDetail.setLastName("test");
		personalDetail.setEmail("email@test.com");
		personalDetail.setResidenceCountry(country);
		personalDetail.setResidenceStatus(ResidenceStatus.EXCEPTIONAL_LEAVE_TO_REMAIN);
		//EasyMock.expect(personalDetailDAOMock.getPersonalDetailWithApplication(form)).andReturn(personalDetail);
		EasyMock.replay(applicationsServiceMock, personalDetailDAOMock);
		assertEquals("redirect:/applications?submissionSuccess=true", applicationController.submitApplication(applDetails, 2, mappingResult).getViewName());
		assertEquals(SubmissionStatus.SUBMITTED, form.getSubmissionStatus());
		EasyMock.verify(applicationsServiceMock);
	}
	
	@Test
	@Ignore
	public void shouldReLoadApplicationFormWhenIncomplete() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		form.setApplicant(student);
		applicationsServiceMock.save(form);
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		//EasyMock.expect(personalDetailDAOMock.getPersonalDetailWithApplication(form)).andReturn(new PersonalDetail());
		EasyMock.replay(applicationsServiceMock, personalDetailDAOMock);
		ApplicationFormDetails applDetails = new ApplicationFormDetails();
		BindingResult mappingResult = new BeanPropertyBindingResult(applDetails, "applicationFormDetails", true, 100);
		assertEquals(SubmissionStatus.UNSUBMITTED, form.getSubmissionStatus());
		assertEquals("private/pgStudents/form/main_application_page", applicationController.submitApplication(applDetails, 2, mappingResult).getViewName());
		fail("Not testing for model fields");
	}

	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfSubmitterNotFormAcpplicant() {
		Integer id = 2;
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(form);		
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);

		form.setApplicant(student);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		RegisteredUser otherApplicant = new RegisteredUserBuilder().id(6).username("fred").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole())
		.toUser();
		authenticationToken.setDetails(otherApplicant);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);		

		applicationController.submitApplication(new ApplicationFormDetails(), 2, null);
	}


	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfSubmittedApplicationFormDoesNotExist() {
		Integer id = 2;		
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(null);	
		EasyMock.replay(applicationsServiceMock);
		applicationController.submitApplication(new ApplicationFormDetails(), 2, null);
	}


	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowSubmitExceptionIfApplicationIsAlreadySubmitted() {
		Integer id = 2;
		ApplicationForm form = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(id)).andReturn(form);		
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);

		form.setApplicant(student);
		
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(null, "");
		applicationController.submitApplication(new ApplicationFormDetails(), 2, mappingResult);
	}

	@Before
	public void setUp() {

		applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userPropertyEditorMock = EasyMock.createMock(UserPropertyEditor.class);
		countryServiceMock = EasyMock.createMock(CountryService.class);
		personalDetailDAOMock = EasyMock.createMock(PersonalDetailDAO.class);
		programmeDetailsDAOMock = EasyMock.createMock(ProgrammeDetailDAO.class);

		applicationController = new SubmitApplicationFormController(applicationsServiceMock, userPropertyEditorMock, 
				countryServiceMock, personalDetailDAOMock, programmeDetailsDAOMock) {
			ApplicationForm newApplicationForm() {
				return applicationForm;
			}
		};

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark").lastName("ham").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(student);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}

