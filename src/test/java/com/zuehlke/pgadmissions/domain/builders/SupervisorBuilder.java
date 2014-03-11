package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;

public class SupervisorBuilder {

	private Integer id;
	private RegisteredUser user;
	private Date confirmedSupervisionDate;
	private ApprovalRound approvalRound;
	private Boolean isPrimary = false;
	private Boolean confirmedSupervision;
	private String declinedSupervisionReason;

	public SupervisorBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public SupervisorBuilder confirmedSupervisionDate(final Date date) {
	    this.confirmedSupervisionDate = date;
	    return this;
	}

	public SupervisorBuilder declinedSupervisionReason(String reason) {
        this.declinedSupervisionReason = reason;
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
	
	public SupervisorBuilder isPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
        return this;
    }
	
	public SupervisorBuilder confirmedSupervision(Boolean hasConfirmed) {
	    this.confirmedSupervision = hasConfirmed;
	    return this;
	}
	
	public Supervisor build() {
		Supervisor supervisor = new Supervisor();
		supervisor.setId(id);		
		supervisor.setUser(user);
		supervisor.setApprovalRound(approvalRound);
		supervisor.setIsPrimary(isPrimary);
		supervisor.setConfirmedSupervision(confirmedSupervision);
		supervisor.setDeclinedSupervisionReason(declinedSupervisionReason);
		supervisor.setConfirmedSupervisionDate(confirmedSupervisionDate);
		return supervisor;
	}
}
