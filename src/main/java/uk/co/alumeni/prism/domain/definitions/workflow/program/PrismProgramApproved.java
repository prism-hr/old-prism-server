package uk.co.alumeni.prism.domain.definitions.workflow.program;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_REENDORSE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_SEND_MESSAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_UNENDORSE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PROGRAM_ENQUIRER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PARTNERSHIP_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_ENDORSE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programCreateProject;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programEscalateApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programSendMessageApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programTerminateApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditApproved;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(programCreateProject());
        stateActions.add(programEscalateApproved());

        stateActions.add(programSendMessageApproved() //
                .withStateActionAssignment(PROGRAM_ENQUIRER, PROGRAM_ADMINISTRATOR) //
                .withStateActionAssignments(PROGRAM_ADMINISTRATOR_GROUP, PROGRAM_ENQUIRER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(PROGRAM_SEND_MESSAGE) //
                        .withRoleTransitions(new PrismRoleTransition() //
                                .withRole(PROGRAM_ENQUIRER) //
                                .withTransitionType(CREATE) //
                                .withTransitionRole(PROGRAM_ENQUIRER) //
                                .withRestrictToOwner() //
                                .withMinimumPermitted(0) //
                                .withMaximumPermitted(1))));

        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_REENDORSE) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP) //
                .withStateTransitions(PROGRAM_ENDORSE_TRANSITION));

        stateActions.add(programTerminateApproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_UNENDORSE) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP) //
                .withStateTransitions(PROGRAM_ENDORSE_TRANSITION));

        stateActions.add(programViewEditApproved()); //
    }

}
