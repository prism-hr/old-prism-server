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

import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class FeedbackCommentValidatorTest {
    
    @Autowired
	private FeedbackCommentValidator validator;
	private ReviewComment reviewComment;
	private InterviewComment interviewComment;
	private ReferenceComment referenceComment;

	@Test
	public void shouldSupportComment() {
		assertTrue(validator.supports(ReviewComment.class));
		assertTrue(validator.supports(InterviewComment.class));
		assertTrue(validator.supports(ReferenceComment.class));
	}

	@Test
	public void shouldRejectIfNotDeclinedAndCommentIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "comment");
		reviewComment.setComment("");
		validator.validate(reviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectIfNotDeclinedAndWillingToInterviewIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "willingToInterview");
		reviewComment.setWillingToInterview(null);
		validator.validate(reviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("willingToInterview").getCode());
	}
	
	@Test
	public void shouldRejectIfNotDeclinedAndSuitableCandidateForUclIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "suitableCandidateForUcl");
		reviewComment.setSuitableCandidateForUcl(null);
		validator.validate(reviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableCandidateForUcl").getCode());
	}
	
	@Test
	public void shouldRejectIfNotDeclinedAndSuitableCandidateForProgrammeIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "suitableCandidateForProgramme");
		reviewComment.setSuitableCandidateForProgramme(null);
		validator.validate(reviewComment, mappingResult);
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
		validator.validate(reviewComment, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfNotDeclinedAndInterviewCommentIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "comment");
		interviewComment.setComment("");
		validator.validate(interviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectInterviewCommentIfNotDeclinedAndWillingToSuperviseIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "willingToSupervise");
		interviewComment.setWillingToSupervise(null);
		validator.validate(interviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("willingToSupervise").getCode());
	}
	
	@Test
	public void shouldRejectInterviewCommentIfNotDeclinedAndSuitableCandidateForUclIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "suitableCandidateForUcl");
		interviewComment.setSuitableCandidateForUcl(null);
		validator.validate(interviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableCandidateForUcl").getCode());
	}
	
	@Test
	public void shouldRejectInterviewCommentIfNotDeclinedAndSuitableCandidateForProgrammeIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "suitableCandidateForProgramme");
		interviewComment.setSuitableCandidateForProgramme(null);
		validator.validate(interviewComment, mappingResult);
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
		validator.validate(interviewComment, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfCommentIsMissing(){
		referenceComment.setComment(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "comment");
		validator.validate(referenceComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectIfSuitableForUCLIsNotSelected(){
		referenceComment.setSuitableForUCL(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "suitableForUCL");
		validator.validate(referenceComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableForUCL").getCode());
	}
	
	@Test
	public void shouldRejectIfSuitableForProgrammeIsNotSelected(){
		referenceComment.setSuitableForProgramme(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referenceComment, "suitableForProgramme");
		validator.validate(referenceComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableForProgramme").getCode());
	}
	
	@Before
	public void setup() {
		reviewComment = new ReviewCommentBuilder().comment("review comment").suitableCandidateForProgramme(false).suitableCandidateForUCL(false).willingToInterview(true).decline(false).toReviewComment();
		interviewComment = new InterviewCommentBuilder().comment("interview comment").suitableCandidateForUcl(false).suitableCandidateForProgramme(false).willingToSupervise(true).decline(false).toInterviewComment();
		referenceComment = new ReferenceCommentBuilder().comment("reference comment").suitableForProgramme(false).suitableForUcl(false).toReferenceComment();
	}
}
