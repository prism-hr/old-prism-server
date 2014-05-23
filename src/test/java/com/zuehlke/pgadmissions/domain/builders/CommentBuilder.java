package com.zuehlke.pgadmissions.domain.builders;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.User;

public class CommentBuilder {
	
	private Application application;
	private User user;
	private String strComment;
	private Integer id;
	private DateTime createdTimeStamp;
	
	public CommentBuilder date(DateTime createdTimeStamp) {
		this.createdTimeStamp = createdTimeStamp;
		return this;
	}
	
	public CommentBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public CommentBuilder application (Application application){
		this.application = application;
		return this;
	}

	public CommentBuilder comment (String comment){
		this.strComment = comment;
		return this;
	}
	
	public CommentBuilder user (User user){
		this.user = user;
		return this;
	}
	
	public Comment build() {
		Comment comment = new Comment();
		comment.setApplication(application);
		comment.setContent(strComment);
		comment.setId(id);
		comment.setUser(user);
		comment.setCreatedTimestamp(createdTimeStamp);
		return comment;
	}
}
