package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Entity(name="STATE_CHANGE_EVENT")
@Access(AccessType.FIELD)
public class StateChangeEvent extends Event {

	private static final long serialVersionUID = 3927731824429659338L;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.ApplicationFormStatusEnumUserType")
	@Column(name = "new_status")
	private ApplicationFormStatus newStatus;


	public ApplicationFormStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(ApplicationFormStatus newStatus) {
		this.newStatus = newStatus;
	}

	

}
