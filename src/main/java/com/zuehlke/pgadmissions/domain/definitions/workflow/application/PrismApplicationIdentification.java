package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_IDENTIFICATION_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_UPLOAD_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_IDENTIFIED_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationComment;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateSubmitted;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationIdentification extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationComment()); //

        stateActions.add(applicationEmailCreator()); //

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_ESCALATE) //
                .withTransitions(APPLICATION_IDENTIFIED_TRANSITION)); //

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_COMPLETE_IDENTIFICATION_STAGE) //
                .withRaisesUrgentFlag() //
                .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP) //
                .withTransitions(APPLICATION_IDENTIFIED_TRANSITION));

        stateActions.add(applicationTerminateSubmitted());

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_UPLOAD_REFERENCE) //
                .withAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
                .withNotifications(APPLICATION_CREATOR, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_UPLOAD_REFERENCE)));

        stateActions.add(applicationViewEdit(state));
        stateActions.add(applicationWithdrawSubmitted(APPLICATION_PARENT_ADMINISTRATOR_GROUP));
    }

}
