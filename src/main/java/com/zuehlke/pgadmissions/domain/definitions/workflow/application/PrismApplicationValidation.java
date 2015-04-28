package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_VALIDATION_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_UPLOAD_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationComment;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationValidation extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationComment()); //
        stateActions.add(applicationCompleteValidation(state)); //
        stateActions.add(applicationEmailCreator()); //
        stateActions.add(applicationEscalate(APPLICATION_VALIDATION_PENDING_COMPLETION)); //
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEdit(state)); //
        stateActions.add(applicationWithdrawValidation());
    }

    public static PrismStateAction applicationCompleteValidation(PrismState state) {
        return applicationCompleteState(APPLICATION_COMPLETE_VALIDATION_STAGE, state, APPLICATION_PARENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction applicationWithdrawValidation() {
        return applicationWithdraw(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_RETIRE_REFEREE_GROUP);
    }

    public static PrismStateAction applicationUploadReference(PrismState state) {
        return new PrismStateAction() //
                .withAction(APPLICATION_UPLOAD_REFERENCE) //
                .withAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
                .withNotifications(APPLICATION_CREATOR, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_UPLOAD_REFERENCE));
    }

}
