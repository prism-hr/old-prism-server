package com.zuehlke.pgadmissions.controllers.applicantform;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LocalDatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.EmploymentPositionService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.validators.EmploymentPositionValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class EmploymentPositionControllerTest {

    @Mock
    @InjectIntoByType
    private EmploymentPositionService employmentPositionService;

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationFormService;

    @Mock
    @InjectIntoByType
    private LocalDatePropertyEditor datePropertyEditor;

    @Mock
    @InjectIntoByType
    private EmploymentPositionValidator employmentPositionValidator;

    @Mock
    @InjectIntoByType
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Mock
    @InjectIntoByType
    private ImportedEntityService importedEntityService;

    @Mock
    @InjectIntoByType
    private FullTextSearchService searchService;

    @TestedObject
    private EmploymentPositionController controller;

}