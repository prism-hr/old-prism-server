package uk.co.alumeni.prism.domain.definitions.workflow.program;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL_PENDING_CORRECTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programCreateProject;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programEscalateUnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programSendMessageUnnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programTerminateUnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programWithdraw;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(programCompleteApproval()
                .withRaisesUrgentFlag() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_APPROVAL_PENDING_CORRECTION) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_APPROVED_OUTCOME))); //

        stateActions.add(programCreateProject());
        stateActions.add(programSendMessageUnnapproved()); //
        stateActions.add(programEscalateUnapproved()); //
        stateActions.add(programTerminateUnapproved());
        stateActions.add(programViewEditApproval(state)); //
        stateActions.add(programWithdraw());
    }

    public static PrismStateAction programCompleteApproval() {
        return new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_COMPLETE_APPROVAL_STAGE) //
                .withAssignments(PrismRoleGroup.PROGRAM_PARENT_ADMINISTRATOR_GROUP) //
                .withNotifications(PrismRole.PROGRAM_ADMINISTRATOR, PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION) //
                .withStateTransitions(PrismStateTransitionGroup.PROGRAM_APPROVE_TRANSITION);
    }

}
