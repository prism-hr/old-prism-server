package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "PENDING_ROLE_NOTIFICATION")
public class PendingRoleNotification implements Serializable {

	private static final long serialVersionUID = -2489009906410335249L;
	
    @Id
    @GeneratedValue
    private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "added_by_user_id")
	private User addedByUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
	private Role role;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "program_id")
	private Program program;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "notification_date")
	private Date notificationDate;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getAddedByUser() {
		return addedByUser;
	}

	public void setAddedByUser(User addedByUser) {
		this.addedByUser = addedByUser;
	}

	public Date getNotificationDate() {
	    return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
	    this.notificationDate = notificationDate;
	}
}
