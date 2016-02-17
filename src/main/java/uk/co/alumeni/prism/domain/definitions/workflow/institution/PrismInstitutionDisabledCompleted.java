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
                .withAction(PrismAction.INSTITUTION_RESTORE) //
                .withAssignments(PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.INSTITUTION_APPROVED) //
                        .withTransitionAction(PrismAction.INSTITUTION_VIEW_EDIT)));

        stateActions.add(institutionViewEditAbstract() //
                .withAssignments(PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP, PrismActionEnhancement.INSTITUTION_VIEW_AS_USER) //
                .withAssignments(PrismRoleGroup.INSTITUTION_VIEWER_GROUP, PrismActionEnhancement.INSTITUTION_VIEW_AS_USER) //
                .withPartnerAssignments(PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP, PrismActionEnhancement.INSTITUTION_VIEW_AS_USER)); //
    }

}
