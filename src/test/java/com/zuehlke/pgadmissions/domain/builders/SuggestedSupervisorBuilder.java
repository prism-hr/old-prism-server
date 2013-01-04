package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;

public class SuggestedSupervisorBuilder {

	private Integer id;
	private String email;
	private String firstname;
	private String lastname;
	private boolean aware;

	public SuggestedSupervisorBuilder aware(boolean aware) {
		this.aware = aware;
		return this;
	}
	
	public SuggestedSupervisorBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public SuggestedSupervisorBuilder email(String email) {
		this.email = email;
		return this;
	}

	public SuggestedSupervisorBuilder firstname(String firstname) {
		this.firstname = firstname;
		return this;
	}
	
	public SuggestedSupervisorBuilder lastname(String lastname) {
		this.lastname = lastname;
		return this;
	}
	
	public SuggestedSupervisor build() {
		SuggestedSupervisor suggestedSupervisor = new SuggestedSupervisor();
		suggestedSupervisor.setEmail(email);
		suggestedSupervisor.setId(id);
		suggestedSupervisor.setFirstname(firstname);
		suggestedSupervisor.setLastname(lastname);
		suggestedSupervisor.setAware(aware);
		return suggestedSupervisor;
	}
}
