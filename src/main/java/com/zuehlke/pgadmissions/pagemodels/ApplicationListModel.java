package com.zuehlke.pgadmissions.pagemodels;

import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

public class ApplicationListModel extends PageModel{

	private List<ApplicationForm> applications;
	private String message = "";
	
	public String getMessage() {
		return message;
	}

	public List<ApplicationForm> getApplications() {
		return applications;
	}

	public void setApplications(List<ApplicationForm> applications) {
		this.applications = applications;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean hasApplications(){
		return !applications.isEmpty();
	}
}
