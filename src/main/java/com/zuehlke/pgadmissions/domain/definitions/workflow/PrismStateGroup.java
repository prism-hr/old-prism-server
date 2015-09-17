package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocalizableDefinition;

public enum PrismStateGroup implements PrismLocalizableDefinition {

    APPLICATION_UNSUBMITTED(APPLICATION), //
    APPLICATION_VALIDATION(APPLICATION), //
    APPLICATION_VERIFICATION(APPLICATION), 
    APPLICATION_REFERENCE(APPLICATION), //
    APPLICATION_REVIEW(APPLICATION), //
    APPLICATION_INTERVIEW(APPLICATION), //
    APPLICATION_APPROVAL(APPLICATION), //
    APPLICATION_APPROVED(APPLICATION), //
    APPLICATION_RESERVED(APPLICATION), //
    APPLICATION_REJECTED(APPLICATION), //
    APPLICATION_WITHDRAWN(APPLICATION), //

    PROJECT_APPROVAL(PROJECT), //
    PROJECT_APPROVED(PROJECT), //
    PROJECT_REJECTED(PROJECT), //
    PROJECT_DISABLED(PROJECT), //
    PROJECT_WITHDRAWN(PROJECT), //

    PROGRAM_APPROVAL(PROGRAM), //
    PROGRAM_APPROVED(PROGRAM), //
    PROGRAM_REJECTED(PROGRAM), //
    PROGRAM_DISABLED(PROGRAM), //
    PROGRAM_WITHDRAWN(PROGRAM), //

    DEPARTMENT_APPROVAL(DEPARTMENT), //
    DEPARTMENT_APPROVED(DEPARTMENT), //
    DEPARTMENT_REJECTED(DEPARTMENT), //
    DEPARTMENT_DISABLED(DEPARTMENT), //
    DEPARTMENT_WITHDRAWN(DEPARTMENT), //

    INSTITUTION_APPROVAL(INSTITUTION), //
    INSTITUTION_APPROVED(INSTITUTION), //
    INSTITUTION_REJECTED(INSTITUTION), //
    INSTITUTION_DISABLED(INSTITUTION), //
    INSTITUTION_WITHDRAWN(INSTITUTION), //

    SYSTEM_RUNNING(SYSTEM);

    private PrismScope scope;

    private PrismStateGroup(PrismScope scope) {
        this.scope = scope;
    }

    public PrismScope getScope() {
        return scope;
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_STATE_GROUP_" + name());
    }

}
