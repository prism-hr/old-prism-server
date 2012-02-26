package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
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
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

@Entity(name = "APPLICATION_FORM")
@Access(AccessType.FIELD)
public class ApplicationForm extends DomainObject<Integer> {

	private static final long serialVersionUID = -7671357234815343496L;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.ApprovalStatusEnumUserType")
	@Column(name = "approval_status")
	private ApprovalStatus approvalStatus;

	@ManyToOne
	@JoinColumn(name = "applicant_id")
	private RegisteredUser applicant = null;

	@OneToOne
	@JoinColumn(name = "approver_user_id")
	private RegisteredUser approver = null;

	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.SubmissionStatusEnumUserType")
	@Column(name = "submission_status")
	private SubmissionStatus submissionStatus = SubmissionStatus.UNSUBMITTED;

	private List<RegisteredUser> reviewers = new ArrayList<RegisteredUser>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "APPLICATION_FORM_REVIEWER_LINK", joinColumns = { @JoinColumn(name = "application_form_id") }, inverseJoinColumns = { @JoinColumn(name = "reviewer_id") })
	@Access(AccessType.PROPERTY)
	public List<RegisteredUser> getReviewers() {
		return reviewers;
	}

	public void setReviewers(List<RegisteredUser> reviewers) {
		//THIS IS A HACK. To be changed.
		if(this.reviewers.size() == reviewers.size() && this.reviewers.containsAll(reviewers)){
			return;
		}
		this.reviewers.clear();
		this.reviewers.addAll(reviewers);
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

	public boolean isUnderReview() {
		return !reviewers.isEmpty();
	}

	public boolean isActive() {
		return approvalStatus == null;
	}
}
