package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

@Entity(name = "REVIEWER")
public class Reviewer implements Serializable {

	private static final long serialVersionUID = 7813331086711135352L;

    @Id
    @GeneratedValue
    private Integer id;

	@OneToOne(mappedBy = "reviewer")
	private ReviewComment review;

	@Enumerated(EnumType.STRING)
	@Column(name = "requires_admin_notification")
	private CheckedStatus requiresAdminNotification;
	
	@Column(name = "admins_notified_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateAdminsNotified;
	
	@Column(name = "last_notified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastNotified;

	@ManyToOne
	@JoinColumn(name = "review_round_id")
	private ReviewRound reviewRound;
	
	@ManyToOne
	@JoinColumn(name = "registered_user_id")
	private RegisteredUser user;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public RegisteredUser getUser() {
		return user;
	}

	public void setUser(RegisteredUser user) {
		this.user = user;
	}

	public Date getLastNotified() {
		return lastNotified;
	}

	public void setLastNotified(Date lastNotified) {
		this.lastNotified = lastNotified;
	}

	public ReviewComment getReview() {
		return review;
	}

	public void setReview(ReviewComment review) {
		this.review = review;
	}

	public ReviewRound getReviewRound() {
		return reviewRound;
	}

	public void setReviewRound(ReviewRound reviewRound) {
		this.reviewRound = reviewRound;
	}

	public Date getDateAdminsNotified() {
		return dateAdminsNotified;
	}

	public void setDateAdminsNotified(Date dateAdminsNotified) {
		this.dateAdminsNotified = dateAdminsNotified;
	}

	public CheckedStatus getRequiresAdminNotification() {
		return requiresAdminNotification;
	}

	public void setRequiresAdminNotification(CheckedStatus requiresAdminNotification) {
		this.requiresAdminNotification = requiresAdminNotification;
	}
	
}
