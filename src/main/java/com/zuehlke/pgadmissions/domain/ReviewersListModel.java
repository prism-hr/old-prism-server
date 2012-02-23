package com.zuehlke.pgadmissions.domain;

import java.util.List;

public class ReviewersListModel {
	
	private ApplicationForm application;
	private List<RegisteredUser> reviewers;
	
	public void setApplication(ApplicationForm application) {
		this.application = application;
	}
	
	public ApplicationForm getApplication() {
		return application;
	}
	
	public void setReviewers(List<RegisteredUser> reviewers) {
		this.reviewers = reviewers;
	}
	
	public List<RegisteredUser> getReviewers() {
		return reviewers;
	}
}
