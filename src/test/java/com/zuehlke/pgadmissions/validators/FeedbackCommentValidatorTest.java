package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ScoreBuilder;
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
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
    }

    @Test
    public void shouldRejectIfNotDeclinedAndWillingToInterviewIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "willingToInterview");
        reviewComment.setWillingToInterview(null);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("willingToInterview").getCode());
    }

    @Test
    public void shouldRejectIfNotDeclinedAndSuitableCandidateForUclIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "suitableCandidateForUcl");
        reviewComment.setSuitableCandidateForUcl(null);
        feedbackCommentValidator.validate(reviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableCandidateForUcl").getCode());
    }

    @Test
    public void shouldRejectIfNotDeclinedAndSuitableCandidateForProgrammeIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "suitableCandidateForProgramme");
        reviewComment.setSuitableCandidateForProgramme(null);
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
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
    }

    @Test
    public void shouldRejectInterviewCommentIfNotDeclinedAndWillingToSuperviseIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "willingToSupervise");
        interviewComment.setWillingToSupervise(null);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("willingToSupervise").getCode());
    }

    @Test
    public void shouldRejectInterviewCommentIfNotDeclinedAndSuitableCandidateForUclIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "suitableCandidateForUcl");
        interviewComment.setSuitableCandidateForUcl(null);
        feedbackCommentValidator.validate(interviewComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableCandidateForUcl").getCode());
    }

    @Test
    public void shouldRejectInterviewCommentIfNotDeclinedAndSuitableCandidateForProgrammeIsEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "suitableCandidateForProgramme");
        interviewComment.setSuitableCandidateForProgramme(null);
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
    public void shouldRejectIfCommentIsMissing() {
        referenceComment.setComment(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "comment");
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
    }

    @Test
    public void shouldRejectIfSuitableForUCLIsNotSelected() {
        referenceComment.setSuitableForUCL(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "suitableForUCL");
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableForUCL").getCode());
    }

    @Test
    public void shouldRejectIfSuitableForProgrammeIsNotSelected() {
        referenceComment.setSuitableForProgramme(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "suitableForProgramme");
        feedbackCommentValidator.validate(referenceComment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableForProgramme").getCode());
    }

    @Test
    public void shouldRequiredScoreIsEmpty() {
        Score score1 = new ScoreBuilder().required(false).questionType(QuestionType.TEXT).build();
        Score score2 = new ScoreBuilder().required(true).questionType(QuestionType.TEXT).build();
        Comment comment = new CommentBuilder().scores(score1, score2).build();
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(comment, "comment");
        feedbackCommentValidator.validate(comment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("scores[1].textResponse").getCode());
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
