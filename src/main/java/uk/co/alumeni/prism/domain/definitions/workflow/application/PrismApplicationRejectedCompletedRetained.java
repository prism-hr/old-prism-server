package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationSendMessageViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationRejectedCompletedRetained extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentViewerRecruiter()); //
		stateActions.add(applicationSendMessageViewerRecruiter()); //
		stateActions.add(applicationViewEdit()); //
	}

}
