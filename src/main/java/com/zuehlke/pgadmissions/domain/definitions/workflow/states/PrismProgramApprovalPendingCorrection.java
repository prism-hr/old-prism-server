package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_CORRECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PROGRAM_CORRECT_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproval.programEmailCreatorApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproval.programEscalateApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproval.programViewEditApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproval.programWithdrawApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproval.projectCompleteApprovalStage;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismProgramApprovalPendingCorrection extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(projectCompleteApprovalStage());

		stateActions.add(new PrismStateAction() //
		        .withAction(PROGRAM_CORRECT) //
		        .withRaisesUrgentFlag() //
		        .withNotification(PROGRAM_CORRECT_REQUEST) //
		        .withAssignments(PROGRAM_ADMINISTRATOR) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROGRAM_APPROVAL) //
		                .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST))); //

		stateActions.add(programEmailCreatorApproval()); //

		stateActions.add(programEscalateApproval()); //

		stateActions.add(programViewEditApproval()); //

		stateActions.add(programWithdrawApproval());
	}

}
