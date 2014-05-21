package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
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

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.validators.QualificationValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class QualificationControllerTest {

    @Mock
    @InjectIntoByType
    private ImportedEntityService importedEntityService;

    @Mock
    @InjectIntoByType
    private ApplicationService applicationsServiceMock;

    @Mock
    @InjectIntoByType
    private QualificationValidator qualificationValidatorMock;

    @Mock
    @InjectIntoByType
    private QualificationService qualificationServiceMock;

    @Mock
    @InjectIntoByType
    private FullTextSearchService fullTextSearchServiceMock;

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
        List<Domicile> domicileList = Lists.newArrayList();
        EasyMock.expect(importedEntityService.getAllDomiciles()).andReturn(domicileList);

        replay();

        List<Domicile> returned = controller.getAllEnabledDomiciles();
        assertSame(domicileList, returned);
    }

}