package uk.co.alumeni.prism.domain.definitions.workflow.program;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_REENDORSE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_UNENDORSE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PARTNERSHIP_MANAGER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_ENDORSE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programCreateProject;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreatorApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programEscalateApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programTerminateApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditApproved;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(programCreateProject());
        stateActions.add(programEmailCreatorApproved());
        stateActions.add(programEscalateApproved());

        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_UNENDORSE) //
                .withPartnerAssignments(PARTNERSHIP_MANAGER_GROUP) //
                .withStateTransitions(PROGRAM_ENDORSE_TRANSITION));

        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_REENDORSE) //
                .withPartnerAssignments(PARTNERSHIP_MANAGER_GROUP) //
                .withStateTransitions(PROGRAM_ENDORSE_TRANSITION));

        stateActions.add(programTerminateApproved()); //
        stateActions.add(programViewEditApproved()); //
    }

}
