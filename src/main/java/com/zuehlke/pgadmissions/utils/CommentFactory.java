package com.zuehlke.pgadmissions.utils;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Component
public class CommentFactory {

	public Comment createComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType) {		
		if(commentType == CommentType.REVIEW_EVALUATION || commentType == CommentType.INTERVIEW_EVALUATION){
			return creatStateChangeComment(applicationForm, user, strComment, commentType);
		}
		if(commentType == CommentType.VALIDATION ){
			return createValidationComment(applicationForm, user, strComment, commentType);
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

	private Comment creatStateChangeComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType) {
		StateChangeComment stateChangeComment = new StateChangeComment();
		stateChangeComment.setApplication(applicationForm);
		stateChangeComment.setUser(user);
		stateChangeComment.setComment(strComment);
		stateChangeComment.setType(commentType);
		return stateChangeComment;
	}
	
	private Comment createValidationComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType) {
		ValidationComment validationComment = new ValidationComment();
		validationComment.setApplication(applicationForm);
		validationComment.setUser(user);
		validationComment.setComment(strComment);
		validationComment.setType(commentType);
		return validationComment;
	}
}
