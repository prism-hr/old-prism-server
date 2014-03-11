package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ApplicationFormTest {

    @Test
    public void shouldReturnReviewableFalseIfApplicationFormRejected() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).build();
        assertFalse(applicationForm.isModifiable());
    }

    @Test
    public void shouldReturnReviewableFalseIfApplicationFormAccepted() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).build();
        assertFalse(applicationForm.isModifiable());
    }

    @Test
    public void shouldReturnReviewableFalseIfApplicationFormAcceptedRejecteOrwitdrawn() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).build();
        assertTrue(applicationForm.isModifiable());
        applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(applicationForm.isModifiable());
        applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).build();
        assertFalse(applicationForm.isModifiable());
        applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).build();
        assertFalse(applicationForm.isModifiable());
        applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.WITHDRAWN).build();
        assertFalse(applicationForm.isModifiable());
    }

    @Test
    public void shouldReturnDecidedTrueIfRejectedOrApproved() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).build();
        assertTrue(applicationForm.isDecided());
        applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).build();
        assertTrue(applicationForm.isDecided());
    }

    @Test
    public void shouldReturnDecidedFalseIfNeitherUnsubmitterOrValidation() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).build();
        assertFalse(applicationForm.isDecided());
        applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(applicationForm.isDecided());
    }

    @Test
    public void shouldReturnTrueIfInStateByString() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).build();
        assertTrue(applicationForm.isInState("UNSUBMITTED"));
        assertFalse(applicationForm.isInState("VALIDATION"));
        assertFalse(applicationForm.isInState("BOB"));
    }

    @Test
    public void shouldHaveEnoughQualificationsToSendToPortico() {
        Document document1 = new DocumentBuilder().id(1).build();

        Qualification qualification1 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();
        Qualification qualification2 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).qualification(qualification1, qualification2).build();

        assertTrue(applicationForm.hasEnoughQualificationsToSendToPortico());
    }

    @Test
    public void shouldNotBeCompleteForSendingToPorticoWithNoQualifications() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();

        assertFalse(applicationForm.hasEnoughQualificationsToSendToPortico());
    }

    @Test
    public void shouldNotBeCompleteForSendingToPorticoWithMoreThanTwoQualifications() {
        Document document1 = new DocumentBuilder().id(1).build();

        Qualification qualification1 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();
        Qualification qualification2 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();
        Qualification qualification3 = new QualificationBuilder().id(1).sendToUCL(true).proofOfAward(document1).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).qualification(qualification1, qualification2, qualification3).build();

        assertFalse(applicationForm.hasEnoughQualificationsToSendToPortico());
    }

    @Test
    public void shouldGetProgramAndProjectTitle() {
        ApplicationForm application = new ApplicationFormBuilder().advert(new ProgramBuilder().title("Ppp").build()).projectTitle("Rrr").build();
        assertEquals("Ppp (project: Rrr)", application.getProgramAndProjectTitle());
    }

    @Test
    public void shouldGetProgramAndProjectTitleWhenProjectTitleIsNull() {
        ApplicationForm application = new ApplicationFormBuilder().advert(new ProgramBuilder().title("Ppp").build()).build();
        assertEquals("Ppp", application.getProgramAndProjectTitle());
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}