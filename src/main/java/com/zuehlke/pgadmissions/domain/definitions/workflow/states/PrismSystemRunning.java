package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_CREATE_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_MANAGE_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_INSTITUTION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.SYSTEM_VIEW_EDIT_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleTransitionGroup.INSTITUTION_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.SYSTEM_RUNNING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.INSTITUTION_CREATED_OUTCOME;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismSystemRunning extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(new PrismStateAction() //
		        .withAction(SYSTEM_VIEW_EDIT) //
		        .withActionEnhancement(SYSTEM_VIEW_EDIT_AS_USER)
		        .withAssignments(SYSTEM_ADMINISTRATOR)
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PrismState.SYSTEM_RUNNING) //
		                .withTransitionAction(PrismAction.SYSTEM_VIEW_EDIT)
		                .withRoleTransitions(new PrismRoleTransition() //
		                        .withRole(SYSTEM_ADMINISTRATOR) //
		                        .withTransitionType(CREATE) //
		                        .withTransitionRole(SYSTEM_ADMINISTRATOR),
		                        new PrismRoleTransition() //
		                                .withRole(SYSTEM_ADMINISTRATOR) //
		                                .withTransitionType(DELETE) //
		                                .withTransitionRole(SYSTEM_ADMINISTRATOR)))); //

		stateActions.add(new PrismStateAction() //
		        .withAction(SYSTEM_CREATE_INSTITUTION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(INSTITUTION_APPROVAL) //
		                .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST) //
		                .withTransitionEvaluation(INSTITUTION_CREATED_OUTCOME) //
		                .withRoleTransitions(INSTITUTION_CREATE_ADMINISTRATOR_GROUP),
		                new PrismStateTransition() //
		                        .withTransitionState(INSTITUTION_APPROVED) //
		                        .withTransitionAction(INSTITUTION_VIEW_EDIT) //
		                        .withTransitionEvaluation(INSTITUTION_CREATED_OUTCOME) //
		                        .withRoleTransitions(INSTITUTION_CREATE_ADMINISTRATOR_GROUP))); //

		stateActions.add(new PrismStateAction() //
		        .withAction(SYSTEM_MANAGE_ACCOUNT)); //

		stateActions.add(new PrismStateAction() //
		        .withAction(SYSTEM_STARTUP) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(SYSTEM_RUNNING) //
		                .withTransitionAction(SYSTEM_VIEW_EDIT)
		                .withRoleTransitions(new PrismRoleTransition() //
		                        .withRole(SYSTEM_ADMINISTRATOR) //
		                        .withTransitionType(CREATE) //
		                        .withTransitionRole(SYSTEM_ADMINISTRATOR) //
		                        .withMinimumPermitted(1) //
		                        .withMaximumPermitted(1))));

		stateActions.add(new PrismStateAction() //
		        .withAction(SYSTEM_VIEW_APPLICATION_LIST)); //

		stateActions.add(new PrismStateAction() //
		        .withAction(SYSTEM_VIEW_INSTITUTION_LIST)); //

		stateActions.add(new PrismStateAction() //
		        .withAction(SYSTEM_VIEW_PROGRAM_LIST)); //

		stateActions.add(new PrismStateAction() //
		        .withAction(SYSTEM_VIEW_PROJECT_LIST));
	}

}
