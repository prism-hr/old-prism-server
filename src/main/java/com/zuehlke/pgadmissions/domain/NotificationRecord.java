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

import com.zuehlke.pgadmissions.domain.enums.NotificationType;

@Entity(name = "NOTIFICATION_RECORD")
@Access(AccessType.FIELD)
public class NotificationRecord extends DomainObject<Integer> {

	private static final long serialVersionUID = 8927883549224930562L;

	@ManyToOne
	@JoinColumn(name = "application_form_id")
	private ApplicationForm application;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private RegisteredUser user;

	@Column(name = "notification_date")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date date;

	@Column(name = "notification_type")
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.NotificationTypeEnumUserType")
	private NotificationType notificationType;

	public NotificationRecord() {

	}

	public NotificationRecord(NotificationType notificationType) {
		this.notificationType = notificationType;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType type) {
		this.notificationType = type;
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
}
