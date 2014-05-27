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
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;

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
        Qualification qualification = new Qualification().withId(2);

        expect(qualificationDAOMock.getById(2)).andReturn(qualification);

        replay();
        Qualification returnedQualification = service.getById(2);

        assertEquals(qualification, returnedQualification);
    }

    @Test
    public void shouldDelegateDeleteToDAO() {
        Qualification qualification = new Qualification().withId(2);
        qualificationDAOMock.delete(qualification);

        replay();
        service.delete(2);
    }

    @Test
    public void shouldSaveNewQualification() {
        Application applicationForm = new Application();
        Qualification qualification = new Qualification();

        replay();
        service.saveOrUpdate(applicationForm, null, qualification);

        assertSame(applicationForm, qualification.getApplication());
        assertThat(applicationForm.getQualifications(), contains(qualification));
    }

    @Test
    public void shouldUpdateExistingQualification() {
        Document existingDocument = new Document();
        Document document = new Document();

        Qualification existingQualification = new Qualification().withDocument(existingDocument);
        Qualification qualification = new Qualification().withDocument(document);

        documentServiceMock.replaceDocument(existingDocument, document);
        expect(qualificationDAOMock.getById(43)).andReturn(existingQualification);

        replay();
        service.saveOrUpdate(null, 43, qualification);
    }

    @Test
    public void shouldSetFlagSendToPorticoOnSelectedQualifications() {
        Qualification qualification1 = new Qualification().withId(1).withIncludeInExport(true);
        Qualification qualification2 = new Qualification().withId(2).withIncludeInExport(true);
        Qualification qualification3 = new Qualification().withId(3).withIncludeInExport(false);
        Qualification qualification4 = new Qualification().withId(4).withIncludeInExport(false);

        Application applicationForm = new ApplicationFormBuilder().qualifications(qualification1, qualification2, qualification3, qualification4).build();

        EasyMock.expect(qualificationDAOMock.getById(3)).andReturn(qualification3);
        EasyMock.expect(qualificationDAOMock.getById(4)).andReturn(qualification4);

        EasyMock.expect(qualificationDAOMock.getById(1)).andReturn(qualification1);
        EasyMock.expect(qualificationDAOMock.getById(2)).andReturn(qualification2);
        EasyMock.expect(qualificationDAOMock.getById(3)).andReturn(qualification3);
        EasyMock.expect(qualificationDAOMock.getById(4)).andReturn(qualification4);

        replay();
        service.selectForSendingToPortico(applicationForm, Arrays.asList(new Integer[] { 3, 4 }));

        assertTrue("SendToUcl flag has not been updated to true", qualification3.isIncludeInExport());
        assertTrue("SendToUcl flag has not been updated to true", qualification4.isIncludeInExport());
        assertFalse("SendToUcl flag has not been updated to false", qualification1.isIncludeInExport());
        assertFalse("SendToUcl flag has not been updated to false", qualification2.isIncludeInExport());
    }

    @Test
    public void shouldSetNoFlagSendToPorticoOnQualifications() {
        Qualification qualification1 = new Qualification().withId(1).withIncludeInExport(true);
        Qualification qualification2 = new Qualification().withId(2).withIncludeInExport(true);
        Qualification qualification3 = new Qualification().withId(3).withIncludeInExport(false);
        Qualification qualification4 = new Qualification().withId(4).withIncludeInExport(false);

        Application applicationForm = new ApplicationFormBuilder().qualifications(qualification1, qualification2, qualification3, qualification4).build();

        EasyMock.expect(qualificationDAOMock.getById(1)).andReturn(qualification1);
        EasyMock.expect(qualificationDAOMock.getById(2)).andReturn(qualification2);
        EasyMock.expect(qualificationDAOMock.getById(3)).andReturn(qualification3);
        EasyMock.expect(qualificationDAOMock.getById(4)).andReturn(qualification4);

        replay();
        service.selectForSendingToPortico(applicationForm, Collections.<Integer> emptyList());

        assertFalse("SendToUcl flag has not been updated to false", qualification3.isIncludeInExport());
        assertFalse("SendToUcl flag has not been updated to false", qualification4.isIncludeInExport());
        assertFalse("SendToUcl flag has not been updated to false", qualification1.isIncludeInExport());
        assertFalse("SendToUcl flag has not been updated to false", qualification2.isIncludeInExport());
    }

}
