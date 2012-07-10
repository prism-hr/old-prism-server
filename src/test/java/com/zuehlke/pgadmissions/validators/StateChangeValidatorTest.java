package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.builders.ValidationCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;

public class StateChangeValidatorTest {
	private StateChangeValidator validator;
	private ValidationComment validationComment;
	private StateChangeComment stateChangeComment;
//	private ReviewEvaluationComment reviewEvaluationComment;
//	private InterviewEvaluationComment interviewEvaluationComment;
//	private ApprovalEvaluationComment approvalEvaluationComment;

	@Test
	public void shouldSupportComment() {
		assertTrue(validator.supports(ValidationComment.class));
		assertTrue(validator.supports(StateChangeComment.class));
//		assertTrue(validator.supports(ReviewEvaluationComment.class));
//		assertTrue(validator.supports(InterviewEvaluationComment.class));
//		assertTrue(validator.supports(ApprovalEvaluationComment.class));
	}

	@Test
	public void shouldRejectIfNoCommentInValidationComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(validationComment, "comment");
		validationComment.setComment("");
		validator.validate(validationComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectIfNoEnglishCompetencyInValidationComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(validationComment, "englishCompentencyOk");
		validationComment.setEnglishCompentencyOk(null);
		validator.validate(validationComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("englishCompentencyOk").getCode());
	}
	
	@Test
	public void shouldRejectIfNoHomeOrOverseasInValidationComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(validationComment, "homeOrOverseas");
		validationComment.setHomeOrOverseas(null);
		validator.validate(validationComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("homeOrOverseas").getCode());
	}
	
	@Test
	public void shouldRejectIfNoQualifiedForPhdInValidationComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(validationComment, "qualifiedForPhd");
		validationComment.setQualifiedForPhd(null);
		validator.validate(validationComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("qualifiedForPhd").getCode());
	}
	
	@Test
	public void shouldRejectIfNoNextStatusInValidationComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(validationComment, "nextStatus");
		validationComment.setNextStatus(null);
		validator.validate(validationComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("nextStatus").getCode());
	}
	
	@Test
	public void shouldRejectIfNoCommentInStateChangeComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeComment, "comment");
		stateChangeComment.setComment("");
		validator.validate(stateChangeComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectIfNoNextStatusInStateChangeComment() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(stateChangeComment, "nextStatus");
		stateChangeComment.setNextStatus(null);
		validator.validate(stateChangeComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("nextStatus").getCode());
	}
	
//	@Test
//	public void shouldRejectIfNoCommentInInterviewEvaluationComment() {
//		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewEvaluationComment, "comment");
//		interviewEvaluationComment.setComment("");
//		validator.validate(interviewEvaluationComment, mappingResult);
//		Assert.assertEquals(1, mappingResult.getErrorCount());
//		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
//	}
//	
//	@Test
//	public void shouldRejectIfNoNextStatusInInterviewEvaluationComment() {
//		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewEvaluationComment, "nextStatus");
//		interviewEvaluationComment.setNextStatus(null);
//		validator.validate(interviewEvaluationComment, mappingResult);
//		Assert.assertEquals(1, mappingResult.getErrorCount());
//		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("nextStatus").getCode());
//	}
//	
//	@Test
//	public void shouldRejectIfNoCommentInApprovalEvaluationComment() {
//		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalEvaluationComment, "comment");
//		approvalEvaluationComment.setComment("");
//		validator.validate(approvalEvaluationComment, mappingResult);
//		Assert.assertEquals(1, mappingResult.getErrorCount());
//		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
//	}
//	
//	@Test
//	public void shouldRejectIfNoNextStatusInApprovalEvaluationComment() {
//		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalEvaluationComment, "nextStatus");
//		approvalEvaluationComment.setNextStatus(null);
//		validator.validate(approvalEvaluationComment, mappingResult);
//		Assert.assertEquals(1, mappingResult.getErrorCount());
//		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("nextStatus").getCode());
//	}
	
	
	@Before
	public void setup() {
		validator = new StateChangeValidator();
		validationComment = new ValidationCommentBuilder().comment("validation comment").nextStatus(ApplicationFormStatus.APPROVAL).englishCompentencyOk(ValidationQuestionOptions.YES).qualifiedForPhd(ValidationQuestionOptions.YES).homeOrOverseas(HomeOrOverseas.HOME).toValidationComment();
		stateChangeComment = new StateChangeComment();
		stateChangeComment.setComment("comment");
		stateChangeComment.setNextStatus(ApplicationFormStatus.APPROVAL);
		//		reviewEvaluationComment = new ReviewEvaluationCommentBuilder().comment("review comment").nextStatus(ApplicationFormStatus.REVIEW).toReviewEvaluationComment();
//		interviewEvaluationComment = new InterviewEvaluationCommentBuilder().comment("interview comment").nextStatus(ApplicationFormStatus.INTERVIEW).toInterviewEvaluationComment();
//		approvalEvaluationComment = new ApprovalEvaluationCommentBuilder().comment("approval comment").nextStatus(ApplicationFormStatus.REJECTED).toApprovalEvaluationComment();
	}
}
