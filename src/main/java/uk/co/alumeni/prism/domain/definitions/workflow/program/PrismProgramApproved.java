package uk.co.alumeni.prism.domain.definitions.workflow.program;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismProgramWorkflow.programCreateProject());
        stateActions.add(PrismProgramWorkflow.programEmailCreatorApproved());

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_UNENDORSE) //
                .withPartnerAssignments(PrismRoleGroup.PARTNERSHIP_MANAGER_GROUP) //
                .withStateTransitions(PrismStateTransitionGroup.PROGRAM_ENDORSE_TRANSITION));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_REENDORSE) //
                .withPartnerAssignments(PrismRoleGroup.PARTNERSHIP_MANAGER_GROUP) //
                .withStateTransitions(PrismStateTransitionGroup.PROGRAM_ENDORSE_TRANSITION));

        stateActions.add(PrismProgramWorkflow.programTerminateApproved()); //
        stateActions.add(PrismProgramWorkflow.programViewEditApproved()); //
    }

}
