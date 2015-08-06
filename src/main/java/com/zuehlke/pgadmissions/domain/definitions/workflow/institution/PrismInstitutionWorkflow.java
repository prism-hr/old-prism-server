package com.zuehlke.pgadmissions.domain.definitions.workflow.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_EMAIL_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_INSTITUTION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.INSTITUTION_VIEW_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.INSTITUTION_VIEW_EDIT_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.INSTITUTION_MANAGE_USERS_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_WITHDRAWN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.INSTITUTION_VIEW_EDIT_TRANSITION;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismInstitutionWorkflow {

    public static PrismStateAction institutionEmailCreatorUnnapproved() {
        return institutionEmailCreatorAbstract();
    }

    public static PrismStateAction institutionEmailCreatorApproved() {
        return institutionEmailCreatorAbstract() //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction institutionEscalateUnapproved() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_ESCALATE) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(INSTITUTION_REJECTED) //
                        .withTransitionAction(INSTITUTION_ESCALATE));
    }

    public static PrismStateAction institutionViewEditUnapproved() {
        return institutionViewEditAbstract() //
                .withActionEnhancement(INSTITUTION_VIEW_AS_USER)
                .withAssignments(INSTITUTION_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction institutionViewEditApproved() {
        return institutionViewEditAbstract()
                .withAssignments(INSTITUTION_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_AS_USER) //
                .withTransitions(INSTITUTION_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(INSTITUTION_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction institutionWithdraw() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_WITHDRAW) //
                .withAssignments(INSTITUTION_ADMINISTRATOR) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(INSTITUTION_WITHDRAWN) //
                        .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST));
    }

    private static PrismStateAction institutionEmailCreatorAbstract() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_EMAIL_CREATOR) //
                .withAssignments(SYSTEM_ADMINISTRATOR);
    }

    private static PrismStateAction institutionViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_VIEW_EDIT);
    }

}
