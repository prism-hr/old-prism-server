package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name="REFERENCE_EVENT")
@Access(AccessType.FIELD)
public class ReferenceEvent extends Event {


	private static final long serialVersionUID = -4275738925634278374L;
		
	@OneToOne
	@JoinColumn(name = "referee_id")	
	private Referee referee;


	public Referee getReferee() {
		return referee;
	}


	public void setReferee(Referee referee) {
		this.referee = referee;
	}
		
}
