package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity(name="APPLICATION_REVIEW")
@Access(AccessType.FIELD) 
public class ApplicationReview extends DomainObject<Integer>{

	private static final long serialVersionUID = 2861325991249900547L;

	private String comment;
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	@ManyToOne
	@JoinColumn(name="user_id")
	private RegisteredUser user = null;
	
	@ManyToOne
	@JoinColumn(name="application_form_id")
	private ApplicationForm application = null;
	
	
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
