package com.zuehlke.pgadmissions.domain;

public class ReviewerAssignedModel {

	private ApplicationForm application;
	private RegisteredUser reviewer;
	
	public ApplicationForm getApplication() {
		return application;
	}
	
	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public void setReviewer(RegisteredUser reviewer) {
		this.reviewer = reviewer;
	}
	
	public RegisteredUser getReviewer() {
		return reviewer;
	}
}
