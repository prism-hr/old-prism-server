package com.zuehlke.pgadmissions.domain.definitions.workflow;

public enum PrismStateGroup {
    
    APPLICATION_APPROVAL(5, true, PrismScope.APPLICATION), //
    APPLICATION_APPROVED(6, true, PrismScope.APPLICATION), //
    APPLICATION_INTERVIEW(4, true, PrismScope.APPLICATION), //
    APPLICATION_REJECTED(7, true, PrismScope.APPLICATION), //
    APPLICATION_REVIEW(3, true, PrismScope.APPLICATION), //
    APPLICATION_UNSUBMITTED(1, false, PrismScope.APPLICATION), //
    APPLICATION_VALIDATION(2, false, PrismScope.APPLICATION), //
    APPLICATION_WITHDRAWN(8, false, PrismScope.APPLICATION), //
    INSTITUTION_APPROVAL(1, false, PrismScope.INSTITUTION), //
    INSTITUTION_APPROVED(2, false, PrismScope.INSTITUTION), //
    INSTITUTION_REJECTED(3, false, PrismScope.INSTITUTION), //
    INSTITUTION_WITHDRAWN(4, false, PrismScope.INSTITUTION), //
    PROGRAM_APPROVAL(1, false, PrismScope.PROGRAM), //
    PROGRAM_APPROVED(2, true, PrismScope.PROGRAM), //
    PROGRAM_DISABLED(4, true, PrismScope.PROGRAM), //
    PROGRAM_REJECTED(3, false, PrismScope.PROGRAM), //
    PROGRAM_WITHDRAWN(5, false, PrismScope.PROGRAM), //
    PROJECT_APPROVAL(1, false, PrismScope.PROJECT), //
    PROJECT_APPROVED(2, true, PrismScope.PROJECT), //
    PROJECT_DISABLED(4, true, PrismScope.PROJECT), //
    PROJECT_REJECTED(3, false, PrismScope.PROJECT), //
    PROJECT_WITHDRAWN(5, false, PrismScope.PROJECT), //
    SYSTEM_RUNNING(1, false, PrismScope.SYSTEM);
    
    private Integer sequenceOrder;
    
    private boolean repeatable;
    
    private PrismScope scope;
    
    private PrismStateGroup(Integer sequenceOrder, boolean repeatable, PrismScope scope) {
        this.sequenceOrder = sequenceOrder;
        this.repeatable = repeatable;
        this.scope = scope;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public final boolean isRepeatable() {
        return repeatable;
    }

    public PrismScope getScope() {
        return scope;
    }

}
