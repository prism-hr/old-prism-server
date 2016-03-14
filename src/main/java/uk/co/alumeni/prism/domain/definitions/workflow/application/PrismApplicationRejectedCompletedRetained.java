package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationRejectedCompletedRetained extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(PrismApplicationWorkflow.applicationCommentViewerRecruiter()); //
		stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorViewerRecruiter()); //
		stateActions.add(PrismApplicationWorkflow.applicationViewEdit()); //
	}

}
