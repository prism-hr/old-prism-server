package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_VALIDATION_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_UPLOAD_REFERENCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_VALIDATION_STAGE_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreator;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationValidation extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationComment());

        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_VALIDATION_STAGE, state, APPLICATION_PARENT_ADMINISTRATOR_GROUP)
                .withRaisesUrgentFlag()
                .withNotification(APPLICATION_COMPLETE_VALIDATION_STAGE_REQUEST));

        stateActions.add(applicationEmailCreator());
        stateActions.add(applicationEscalate());

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_UPLOAD_REFERENCE) //
                .withAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_UPLOAD_REFERENCE)));

        stateActions.add(applicationViewEdit(state));
        stateActions.add(applicationWithdrawSubmitted(APPLICATION_PARENT_ADMINISTRATOR_GROUP));
    }

}
