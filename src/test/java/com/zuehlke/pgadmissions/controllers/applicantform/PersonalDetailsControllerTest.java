package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.SessionStatus;

import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageQualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DisabilityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.EthnicityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.DisabilityService;
import com.zuehlke.pgadmissions.services.EthnicityService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.LanguageQualificationValidator;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;

public class PersonalDetailsControllerTest {
	private RegisteredUser currentUser;
	private DatePropertyEditor datePropertyEditorMock;
	private ApplicationsService applicationsServiceMock;
	private PersonalDetailsValidator personalDetailsValidatorMock;
	private PersonalDetailsService personalDetailsServiceMock;
	private PersonalDetailsController controller;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
	
	private CountryService countryServiceMock;
	private EthnicityService ethnicityServiceMock;
	private DisabilityService disabilityServiceMock;
	private LanguageService languageServiceMok;
	private CountryPropertyEditor countryPropertyEditorMock;
	private EthnicityPropertyEditor ethnicityPropertyEditorMock;
	private DisabilityPropertyEditor disabilityPropertyEditorMock;
	private LanguagePropertyEditor languagePropertyEditorMopck;
	private UserService userServiceMock;
	private DomicileDAO domicileDAOMock;
	private DomicilePropertyEditor domicilePropertyEditorMock;
	
	private Model modelMock;
	
	private LanguageQualificationValidator languageQualificationValidatorMock;
    private DocumentPropertyEditor documentPropertyEditorMock;
    
