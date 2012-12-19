package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Entity(name="INTERVIEW_COMMENT")
public class InterviewComment extends Comment {
	
	private static final long serialVersionUID = 9120577563568889651L;

	@OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "interviewer_id")
	private Interviewer interviewer;
	
	@Column(name = "willing_to_supervise")
	private Boolean willingToSupervise;
	
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
	

	public Interviewer getInterviewer() {
		return interviewer;
	}

	public void setInterviewer(Interviewer interviewer) {
		this.interviewer = interviewer;
	}

	public Boolean getWillingToSupervise() {
		return willingToSupervise;
	}

	public void setWillingToSupervise(Boolean willingToSupervice) {
		this.willingToSupervise = willingToSupervice;
	}

	public boolean isWillingToSuperviseSet() {
		return willingToSupervise != null;
	}
	public Boolean getSuitableCandidateForUcl() {
		return suitableCandidateForUcl;
	}

	public void setSuitableCandidateForUcl(Boolean suitableCandidate) {
		this.suitableCandidateForUcl = suitableCandidate;
	}
	public boolean isSuitableCandidateSet() {
		return suitableCandidateForUcl != null;
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
