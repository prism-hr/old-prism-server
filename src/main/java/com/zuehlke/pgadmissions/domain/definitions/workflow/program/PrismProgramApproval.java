package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_APPROVE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programCreateProject;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreatorUnnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEscalateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programTerminateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(programCompleteApproval()
                .withRaisesUrgentFlag() //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_APPROVAL_PENDING_CORRECTION) //
                        .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
                        .withTransitionEvaluation(PROGRAM_APPROVED_OUTCOME))); //

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
                .withAssignments(PROGRAM_PARENT_ADMINISTRATOR_GROUP) //
                .withNotification(PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION) //
                .withTransitions(PROGRAM_APPROVE_TRANSITION);
    }

}
