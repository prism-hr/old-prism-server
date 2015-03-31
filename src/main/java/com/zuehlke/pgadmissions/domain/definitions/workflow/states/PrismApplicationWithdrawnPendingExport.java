package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_EXPORTED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationCommentReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationEmailCreatorReview;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;

public class PrismApplicationWithdrawnPendingExport extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentReview()); //

		stateActions.add(applicationEmailCreatorReview()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(PrismAction.APPLICATION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED) //
		                .withTransitionAction(APPLICATION_ESCALATE))); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_EXPORT) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_WITHDRAWN_PENDING_EXPORT) //
		                .withTransitionAction(APPLICATION_EXPORT) //
		                .withTransitionEvaluation(APPLICATION_EXPORTED_OUTCOME), //
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED) //
		                        .withTransitionAction(APPLICATION_EXPORT) //
		                        .withTransitionEvaluation(APPLICATION_EXPORTED_OUTCOME), //
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_WITHDRAWN_PENDING_CORRECTION) //
		                        .withTransitionAction(APPLICATION_EXPORT) //
		                        .withTransitionEvaluation(APPLICATION_EXPORTED_OUTCOME))); //

		stateActions.add(new PrismStateAction() //
		        .withAction(PrismAction.APPLICATION_VIEW_EDIT) //
		        .withRaisesUrgentFlag(false) //
		        .withDefaultAction(true) //
		        .withAssignments(Arrays.asList( //
		                new PrismStateActionAssignment() //
		                        .withRole(PrismRole.APPLICATION_CREATOR) //
		                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_CREATOR), //
		                new PrismStateActionAssignment() //
		                        .withRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
		                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
		                new PrismStateActionAssignment() //
		                        .withRole(PrismRole.APPLICATION_VIEWER_REFEREE) //
		                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_REFEREE), //
		                new PrismStateActionAssignment() //
		                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
		                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_ADMITTER), //
		                new PrismStateActionAssignment() //
		                        .withRole(PrismRole.INSTITUTION_ADMITTER) //
		                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_ADMITTER), //
		                new PrismStateActionAssignment() //
		                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
		                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
		                new PrismStateActionAssignment() //
		                        .withRole(PrismRole.PROGRAM_APPROVER) //
		                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
		                new PrismStateActionAssignment() //
		                        .withRole(PrismRole.PROGRAM_VIEWER) //
		                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
		                new PrismStateActionAssignment() //
		                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
		                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
		                new PrismStateActionAssignment() //
		                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
		                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER)))); //
	}

}
