package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;

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
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.dto.ApplicationFormDetails;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.SubmitApplicationService;

public class SubmitApplicationFormControllerTest {

	private SubmitApplicationFormController applicationController;
	private ApplicationForm applicationForm;
	private ApplicationsService applicationsServiceMock;
	private UserPropertyEditor userPropertyEditorMock;
	private RegisteredUser student;
	private CountryService countryServiceMock;
	private LanguageService languageServiceMock;
	private SubmitApplicationService referencesServiceMock;

	@Test
	@Ignore
	public void shouldLoadApplicationFormByIdAndChangeSubmissionStatusToSubmittedAndSetTheCurrentTimeStampToSubmitDate() {
		ProgrammeDetail programmeDetail = EasyMock.createMock(ProgrammeDetail.class);
		EasyMock.expect(programmeDetail.getSupervisors()).andReturn(new ArrayList<Supervisor>());
		EasyMock.expect(programmeDetail.getProgrammeName()).andReturn("test");
		EasyMock.expect(programmeDetail.getProjectName()).andReturn("test");
		EasyMock.expect(programmeDetail.getReferrer()).andReturn(Referrer.OPTION_1);
		EasyMock.expect(programmeDetail.getStartDate()).andReturn(new Date());
		EasyMock.expect(programmeDetail.getStudyOption()).andReturn(StudyOption.FULL_TIME);
		EasyMock.replay(programmeDetail);
		
		PersonalDetail personalDetail = EasyMock.createMock(PersonalDetail.class);
		EasyMock.replay(personalDetail);
		
		ApplicationForm appForm = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(appForm.getApplicant()).andReturn(student);
		EasyMock.expect(appForm.getApplicant()).andReturn(student);
		EasyMock.expect(appForm.isSubmitted()).andReturn(false);
		EasyMock.expect(appForm.getAddresses()).andReturn(new ArrayList<Address>());
		EasyMock.expect(appForm.getAddresses()).andReturn(new ArrayList<Address>());
		EasyMock.expect(appForm.getReferees()).andReturn(new ArrayList<Referee>());
		EasyMock.expect(appForm.getProgrammeDetails()).andReturn(programmeDetail);
		EasyMock.expect(appForm.getPersonalDetails()).andReturn(personalDetail);
		
		EasyMock.expect(appForm.getProgrammeDetails()).andReturn(programmeDetail);
		EasyMock.expect(appForm.getSubmissionStatus()).andReturn(SubmissionStatus.UNSUBMITTED);
		EasyMock.replay(appForm);
		
		EasyMock.expect(applicationsServiceMock.getApplicationById(23)).andReturn(appForm);
		
		ApplicationFormDetails details = EasyMock.createMock(ApplicationFormDetails.class);
		details.setNumberOfAddresses(0);
		details.setNumberOfReferees(0);
		details.setNumberOfContactAddresses(0);
		details.setPersonalDetails(personalDetail);
		details.setProgrammeDetails(programmeDetail);
		EasyMock.expect(details.getNumberOfAddresses()).andReturn(1);
		EasyMock.expect(details.getNumberOfContactAddresses()).andReturn(1);
		EasyMock.expect(details.getNumberOfContactAddresses()).andReturn(1);
		EasyMock.expect(details.getNumberOfReferees()).andReturn(2);
		EasyMock.expect(details.getProgrammeDetails()).andReturn(programmeDetail);
		EasyMock.expect(details.getProgrammeDetails()).andReturn(programmeDetail);
		EasyMock.expect(details.getProgrammeDetails()).andReturn(programmeDetail);
		EasyMock.replay(details);
		
		BindingResult result = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(result.hasErrors()).andReturn(false);
		EasyMock.replay(result);
		
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = applicationController.submitApplication(details, 23, result);
		Assert.assertEquals("redirect:/applications?submissionSuccess=true", modelAndView.getViewName());
		Assert.assertNotNull(appForm.getSubmittedDate());
	}
	
	@Test
	public void shouldReLoadApplicationFormWhenIncomplete() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		form.setApplicant(student);
		applicationsServiceMock.save(form);
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		ApplicationFormDetails applDetails = new ApplicationFormDetails();
		BindingResult mappingResult = new BeanPropertyBindingResult(applDetails, "applicationFormDetails", true, 100);
		assertEquals(SubmissionStatus.UNSUBMITTED, form.getSubmissionStatus());
		 ModelAndView modelAndView = applicationController.submitApplication(applDetails, 2, mappingResult);
		assertEquals("private/pgStudents/form/main_application_page",modelAndView.getViewName());
	
		ApplicationPageModel model  = (ApplicationPageModel) modelAndView.getModel().get("model");
		assertNotNull(model.getQualification());
		assertNotNull(model.getAddress());
		assertNotNull(model.getFunding());
		assertNotNull(model.getEmploymentPosition());
		assertNotNull(model.getReferrers());
		
		assertEquals(Gender.values().length, model.getGenders().size());
		assertTrue(model.getGenders().containsAll(Arrays.asList(Gender.values())));
		assertEquals(StudyOption.values().length, model.getStudyOptions().size());
		assertTrue(model.getStudyOptions().containsAll(Arrays.asList(StudyOption.values())));
		assertEquals(Referrer.values().length, model.getReferrers().size());
		assertTrue(model.getReferrers().containsAll(Arrays.asList(Referrer.values())));
		assertEquals(PhoneType.values().length, model.getPhoneTypes().size());
		assertTrue(model.getPhoneTypes().containsAll(Arrays.asList(PhoneType.values())));
		
		assertEquals(DocumentType.values().length, model.getDocumentTypes().size());
		assertTrue(model.getDocumentTypes().containsAll(Arrays.asList(DocumentType.values())));
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
		languageServiceMock = EasyMock.createMock(LanguageService.class);
		referencesServiceMock = EasyMock.createMock(SubmitApplicationService.class);
		applicationController = new SubmitApplicationFormController(applicationsServiceMock, userPropertyEditorMock, 
				countryServiceMock, languageServiceMock, referencesServiceMock) {
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

