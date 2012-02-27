package com.zuehlke.pgadmissions.pagemodels;

import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ReviewersListModel {
	
	private RegisteredUser user;
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
	
	public RegisteredUser getUser() {
		return user;
	}

	public void setUser(RegisteredUser user) {
		this.user = user;
	}

}
