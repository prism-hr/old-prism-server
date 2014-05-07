package com.zuehlke.pgadmissions.controllers.applicantform;

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
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.validators.RefereeValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class RefereeControllerTest {

    @Mock
    @InjectIntoByType
    private RefereeService refereeService;

    @Mock
    @InjectIntoByType
    private DomicileService domicileService;

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationsService;

    @Mock
    @InjectIntoByType
    private DomicilePropertyEditor domicilePropertyEditor;

    @Mock
    @InjectIntoByType
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Mock
    @InjectIntoByType
    private RefereeValidator refereeValidator;

    @Mock
    @InjectIntoByType
    private EncryptionHelper encryptionHelper;

    @Mock
    @InjectIntoByType
    private FullTextSearchService searchService;

    @TestedObject
    private RefereeController controller;

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(refereeValidator);
        binderMock.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);
        EasyMock.verify(binderMock);
    }

}
