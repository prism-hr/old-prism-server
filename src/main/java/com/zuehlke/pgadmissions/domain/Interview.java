package com.zuehlke.pgadmissions.domain;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity(name = "INTERVIEW")
@Access(AccessType.FIELD)
public class Interview extends DomainObject<Integer> {

	private static final long serialVersionUID = -730673777949846236L;

	@Column(name = "last_notified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastNotified;

	@Column(name = "interview_time")
	private String interviewTime;

	@Column(name = "created_date", insertable = false)
	@Generated(GenerationTime.INSERT)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "further_details")
	private String furtherDetails;

	@ManyToOne
	@JoinColumn(name = "application_form_id")
	private ApplicationForm application;

	@Column(name = "location_url")
	private String locationURL;

	@Temporal(TemporalType.DATE)
	@Column(name = "due_date")
	private Date interviewDueDate;

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "interview_id")
	private List<Interviewer> interviewers = new ArrayList<Interviewer>();

	public Date getLastNotified() {
		return lastNotified;
	}

	public void setLastNotified(Date lastNotified) {
		this.lastNotified = lastNotified;
	}

	public String getFurtherDetails() {
		return furtherDetails;
	}

	public void setFurtherDetails(String furtherDetails) {
		this.furtherDetails = furtherDetails;
	}

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public String getLocationURL() {
		return locationURL;
	}

	public void setLocationURL(String locationURL) {
		this.locationURL = locationURL;
	}

	public Date getInterviewDueDate() {
		return interviewDueDate;
	}

	public void setInterviewDueDate(Date dueDate) {
		this.interviewDueDate = dueDate;
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

	public List<Interviewer> getInterviewers() {
		return interviewers;
	}

	public void setInterviewers(List<Interviewer> interviewers) {
		this.interviewers = interviewers;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date created) {
		this.createdDate = created;
	}

	public String getInterviewTime() {
		return interviewTime;
	}

	public void setInterviewTime(String interviewTime) {
		this.interviewTime = interviewTime;
	}

	public String[] getTimeParts() {
		String[] parts = new String[3];
		if (interviewTime != null) {
			int semiColonPosition = interviewTime.indexOf(":");
			int emptySpacePosition = interviewTime.indexOf(" ", semiColonPosition);
			parts[0] = interviewTime.substring(0, semiColonPosition);
			parts[1] = interviewTime.substring(semiColonPosition + 1, emptySpacePosition);
			parts[2] = interviewTime.substring(emptySpacePosition + 1);
		}
		return parts;
	}

}
