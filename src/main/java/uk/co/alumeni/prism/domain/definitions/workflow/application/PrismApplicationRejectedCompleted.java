package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationRejected.applicationReverseRejection;

public class PrismApplicationRejectedCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationComment()); //
        stateActions.add(PrismApplicationWorkflow.applicationSendMessage()); //
        stateActions.add(applicationReverseRejection());
        stateActions.add(PrismApplicationWorkflow.applicationViewEdit()); //
    }

}
