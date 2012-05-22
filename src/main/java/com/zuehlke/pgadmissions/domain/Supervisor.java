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

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.AwareStatus;

@Entity(name = "SUPERVISOR")
@Access(AccessType.FIELD)
public class Supervisor extends DomainObject<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -189828903532203309L;

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

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.AwareStatusEnumUserType")
	@Column(name = "aware_supervisor")
	private AwareStatus awareSupervisor;
	
	@ManyToOne
	@JoinColumn(name = "registered_user_id")
	private RegisteredUser user;
	
	@Column(name = "last_notified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastNotified;

	private String email;
	private String firstname;
	private String lastname;


	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		if(StringUtils.isBlank(firstname)){
			this.firstname = null;
		}else {
			this.firstname = firstname;
		}
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		if(StringUtils.isBlank(lastname)){
			this.lastname = null;
		}
		else {
			this.lastname = lastname;
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if(StringUtils.isBlank(email)){
			this.email = null;
		}
		else {
			this.email = email;
		}
	}

	public AwareStatus getAwareSupervisor() {
		return awareSupervisor;
	}

	public void setAwareSupervisor(AwareStatus awareSupervisor) {
		this.awareSupervisor = awareSupervisor;
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

}
