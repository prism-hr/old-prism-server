package uk.co.alumeni.prism.domain.definitions.workflow.program;

import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programSendMessageApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditAbstract;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramDisabledCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(programSendMessageApproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_RESTORE) //
                .withAssignments(PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROGRAM_APPROVED) //
                        .withTransitionAction(PrismAction.PROGRAM_VIEW_EDIT)));

        stateActions.add(programViewEditAbstract() //
                .withAssignments(PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP, PrismActionEnhancement.PROGRAM_VIEW_AS_USER) //
                .withAssignments(PrismRoleGroup.PROGRAM_STAFF_GROUP, PrismActionEnhancement.PROGRAM_VIEW_AS_USER) //
                .withPartnerAssignments(PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP, PrismActionEnhancement.PROGRAM_VIEW_AS_USER)); //
    }

}
