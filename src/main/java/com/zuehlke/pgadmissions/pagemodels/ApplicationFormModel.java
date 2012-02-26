package com.zuehlke.pgadmissions.pagemodels;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ApplicationFormModel extends DomainModel{

	private ApplicationForm applicationForm;
	private RegisteredUser user;
	
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
}
