package com.zuehlke.pgadmissions.domain.definitions.workflow.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionEmailCreatorUnnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionViewEditInactive;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionRejected extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(institutionEmailCreatorUnnapproved());
		stateActions.add(institutionViewEditInactive());
	}

}
