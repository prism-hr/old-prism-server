package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class InterviewEvaluationCommentBuilder {

	private RegisteredUser user;
	private ApplicationForm application;
	private CommentType type;
	private Date date;	
	private String comment;	
	private Integer id;
	private ApplicationFormStatus nextStatus;
	private Interview interview;

	public InterviewEvaluationCommentBuilder interview(Interview interview){
		this.interview = interview;
		return this;
	}
	
	public InterviewEvaluationCommentBuilder nextStatus(ApplicationFormStatus nextStatus){
		this.nextStatus = nextStatus;
		return this;
	}
	
	public InterviewEvaluationCommentBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public InterviewEvaluationCommentBuilder application(ApplicationForm application){
		this.application = application;
		return this;
	}
		
	public InterviewEvaluationCommentBuilder type(CommentType type){
		this.type = type;
		return this;
	}
	
	public InterviewEvaluationCommentBuilder date(Date date){
		this.date = date;
		return this;
	}
	
	public InterviewEvaluationCommentBuilder comment(String comment){
		this.comment = comment;
		return this;
	}
	
	public InterviewEvaluationCommentBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public InterviewEvaluationComment build() {
		InterviewEvaluationComment reviewEaluationComment = new InterviewEvaluationComment();
		reviewEaluationComment.setApplication(application);
		reviewEaluationComment.setComment(comment);
		reviewEaluationComment.setDate(date);
		reviewEaluationComment.setId(id);
		reviewEaluationComment.setType(type);
		reviewEaluationComment.setUser(user);
		reviewEaluationComment.setNextStatus(nextStatus);
		reviewEaluationComment.setInterview(interview);
		return reviewEaluationComment;
	}
}
