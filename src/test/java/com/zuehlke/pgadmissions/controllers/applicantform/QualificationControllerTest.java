package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.QualificationValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class QualificationControllerTest {

    @Mock
    @InjectIntoByType
    private ImportedEntityService importedEntityService;

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationsServiceMock;

    @Mock
    @InjectIntoByType
    private QualificationValidator qualificationValidatorMock;

    @Mock
    @InjectIntoByType
    private QualificationService qualificationServiceMock;

    @Mock
    @InjectIntoByType
    private FullTextSearchService fullTextSearchServiceMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private InstitutionDAO institutionDAOMock;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleServiceMock;

    @TestedObject
    private QualificationController controller;

    @Test
    public void shouldReturnAllLanguages() {
        List<Language> languageList = Arrays.asList(new LanguageBuilder().id(1).enabled(true).build(), new LanguageBuilder().id(2).enabled(false).build());
        EasyMock.expect(importedEntityService.getAllLanguages()).andReturn(Collections.singletonList(languageList.get(0)));

        replay();
        List<Language> allLanguages = controller.getAllEnabledLanguages();

        assertEquals(1, allLanguages.size());
        assertEquals(languageList.get(0), allLanguages.get(0));
    }

    @Test
    public void shouldReturnAllDomiciles() {
        List<Domicile> domicileList = Arrays.asList(new DomicileBuilder().id(1).enabled(true).build(), new DomicileBuilder().id(2).enabled(false).build());
        EasyMock.expect(importedEntityService.getAllDomiciles()).andReturn(Collections.singletonList(domicileList.get(0)));

        replay();

        List<Domicile> allDomiciles = controller.getAllEnabledDomiciles();
        assertEquals(1, allDomiciles.size());
        assertEquals(domicileList.get(0), allDomiciles.get(0));
    }

}