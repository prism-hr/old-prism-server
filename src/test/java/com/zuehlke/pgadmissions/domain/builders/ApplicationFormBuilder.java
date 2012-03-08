package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class ApplicationFormBuilder {

	private Integer id;
	
	private ApprovalStatus approved;
	
	private RegisteredUser approver;

	private RegisteredUser applicant;
	
	private Project project;
	
	private SubmissionStatus submissionStatus = SubmissionStatus.UNSUBMITTED;
	
	private Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();
	
	private Date appDate;
	
	private List<Qualification> qualifications = new ArrayList<Qualification>();

	private List<EmploymentPosition> employmentPositions = new ArrayList<EmploymentPosition>();
	
	private List<Address> addresses = new ArrayList<Address>();
	private List<Funding> fundings = new ArrayList<Funding>();
	
	public ApplicationFormBuilder applicant (RegisteredUser applicant) {
		this.applicant = applicant;
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
	
	public ApplicationFormBuilder qualification(Qualification qualification) {
		this.qualifications.add(qualification);
		return this;
	}
	
	public ApplicationFormBuilder qualifications(Qualification... qualifications) {
		for (Qualification qualification : qualifications) {
			this.qualifications.add(qualification);
		}
		return this;
	}
	
	public ApplicationFormBuilder employmentPosition(EmploymentPosition employmentPosition) {
		this.employmentPositions.add(employmentPosition);
		return this;
	}
	
	public ApplicationFormBuilder employmentPositions(EmploymentPosition... employmentPositions) {
		for (EmploymentPosition employmentPosition : employmentPositions) {
			this.employmentPositions.add(employmentPosition);
		}
		return this;
	}
	
	public ApplicationFormBuilder fundings(Funding... fundings) {
		for (Funding funding : fundings) {
			this.fundings.add(funding);
		}
		return this;
	}
	
	public ApplicationFormBuilder addresses(Address... addresses) {
		for (Address address : addresses) {
			this.addresses.add(address);
		}
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
	
	public ApplicationFormBuilder appDate(Date date) {
		this.appDate = date;
		return this;
	}
	
	
	public ApplicationForm toApplicationForm() {
		ApplicationForm application = new ApplicationForm();	
		application.setId(id);		
		application.setApplicant(applicant);
		if (reviewers != null) {
			application.getReviewers().addAll(reviewers);
		}
		application.setApprovalStatus(approved);
		application.setApprover(approver);
		application.setProject(project);
		application.setSubmissionStatus(submissionStatus);
		application.setApplicationTimestamp(appDate);
		application.getQualifications().addAll(qualifications);
		application.getAddresses().addAll(addresses);
		application.getFundings().addAll(fundings);
		return application;
	}
}
