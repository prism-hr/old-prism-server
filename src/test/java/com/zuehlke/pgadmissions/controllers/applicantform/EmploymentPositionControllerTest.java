package com.zuehlke.pgadmissions.controllers.applicantform;

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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.EmploymentPositionService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.validators.EmploymentPositionValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class EmploymentPositionControllerTest {

    @Mock
    @InjectIntoByType
    private EmploymentPositionService employmentPositionService;

    @Mock
    @InjectIntoByType
    private LanguageService languageService;

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationFormService;

    @Mock
    @InjectIntoByType
    private LanguagePropertyEditor languagePropertyEditor;

    @Mock
    @InjectIntoByType
    private DatePropertyEditor datePropertyEditor;

    @Mock
    @InjectIntoByType
    private EmploymentPositionValidator employmentPositionValidator;

    @Mock
    @InjectIntoByType
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Mock
    @InjectIntoByType
    private DomicileService domicileService;

    @Mock
    @InjectIntoByType
    private DomicilePropertyEditor domicilePropertyEditor;

    @Mock
    @InjectIntoByType
    private FullTextSearchService searchService;

    @TestedObject
    private EmploymentPositionController controller;

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(employmentPositionValidator);
        binderMock.registerCustomEditor(Date.class, datePropertyEditor);
        binderMock.registerCustomEditor(Language.class, languagePropertyEditor);
        binderMock.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);
        EasyMock.verify(binderMock);
    }
}