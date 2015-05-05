package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_CORRECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PROGRAM_CORRECT_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_REVIVE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL_PARTNER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramApprovalPartner.programCompleteApprovalPartner;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programEscalateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programViewEditUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.program.PrismProgramWorkflow.programWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProgramApprovalPartnerPendingCorrection extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(programCompleteApprovalPartner());

		stateActions.add(new PrismStateAction() //
		        .withAction(PROGRAM_CORRECT) //
		        .withRaisesUrgentFlag() //
		        .withNotification(PROGRAM_CORRECT_REQUEST) //
		        .withAssignments(PROGRAM_ADMINISTRATOR) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROGRAM_APPROVAL_PARTNER) //
		                .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
		                .withRoleTransitions(PROGRAM_REVIVE_ADMINISTRATOR_GROUP))); //

		stateActions.add(programEmailCreator()); //
		stateActions.add(programEscalateUnapproved()); //
		stateActions.add(programViewEditUnapproved()); //
		stateActions.add(programWithdraw());
	}

}
