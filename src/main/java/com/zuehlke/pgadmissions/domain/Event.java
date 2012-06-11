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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Entity(name = "EVENT")
@Access(AccessType.FIELD)
public class Event extends TimelineEntity {
	

	private static final long serialVersionUID = 3927731824429659338L;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "event_date")
	private Date date;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.ApplicationFormStatusEnumUserType")
	@Column(name = "new_status")
	private ApplicationFormStatus newStatus;
	
	@ManyToOne
	@JoinColumn(name="application_form_id")
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

	/* (non-Javadoc)
	 * @see com.zuehlke.pgadmissions.domain.TimelineEntity#getDate()
	 */
	@Override
	public Date getDate() {
		return date;
	}

	public void setDate(Date eventDate) {
		this.date = eventDate;
	}

	public ApplicationFormStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(ApplicationFormStatus newStatus) {
		this.newStatus = newStatus;
	}

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	


}
