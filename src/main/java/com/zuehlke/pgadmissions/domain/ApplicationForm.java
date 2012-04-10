package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

@Entity(name = "APPLICATION_FORM")
@Access(AccessType.FIELD)
public class ApplicationForm extends DomainObject<Integer> implements Comparable<ApplicationForm> {

	private static final long serialVersionUID = -7671357234815343496L;

	@ManyToOne
	@JoinColumn(name = "cv_id")
	private Document cv = null;

	@ManyToOne
	@JoinColumn(name = "personal_statement_id")
	private Document personalStatement = null;

	@Column(name = "app_date_time", insertable = false)
	@Generated(GenerationTime.INSERT)
	@Temporal(TemporalType.TIMESTAMP)
	private Date applicationTimestamp;

	@Column(name = "submitted_on_timestamp")
	private Date submittedDate;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.ApprovalStatusEnumUserType")
	@Column(name = "approval_status")
	private ApprovalStatus approvalStatus;

	@ManyToOne
	@JoinColumn(name = "applicant_id")
	private RegisteredUser applicant = null;

	@ManyToOne
	@JoinColumn(name = "approver_user_id")
	private RegisteredUser approver = null;

	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;

	@OneToOne(mappedBy = "application")
	private PersonalDetail personalDetails;

	@OneToOne(mappedBy = "application")
	private ProgrammeDetail programmeDetails;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.SubmissionStatusEnumUserType")
	@Column(name = "submission_status")
	private SubmissionStatus submissionStatus = SubmissionStatus.UNSUBMITTED;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "APPLICATION_FORM_REVIEWER_LINK", joinColumns = { @JoinColumn(name = "application_form_id") }, inverseJoinColumns = { @JoinColumn(name = "reviewer_id") })
	private List<RegisteredUser> reviewers = new ArrayList<RegisteredUser>();

	@OneToMany(mappedBy = "application")
	private List<ApplicationReview> applicationComments = new ArrayList<ApplicationReview>();

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_id")
	private List<Address> addresses = new ArrayList<Address>();

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_id")
	private List<Qualification> qualifications = new ArrayList<Qualification>();

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_id")
	private List<Funding> fundings = new ArrayList<Funding>();

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_id")
	private List<EmploymentPosition> employmentPositions = new ArrayList<EmploymentPosition>();

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_id")
	private List<Referee> referees = new ArrayList<Referee>();

	@Column(name = "additional_information")
	private String additionalInformation;

	public List<Qualification> getQualifications() {
		return qualifications;
	}

	public void setQualifications(List<Qualification> qualifications) {
		this.qualifications.clear();
		this.qualifications.addAll(qualifications);
	}

	public List<RegisteredUser> getReviewers() {
		return reviewers;
	}

	public void setReviewers(List<RegisteredUser> reviewers) {
		this.reviewers = reviewers;
	}

	public Project getProject() {
		return project;
	}

	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}

	public RegisteredUser getApplicant() {
		return applicant;
	}

	public void setApplicant(RegisteredUser user) {
		this.applicant = user;
	}

	public RegisteredUser getApprover() {
		return approver;
	}

	public void setApprover(RegisteredUser approver) {
		this.approver = approver;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setSubmissionStatus(SubmissionStatus submissionStatus) {
		this.submissionStatus = submissionStatus;

	}

	public SubmissionStatus getSubmissionStatus() {
		return submissionStatus;
	}

	public Date getApplicationTimestamp() {
		return applicationTimestamp;
	}

	public void setApplicationTimestamp(Date applicationTimestamp) {
		this.applicationTimestamp = applicationTimestamp;
	}

	public boolean isUnderReview() {
		return !reviewers.isEmpty();
	}

	public boolean isReviewable() {
		if (submissionStatus != SubmissionStatus.SUBMITTED || approvalStatus != null) {
			return false;
		}
		return true;
	}

	public boolean isSubmitted() {
		return submissionStatus == SubmissionStatus.SUBMITTED;
	}

	@Override
	public int compareTo(ApplicationForm appForm) {
		if (this.applicationTimestamp == null) {
			return -1;
		}

		if (appForm.getApplicationTimestamp() == null) {
			return 1;
		}
		return (-1) * this.applicationTimestamp.compareTo(appForm.getApplicationTimestamp());
	}

	public boolean isDecided() {
		return approvalStatus != null;
	}

	public List<ApplicationReview> getApplicationComments() {
		return applicationComments;
	}

	public void setApplicationComments(List<ApplicationReview> applicationComments) {
		this.applicationComments = applicationComments;
	}

	public boolean hasComments() {
		return applicationComments != null && !applicationComments.isEmpty();
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public boolean hasQualifications() {
		return !qualifications.isEmpty();
	}

	public List<Funding> getFundings() {
		return fundings;
	}

	public void setFundings(List<Funding> fundings) {
		this.fundings = fundings;
	}

	public List<EmploymentPosition> getEmploymentPositions() {
		return employmentPositions;
	}

	public void setEmploymentPositions(List<EmploymentPosition> employmentPositions) {
		this.employmentPositions = employmentPositions;
	}

	public List<Referee> getReferees() {
		return referees;
	}

	public void setReferees(List<Referee> referees) {
		this.referees = referees;
	}

	public PersonalDetail getPersonalDetails() {
		if (personalDetails == null) {
			return new PersonalDetail();
		}
		return personalDetails;
	}

	public void setPersonalDetails(PersonalDetail personalDetails) {
		this.personalDetails = personalDetails;
	}

	public ProgrammeDetail getProgrammeDetails() {
		if (programmeDetails == null) {
			return new ProgrammeDetail();
		}
		return programmeDetails;
	}

	public void setProgrammeDetails(ProgrammeDetail programmeDetails) {
		this.programmeDetails = programmeDetails;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		if (StringUtils.isBlank(additionalInformation)) {
			this.additionalInformation = null;
		} else {
			this.additionalInformation = additionalInformation;
		}
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedOn) {
		this.submittedDate = submittedOn;
	}

	public Document getCv() {
		return cv;
	}

	public void setCv(Document cv) {
		this.cv = cv;
	}

	public Document getPersonalStatement() {
		return personalStatement;
	}

	public void setPersonalStatement(Document personalStatement) {
		this.personalStatement = personalStatement;
	}
}
