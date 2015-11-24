package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovedCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //
        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(PrismApplicationWorkflow.applicationViewEdit()); //
    }

}
