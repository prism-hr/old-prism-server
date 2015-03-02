package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

public enum PrismStateTransitionEvaluation {

	APPLICATION_COMPLETED_OUTCOME(false, APPLICATION), //
	APPLICATION_CONFIRMED_SUPERVISION_OUTCOME(false, APPLICATION), //
	APPLICATION_COMPLETED_STATE_OUTCOME(true, APPLICATION), //
	APPLICATION_EXPORTED_OUTCOME(false, APPLICATION), //
	APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME(false, APPLICATION), //
	APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME(false, APPLICATION), //
	APPLICATION_ASSIGNED_REVIEWER_OUTCOME(false, APPLICATION), //
	APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME(false, APPLICATION), //
	APPLICATION_ASSIGNED_SUPERVISOR_OUTCOME(false, APPLICATION), //
	APPLICATION_CONFIRMED_INTERVIEW_OUTCOME(false, APPLICATION), //
	APPLICATION_ESCALATED_OUTCOME(false, APPLICATION), //
	APPLICATION_APPROVED_OUTCOME(false, APPLICATION), //
	APPLICATION_REJECTED_OUTCOME(false, APPLICATION), //
	APPLICATION_WITHDRAWN_OUTCOME(false, APPLICATION), //
	APPLICATION_CONFIRMED_ELIGIBILITY_OUTCOME(false, APPLICATION), //
	APPLICATION_PROVIDED_REFERENCE_OUTCOME(false, APPLICATION), //
	APPLICATION_VERIFICATION_COMPLETED_OUTCOME(true, APPLICATION), //
	APPLICATION_REFERENCE_COMPLETED_OUTCOME(true, APPLICATION), //
	APPLICATION_PROVIDED_REVIEW_OUTCOME(false, APPLICATION), //
	APPLICATION_RECRUITED_OUTCOME(false, APPLICATION), //
	INSTITUTION_APPROVED_OUTCOME(true, INSTITUTION), //
	INSTITUTION_CREATED_OUTCOME(false, INSTITUTION), //
	PROGRAM_APPROVED_OUTCOME(true, PROGRAM), //
	PROGRAM_IMPORTED_OUTCOME(true, PROGRAM), //
	PROGRAM_UPDATED_OUTCOME(true, PROGRAM), //
	PROGRAM_CREATED_OUTCOME(false, PROGRAM), //
	PROGRAM_ESCALATED_OUTCOME(false, PROGRAM), //
	PROJECT_APPROVED_OUTCOME(true, PROJECT), //
	PROJECT_UPDATED_OUTCOME(true, PROJECT), //
	PROJECT_CREATED_OUTCOME(false, PROJECT);

	private boolean nextStateSelection;

	private PrismScope scope;

	private Class<? extends StateTransitionResolver> resolver;

	private PrismStateTransitionEvaluation(boolean nextStateSelection, PrismScope scope) {
		this.nextStateSelection = nextStateSelection;
		this.scope = scope;
	}

	public final boolean isNextStateSelection() {
		return nextStateSelection;
	}

	public final PrismScope getScope() {
		return scope;
	}

	public Class<? extends StateTransitionResolver> getResolver() {
		return resolver;
	}

}
