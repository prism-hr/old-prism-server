package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Entity(name="STATE_CHANGE_EVENT")
public class StateChangeEvent extends Event {

	private static final long serialVersionUID = 3927731824429659338L;

	@Enumerated(EnumType.STRING)
	@Column(name = "new_status")
	private ApplicationFormStatus newStatus;

	public ApplicationFormStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(ApplicationFormStatus newStatus) {
		this.newStatus = newStatus;
	}

	

}
