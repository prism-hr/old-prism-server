package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity(name = "PENDING_ROLE_NOTIFICATION")
@Access(AccessType.FIELD)
public class PendingRoleNotification extends DomainObject<Integer> {


	private static final long serialVersionUID = -2489009906410335249L;
	
	@ManyToOne
	@JoinColumn(name = "added_by_user_id")
	private RegisteredUser addedByUser;
	
	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;
	
	@ManyToOne
	@JoinColumn(name = "program_id")
	private Program program;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private RegisteredUser user;
	
	
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public RegisteredUser getUser() {
		return user;
	}

	public void setUser(RegisteredUser user) {
		this.user = user;
	}

	public RegisteredUser getAddedByUser() {
		return addedByUser;
	}

	public void setAddedByUser(RegisteredUser addedByUser) {
		this.addedByUser = addedByUser;
	}


}
