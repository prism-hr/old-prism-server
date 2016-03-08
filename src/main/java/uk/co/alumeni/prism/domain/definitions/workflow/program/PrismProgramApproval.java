package uk.co.alumeni.prism.domain.definitions.workflow.program;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_COMPLETE_APPROVAL_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROGRAM_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL_PENDING_CORRECTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_APPROVED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_APPROVE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programCreateProject;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreatorUnnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programEscalateUnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programTerminateUnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programWithdraw;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(programCompleteApproval()
                .withRaisesUrgentFlag() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_APPROVAL_PENDING_CORRECTION) //
                        .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
                        .withStateTransitionEvaluation(PROGRAM_APPROVED_OUTCOME))); //

        stateActions.add(programCreateProject());
        stateActions.add(programEmailCreatorUnnapproved()); //
        stateActions.add(programEscalateUnapproved()); //
        stateActions.add(programTerminateUnapproved());
        stateActions.add(programViewEditApproval(state)); //
        stateActions.add(programWithdraw());
    }

    public static PrismStateAction programCompleteApproval() {
        return new PrismStateAction() //
                .withAction(PROGRAM_COMPLETE_APPROVAL_STAGE) //
                .withStateActionAssignments(PROGRAM_PARENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(PROGRAM_APPROVE_TRANSITION);
    }

}
