	package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Entity(name = "EVENT")
@Access(AccessType.FIELD)
public class Event extends DomainObject<Integer> {
	

	private static final long serialVersionUID = 3927731824429659338L;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "event_date")
	private Date eventDate;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.ApplicationFormStatusEnumUserType")
	@Column(name = "new_status")
	private ApplicationFormStatus newStatus;
	

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

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public ApplicationFormStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(ApplicationFormStatus newStatus) {
		this.newStatus = newStatus;
	}
	
	
	

}
