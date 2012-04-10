package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.AwareStatus;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

public class SupervisorBuilder {

	private Integer id;
	private AwareStatus awareSupervisor;
	private String email;
	private String firstname;
	private String lastname;

	public SupervisorBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public SupervisorBuilder awareSupervisor(AwareStatus awareSupervisor) {
		this.awareSupervisor = awareSupervisor;
		return this;
	}
	
	public SupervisorBuilder email(String email) {
		this.email = email;
		return this;
	}

	public SupervisorBuilder firstname(String firstname) {
		this.firstname = firstname;
		return this;
	}
	
	public SupervisorBuilder lastname(String lastname) {
		this.lastname = lastname;
		return this;
	}
	
	public Supervisor toSupervisor() {
		Supervisor supervisor = new Supervisor();
		supervisor.setId(id);
		supervisor.setEmail(email);
		supervisor.setFirstname(firstname);
		supervisor.setLastname(lastname);
		supervisor.setAwareSupervisor(awareSupervisor);
		return supervisor;
	}
}
