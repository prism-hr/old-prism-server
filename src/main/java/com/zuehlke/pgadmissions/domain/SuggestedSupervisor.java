package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity(name = "SUGGESTED_SUPERVISOR")
@Inheritance(strategy = InheritanceType.JOINED)
public class SuggestedSupervisor extends Person {

    private static final long serialVersionUID = -4309091276524405556L;
	
	private boolean aware;

	public boolean isAware() {
		return aware;
	}

	public void setAware(boolean aware) {
		this.aware = aware;
	}
	
}
