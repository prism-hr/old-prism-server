package com.zuehlke.pgadmissions.domain.definitions.workflow.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_INSTITUTION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.INSTITUTION_COMPLETE_APPROVAL_STAGE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVAL_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.INSTITUTION_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.INSTITUTION_APPROVE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionEmailCreatorUnnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionEscalateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionTerminateUnapproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionViewEditApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(institutionCompleteApproval() //
                .withRaisesUrgentFlag() //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(INSTITUTION_APPROVAL_PENDING_CORRECTION) //
                        .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST)
                        .withTransitionEvaluation(INSTITUTION_APPROVED_OUTCOME))); //

        stateActions.add(institutionEmailCreatorUnnapproved()); //
        stateActions.add(institutionEscalateUnapproved()); //
        stateActions.add(institutionTerminateUnapproved()); //
        stateActions.add(institutionViewEditApproval(state)); //
        stateActions.add(institutionWithdraw());
    }

    public static PrismStateAction institutionCompleteApproval() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_COMPLETE_APPROVAL_STAGE) //
                .withAssignments(SYSTEM_ADMINISTRATOR) //
                .withNotification(INSTITUTION_COMPLETE_APPROVAL_STAGE_NOTIFICATION) //
                .withTransitions(INSTITUTION_APPROVE_TRANSITION);
    }

}
