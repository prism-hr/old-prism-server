package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.PersonalDetailDAO;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PassportInformation;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class PersonalDetailsServiceTest {

    @Mock
    @InjectIntoByType
    private PersonalDetailDAO personalDetailDAOMock;

    @TestedObject
    private PersonalDetailsService service;

    @Test
    public void shouldUserDAOToSavePersonalDetails() {

        PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).build();
        personalDetailDAOMock.save(personalDetails);

        replay();
        service.save(personalDetails);
        verify();
    }

    @Test
    public void shouldSetPassportInformationToNullIfNotAvailable() {
        PersonalDetails personalDetails = new PersonalDetailsBuilder().requiresVisa(false).passportAvailable(true)
                .passportInformation(new PassportInformation()).languageQualificationAvailable(false).languageQualification(new LanguageQualification())
                .build();

        personalDetailDAOMock.save(personalDetails);

        replay();
        service.save(personalDetails);
        verify();

        assertFalse(personalDetails.getPassportAvailable());
        assertNull(personalDetails.getPassportInformation());

        assertNull(personalDetails.getLanguageQualification());
    }
}
