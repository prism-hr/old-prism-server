package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "SUGGESTED_SUPERVISOR")
@Inheritance(strategy = InheritanceType.JOINED)
public class SuggestedSupervisor extends Person {

    private static final long serialVersionUID = -4309091276524405556L;
	
    @ManyToOne
    @JoinColumn(name="application_form_id")
    private ApplicationForm application;
    
	public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    private boolean aware;

	public boolean isAware() {
		return aware;
	}

	public void setAware(boolean aware) {
		this.aware = aware;
	}
	
}
