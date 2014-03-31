package com.zuehlke.pgadmissions.controllers.applicantform;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.BooleanPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.AdditionalInformationValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class AdditionalInformationControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationServiceMock;

    @Mock
    @InjectIntoByType
    private AdditionalInformationValidator validatorMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormPropertyEditor applFormPropertyEditorMock;

    @Mock
    @InjectIntoByType
    private BooleanPropertyEditor booleanPropertyEditorMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleServiceMock;

    @TestedObject
    private AdditionalInformationController controller;

    @Test
    public void shouldReturnApplicationFormView() {
        Assert.assertEquals("/private/pgStudents/form/components/additional_information",
                controller.getAdditionalInformationView(new ApplicationForm(), new ExtendedModelMap()));
    }

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(validatorMock);
        binderMock.registerCustomEditor(ApplicationForm.class, applFormPropertyEditorMock);
        binderMock.registerCustomEditor(Boolean.class, booleanPropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        EasyMock.replay(binderMock);
        controller.registerValidatorsEditors(binderMock);
        EasyMock.verify(binderMock);
    }

}