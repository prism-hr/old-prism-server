package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproval.projectEmailCreatorApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproved.projectEscalateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproved.projectViewEditApproved;

public class PrismProjectDisabledPendingReactivation extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(projectEmailCreatorApproval()); //

		stateActions.add(projectEscalateApproved()); //

		stateActions.add(projectViewEditApproved()); //
	}

}
