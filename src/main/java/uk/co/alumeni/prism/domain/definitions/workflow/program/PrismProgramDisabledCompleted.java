package uk.co.alumeni.prism.domain.definitions.workflow.program;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_RESTORE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_VIEW_EDIT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.PROGRAM_VIEW_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.PROGRAM_VIEW_EDIT_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programSendMessageApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditAbstract;

public class PrismProgramDisabledCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(programSendMessageApproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_RESTORE) //
                .withStateActionAssignments(PROGRAM_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_APPROVED) //
                        .withTransitionAction(PROGRAM_VIEW_EDIT)));

        stateActions.add(programViewEditAbstract() //
                .withStateActionAssignments(PROGRAM_ADMINISTRATOR_GROUP, PROGRAM_VIEW_EDIT_AS_USER) //
                .withStateActionAssignments(PROGRAM_STAFF_GROUP, PROGRAM_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PROGRAM_VIEW_AS_USER)); //
    }

}
