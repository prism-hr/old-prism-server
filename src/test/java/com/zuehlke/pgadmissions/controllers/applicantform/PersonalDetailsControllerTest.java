package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageQualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DisabilityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.EthnicityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.DisabilityService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.EthnicityService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.PersonalDetailsUserValidator;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;

public class PersonalDetailsControllerTest {
    private RegisteredUser currentUser;
    private DatePropertyEditor datePropertyEditorMock;
    private ApplicationsService applicationsServiceMock;
    private PersonalDetailsValidator personalDetailsValidatorMock;
    private PersonalDetailsController controller;
    private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
    private PersonalDetailsUserValidator personalDetailsUserValidatorMock;

    private CountryService countryServiceMock;
    private EthnicityService ethnicityServiceMock;
    private DisabilityService disabilityServiceMock;
    private LanguageService languageServiceMok;
    private DocumentService documentServiceMock;
    private EncryptionHelper encryptionHelperMock;

    private CountryPropertyEditor countryPropertyEditorMock;
    private EthnicityPropertyEditor ethnicityPropertyEditorMock;
    private DisabilityPropertyEditor disabilityPropertyEditorMock;
    private LanguagePropertyEditor languagePropertyEditorMopck;
    private UserService userServiceMock;
    private DomicileService domicileServiceMock;
    private DomicilePropertyEditor domicilePropertyEditorMock;
    private PersonalDetailsService personalDetailsServiceMock;

    private Model modelMock;

    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;
    private DocumentPropertyEditor documentPropertyEditorMock;

