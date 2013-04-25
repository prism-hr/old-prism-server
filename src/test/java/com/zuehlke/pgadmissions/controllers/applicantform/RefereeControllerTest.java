package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.validators.RefereeValidator;

public class RefereeControllerTest {

	
	private RegisteredUser currentUser;
	private RefereeService refereeServiceMock;
	private RefereeController controller;	
	private CountryService countriesServiceMock;
	private ApplicationsService applicationsServiceMock;	
	private CountryPropertyEditor countryPropertyEditor;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
	private RefereeValidator refereeValidatorMock;
	private EncryptionUtils encryptionUtilsMock;
	private EncryptionHelper encryptionHelperMock;
	private UserService userServiceMock;

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
		Referee referee = new RefereeBuilder().id(1)
				.application(new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).id(5).build()).build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.replay(refereeServiceMock, errors);
		controller.editReferee(referee, errors);
		EasyMock.verify(refereeServiceMock);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnSubmitIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.editReferee(null, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnGetIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.getRefereeView();
	}

	@Test
	public void shouldReturnRefereeView() {
		assertEquals("/private/pgStudents/form/components/references_details", controller.getRefereeView());
	}

	@Test
	public void shouldReturnAllCountries() {
		List<Country> countryList = Arrays.asList(new CountryBuilder().id(1).build(), new CountryBuilder().id(2).build());
		EasyMock.expect(countriesServiceMock.getAllCountries()).andReturn(countryList);
		EasyMock.replay(countriesServiceMock);
		List<Country> allCountries = controller.getAllCountries();
		assertSame(countryList, allCountries);
	}

	@Test
	public void shouldReturnApplicationForm() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser);
		ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
		assertEquals(applicationForm, returnedApplicationForm);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("1");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserCAnnotSeeApplFormOnGet() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(applicationsServiceMock, currentUser);
		controller.getApplicationForm("1");
	}

	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(refereeValidatorMock);
		binderMock.registerCustomEditor(Country.class, countryPropertyEditor);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldGetRefereeFromServiceIfIdProvided() {
		Referee referee = new RefereeBuilder().id(1).build();

		EasyMock.expect(encryptionHelperMock.decryptToInteger("enc")).andReturn(1);
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(referee);
		EasyMock.replay(refereeServiceMock, encryptionHelperMock);
		
		Referee returnedReferee = controller.getReferee("enc");
		EasyMock.verify(refereeServiceMock, encryptionHelperMock);
		
		assertEquals(referee, returnedReferee);
	}

	@Test
	public void shouldReturnNewRefereeIfIdIsBlank() {
		Referee returnedReferee = controller.getReferee("");
		assertNull(returnedReferee.getId());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfRefereeDoesNotExist() {
		EasyMock.expect(encryptionHelperMock.decryptToInteger("encrypted")).andReturn(1);
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(null);
		EasyMock.replay(refereeServiceMock, encryptionHelperMock);
		
		controller.getReferee("encrypted");
	}

	@Test
	public void shouldReturnMessage() {
		assertEquals("bob", controller.getMessage("bob"));
	}

	@Test
	public void shouldSaveRefereeAndRedirectIfNoErrors() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("ABC").id(5).build();
		Referee referee = new RefereeBuilder().id(1).application(applicationForm).build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		refereeServiceMock.save(referee);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(refereeServiceMock,applicationsServiceMock,  errors);
		String view = controller.editReferee(referee, errors);
		EasyMock.verify(refereeServiceMock, applicationsServiceMock);
		assertEquals( "redirect:/update/getReferee?applicationId=ABC", view);
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(),Calendar.DATE), DateUtils.truncate(applicationForm.getLastUpdated(), Calendar.DATE));
	}
	
	@Test
	public void shouldSaveRefereeAndSendEmailIfApplicationInApprovalStageAndIfNoErrors() {
		ApplicationForm application = new ApplicationFormBuilder().id(5).applicationNumber("ABC").status(ApplicationFormStatus.APPROVAL).build();
		Referee referee = new RefereeBuilder().id(1).application(application).build();
		application.setReferees(Arrays.asList(referee));
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		refereeServiceMock.processRefereesRoles(application.getReferees());
		refereeServiceMock.sendRefereeMailNotification(referee);
		EasyMock.replay(refereeServiceMock, errors);
		String view = controller.editReferee(referee, errors);
		EasyMock.verify(refereeServiceMock);
		assertEquals( "redirect:/update/getReferee?applicationId=ABC", view);
	}
	
    @Test
    public void shouldNotSendEmailIfApplicationInValidationStageAndIfNoErrors() {
        ApplicationForm application = new ApplicationFormBuilder().id(5).applicationNumber("ABC").status(ApplicationFormStatus.VALIDATION).build();
        Referee referee = new RefereeBuilder().id(1).application(application).build();
        application.setReferees(Arrays.asList(referee));
        BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(errors.hasErrors()).andReturn(false);
        refereeServiceMock.processRefereesRoles(application.getReferees());
        //refereeServiceMock.sendRefereeMailNotification(referee);
        EasyMock.replay(refereeServiceMock, errors);
        String view = controller.editReferee(referee, errors);
        EasyMock.verify(refereeServiceMock);
        assertEquals( "redirect:/update/getReferee?applicationId=ABC", view);
    }	
	
	@Test
	public void shouldSaveRefereeAndSendEmailIfApplicationInReviewStageAndIfNoErrors() {
		ApplicationForm application = new ApplicationFormBuilder().id(5).applicationNumber("ABC").status(ApplicationFormStatus.REVIEW).build();
		Referee referee = new RefereeBuilder().id(1).application(application).build();
		application.setReferees(Arrays.asList(referee));
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		refereeServiceMock.processRefereesRoles(application.getReferees());
		refereeServiceMock.sendRefereeMailNotification(referee);
		EasyMock.replay(refereeServiceMock, errors);
		String view = controller.editReferee(referee, errors);
		EasyMock.verify(refereeServiceMock);
		assertEquals( "redirect:/update/getReferee?applicationId=ABC", view);
	}
	
	@Test
	public void shouldSaveRefereeAndSendEmailIfApplicationInInterviewStageAndIfNoErrors() {
		ApplicationForm application = new ApplicationFormBuilder().id(5).applicationNumber("ABC").status(ApplicationFormStatus.INTERVIEW).build();
		Referee referee = new RefereeBuilder().id(1).application(application).build();
		application.setReferees(Arrays.asList(referee));
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		refereeServiceMock.processRefereesRoles(application.getReferees());
		refereeServiceMock.sendRefereeMailNotification(referee);
		EasyMock.replay(refereeServiceMock, errors);
		String view = controller.editReferee(referee, errors);
		EasyMock.verify(refereeServiceMock);
		assertEquals( "redirect:/update/getReferee?applicationId=ABC", view);
	}
	
	@Test
	public void shouldNotSendEmailIfApplicationIsInValidationdAndIfNoErrors() {
		ApplicationForm application = new ApplicationFormBuilder().id(5).applicationNumber("ABC").status(ApplicationFormStatus.VALIDATION).build();
		Referee referee = new RefereeBuilder().id(1).application(application).build();
		application.setReferees(Arrays.asList(referee));
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		refereeServiceMock.processRefereesRoles(application.getReferees());
		applicationsServiceMock.save(application);
		EasyMock.replay(applicationsServiceMock, refereeServiceMock);
		String view = controller.editReferee(referee, errors);
		EasyMock.verify(applicationsServiceMock, refereeServiceMock);
		assertEquals( "redirect:/update/getReferee?applicationId=ABC", view);
	}

	@Test
	public void shouldNotSaveAndReturnToViewIfErrors() {
		Referee referee = new RefereeBuilder().id(1).application(new ApplicationFormBuilder().id(5).build()).build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(true);
	
		EasyMock.replay(refereeServiceMock, errors);
		String view = controller.editReferee(referee, errors);
		EasyMock.verify(refereeServiceMock);
		assertEquals("/private/pgStudents/form/components/references_details", view);
	}
	
	
	@Before
	public void setUp() {

		refereeServiceMock = EasyMock.createMock(RefereeService.class);		
		countriesServiceMock = EasyMock.createMock(CountryService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		countryPropertyEditor = EasyMock.createMock(CountryPropertyEditor.class);
		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		refereeValidatorMock = EasyMock.createMock(RefereeValidator.class);

		controller = new RefereeController(refereeServiceMock, userServiceMock,countriesServiceMock, applicationsServiceMock,// 
				countryPropertyEditor, applicationFormPropertyEditorMock, refereeValidatorMock, encryptionHelperMock);

		currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
		
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
	}

	
}
