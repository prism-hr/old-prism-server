package com.zuehlke.pgadmissions.domain;

import java.util.List;

public class CommentModel {

	private String message;
	private ApplicationForm application;
	private String comment;
	private List<ApplicationReview> comments;

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

	public void setComments(
			List<ApplicationReview> applicationReviewsByApplication) {
				this.comments = applicationReviewsByApplication;
		
	}
	
	public List<ApplicationReview> getComments() {
		return comments;
	}
}
