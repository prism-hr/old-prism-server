package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.CompleteReviewComment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ReviewEvaluationCommentBuilder {

	private User user;
	private ApplicationForm application;
	private Date date;	
	private String comment;	
	private Integer id;
	private ApplicationFormStatus nextStatus;
	private User delegateAdministrator;

	public ReviewEvaluationCommentBuilder nextStatus(ApplicationFormStatus nextStatus){
		this.nextStatus = nextStatus;
		return this;
	}
	
	public ReviewEvaluationCommentBuilder user(User user){
		this.user = user;
		return this;
	}
	
	
	public ReviewEvaluationCommentBuilder application(ApplicationForm application){
		this.application = application;
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
	
	public ReviewEvaluationCommentBuilder delegateAdministrator(User delegateAdministrator){
	    this.delegateAdministrator = delegateAdministrator;
	    return this;
	}
	
	public CompleteReviewComment build(){
		CompleteReviewComment reviewEaluationComment = new CompleteReviewComment();
		reviewEaluationComment.setApplication(application);
		reviewEaluationComment.setContent(comment);
		reviewEaluationComment.setCreatedTimestamp(date);
		reviewEaluationComment.setId(id);
		reviewEaluationComment.setUser(user);
		reviewEaluationComment.setNextStatus(nextStatus);
		reviewEaluationComment.setDelegateAdministrator(delegateAdministrator);
		return reviewEaluationComment;
	}
}
