package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_ENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_ENDORSER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_ENDORSE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programCreateProject;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreatorApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programTerminateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(programCreateProject());
        stateActions.add(programEmailCreatorApproved());

        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_ENDORSE) //
                .withRaisesUrgentFlag() //
                .withPartnerAssignments(PROGRAM_ENDORSER_GROUP) //
                .withTransitions(PROGRAM_ENDORSE_TRANSITION));

        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_ENDORSE) //
                .withPartnerAssignments(PROGRAM_ENDORSER_GROUP) //
                .withTransitions(PROGRAM_ENDORSE_TRANSITION));

        stateActions.add(new PrismStateAction() //
                .withAction(PROGRAM_ENDORSE) //
                .withPartnerAssignments(PROGRAM_ENDORSER_GROUP) //
                .withTransitions(PROGRAM_ENDORSE_TRANSITION));

        stateActions.add(programTerminateApproved()); //
        stateActions.add(programViewEditApproved()); //
    }

}
