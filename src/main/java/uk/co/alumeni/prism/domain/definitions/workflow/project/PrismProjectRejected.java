package uk.co.alumeni.prism.domain.definitions.workflow.project;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectRejected extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismProjectWorkflow.projectSendMessageUnnapproved()); //
        stateActions.add(PrismProjectWorkflow.projectViewEditInactive()); //
    }

}
