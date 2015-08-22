package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.EMAIL_RESOURCE_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.ESCALATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.EXPORT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.IMPORT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.INITIALISE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.MANAGE_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.PROCESS_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.PROPAGATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.PURGE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_EDIT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_RESOURCE_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.WITHDRAW_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType.ALL_ASSESSMENT_CONTENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType.ALL_CONTENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocalizableDefinition;

public enum PrismAction implements PrismLocalizableDefinition {

    APPLICATION_ASSIGN_INTERVIEWERS(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_ASSIGN_REVIEWERS(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_ASSIGN_SUPERVISORS(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_RESERVE(getDefaultProcessApplicationActionDefinition()), APPLICATION_COMMENT(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE(getDefaultViewEditApplicationActionDefinition()), //
    APPLICATION_COMPLETE_VALIDATION_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_VERIFICATION_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_REFERENCE_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_REVIEW_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_INTERVIEW_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_APPROVAL_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_APPROVED_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_RESERVED_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_COMPLETE_REJECTED_STAGE(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_CONFIRM_ELIGIBILITY(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION(getDefaultProcessApplicationActionDefinition()), //
    APPLICATION_CONFIRM_REJECTION(getDefaultProcessApplicationActionDefinition()), //
    APPLICATION_CONFIRM_PRIMARY_SUPERVISION(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_CONFIRM_SECONDARY_SUPERVISION(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_CORRECT(getDefaultProcessApplicationActionDefinition()), //
    APPLICATION_FORGET_EXPORT(getDefaultProcessApplicationActionDefinition()), //
    APPLICATION_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(APPLICATION)), //
    APPLICATION_ESCALATE(getDefaultEscalateResourceActionDefinition(APPLICATION)), //
    APPLICATION_EXPORT(getDefaultResourceActionDefinitionSystemInvocation(EXPORT_RESOURCE, APPLICATION) //
            .withVisibleAction() //
            .withRedactions(getDefaultApplicationActionRedactions())), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK(getDefaultRateApplicationActionDefinition()), //
    APPLICATION_PROVIDE_REFERENCE(getDefaultRateApplicationActionDefinitionDeclinable()), //
    APPLICATION_PROVIDE_REVIEW(getDefaultRateApplicationActionDefinition()), //
    APPLICATION_PURGE(getDefaultResourceActionDefinitionSystemInvocation(PURGE_RESOURCE, APPLICATION)), //
    APPLICATION_REVERSE_REJECTION(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY(getDefaultProcessApplicationActionDefinitionWithRedactions()), //
    APPLICATION_UPLOAD_REFERENCE(getDefaultRateApplicationActionDefinitionDeclinable()), //
    APPLICATION_VIEW_EDIT(getDefaultViewEditApplicationActionDefinition()), //
    APPLICATION_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(APPLICATION)), //
    APPLICATION_WITHDRAW(getDefaultWithdrawResourceActionDefinition(APPLICATION)),

    PROJECT_ENDORSE(getDefaultEndorseResourceActionDefinition(PROJECT)), //
    PROJECT_COMPLETE_APPROVAL_STAGE(getDefaultProcessResourceActionDefinitionVisible(PROJECT)), //
    PROJECT_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(PROJECT)), //
    PROJECT_CORRECT(getDefaultProcessResourceActionDefinitionVisible(PROJECT)), //
    PROJECT_CREATE_APPLICATION(getDefaultCreateResourceActionDefinition(PROJECT)), //
    PROJECT_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(PROJECT)), //
    PROJECT_ESCALATE(getDefaultEscalateResourceActionDefinition(PROJECT)), //
    PROJECT_RESTORE(getDefaultProcessResourceActionDefinitionVisible(PROJECT)), //
    PROJECT_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(PROJECT)), //
    PROJECT_WITHDRAW(getDefaultWithdrawResourceActionDefinition(PROJECT)), //

    PROGRAM_ENDORSE(getDefaultEndorseResourceActionDefinition(PROGRAM)), //
    PROGRAM_COMPLETE_APPROVAL_STAGE(getDefaultProcessResourceActionDefinitionVisible(PROGRAM)), //
    PROGRAM_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(PROGRAM)), //
    PROGRAM_CORRECT(getDefaultProcessResourceActionDefinitionVisible(PROGRAM)), //
    PROGRAM_CREATE_APPLICATION(getDefaultCreateResourceActionDefinition(PROGRAM)), //
    PROGRAM_CREATE_PROJECT(getDefaultCreateResourceActionDefinition(PROGRAM)), //
    PROGRAM_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(PROGRAM)), //
    PROGRAM_ESCALATE(getDefaultEscalateResourceActionDefinition(PROGRAM)), //
    PROGRAM_IMPORT_PROJECT(getDefaultImportResourceActionDefinition(PROGRAM)), //
    PROGRAM_RESTORE(getDefaultProcessResourceActionDefinitionVisible(PROGRAM)), //
    PROGRAM_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(PROGRAM)), //
    PROGRAM_WITHDRAW(getDefaultWithdrawResourceActionDefinition(PROGRAM)), //

    DEPARTMENT_ENDORSE(getDefaultEndorseResourceActionDefinition(DEPARTMENT)), //
    DEPARTMENT_COMPLETE_APPROVAL_STAGE(getDefaultProcessResourceActionDefinitionVisible(DEPARTMENT)), //
    DEPARTMENT_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(DEPARTMENT)), //
    DEPARTMENT_CORRECT(getDefaultProcessResourceActionDefinitionVisible(DEPARTMENT)), //
    DEPARTMENT_CREATE_APPLICATION(getDefaultCreateResourceActionDefinition(DEPARTMENT)), //
    DEPARTMENT_CREATE_PROJECT(getDefaultCreateResourceActionDefinition(DEPARTMENT)), //
    DEPARTMENT_CREATE_PROGRAM(getDefaultCreateResourceActionDefinition(DEPARTMENT)), //
    DEPARTMENT_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(DEPARTMENT)), //
    DEPARTMENT_ESCALATE(getDefaultEscalateResourceActionDefinition(DEPARTMENT)), //
    DEPARTMENT_IMPORT_PROGRAM(getDefaultImportResourceActionDefinition(DEPARTMENT)), //
    DEPARTMENT_IMPORT_PROJECT(getDefaultImportResourceActionDefinition(DEPARTMENT)), //
    DEPARTMENT_RESTORE(getDefaultProcessResourceActionDefinitionVisible(DEPARTMENT)), //
    DEPARTMENT_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(DEPARTMENT)), //
    DEPARTMENT_WITHDRAW(getDefaultWithdrawResourceActionDefinition(DEPARTMENT)), //

    INSTITUTION_ENDORSE(getDefaultEndorseResourceActionDefinition(INSTITUTION)), //
    INSTITUTION_COMPLETE_APPROVAL_STAGE(getDefaultProcessResourceActionDefinitionVisible(INSTITUTION)), //
    INSTITUTION_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(INSTITUTION)), //
    INSTITUTION_CORRECT(getDefaultProcessResourceActionDefinitionVisible(INSTITUTION)), //
    INSTITUTION_CREATE_DEPARTMENT(getDefaultCreateResourceActionDefinition(INSTITUTION)), //
    INSTITUTION_CREATE_PROGRAM(getDefaultCreateResourceActionDefinition(INSTITUTION)), //
    INSTITUTION_CREATE_PROJECT(getDefaultCreateResourceActionDefinition(INSTITUTION)), //
    INSTITUTION_CREATE_APPLICATION(getDefaultCreateResourceActionDefinition(INSTITUTION)), //
    INSTITUTION_EMAIL_CREATOR(getDefaultEmailResourceCreatorActionDefinition(INSTITUTION)), //
    INSTITUTION_ESCALATE(getDefaultEscalateResourceActionDefinition(INSTITUTION)), //
    INSTITUTION_IMPORT_DEPARTMENT(getDefaultImportResourceActionDefinition(INSTITUTION)), //
    INSTITUTION_IMPORT_PROGRAM(getDefaultImportResourceActionDefinition(INSTITUTION)), //
    INSTITUTION_IMPORT_PROJECT(getDefaultImportResourceActionDefinition(INSTITUTION)), //
    INSTITUTION_RESTORE(getDefaultProcessResourceActionDefinitionVisible(INSTITUTION)), //
    INSTITUTION_TERMINATE(getDefaultPropagateResourceActionDefinitionVisible(INSTITUTION)), //
    INSTITUTION_WITHDRAW(getDefaultWithdrawResourceActionDefinition(INSTITUTION)), //

    SYSTEM_VIEW_EDIT(getDefaultViewEditResourceActionDefinition(SYSTEM)), //
    SYSTEM_CREATE_INSTITUTION(getDefaultCreateResourceActionDefinition(SYSTEM)), //
    SYSTEM_IMPORT_INSTITUTION(getDefaultImportResourceActionDefinition(SYSTEM)), //
    SYSTEM_STARTUP(getDefaultResourceActionDefinitionVisible(INITIALISE_RESOURCE, SYSTEM)), //
    SYSTEM_MANAGE_ACCOUNT(getDefaultResourceActionDefinition(MANAGE_ACCOUNT, SYSTEM)), //
    SYSTEM_VIEW_APPLICATION_LIST(getDefaultSystemViewResourceListActionDefinition()), //
    SYSTEM_VIEW_INSTITUTION_LIST(getDefaultSystemViewResourceListActionDefinition()), //
    SYSTEM_VIEW_DEPARTMENT_LIST(getDefaultSystemViewResourceListActionDefinition()), //
    SYSTEM_VIEW_PROGRAM_LIST(getDefaultSystemViewResourceListActionDefinition()), //
    SYSTEM_VIEW_PROJECT_LIST(getDefaultSystemViewResourceListActionDefinition());

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

    private static PrismActionDefinition getDefaultCreateResourceActionDefinition(PrismScope scope) {
        return getDefaultResourceActionDefinitionVisible(CREATE_RESOURCE, scope);
    }

    private static PrismActionDefinition getDefaultViewEditResourceActionDefinition(PrismScope scope) {
        return getDefaultResourceActionDefinition(VIEW_EDIT_RESOURCE, scope);
    }

    private static PrismActionDefinition getDefaultImportResourceActionDefinition(PrismScope scope) {
        return getDefaultResourceActionDefinition(IMPORT_RESOURCE, scope) //
                .withSystemInvocationOnly();
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

    private static PrismActionDefinition getDefaultProcessApplicationActionDefinition() {
        return getDefaultApplicationActionDefinition(PROCESS_RESOURCE);
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

    private static PrismActionDefinition getDefaultEndorseResourceActionDefinition(PrismScope scope) {
        return getDefaultProcessResourceActionDefinitionVisible(scope) //
                .withRatingAction();
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

    private static PrismActionDefinition getDefaultResourceActionDefinition(PrismActionCategory actionCategory, PrismScope scope) {
        return new PrismActionDefinition() //
                .withActionCategory(actionCategory) //
                .withScope(scope);
    }

    private static PrismActionDefinition getDefaultResourceActionDefinitionVisible(PrismActionCategory actionCategory, PrismScope scope) {
        return getDefaultResourceActionDefinition(actionCategory, scope) //
                .withVisibleAction();
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

    public enum PrismActionGroup {

        RESOURCE_ENDORSE(INSTITUTION_ENDORSE, DEPARTMENT_ENDORSE, PROGRAM_ENDORSE, PROJECT_ENDORSE);

        private PrismAction[] actions;

        private PrismActionGroup(PrismAction... actions) {
            this.actions = actions;
        }

        public PrismAction[] getActions() {
            return actions;
        }

    }

}
