package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

public enum PrismStateDurationDefinition {

	APPLICATION_CONFIRM_ELIGIBILITY_DURATION(3, false, APPLICATION), //
	APPLICATION_PROVIDE_REFERENCE_DURATION(7, false, APPLICATION), //
	APPLICATION_PROVIDE_REVIEW_DURATION(7, false, APPLICATION), //
	APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION(3, false, APPLICATION), //
	APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION(7, false, APPLICATION), //
	APPLICATION_CONFIRM_SUPERVISION_DURATION(3, false, APPLICATION), //
	APPLICATION_ESCALATE_DURATION(84, true, APPLICATION), //
	APPLICATION_RESERVE_DURATION(84, true, APPLICATION), //
	APPLICATION_PURGE_DURATION(364, false, APPLICATION), //
	PROJECT_ESCALATE_DURATION(28, true, PROJECT), //
	PROGRAM_ESCALATE_DURATION(28, true, PROGRAM), //
	INSTITUTION_ESCALATE_DURATION(28, true, INSTITUTION);

	private Integer defaultDuration;

	private boolean escalation;

	private PrismScope scope;

	PrismStateDurationDefinition(Integer defaultDuration, boolean escalation, PrismScope scope) {
		this.defaultDuration = defaultDuration;
		this.escalation = escalation;
		this.scope = scope;
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

}
