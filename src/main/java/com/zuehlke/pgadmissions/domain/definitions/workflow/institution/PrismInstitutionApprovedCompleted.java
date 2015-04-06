package com.zuehlke.pgadmissions.domain.definitions.workflow.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_IMPORT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.INSTITUTION_MANAGE_USERS_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_IMPORT_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionApproved.institutionEmailCreatorApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionApproved.institutionViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionApprovedCompleted extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(institutionEmailCreatorApproved()); //

		stateActions.add(institutionViewEditApproved() //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(INSTITUTION_APPROVED_COMPLETED) //
		                .withTransitionAction(INSTITUTION_VIEW_EDIT)
		                .withRoleTransitions(INSTITUTION_MANAGE_USERS_GROUP))); //

		stateActions.add(new PrismStateAction() //
		        .withAction(INSTITUTION_CREATE_PROGRAM) //
		        .withTransitions(PROGRAM_CREATE_TRANSITION //
		                .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP))); //

		stateActions.add(new PrismStateAction() //
		        .withAction(INSTITUTION_IMPORT_PROGRAM) //
		        .withTransitions(PROGRAM_IMPORT_TRANSITION //
		                .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP)));
	}

}
