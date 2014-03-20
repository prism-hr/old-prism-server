package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.RefereeValidator;

public class RefereeControllerTest {

    private RegisteredUser currentUser;
    private RefereeService refereeServiceMock;
    private RefereeController controller;
    private ApplicationsService applicationsServiceMock;
    private DomicilePropertyEditor domicilePropertyEditor;
    private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
    private RefereeValidator refereeValidatorMock;
    private EncryptionHelper encryptionHelperMock;
    private UserService userServiceMock;
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;
    private DomicileService domicileServiceMock;
    private FullTextSearchService fullTextSearchServiceMock;

    @Test(expected = CannotUpdateApplicationException.class)
    public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
        ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).id(5).build();
        Referee referee = new RefereeBuilder().id(1).application(application).build();
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("applicationForm", application);
        BindingResult errors = EasyMock.createMock(BindingResult.class);
        EasyMock.replay(refereeServiceMock, errors);
        controller.editReferee(null, referee, errors, modelMap);
        EasyMock.verify(refereeServiceMock);

    }

    @Test
    public void shouldReturnRefereeView() {
        ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).id(5).build();
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("applicationForm", application);

        Referee referee = new Referee();

        EasyMock.expect(encryptionHelperMock.decryptToInteger("enc")).andReturn(1);
        EasyMock.expect(refereeServiceMock.getById(1)).andReturn(referee);
        
        EasyMock.replay(refereeServiceMock, encryptionHelperMock);
        assertEquals("/private/pgStudents/form/components/references_details", controller.getRefereeView("enc", modelMap));
        EasyMock.verify(refereeServiceMock, encryptionHelperMock);
        
        assertSame(referee, modelMap.get("referee"));
    }
    
    @Test
    public void shouldReturnRefereeViewWithNewReferee() {
        ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).id(5).build();
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("applicationForm", application);

        assertEquals("/private/pgStudents/form/components/references_details", controller.getRefereeView("", modelMap));
        
        assertNotNull(modelMap.get("referee"));
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
        binderMock.setValidator(refereeValidatorMock);
        binderMock.registerCustomEditor(Domicile.class, domicilePropertyEditor);
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
        EasyMock.expect(refereeServiceMock.getById(1)).andReturn(referee);
        EasyMock.replay(refereeServiceMock, encryptionHelperMock);

        Referee returnedReferee = controller.getReferee("enc");
        EasyMock.verify(refereeServiceMock, encryptionHelperMock);

        assertEquals(referee, returnedReferee);
    }

    @Test
    public void shouldReturnNullIfIdIsBlank() {
        assertNull(controller.getReferee(""));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfRefereeDoesNotExist() {
        EasyMock.expect(encryptionHelperMock.decryptToInteger("encrypted")).andReturn(1);
        EasyMock.expect(refereeServiceMock.getById(1)).andReturn(null);
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
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("applicationForm", applicationForm);
        Referee referee = new RefereeBuilder().id(1).application(applicationForm).build();
        BindingResult errors = EasyMock.createMock(BindingResult.class);

        EasyMock.expect(errors.hasErrors()).andReturn(false);
        EasyMock.expect(encryptionHelperMock.decryptToInteger("enc")).andReturn(1);
        EasyMock.expect(refereeServiceMock.getById(1)).andReturn(referee);
        refereeServiceMock.save(referee);
        applicationsServiceMock.save(applicationForm);

        EasyMock.replay(refereeServiceMock, applicationsServiceMock, errors, encryptionHelperMock);
        String view = controller.editReferee("enc", referee, errors, modelMap);
        EasyMock.verify(refereeServiceMock, applicationsServiceMock, errors, encryptionHelperMock);

        assertEquals("redirect:/update/getReferee?applicationId=ABC", view);
    }

    @Test
    public void shouldSaveRefereeAndSendEmailIfApplicationInApprovalStageAndIfNoErrors() {
        ApplicationForm application = new ApplicationFormBuilder().id(5).applicationNumber("ABC").status(ApplicationFormStatus.APPROVAL).build();
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("applicationForm", application);
        Referee referee = new RefereeBuilder().id(1).application(application).build();
        application.setReferees(Arrays.asList(referee));
        BindingResult errors = EasyMock.createMock(BindingResult.class);

        EasyMock.expect(errors.hasErrors()).andReturn(false);
        EasyMock.expect(encryptionHelperMock.decryptToInteger("enc")).andReturn(1);
        EasyMock.expect(refereeServiceMock.getById(1)).andReturn(referee);
        refereeServiceMock.processRefereesRoles(application.getReferees());

        EasyMock.replay(refereeServiceMock, errors, encryptionHelperMock);
        String view = controller.editReferee("enc", referee, errors, modelMap);
        EasyMock.verify(refereeServiceMock, errors, encryptionHelperMock);

        assertEquals("redirect:/update/getReferee?applicationId=ABC", view);
    }

    @Test
    public void shouldNotSaveAndReturnToViewIfErrors() {
        ApplicationForm application = new ApplicationFormBuilder().id(5).applicationNumber("ABC").status(ApplicationFormStatus.APPROVAL).build();
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("applicationForm", application);
        Referee referee = new RefereeBuilder().id(1).application(new ApplicationFormBuilder().id(5).build()).build();
        BindingResult errors = EasyMock.createMock(BindingResult.class);

        EasyMock.expect(errors.hasErrors()).andReturn(true);
        EasyMock.expect(encryptionHelperMock.decryptToInteger("enc")).andReturn(1);
        EasyMock.expect(refereeServiceMock.getById(1)).andReturn(referee);

        EasyMock.replay(refereeServiceMock, errors, encryptionHelperMock);
        String view = controller.editReferee("enc", referee, errors, modelMap);
        EasyMock.verify(refereeServiceMock, errors, encryptionHelperMock);
        assertEquals("/private/pgStudents/form/components/references_details", view);
    }

    @Before
    public void setUp() {

        refereeServiceMock = EasyMock.createMock(RefereeService.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        domicilePropertyEditor = EasyMock.createMock(DomicilePropertyEditor.class);
        applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        refereeValidatorMock = EasyMock.createMock(RefereeValidator.class);
        applicationFormUserRoleServiceMock = EasyMock.createMock(ApplicationFormUserRoleService.class);
        domicileServiceMock = EasyMock.createMock(DomicileService.class);
        fullTextSearchServiceMock = EasyMock.createMock(FullTextSearchService.class);

        controller = new RefereeController(refereeServiceMock, userServiceMock, applicationsServiceMock, domicilePropertyEditor,
                applicationFormPropertyEditorMock, refereeValidatorMock, encryptionHelperMock, applicationFormUserRoleServiceMock, domicileServiceMock,
                fullTextSearchServiceMock);

        currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.APPLICANT).build()).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
        EasyMock.replay(userServiceMock);
    }

}
