package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationWithdrawnCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationComment()); //
        stateActions.add(PrismApplicationWorkflow.applicationSendMessage()); //
        stateActions.add(PrismApplicationWorkflow.applicationViewEdit()); //
    }

}
