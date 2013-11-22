package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeSuggestionComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class StateChangeSuggestionCommentBuilder {

	private RegisteredUser user;
	private ApplicationForm application;
	private CommentType type;
	private Date date;	
	private String comment;	
	private Integer id;
	private ApplicationFormStatus nextStatus;

	public StateChangeSuggestionCommentBuilder nextStatus(ApplicationFormStatus nextStatus){
		this.nextStatus = nextStatus;
		return this;
	}
	
	public StateChangeSuggestionCommentBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public StateChangeSuggestionCommentBuilder application(ApplicationForm application){
		this.application = application;
		return this;
	}
		
	public StateChangeSuggestionCommentBuilder type(CommentType type){
		this.type = type;
		return this;
	}
	
	public StateChangeSuggestionCommentBuilder date(Date date){
		this.date = date;
		return this;
	}
	
	public StateChangeSuggestionCommentBuilder comment(String comment){
		this.comment = comment;
		return this;
	}
	
	public StateChangeSuggestionCommentBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public StateChangeSuggestionComment build() {
	    StateChangeSuggestionComment suggestionComment = new StateChangeSuggestionComment();
		suggestionComment.setApplication(application);
		suggestionComment.setComment(comment);
		suggestionComment.setDate(date);
		suggestionComment.setId(id);
		suggestionComment.setType(type);
		suggestionComment.setUser(user);
		suggestionComment.setNextStatus(nextStatus);
		return suggestionComment;
	}
}
