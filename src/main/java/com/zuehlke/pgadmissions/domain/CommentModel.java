package com.zuehlke.pgadmissions.domain;

public class CommentModel {

	private String message;
	private ApplicationForm application;
	private String comment;

	public String getComment() {
		return comment;
	}

	public ApplicationForm getApplication() {
		return application;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public void setComment(String comment) {
		this.comment = comment;
		
	}
}
