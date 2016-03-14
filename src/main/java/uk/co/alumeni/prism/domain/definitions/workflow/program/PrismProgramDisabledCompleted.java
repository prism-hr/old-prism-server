package uk.co.alumeni.prism.domain.definitions.workflow.program;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_RESTORE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.PROGRAM_VIEW_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.PROGRAM_VIEW_EDIT_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROGRAM_VIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreatorApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditAbstract;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramDisabledCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(programEmailCreatorApproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_RESTORE) //
                .withStateActionAssignments(PROGRAM_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROGRAM_APPROVED) //
                        .withTransitionAction(PrismAction.PROGRAM_VIEW_EDIT)));

        stateActions.add(programViewEditAbstract() //
                .withStateActionAssignments(PROGRAM_ADMINISTRATOR_GROUP, PROGRAM_VIEW_EDIT_AS_USER) //
                .withStateActionAssignments(PROGRAM_VIEWER_GROUP, PROGRAM_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PROGRAM_VIEW_AS_USER)); //
    }

}
