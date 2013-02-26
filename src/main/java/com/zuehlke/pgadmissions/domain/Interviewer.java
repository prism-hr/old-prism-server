package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "INTERVIEWER")
public class Interviewer implements Serializable {

	private static final long serialVersionUID = 1676615842814210633L;

	@Id
	@GeneratedValue
	private Integer id;

	@Column(name = "requires_admin_notification")
	private boolean requiresAdminNotification;
	
	@Column(name = "first_admin_notification")
	private boolean firstAdminNotification = true;
	
	@Column(name = "admins_notified_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateAdminsNotified;
	
	@OneToOne(mappedBy = "interviewer")
	private InterviewComment interviewComment;

	
	@Column(name = "last_notified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastNotified;

	@ManyToOne
	@JoinColumn(name = "registered_user_id")
	private RegisteredUser user;

	@ManyToOne
	@JoinColumn(name = "interview_id")
	private Interview interview;
	
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

	public InterviewComment getInterviewComment() {
		return interviewComment;
	}

	public void setInterviewComment(InterviewComment interviewComment) {
		this.interviewComment = interviewComment;
	}

	public Interview getInterview() {
		return interview;
	}

	public void setInterview(Interview interview) {
		this.interview = interview;
	}

	

	public Date getDateAdminsNotified() {
		return dateAdminsNotified;
	}

	public void setDateAdminsNotified(Date dateAdminsNotified) {
		this.dateAdminsNotified = dateAdminsNotified;
	}

	public boolean isRequiresAdminNotification() {
		return requiresAdminNotification;
	}

	public void setRequiresAdminNotification(boolean requiresAdminNotification) {
		this.requiresAdminNotification = requiresAdminNotification;
	}

	public boolean isFirstAdminNotification() {
		return firstAdminNotification;
	}

	public void setFirstAdminNotification(boolean firstAdminNotification) {
		this.firstAdminNotification = firstAdminNotification;
	}
}
