package uk.co.alumeni.prism.domain.definitions.workflow;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismLocalizableDefinition;

public enum PrismAction implements PrismLocalizableDefinition {

    APPLICATION_ASSIGN_INTERVIEWERS(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_ASSIGN_HIRING_MANAGERS(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_ASSIGN_REVIEWERS(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMMENT(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE(getDefaultViewEditApplicationActionDefinition()), //
    APPLICATION_COMPLETE_VALIDATION_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_REFERENCE_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_REVIEW_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_INTERVIEW_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_APPROVAL_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_APPROVED_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_REJECTED_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_CONFIRM_OFFER(getDefaultProcessApplicationActionDefinition()), //
    APPLICATION_CONFIRM_OFFER_ACCEPTANCE(getDefaultProcessApplicationActionDefinition()), //
    APPLICATION_CONFIRM_REJECTION(getDefaultProcessApplicationActionDefinition()), //
    APPLICATION_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(PrismScope.APPLICATION)), //
    APPLICATION_ESCALATE(getDefaultEscalateResourceActionDefinition(PrismScope.APPLICATION)), //
    APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK(getDefaultRateApplicationActionDefinitionDeclinable()), //
    APPLICATION_PROVIDE_PARTNER_APPROVAL(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_PROVIDE_REFERENCE(getDefaultRateApplicationActionDefinitionDeclinable()), //
    APPLICATION_PROVIDE_REVIEW(getDefaultRateApplicationActionDefinitionDeclinable()), //
    APPLICATION_REVERSE_REJECTION(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_UPLOAD_REFERENCE(getDefaultRateApplicationActionDefinitionDeclinable()), //
    APPLICATION_VIEW_EDIT(getDefaultViewEditApplicationActionDefinition()), //
    APPLICATION_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(PrismScope.APPLICATION)), //
    APPLICATION_WITHDRAW(getDefaultWithdrawResourceActionDefinition(PrismScope.APPLICATION)),

    PROJECT_UNENDORSE(getDefaultPartnerActionDefinition(PrismScope.PROJECT, PrismPartnershipState.ENDORSEMENT_PROVIDED, PrismPartnershipState.ENDORSEMENT_REVOKED)), //
    PROJECT_REENDORSE(getDefaultPartnerActionDefinition(PrismScope.PROJECT, PrismPartnershipState.ENDORSEMENT_REVOKED, PrismPartnershipState.ENDORSEMENT_PROVIDED)), //
    PROJECT_COMPLETE(getDefaultViewEditResourceActionDefinition(PrismScope.PROJECT)), //
    PROJECT_COMPLETE_APPROVAL_STAGE(getDefaultProcessResourceActionDefinitionVisible(PrismScope.PROJECT)), //
    PROJECT_COMPLETE_PARENT_APPROVAL_STAGE(getDefaultPropagateResourceActionDefinitionVisible(PrismScope.PROJECT)), //
    PROJECT_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(PrismScope.PROJECT)), //
    PROJECT_CORRECT(getDefaultProcessResourceActionDefinitionVisible(PrismScope.PROJECT)), //
    PROJECT_CREATE_APPLICATION(getDefaultCreateResourceActionDefinitionVisible(PrismScope.PROJECT)), //
    PROJECT_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(PrismScope.PROJECT)), //
    PROJECT_ESCALATE(getDefaultEscalateResourceActionDefinition(PrismScope.PROJECT)), //
    PROJECT_RESTORE(getDefaultProcessResourceActionDefinitionVisible(PrismScope.PROJECT)), //
    PROJECT_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(PrismScope.PROJECT)), //
    PROJECT_WITHDRAW(getDefaultWithdrawResourceActionDefinition(PrismScope.PROJECT)), //

    PROGRAM_UNENDORSE(getDefaultPartnerActionDefinition(PrismScope.PROGRAM, PrismPartnershipState.ENDORSEMENT_PROVIDED, PrismPartnershipState.ENDORSEMENT_REVOKED)), //
    PROGRAM_REENDORSE(getDefaultPartnerActionDefinition(PrismScope.PROGRAM, PrismPartnershipState.ENDORSEMENT_REVOKED, PrismPartnershipState.ENDORSEMENT_PROVIDED)), //
    PROGRAM_COMPLETE(getDefaultViewEditResourceActionDefinition(PrismScope.PROGRAM)), //
    PROGRAM_COMPLETE_APPROVAL_STAGE(getDefaultProcessResourceActionDefinitionVisible(PrismScope.PROGRAM)), //
    PROGRAM_COMPLETE_PARENT_APPROVAL_STAGE(getDefaultPropagateResourceActionDefinitionVisible(PrismScope.PROGRAM)), //
    PROGRAM_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(PrismScope.PROGRAM)), //
    PROGRAM_CORRECT(getDefaultProcessResourceActionDefinitionVisible(PrismScope.PROGRAM)), //
    PROGRAM_CREATE_PROJECT(getDefaultCreateResourceActionDefinitionInvisible(PrismScope.PROGRAM)), //
    PROGRAM_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(PrismScope.PROGRAM)), //
    PROGRAM_ESCALATE(getDefaultEscalateResourceActionDefinition(PrismScope.PROGRAM)), //
    PROGRAM_RESTORE(getDefaultProcessResourceActionDefinitionVisible(PrismScope.PROGRAM)), //
    PROGRAM_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(PrismScope.PROGRAM)), //
    PROGRAM_WITHDRAW(getDefaultWithdrawResourceActionDefinition(PrismScope.PROGRAM)), //

    DEPARTMENT_COMPLETE(getDefaultViewEditResourceActionDefinition(PrismScope.DEPARTMENT)), //
    DEPARTMENT_COMPLETE_APPROVAL_STAGE(getDefaultProcessResourceActionDefinitionVisible(PrismScope.DEPARTMENT)), //
    DEPARTMENT_COMPLETE_PARENT_APPROVAL_STAGE(getDefaultPropagateResourceActionDefinitionVisible(PrismScope.DEPARTMENT)), //
    DEPARTMENT_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(PrismScope.DEPARTMENT)), //
    DEPARTMENT_CORRECT(getDefaultProcessResourceActionDefinitionVisible(PrismScope.DEPARTMENT)), //
    DEPARTMENT_CREATE_PROGRAM(getDefaultCreateResourceActionDefinitionInvisible(PrismScope.DEPARTMENT)), //
    DEPARTMENT_CREATE_PROJECT(getDefaultCreateResourceActionDefinitionVisible(PrismScope.DEPARTMENT)), //
    DEPARTMENT_CREATE_APPLICATION(getDefaultCreateResourceActionDefinitionVisible(PrismScope.DEPARTMENT)), //
    DEPARTMENT_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(PrismScope.DEPARTMENT)), //
    DEPARTMENT_ESCALATE(getDefaultEscalateResourceActionDefinition(PrismScope.DEPARTMENT)), //
    DEPARTMENT_RESTORE(getDefaultProcessResourceActionDefinitionVisible(PrismScope.DEPARTMENT)), //
    DEPARTMENT_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(PrismScope.DEPARTMENT)), //
    DEPARTMENT_WITHDRAW(getDefaultWithdrawResourceActionDefinition(PrismScope.DEPARTMENT)), //

    INSTITUTION_COMPLETE(getDefaultViewEditResourceActionDefinition(PrismScope.INSTITUTION)), //
    INSTITUTION_COMPLETE_APPROVAL_STAGE(getDefaultProcessResourceActionDefinitionVisible(PrismScope.INSTITUTION)), //
    INSTITUTION_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(PrismScope.INSTITUTION)), //
    INSTITUTION_CORRECT(getDefaultProcessResourceActionDefinitionVisible(PrismScope.INSTITUTION)), //
    INSTITUTION_CREATE_DEPARTMENT(getDefaultCreateResourceActionDefinitionVisible(PrismScope.INSTITUTION)), //
    INSTITUTION_CREATE_PROGRAM(getDefaultCreateResourceActionDefinitionInvisible(PrismScope.INSTITUTION)), //
    INSTITUTION_CREATE_PROJECT(getDefaultCreateResourceActionDefinitionVisible(PrismScope.INSTITUTION)), //
    INSTITUTION_CREATE_APPLICATION(getDefaultCreateResourceActionDefinitionVisible(PrismScope.INSTITUTION)), //
    INSTITUTION_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(PrismScope.INSTITUTION)), //
    INSTITUTION_ESCALATE(getDefaultEscalateResourceActionDefinition(PrismScope.INSTITUTION)), //
    INSTITUTION_RESTORE(getDefaultProcessResourceActionDefinitionVisible(PrismScope.INSTITUTION)), //
    INSTITUTION_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(PrismScope.INSTITUTION)), //
    INSTITUTION_WITHDRAW(getDefaultWithdrawResourceActionDefinition(PrismScope.INSTITUTION)), //

    SYSTEM_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(PrismScope.SYSTEM)), //
    SYSTEM_CREATE_INSTITUTION(getDefaultCreateResourceActionDefinitionVisible(PrismScope.SYSTEM)), //
    SYSTEM_STARTUP(getDefaultResourceActionDefinitionVisible(PrismActionCategory.INITIALISE_RESOURCE, PrismScope.SYSTEM)), //
    SYSTEM_MANAGE_ACCOUNT(getDefaultResourceActionDefinition(PrismActionCategory.MANAGE_ACCOUNT, PrismScope.SYSTEM)), //
    SYSTEM_VIEW_ACTIVITY_LIST(getDefaultResourceActionDefinition(PrismActionCategory.VIEW_ACTIVITY_LIST, PrismScope.SYSTEM)), //
    SYSTEM_VIEW_TASK_LIST(getDefaultResourceActionDefinition(PrismActionCategory.VIEW_ACTIVITY_LIST, PrismScope.SYSTEM)), //
    SYSTEM_VIEW_APPOINTMENT_LIST(getDefaultResourceActionDefinition(PrismActionCategory.VIEW_ACTIVITY_LIST, PrismScope.SYSTEM)), //
    SYSTEM_VIEW_CONNECTION_LIST(getDefaultResourceActionDefinition(PrismActionCategory.VIEW_ACTIVITY_LIST, PrismScope.SYSTEM)), //
    SYSTEM_VIEW_JOIN_LIST(getDefaultResourceActionDefinition(PrismActionCategory.VIEW_ACTIVITY_LIST, PrismScope.SYSTEM)), //
    SYSTEM_VIEW_INSTITUTION_LIST(getDefaultSystemViewResourceListActionDefinition()), //
    SYSTEM_VIEW_DEPARTMENT_LIST(getDefaultSystemViewResourceListActionDefinition()), //
    SYSTEM_VIEW_PROGRAM_LIST(getDefaultSystemViewResourceListActionDefinition()), //
    SYSTEM_VIEW_PROJECT_LIST(getDefaultSystemViewResourceListActionDefinition()), //
    SYSTEM_VIEW_APPLICATION_LIST(getDefaultSystemViewResourceListActionDefinition());

    private PrismActionDefinition actionDefinition;

    private PrismAction(PrismActionDefinition actionDefinition) {
        this.actionDefinition = actionDefinition;
    }

    public boolean isSystemInvocationOnly() {
        return actionDefinition.isSystemInvocationOnly();
    }

    public PrismActionCategory getActionCategory() {
        return actionDefinition.getActionCategory();
    }

    public boolean isRatingAction() {
        return actionDefinition.isRatingAction();
    }

    public boolean isDeclinableAction() {
        return actionDefinition.isDeclinableAction();
    }

    public boolean isVisibleAction() {
        return actionDefinition.isVisibleAction();
    }

    public PrismPartnershipState getPartnershipState() {
        return actionDefinition.getPartnershipState();
    }

    public PrismPartnershipState getPartnershipTransitionState() {
        return actionDefinition.getPartnershipTransitionState();
    }

    public PrismScope getScope() {
        return actionDefinition.getScope();
    }

    public List<PrismActionRedaction> getRedactions() {
        return actionDefinition.getRedactions();
    }

    private static class PrismActionDefinition {

        private boolean systemInvocationOnly = false;

        private PrismActionCategory actionCategory;

        private boolean ratingAction = false;

        private boolean declinableAction = false;

        private boolean visibleAction = false;

        private PrismPartnershipState partnershipState;

        private PrismPartnershipState partnershipTransitionState;

        private PrismScope scope;

        private List<PrismActionRedaction> redactions = Lists.newArrayList();

        public boolean isSystemInvocationOnly() {
            return systemInvocationOnly;
        }

        public PrismActionCategory getActionCategory() {
            return actionCategory;
        }

        public boolean isRatingAction() {
            return ratingAction;
        }

        public boolean isDeclinableAction() {
            return declinableAction;
        }

        public boolean isVisibleAction() {
            return visibleAction;
        }

        public PrismPartnershipState getPartnershipState() {
            return partnershipState;
        }

        public PrismPartnershipState getPartnershipTransitionState() {
            return partnershipTransitionState;
        }

        public PrismScope getScope() {
            return scope;
        }

        public List<PrismActionRedaction> getRedactions() {
            return redactions;
        }

        public PrismActionDefinition withSystemInvocationOnly() {
            this.systemInvocationOnly = true;
            return this;
        }

        public PrismActionDefinition withActionCategory(PrismActionCategory actionCategory) {
            this.actionCategory = actionCategory;
            return this;
        }

        public PrismActionDefinition withRatingAction() {
            this.ratingAction = true;
            return this;
        }

        public PrismActionDefinition withDeclinableAction() {
            this.declinableAction = true;
            return this;
        }

        public PrismActionDefinition withVisibleAction() {
            this.visibleAction = true;
            return this;
        }

        public PrismActionDefinition withPartnershipState(PrismPartnershipState partnershipState) {
            this.partnershipState = partnershipState;
            return this;
        }

        public PrismActionDefinition withPartnershipTransitionState(PrismPartnershipState partnershipTransitionState) {
            this.partnershipTransitionState = partnershipTransitionState;
            return this;
        }

        public PrismActionDefinition withScope(PrismScope scope) {
            this.scope = scope;
            return this;
        }

        public PrismActionDefinition withRedactions(List<PrismActionRedaction> redactions) {
            this.redactions = redactions;
            return this;
        }

    }

    private static PrismActionDefinition getDefaultViewEditApplicationActionDefinition() {
        return getDefaultViewEditResourceActionDefinition(PrismScope.APPLICATION) //
                .withVisibleAction() //
                .withRedactions(getDefaultApplicationActionRedactions());
    }

    private static PrismActionDefinition getDefaultCreateResourceActionDefinitionVisible(PrismScope scope) {
        return getDefaultResourceActionDefinitionVisible(PrismActionCategory.CREATE_RESOURCE, scope);
    }

    private static PrismActionDefinition getDefaultCreateResourceActionDefinitionInvisible(PrismScope scope) {
        return getDefaultResourceActionDefinitionVisible(PrismActionCategory.CREATE_RESOURCE, scope) //
                .withSystemInvocationOnly();
    }

    private static PrismActionDefinition getDefaultViewEditResourceActionDefinition(PrismScope scope) {
        return getDefaultResourceActionDefinition(PrismActionCategory.VIEW_EDIT_RESOURCE, scope);
    }

    private static PrismActionDefinition getDefaultWithdrawResourceActionDefinition(PrismScope scope) {
        return getDefaultResourceActionDefinition(PrismActionCategory.WITHDRAW_RESOURCE, scope);
    }

    private static PrismActionDefinition getDefaultSystemViewResourceListActionDefinition() {
        return getDefaultResourceActionDefinition(PrismActionCategory.VIEW_RESOURCE_LIST, PrismScope.SYSTEM);
    }

    private static PrismActionDefinition getDefaultRateApplicationActionDefinition() {
        return getDefaultProcessApplicationActionDefinitionWithRedactions() //
                .withRatingAction();
    }

    private static PrismActionDefinition getDefaultRateApplicationActionDefinitionDeclinable() {
        return getDefaultRateApplicationActionDefinition() //
                .withDeclinableAction();
    }

    private static PrismActionDefinition getDefaultPartnerActionDefinition(PrismScope scope, PrismPartnershipState partnershipState,
            PrismPartnershipState partnershipTransitionState) {
        return getDefaultProcessResourceActionDefinition(scope) //
                .withPartnershipState(partnershipState) //
                .withPartnershipTransitionState(partnershipTransitionState);
    }

    private static PrismActionDefinition getDefaultProcessApplicationActionDefinition() {
        return getDefaultApplicationActionDefinition(PrismActionCategory.PROCESS_RESOURCE);
    }

    private static PrismActionDefinition getDefaultProcessApplicationActionDefinitionWithRedactions() {
        return getDefaultProcessApplicationActionDefinition() //
                .withRedactions(getDefaultApplicationActionRedactions());
    }

    private static PrismActionDefinition getDefaultPropagateResourceActionDefinitionVisible(PrismScope scope) {
        return getDefaultResourceActionDefinitionSystemInvocation(PrismActionCategory.PROPAGATE_RESOURCE, scope) //
                .withVisibleAction();
    }

    private static PrismActionDefinition getDefaultEmailResourceCreatorActionDefinition(PrismScope scope) {
        return getDefaultResourceActionDefinition(PrismActionCategory.EMAIL_RESOURCE_CREATOR, scope);
    }

    private static PrismActionDefinition getDefaultEscalateResourceActionDefinition(PrismScope scope) {
        return getDefaultResourceActionDefinitionSystemInvocation(PrismActionCategory.ESCALATE_RESOURCE, scope);
    }

    private static PrismActionDefinition getDefaultProcessResourceActionDefinition(PrismScope scope) {
        return getDefaultResourceActionDefinition(PrismActionCategory.PROCESS_RESOURCE, scope);
    }

    private static PrismActionDefinition getDefaultProcessResourceActionDefinitionVisible(PrismScope scope) {
        return getDefaultProcessResourceActionDefinition(scope) //
                .withVisibleAction();
    }

    private static PrismActionDefinition getDefaultApplicationActionDefinition(PrismActionCategory actionCategory) {
        return getDefaultResourceActionDefinitionVisible(actionCategory, PrismScope.APPLICATION);
    }

    private static PrismActionDefinition getDefaultResourceActionDefinitionSystemInvocation(PrismActionCategory actionCategory, PrismScope scope) {
        return getDefaultResourceActionDefinition(actionCategory, scope) //
                .withSystemInvocationOnly();
    }

    private static PrismActionDefinition getDefaultResourceActionDefinitionVisible(PrismActionCategory actionCategory, PrismScope scope) {
        return getDefaultResourceActionDefinition(actionCategory, scope) //
                .withVisibleAction();
    }

    private static PrismActionDefinition getDefaultResourceActionDefinition(PrismActionCategory actionCategory, PrismScope scope) {
        return new PrismActionDefinition() //
                .withActionCategory(actionCategory) //
                .withScope(scope);
    }

    private static List<PrismActionRedaction> getDefaultApplicationActionRedactions() {
        return Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismActionRedactionType.ALL_ASSESSMENT_CONTENT), //
                new PrismActionRedaction().withRole(PrismRole.APPLICATION_INTERVIEWEE).withRedactionType(PrismActionRedactionType.ALL_ASSESSMENT_CONTENT), //
                new PrismActionRedaction().withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(PrismActionRedactionType.ALL_ASSESSMENT_CONTENT), //
                new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismActionRedactionType.ALL_CONTENT), //
                new PrismActionRedaction().withRole(PrismRole.APPLICATION_VIEWER_REFEREE).withRedactionType(PrismActionRedactionType.ALL_CONTENT));
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_ACTION_" + name());
    }

}
