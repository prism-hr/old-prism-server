package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_RESTORE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PROJECT_VIEW_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_VIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectEmailCreatorApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectWorkflow.projectViewEditAbstract;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectDisabledCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(projectEmailCreatorApproved());

        stateActions.add(new PrismStateAction() //
                .withAction(PROJECT_RESTORE) //
                .withAssignments(PROJECT_ADMINISTRATOR_GROUP) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_APPROVED) //
                        .withTransitionAction(PROJECT_VIEW_EDIT)));

        stateActions.add(projectViewEditAbstract()
                .withAssignments(PROJECT_ADMINISTRATOR_GROUP, PROJECT_VIEW_AS_USER) //
                .withAssignments(PROJECT_VIEWER_GROUP, PROJECT_VIEW_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PROJECT_VIEW_AS_USER));
    }

}
