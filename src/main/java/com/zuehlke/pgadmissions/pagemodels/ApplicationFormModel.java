package com.zuehlke.pgadmissions.pagemodels;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

public class ApplicationFormModel extends DomainModel{

	private ApplicationForm applicationForm;
	
	public ApplicationForm getApplicationForm() {
		return applicationForm;
	}
	public void setApplicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;
	}
}
