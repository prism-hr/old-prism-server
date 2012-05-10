package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity(name = "INTERVIEW")
@Access(AccessType.FIELD)
public class Interview extends DomainObject<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -730673777949846236L;

	@Column(name = "last_notified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastNotified;

	@Column(name = "further_details")
	private String furtherDetails;
	
	@OneToOne
	@JoinColumn(name = "application_form_id")
	private ApplicationForm application;
	
	@Column(name = "location_url")
	private String locationURL;
	
	@Temporal(TemporalType.DATE)
	@Column(name="due_date")
	private Date dueDate;
	
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

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
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
	
}
