package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
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
@Access(AccessType.FIELD)
public class Interviewer extends DomainObject<Integer> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1676615842814210633L;

	@OneToOne(mappedBy = "interviewer")
	private InterviewComment interviewComment;

	
	@Column(name = "last_notified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastNotified;

	@ManyToOne
	@JoinColumn(name = "registered_user_id")
	private RegisteredUser user;

	@ManyToOne
	@JoinColumn(name = "application_form_id")
	private ApplicationForm application;

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

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
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

}
