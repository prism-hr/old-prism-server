package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApproval.institutionEmailCreatorApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApproval.institutionViewEditApproval;

public class PrismInstitutionRejected extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(institutionEmailCreatorApproval()); //

		stateActions.add(institutionViewEditApproval()); //
	}

}
