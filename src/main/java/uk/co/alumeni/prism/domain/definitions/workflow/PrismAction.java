package uk.co.alumeni.prism.domain.definitions.workflow;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.EMAIL_RESOURCE_CREATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.ESCALATE_RESOURCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.INITIALISE_RESOURCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.MANAGE_ACCOUNT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.PROCESS_RESOURCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.PROPAGATE_RESOURCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.VIEW_ACTIVITY_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.VIEW_EDIT_RESOURCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.VIEW_RESOURCE_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.WITHDRAW_RESOURCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionRedactionType.ALL_ASSESSMENT_CONTENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionRedactionType.ALL_CONTENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PROVIDED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_REVOKED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWEE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_REFEREE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismLocalizableDefinition;

import com.google.common.collect.Lists;

public enum PrismAction implements PrismLocalizableDefinition {

    APPLICATION_ASSIGN_HIRING_MANAGERS(getDefaultProcessApplicationActionDefinitionWithRedactionsAndReplicableUserAssignments()), //
    APPLICATION_ASSIGN_INTERVIEWERS(getDefaultProcessApplicationActionDefinitionWithRedactionsAndReplicableUserAssignments()), //
    APPLICATION_ASSIGN_REVIEWERS(getDefaultProcessApplicationActionDefinitionWithRedactionsAndReplicableUserAssignments()), //
    APPLICATION_COMMENT(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE(getDefaultViewEditApplicationActionDefinition()), //
    APPLICATION_COMPLETE_VALIDATION_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_REFERENCE_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_REVIEW_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_INTERVIEW_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_APPROVAL_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_RESERVED_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_APPROVED_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_REJECTED_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_CONFIRM_OFFER(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_CONFIRM_OFFER_ACCEPTANCE(getDefaultProcessApplicationActionDefinition()), //
    APPLICATION_REVISE_OFFER(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_CONFIRM_REJECTION(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(APPLICATION)), //
    APPLICATION_ESCALATE(getDefaultEscalateResourceActionDefinition(APPLICATION)), //
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
    APPLICATION_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(APPLICATION)), //
    APPLICATION_WITHDRAW(getDefaultWithdrawResourceActionDefinition(APPLICATION)),

    PROJECT_UNENDORSE(getDefaultPartnerActionDefinition(PROJECT, ENDORSEMENT_PROVIDED, ENDORSEMENT_REVOKED)), //
    PROJECT_REENDORSE(getDefaultPartnerActionDefinition(PROJECT, ENDORSEMENT_REVOKED, ENDORSEMENT_PROVIDED)), //
    PROJECT_COMPLETE(getDefaultViewEditResourceActionDefinition(PrismScope.PROJECT)), //
    PROJECT_COMPLETE_APPROVAL_STAGE(getDefaultProcessResourceActionDefinitionVisible(PROJECT)), //
    PROJECT_COMPLETE_PARENT_APPROVAL_STAGE(getDefaultPropagateResourceActionDefinitionVisible(PROJECT)), //
    PROJECT_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(PROJECT)), //
    PROJECT_CORRECT(getDefaultProcessResourceActionDefinitionVisible(PROJECT)), //
    PROJECT_CREATE_APPLICATION(getDefaultCreateResourceActionDefinitionVisible(PROJECT)), //
    PROJECT_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(PROJECT)), //
    PROJECT_ESCALATE(getDefaultEscalateResourceActionDefinition(PROJECT)), //
    PROJECT_RESTORE(getDefaultProcessResourceActionDefinitionVisible(PROJECT)), //
    PROJECT_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(PROJECT)), //
    PROJECT_WITHDRAW(getDefaultWithdrawResourceActionDefinition(PROJECT)), //

    PROGRAM_UNENDORSE(getDefaultPartnerActionDefinition(PROGRAM, ENDORSEMENT_PROVIDED, ENDORSEMENT_REVOKED)), //
    PROGRAM_REENDORSE(getDefaultPartnerActionDefinition(PROGRAM, ENDORSEMENT_REVOKED, ENDORSEMENT_PROVIDED)), //
    PROGRAM_COMPLETE(getDefaultViewEditResourceActionDefinition(PROGRAM)), //
    PROGRAM_COMPLETE_APPROVAL_STAGE(getDefaultProcessResourceActionDefinitionVisible(PROGRAM)), //
    PROGRAM_COMPLETE_PARENT_APPROVAL_STAGE(getDefaultPropagateResourceActionDefinitionVisible(PROGRAM)), //
    PROGRAM_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(PROGRAM)), //
    PROGRAM_CORRECT(getDefaultProcessResourceActionDefinitionVisible(PROGRAM)), //
    PROGRAM_CREATE_PROJECT(getDefaultCreateResourceActionDefinitionInvisible(PROGRAM)), //
    PROGRAM_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(PROGRAM)), //
    PROGRAM_ESCALATE(getDefaultEscalateResourceActionDefinition(PROGRAM)), //
    PROGRAM_RESTORE(getDefaultProcessResourceActionDefinitionVisible(PROGRAM)), //
    PROGRAM_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(PROGRAM)), //
    PROGRAM_WITHDRAW(getDefaultWithdrawResourceActionDefinition(PROGRAM)), //

    DEPARTMENT_COMPLETE(getDefaultViewEditResourceActionDefinition(DEPARTMENT)), //
    DEPARTMENT_COMPLETE_APPROVAL_STAGE(getDefaultProcessResourceActionDefinitionVisible(DEPARTMENT)), //
    DEPARTMENT_COMPLETE_PARENT_APPROVAL_STAGE(getDefaultPropagateResourceActionDefinitionVisible(DEPARTMENT)), //
    DEPARTMENT_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(DEPARTMENT)), //
    DEPARTMENT_CORRECT(getDefaultProcessResourceActionDefinitionVisible(DEPARTMENT)), //
    DEPARTMENT_CREATE_PROGRAM(getDefaultCreateResourceActionDefinitionInvisible(DEPARTMENT)), //
    DEPARTMENT_CREATE_PROJECT(getDefaultCreateResourceActionDefinitionVisible(DEPARTMENT)), //
    DEPARTMENT_CREATE_APPLICATION(getDefaultCreateResourceActionDefinitionVisible(DEPARTMENT)), //
    DEPARTMENT_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(DEPARTMENT)), //
    DEPARTMENT_ESCALATE(getDefaultEscalateResourceActionDefinition(DEPARTMENT)), //
    DEPARTMENT_RESTORE(getDefaultProcessResourceActionDefinitionVisible(DEPARTMENT)), //
    DEPARTMENT_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(DEPARTMENT)), //
    DEPARTMENT_WITHDRAW(getDefaultWithdrawResourceActionDefinition(DEPARTMENT)), //

    INSTITUTION_COMPLETE(getDefaultViewEditResourceActionDefinition(INSTITUTION)), //
    INSTITUTION_COMPLETE_APPROVAL_STAGE(getDefaultProcessResourceActionDefinitionVisible(INSTITUTION)), //
    INSTITUTION_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(INSTITUTION)), //
    INSTITUTION_CORRECT(getDefaultProcessResourceActionDefinitionVisible(INSTITUTION)), //
    INSTITUTION_CREATE_DEPARTMENT(getDefaultCreateResourceActionDefinitionVisible(INSTITUTION)), //
    INSTITUTION_CREATE_PROGRAM(getDefaultCreateResourceActionDefinitionInvisible(INSTITUTION)), //
    INSTITUTION_CREATE_PROJECT(getDefaultCreateResourceActionDefinitionVisible(INSTITUTION)), //
    INSTITUTION_CREATE_APPLICATION(getDefaultCreateResourceActionDefinitionVisible(INSTITUTION)), //
    INSTITUTION_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(INSTITUTION)), //
    INSTITUTION_ESCALATE(getDefaultEscalateResourceActionDefinition(INSTITUTION)), //
    INSTITUTION_RESTORE(getDefaultProcessResourceActionDefinitionVisible(INSTITUTION)), //
    INSTITUTION_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(INSTITUTION)), //
    INSTITUTION_WITHDRAW(getDefaultWithdrawResourceActionDefinition(INSTITUTION)), //

    SYSTEM_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(SYSTEM)), //
    SYSTEM_CREATE_INSTITUTION(getDefaultCreateResourceActionDefinitionVisible(SYSTEM)), //
    SYSTEM_STARTUP(getDefaultResourceActionDefinitionVisible(INITIALISE_RESOURCE, SYSTEM)), //
    SYSTEM_MANAGE_ACCOUNT(getDefaultResourceActionDefinition(MANAGE_ACCOUNT, PrismScope.SYSTEM)), //
    SYSTEM_VIEW_ACTIVITY_LIST(getDefaultResourceActionDefinition(VIEW_ACTIVITY_LIST, SYSTEM)), //
    SYSTEM_VIEW_TASK_LIST(getDefaultResourceActionDefinition(VIEW_ACTIVITY_LIST, SYSTEM)), //
    SYSTEM_VIEW_APPOINTMENT_LIST(getDefaultResourceActionDefinition(VIEW_ACTIVITY_LIST, SYSTEM)), //
    SYSTEM_VIEW_CONNECTION_LIST(getDefaultResourceActionDefinition(VIEW_ACTIVITY_LIST, SYSTEM)), //
    SYSTEM_VIEW_JOIN_LIST(getDefaultResourceActionDefinition(VIEW_ACTIVITY_LIST, SYSTEM)), //
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

    public boolean isTransitionAction() {
        return actionDefinition.isTransitionAction();
    }

    public boolean isDeclinableAction() {
        return actionDefinition.isDeclinableAction();
    }

    public boolean isVisibleAction() {
        return actionDefinition.isVisibleAction();
    }

    public boolean isReplicableUserAssignmentAction() {
        return actionDefinition.isReplicableUserAssignmentAction();
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

    public String getZeroPaddedOrdinal() {
        return StringUtils.leftPad(String.valueOf(this.ordinal()), (String.valueOf(values().length).length() - 1), "0");
    }

    private static class PrismActionDefinition {

        private boolean systemInvocationOnly = false;

        private PrismActionCategory actionCategory;

        private boolean ratingAction = false;

        private boolean transitionAction = false;

        private boolean declinableAction = false;

        private boolean visibleAction = false;

        private boolean replicableUserAssignmentAction = false;

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

        public boolean isTransitionAction() {
            return transitionAction;
        }

        public boolean isDeclinableAction() {
            return declinableAction;
        }

        public boolean isVisibleAction() {
            return visibleAction;
        }

        public boolean isReplicableUserAssignmentAction() {
            return replicableUserAssignmentAction;
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

        public PrismActionDefinition withReplicableUserAssignmentAction() {
            this.replicableUserAssignmentAction = true;
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
        return getDefaultViewEditResourceActionDefinition(APPLICATION) //
                .withVisibleAction() //
                .withRedactions(getDefaultApplicationActionRedactions());
    }

    private static PrismActionDefinition getDefaultCreateResourceActionDefinitionVisible(PrismScope scope) {
        return getDefaultResourceActionDefinitionVisible(CREATE_RESOURCE, scope);
    }

    private static PrismActionDefinition getDefaultCreateResourceActionDefinitionInvisible(PrismScope scope) {
        return getDefaultResourceActionDefinitionVisible(CREATE_RESOURCE, scope) //
                .withSystemInvocationOnly();
    }

    private static PrismActionDefinition getDefaultViewEditResourceActionDefinition(PrismScope scope) {
        return getDefaultResourceActionDefinition(VIEW_EDIT_RESOURCE, scope);
    }

    private static PrismActionDefinition getDefaultWithdrawResourceActionDefinition(PrismScope scope) {
        return getDefaultResourceActionDefinition(WITHDRAW_RESOURCE, scope);
    }

    private static PrismActionDefinition getDefaultSystemViewResourceListActionDefinition() {
        return getDefaultResourceActionDefinition(VIEW_RESOURCE_LIST, SYSTEM);
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
        return getDefaultApplicationActionDefinition(PROCESS_RESOURCE);
    }

    private static PrismActionDefinition getDefaultProcessApplicationActionDefinitionWithRedactionsAndReplicableUserAssignments() {
        return getDefaultProcessApplicationActionDefinition() //
                .withRedactions(getDefaultApplicationActionRedactions()) //
                .withReplicableUserAssignmentAction();
    }

    private static PrismActionDefinition getDefaultProcessApplicationActionDefinitionWithRedactions() {
        return getDefaultProcessApplicationActionDefinition() //
                .withRedactions(getDefaultApplicationActionRedactions());
    }

    private static PrismActionDefinition getDefaultPropagateResourceActionDefinitionVisible(PrismScope scope) {
        return getDefaultResourceActionDefinitionSystemInvocation(PROPAGATE_RESOURCE, scope) //
                .withVisibleAction();
    }

    private static PrismActionDefinition getDefaultEmailResourceCreatorActionDefinition(PrismScope scope) {
        return getDefaultResourceActionDefinition(EMAIL_RESOURCE_CREATOR, scope);
    }

    private static PrismActionDefinition getDefaultEscalateResourceActionDefinition(PrismScope scope) {
        return getDefaultResourceActionDefinitionSystemInvocation(ESCALATE_RESOURCE, scope);
    }

    private static PrismActionDefinition getDefaultProcessResourceActionDefinition(PrismScope scope) {
        return getDefaultResourceActionDefinition(PROCESS_RESOURCE, scope);
    }

    private static PrismActionDefinition getDefaultProcessResourceActionDefinitionVisible(PrismScope scope) {
        return getDefaultProcessResourceActionDefinition(scope) //
                .withVisibleAction();
    }

    private static PrismActionDefinition getDefaultApplicationActionDefinition(PrismActionCategory actionCategory) {
        return getDefaultResourceActionDefinitionVisible(actionCategory, APPLICATION);
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
        return Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT));
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_ACTION_" + name());
    }

}
