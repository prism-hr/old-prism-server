package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class ApplicationFormBuilder {

	private ProgrammeDetails programmeDetails;
	
	private PersonalDetails personalDetails;
	
	private Address currentAddress;

	private Address contactAddress;

	private Integer id;

	private ApprovalStatus approved;

	private RegisteredUser approver;

	private RegisteredUser applicant;

	private Project project;

	private SubmissionStatus submissionStatus = SubmissionStatus.UNSUBMITTED;

	private Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();

	private Date appDate;

	private Date submittedDate;

	private List<Qualification> qualifications = new ArrayList<Qualification>();

	private List<Referee> referees = new ArrayList<Referee>();

	private List<EmploymentPosition> employmentPositions = new ArrayList<EmploymentPosition>();

	private List<Funding> fundings = new ArrayList<Funding>();

	private Document cv = null;

	private Document personalStatement = null;

	public ApplicationFormBuilder personalDetails(PersonalDetails personalDetails) {
		this.personalDetails = personalDetails;
		return this;
	}
	
	
	public ApplicationFormBuilder programmeDetails(ProgrammeDetails programmeDetails) {
		this.programmeDetails = programmeDetails;
		return this;
	}
	
	public ApplicationFormBuilder contactAddress(Address contactAddress) {
		this.contactAddress = contactAddress;
		return this;
	}
	public ApplicationFormBuilder currentAddress(Address currentAddress) {
		this.currentAddress = currentAddress;
		return this;
	}
	public ApplicationFormBuilder personalStatement(Document personalStatement) {
		this.personalStatement = personalStatement;
		return this;
	}

	public ApplicationFormBuilder cv(Document cv) {
		this.cv = cv;
		return this;
	}

	public ApplicationFormBuilder applicant(RegisteredUser applicant) {
		this.applicant = applicant;
		return this;
	}

	public ApplicationFormBuilder project(Project project) {
		this.project = project;
		return this;
	}

	public ApplicationFormBuilder approver(RegisteredUser user) {
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

	public ApplicationFormBuilder referees(Referee... referees) {
		for (Referee referee : referees) {
			this.referees.add(referee);
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



	public ApplicationFormBuilder approvedSatus(ApprovalStatus approved) {
		this.approved = approved;
		return this;
	}

	public ApplicationFormBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public ApplicationFormBuilder reviewers(Set<RegisteredUser> reviewers) {
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

	public ApplicationFormBuilder submittedDate(Date date) {
		this.submittedDate = date;
		return this;
	}

	public ApplicationForm toApplicationForm() {
		ApplicationForm application = new ApplicationForm();
		application.setId(id);
		application.setApplicant(applicant);
		if (reviewers != null) {
			application.getReviewers().addAll(reviewers);
		}
		application.setSubmittedDate(submittedDate);
		application.setApprovalStatus(approved);
		application.setApprover(approver);
		application.setReferees(referees);
		application.setProject(project);
		application.setSubmissionStatus(submissionStatus);
		application.setApplicationTimestamp(appDate);
		application.getQualifications().addAll(qualifications);
		application.setProgrammeDetails(programmeDetails);
		application.getFundings().addAll(fundings);
		application.setCv(cv);
		application.setPersonalStatement(personalStatement);
		application.setContactAddress(contactAddress);
		application.setCurrentAddress(currentAddress);
		application.setPersonalDetails(personalDetails);
		return application;
	}
}
