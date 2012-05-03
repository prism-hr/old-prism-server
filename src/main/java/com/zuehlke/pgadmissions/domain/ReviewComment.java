package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

/**
 * @author kpa
 *
 */
@Entity(name="REVIEW_COMMENT")
@Access(AccessType.FIELD)
public class ReviewComment extends Comment{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9120577563568889651L;

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
	
	public CheckedStatus getAdminsNotified() {
		return adminsNotified;
	}

	public void setAdminsNotified(CheckedStatus adminsNotified) {
		this.adminsNotified = adminsNotified;
	}

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.CommentTypeEnumUserType")
	@Column(name="comment_type")
	private CommentType type;

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

	
}
