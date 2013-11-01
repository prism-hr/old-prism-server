package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.FundingBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.FundingService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FundingValidator;

public class FundingControllerTest {

	private RegisteredUser currentUser;

	private DatePropertyEditor datePropertyEditorMock;
	private ApplicationsService applicationsServiceMock;
	private FundingValidator fundingValidatorMock;

	private FundingService fundingServiceMock;
	private FundingController controller;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;

	private DocumentPropertyEditor documentPropertyEditorMock;

	private UserService userServiceMock;

	private EncryptionHelper encryptionHelperMock;
	
	private ApplicationFormAccessService accessServiceMock;

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
		Funding funding = new FundingBuilder().id(1).application(new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).id(5).build())
				.build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.replay(fundingServiceMock, errors);
		controller.editFunding(funding, errors);
		EasyMock.verify(fundingServiceMock);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnSubmitIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.editFunding(null, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnGetIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.getFundingView();
	}

	@Test
	public void shouldReturnFundingView() {
		assertEquals("/private/pgStudents/form/components/funding_details", controller.getFundingView());
	}

	@Test
	public void shouldReturnAllFundingTypes() {

		FundingType[] fundingTypes = controller.getFundingTypes();
		assertArrayEquals(fundingTypes, FundingType.values());
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
		binderMock.setValidator(fundingValidatorMock);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
		binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldGetFundingFromServiceIfIdProvided() {
		EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
		
		Funding funding = new FundingBuilder().id(1).build();
		EasyMock.expect(fundingServiceMock.getFundingById(1)).andReturn(funding);
		EasyMock.replay(fundingServiceMock, encryptionHelperMock);
		Funding returnedFunding = controller.getFunding("encryptedId");
		assertEquals(funding, returnedFunding);
	}

	@Test
	public void shouldReturnNewFundingIfIdIsNull() {
		Funding returnedFunding = controller.getFunding(null);
		assertNull(returnedFunding.getId());
	}
	
	@Test	
	public void shouldReturnNewFundingIfIdIsBlank() {
		Funding returnedFunding = controller.getFunding("");
		assertNull(returnedFunding.getId());
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfFundingDoesNotExist() {
		EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
		EasyMock.expect(fundingServiceMock.getFundingById(1)).andReturn(null);
		EasyMock.replay(fundingServiceMock, encryptionHelperMock);
		controller.getFunding("encryptedId");

	}

	@Test
	public void shouldReturnMessage() {
		assertEquals("bob", controller.getMessage("bob"));

	}

	@Test
	public void shouldSaveQulificationAndRedirectIfNoErrors() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").build();
		Funding funding = new FundingBuilder().id(1).application(applicationForm).build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		fundingServiceMock.save(funding);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(fundingServiceMock, applicationsServiceMock, errors);
		String view = controller.editFunding(funding, errors);
		EasyMock.verify(fundingServiceMock, applicationsServiceMock);
		assertEquals("redirect:/update/getFunding?applicationId=ABC", view);
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE), DateUtils.truncate(applicationForm.getLastUpdated(), Calendar.DATE));
	}

	@Test
	public void shouldNotSaveAndReturnToViewIfErrors() {
		Funding funding = new FundingBuilder().id(1).application(new ApplicationFormBuilder().id(5).build()).build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(true);

		EasyMock.replay(fundingServiceMock, errors);
		String view = controller.editFunding(funding, errors);
		EasyMock.verify(fundingServiceMock);
		assertEquals("/private/pgStudents/form/components/funding_details", view);
	}

	@Before
	public void setUp() {
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);

		fundingValidatorMock = EasyMock.createMock(FundingValidator.class);
		fundingServiceMock = EasyMock.createMock(FundingService.class);

		documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);

		controller = new FundingController(applicationsServiceMock, applicationFormPropertyEditorMock, datePropertyEditorMock, fundingValidatorMock,
				fundingServiceMock, documentPropertyEditorMock, userServiceMock, encryptionHelperMock, accessServiceMock);

		currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);

	}

}