    private SessionStatus sessionStatusMock;

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
		PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).applicationForm(new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).id(5).build()).build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.replay(personalDetailsServiceMock, errors);
		controller.editPersonalDetails(personalDetails, errors, modelMock, sessionStatusMock);
		EasyMock.verify(personalDetailsServiceMock);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnSubmitIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.editPersonalDetails(null, null, null, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnGetIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.getPersonalDetailsView("1", modelMock);
	}

	@Test
	public void shouldReturnPersonalDetailsView() {
	    currentUser = EasyMock.createMock(RegisteredUser.class);
	    EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(true);
        EasyMock.reset(userServiceMock);
        
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.replay(userServiceMock);
        
        
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser);
		
        EasyMock.expect(modelMock.addAttribute(EasyMock.eq("languageQualification"), EasyMock.isA(LanguageQualification.class))).andReturn(modelMock);
        EasyMock.expect(modelMock.addAttribute(EasyMock.eq("personalDetails"), EasyMock.isA(PersonalDetails.class))).andReturn(modelMock);
        EasyMock.expect(modelMock.addAttribute(EasyMock.eq("applicationForm"), EasyMock.isA(ApplicationForm.class))).andReturn(modelMock);
        
        EasyMock.replay(modelMock);
        
        assertEquals("/private/pgStudents/form/components/personal_details", controller.getPersonalDetailsView("1", modelMock));
	}

	@Test
	public void shouldReturnAllEnabledLanguages() {
		List<Language> languageList = Arrays.asList(new LanguageBuilder().id(1).enabled(true).build(), new LanguageBuilder().id(2).enabled(false).build());
		EasyMock.expect(languageServiceMok.getAllEnabledLanguages()).andReturn(Collections.singletonList(languageList.get(0)));
		EasyMock.replay(languageServiceMok);
		List<Language> allLanguages = controller.getAllEnabledLanguages();
		assertEquals(1, allLanguages.size());
        assertEquals(languageList.get(0), allLanguages.get(0));
	}

	@Test
	public void shouldReturnAllEnabledCountries() {
		List<Country> countryList = Arrays.asList(new CountryBuilder().id(1).enabled(true).build(), new CountryBuilder().id(2).enabled(false).build());
		EasyMock.expect(countryServiceMock.getAllEnabledCountries()).andReturn(Collections.singletonList(countryList.get(0)));
		EasyMock.replay(countryServiceMock);
		List<Country> allCountries = controller.getAllEnabledCountries();
		assertEquals(1, allCountries.size());
        assertEquals(countryList.get(0), allCountries.get(0));
	}

	@Test
	public void returnAllEnabledEthnicities() {
		List<Ethnicity> ethnicityList = Arrays.asList(new EthnicityBuilder().id(1).enabled(true).build(), new EthnicityBuilder().id(2).enabled(false).build());
		EasyMock.expect(ethnicityServiceMock.getAllEnabledEthnicities()).andReturn(Collections.singletonList(ethnicityList.get(0)));
		EasyMock.replay(ethnicityServiceMock);
		List<Ethnicity> allEthnicities = controller.getAllEnabledEthnicities();
		assertEquals(1, allEthnicities.size());
        assertEquals(ethnicityList.get(0), allEthnicities.get(0));
	}

	@Test
	public void returnAllEnabledDisabilities() {
		List<Disability> disabilityList = Arrays.asList(new DisabilityBuilder().id(1).enabled(true).build(), new DisabilityBuilder().id(2).enabled(false).build());
		EasyMock.expect(disabilityServiceMock.getAllEnabledDisabilities()).andReturn(Collections.singletonList(disabilityList.get(0)));
		EasyMock.replay(disabilityServiceMock);
		List<Disability> allDisabilities = controller.getAllEnabledDisabilities();
		assertEquals(1, allDisabilities.size());
        assertEquals(disabilityList.get(0), allDisabilities.get(0));
	}

	@Test
	public void shouldReturnCurrentUser() {
		assertEquals(currentUser, controller.getUser());
	}

	@Test
	public void shouldReturnAllGenders() {
		Gender[] genders = controller.getGenders();
		assertArrayEquals(genders, Gender.values());
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
		binderMock.setValidator(personalDetailsValidatorMock);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		binderMock.registerCustomEditor(Language.class, languagePropertyEditorMopck);
		binderMock.registerCustomEditor(Country.class, countryPropertyEditorMock);
		binderMock.registerCustomEditor(Domicile.class, domicilePropertyEditorMock);
		binderMock.registerCustomEditor(Ethnicity.class, ethnicityPropertyEditorMock);
		binderMock.registerCustomEditor(Disability.class, disabilityPropertyEditorMock);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
		EasyMock.replay(binderMock);
		controller.registerPropertyEditorsForPersonalDetails(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldGetPersonalDetailsFromApplicationForm() {

		PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
		applicationForm.setPersonalDetails(personalDetails);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(applicationsServiceMock, currentUser);

		PersonalDetails returnedPersonalDetails = controller.getPersonalDetails("5");
		assertEquals(personalDetails, returnedPersonalDetails);
	}

	@Test
	public void shouldReturnNewPersonalDetailsIfApplicationFormHasNoPersonalDetails() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(applicationsServiceMock, currentUser);
		PersonalDetails returnedPersonalDetails = controller.getPersonalDetails("5");
		assertNull(returnedPersonalDetails.getId());
	}

	@Test
	public void shouldReturnMessage() {
		assertEquals("bob", controller.getMessage("bob"));
	}

	@Test
	public void shouldReturnErrorCode() {
		assertEquals("bob", controller.getErrorCode("bob"));
	}

	@Test
	public void shouldSavePersonalDetailsAndRedirectIfNoErrors() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").build();
		PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).applicationForm(applicationForm).build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		personalDetailsServiceMock.save(personalDetails);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(personalDetailsServiceMock,applicationsServiceMock,  errors);
		String view = controller.editPersonalDetails(personalDetails, errors, modelMock, sessionStatusMock);
		EasyMock.verify(personalDetailsServiceMock, applicationsServiceMock);
		assertEquals("redirect:/update/getPersonalDetails?applicationId=ABC", view);
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(),Calendar.DATE), DateUtils.truncate(applicationForm.getLastUpdated(), Calendar.DATE));
	}

	@Test
	public void shouldNotSaveAndReturnToViewIfErrors() {
		PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).applicationForm(new ApplicationFormBuilder().id(5).build()).build();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(true);

		EasyMock.replay(personalDetailsServiceMock, errors);
		String view = controller.editPersonalDetails(personalDetails, errors, modelMock, sessionStatusMock);
		EasyMock.verify(personalDetailsServiceMock);
		assertEquals("/private/pgStudents/form/components/personal_details", view);
	}
	
	@Test
	public void shouldAddLanguageQualification() {
	    BindingResult resultMock = EasyMock.createMock(BindingResult.class);
	    Model modelMock = EasyMock.createMock(Model.class);
	    PersonalDetails personalDetailsMock = EasyMock.createMock(PersonalDetails.class);
	    currentUser = EasyMock.createMock(RegisteredUser.class);
        LanguageQualification qualification = new LanguageQualificationBuilder().id(1).dateOfExamination(new Date())
                .examTakenOnline(false).languageQualification(LanguageQualificationEnum.IELTS_ACADEMIC)
                .listeningScore("1").overallScore("1").readingScore("1").speakingScore("1").writingScore("1")
                .build();
        
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(true);
        EasyMock.expect(resultMock.hasErrors()).andReturn(false);
        personalDetailsMock.addLanguageQualification(qualification);
        personalDetailsMock.setLanguageQualificationAvailable(true);
        personalDetailsMock.setEnglishFirstLanguage(false);
        EasyMock.expect(modelMock.addAttribute("languageQualification", qualification)).andReturn(modelMock);
        EasyMock.expect(modelMock.addAttribute(EasyMock.eq("languageQualification"), EasyMock.isA(LanguageQualification.class))).andReturn(modelMock);

        EasyMock.replay(userServiceMock, currentUser, resultMock, modelMock, personalDetailsMock);
        
	    String resultView = controller.addLanguageQualification(qualification, resultMock, personalDetailsMock, modelMock);
	    assertEquals("/private/pgStudents/form/components/personal_details_language_qualifications", resultView);
	    
	    EasyMock.verify(userServiceMock, currentUser, resultMock, modelMock, personalDetailsMock);
	}
	
	@Test
	public void shouldReturnLanguageQualification() {
	    PersonalDetails personalDetailsMock = EasyMock.createMock(PersonalDetails.class);
	    Model modelMock = EasyMock.createMock(Model.class);
	    LanguageQualification qualification = new LanguageQualificationBuilder().id(1).dateOfExamination(new Date())
                .examTakenOnline(false).languageQualification(LanguageQualificationEnum.IELTS_ACADEMIC)
                .listeningScore("1").overallScore("1").readingScore("1").speakingScore("1").writingScore("1")
                .build();
	    
	    EasyMock.expect(personalDetailsMock.getLanguageQualifications()).andReturn(Arrays.asList(qualification));
	    EasyMock.expect(modelMock.addAttribute("languageQualificationId", "0")).andReturn(modelMock);
	    EasyMock.expect(modelMock.addAttribute("languageQualification", qualification)).andReturn(modelMock);

	    EasyMock.replay(personalDetailsMock, modelMock);
	    
	    String resultView = controller.getLanguageQualification("0", personalDetailsMock, modelMock);
	    assertEquals("/private/pgStudents/form/components/personal_details_language_qualifications", resultView);
	    
	    EasyMock.verify(personalDetailsMock, modelMock);
	}
	
	@Test
	@SuppressWarnings("unchecked")
    public void shouldDeleteLanguageQualification() {
        PersonalDetails personalDetailsMock = EasyMock.createMock(PersonalDetails.class);
        currentUser = EasyMock.createMock(RegisteredUser.class);
        Model modelMock = EasyMock.createMock(Model.class);
        LanguageQualification qualification = new LanguageQualificationBuilder().id(1).dateOfExamination(new Date())
                .examTakenOnline(false).languageQualification(LanguageQualificationEnum.IELTS_ACADEMIC)
                .listeningScore("1").overallScore("1").readingScore("1").speakingScore("1").writingScore("1")
                .build();
        
        ArrayList<LanguageQualification> listMock = EasyMock.createMock(ArrayList.class);
        
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(true);
        
        EasyMock.expect(personalDetailsMock.getLanguageQualifications()).andReturn(listMock);
        EasyMock.expect(listMock.remove(0)).andReturn(qualification);
        EasyMock.expect(modelMock.addAttribute(EasyMock.eq("languageQualification"), EasyMock.isA(LanguageQualification.class))).andReturn(modelMock);

        EasyMock.replay(userServiceMock, currentUser, personalDetailsMock, modelMock, listMock);
        
        String resultView = controller.deleteLanguageQualification("0", personalDetailsMock, modelMock);
        assertEquals("/private/pgStudents/form/components/personal_details_language_qualifications", resultView);
        
        EasyMock.verify(userServiceMock, currentUser, personalDetailsMock, modelMock, listMock);
    }
	
	@Test
	@SuppressWarnings("unchecked")
	public void shouldUpdateLanguageQualification() {
	    BindingResult resultMock = EasyMock.createMock(BindingResult.class);
	    PersonalDetails personalDetailsMock = EasyMock.createMock(PersonalDetails.class);
        currentUser = EasyMock.createMock(RegisteredUser.class);
        Model modelMock = EasyMock.createMock(Model.class);
        LanguageQualification qualification = new LanguageQualificationBuilder().id(1).dateOfExamination(new Date())
                .examTakenOnline(false).languageQualification(LanguageQualificationEnum.IELTS_ACADEMIC)
                .listeningScore("1").overallScore("1").readingScore("1").speakingScore("1").writingScore("1")
                .build();
        
        LanguageQualification qualificationMock = EasyMock.createMock(LanguageQualification.class);
        
        ArrayList<LanguageQualification> listMock = EasyMock.createMock(ArrayList.class);
	    
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(true);
        
        EasyMock.expect(resultMock.hasErrors()).andReturn(false);
        
        EasyMock.expect(personalDetailsMock.getLanguageQualifications()).andReturn(listMock);
        EasyMock.expect(listMock.remove(0)).andReturn(qualificationMock);
        EasyMock.expect(qualificationMock.getId()).andReturn(1);
        
        personalDetailsMock.addLanguageQualification(qualification);
        
        EasyMock.expect(modelMock.addAttribute(EasyMock.eq("languageQualification"), EasyMock.isA(LanguageQualification.class))).andReturn(modelMock);

        EasyMock.replay(userServiceMock, currentUser, personalDetailsMock, modelMock, listMock, resultMock, qualificationMock);
        
        String resultView = controller.updateLanguageQualification(qualification, resultMock, "0", personalDetailsMock, modelMock);
        assertEquals("/private/pgStudents/form/components/personal_details_language_qualifications", resultView);
        
        EasyMock.verify(userServiceMock, currentUser, personalDetailsMock, modelMock, listMock, resultMock, qualificationMock);
	}
	
	@Test
	public void shouldDeleteLanguageQualificationsDocument() {
	    BindingResult resultMock = EasyMock.createMock(BindingResult.class);
        PersonalDetails personalDetailsMock = EasyMock.createMock(PersonalDetails.class);
        currentUser = EasyMock.createMock(RegisteredUser.class);
        Model modelMock = EasyMock.createMock(Model.class);
        
        LanguageQualification qualificationMock = EasyMock.createMock(LanguageQualification.class);
        
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(true);
        
        EasyMock.expect(personalDetailsMock.getLanguageQualifications()).andReturn(Arrays.asList(qualificationMock));
        qualificationMock.setLanguageQualificationDocument(null);
        
        EasyMock.expect(modelMock.addAttribute(EasyMock.eq("languageQualification"), EasyMock.isA(LanguageQualification.class))).andReturn(modelMock);

        EasyMock.replay(userServiceMock, currentUser, personalDetailsMock, modelMock, resultMock, qualificationMock);
        
        String resultView = controller.deleteLanguageQualificationsDocument(personalDetailsMock, "0", modelMock);
        assertEquals("/private/pgStudents/form/components/personal_details_language_qualifications", resultView);
        
        EasyMock.verify(userServiceMock, currentUser, personalDetailsMock, modelMock, resultMock, qualificationMock);
	}

	@Before
	public void setUp() {
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		countryServiceMock = EasyMock.createMock(CountryService.class);
		disabilityServiceMock = EasyMock.createMock(DisabilityService.class);
		ethnicityServiceMock = EasyMock.createMock(EthnicityService.class);
		languageServiceMok = EasyMock.createMock(LanguageService.class);
		personalDetailsServiceMock = EasyMock.createMock(PersonalDetailsService.class);
		
		modelMock = EasyMock.createMock(Model.class);

		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
		countryPropertyEditorMock = EasyMock.createMock(CountryPropertyEditor.class);
		ethnicityPropertyEditorMock = EasyMock.createMock(EthnicityPropertyEditor.class);
		disabilityPropertyEditorMock = EasyMock.createMock(DisabilityPropertyEditor.class);
		languagePropertyEditorMopck = EasyMock.createMock(LanguagePropertyEditor.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
		domicileDAOMock = EasyMock.createMock(DomicileDAO.class);
		domicilePropertyEditorMock = EasyMock.createMock(DomicilePropertyEditor.class);
		
		languageQualificationValidatorMock = EasyMock.createMock(LanguageQualificationValidator.class); 
	    documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
		
		personalDetailsValidatorMock = EasyMock.createMock(PersonalDetailsValidator.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		
		sessionStatusMock = EasyMock.createMock(SessionStatus.class);
		
		controller = new PersonalDetailsController(applicationsServiceMock, userServiceMock, applicationFormPropertyEditorMock,// 
				datePropertyEditorMock, countryServiceMock, ethnicityServiceMock, disabilityServiceMock,// 
				languageServiceMok, languagePropertyEditorMopck, countryPropertyEditorMock,// 
				disabilityPropertyEditorMock, ethnicityPropertyEditorMock,// 
				personalDetailsValidatorMock, personalDetailsServiceMock, domicileDAOMock, 
				domicilePropertyEditorMock, 
				languageQualificationValidatorMock,
				documentPropertyEditorMock);

		currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
	}
}
