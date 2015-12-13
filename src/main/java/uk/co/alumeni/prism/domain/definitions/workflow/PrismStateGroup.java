package uk.co.alumeni.prism.domain.definitions.workflow;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismLocalizableDefinition;

public enum PrismStateGroup implements PrismLocalizableDefinition {

    APPLICATION_UNSUBMITTED(PrismScope.APPLICATION), //
    APPLICATION_VALIDATION(PrismScope.APPLICATION), //
    APPLICATION_REVIEW(PrismScope.APPLICATION), //
    APPLICATION_INTERVIEW(PrismScope.APPLICATION), //
    APPLICATION_APPROVAL(PrismScope.APPLICATION), //
    APPLICATION_REFERENCE(PrismScope.APPLICATION), //
    APPLICATION_APPROVED(PrismScope.APPLICATION), //
    APPLICATION_REJECTED(PrismScope.APPLICATION), //
    APPLICATION_WITHDRAWN(PrismScope.APPLICATION), //

    PROJECT_UNSUBMITTED(PrismScope.PROJECT), //
    PROJECT_APPROVAL(PrismScope.PROJECT), //
    PROJECT_APPROVED(PrismScope.PROJECT), //
    PROJECT_REJECTED(PrismScope.PROJECT), //
    PROJECT_DISABLED(PrismScope.PROJECT), //
    PROJECT_WITHDRAWN(PrismScope.PROJECT), //

    PROGRAM_UNSUBMITTED(PrismScope.PROGRAM), //
    PROGRAM_APPROVAL(PrismScope.PROGRAM), //
    PROGRAM_APPROVED(PrismScope.PROGRAM), //
    PROGRAM_REJECTED(PrismScope.PROGRAM), //
    PROGRAM_DISABLED(PrismScope.PROGRAM), //
    PROGRAM_WITHDRAWN(PrismScope.PROGRAM), //

    DEPARTMENT_UNSUBMITTED(PrismScope.DEPARTMENT), //
    DEPARTMENT_APPROVAL(PrismScope.DEPARTMENT), //
    DEPARTMENT_APPROVED(PrismScope.DEPARTMENT), //
    DEPARTMENT_REJECTED(PrismScope.DEPARTMENT), //
    DEPARTMENT_DISABLED(PrismScope.DEPARTMENT), //
    DEPARTMENT_WITHDRAWN(PrismScope.DEPARTMENT), //

    INSTITUTION_UNSUBMITTED(PrismScope.INSTITUTION), //
    INSTITUTION_APPROVAL(PrismScope.INSTITUTION), //
    INSTITUTION_APPROVED(PrismScope.INSTITUTION), //
    INSTITUTION_REJECTED(PrismScope.INSTITUTION), //
    INSTITUTION_DISABLED(PrismScope.INSTITUTION), //
    INSTITUTION_WITHDRAWN(PrismScope.INSTITUTION), //

    SYSTEM_RUNNING(PrismScope.SYSTEM);

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
