package uk.co.alumeni.prism.domain.definitions.workflow.project;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_CREATE_APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_REENDORSE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_SEND_MESSAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_UNENDORSE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PROJECT_ENQUIRER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PARTNERSHIP_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CREATE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_ENDORSE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectWorkflow.projectEscalateApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectWorkflow.projectSendMessageApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectWorkflow.projectTerminateApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectWorkflow.projectViewEditApproved;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_CREATE_APPLICATION) //
                .withActionCondition(ACCEPT_APPLICATION) //
                .withStateTransitions(APPLICATION_CREATE_TRANSITION //
                        .withRoleTransitions(APPLICATION_CREATE_CREATOR_GROUP))); //

        stateActions.add(projectSendMessageApproved() //
                .withStateActionAssignment(PROJECT_ENQUIRER, PROJECT_ADMINISTRATOR) //
                .withStateActionAssignments(PROJECT_ADMINISTRATOR_GROUP, PROJECT_ENQUIRER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(PROJECT_SEND_MESSAGE) //
                        .withRoleTransitions(new PrismRoleTransition() //
                                .withRole(PROJECT_ENQUIRER) //
                                .withTransitionType(CREATE) //
                                .withTransitionRole(PROJECT_ENQUIRER) //
                                .withRestrictToOwner() //
                                .withMinimumPermitted(0) //
                                .withMaximumPermitted(1))));

        stateActions.add(projectEscalateApproved());

        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_REENDORSE) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP) //
                .withStateTransitions(PROJECT_ENDORSE_TRANSITION));

        stateActions.add(projectTerminateApproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_UNENDORSE) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP) //
                .withStateTransitions(PROJECT_ENDORSE_TRANSITION));

        stateActions.add(projectViewEditApproved()); //
    }

}
