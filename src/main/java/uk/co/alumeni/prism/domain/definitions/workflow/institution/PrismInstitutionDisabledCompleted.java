package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.INSTITUTION_RESTORE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.INSTITUTION_VIEW_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.INSTITUTION_VIEW_EDIT_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_VIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionEmailCreatorApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionViewEditAbstract;
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
                        .withTransitionState(INSTITUTION_APPROVED) //
                        .withTransitionAction(INSTITUTION_VIEW_EDIT)));

        stateActions.add(institutionViewEditAbstract() //
                .withStateActionAssignments(INSTITUTION_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_EDIT_AS_USER) //
                .withStateActionAssignments(INSTITUTION_VIEWER_GROUP, INSTITUTION_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(INSTITUTION_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_AS_USER)); //
    }

}
