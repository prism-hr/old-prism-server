package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;


import com.zuehlke.pgadmissions.domain.enums.CommentType;

/**
 * @author kpa
 *
 */
@Entity(name="REVIEW_COMMENT")
@Access(AccessType.FIELD)
public class ReviewComment extends Comment{

	private static final long serialVersionUID = 9120577563568889651L;

	@OneToOne(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "reviewer_id")
	private Reviewer reviewer;
	
	
	@Column(name = "willing_to_interview")
	private Boolean willingToInterview;
	
	
	@Column(name="suitable_candidate")
	private Boolean suitableCandidateForUcl;
	
	@Column(name="applicant_suitable_for_programme")
	private Boolean suitableCandidateForProgramme;
	
	
	@Column(name = "decline")
	private boolean decline;
	
	
	@Column(name = "admins_notified")
	private boolean adminsNotified;
	

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.CommentTypeEnumUserType")
	@Column(name="comment_type")
	private CommentType type;

	public CommentType getType() {
		return type;
	}

	public void setType(CommentType type) {
		this.type = type;
	}


	public Reviewer getReviewer() {
		return reviewer;
	}

	public void setReviewer(Reviewer reviewer) {
		this.reviewer = reviewer;
	}
	
	public boolean isWillingToInterviewSet() {
		return willingToInterview != null;
	}

	public Boolean getWillingToInterview() {
		return willingToInterview;
	}

	public void setWillingToInterview(Boolean willingToInterview) {
		this.willingToInterview = willingToInterview;
	}
	
	public boolean isSuitableCandidateSet() {
		return suitableCandidateForUcl != null;
	}
	public Boolean getSuitableCandidateForUcl() {
		return suitableCandidateForUcl;
	}

	public void setSuitableCandidateForUcl(Boolean suitableCandidate) {
		this.suitableCandidateForUcl = suitableCandidate;
	}

	public boolean isDecline() {
		return decline;
	}

	public void setDecline(boolean decline) {
		this.decline = decline;
	}

	public boolean isAdminsNotified() {
		return adminsNotified;
	}

	public void setAdminsNotified(boolean adminsNotified) {
		this.adminsNotified = adminsNotified;
	}

	public Boolean getSuitableCandidateForProgramme() {
		return suitableCandidateForProgramme;
	}

	public void setSuitableCandidateForProgramme(
			Boolean suitableCandidateForProgramme) {
		this.suitableCandidateForProgramme = suitableCandidateForProgramme;
	}

	
}
