package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

public enum PrismAction {

    APPLICATION_ASSESS_ELIGIBILITY(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_ASSIGN_INTERVIEWERS(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_ASSIGN_REVIEWERS(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_ASSIGN_SUPERVISORS(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_COMMENT(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_COMPLETE(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, null), //
    APPLICATION_COMPLETE_APPROVAL_STAGE(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_COMPLETE_INTERVIEW_STAGE(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_COMPLETE_REVIEW_STAGE(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_COMPLETE_VALIDATION_STAGE(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_CONFIRM_ELIGIBILITY(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_ASSESSMENT_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, null), //
    APPLICATION_CONFIRM_REJECTION(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, null), //
    APPLICATION_CONFIRM_SUPERVISION(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_CORRECT(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, null), //
    APPLICATION_EMAIL_CREATOR(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, null), //
    APPLICATION_ESCALATE(PrismActionType.SYSTEM_ESCALATION, PrismScope.APPLICATION, null, null), //
    APPLICATION_EXPORT(PrismActionType.SYSTEM_ESCALATION, PrismScope.APPLICATION, null, null), //
    APPLICATION_MOVE_TO_DIFFERENT_STAGE(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_PROVIDE_REFERENCE(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_PROVIDE_REVIEW(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_TERMINATE(PrismActionType.SYSTEM_PROPAGATION, PrismScope.APPLICATION, null, null), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_VIEW_EDIT(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, Arrays.asList( //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT), //
            new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT))), //
    APPLICATION_WITHDRAW(PrismActionType.USER_INVOCATION, PrismScope.APPLICATION, null, null), //
    INSTITUTION_CONFIGURE(PrismActionType.USER_INVOCATION, PrismScope.INSTITUTION, null, null), //
    INSTITUTION_CREATE_PROGRAM(PrismActionType.USER_INVOCATION, PrismScope.INSTITUTION, PrismScope.PROGRAM, null), //
    INSTITUTION_IMPORT_PROGRAM(PrismActionType.USER_INVOCATION, PrismScope.INSTITUTION, null, null), //
    INSTITUTION_VIEW(PrismActionType.USER_INVOCATION, PrismScope.INSTITUTION, null, null), //
    INSTITUTION_VIEW_APPLICATION_LIST(PrismActionType.USER_INVOCATION, PrismScope.INSTITUTION, null, null), //
    INSTITUTION_VIEW_PROGRAM_LIST(PrismActionType.USER_INVOCATION, PrismScope.INSTITUTION, null, null), //
    INSTITUTION_VIEW_PROJECT_LIST(PrismActionType.USER_INVOCATION, PrismScope.INSTITUTION, null, null), //
    PROGRAM_COMPLETE_APPROVAL_STAGE(PrismActionType.USER_INVOCATION, PrismScope.PROGRAM, null, null), //
    PROGRAM_CONCLUDE(PrismActionType.SYSTEM_PROPAGATION, PrismScope.PROGRAM, null, null), //
    PROGRAM_CONFIGURE(PrismActionType.USER_INVOCATION, PrismScope.PROGRAM, null, null), //
    PROGRAM_CORRECT(PrismActionType.USER_INVOCATION, PrismScope.PROGRAM, null, null), //
    PROGRAM_CREATE_APPLICATION(PrismActionType.USER_INVOCATION, PrismScope.PROGRAM, PrismScope.APPLICATION, null), //
    PROGRAM_CREATE_PROJECT(PrismActionType.USER_INVOCATION, PrismScope.PROGRAM, PrismScope.PROJECT, null), //
    PROGRAM_EMAIL_CREATOR(PrismActionType.USER_INVOCATION, PrismScope.PROGRAM, null, null), //
    PROGRAM_ESCALATE(PrismActionType.SYSTEM_ESCALATION, PrismScope.PROGRAM, null, null), //
    PROGRAM_RESTORE(PrismActionType.SYSTEM_PROPAGATION, PrismScope.PROGRAM, null, null), //
    PROGRAM_VIEW_APPLICATION_LIST(PrismActionType.USER_INVOCATION, PrismScope.PROGRAM, null, null), //
    PROGRAM_VIEW(PrismActionType.USER_INVOCATION, PrismScope.PROGRAM, null, null), //
    PROGRAM_VIEW_PROJECT_LIST(PrismActionType.USER_INVOCATION, PrismScope.PROGRAM, null, null), //
    PROGRAM_WITHDRAW(PrismActionType.USER_INVOCATION, PrismScope.PROGRAM, null, null), //
    PROJECT_COMPLETE_APPROVAL_STAGE(PrismActionType.USER_INVOCATION, PrismScope.PROJECT, null, null),
    PROJECT_CONCLUDE(PrismActionType.SYSTEM_PROPAGATION, PrismScope.PROJECT, null, null), //
    PROJECT_CONFIGURE(PrismActionType.USER_INVOCATION, PrismScope.PROJECT, null, null), //
    PROJECT_CORRECT(PrismActionType.USER_INVOCATION, PrismScope.PROJECT, null, null),
    PROJECT_CREATE_APPLICATION(PrismActionType.USER_INVOCATION, PrismScope.PROJECT, PrismScope.APPLICATION, null), //
    PROJECT_EMAIL_CREATOR(PrismActionType.USER_INVOCATION, PrismScope.PROJECT, null, null), //
    PROJECT_ESCALATE(PrismActionType.SYSTEM_ESCALATION, PrismScope.PROJECT, null, null), //
    PROJECT_RESTORE(PrismActionType.SYSTEM_PROPAGATION, PrismScope.PROJECT, null, null), //
    PROJECT_SUSPEND(PrismActionType.SYSTEM_PROPAGATION, PrismScope.PROJECT, null, null), //
    PROJECT_TERMINATE(PrismActionType.SYSTEM_PROPAGATION, PrismScope.PROJECT, null, null), //
    PROJECT_VIEW(PrismActionType.USER_INVOCATION, PrismScope.PROJECT, null, null), //
    PROJECT_VIEW_APPLICATION_LIST(PrismActionType.USER_INVOCATION, PrismScope.PROJECT, null, null), //
    PROJECT_WITHDRAW(PrismActionType.USER_INVOCATION, PrismScope.PROJECT, null, null), //
    SYSTEM_CONFIGURE(PrismActionType.USER_INVOCATION, PrismScope.SYSTEM, null, null), //
    SYSTEM_CREATE_INSTITUTION(PrismActionType.USER_INVOCATION, PrismScope.SYSTEM, PrismScope.INSTITUTION, null), //
    SYSTEM_MANAGE_ACCOUNT(PrismActionType.USER_INVOCATION, PrismScope.SYSTEM, null, null), //
    SYSTEM_VIEW_APPLICATION_LIST(PrismActionType.USER_INVOCATION, PrismScope.SYSTEM, null, null), //
    SYSTEM_VIEW_INSTITUTION_LIST(PrismActionType.USER_INVOCATION, PrismScope.SYSTEM, null, null), //
    SYSTEM_VIEW_PROGRAM_LIST(PrismActionType.USER_INVOCATION, PrismScope.SYSTEM, null, null), //
    SYSTEM_VIEW_PROJECT_LIST(PrismActionType.USER_INVOCATION, PrismScope.SYSTEM, null, null);

    private PrismActionType actionType;

    private PrismScope scope;

    private PrismScope creationScope;

    private List<PrismActionRedaction> redactions = Lists.newArrayList();
    
    private static final List<PrismAction> creationActions = Lists.newArrayList();
    
    static {
        for (PrismAction action : PrismAction.values()) {
            if (action.getCreationScope() != null) {
                creationActions.add(action);
            }
        }
    }

    private PrismAction(PrismActionType actionType, PrismScope scope, PrismScope creationScope, List<PrismActionRedaction> redactions) {
        this.actionType = actionType;
        this.scope = scope;
        this.creationScope = creationScope;
        this.redactions = redactions == null ? this.redactions : redactions;
    }

    public PrismActionType getActionType() {
        return actionType;
    }

    public PrismScope getScope() {
        return scope;
    }

    public PrismScope getCreationScope() {
        return creationScope;
    }

    public List<PrismActionRedaction> getRedactions() {
        return redactions;
    }

    public static List<PrismAction> getCreationActions() {
        return creationActions;
    }

}