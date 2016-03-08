package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionEmailCreatorApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionViewEditAbstract;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionDisabledCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(institutionEmailCreatorApproved()); //

        stateActions.add(new PrismStateAction() //
                .withAction(INSTITUTION_RESTORE) //
                .withStateActionAssignments(INSTITUTION_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.INSTITUTION_APPROVED) //
                        .withTransitionAction(PrismAction.INSTITUTION_VIEW_EDIT)));

        stateActions.add(institutionViewEditAbstract() //
                .withStateActionAssignments(INSTITUTION_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_EDIT_AS_USER) //
                .withStateActionAssignments(INSTITUTION_VIEWER_GROUP, INSTITUTION_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(INSTITUTION_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_AS_USER)); //
    }

}
