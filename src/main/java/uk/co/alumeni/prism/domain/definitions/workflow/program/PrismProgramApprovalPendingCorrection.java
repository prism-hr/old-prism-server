package uk.co.alumeni.prism.domain.definitions.workflow.program;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.PROGRAM_CORRECT_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programCreateProject;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreatorUnnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programEscalateUnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programTerminateUnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programWithdraw;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApprovalPendingCorrection extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismProgramApproval.programCompleteApproval());

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_CORRECT) //
                .withRaisesUrgentFlag() //
                .withNotification(PROGRAM_CORRECT_REQUEST) //
                .withStateActionAssignments(PrismRole.PROGRAM_ADMINISTRATOR) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_APPROVAL) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) //
                        .withRoleTransitions(PrismRoleTransitionGroup.PROGRAM_REVIVE_ADMINISTRATOR_GROUP))); //

        stateActions.add(programCreateProject());
        stateActions.add(programEmailCreatorUnnapproved()); //
        stateActions.add(programEscalateUnapproved()); //
        stateActions.add(programTerminateUnapproved());
        stateActions.add(programViewEditApproval(state)); //
        stateActions.add(programWithdraw());
    }

}
