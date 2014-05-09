package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertArrayEquals;
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
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.validators.PersonalDetailsUserValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class PersonalDetailsControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationFormService;

    @Mock
    @InjectIntoByType
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Mock
    @InjectIntoByType
    private DatePropertyEditor datePropertyEditor;

    @Mock
    @InjectIntoByType
    private ImportedEntityService importedEntityService;

    @Mock
    @InjectIntoByType
    private PersonalDetailsUserValidator personalDetailsUserValidator;

    @Mock
    @InjectIntoByType
    private PersonalDetailsService personalDetailsService;

    @TestedObject
    private PersonalDetailsController controller;

    @Test
    public void shouldReturnAllEnabledLanguages() {
        List<Language> languageList = Arrays.asList(new LanguageBuilder().id(1).enabled(true).build(), new LanguageBuilder().id(2).enabled(false).build());
        EasyMock.expect(importedEntityService.getAllLanguages()).andReturn(Collections.singletonList(languageList.get(0)));

        replay();

        List<Language> allLanguages = controller.getAllEnabledLanguages();
        assertEquals(1, allLanguages.size());
        assertEquals(languageList.get(0), allLanguages.get(0));
    }

    @Test
    public void shouldReturnAllEnabledCountries() {
        List<Country> countryList = Lists.newArrayList();
        EasyMock.expect(importedEntityService.getAllCountries()).andReturn(countryList);

        replay();
        
        List<Country> returned = controller.getAllEnabledCountries();
        assertSame(countryList, returned);
    }

    @Test
    public void returnAllEnabledEthnicities() {
        List<Ethnicity> ethnicityList = Arrays.asList(new EthnicityBuilder().id(1).enabled(true).build(), new EthnicityBuilder().id(2).enabled(false).build());
        EasyMock.expect(importedEntityService.getAllEthnicities()).andReturn(Collections.singletonList(ethnicityList.get(0)));

        replay();
        
        List<Ethnicity> allEthnicities = controller.getAllEnabledEthnicities();
        assertEquals(1, allEthnicities.size());
        assertEquals(ethnicityList.get(0), allEthnicities.get(0));
    }

    @Test
    public void returnAllEnabledDisabilities() {
        List<Disability> disabilityList = Lists.newArrayList();
        EasyMock.expect(importedEntityService.getAllDisabilities()).andReturn(disabilityList);

        replay();
        
        List<Disability> returned = controller.getAllEnabledDisabilities();
        assertSame(disabilityList, returned);
    }

    @Test
    public void shouldReturnAllGenders() {
        Gender[] genders = controller.getGenders();
        assertArrayEquals(genders, Gender.values());
    }

}