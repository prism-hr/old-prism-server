package com.zuehlke.pgadmissions.controllers.applicantform;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.validators.RefereeValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class RefereeControllerTest {

    @Mock
    @InjectIntoByType
    private RefereeService refereeService;

    @Mock
    @InjectIntoByType
    private ImportedEntityService importedEntityService;

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationsService;

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

}
