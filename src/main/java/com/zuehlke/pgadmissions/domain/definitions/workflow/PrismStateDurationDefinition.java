package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuehlke.pgadmissions.domain.definitions.PrismConfigurationLocalizable;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;

public enum PrismStateDurationDefinition implements PrismConfigurationLocalizable {

    APPLICATION_CONFIRM_ELIGIBILITY_DURATION(3, false, APPLICATION, SYSTEM_APPLICATION_CONFIRM_ELIGIBILITY_DURATION_LABEL,
            SYSTEM_APPLICATION_CONFIRM_ELIGIBILITY_DURATION_HINT), //
    APPLICATION_PROVIDE_REFERENCE_DURATION(7, false, APPLICATION, SYSTEM_APPLICATION_PROVIDE_REFERENCE_DURATION_LABEL, SYSTEM_APPLICATION_PROVIDE_REFERENCE_DURATION_HINT), //
    APPLICATION_PROVIDE_REVIEW_DURATION(7, false, APPLICATION, SYSTEM_APPLICATION_PROVIDE_REVIEW_DURATION_LABEL, SYSTEM_APPLICATION_PROVIDE_REVIEW_DURATION_HINT), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION(3, false, APPLICATION, SYSTEM_APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION_LABEL,
            SYSTEM_APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION_HINT), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION(7, false, APPLICATION, SYSTEM_APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION_LABEL,
            SYSTEM_APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION_HINT), //
    APPLICATION_CONFIRM_SUPERVISION_DURATION(3, false, APPLICATION, SYSTEM_APPLICATION_CONFIRM_SUPERVISION_DURATION_LABEL,
            SYSTEM_APPLICATION_CONFIRM_SUPERVISION_DURATION_HINT), //
    APPLICATION_ESCALATE_DURATION(84, true, APPLICATION, SYSTEM_APPLICATION_ESCALATE_DURATION_LABEL, SYSTEM_APPLICATION_ESCALATE_DURATION_HINT), //
    APPLICATION_RESERVE_DURATION(84, true, APPLICATION, SYSTEM_APPLICATION_RESERVE_DURATION_LABEL, SYSTEM_APPLICATION_RESERVE_DURATION_HINT), //
    APPLICATION_PURGE_DURATION(364, false, APPLICATION, SYSTEM_APPLICATION_PURGE_DURATION_LABEL, SYSTEM_APPLICATION_PURGE_DURATION_HINT), //
    PROJECT_ESCALATE_DURATION(28, true, PROJECT, SYSTEM_PROJECT_ESCALATE_DURATION_LABEL, SYSTEM_PROJECT_ESCALATE_DURATION_HINT), //
    PROGRAM_ESCALATE_DURATION(28, true, PROGRAM, SYSTEM_PROGRAM_ESCALATE_DURATION_LABEL, SYSTEM_PROGRAM_ESCALATE_DURATION_HINT), //
    INSTITUTION_ESCALATE_DURATION(28, true, INSTITUTION, SYSTEM_INSTITUTION_ESCALATE_DURATION_LABEL, SYSTEM_INSTITUTION_ESCALATE_DURATION_HINT);

    private Integer defaultDuration;

    private boolean escalation;

    private PrismScope scope;

    private PrismDisplayPropertyDefinition label;

    private PrismDisplayPropertyDefinition tooltip;

    PrismStateDurationDefinition(Integer defaultDuration, boolean escalation, PrismScope scope, PrismDisplayPropertyDefinition label,
                                 PrismDisplayPropertyDefinition tooltip) {
        this.defaultDuration = defaultDuration;
        this.escalation = escalation;
        this.scope = scope;
        this.label = label;
        this.tooltip = tooltip;
    }

    public Integer getDefaultDuration() {
        return defaultDuration;
    }

    public boolean isEscalation() {
        return escalation;
    }

    public PrismScope getScope() {
        return scope;
    }

    public PrismDisplayPropertyDefinition getLabel() {
        return label;
    }

    public PrismDisplayPropertyDefinition getTooltip() {
        return tooltip;
    }

}
