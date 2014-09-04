package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.rest.validation.validator.comment.AssignInterviewersCommentCustomValidator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionValidationFieldRestriction.*;

public enum PrismAction {

    APPLICATION_ASSESS_ELIGIBILITY(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(QUALIFIED, NOT_NULL)
                    .addResolution(COMPETENT_IN_WORK_LANGUAGE, NOT_NULL)
                    .addResolution(RESIDENCE_STATE, NOT_NULL)
                    .build()),
    APPLICATION_ASSIGN_INTERVIEWERS(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(ASSIGNED_USERS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .addResolution(INTERVIEW_TIME_ZONE, NOT_NULL)
                    .addResolution(INTERVIEW_DURATION, NOT_NULL)
                    .addResolution(INTERVIEW_DATE_TIME)
                    .addResolution(APPOINTMENT_TIMESLOTS)
                    .addResolution(INTERVIEWER_INSTRUCTIONS)
                    .addResolution(INTERVIEWEE_INSTRUCTIONS)
                    .addResolution(INTERVIEW_LOCATION)
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_ASSIGN_REVIEWERS(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_ASSIGN_SUPERVISORS(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_COMMENT(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_COMPLETE(PrismActionType.USER_INVOCATION, PrismActionCategory.VIEW_EDIT_RESOURCE, false, true, PrismScope.APPLICATION, null, null, null),
    APPLICATION_COMPLETE_APPROVAL_STAGE(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, true, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_COMPLETE_INTERVIEW_STAGE(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, true, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_COMPLETE_REVIEW_STAGE(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, true, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_COMPLETE_VALIDATION_STAGE(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, true, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_CONFIRM_ELIGIBILITY(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_ASSESSMENT_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, false, PrismScope.APPLICATION, null, null,
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_CONFIRM_REJECTION(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, false, PrismScope.APPLICATION, null, null,
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_CONFIRM_SUPERVISION(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_CORRECT(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, false, PrismScope.APPLICATION, null, null, null),
    APPLICATION_EMAIL_CREATOR(PrismActionType.USER_INVOCATION, PrismActionCategory.EMAIL_RESOURCE_CREATOR, false, false, PrismScope.APPLICATION, null, null, null),
    APPLICATION_ESCALATE(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.ESCALATE_RESOURCE, false, true, PrismScope.APPLICATION, null, null, null),
    APPLICATION_EXPORT(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.EXPORT_RESOURCE, false, false, PrismScope.APPLICATION, null, null, null),
    APPLICATION_MOVE_TO_DIFFERENT_STAGE(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, true, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, true, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_PROVIDE_REFERENCE(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, true, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_PROVIDE_REVIEW(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, true, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_TERMINATE(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.PROPAGATE_RESOURCE, false, true, PrismScope.APPLICATION, null, null, null),
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)),
            PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY)
                    .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .setCustomValidator(new AssignInterviewersCommentCustomValidator())
                    .build()),
    APPLICATION_VIEW_EDIT(PrismActionType.USER_INVOCATION, PrismActionCategory.VIEW_EDIT_RESOURCE, false, false, PrismScope.APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(PrismRole.APPLICATION_CREATOR).withRedactionType(PrismRedactionType.ALL_CONTENT),
                    new PrismActionRedaction().withRole(PrismRole.APPLICATION_REFEREE).withRedactionType(PrismRedactionType.ALL_CONTENT)), null),
    APPLICATION_WITHDRAW(PrismActionType.USER_INVOCATION, PrismActionCategory.WITHDRAW_RESOURCE, false, true, PrismScope.APPLICATION, null, null, null),
    INSTITUTION_COMPLETE_APPROVAL_STAGE(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, true, PrismScope.INSTITUTION, null, null, null),
    INSTITUTION_VIEW_EDIT(PrismActionType.USER_INVOCATION, PrismActionCategory.VIEW_EDIT_RESOURCE, false, false, PrismScope.INSTITUTION, null, null, null),
    INSTITUTION_CORRECT(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, true, PrismScope.INSTITUTION, null, null, null),
    INSTITUTION_CREATE_PROGRAM(PrismActionType.USER_INVOCATION, PrismActionCategory.CREATE_RESOURCE, false, true, PrismScope.INSTITUTION, PrismScope.PROGRAM, null, null),
    INSTITUTION_EMAIL_CREATOR(PrismActionType.USER_INVOCATION, PrismActionCategory.EMAIL_RESOURCE_CREATOR, false, false, PrismScope.INSTITUTION, null, null, null),
    INSTITUTION_ESCALATE(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.ESCALATE_RESOURCE, false, true, PrismScope.INSTITUTION, null, null, null),
    INSTITUTION_IMPORT_PROGRAM(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.CREATE_RESOURCE, false, true, PrismScope.INSTITUTION, PrismScope.PROGRAM, null, null),
    INSTITUTION_WITHDRAW(PrismActionType.USER_INVOCATION, PrismActionCategory.WITHDRAW_RESOURCE, false, true, PrismScope.INSTITUTION, null, null, null),
    PROGRAM_COMPLETE_APPROVAL_STAGE(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, true, PrismScope.PROGRAM, null, null, null),
    PROGRAM_CONCLUDE(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.PROPAGATE_RESOURCE, false, true, PrismScope.PROGRAM, null, null, null),
    PROGRAM_VIEW_EDIT(PrismActionType.USER_INVOCATION, PrismActionCategory.VIEW_EDIT_RESOURCE, false, false, PrismScope.PROGRAM, null, null, null),
    PROGRAM_CORRECT(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, true, PrismScope.PROGRAM, null, null, null),
    PROGRAM_CREATE_APPLICATION(PrismActionType.USER_INVOCATION, PrismActionCategory.CREATE_RESOURCE, false, true, PrismScope.PROGRAM, PrismScope.APPLICATION, null, null),
    PROGRAM_CREATE_PROJECT(PrismActionType.USER_INVOCATION, PrismActionCategory.CREATE_RESOURCE, false, true, PrismScope.PROGRAM, PrismScope.PROJECT, null, null),
    PROGRAM_EMAIL_CREATOR(PrismActionType.USER_INVOCATION, PrismActionCategory.EMAIL_RESOURCE_CREATOR, false, false, PrismScope.PROGRAM, null, null, null),
    PROGRAM_ESCALATE(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.ESCALATE_RESOURCE, false, true, PrismScope.PROGRAM, null, null, null),
    PROGRAM_RESTORE(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.PROPAGATE_RESOURCE, false, true, PrismScope.PROGRAM, null, null, null),
    PROGRAM_WITHDRAW(PrismActionType.USER_INVOCATION, PrismActionCategory.WITHDRAW_RESOURCE, false, true, PrismScope.PROGRAM, null, null, null),
    PROJECT_COMPLETE_APPROVAL_STAGE(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, true, PrismScope.PROJECT, null, null, null),
    PROJECT_CONCLUDE(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.PROPAGATE_RESOURCE, false, true, PrismScope.PROJECT, null, null, null),
    PROJECT_VIEW_EDIT(PrismActionType.USER_INVOCATION, PrismActionCategory.VIEW_EDIT_RESOURCE, false, false, PrismScope.PROJECT, null, null, null),
    PROJECT_CORRECT(PrismActionType.USER_INVOCATION, PrismActionCategory.PROCESS_RESOURCE, false, true, PrismScope.PROJECT, null, null, null),
    PROJECT_CREATE_APPLICATION(PrismActionType.USER_INVOCATION, PrismActionCategory.CREATE_RESOURCE, false, true, PrismScope.PROJECT, PrismScope.APPLICATION, null, null),
    PROJECT_EMAIL_CREATOR(PrismActionType.USER_INVOCATION, PrismActionCategory.EMAIL_RESOURCE_CREATOR, false, false, PrismScope.PROJECT, null, null, null),
    PROJECT_ESCALATE(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.ESCALATE_RESOURCE, false, true, PrismScope.PROJECT, null, null, null),
    PROJECT_RESTORE(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.PROPAGATE_RESOURCE, false, true, PrismScope.PROJECT, null, null, null),
    PROJECT_SUSPEND(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.PROPAGATE_RESOURCE, false, true, PrismScope.PROJECT, null, null, null),
    PROJECT_TERMINATE(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.PROPAGATE_RESOURCE, false, true, PrismScope.PROJECT, null, null, null),
    PROJECT_WITHDRAW(PrismActionType.USER_INVOCATION, PrismActionCategory.WITHDRAW_RESOURCE, false, true, PrismScope.PROJECT, null, null, null),
    SYSTEM_VIEW_EDIT(PrismActionType.USER_INVOCATION, PrismActionCategory.VIEW_EDIT_RESOURCE, false, false, PrismScope.SYSTEM, null, null, null),
    SYSTEM_CREATE_INSTITUTION(PrismActionType.USER_INVOCATION, PrismActionCategory.CREATE_RESOURCE, false, true, PrismScope.SYSTEM, PrismScope.INSTITUTION, null, null),
    SYSTEM_MANAGE_ACCOUNT(PrismActionType.USER_INVOCATION, PrismActionCategory.MANAGE_ACCOUNT, false, false, PrismScope.SYSTEM, null, null, null),
    SYSTEM_STARTUP(PrismActionType.SYSTEM_INVOCATION, PrismActionCategory.INITIALISE_RESOURCE, false, false, PrismScope.SYSTEM, PrismScope.SYSTEM, null, null),
    SYSTEM_VIEW_APPLICATION_LIST(PrismActionType.USER_INVOCATION, PrismActionCategory.VIEW_RESOURCE_LIST, false, false, PrismScope.SYSTEM, null, null, null),
    SYSTEM_VIEW_INSTITUTION_LIST(PrismActionType.USER_INVOCATION, PrismActionCategory.VIEW_RESOURCE_LIST, false, false, PrismScope.SYSTEM, null, null, null),
    SYSTEM_VIEW_PROGRAM_LIST(PrismActionType.USER_INVOCATION, PrismActionCategory.VIEW_RESOURCE_LIST, false, false, PrismScope.SYSTEM, null, null, null),
    SYSTEM_VIEW_PROJECT_LIST(PrismActionType.USER_INVOCATION, PrismActionCategory.VIEW_RESOURCE_LIST, false, false, PrismScope.SYSTEM, null, null, null);

    private PrismActionType actionType;

    private PrismActionCategory actionCategory;

    private boolean ratingAction;

    private boolean transitionAction;

    private PrismScope scope;

    private PrismScope creationScope;

    private List<PrismActionRedaction> redactions;

    private PrismActionValidationDefinition validationDefinition;

    private static final List<PrismAction> creationActions = Lists.newArrayList();

    private static final HashMap<PrismAction, PrismAction> fallbackActions = Maps.newHashMap();

    static {
        for (PrismAction action : PrismAction.values()) {
            if (action.getCreationScope() != null) {
                creationActions.add(action);
            }
        }

        for (PrismAction action : PrismAction.values()) {
            PrismScope scope = action.getScope();
            PrismScope creationScope = action.getCreationScope();
            switch (creationScope == null ? scope : creationScope) {
                case SYSTEM:
                case APPLICATION:
                    fallbackActions.put(action, PrismAction.SYSTEM_VIEW_APPLICATION_LIST);
                    break;
                case INSTITUTION:
                    fallbackActions.put(action, PrismAction.SYSTEM_VIEW_INSTITUTION_LIST);
                    break;
                case PROGRAM:
                    fallbackActions.put(action, PrismAction.SYSTEM_VIEW_PROGRAM_LIST);
                    break;
                case PROJECT:
                    fallbackActions.put(action, PrismAction.SYSTEM_VIEW_PROJECT_LIST);
                    break;
            }
        }
    }

    private PrismAction(PrismActionType actionType, PrismActionCategory actionCategory, boolean ratingAction, boolean transitionAction, PrismScope scope,
                        PrismScope creationScope, List<PrismActionRedaction> redactions, PrismActionValidationDefinition validationDefinition) {
        this.actionType = actionType;
        this.actionCategory = actionCategory;
        this.ratingAction = ratingAction;
        this.transitionAction = transitionAction;
        this.scope = scope;
        this.creationScope = creationScope;
        this.redactions = redactions == null ? Collections.<PrismActionRedaction>emptyList() : redactions;
        this.validationDefinition = validationDefinition;
    }

    public PrismActionType getActionType() {
        return actionType;
    }

    public PrismActionCategory getActionCategory() {
        return actionCategory;
    }

    public final boolean isRatingAction() {
        return ratingAction;
    }

    public final void setRatingAction(boolean ratingAction) {
        this.ratingAction = ratingAction;
    }

    public final boolean isTransitionAction() {
        return transitionAction;
    }

    public final void setTransitionAction(boolean transitionAction) {
        this.transitionAction = transitionAction;
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

    public PrismActionValidationDefinition getValidationDefinition() {
        return validationDefinition;
    }

    public static List<PrismAction> getCreationActions() {
        return creationActions;
    }

    public static PrismAction getFallBackAction(PrismAction action) {
        return fallbackActions.get(action);
    }

}
