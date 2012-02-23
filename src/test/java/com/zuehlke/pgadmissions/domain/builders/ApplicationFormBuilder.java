package com.zuehlke.pgadmissions.domain.builders;

import java.util.HashSet;
import java.util.Set;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class ApplicationFormBuilder {

	private Integer id;
	
	private ApprovalStatus approved;
	
	private RegisteredUser approver;

	private RegisteredUser user;
	
	private Project project;
	
	private SubmissionStatus submissionStatus = SubmissionStatus.UNSUBMITTED;
	
	private Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();
	
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
	
	public ApplicationFormBuilder approvedSatus(ApprovalStatus approved) {
		this.approved = approved;
		return this;
	}

	public ApplicationFormBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public ApplicationFormBuilder reviewers (Set<RegisteredUser> reviewers) {
		this.reviewers = reviewers;
		return this;
	}
	
	public ApplicationFormBuilder submissionStatus(SubmissionStatus submissionStatus) {
		this.submissionStatus = submissionStatus;
		return this;
	}
	
	public ApplicationForm toApplicationForm() {
		ApplicationForm application = new ApplicationForm();	
		application.setId(id);		
		application.setUser(user);
		if (reviewers != null) {
			application.getReviewers().addAll(reviewers);
		}
		application.setApprovalStatus(approved);
		application.setApprover(approver);
		application.setProject(project);
		application.setSubmissionStatus(submissionStatus);
		return application;
	}
}
