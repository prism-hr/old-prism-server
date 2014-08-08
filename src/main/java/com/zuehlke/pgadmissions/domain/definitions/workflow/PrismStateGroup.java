package com.zuehlke.pgadmissions.domain.definitions.workflow;

public enum PrismStateGroup {
    
    APPLICATION_APPROVAL(5, PrismScope.APPLICATION), //
    APPLICATION_APPROVED(6, PrismScope.APPLICATION), //
    APPLICATION_INTERVIEW(4, PrismScope.APPLICATION), //
    APPLICATION_REJECTED(7, PrismScope.APPLICATION), //
    APPLICATION_REVIEW(3, PrismScope.APPLICATION), //
    APPLICATION_UNSUBMITTED(1, PrismScope.APPLICATION), //
    APPLICATION_VALIDATION(2, PrismScope.APPLICATION), //
    APPLICATION_WITHDRAWN(8, PrismScope.APPLICATION), //
    INSTITUTION_APPROVAL(1, PrismScope.INSTITUTION), //
    INSTITUTION_APPROVED(2, PrismScope.INSTITUTION), //
    INSTITUTION_REJECTED(3, PrismScope.INSTITUTION), //
    INSTITUTION_WITHDRAWN(4, PrismScope.INSTITUTION), //
    PROGRAM_APPROVAL(1, PrismScope.PROGRAM), //
    PROGRAM_APPROVED(2, PrismScope.PROGRAM), //
    PROGRAM_DISABLED(4, PrismScope.PROGRAM), //
    PROGRAM_REJECTED(3, PrismScope.PROGRAM), //
    PROGRAM_WITHDRAWN(5, PrismScope.PROGRAM), //
    PROJECT_APPROVAL(1, PrismScope.PROJECT), //
    PROJECT_APPROVED(2, PrismScope.PROJECT), //
    PROJECT_DISABLED(4, PrismScope.PROJECT), //
    PROJECT_REJECTED(3, PrismScope.PROJECT), //
    PROJECT_WITHDRAWN(5, PrismScope.PROJECT), //
    SYSTEM_RUNNING(1, PrismScope.SYSTEM);
    
    private Integer sequenceOrder;
    
    private PrismScope scope;
    
    private PrismStateGroup(int sequenceOrder, PrismScope scope) {
        this.sequenceOrder = sequenceOrder;
        this.scope = scope;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public PrismScope getScope() {
        return scope;
    }

}
