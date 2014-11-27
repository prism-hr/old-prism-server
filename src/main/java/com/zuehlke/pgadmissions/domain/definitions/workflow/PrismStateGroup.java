package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

public enum PrismStateGroup {

    APPLICATION_UNSUBMITTED(1, false, APPLICATION), //
    APPLICATION_VALIDATION(2, false, APPLICATION), //
    APPLICATION_VERIFICATION(3, false, APPLICATION), //
    APPLICATION_REFERENCE(4, false, APPLICATION), //
    APPLICATION_REVIEW(5, true, APPLICATION), //
    APPLICATION_INTERVIEW(6, true, APPLICATION), //
    APPLICATION_APPROVAL(7, true, APPLICATION), //
    APPLICATION_APPROVED(8, true, APPLICATION), //
    APPLICATION_REJECTED(9, true, APPLICATION), //
    APPLICATION_WITHDRAWN(10, false, APPLICATION), //
    PROJECT_APPROVAL(1, false, PROJECT), //
    PROJECT_APPROVED(2, true, PROJECT), //
    PROJECT_REJECTED(3, false, PROJECT), //
    PROJECT_DISABLED(4, true, PROJECT), //
    PROJECT_WITHDRAWN(5, false, PROJECT), //
    PROGRAM_APPROVAL(1, false, PROGRAM), //
    PROGRAM_APPROVED(2, true, PROGRAM), //
    PROGRAM_REJECTED(3, false, PROGRAM), //
    PROGRAM_DISABLED(4, true, PROGRAM), //
    PROGRAM_WITHDRAWN(5, false, PROGRAM), //
    INSTITUTION_APPROVAL(1, false, INSTITUTION), //
    INSTITUTION_APPROVED(2, false, INSTITUTION), //
    INSTITUTION_REJECTED(3, false, INSTITUTION), //
    INSTITUTION_WITHDRAWN(4, false, INSTITUTION), //
    SYSTEM_RUNNING(1, false, SYSTEM);

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
