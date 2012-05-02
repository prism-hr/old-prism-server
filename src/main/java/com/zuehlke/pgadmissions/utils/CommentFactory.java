package com.zuehlke.pgadmissions.utils;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Component
public class CommentFactory {

	public Comment createComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType) {		
		if(commentType == CommentType.VALIDATION || commentType == CommentType.REVIEW_EVALUATION){
			StateChangeComment stateChangeComment = new StateChangeComment();
			stateChangeComment.setApplication(applicationForm);
			stateChangeComment.setUser(user);
			stateChangeComment.setComment(strComment);
			stateChangeComment.setType(commentType);
			return stateChangeComment;
		}
		
		if(commentType == CommentType.REVIEW){
			ReviewComment reviewComment = new ReviewComment();
			reviewComment.setApplication(applicationForm);
			reviewComment.setUser(user);
			reviewComment.setComment(strComment);
			return reviewComment;
		}
		Comment comment = new Comment();
		comment.setApplication(applicationForm);
		comment.setComment(strComment);
		comment.setUser(user);
		return comment;		
	}

}
