package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RequestRestartComment;

public class RequestRestartCommentBuilder {
	
	private ApplicationForm application;
	private RegisteredUser user;
	private String strComment;
	private Integer id;
	private Date createdTimeStamp;
	
	public RequestRestartCommentBuilder date(Date createdTimeStamp) {
		this.createdTimeStamp = createdTimeStamp;
		return this;
	}
	
	public RequestRestartCommentBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public RequestRestartCommentBuilder application (ApplicationForm application){
		this.application = application;
		return this;
	}
	public RequestRestartCommentBuilder comment (String comment){
		this.strComment = comment;
		return this;
	}
	public RequestRestartCommentBuilder user (RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public RequestRestartComment build() {
		RequestRestartComment comment = new RequestRestartComment();
		comment.setApplication(application);
		comment.setComment(strComment);
		comment.setId(id);
		comment.setUser(user);
		comment.setDate(createdTimeStamp);
		return comment;
	}
}
