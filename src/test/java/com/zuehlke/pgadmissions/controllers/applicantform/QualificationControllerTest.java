package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

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
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.QualificationTypePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.QualificationValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class QualificationControllerTest {

    @Mock
    @InjectIntoByType
    private LanguageService languageServiceMock;

    @Mock
    @InjectIntoByType
    private LanguagePropertyEditor languagePropertyEditorMock;

    @Mock
    @InjectIntoByType
    private DatePropertyEditor datePropertyEditorMock;

    @Mock
    @InjectIntoByType
    private DomicilePropertyEditor domicilePropertyEditor;

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationsServiceMock;

    @Mock
    @InjectIntoByType
    private QualificationValidator qualificationValidatorMock;

    @Mock
    @InjectIntoByType
    private DomicileDAO domicileDAOMock;

    @Mock
    @InjectIntoByType
    private QualificationService qualificationServiceMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;

    @Mock
    @InjectIntoByType
    private QualificationTypePropertyEditor qualificationTypePropertyEditorMock;

    @Mock
    @InjectIntoByType
    private FullTextSearchService fullTextSearchServiceMock;

    @Mock
    @InjectIntoByType
    private DocumentPropertyEditor documentPropertyEditorMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private QualificationInstitutionDAO institutionDAOMock;

    @Mock
    @InjectIntoByType
    private QualificationTypeDAO qualificationTypeDAOMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;

    @TestedObject
    private QualificationController controller;

    @Test
    public void shouldReturnQualificationView() {
        RegisteredUser applicant = new RegisteredUser();
        ModelMap modelMap = new ModelMap();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant);

        replay();
        assertEquals(QualificationController.APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME, controller.getQualificationView("app1", null, modelMap));

        assertNotNull(modelMap.get("qualification"));
    }

    @Test
    public void shouldNotSaveAndReturnToViewIfErrors() {
        RegisteredUser applicant = new RegisteredUser();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).applicationNumber("ABC").build();
        Qualification qualification = new QualificationBuilder().application(applicationForm).build();
        BindingResult errors = new BeanPropertyBindingResult(qualification, "qualification");
        errors.reject("aa");
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant);

        replay();
        String view = controller.editQualification(null, qualification, errors, modelMap);
        assertEquals(QualificationController.APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME, view);
    }

    @Test
    public void shouldSaveQulificationAndRedirectIfNoErrors() {
        RegisteredUser applicant = new RegisteredUser();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).applicationNumber("ABC").build();
        Qualification qualification = new QualificationBuilder().application(applicationForm).build();
        BindingResult errors = new BeanPropertyBindingResult(qualification, "qualification");
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant);
        qualificationServiceMock.saveOrUpdate(applicationForm, null, qualification);
        applicationFormUserRoleServiceMock.insertApplicationUpdate(applicationForm, applicant, ApplicationUpdateScope.ALL_USERS);

        replay();
        String view = controller.editQualification(null, qualification, errors, modelMap);

        assertEquals("redirect:/update/getQualification?applicationId=ABC", view);
    }

    @Test
    public void shouldReturnAllLanguages() {
        List<Language> languageList = Arrays.asList(new LanguageBuilder().id(1).enabled(true).build(), new LanguageBuilder().id(2).enabled(false).build());
        EasyMock.expect(languageServiceMock.getAllEnabledLanguages()).andReturn(Collections.singletonList(languageList.get(0)));

        replay();
        List<Language> allLanguages = controller.getAllEnabledLanguages();

        assertEquals(1, allLanguages.size());
        assertEquals(languageList.get(0), allLanguages.get(0));
    }

    @Test
    public void shouldReturnAllDomiciles() {
        List<Domicile> domicileList = Arrays.asList(new DomicileBuilder().id(1).enabled(true).build(), new DomicileBuilder().id(2).enabled(false).build());
        EasyMock.expect(domicileDAOMock.getAllEnabledDomicilesExceptAlternateValues()).andReturn(Collections.singletonList(domicileList.get(0)));

        replay();

        List<Domicile> allDomiciles = controller.getAllEnabledDomiciles();
        assertEquals(1, allDomiciles.size());
        assertEquals(domicileList.get(0), allDomiciles.get(0));
    }

    @Test
    public void shouldReturnApplicationForm() {
        RegisteredUser applicant = new RegisteredUser();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(applicant);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);

        replay();
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");

        assertEquals(applicationForm, returnedApplicationForm);
    }

    @Test(expected = CannotUpdateApplicationException.class)
    public void shouldThrowExceptionIfApplicationNotModifiable() {
        RegisteredUser user = new RegisteredUserBuilder().id(4).build();
        RegisteredUser applicant = new RegisteredUserBuilder().id(5).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(user);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);

        replay();
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");

        assertEquals(applicationForm, returnedApplicationForm);
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

        replay();
        controller.registerPropertyEditors(binderMock);
    }

}