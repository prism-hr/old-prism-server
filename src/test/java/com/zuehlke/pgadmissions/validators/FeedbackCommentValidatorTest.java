package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ScoreBuilder;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;

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
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "comment");
        reviewComment.setComment("");
        reviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
    }

    @Test
    public void shouldRejectIfNotDeclinedAndWillingToInterviewIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "willingToInterview");
        reviewComment.setWillingToInterview(null);
        reviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("willingToInterview").getCode());
    }

    @Test
    public void shouldRejectIfNotDeclinedAndSuitableCandidateForUclIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "suitableCandidateForUcl");
        reviewComment.setSuitableCandidateForUcl(null);
        reviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableCandidateForUcl").getCode());
    }

    @Test
    public void shouldRejectIfNotDeclinedAndSuitableCandidateForProgrammeIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "suitableCandidateForProgramme");
        reviewComment.setSuitableCandidateForProgramme(null);
        reviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableCandidateForProgramme").getCode());
    }

    @Test
    public void shouldNotRejectAnyEmptyFieldIfItIsDeclined() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "reviewComment");
        reviewComment.setDecline(true);
        reviewComment.setComment(null);
        reviewComment.setSuitableCandidateForUcl(null);
        reviewComment.setWillingToInterview(null);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldRejectIfNotDeclinedAndInterviewCommentIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "comment");
        interviewComment.setComment("");
        interviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
    }

    @Test
    public void shouldRejectInterviewCommentIfNotDeclinedAndWillingToSuperviseIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "willingToSupervise");
        interviewComment.setWillingToSupervise(null);
        interviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("willingToSupervise").getCode());
    }

    @Test
    public void shouldRejectInterviewCommentIfNotDeclinedAndSuitableCandidateForUclIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "suitableCandidateForUcl");
        interviewComment.setSuitableCandidateForUcl(null);
        interviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableCandidateForUcl").getCode());
    }

    @Test
    public void shouldRejectInterviewCommentIfNotDeclinedAndSuitableCandidateForProgrammeIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "suitableCandidateForProgramme");
        interviewComment.setSuitableCandidateForProgramme(null);
        interviewComment.setConfirmNextStage(true);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableCandidateForProgramme").getCode());
    }

    @Test
    public void shouldNotRejectInterviewCommentAnyEmptyFieldIfItIsDeclined() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "interviewComment");
        interviewComment.setDecline(true);
        interviewComment.setComment(null);
        interviewComment.setSuitableCandidateForUcl(null);
        interviewComment.setWillingToSupervise(null);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }
    
    @Test
    public void shouldRejectInterviewIfConfirmationCheckboxIsNotPresent() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "confirmNextStage");
        interviewComment.setConfirmNextStage(null);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }
    
    @Test
    public void shouldRejectInterviewIfConfirmationCheckboxIsNotChecked() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "confirmNextStage");
        interviewComment.setConfirmNextStage(false);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }

    @Test
    public void shouldRejectIfCommentIsMissing() {
        referenceComment.setComment(null);
        referenceComment.setConfirmNextStage(true);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "comment");
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
    }

    @Test
    public void shouldRejectIfSuitableForUCLIsNotSelected() {
        referenceComment.setSuitableForUCL(null);
        referenceComment.setConfirmNextStage(true);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "suitableForUCL");
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableForUCL").getCode());
    }

    @Test
    public void shouldRejectIfSuitableForProgrammeIsNotSelected() {
        referenceComment.setSuitableForProgramme(null);
        referenceComment.setConfirmNextStage(true);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "suitableForProgramme");
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableForProgramme").getCode());
    }
    
    @Test
    public void shouldRejectReferenceIfConfirmationCheckboxIsNotPresent() {
        referenceComment.setConfirmNextStage(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "confirmNextStage");
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }

    @Test
    public void shouldRejectReferenceIfConfirmationCheckboxIsNotChecked() {
        referenceComment.setConfirmNextStage(false);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "confirmNextStage");
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }

    @Test
    public void shouldRejectReviewIfConfirmationCheckboxIsNotPresent() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "confirmNextStage");
        reviewComment.setConfirmNextStage(null);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }

    @Test
    public void shouldRejectReviewIfConfirmationCheckboxIsNotChecked() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "confirmNextStage");
        reviewComment.setConfirmNextStage(false);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals("checkbox.mandatory", mappingResult.getFieldError("confirmNextStage").getCode());
    }

    @Before
    public void setup() {
        reviewComment = new ReviewCommentBuilder().comment("review comment").suitableCandidateForProgramme(false).suitableCandidateForUCL(false)
                .willingToInterview(true).decline(false).build();
        interviewComment = new InterviewCommentBuilder().comment("interview comment").suitableCandidateForUcl(false).suitableCandidateForProgramme(false)
                .willingToSupervise(true).decline(false).build();
        referenceComment = new ReferenceCommentBuilder().comment("reference comment").suitableForProgramme(false).suitableForUcl(false).build();

        feedbackCommentValidator = new FeedbackCommentValidator();
        feedbackCommentValidator.setValidator((javax.validation.Validator) validator);
    }
}
