package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.AwareStatus;
import com.zuehlke.pgadmissions.domain.enums.PrimaryStatus;

public class SupervisorBuilder {

	private Integer id;
	private AwareStatus awareSupervisor;
	private PrimaryStatus primarySupervisor;
	private String email;

	public SupervisorBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public SupervisorBuilder awareSupervisor(AwareStatus awareSupervisor) {
		this.awareSupervisor = awareSupervisor;
		return this;
	}
	
	public SupervisorBuilder primarySupervisor(PrimaryStatus primarySupervisor) {
		this.primarySupervisor = primarySupervisor;
		return this;
	}

	public SupervisorBuilder email(String email) {
		this.email = email;
		return this;
	}

	public Supervisor toSupervisor() {
		Supervisor supervisor = new Supervisor();
		supervisor.setId(id);
		supervisor.setEmail(email);
		supervisor.setAwareSupervisor(awareSupervisor);
		supervisor.setPrimarySupervisor(primarySupervisor);
		return supervisor;
	}
}
