package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertSame;
import static org.unitils.easymock.EasyMockUnitils.replay;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.ApplicationPassport;
import com.zuehlke.pgadmissions.domain.ApplicationPersonalDetails;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class PersonalDetailsServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationDAO applicationFormDAOMock;

    @Mock
    @InjectIntoByType
    private DocumentService documentService;

    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContext;

    @TestedObject
    private PersonalDetailsService service;

    @Test
    public void shouldSavePersonalDetailsWithEmptyDetails() {
        PersonalDetailsService thisBeanMock = EasyMockUnitils.createMock(PersonalDetailsService.class);

        Document oldQualificationDocument = new Document();
        Document newQualificationDocument = new Document();

        User applicant = new User();
        User newApplicant = new User();

        ApplicationPersonalDetails personalDetails = new ApplicationPersonalDetails().withLanguageQualification(new ApplicationLanguageQualification()
                .withProofOfAward(oldQualificationDocument));
        ApplicationPersonalDetails newPersonalDetails = new ApplicationPersonalDetails()
                .withLanguageQualification(new ApplicationLanguageQualification().withProofOfAward(newQualificationDocument)).withPassportAvailable(true)
                .withPassportInformation(new ApplicationPassport()).withId(1).withLanguageQualificationAvailable(null).withPassportAvailable(null);

        Application applicationForm = new ApplicationFormBuilder().personalDetails(personalDetails).applicant(applicant).build();

        expect(applicationContext.getBean(PersonalDetailsService.class)).andReturn(thisBeanMock);
        expect(applicationFormDAOMock.getById(84)).andReturn(applicationForm);
        documentService.replaceDocument(oldQualificationDocument, newQualificationDocument);
        applicationFormDAOMock.save(applicationForm);

        replay();
        service.saveOrUpdate(8, newPersonalDetails, newApplicant);

        assertSame(personalDetails, applicationForm.getPersonalDetails());
    }
}
