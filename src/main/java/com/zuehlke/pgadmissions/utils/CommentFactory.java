package com.zuehlke.pgadmissions.utils;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Component
public class CommentFactory {

	public Comment createComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType, ApplicationFormStatus nextStatus) {		
		if(commentType == CommentType.INTERVIEW_EVALUATION){
			return createInterviewEvaluationComment(applicationForm, user, strComment, commentType, nextStatus);
		}
		if(commentType == CommentType.REVIEW_EVALUATION){
			return createReviewEvaluationComment(applicationForm, user, strComment, commentType, nextStatus);
		}
		if(commentType == CommentType.VALIDATION ){
			return createValidationComment(applicationForm, user, strComment, commentType, nextStatus);
		}
		
		if(commentType == CommentType.REVIEW){
			return createReviewComment(applicationForm, user, strComment, commentType);
		}
		return createGenericComment(applicationForm, user, strComment);		
	}

	private Comment createGenericComment(ApplicationForm applicationForm, RegisteredUser user, String strComment) {
		Comment comment = new Comment();
		comment.setApplication(applicationForm);
		comment.setComment(strComment);
		comment.setUser(user);
		return comment;
	}


	private Comment createReviewComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType) {
		ReviewComment reviewComment = new ReviewComment();
		reviewComment.setApplication(applicationForm);
		reviewComment.setUser(user);
		reviewComment.setComment(strComment);
		reviewComment.setType(commentType);
		return reviewComment;
	}


	
	private Comment createValidationComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType, ApplicationFormStatus nextStatus) {
		ValidationComment validationComment = new ValidationComment();
		validationComment.setApplication(applicationForm);
		validationComment.setUser(user);
		validationComment.setComment(strComment);
		validationComment.setType(commentType);
		validationComment.setNextStatus(nextStatus);
		return validationComment;
	}
	
	private Comment createReviewEvaluationComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType, ApplicationFormStatus nextStatus) {
		ReviewEvaluationComment comment = new ReviewEvaluationComment();
		comment.setApplication(applicationForm);
		comment.setUser(user);
		comment.setComment(strComment);
		comment.setType(commentType);
		comment.setNextStatus(nextStatus);
		return comment;
	}
	
	private Comment createInterviewEvaluationComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType, ApplicationFormStatus nextStatus) {
		InterviewEvaluationComment comment = new InterviewEvaluationComment();
		comment.setApplication(applicationForm);
		comment.setUser(user);
		comment.setComment(strComment);
		comment.setType(commentType);
		comment.setNextStatus(nextStatus);
		return comment;
	}
}
