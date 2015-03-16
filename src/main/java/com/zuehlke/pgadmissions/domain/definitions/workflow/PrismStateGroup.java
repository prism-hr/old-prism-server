package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import org.apache.commons.lang3.text.WordUtils;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;


public enum PrismStateGroup {

    APPLICATION_UNSUBMITTED(1, false, APPLICATION, SYSTEM_STATE_GROUP_APPLICATION_UNSUBMITTED), //
    APPLICATION_VALIDATION(2, false, APPLICATION, SYSTEM_STATE_GROUP_APPLICATION_VALIDATION), //
    APPLICATION_VERIFICATION(3, false, APPLICATION, SYSTEM_STATE_GROUP_APPLICATION_VERIFICATION), //
    APPLICATION_REFERENCE(4, false, APPLICATION, SYSTEM_STATE_GROUP_APPLICATION_REFERENCE), //
    APPLICATION_REVIEW(5, true, APPLICATION, SYSTEM_STATE_GROUP_APPLICATION_REVIEW), //
    APPLICATION_INTERVIEW(6, true, APPLICATION, SYSTEM_STATE_GROUP_APPLICATION_INTERVIEW), //
    APPLICATION_APPROVAL(7, true, APPLICATION, SYSTEM_STATE_GROUP_APPLICATION_APPROVAL), //
    APPLICATION_APPROVED(8, true, APPLICATION, SYSTEM_STATE_GROUP_APPLICATION_APPROVED), //
    APPLICATION_REJECTED(9, true, APPLICATION, SYSTEM_STATE_GROUP_APPLICATION_REJECTED), //
    APPLICATION_WITHDRAWN(10, false, APPLICATION, SYSTEM_STATE_GROUP_APPLICATION_WITHDRAWN), //
    PROJECT_APPROVAL(1, false, PROJECT, SYSTEM_STATE_GROUP_PROJECT_APPROVAL), //
    PROJECT_APPROVED(2, true, PROJECT, SYSTEM_STATE_GROUP_PROJECT_APPROVED), //
    PROJECT_REJECTED(3, false, PROJECT, SYSTEM_STATE_GROUP_PROJECT_REJECTED), //
    PROJECT_DISABLED(4, true, PROJECT, SYSTEM_STATE_GROUP_PROJECT_DISABLED), //
    PROJECT_WITHDRAWN(5, false, PROJECT, SYSTEM_STATE_GROUP_PROJECT_WITHDRAWN), //
    PROGRAM_APPROVAL(1, false, PROGRAM, SYSTEM_STATE_GROUP_PROGRAM_APPROVAL), //
    PROGRAM_APPROVED(2, true, PROGRAM, SYSTEM_STATE_GROUP_PROGRAM_APPROVED), //
    PROGRAM_REJECTED(3, false, PROGRAM, SYSTEM_STATE_GROUP_PROGRAM_REJECTED), //
    PROGRAM_DISABLED(4, true, PROGRAM, SYSTEM_STATE_GROUP_PROGRAM_DISABLED), //
    PROGRAM_WITHDRAWN(5, false, PROGRAM, SYSTEM_STATE_GROUP_PROGRAM_WITHDRAWN), //
    INSTITUTION_APPROVAL(1, false, INSTITUTION, SYSTEM_STATE_GROUP_INSTITUTION_APPROVAL), //
    INSTITUTION_APPROVED(2, false, INSTITUTION, SYSTEM_STATE_GROUP_INSTITUTION_APPROVED), //
    INSTITUTION_REJECTED(3, false, INSTITUTION, SYSTEM_STATE_GROUP_INSTITUTION_REJECTED), //
    INSTITUTION_WITHDRAWN(4, false, INSTITUTION, SYSTEM_STATE_GROUP_INSTITUTION_WITHDRAWN), //
    SYSTEM_RUNNING(1, false, SYSTEM, SYSTEM_STATE_GROUP_SYSTEM_RUNNING);

    private Integer sequenceOrder;

    private boolean repeatable;

    private PrismScope scope;

    private PrismDisplayPropertyDefinition displayProperty;

    private PrismStateGroup(Integer sequenceOrder, boolean repeatable, PrismScope scope, PrismDisplayPropertyDefinition displayProperty) {
        this.sequenceOrder = sequenceOrder;
        this.repeatable = repeatable;
        this.scope = scope;
        this.displayProperty = displayProperty;
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

    public final PrismDisplayPropertyDefinition getDisplayProperty() {
        return displayProperty;
    }

    public String getReference() {
        String reference = "";
        String[] tokens = name().split("_");
        for (int i = 0; i < tokens.length; i++) {
            reference = i == 0 ? reference + tokens[i].toLowerCase() : WordUtils.capitalize(tokens[i]);
        }
        return reference;
    }

}
