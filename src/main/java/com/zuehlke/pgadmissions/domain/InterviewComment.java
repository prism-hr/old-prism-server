package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;


@Entity(name="INTERVIEW_COMMENT")
@Access(AccessType.FIELD)
public class InterviewComment extends Comment{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9120577563568889651L;

	@OneToOne(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "interviewer_id")
	private Interviewer interviewer;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.CheckedStatusEnumUserType")
	@Column(name = "willing_to_supervise")
	private CheckedStatus willingToSupervice;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.CheckedStatusEnumUserType")
	@Column(name="suitable_candidate")
	private CheckedStatus suitableCandidate;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.CheckedStatusEnumUserType")
	@Column(name = "decline")
	private CheckedStatus decline;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.CheckedStatusEnumUserType")
	@Column(name = "admins_notified")
	private CheckedStatus adminsNotified;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.CommentTypeEnumUserType")
	@Column(name="comment_type")
	private CommentType type;
	
	public CheckedStatus getAdminsNotified() {
		return adminsNotified;
	}

	public void setAdminsNotified(CheckedStatus adminsNotified) {
		this.adminsNotified = adminsNotified;
	}

	public CommentType getType() {
		return type;
	}

	public void setType(CommentType type) {
		this.type = type;
	}
	
	public CheckedStatus getWillingToSupervice() {
		return willingToSupervice;
	}

	public void setWillingToSupervice(CheckedStatus willingToSupervice) {
		this.willingToSupervice = willingToSupervice;
	}

	public CheckedStatus getSuitableCandidate() {
		return suitableCandidate;
	}

	public void setSuitableCandidate(CheckedStatus suitableCandidate) {
		this.suitableCandidate = suitableCandidate;
	}

	public CheckedStatus getDecline() {
		return decline;
	}

	public void setDecline(CheckedStatus decline) {
		this.decline = decline;
	}

	public Interviewer getInterviewer() {
		return interviewer;
	}

	public void setInterviewer(Interviewer interviewer) {
		this.interviewer = interviewer;
	}


	
}
