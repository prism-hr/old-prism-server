package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.util.Arrays;
import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class QualificationServiceTest {

    @Mock
    @InjectIntoByType
    private QualificationDAO qualificationDAOMock;

    @Mock
    @InjectIntoByType
    private DocumentService documentServiceMock;

    @TestedObject
    private QualificationService service;

    @Test
    public void shouldDelegateGetQualificationToDAO() {
        Qualification qualification = new QualificationBuilder().id(2).build();

        expect(qualificationDAOMock.getQualificationById(2)).andReturn(qualification);

        replay();
        Qualification returnedQualification = service.getQualificationById(2);

        assertEquals(qualification, returnedQualification);
    }

    @Test
    public void shouldDelegateDeleteToDAO() {
        Qualification qualification = new QualificationBuilder().id(2).build();
        qualificationDAOMock.delete(qualification);

        replay();
        service.delete(qualification);
    }

    @Test
    public void shouldSaveNewQualification() {
        ApplicationForm applicationForm = new ApplicationForm();
        Qualification qualification = new Qualification();
        qualificationDAOMock.save(qualification);

        replay();
        service.save(applicationForm, null, qualification);

        assertSame(applicationForm, qualification.getApplication());
        assertThat(applicationForm.getQualifications(), contains(qualification));
    }

    @Test
    public void shouldUpdateExistingQualification() {
        Document existingDocument = new Document();
        Document document = new Document();

        Qualification existingQualification = new QualificationBuilder().proofOfAward(existingDocument).build();
        Qualification qualification = new QualificationBuilder().proofOfAward(document).build();

        documentServiceMock.documentReferentialityChanged(existingDocument, document);
        expect(qualificationDAOMock.getQualificationById(43)).andReturn(existingQualification);

        replay();
        service.save(null, 43, qualification);
    }

    @Test
    public void shouldSetFlagSendToPorticoOnSelectedQualifications() {
        Qualification qualification1 = new QualificationBuilder().id(1).sendToUCL(true).build();
        Qualification qualification2 = new QualificationBuilder().id(2).sendToUCL(true).build();
        Qualification qualification3 = new QualificationBuilder().id(3).sendToUCL(false).build();
        Qualification qualification4 = new QualificationBuilder().id(4).sendToUCL(false).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().qualifications(qualification1, qualification2, qualification3, qualification4).build();

        EasyMock.expect(qualificationDAOMock.getQualificationById(3)).andReturn(qualification3);
        EasyMock.expect(qualificationDAOMock.getQualificationById(4)).andReturn(qualification4);

        EasyMock.expect(qualificationDAOMock.getQualificationById(1)).andReturn(qualification1);
        EasyMock.expect(qualificationDAOMock.getQualificationById(2)).andReturn(qualification2);
        EasyMock.expect(qualificationDAOMock.getQualificationById(3)).andReturn(qualification3);
        EasyMock.expect(qualificationDAOMock.getQualificationById(4)).andReturn(qualification4);

        replay();
        service.selectForSendingToPortico(applicationForm, Arrays.asList(new Integer[] { 3, 4 }));

        assertTrue("SendToUcl flag has not been updated to true", qualification3.getSendToUCL());
        assertTrue("SendToUcl flag has not been updated to true", qualification4.getSendToUCL());
        assertFalse("SendToUcl flag has not been updated to false", qualification1.getSendToUCL());
        assertFalse("SendToUcl flag has not been updated to false", qualification2.getSendToUCL());
    }

    @Test
    public void shouldSetNoFlagSendToPorticoOnQualifications() {
        Qualification qualification1 = new QualificationBuilder().id(1).sendToUCL(true).build();
        Qualification qualification2 = new QualificationBuilder().id(2).sendToUCL(true).build();
        Qualification qualification3 = new QualificationBuilder().id(3).sendToUCL(false).build();
        Qualification qualification4 = new QualificationBuilder().id(4).sendToUCL(false).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().qualifications(qualification1, qualification2, qualification3, qualification4).build();

        EasyMock.expect(qualificationDAOMock.getQualificationById(1)).andReturn(qualification1);
        EasyMock.expect(qualificationDAOMock.getQualificationById(2)).andReturn(qualification2);
        EasyMock.expect(qualificationDAOMock.getQualificationById(3)).andReturn(qualification3);
        EasyMock.expect(qualificationDAOMock.getQualificationById(4)).andReturn(qualification4);

        replay();
        service.selectForSendingToPortico(applicationForm, Collections.<Integer> emptyList());

        assertFalse("SendToUcl flag has not been updated to false", qualification3.getSendToUCL());
        assertFalse("SendToUcl flag has not been updated to false", qualification4.getSendToUCL());
        assertFalse("SendToUcl flag has not been updated to false", qualification1.getSendToUCL());
        assertFalse("SendToUcl flag has not been updated to false", qualification2.getSendToUCL());
    }

}
