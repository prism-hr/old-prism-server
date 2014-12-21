package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import org.apache.commons.lang3.text.WordUtils;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;

public enum PrismStateGroup {

    APPLICATION_UNSUBMITTED(1, false, APPLICATION, APPLICATION_UNSUBMITTED_STATE_GROUP), //
    APPLICATION_VALIDATION(2, false, APPLICATION, APPLICATION_VALIDATION_STATE_GROUP), //
    APPLICATION_VERIFICATION(3, false, APPLICATION, APPLICATION_VERIFICATION_STATE_GROUP), //
    APPLICATION_REFERENCE(4, false, APPLICATION, APPLICATION_REFERENCE_STATE_GROUP), //
    APPLICATION_REVIEW(5, true, APPLICATION, APPLICATION_REVIEW_STATE_GROUP), //
    APPLICATION_INTERVIEW(6, true, APPLICATION, APPLICATION_INTERVIEW_STATE_GROUP), //
    APPLICATION_APPROVAL(7, true, APPLICATION, APPLICATION_APPROVAL_STATE_GROUP), //
    APPLICATION_APPROVED(8, true, APPLICATION, APPLICATION_APPROVED_STATE_GROUP), //
    APPLICATION_REJECTED(9, true, APPLICATION, APPLICATION_REJECTED_STATE_GROUP), //
    APPLICATION_WITHDRAWN(10, false, APPLICATION, APPLICATION_WITHDRAWN_STATE_GROUP), //
    PROJECT_APPROVAL(1, false, PROJECT, PROJECT_APPROVAL_STATE_GROUP), //
    PROJECT_APPROVED(2, true, PROJECT, PROJECT_APPROVED_STATE_GROUP), //
    PROJECT_REJECTED(3, false, PROJECT, PROJECT_REJECTED_STATE_GROUP), //
    PROJECT_DISABLED(4, true, PROJECT, PROJECT_DISABLED_STATE_GROUP), //
    PROJECT_WITHDRAWN(5, false, PROJECT, PROJECT_WITHDRAWN_STATE_GROUP), //
    PROGRAM_APPROVAL(1, false, PROGRAM, PROGRAM_APPROVAL_STATE_GROUP), //
    PROGRAM_APPROVED(2, true, PROGRAM, PROGRAM_APPROVED_STATE_GROUP), //
    PROGRAM_REJECTED(3, false, PROGRAM, PROGRAM_REJECTED_STATE_GROUP), //
    PROGRAM_DISABLED(4, true, PROGRAM, PROGRAM_DISABLED_STATE_GROUP), //
    PROGRAM_WITHDRAWN(5, false, PROGRAM, PROGRAM_WITHDRAWN_STATE_GROUP), //
    INSTITUTION_APPROVAL(1, false, INSTITUTION, INSTITUTION_APPROVAL_STATE_GROUP), //
    INSTITUTION_APPROVED(2, false, INSTITUTION, INSTITUTION_APPROVED_STATE_GROUP), //
    INSTITUTION_REJECTED(3, false, INSTITUTION, INSTITUTION_REJECTED_STATE_GROUP), //
    INSTITUTION_WITHDRAWN(4, false, INSTITUTION, INSTITUTION_WITHDRAWN_STATE_GROUP), //
    SYSTEM_RUNNING(1, false, SYSTEM, SYSTEM_RUNNING_STATE_GROUP);

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
