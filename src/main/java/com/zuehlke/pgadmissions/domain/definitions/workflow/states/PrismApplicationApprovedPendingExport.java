package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_APPROVED_EXPORT_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationCommentVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationEmailCreatorVerification;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationApprovedPendingExport extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentVerification()); //

		stateActions.add(applicationEmailCreatorVerification()); //

		stateActions.add(applicationEscalateApprovedPendingExport()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_EXPORT) //
		        .withTransitions(APPLICATION_APPROVED_EXPORT_TRANSITION)); //

		stateActions.add(applicationViewEditApprovedPendingExport()); //
	}

	public static PrismStateAction applicationEscalateApprovedPendingExport() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_APPROVED_COMPLETED) //
		                .withTransitionAction(APPLICATION_ESCALATE));
	}

	public static PrismStateAction applicationViewEditApprovedPendingExport() {
		return new PrismStateAction() //
		        .withAction(PrismAction.APPLICATION_VIEW_EDIT) //
		        .withAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_AS_CREATOR) //
		        .withAssignments(APPLICATION_VIEWER_RECRUITER, APPLICATION_VIEW_AS_RECRUITER) //
		        .withAssignments(APPLICATION_VIEWER_REFEREE, APPLICATION_VIEW_AS_REFEREE) //
		        .withAssignments(INSTITUTION_ADMINISTRATOR, APPLICATION_VIEW_AS_ADMITTER) //
		        .withAssignments(INSTITUTION_ADMITTER, APPLICATION_VIEW_AS_ADMITTER) //
		        .withAssignments(PROGRAM_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
		        .withAssignments(PROGRAM_APPROVER, APPLICATION_VIEW_AS_RECRUITER) //
		        .withAssignments(PROGRAM_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
		        .withAssignments(PROJECT_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
		        .withAssignments(PROJECT_PRIMARY_SUPERVISOR, APPLICATION_VIEW_AS_RECRUITER);
	}

}
