package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationValidation.applicationCompleteValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationValidation.applicationUploadReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationValidation.applicationWithdrawValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationComment;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateSubmitted;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationValidationPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationComment()); //

        stateActions.add(applicationCompleteValidation(state) //
                .withRaisesUrgentFlag() //
                .withNotification(SYSTEM_APPLICATION_TASK_REQUEST)); //

        stateActions.add(applicationEmailCreator()); //
        stateActions.add(applicationEscalate()); //
        stateActions.add(applicationTerminateSubmitted());
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEdit(state));
        stateActions.add(applicationWithdrawValidation());
    }

}
