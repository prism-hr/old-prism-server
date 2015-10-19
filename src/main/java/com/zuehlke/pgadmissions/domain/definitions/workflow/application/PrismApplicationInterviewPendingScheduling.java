package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationCompleteInterviewScheduling;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationConfirmInterviewArrangements;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationProvideInterviewAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationTerminateInterviewScheduling;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationUpdateInterviewAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationViewEditInterviewScheduling;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationWithdrawInterviewScheduling;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationInterviewPendingScheduling extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //
        stateActions.add(applicationCompleteInterviewScheduling(state)); //

        stateActions.add(applicationConfirmInterviewArrangements() //
                .withRaisesUrgentFlag()); //

        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //

        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_INTERVIEWEE_GROUP, //
                APPLICATION_RETIRE_INTERVIEWER_GROUP));

        stateActions.add(applicationProvideInterviewAvailability() //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
                        .withRoleTransitions(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP)));

        stateActions.add(applicationTerminateInterviewScheduling());

        stateActions.add(applicationUpdateInterviewAvailability() //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS)));

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditInterviewScheduling(state)); //
        stateActions.add(applicationWithdrawInterviewScheduling());
    }

}
