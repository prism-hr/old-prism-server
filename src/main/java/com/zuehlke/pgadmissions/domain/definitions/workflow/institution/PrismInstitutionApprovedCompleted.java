package com.zuehlke.pgadmissions.domain.definitions.workflow.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_IMPORT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_IMPORT_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionEmailCreator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionApprovedCompleted extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(institutionEmailCreator()); //
		stateActions.add(institutionViewEditApproved()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(INSTITUTION_CREATE_PROGRAM) //
		        .withTransitions(PROGRAM_CREATE_TRANSITION //
		                .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP))); //

		stateActions.add(new PrismStateAction() //
		        .withAction(INSTITUTION_IMPORT_PROGRAM) //
		        .withTransitions(PROGRAM_IMPORT_TRANSITION));
	}

}
