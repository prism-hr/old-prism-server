package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;

public enum PrismStateDurationDefinition {

    APPLICATION_CONFIRM_ELIGIBILITY_DURATION(3, false, APPLICATION, APPLICATION_CONFIRM_ELIGIBILITY_DURATION_LABEL, APPLICATION_CONFIRM_ELIGIBILITY_DURATION_TOOLTIP), //
    APPLICATION_PROVIDE_REFERENCE_DURATION(7, false, APPLICATION, APPLICATION_PROVIDE_REFERENCE_DURATION_LABEL, APPLICATION_PROVIDE_REFERENCE_DURATION_TOOLTIP), //
    APPLICATION_PROVIDE_REVIEW_DURATION(7, false, APPLICATION, APPLICATION_PROVIDE_REVIEW_DURATION_LABEL, APPLICATION_PROVIDE_REVIEW_DURATION_TOOLTIP), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION(3, false, APPLICATION, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION_LABEL,
            APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION_TOOLTIP), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION(7, false, APPLICATION, APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION_LABEL,
            APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION_TOOLTIP), //
    APPLICATION_CONFIRM_SUPERVISION_DURATION(3, false, APPLICATION, APPLICATION_CONFIRM_SUPERVISION_DURATION_LABEL, APPLICATION_CONFIRM_SUPERVISION_DURATION_TOOLTIP), //
    APPLICATION_ESCALATE_DURATION(84, true, APPLICATION, APPLICATION_ESCALATE_DURATION_LABEL, APPLICATION_ESCALATE_DURATION_TOOLTIP), //
    APPLICATION_PURGE_DURATION(168, false, APPLICATION, APPLICATION_PURGE_DURATION_LABEL, APPLICATION_PURGE_DURATION_TOOLTIP), //
    PROJECT_ESCALATE_DURATION(28, true, PROJECT, PROJECT_ESCALATE_DURATION_LABEL, PROJECT_ESCALATE_DURATION_TOOLTIP), //
    PROGRAM_ESCALATE_DURATION(28, true, PROGRAM, PROGRAM_ESCALATE_DURATION_LABEL, PROGRAM_ESCALATE_DURATION_TOOLTIP), //
    INSTITUTION_ESCALATE_DURATION(28, true, INSTITUTION, INSTITUTION_ESCALATE_DURATION_LABEL, INSTITUTION_ESCALATE_DURATION_TOOLTIP);

    private Integer defaultDuration;

    private boolean escalation;

    private PrismScope scope;

    private PrismDisplayPropertyDefinition label;

    private PrismDisplayPropertyDefinition tooltip;

    private PrismStateDurationDefinition(Integer defaultDuration, boolean escalation, PrismScope scope, PrismDisplayPropertyDefinition label,
            PrismDisplayPropertyDefinition tooltip) {
        this.defaultDuration = defaultDuration;
        this.escalation = escalation;
        this.scope = scope;
        this.label = label;
        this.tooltip = tooltip;
    }

    public final Integer getDefaultDuration() {
        return defaultDuration;
    }

    public final boolean isEscalation() {
        return escalation;
    }

    public final PrismScope getScope() {
        return scope;
    }

    public final PrismDisplayPropertyDefinition getLabel() {
        return label;
    }

    public final PrismDisplayPropertyDefinition getTooltip() {
        return tooltip;
    }

}
