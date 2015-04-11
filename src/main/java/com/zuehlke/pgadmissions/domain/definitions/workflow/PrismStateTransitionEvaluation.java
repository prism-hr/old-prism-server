package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationApprovedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationAssignedInterviewerResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationAssignedReviewerResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationAssignedSupervisorResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationCompletedReferenceStateResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationCompletedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationCompletedStateResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationCompletedVerificationStateResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationConfirmedEligibilityResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationConfirmedInterviewResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationConfirmedSupervisionResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationEscalatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationExportedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationProvidedInterviewAvailabilityResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationProvidedInterviewFeedbackResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationProvidedReferenceResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationProvidedReviewResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationRejectedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationUpdateInterviewAvailabilityResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ApplicationWithdrawnResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.InstitutionApprovedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.InstitutionCreatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ProgramApprovedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ProgramCreatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ProgramEscalatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ProgramImportedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ProgramUpdatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ProjectCreatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ProjectRestoredResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.ProjectUpdatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

public enum PrismStateTransitionEvaluation {

	APPLICATION_COMPLETED_OUTCOME(false, APPLICATION, ApplicationCompletedResolver.class), //
	APPLICATION_CONFIRMED_SUPERVISION_OUTCOME(false, APPLICATION, ApplicationConfirmedSupervisionResolver.class), //
	APPLICATION_COMPLETED_STATE_OUTCOME(true, APPLICATION, ApplicationCompletedStateResolver.class), //
	APPLICATION_EXPORTED_OUTCOME(false, APPLICATION, ApplicationExportedResolver.class), //
	APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME(false, APPLICATION, ApplicationProvidedInterviewAvailabilityResolver.class), //
	APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME(false, APPLICATION, ApplicationProvidedInterviewFeedbackResolver.class), //
	APPLICATION_ASSIGNED_REVIEWER_OUTCOME(false, APPLICATION, ApplicationAssignedReviewerResolver.class), //
	APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME(false, APPLICATION, ApplicationAssignedInterviewerResolver.class), //
	APPLICATION_ASSIGNED_SUPERVISOR_OUTCOME(false, APPLICATION, ApplicationAssignedSupervisorResolver.class), //
	APPLICATION_CONFIRMED_INTERVIEW_OUTCOME(false, APPLICATION, ApplicationConfirmedInterviewResolver.class), //
	APPLICATION_ESCALATED_OUTCOME(false, APPLICATION, ApplicationEscalatedResolver.class), //
	APPLICATION_APPROVED_OUTCOME(false, APPLICATION, ApplicationApprovedResolver.class), //
	APPLICATION_REJECTED_OUTCOME(false, APPLICATION, ApplicationRejectedResolver.class), //
	APPLICATION_WITHDRAWN_OUTCOME(false, APPLICATION, ApplicationWithdrawnResolver.class), //
	APPLICATION_CONFIRMED_ELIGIBILITY_OUTCOME(false, APPLICATION, ApplicationConfirmedEligibilityResolver.class), //
	APPLICATION_PROVIDED_REFERENCE_OUTCOME(false, APPLICATION, ApplicationProvidedReferenceResolver.class), //
	APPLICATION_COMPLETED_VERIFICATION_STATE_OUTCOME(true, APPLICATION, ApplicationCompletedVerificationStateResolver.class), //
	APPLICATION_COMPLETED_REFERENCE_STATE_OUTCOME(true, APPLICATION, ApplicationCompletedReferenceStateResolver.class), //
	APPLICATION_PROVIDED_REVIEW_OUTCOME(false, APPLICATION, ApplicationProvidedReviewResolver.class), //
	APPLICATION_UPDATED_INTERVIEW_AVAILABILITY_OUTCOME(false, APPLICATION, ApplicationUpdateInterviewAvailabilityResolver.class), //
	INSTITUTION_APPROVED_OUTCOME(true, INSTITUTION, InstitutionApprovedResolver.class), //
	INSTITUTION_CREATED_OUTCOME(false, INSTITUTION, InstitutionCreatedResolver.class), //
	PROGRAM_APPROVED_OUTCOME(true, PROGRAM, ProgramApprovedResolver.class), //
	PROGRAM_IMPORTED_OUTCOME(true, PROGRAM, ProgramImportedResolver.class), //
	PROGRAM_UPDATED_OUTCOME(true, PROGRAM, ProgramUpdatedResolver.class), //
	PROGRAM_CREATED_OUTCOME(false, PROGRAM, ProgramCreatedResolver.class), //
	PROGRAM_ESCALATED_OUTCOME(false, PROGRAM, ProgramEscalatedResolver.class), //
	PROJECT_APPROVED_OUTCOME(true, PROJECT, ProgramApprovedResolver.class), //
	PROJECT_UPDATED_OUTCOME(true, PROJECT, ProjectUpdatedResolver.class), //
	PROJECT_CREATED_OUTCOME(false, PROJECT, ProjectCreatedResolver.class), //
	PROJECT_RESTORED_OUTCOME(false, PROJECT, ProjectRestoredResolver.class);

	private boolean nextStateSelection;

	private PrismScope scope;

	private Class<? extends StateTransitionResolver> resolver;

	private PrismStateTransitionEvaluation(boolean nextStateSelection, PrismScope scope, Class<? extends StateTransitionResolver> resolver) {
		this.nextStateSelection = nextStateSelection;
		this.scope = scope;
		this.resolver = resolver;
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
