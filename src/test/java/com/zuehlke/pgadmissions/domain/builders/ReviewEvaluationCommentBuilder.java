package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ReviewEvaluationCommentBuilder {

	private RegisteredUser user;
	private ApplicationForm application;
	private CommentType type;
	private Date date;	
	private String comment;	
	private Integer id;
	private ApplicationFormStatus nextStatus;
	private ReviewRound reviewRound;
	private RegisteredUser delegateAdministrator;

	public ReviewEvaluationCommentBuilder reviewRound(ReviewRound reviewRound){
		this.reviewRound = reviewRound;
		return this;
	}
	
	public ReviewEvaluationCommentBuilder nextStatus(ApplicationFormStatus nextStatus){
		this.nextStatus = nextStatus;
		return this;
	}
	
	public ReviewEvaluationCommentBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	
	public ReviewEvaluationCommentBuilder application(ApplicationForm application){
		this.application = application;
		return this;
	}
		
	public ReviewEvaluationCommentBuilder type(CommentType type){
		this.type = type;
		return this;
	}
	
	public ReviewEvaluationCommentBuilder date(Date date){
		this.date = date;
		return this;
	}
	
	public ReviewEvaluationCommentBuilder comment(String comment){
		this.comment = comment;
		return this;
	}
	
	public ReviewEvaluationCommentBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ReviewEvaluationCommentBuilder delegateAdministrator(RegisteredUser delegateAdministrator){
	    this.delegateAdministrator = delegateAdministrator;
	    return this;
	}
	
	public ReviewEvaluationComment build(){
		ReviewEvaluationComment reviewEaluationComment = new ReviewEvaluationComment();
		reviewEaluationComment.setApplication(application);
		reviewEaluationComment.setComment(comment);
		reviewEaluationComment.setDate(date);
		reviewEaluationComment.setId(id);
		reviewEaluationComment.setType(type);
		reviewEaluationComment.setUser(user);
		reviewEaluationComment.setNextStatus(nextStatus);
		reviewEaluationComment.setReviewRound(reviewRound);
		reviewEaluationComment.setDelegateAdministrator(delegateAdministrator);
		return reviewEaluationComment;
	}
}
