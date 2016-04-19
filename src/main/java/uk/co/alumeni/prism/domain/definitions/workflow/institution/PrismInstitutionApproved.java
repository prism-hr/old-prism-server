package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.INSTITUTION_SEND_MESSAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.INSTITUTION_ENQUIRER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CREATE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionCreateDepartment;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionCreateProgram;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionCreateProject;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionSendMessageApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionTerminateApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionViewEditApproved;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(INSTITUTION_CREATE_APPLICATION) //
                .withActionCondition(ACCEPT_APPLICATION) //
                .withStateTransitions(APPLICATION_CREATE_TRANSITION //
                        .withRoleTransitions(APPLICATION_CREATE_CREATOR_GROUP))); //

        stateActions.add(institutionCreateDepartment());
        stateActions.add(institutionCreateProgram());
        stateActions.add(institutionCreateProject());

        stateActions.add(institutionSendMessageApproved() //
                .withStateActionAssignment(INSTITUTION_ENQUIRER, INSTITUTION_ADMINISTRATOR) //
                .withStateActionAssignments(INSTITUTION_ADMINISTRATOR_GROUP, INSTITUTION_ENQUIRER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(INSTITUTION_SEND_MESSAGE) //
                        .withRoleTransitions(new PrismRoleTransition() //
                                .withRole(INSTITUTION_ENQUIRER) //
                                .withTransitionType(CREATE) //
                                .withTransitionRole(INSTITUTION_ENQUIRER) //
                                .withRestrictToOwner() //
                                .withMinimumPermitted(0) //
                                .withMaximumPermitted(1))));

        stateActions.add(institutionTerminateApproved());
        stateActions.add(institutionViewEditApproved()); //
    }

}
