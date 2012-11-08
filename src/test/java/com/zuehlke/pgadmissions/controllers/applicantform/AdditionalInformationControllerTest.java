package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdditionalInformationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.BooleanPropertyEditor;
import com.zuehlke.pgadmissions.services.AdditionalInfoService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AdditionalInformationValidator;

public class AdditionalInformationControllerTest {
	private RegisteredUser currentUser;

	private AdditionalInfoService addInfoServiceMock;
	private ApplicationsService applicationServiceMock;
	private AdditionalInformationValidator validatorMock;
	private ApplicationFormPropertyEditor applFormPropertyEditorMock;
	private BooleanPropertyEditor booleanPropertyEditorMock;

	private AdditionalInformationController controller;

	private UserService userServiceMock;


	@Before
	public void setUp() {
		addInfoServiceMock = EasyMock.createMock(AdditionalInfoService.class);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		applFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
		booleanPropertyEditorMock = EasyMock.createMock(BooleanPropertyEditor.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		validatorMock = EasyMock.createMock(AdditionalInformationValidator.class);
		controller = new AdditionalInformationController(applicationServiceMock, userServiceMock, applFormPropertyEditorMock,// 
				booleanPropertyEditorMock, addInfoServiceMock, validatorMock);		

		currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnSubmitIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.editAdditionalInformation(null, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnGetIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.getAdditionalInformationView();
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void throwExceptionWhenApplicationFormAlreadySubmitted() {
		ApplicationForm applForm = new ApplicationFormBuilder().id(1)//
				.status(ApplicationFormStatus.APPROVED)//
				.toApplicationForm();
		AdditionalInformation info = new AdditionalInformationBuilder().id(1)//
				.applicationForm(applForm).toAdditionalInformation();
		controller.editAdditionalInformation(info, null);
	}

	@Test
	public void shouldReturnApplicationFormViewWhenErrors() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		AdditionalInformation info = new AdditionalInformationBuilder().id(1).applicationForm(applicationForm).toAdditionalInformation();

		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(true);

		EasyMock.replay(errors, applicationServiceMock, addInfoServiceMock);
		String viewID = controller.editAdditionalInformation(info, errors);
		EasyMock.verify(errors, applicationServiceMock, addInfoServiceMock);
		Assert.assertEquals("/private/pgStudents/form/components/additional_information", viewID);
	}

	@Test
	public void shouldReturnApplicationFormView() {
		Assert.assertEquals("/private/pgStudents/form/components/additional_information", controller.getAdditionalInformationView());
	}

	@Test
	public void shouldReturnMessage() {
		Assert.assertEquals("bob", controller.getMessage("bob"));
	}

	@Test
	public void shouldReturnErrorCode() {
		Assert.assertEquals("bob", controller.getErrorCode("bob"));
	}

	@Test
	public void shouldSaveAdditionalInfoAndRedirect() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").toApplicationForm();
		AdditionalInformation info = new AdditionalInformationBuilder().id(1).applicationForm(applicationForm).toAdditionalInformation();

		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);

		addInfoServiceMock.save(info);
		applicationServiceMock.save(applicationForm);
		
		EasyMock.replay(errors, applicationServiceMock, addInfoServiceMock);
		String viewID = controller.editAdditionalInformation(info, errors);
		EasyMock.verify(errors, applicationServiceMock, addInfoServiceMock);
		Assert.assertEquals("redirect:/update/getAdditionalInformation?applicationId=ABC", viewID);
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(),Calendar.DATE), DateUtils.truncate(applicationForm.getLastUpdated(), Calendar.DATE));
	}

	@Test
	public void returnAdditionalInfo() {
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);

		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(userMock).anyTimes();
		EasyMock.replay(userServiceMock);
		AdditionalInformation additionalInfo = new AdditionalInformationBuilder().id(200).toAdditionalInformation();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(100).toApplicationForm();
		applicationForm.setAdditionalInformation(additionalInfo);

		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("100")).andReturn(applicationForm);

		EasyMock.replay(userMock, applicationServiceMock, addInfoServiceMock);
		AdditionalInformation returnedAddInfo = controller.getAdditionalInformation("100");
		Assert.assertEquals(additionalInfo, returnedAddInfo);
		EasyMock.verify(userMock, applicationServiceMock, addInfoServiceMock);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void throwRNFEIfApplicationFormDoesNotExist() {
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
		EasyMock.replay(applicationServiceMock, addInfoServiceMock);
		controller.getAdditionalInformation("1");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void throwRNFEIfUserCantseeForm() {
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);

		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(userMock).anyTimes();
		EasyMock.replay(userServiceMock);
		AdditionalInformation additionalInfo = new AdditionalInformationBuilder().id(200).toAdditionalInformation();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(100).toApplicationForm();
		applicationForm.setAdditionalInformation(additionalInfo);

		EasyMock.expect(userMock.canSee(applicationForm)).andReturn(false);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("100")).andReturn(applicationForm);

		EasyMock.replay(userMock, applicationServiceMock, addInfoServiceMock);
		controller.getAdditionalInformation("100");
	}

	@Test
	public void shouldBindPropertyEditors() {
	    final StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(false);
	    controller = new AdditionalInformationController(applicationServiceMock, userServiceMock, 
	            applFormPropertyEditorMock, booleanPropertyEditorMock, addInfoServiceMock, validatorMock) {
	        @Override
            public StringTrimmerEditor newStringTrimmerEditor() {
                return stringTrimmerEditor;
            }
	    };      
	    
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(validatorMock);
		binderMock.registerCustomEditor(ApplicationForm.class, applFormPropertyEditorMock);
		binderMock.registerCustomEditor(Boolean.class, booleanPropertyEditorMock);
		binderMock.registerCustomEditor(String.class, stringTrimmerEditor);
		EasyMock.replay(binderMock);
		controller.registerValidatorsEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldReturnApplicationForm() {
		currentUser = EasyMock.createMock(RegisteredUser.class);

		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("100")).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock, currentUser);
		ApplicationForm returnedApplicationForm = controller.getApplicationForm("100");
		Assert.assertEquals(applicationForm, returnedApplicationForm);
		EasyMock.verify(applicationServiceMock);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowRNFEIfNullApplication() {
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
		EasyMock.replay(applicationServiceMock);
		controller.getApplicationForm("1");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowRNFEIfUserCAnnotSeeApplFormOnGet() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(applicationServiceMock, currentUser);
		controller.getApplicationForm("1");

	}
}
