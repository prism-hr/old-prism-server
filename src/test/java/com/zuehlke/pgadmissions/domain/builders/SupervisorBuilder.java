package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;

public class SupervisorBuilder {

	private Integer id;
	private RegisteredUser user;
	private Date lastNotified;
	private ApprovalRound approvalRound;

	public SupervisorBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	
	public SupervisorBuilder user(RegisteredUser user) {
		this.user = user;
		return this;
	}
	
	public SupervisorBuilder approvalRound(ApprovalRound approvalRound) {
		this.approvalRound = approvalRound;
		return this;
	}
	
	
	public SupervisorBuilder lastNotified(Date lastNotified) {
		this.lastNotified = lastNotified;
		return this;
	}
		
	
	public Supervisor toSupervisor() {
		Supervisor supervisor = new Supervisor();
		supervisor.setId(id);		
		supervisor.setUser(user);
		supervisor.setLastNotified(lastNotified);
		supervisor.setApprovalRound(approvalRound);
		return supervisor;
	}
}
