package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionWithdrawn extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(PrismInstitutionWorkflow.institutionSendMessageUnnapproved());
		stateActions.add(PrismInstitutionWorkflow.institutionViewEditInactive());
	}

}
