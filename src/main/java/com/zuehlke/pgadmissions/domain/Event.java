package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "EVENT")
@Inheritance(strategy = InheritanceType.JOINED)
@Access(AccessType.FIELD) 
public abstract class Event extends DomainObject<Integer> {

	
	private static final long serialVersionUID = -3417291018172094109L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "event_date")
	protected Date date;
	
	@ManyToOne
	@JoinColumn(name = "application_form_id")
	protected ApplicationForm application;
	
	@ManyToOne
	@JoinColumn(name = "registered_user_id")
	protected RegisteredUser user;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public RegisteredUser getUser() {
		return user;
	}

	public void setUser(RegisteredUser user) {
		this.user = user;
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