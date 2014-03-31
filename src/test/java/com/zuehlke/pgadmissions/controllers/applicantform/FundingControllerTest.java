package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.util.Date;

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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.FundingBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.FundingService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FundingValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class FundingControllerTest {

    @Mock
    @InjectIntoByType
    private DatePropertyEditor datePropertyEditorMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationsServiceMock;

    @Mock
    @InjectIntoByType
    private FundingValidator fundingValidatorMock;

    @Mock
    @InjectIntoByType
    private FundingService fundingServiceMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;

    @Mock
    @InjectIntoByType
    private DocumentPropertyEditor documentPropertyEditorMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private EncryptionHelper encryptionHelperMock;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleServiceMock;

    @TestedObject
    private FundingController controller;

    @Test
    public void shouldReturnFundingViewForEdit() {
        Funding funding = new Funding();
        ModelMap modelMap = new ModelMap();
        replay();
        assertEquals("/private/pgStudents/form/components/funding_details", controller.getFundingView("24", modelMap));
        assertSame(funding, modelMap.get("funding"));
    }

	@Test
	public void shouldReturnApplicationForm() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
	
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser);
		ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
		assertEquals(applicationForm, returnedApplicationForm);
	}



    @Test
    public void shouldSaveQulificationAndRedirectIfNoErrors() {
        RegisteredUser user = new RegisteredUser();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").build();
        Funding funding = new FundingBuilder().id(1).application(applicationForm).build();
        BindingResult bindingResult = new BeanPropertyBindingResult(funding, "funding");
        ModelMap modelMap = new ModelMap();
        
        modelMap.put("applicationForm", applicationForm);
        
        
        expect(encryptionHelperMock.decryptToInteger("24")).andReturn(24);
        fundingServiceMock.save(5, 24, funding);
        applicationsServiceMock.save(applicationForm);
        expect(userServiceMock.getCurrentUser()).andReturn(user);
        applicationFormUserRoleServiceMock.insertApplicationUpdate(applicationForm, user, ApplicationUpdateScope.ALL_USERS);
        
        replay();
        String view = controller.editFunding("24", funding, bindingResult, modelMap);
        
        assertEquals("redirect:/update/getFunding?applicationId=ABC", view);
    }

    @Test
    public void shouldNotSaveAndReturnToViewIfErrors() {
        Funding funding = new FundingBuilder().id(1).application(new ApplicationFormBuilder().id(5).build()).build();
        BindingResult bindingResult = new BeanPropertyBindingResult(funding, "funding");
        bindingResult.reject("dupa");

        replay();
        String view = controller.editFunding(null, null, bindingResult, null);
        assertEquals("/private/pgStudents/form/components/funding_details", view);
    }

    @Test
    public void shouldReturnAllFundingTypes() {
        FundingType[] fundingTypes = controller.getFundingTypes();
        assertArrayEquals(fundingTypes, FundingType.values());
    }

    @Test
    public void shouldReturnApplicationForm() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();

        expect(applicationsServiceMock.getByApplicationNumber("1")).andReturn(applicationForm);

        replay();
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
        assertEquals(applicationForm, returnedApplicationForm);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getByApplicationNumber("1")).andReturn(null);

        replay();
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

        replay();
        controller.registerPropertyEditors(binderMock);
    }

}
