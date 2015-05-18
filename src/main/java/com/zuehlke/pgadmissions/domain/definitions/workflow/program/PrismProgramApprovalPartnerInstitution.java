package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PROGRAM_STARTUP_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEscalateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApprovalPartnerInstitution extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(programEscalateUnapproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_STARTUP) //
                .withNotifications(PROGRAM_ADMINISTRATOR, PROGRAM_STARTUP_NOTIFICATION) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_APPROVAL) //
                        .withTransitionAction(PROGRAM_STARTUP)));

        stateActions.add(programViewEditUnapproved()); //
        stateActions.add(programWithdraw());
    }
}
