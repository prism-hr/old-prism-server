package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LocalDatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.FundingService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.FundingValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class FundingControllerTest {

    @Mock
    @InjectIntoByType
    private LocalDatePropertyEditor datePropertyEditorMock;

    @Mock
    @InjectIntoByType
    private ApplicationService applicationsServiceMock;

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
    public void shouldReturnAllFundingTypes() {
        FundingType[] fundingTypes = controller.getFundingTypes();
        assertArrayEquals(fundingTypes, FundingType.values());
    }

    @Test
    public void shouldReturnApplicationForm() {
        Application applicationForm = new ApplicationFormBuilder().id(1).build();

        expect(applicationsServiceMock.getByApplicationNumber("1")).andReturn(applicationForm);

        replay();
        Application returnedApplicationForm = controller.getApplicationForm("1");
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
        binderMock.registerCustomEditor(Application.class, applicationFormPropertyEditorMock);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        replay();
        controller.registerPropertyEditors(binderMock);
    }

}
