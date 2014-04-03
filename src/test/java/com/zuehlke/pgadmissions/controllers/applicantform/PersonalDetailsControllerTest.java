package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DisabilityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.EthnicityPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LanguagePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.DisabilityService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.EthnicityService;
import com.zuehlke.pgadmissions.services.LanguageService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.validators.PersonalDetailsUserValidator;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;

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
    private CountryService countryService;

    @Mock
    @InjectIntoByType
    private DomicileService domicileService;

    @Mock
    @InjectIntoByType
    private EthnicityService ethnicityService;

    @Mock
    @InjectIntoByType
    private DisabilityService disabilityService;

    @Mock
    @InjectIntoByType
    private LanguageService languageService;

    @Mock
    @InjectIntoByType
    private LanguagePropertyEditor languagePropertyEditor;

    @Mock
    @InjectIntoByType
    private CountryPropertyEditor countryPropertyEditor;

    @Mock
    @InjectIntoByType
    private DisabilityPropertyEditor disabilityPropertyEditor;

    @Mock
    @InjectIntoByType
    private EthnicityPropertyEditor ethnicityPropertyEditor;

    @Mock
    @InjectIntoByType
    private PersonalDetailsValidator personalDetailsValidator;

    @Mock
    @InjectIntoByType
    private DomicilePropertyEditor domicilePropertyEditor;

    @Mock
    @InjectIntoByType
    private DocumentPropertyEditor documentPropertyEditor;

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
        EasyMock.expect(languageService.getAllEnabledLanguages()).andReturn(Collections.singletonList(languageList.get(0)));
        EasyMock.replay(languageService);
        List<Language> allLanguages = controller.getAllEnabledLanguages();
        assertEquals(1, allLanguages.size());
        assertEquals(languageList.get(0), allLanguages.get(0));
    }

    @Test
    public void shouldReturnAllEnabledCountries() {
        List<Country> countryList = Arrays.asList(new CountryBuilder().id(1).enabled(true).build(), new CountryBuilder().id(2).enabled(false).build());
        EasyMock.expect(countryService.getAllEnabledCountries()).andReturn(Collections.singletonList(countryList.get(0)));
        EasyMock.replay(countryService);
        List<Country> allCountries = controller.getAllEnabledCountries();
        assertEquals(1, allCountries.size());
        assertEquals(countryList.get(0), allCountries.get(0));
    }

    @Test
    public void returnAllEnabledEthnicities() {
        List<Ethnicity> ethnicityList = Arrays.asList(new EthnicityBuilder().id(1).enabled(true).build(), new EthnicityBuilder().id(2).enabled(false).build());
        EasyMock.expect(ethnicityService.getAllEnabledEthnicities()).andReturn(Collections.singletonList(ethnicityList.get(0)));
        EasyMock.replay(ethnicityService);
        List<Ethnicity> allEthnicities = controller.getAllEnabledEthnicities();
        assertEquals(1, allEthnicities.size());
        assertEquals(ethnicityList.get(0), allEthnicities.get(0));
    }

    @Test
    public void returnAllEnabledDisabilities() {
        List<Disability> disabilityList = Arrays.asList(new DisabilityBuilder().id(1).enabled(true).build(), new DisabilityBuilder().id(2).enabled(false)
                .build());
        EasyMock.expect(disabilityService.getAllEnabledDisabilities()).andReturn(Collections.singletonList(disabilityList.get(0)));
        EasyMock.replay(disabilityService);
        List<Disability> allDisabilities = controller.getAllEnabledDisabilities();
        assertEquals(1, allDisabilities.size());
        assertEquals(disabilityList.get(0), allDisabilities.get(0));
    }

    @Test
    public void shouldReturnAllGenders() {
        Gender[] genders = controller.getGenders();
        assertArrayEquals(genders, Gender.values());
    }

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(personalDetailsValidator);
        binderMock.registerCustomEditor(Date.class, datePropertyEditor);
        binderMock.registerCustomEditor(Language.class, languagePropertyEditor);
        binderMock.registerCustomEditor(Country.class, countryPropertyEditor);
        binderMock.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binderMock.registerCustomEditor(Ethnicity.class, ethnicityPropertyEditor);
        binderMock.registerCustomEditor(Disability.class, disabilityPropertyEditor);
        binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditor);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.eq("firstNationality"), EasyMock.anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.eq("secondNationality"), EasyMock.anyObject(StringTrimmerEditor.class));
        EasyMock.replay(binderMock);
        controller.registerPropertyEditorsForPersonalDetails(binderMock);
        EasyMock.verify(binderMock);
    }


}