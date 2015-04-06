package com.zuehlke.pgadmissions.domain.definitions.workflow.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CORRECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_INSTITUTION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.INSTITUTION_CORRECT_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionApproval.institutionCompleteApprovalStage;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionApproval.institutionEmailCreatorApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionApproval.institutionEscalateApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionApproval.institutionViewEditApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionApproval.institutionWithdrawApproval;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionApprovalPendingCorrection extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(institutionCompleteApprovalStage());
		
		stateActions.add(new PrismStateAction() //
		        .withAction(INSTITUTION_CORRECT) //
		        .withRaisesUrgentFlag() //
		        .withNotification(INSTITUTION_CORRECT_REQUEST) //
		        .withAssignments(INSTITUTION_ADMINISTRATOR) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(INSTITUTION_APPROVAL) //
		                .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST))); //

		stateActions.add(institutionEmailCreatorApproval()); //

		stateActions.add(institutionEscalateApproval()); //

		stateActions.add(institutionViewEditApproval()); //

		stateActions.add(institutionWithdrawApproval());
	}

}
