package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PassportInformation;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class PersonalDetailsServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormDAO applicationFormDAOMock;

    @TestedObject
    private PersonalDetailsService service;

    @Test
    public void shouldSavePersonalDetailsWithEmptyDetails() {
        PersonalDetails personalDetails = new PersonalDetails();
        ApplicationForm applicationForm = new ApplicationFormBuilder().personalDetails(personalDetails).build();
        PersonalDetails newPersonalDetails = new PersonalDetailsBuilder().languageQualification(new LanguageQualification()).passportAvailable(true)
                .passportInformation(new PassportInformation()).id(1).languageQualificationAvailable(null).passportAvailable(null).build();

        applicationFormDAOMock.save(applicationForm);

        replay();
        service.save(applicationForm, newPersonalDetails);
        verify();

        assertSame(personalDetails, applicationForm.getPersonalDetails());
        assertNull(personalDetails.getLanguageQualification());
        assertFalse(personalDetails.getPassportAvailable());
        assertNull(personalDetails.getLanguageQualification());
    }
}
