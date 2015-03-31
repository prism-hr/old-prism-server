package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproval.projectEmailCreatorApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproved.projectEscalateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproved.projectSuspendApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproved.projectTerminateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproved.projectViewEditApproved;

public class PrismProjectDeactivated extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(projectEmailCreatorApproval()); //

        stateActions.add(projectViewEditApproved()); //

        stateActions.add(projectEscalateApproved()); //

        stateActions.add(projectSuspendApproved()); //

        stateActions.add(projectTerminateApproved()); //
    }

}
