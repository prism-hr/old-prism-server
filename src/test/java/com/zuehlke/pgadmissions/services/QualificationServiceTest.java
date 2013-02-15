package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;

public class QualificationServiceTest {

    private QualificationDAO qualificationDAOMock;

    private QualificationService qualificationService;

    @Test
    public void shouldDelegateGetQualificationToDAO() {
        Qualification qualification = new QualificationBuilder().id(2).build();
        EasyMock.expect(qualificationDAOMock.getQualificationById(2)).andReturn(qualification);
        EasyMock.replay(qualificationDAOMock);
        Qualification returnedQualification = qualificationService.getQualificationById(2);
        assertEquals(qualification, returnedQualification);
    }

    @Test
    public void shouldDelegateDeleteToDAO() {
        Qualification qualification = new QualificationBuilder().id(2).build();
        qualificationDAOMock.delete(qualification);
        EasyMock.replay(qualificationDAOMock);
        qualificationService.delete(qualification);
        EasyMock.verify(qualificationDAOMock);
    }

    @Test
    public void shouldDelegateSaveToDAO() {
        Qualification qualification = new QualificationBuilder().id(2).build();
        qualificationDAOMock.save(qualification);
        EasyMock.replay(qualificationDAOMock);
        qualificationService.save(qualification);
        EasyMock.verify(qualificationDAOMock);
    }

    @Test
    public void shouldSetFlagSendToPorticoOnSelectedQualifications() {
        ApplicationForm applicationFormMock = EasyMock.createMock(ApplicationForm.class);

        Qualification qualification1 = new QualificationBuilder().id(1).sendToUCL(true).build();
        Qualification qualification2 = new QualificationBuilder().id(2).sendToUCL(true).build();
        Qualification qualification3 = new QualificationBuilder().id(3).sendToUCL(false).build();
        Qualification qualification4 = new QualificationBuilder().id(4).sendToUCL(false).build();

        EasyMock.expect(applicationFormMock.getQualifications()).andReturn(Arrays.asList(qualification1, qualification2, qualification3, qualification4));
        EasyMock.expect(qualificationDAOMock.getQualificationById(3)).andReturn(qualification3);
        EasyMock.expect(qualificationDAOMock.getQualificationById(4)).andReturn(qualification4);

        EasyMock.replay(applicationFormMock, qualificationDAOMock);

        qualificationService.selectForSendingToPortico(applicationFormMock, Arrays.asList(new Integer[] { 3, 4 }));

        EasyMock.verify(applicationFormMock, qualificationDAOMock);

        assertTrue("SendToUcl flag has not been updated to true", qualification3.getSendToUCL());
        assertTrue("SendToUcl flag has not been updated to true", qualification4.getSendToUCL());
        assertFalse("SendToUcl flag has not been updated to false", qualification1.getSendToUCL());
        assertFalse("SendToUcl flag has not been updated to false", qualification2.getSendToUCL());
    }

    @Test
    public void shouldSetNoFlagSendToPorticoOnQualifications() {
        ApplicationForm applicationFormMock = EasyMock.createMock(ApplicationForm.class);

        Qualification qualification1 = new QualificationBuilder().id(1).sendToUCL(true).build();
        Qualification qualification2 = new QualificationBuilder().id(2).sendToUCL(true).build();
        Qualification qualification3 = new QualificationBuilder().id(3).sendToUCL(false).build();
        Qualification qualification4 = new QualificationBuilder().id(4).sendToUCL(false).build();

        EasyMock.expect(applicationFormMock.getQualifications()).andReturn(Arrays.asList(qualification1, qualification2, qualification3, qualification4));

        EasyMock.replay(applicationFormMock, qualificationDAOMock);

        qualificationService.selectForSendingToPortico(applicationFormMock, Collections.<Integer> emptyList());

        EasyMock.verify(applicationFormMock, qualificationDAOMock);

        assertFalse("SendToUcl flag has not been updated to false", qualification3.getSendToUCL());
        assertFalse("SendToUcl flag has not been updated to false", qualification4.getSendToUCL());
        assertFalse("SendToUcl flag has not been updated to false", qualification1.getSendToUCL());
        assertFalse("SendToUcl flag has not been updated to false", qualification2.getSendToUCL());
    }

    @Before
    public void setup() {
        qualificationDAOMock = EasyMock.createMock(QualificationDAO.class);
        qualificationService = new QualificationService(qualificationDAOMock);
    }
}
