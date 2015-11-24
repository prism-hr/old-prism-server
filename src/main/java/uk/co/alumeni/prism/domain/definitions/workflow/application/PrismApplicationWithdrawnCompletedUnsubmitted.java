package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;

public class PrismApplicationWithdrawnCompletedUnsubmitted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_VIEW_EDIT) //
                .withAssignments(PrismRole.APPLICATION_CREATOR, PrismActionEnhancement.APPLICATION_VIEW_AS_CREATOR)); //
    }

}
