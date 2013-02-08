package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name="APPROVAL_STATE_CHANGE_EVENT")
public class ApprovalStateChangeEvent extends StateChangeEvent {

	private static final long serialVersionUID = -2095890298884570733L;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "approval_round_id")
	private ApprovalRound approvalRound;

	public ApprovalRound getApprovalRound() {
		return approvalRound;
	}

	public void setApprovalRound(ApprovalRound approvalRound) {
		this.approvalRound = approvalRound;
	}
	
}
