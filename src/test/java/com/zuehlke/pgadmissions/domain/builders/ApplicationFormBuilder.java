package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ApplicationFormBuilder {

	private Integer id;
	
	private String approved;
	
	private RegisteredUser approver;

	private RegisteredUser user;
	
	private RegisteredUser reviewer;
	
	private Project project;
	
	public ApplicationFormBuilder registeredUser (RegisteredUser user) {
		this.user = user;
		return this;
	}
	
	public ApplicationFormBuilder project (Project project) {
		this.project = project;
		return this;
	}
	
	public ApplicationFormBuilder approver (RegisteredUser user) {
		this.approver = user;
		return this;
	}
	
	public ApplicationFormBuilder approved(String approved) {
		this.approved = approved;
		return this;
	}
	

	
	public ApplicationFormBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public ApplicationFormBuilder reviewer (RegisteredUser reviewer) {
		this.reviewer = reviewer;
		return this;
	}
	public ApplicationForm toApplicationForm() {
		ApplicationForm application = new ApplicationForm();	
		application.setId(id);		
		application.setUser(user);
		application.setReviewer(reviewer);
		application.setApproved(approved);
		application.setApprover(approver);
		application.setProject(project);
		return application;
	}
}
