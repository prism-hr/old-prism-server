package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

public enum PrismStateGroup {

    APPLICATION_UNSUBMITTED(1, false, false, APPLICATION), //
    APPLICATION_VALIDATION(2, false, false, APPLICATION), //
    APPLICATION_VERIFICATION(3, false, true, APPLICATION), //
    APPLICATION_REFERENCE(4, false, true, APPLICATION), //
    APPLICATION_REVIEW(5, true, false, APPLICATION), //
    APPLICATION_INTERVIEW(6, true, false, APPLICATION), //
    APPLICATION_APPROVAL(7, true, false, APPLICATION), //
    APPLICATION_APPROVED(8, true, false, APPLICATION), //
    APPLICATION_REJECTED(9, true, false, APPLICATION), //
    APPLICATION_WITHDRAWN(10, false, false, APPLICATION), //
    PROJECT_APPROVAL(1, false, false, PROJECT), //
    PROJECT_APPROVED(2, true, false, PROJECT), //
    PROJECT_REJECTED(3, false, false, PROJECT), //
    PROJECT_DISABLED(4, true, false, PROJECT), //
    PROJECT_WITHDRAWN(5, false, false, PROJECT), //
    PROGRAM_APPROVAL(1, false, false, PROGRAM), //
    PROGRAM_APPROVED(2, true, false, PROGRAM), //
    PROGRAM_REJECTED(3, false, false, PROGRAM), //
    PROGRAM_DISABLED(4, true, false, PROGRAM), //
    PROGRAM_WITHDRAWN(5, false, false, PROGRAM), //
    INSTITUTION_APPROVAL(1, false, false, INSTITUTION), //
    INSTITUTION_APPROVED(2, false, false, INSTITUTION), //
    INSTITUTION_REJECTED(3, false, false, INSTITUTION), //
    INSTITUTION_WITHDRAWN(4, false, false, INSTITUTION), //
    SYSTEM_RUNNING(1, false, false, SYSTEM);

    private Integer sequenceOrder;

    private boolean repeatable;
    
    private boolean parallelizable;

    private PrismScope scope;

    private PrismStateGroup(Integer sequenceOrder, boolean repeatable, boolean parallelizable, PrismScope scope) {
        this.sequenceOrder = sequenceOrder;
        this.repeatable = repeatable;
        this.parallelizable = parallelizable;
        this.scope = scope;
    }
    
    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public final boolean isRepeatable() {
        return repeatable;
    }

    public final boolean isParallelizable() {
        return parallelizable;
    }

    public PrismScope getScope() {
        return scope;
    }

}
