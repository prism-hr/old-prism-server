package com.zuehlke.pgadmissions.domain.enums;

public enum OpportunityRequestCommentType {

    REJECT(OpportunityRequestStatus.REJECTED), //
    APPROVE(OpportunityRequestStatus.APPROVED), //
    REVISE(OpportunityRequestStatus.REVISED);

    private final OpportunityRequestStatus targetRequestStatus;

    private OpportunityRequestCommentType(OpportunityRequestStatus targetRequestStatus) {
        this.targetRequestStatus = targetRequestStatus;
    }

    public OpportunityRequestStatus getTargetRequestStatus() {
        return targetRequestStatus;
    }

}
