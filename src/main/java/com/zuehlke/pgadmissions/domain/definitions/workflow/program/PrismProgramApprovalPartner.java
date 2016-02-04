package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_COMPLETE_APPROVAL_PARTNER_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PROGRAM_COMPLETE_APPROVAL_PARTNER_STAGE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROGRAM_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROGRAM_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL_PARTNER_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_APPROVED_PARTNER_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_APPROVE_PARTNER_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEscalateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApprovalPartner extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(programCompleteApprovalPartner()
                .withRaisesUrgentFlag() //
                .withNotification(SYSTEM_PROGRAM_TASK_REQUEST) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_APPROVAL_PARTNER_PENDING_CORRECTION) //
                        .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
                        .withTransitionEvaluation(PROGRAM_APPROVED_PARTNER_OUTCOME))); //

        stateActions.add(programEmailCreator()); //
        stateActions.add(programEscalateUnapproved()); //
        stateActions.add(programViewEditUnapproved()); //
        stateActions.add(programWithdraw());
    }

    public static PrismStateAction programCompleteApprovalPartner() {
        return new PrismStateAction() //
                .withAction(PROGRAM_COMPLETE_APPROVAL_PARTNER_STAGE) //
                .withPartnerAssignments(INSTITUTION_ADMINISTRATOR) //
                .withNotifications(PROGRAM_ADMINISTRATOR, PROGRAM_COMPLETE_APPROVAL_PARTNER_STAGE_NOTIFICATION) //
                .withNotifications(INSTITUTION_ADMINISTRATOR_GROUP, SYSTEM_PROGRAM_UPDATE_NOTIFICATION) //
                .withPartnerNotifications(INSTITUTION_ADMINISTRATOR, SYSTEM_PROGRAM_UPDATE_NOTIFICATION) //
                .withTransitions(PROGRAM_APPROVE_PARTNER_TRANSITION);
    }

}
