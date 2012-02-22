package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

@Entity(name = "APPLICATION_FORM")
@Access(AccessType.FIELD) 
public class ApplicationForm extends DomainObject<Integer> {
	
	private static final long serialVersionUID = 1L;
	
	private String approved;
	
	@ManyToOne
	@JoinColumn(name="registered_user_id")
	private RegisteredUser user = null;

	@ManyToOne
	@JoinColumn(name="reviewer_user_id")
	private RegisteredUser reviewer = null;
	
	@OneToOne
	@JoinColumn(name="approver_user_id")
	private RegisteredUser approver = null;

	@ManyToOne
	@JoinColumn(name="project_id")
	private Project project;

	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.SubmissionStatusEnumUserType")	
	@Column(name="submission_status")
	private SubmissionStatus submissionStatus = SubmissionStatus.UNSUBMITTED;
	
	
	public Project getProject() {
		return project;
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

	public RegisteredUser getUser() {
		return user;
	}

	public void setUser(RegisteredUser user) {
		this.user = user;
	}
	
	public RegisteredUser getReviewer() {
		return reviewer;
	}
	
	public void setReviewer(RegisteredUser reviewer) {
		this.reviewer = reviewer;
	}


	public RegisteredUser getApprover() {
		return approver;
	}

	public void setApprover(RegisteredUser approver) {
		this.approver = approver;
	}

	public String getApproved() {
		return approved;
	}

	public void setApproved(String approved) {
		this.approved = approved;
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
		return reviewer != null;
	}
}
