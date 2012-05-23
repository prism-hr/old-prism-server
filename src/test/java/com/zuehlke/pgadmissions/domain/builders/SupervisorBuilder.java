package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.AwareStatus;

public class SupervisorBuilder {

	private Integer id;
	private AwareStatus awareSupervisor;
	private String email;
	private String firstname;
	private String lastname;
	private RegisteredUser user;
	private Date lastModified;

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
	
	public SupervisorBuilder user(RegisteredUser user) {
		this.user = user;
		return this;
	}
	
	
	public SupervisorBuilder lastModified(Date lastModified) {
		this.lastModified = lastModified;
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
		supervisor.setUser(user);
		supervisor.setLastNotified(lastModified);
		return supervisor;
	}
}