    @Test(expected = CannotUpdateApplicationException.class)
    public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
        ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).id(5).build();
        PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).applicationForm(application).build();
        EasyMock.replay(userServiceMock);
        controller.editPersonalDetails(personalDetails, null, null, null, modelMock, application);
        EasyMock.verify(userServiceMock);
    }

    @Test
    public void shouldReturnPersonalDetailsView() {
        currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(true);
        EasyMock.reset(userServiceMock);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.replay(userServiceMock);

        PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).personalDetails(personalDetails).build();
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser);

        EasyMock.expect(modelMock.addAttribute(EasyMock.eq("languageQualification"), EasyMock.isA(LanguageQualification.class))).andReturn(modelMock);
        EasyMock.expect(modelMock.addAttribute(EasyMock.eq("personalDetails"), EasyMock.isA(PersonalDetails.class))).andReturn(modelMock);
        EasyMock.expect(modelMock.addAttribute(EasyMock.eq("applicationForm"), EasyMock.isA(ApplicationForm.class))).andReturn(modelMock);

        EasyMock.replay(modelMock);

        assertEquals("/private/pgStudents/form/components/personal_details", controller.getPersonalDetailsView("1", modelMock));
        assertNotNull(personalDetails.getLanguageQualification());

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
        List<Disability> disabilityList = Arrays.asList(new DisabilityBuilder().id(1).enabled(true).build(), new DisabilityBuilder().id(2).enabled(false)
                .build());
        EasyMock.expect(disabilityServiceMock.getAllEnabledDisabilities()).andReturn(Collections.singletonList(disabilityList.get(0)));
        EasyMock.replay(disabilityServiceMock);
        List<Disability> allDisabilities = controller.getAllEnabledDisabilities();
        assertEquals(1, allDisabilities.size());
        assertEquals(disabilityList.get(0), allDisabilities.get(0));
    }

    @Test
    public void shouldReturnCurrentUser() {
        EasyMock.replay(userServiceMock);
        assertEquals(currentUser, controller.getUser());
        EasyMock.verify(userServiceMock);
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

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
        EasyMock.replay(applicationsServiceMock);
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
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.eq("firstNationality"), EasyMock.anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.eq("secondNationality"), EasyMock.anyObject(StringTrimmerEditor.class));
        EasyMock.replay(binderMock);
        controller.registerPropertyEditorsForPersonalDetails(binderMock);
        EasyMock.verify(binderMock);
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
        LanguageQualification languageQualification = new LanguageQualificationBuilder().build();
        PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).applicationForm(applicationForm).languageQualificationAvailable(false).build();
        personalDetails.setLanguageQualification(languageQualification);
        BindingResult personalDetailsErrors = new BeanPropertyBindingResult(personalDetails, "personalDetails");

        RegisteredUser updatedUser = new RegisteredUserBuilder().firstName("Jakub").firstName2("Marcin").firstName3("Jozef").lastName("Fibinger").build();
        BindingResult updatedUserErrors = new BeanPropertyBindingResult(updatedUser, "updatedUser");

        userServiceMock.updateCurrentUser(updatedUser);
        personalDetailsServiceMock.save(applicationForm, personalDetails);
        EasyMock.replay(applicationsServiceMock, userServiceMock);
        String view = controller.editPersonalDetails(personalDetails, personalDetailsErrors, updatedUser, updatedUserErrors, modelMock, applicationForm);

        EasyMock.verify(applicationsServiceMock, userServiceMock);
        assertEquals("redirect:/update/getPersonalDetails?applicationId=ABC", view);
    }

    @Test
    public void shouldNotSaveAndReturnToViewIfPersonalDetailsErrors() {
        ApplicationForm application = new ApplicationFormBuilder().id(5).build();
        PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).applicationForm(application).build();
        BindingResult personalDetailsErrors = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailsErrors.reject("anyError");

        RegisteredUser updatedUser = new RegisteredUserBuilder().firstName("Jakub").firstName2("Marcin").firstName3("Jozef").lastName("Fibinger").build();
        BindingResult updatedUserErrors = new BeanPropertyBindingResult(updatedUser, "updatedUser");

        EasyMock.replay(userServiceMock);
        String view = controller.editPersonalDetails(personalDetails, personalDetailsErrors, updatedUser, updatedUserErrors, modelMock, application);
        EasyMock.verify(userServiceMock);
        assertEquals("/private/pgStudents/form/components/personal_details", view);
    }

    @Test
    public void shouldNotSaveAndReturnToViewIfUpdatedUserErrors() {
        ApplicationForm application = new ApplicationFormBuilder().id(5).build();
        PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).applicationForm(application).build();
        BindingResult personalDetailsErrors = new BeanPropertyBindingResult(personalDetails, "personalDetails");

        RegisteredUser updatedUser = new RegisteredUserBuilder().firstName("Jakub").firstName2("Marcin").firstName3("Jozef").lastName("Fibinger").build();
        BindingResult updatedUserErrors = new BeanPropertyBindingResult(updatedUser, "updatedUser");
        updatedUserErrors.reject("anyError");

        EasyMock.replay(userServiceMock);
        String view = controller.editPersonalDetails(personalDetails, personalDetailsErrors, updatedUser, updatedUserErrors, modelMock, application);
        EasyMock.verify(userServiceMock);
        assertEquals("/private/pgStudents/form/components/personal_details", view);
    }

    @Test
    public void shouldDeleteLanguageQualificationsDocument() {
        BindingResult resultMock = EasyMock.createMock(BindingResult.class);
        currentUser = EasyMock.createMock(RegisteredUser.class);
        Model modelMock = EasyMock.createMock(Model.class);

        Document document = new DocumentBuilder().id(33).build();

        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.expect(encryptionHelperMock.decryptToInteger("docId")).andReturn(8);
        EasyMock.expect(documentServiceMock.getDocumentById(8)).andReturn(document);

        documentServiceMock.delete(document);

        EasyMock.replay(documentServiceMock, encryptionHelperMock, userServiceMock, currentUser, modelMock, resultMock);

        String resultView = controller.deleteLanguageQualificationsDocument("docId", modelMock);
        assertEquals("/private/pgStudents/form/components/personal_details_language_qualifications", resultView);

        EasyMock.verify(documentServiceMock, encryptionHelperMock, userServiceMock, currentUser, modelMock, resultMock);
    }

    @Before
    public void setUp() {
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        countryServiceMock = EasyMock.createMock(CountryService.class);
        disabilityServiceMock = EasyMock.createMock(DisabilityService.class);
        ethnicityServiceMock = EasyMock.createMock(EthnicityService.class);
        languageServiceMok = EasyMock.createMock(LanguageService.class);
        documentServiceMock = EasyMock.createMock(DocumentService.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);

        modelMock = EasyMock.createMock(Model.class);

        applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
        countryPropertyEditorMock = EasyMock.createMock(CountryPropertyEditor.class);
        ethnicityPropertyEditorMock = EasyMock.createMock(EthnicityPropertyEditor.class);
        disabilityPropertyEditorMock = EasyMock.createMock(DisabilityPropertyEditor.class);
        languagePropertyEditorMopck = EasyMock.createMock(LanguagePropertyEditor.class);
        datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
        domicileServiceMock = EasyMock.createMock(DomicileService.class);
        domicilePropertyEditorMock = EasyMock.createMock(DomicilePropertyEditor.class);
        personalDetailsUserValidatorMock = EasyMock.createMock(PersonalDetailsUserValidator.class);

        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);

        personalDetailsValidatorMock = EasyMock.createMock(PersonalDetailsValidator.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(ApplicationFormUserRoleService.class);
        personalDetailsServiceMock = EasyMock.createMock(PersonalDetailsService.class);

        controller = new PersonalDetailsController(applicationsServiceMock, userServiceMock, applicationFormPropertyEditorMock, datePropertyEditorMock,
                countryServiceMock, ethnicityServiceMock, disabilityServiceMock, languageServiceMok, languagePropertyEditorMopck, countryPropertyEditorMock,
                disabilityPropertyEditorMock, ethnicityPropertyEditorMock, personalDetailsValidatorMock, domicileServiceMock, domicilePropertyEditorMock,
                documentPropertyEditorMock, documentServiceMock, encryptionHelperMock, personalDetailsUserValidatorMock, personalDetailsServiceMock,
                applicationFormUserRoleServiceMock);

        currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
    }
}