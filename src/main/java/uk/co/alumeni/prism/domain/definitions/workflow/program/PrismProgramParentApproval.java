package uk.co.alumeni.prism.domain.definitions.workflow.program;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismProgramParentApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_COMPLETE_PARENT_APPROVAL_STAGE) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROGRAM_APPROVED) //
                        .withTransitionAction(PrismAction.PROGRAM_COMPLETE_PARENT_APPROVAL_STAGE)));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_CREATE_PROJECT) //
                .withActionCondition(PrismActionCondition.ACCEPT_PROJECT) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_APPROVAL_PARENT_APPROVAL) //
                        .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT)
                        .withRoleTransitions(PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP)));

        stateActions.add(PrismProgramWorkflow.programEmailCreatorUnnapproved()); //
        stateActions.add(PrismProgramWorkflow.programEscalateUnapproved()); //
        stateActions.add(PrismProgramWorkflow.programTerminateUnapproved());
        stateActions.add(PrismProgramWorkflow.programViewEditApproval(state)); //
        stateActions.add(PrismProgramWorkflow.programWithdraw());
    }

}
