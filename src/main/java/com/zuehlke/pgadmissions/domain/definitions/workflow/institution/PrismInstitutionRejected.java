package com.zuehlke.pgadmissions.domain.definitions.workflow.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionApproval.institutionEmailCreatorApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionApproval.institutionViewEditApproval;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionRejected extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(institutionEmailCreatorApproval()); //

		stateActions.add(institutionViewEditApproval()); //
	}

}
