package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class FeedbackCommentValidatorTest {

    @Autowired
    private Validator validator;

    private FeedbackCommentValidator feedbackCommentValidator;

    private ReviewComment reviewComment;

    private InterviewComment interviewComment;

    private ReferenceComment referenceComment;

    @Test
    public void shouldSupportComment() {
        assertTrue(feedbackCommentValidator.supports(ReviewComment.class));
        assertTrue(feedbackCommentValidator.supports(InterviewComment.class));
        assertTrue(feedbackCommentValidator.supports(ReferenceComment.class));
    }

    @Test
    public void shouldRejectIfNotDeclinedAndCommentIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "reviewComment");
        reviewComment.setContent("");
        reviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
    }

    @Test
    public void shouldRejectIfNotDeclinedAndWillingToInterviewIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "reviewComment");
        reviewComment.setWillingToInterview(null);
        reviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("willingToInterview").getCode());
    }

    @Test
    public void shouldRejectIfNotDeclinedAndWillingToWorkWithApplicantIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "reviewComment");
        reviewComment.setWillingToSupervise(null);
        reviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("willingToWorkWithApplicant").getCode());
    }
    
    @Test
    public void shouldRejectIfNotDeclinedAndSuitableCandidateForUclIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "reviewComment");
        reviewComment.setSuitableForInstitution(null);
        reviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableCandidateForUcl").getCode());
    }

    @Test
    public void shouldRejectIfNotDeclinedAndSuitableCandidateForProgrammeIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "reviewComment");
        reviewComment.setSuitableForProgramme(null);
        reviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableCandidateForProgramme").getCode());
    }

    @Test
    public void shouldNotRejectAnyEmptyFieldIfItIsDeclined() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "reviewComment");
        reviewComment.setDeclined(true);
        reviewComment.setContent(null);
        reviewComment.setSuitableForInstitution(null);
        reviewComment.setWillingToInterview(null);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldRejectIfNotDeclinedAndInterviewCommentIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "interviewComment");
        interviewComment.setContent("");
        interviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
    }

    @Test
    public void shouldRejectInterviewCommentIfNotDeclinedAndWillingToSuperviseIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "interviewComment");
        interviewComment.setWillingToSupervise(null);
        interviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("willingToSupervise").getCode());
    }

    @Test
    public void shouldRejectInterviewCommentIfNotDeclinedAndSuitableCandidateForUclIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "interviewComment");
        interviewComment.setSuitableForInstitution(null);
        interviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableCandidateForUcl").getCode());
    }

    @Test
    public void shouldRejectInterviewCommentIfNotDeclinedAndSuitableCandidateForProgrammeIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "interviewComment");
        interviewComment.setSuitableForProgramme(null);
        interviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableCandidateForProgramme").getCode());
    }

    @Test
    public void shouldNotRejectInterviewCommentAnyEmptyFieldIfItIsDeclined() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "interviewComment");
        interviewComment.setDeclined(true);
        interviewComment.setContent(null);
        interviewComment.setSuitableForInstitution(null);
        interviewComment.setWillingToSupervise(null);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }
    
    @Test
    public void shouldRejectInterviewIfConfirmationCheckboxIsNotPresent() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "interviewComment");
        interviewComment.setConfirmNextStage(null);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }
    
    
    @Test
    public void shouldRejectInterviewIfApplicantRatingNotProvided() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "interviewComment");
        interviewComment.setRating(null);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("applicantRating").getCode());
    }
    
    @Test
    public void shouldRejectInterviewIfConfirmationCheckboxIsNotChecked() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "interviewComment");
        interviewComment.setConfirmNextStage(false);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }

    @Test
    public void shouldRejectIfCommentIsMissing() {
        referenceComment.setContent(null);
        referenceComment.setConfirmNextStage(true);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "comment");
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
    }

    @Test
    public void shouldRejectIfSuitableForUCLIsNotSelected() {
        referenceComment.setSuitableForInstitution(null);
        referenceComment.setConfirmNextStage(true);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "referenceComment");
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableForUCL").getCode());
    }

    @Test
    public void shouldRejectIfSuitableForProgrammeIsNotSelected() {
        referenceComment.setSuitableForProgramme(null);
        referenceComment.setConfirmNextStage(true);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "referenceComment");
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableForProgramme").getCode());
    }
    
    @Test
    public void shouldRejectReferenceIfConfirmationCheckboxIsNotPresent() {
        referenceComment.setConfirmNextStage(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "referenceComment");
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }

    @Test
    public void shouldRejectReferenceIfConfirmationCheckboxIsNotChecked() {
        referenceComment.setConfirmNextStage(false);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "referenceComment");
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }
    
    @Test
    public void shouldRejectReferenceIfApplicantRatingNotProvided() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "reviewComment");
        referenceComment.setRating(null);
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("applicantRating").getCode());
    }

    @Test
    public void shouldRejectReviewIfConfirmationCheckboxIsNotPresent() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "reviewComment");
        reviewComment.setConfirmNextStage(null);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }

    @Test
    public void shouldRejectReviewIfConfirmationCheckboxIsNotChecked() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "reviewComment");
        reviewComment.setConfirmNextStage(false);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }

    @Test
    public void shouldRejectReviewIfApplicantRatingNotProvided() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "reviewComment");
        reviewComment.setRating(null);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("applicantRating").getCode());
    }

    @Before
    public void setup() {
        ApplicationForm application = new ApplicationFormBuilder().useCustomReferenceQuestions(false).build();
        reviewComment = new ReviewCommentBuilder().content("review comment").application(application).suitableCandidateForProgramme(false).suitableCandidateForUCL(false).willingToInterview(true).willingToWorkWithApplicant(false).decline(false).applicantRating(3).build();
        interviewComment = new InterviewCommentBuilder().content("interview comment").application(application).suitableCandidateForUcl(false).suitableCandidateForProgramme(false).willingToSupervise(true).decline(false).applicantRating(2).build();
        referenceComment = new ReferenceCommentBuilder().comment("reference comment").application(application).suitableForProgramme(false).suitableForUcl(false).applicantRating(4).build();

        feedbackCommentValidator = new FeedbackCommentValidator();
        feedbackCommentValidator.setValidator((javax.validation.Validator) validator);
    }
}
