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
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ValidationStage;

public class ApplicationFormBuilder {

	private ApplicationFormStatus status = ApplicationFormStatus.UNSUBMITTED;
	private ProgrammeDetails programmeDetails;

	private PersonalDetails personalDetails;

	private Address currentAddress;

	private Address contactAddress;

	private Integer id;

	private RegisteredUser approver;

	private RegisteredUser applicant;

	private String projectTitle;

	private Program program;

	private Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();

	private Date appDate;

	private Date submittedDate;

	private Date validationDueDate;

	private Date lastEmailReminderDate;

	private Date lastSubmissionNotification;

	private List<Qualification> qualifications = new ArrayList<Qualification>();

	private List<Referee> referees = new ArrayList<Referee>();

	private List<EmploymentPosition> employmentPositions = new ArrayList<EmploymentPosition>();

	private List<Funding> fundings = new ArrayList<Funding>();

	private Document cv = null;

	private Document personalStatement = null;

	public ApplicationFormBuilder status(ApplicationFormStatus status) {
		this.status = status;
		return this;
	}

	public ApplicationFormBuilder projectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
		return this;
	}

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

	public ApplicationFormBuilder program(Program program) {
		this.program = program;
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

	public ApplicationFormBuilder lastSubmissionNotification(Date lastSubmissionNotification) {
		this.lastSubmissionNotification = lastSubmissionNotification;
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

	public ApplicationFormBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public ApplicationFormBuilder reviewers(Set<RegisteredUser> reviewers) {
		this.reviewers = reviewers;
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

	public ApplicationFormBuilder lastEmailReminderDate(Date lastEmailReminderDate) {
		this.lastEmailReminderDate = lastEmailReminderDate;
		return this;
	}

	public ApplicationFormBuilder validationDueDate(Date validationDueDate) {
		this.validationDueDate = validationDueDate;
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

		application.setApprover(approver);
		application.setReferees(referees);

		application.setApplicationTimestamp(appDate);
		application.getQualifications().addAll(qualifications);
		application.setProgrammeDetails(programmeDetails);
		application.getFundings().addAll(fundings);
		application.setCv(cv);
		application.setPersonalStatement(personalStatement);
		application.setContactAddress(contactAddress);
		application.setCurrentAddress(currentAddress);
		application.setPersonalDetails(personalDetails);
		application.setValidationDueDate(validationDueDate);
		application.setLastEmailReminderDate(lastEmailReminderDate);
		application.setProgram(program);
		application.setLastSubmissionNotification(lastSubmissionNotification);
		application.setProjectTitle(projectTitle);
		application.setStatus(status);
		return application;
	}
}
