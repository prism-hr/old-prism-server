package com.zuehlke.pgadmissions.pagemodels;

import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ApplicationListModel extends DomainModel{

	private RegisteredUser user;
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

	public void setUser(RegisteredUser user) {
		this.user = user;
	}

	public RegisteredUser getUser() {
		return this.user;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
