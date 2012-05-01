package com.zuehlke.pgadmissions.utils;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Component
public class CommentFactory {

	public Comment createComment(ApplicationForm applicationForm, RegisteredUser user, String strComment, CommentType commentType) {		
		if(commentType == CommentType.VALIDATION){
			ValidationComment validationComment = new ValidationComment();
			validationComment.setApplication(applicationForm);
			validationComment.setUser(user);
			validationComment.setComment(strComment);
			return validationComment;
		}
		Comment comment = new Comment();
		comment.setApplication(applicationForm);
		comment.setComment(strComment);
		comment.setUser(user);
		return comment;		
	}

}
