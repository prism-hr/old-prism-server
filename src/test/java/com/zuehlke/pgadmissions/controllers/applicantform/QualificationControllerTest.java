package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.dao.QualificationTypeDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.QualificationTypePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.QualificationValidator;

public class QualificationControllerTest {
	private RegisteredUser currentUser;
	private LanguageService languageServiceMock;
	private LanguagePropertyEditor languagePropertyEditorMock;
	private DatePropertyEditor datePropertyEditorMock;
	private DomicilePropertyEditor domicilePropertyEditor;
	private ApplicationsService applicationsServiceMock;
	private QualificationValidator qualificationValidatorMock;
	private DomicileDAO domicileDAOMock;
	private QualificationService qualificationServiceMock;
	private QualificationController controller;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
	private QualificationTypePropertyEditor qualificationTypePropertyEditorMock;
	private FullTextSearchService fullTextSearchServiceMock;

	private DocumentPropertyEditor documentPropertyEditorMock;
	private UserService userServiceMock;
	private EncryptionHelper encryptionHelperMock;
	
	private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;
	
	private Model modelMock;
	
	private QualificationInstitutionDAO institutionDAOMock;
	
	private QualificationTypeDAO qualificationTypeDAOMock;

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
		Qualification qualification = new QualificationBuilder().id(1).application(new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).id(5).build()).build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.replay(qualificationServiceMock, errors);
		controller.editQualification(qualification, errors, modelMock);
		EasyMock.verify(qualificationServiceMock);

	}

	@Test
	public void shouldReturnQualificationView() {
		assertEquals(QualificationController.APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME, controller.getQualificationView());
	}

	@Test
	public void shouldReturnAllLanguages() {
		List<Language> languageList = Arrays.asList(new LanguageBuilder().id(1).enabled(true).build(), new LanguageBuilder().id(2).enabled(false).build());
		EasyMock.expect(languageServiceMock.getAllEnabledLanguages()).andReturn(Collections.singletonList(languageList.get(0)));
		EasyMock.replay(languageServiceMock);
		List<Language> allLanguages = controller.getAllEnabledLanguages();
		assertEquals(1, allLanguages.size());
        assertEquals(languageList.get(0), allLanguages.get(0));
	}

	@Test
	public void shouldReturnAllDomiciles() {
		List<Domicile> domicileList = Arrays.asList(new DomicileBuilder().id(1).enabled(true).build(), new DomicileBuilder().id(2).enabled(false).build());
		EasyMock.expect(domicileDAOMock.getAllEnabledDomicilesExceptAlternateValues()).andReturn(Collections.singletonList(domicileList.get(0)));
		EasyMock.replay(domicileDAOMock);
		List<Domicile> allDomiciles = controller.getAllEnabledDomiciles();
		assertEquals(1, allDomiciles.size());
        assertEquals(domicileList.get(0), allDomiciles.get(0));
	}

	@Test
	public void shouldReturnApplicationForm() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
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

	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(qualificationValidatorMock);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		binderMock.registerCustomEditor(Language.class, languagePropertyEditorMock);
		binderMock.registerCustomEditor(Domicile.class, domicilePropertyEditor);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
		binderMock.registerCustomEditor(QualificationType.class, qualificationTypePropertyEditorMock);
		binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldGetQualificationFromServiceIfIdProvided() {
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
		Qualification qualification = new QualificationBuilder().id(1).institutionCountry(new DomicileBuilder().id(1).name("Foo").code("XL").build()).build();
		EasyMock.expect(qualificationServiceMock.getQualificationById(1)).andReturn(qualification);
		EasyMock.replay(qualificationServiceMock, encryptionHelperMock);
		Qualification returnedQualification = controller.getQualification("bob", modelMock);
		assertEquals(qualification, returnedQualification);
	}

	@Test
	public void shouldReturnNewQualificationIfIdIsNull() {
		Qualification returnedQualification = controller.getQualification(null, null);
		assertNull(returnedQualification.getId());
	}

	@Test
	public void shouldReturnNewQualificationIfIdIsBlank() {
		Qualification returnedQualification = controller.getQualification("", modelMock);
		assertNull(returnedQualification.getId());
	}
	
	@Test(expected = ResourceNotFoundException.class)	
	public void shouldThrowResourceNotFoundExceptionIfQualificationDoesNotExist() {
		EasyMock.expect(encryptionHelperMock.decryptToInteger("bob")).andReturn(1);
		EasyMock.expect(qualificationServiceMock.getQualificationById(1)).andReturn(null);
		EasyMock.replay(qualificationServiceMock,encryptionHelperMock);
		controller.getQualification("bob", modelMock);

	}

	@Test
	public void shouldReturnMessage() {
		assertEquals("bob", controller.getMessage("bob"));

	}

	@Test
	public void shouldSaveQulificationAndRedirectIfNoErrors() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").build();
		Qualification qualification = new QualificationBuilder().id(1).application(applicationForm).build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		qualificationServiceMock.save(qualification);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(qualificationServiceMock, applicationsServiceMock, errors);
		String view = controller.editQualification(qualification, errors, modelMock);
		EasyMock.verify(qualificationServiceMock, applicationsServiceMock);
		assertEquals("redirect:/update/getQualification?applicationId=ABC", view);
	}

	@Test
	public void shouldNotSaveAndReturnToViewIfErrors() {
		Qualification qualification = new QualificationBuilder().id(1).application(new ApplicationFormBuilder().id(5).build()).institutionCountry(new DomicileBuilder().id(1).name("Foo").code("XL").build()).build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(true);

		EasyMock.replay(qualificationServiceMock, errors);
		String view = controller.editQualification(qualification, errors, modelMock);
		EasyMock.verify(qualificationServiceMock);
		assertEquals(QualificationController.APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME, view);
	}

	@Before
	public void setUp() {
		languageServiceMock = EasyMock.createMock(LanguageService.class);
		languagePropertyEditorMock = EasyMock.createMock(LanguagePropertyEditor.class);

		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
		applicationFormUserRoleServiceMock = EasyMock.createMock(ApplicationFormUserRoleService.class);

		domicilePropertyEditor = EasyMock.createMock(DomicilePropertyEditor.class);
		domicileDAOMock = EasyMock.createMock(DomicileDAO.class);

		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);

		qualificationValidatorMock = EasyMock.createMock(QualificationValidator.class);
		qualificationServiceMock = EasyMock.createMock(QualificationService.class);

		documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);

		userServiceMock = EasyMock.createMock(UserService.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		
		qualificationTypeDAOMock = EasyMock.createMock(QualificationTypeDAO.class);

		qualificationTypePropertyEditorMock = EasyMock.createMock(QualificationTypePropertyEditor.class);
		
		modelMock = EasyMock.createMock(Model.class);
		
		institutionDAOMock = EasyMock.createMock(QualificationInstitutionDAO.class);
		
		fullTextSearchServiceMock = EasyMock.createMock(FullTextSearchService.class);
		
		controller = new QualificationController(applicationsServiceMock, applicationFormPropertyEditorMock, datePropertyEditorMock, domicileDAOMock,
				languageServiceMock, languagePropertyEditorMock, domicilePropertyEditor, qualificationValidatorMock, qualificationServiceMock,
				documentPropertyEditorMock, userServiceMock, encryptionHelperMock, qualificationTypeDAOMock, qualificationTypePropertyEditorMock, 
				institutionDAOMock, applicationFormUserRoleServiceMock, fullTextSearchServiceMock);

		currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);

	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}