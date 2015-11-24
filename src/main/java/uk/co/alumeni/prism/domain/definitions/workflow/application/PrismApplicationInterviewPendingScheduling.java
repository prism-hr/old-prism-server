package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;

public class PrismApplicationInterviewPendingScheduling extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //
        stateActions.add(PrismApplicationInterview.applicationCompleteInterviewScheduling(state)); //

        stateActions.add(PrismApplicationInterview.applicationConfirmInterviewArrangements() //
                .withRaisesUrgentFlag()); //

        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter()); //

        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP,
                PrismRoleTransitionGroup.APPLICATION_RETIRE_INTERVIEWEE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_INTERVIEWER_GROUP));

        stateActions.add(PrismApplicationInterview.applicationProvideInterviewAvailability() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP)));

        stateActions.add(PrismApplicationInterview.applicationTerminateInterviewScheduling());

        stateActions.add(PrismApplicationInterview.applicationUpdateInterviewAvailability(PrismRoleGroup.APPLICATION_SCHEDULED_INTERVIEW_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS)));

        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(PrismApplicationInterview.applicationViewEditInterviewScheduling(state)); //
        stateActions.add(PrismApplicationInterview.applicationWithdrawInterviewScheduling());
    }

}
