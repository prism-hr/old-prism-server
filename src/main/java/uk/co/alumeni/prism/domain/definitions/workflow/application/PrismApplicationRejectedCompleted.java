package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationRejectedCompleted extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //
		stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter()); //
		stateActions.add(PrismApplicationRejected.applicationReverseRejection());
		stateActions.add(PrismApplicationWorkflow.applicationViewEdit()); //
	}

}
