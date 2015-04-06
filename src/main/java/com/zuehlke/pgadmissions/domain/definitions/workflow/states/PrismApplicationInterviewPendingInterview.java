package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_CONFIRMED_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_CONFIRMED_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterview.applicationUpdateInterviewAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterview.applicationViewEditInterviewScheduled;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationCommentWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationCompleteState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationWithdraw;

public class PrismApplicationInterviewPendingInterview extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiterAndAdministrator()); //

		stateActions.add(applicationCompleteState(APPLICATION_ADMINISTRATOR_GROUP, //
		        APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		        APPLICATION_DELETE_CONFIRMED_INTERVIEWEE_GROUP,
		        APPLICATION_DELETE_CONFIRMED_INTERVIEWER_GROUP));

		stateActions.add(applicationEmailCreatorWithViewerRecruiterAndAdministrator()); //
		stateActions.add(applicationEscalate(APPLICATION_INTERVIEW_PENDING_FEEDBACK, APPLICATION_DELETE_CONFIRMED_INTERVIEWEE_GROUP)); //
		stateActions.add(applicationUpdateInterviewAvailability(APPLICATION_INTERVIEW_PENDING_SCHEDULING)); //
		stateActions.add(applicationViewEditInterviewScheduled(state)); //
		stateActions.add(applicationWithdraw(APPLICATION_ADMINISTRATOR_GROUP, //
		        APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		        APPLICATION_DELETE_REFEREE_GROUP,
		        APPLICATION_DELETE_CONFIRMED_INTERVIEWEE_GROUP,
		        APPLICATION_DELETE_CONFIRMED_INTERVIEWER_GROUP));
	}

}
