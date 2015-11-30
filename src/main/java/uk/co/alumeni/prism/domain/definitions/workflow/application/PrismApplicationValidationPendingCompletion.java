package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_VALIDATION_STAGE_REQUEST;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationValidationPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationComment()); //

        stateActions.add(PrismApplicationValidation.applicationCompleteValidation(state) //
                .withRaisesUrgentFlag() //
                .withNotification(APPLICATION_COMPLETE_VALIDATION_STAGE_REQUEST)); //

        stateActions.add(PrismApplicationWorkflow.applicationEmailCreator()); //
        stateActions.add(PrismApplicationWorkflow.applicationEscalate()); //
        stateActions.add(PrismApplicationWorkflow.applicationTerminateSubmitted());
        stateActions.add(PrismApplicationValidation.applicationUploadReference(state));
        stateActions.add(PrismApplicationWorkflow.applicationViewEdit(state));
        stateActions.add(PrismApplicationValidation.applicationWithdrawValidation());
    }

}
