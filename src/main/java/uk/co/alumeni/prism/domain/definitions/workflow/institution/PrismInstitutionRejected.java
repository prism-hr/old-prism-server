package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionEmailCreatorUnnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionViewEditInactive;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionRejected extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(institutionEmailCreatorUnnapproved());
		stateActions.add(institutionViewEditInactive());
	}

}
