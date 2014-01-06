package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="APPROVAL_EVALUATION_COMMENT")
public class ApprovalEvaluationComment extends StateChangeComment {

	private static final long serialVersionUID = 2184172372328153404L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="approval_id")
	private ApprovalRound approvalRound = null;

	public ApprovalRound getApprovalRound() {
		return approvalRound;
	}

	public void setApprovalRound(ApprovalRound approvalRound) {
		this.approvalRound = approvalRound;
	}

}