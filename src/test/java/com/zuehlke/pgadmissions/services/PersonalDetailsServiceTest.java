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

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.PassportInformation;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageQualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class PersonalDetailsServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormDAO applicationFormDAOMock;

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

        RegisteredUser applicant = new RegisteredUser();
        RegisteredUser newApplicant = new RegisteredUser();

        PersonalDetails personalDetails = new PersonalDetailsBuilder().languageQualification(
                new LanguageQualificationBuilder().languageQualificationDocument(oldQualificationDocument).build()).build();
        PersonalDetails newPersonalDetails = new PersonalDetailsBuilder()
                .languageQualification(new LanguageQualificationBuilder().languageQualificationDocument(newQualificationDocument).build())
                .passportAvailable(true).passportInformation(new PassportInformation()).id(1).languageQualificationAvailable(null).passportAvailable(null)
                .build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().personalDetails(personalDetails).applicant(applicant).build();

        expect(applicationContext.getBean(PersonalDetailsService.class)).andReturn(thisBeanMock);
        expect(applicationFormDAOMock.get(84)).andReturn(applicationForm);
        documentService.documentReferentialityChanged(oldQualificationDocument, newQualificationDocument);
        thisBeanMock.copyPersonalDetails(personalDetails, newPersonalDetails);
        thisBeanMock.copyApplicantData(applicant, newApplicant);
        applicationFormDAOMock.save(applicationForm);

        replay();
        service.save(84, newPersonalDetails, newApplicant);

        assertSame(personalDetails, applicationForm.getPersonalDetails());
    }
}
