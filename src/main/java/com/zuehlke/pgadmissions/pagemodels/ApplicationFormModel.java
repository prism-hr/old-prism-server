package com.zuehlke.pgadmissions.pagemodels;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ApplicationFormModel extends DomainModel{

	private ApplicationForm applicationForm;
	private String projectId;
	
	public ApplicationForm getApplicationForm() {
		return applicationForm;
	}
	public void setApplicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	
}
