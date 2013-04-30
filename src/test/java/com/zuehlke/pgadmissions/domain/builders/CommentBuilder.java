package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class CommentBuilder {
	
	private ApplicationForm application;
	private RegisteredUser user;
	private String strComment;
	private Integer id;
	private Date createdTimeStamp;
	
	public CommentBuilder date(Date createdTimeStamp) {
		this.createdTimeStamp = createdTimeStamp;
		return this;
	}
	
	public CommentBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public CommentBuilder application (ApplicationForm application){
		this.application = application;
		return this;
	}
	public CommentBuilder comment (String comment){
		this.strComment = comment;
		return this;
	}
	public CommentBuilder user (RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public Comment build() {
		Comment comment = new Comment();
		comment.setApplication(application);
		comment.setComment(strComment);
		comment.setId(id);
		comment.setUser(user);
		comment.setDate(createdTimeStamp);
		return comment;
	}
}
