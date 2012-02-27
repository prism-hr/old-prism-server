package com.zuehlke.pgadmissions.pagemodels;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

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
