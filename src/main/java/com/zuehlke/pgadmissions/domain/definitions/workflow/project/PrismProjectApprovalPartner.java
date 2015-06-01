package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_COMPLETE_APPROVAL_PARTNER_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PROJECT_COMPLETE_APPROVAL_PARTNER_STAGE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROJECT_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVAL_PARTNER_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_APPROVED_PARTNER_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_APPROVE_PARTNER_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEscalateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectSuspendUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectTerminateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectViewEditUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectApprovalPartner extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(projectCompleteApprovalPartner() //
                .withRaisesUrgentFlag() //
                .withNotification(SYSTEM_PROJECT_TASK_REQUEST) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_APPROVAL_PARTNER_PENDING_CORRECTION) //
                        .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
                        .withTransitionEvaluation(PROJECT_APPROVED_PARTNER_OUTCOME)));

        stateActions.add(projectEmailCreator());
        stateActions.add(projectEscalateUnapproved());
        stateActions.add(projectSuspendUnapproved());
        stateActions.add(projectTerminateUnapproved());
        stateActions.add(projectViewEditUnapproved());
        stateActions.add(projectWithdraw());
    }

    public static PrismStateAction projectCompleteApprovalPartner() {
        return new PrismStateAction() //
                .withAction(PROJECT_COMPLETE_APPROVAL_PARTNER_STAGE) //
                .withPartnerAssignments(INSTITUTION_ADMINISTRATOR) //
                .withNotifications(PROJECT_ADMINISTRATOR, PROJECT_COMPLETE_APPROVAL_PARTNER_STAGE_NOTIFICATION) //
                .withNotifications(PROGRAM_ADMINISTRATOR_GROUP, SYSTEM_PROJECT_UPDATE_NOTIFICATION) //
                .withPartnerNotifications(INSTITUTION_ADMINISTRATOR, SYSTEM_PROJECT_UPDATE_NOTIFICATION) //
                .withTransitions(PROJECT_APPROVE_PARTNER_TRANSITION);
    }

}
