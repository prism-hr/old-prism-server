package com.zuehlke.pgadmissions.pagemodels;

import java.util.List;
import java.util.Map;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class PageModel {

	private Map<String, String> errorObjs;
	private ApplicationForm applicationForm;
	private RegisteredUser user;
	private List<ApplicationReview> applicationComments;
	
	public Map<String, String> getErrorObjs() {
		return errorObjs;
	}

	public void setErrorObjs(Map<String, String> errorObjs) {
		this.errorObjs = errorObjs;
	}

	public ApplicationForm getApplicationForm() {
		return applicationForm;
	}

	public void setApplicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;
	}

	public RegisteredUser getUser() {
		return user;
	}

	public void setUser(RegisteredUser user) {
		this.user = user;
	}

	public List<ApplicationReview> getApplicationComments() {
		return applicationComments;
	}

	public void setApplicationComments(List<ApplicationReview> applicationComments) {
		this.applicationComments = applicationComments;
	}
	
	
}
