package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.EMAIL_RESOURCE_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.ESCALATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.EXPORT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.INITIALISE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.MANAGE_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.PROCESS_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.PROPAGATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.PURGE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_EDIT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_RESOURCE_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.WITHDRAW_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.APPLICATION_ELIGIBLE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.APPLICATION_INTERESTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.APPLICATION_RATING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.APPOINTMENT_CONDITIONS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.APPOINTMENT_PREFERENCES;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.APPOINTMENT_TIMESLOTS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.ASSIGNED_USERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.CONTENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.DOCUMENTS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.INTERVIEWEE_INSTRUCTIONS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.INTERVIEWER_INSTRUCTIONS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.INTERVIEW_DATE_TIME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.INTERVIEW_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.INTERVIEW_LOCATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.INTERVIEW_TIME_ZONE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.POSITION_DESCRIPTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.POSITION_PROVISIONAL_START_DATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.POSITION_TITLE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.RECRUITER_ACCEPT_APPOINTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.REJECTION_REASON;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.TRANSITION_STATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition.APPLICATION_COMPLETE_CUSTOM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_CUSTOM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition.APPLICATION_PROVIDE_REFERENCE_CUSTOM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition.APPLICATION_PROVIDE_REVIEW_CUSTOM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType.ALL_ASSESSMENT_CONTENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType.ALL_CONTENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType.SYSTEM_INVOCATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType.USER_INVOCATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionValidationFieldRestriction.NOT_EMPTY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionValidationFieldRestriction.NOT_NULL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionValidationFieldRestriction.SIZE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.rest.validation.validator.comment.CommentAssignInterviewersCustomValidator;
import com.zuehlke.pgadmissions.rest.validation.validator.comment.CommentConfirmSupervisionCustomValidator;

public enum PrismAction {

    APPLICATION_ASSIGN_INTERVIEWERS(USER_INVOCATION, PROCESS_RESOURCE, false, false, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT),
            new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder()
            .addResolution(ASSIGNED_USERS).addResolution(INTERVIEW_TIME_ZONE, NOT_NULL)
            .addResolution(INTERVIEW_DURATION, NOT_NULL).addResolution(INTERVIEW_DATE_TIME).addResolution(APPOINTMENT_TIMESLOTS)
            .addResolution(INTERVIEWER_INSTRUCTIONS).addResolution(INTERVIEWEE_INSTRUCTIONS).addResolution(INTERVIEW_LOCATION)
            .setCustomValidator(new CommentAssignInterviewersCustomValidator()).build()), //
    APPLICATION_ASSIGN_REVIEWERS(USER_INVOCATION, PROCESS_RESOURCE, false, false, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder()
            .addResolution(ASSIGNED_USERS).build()), //
    APPLICATION_ASSIGN_SUPERVISORS(USER_INVOCATION, PROCESS_RESOURCE, false, false, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder()
            .addResolution(ASSIGNED_USERS).addResolution(POSITION_TITLE)
            .addResolution(POSITION_DESCRIPTION).addResolution(POSITION_PROVISIONAL_START_DATE).addResolution(APPOINTMENT_CONDITIONS).build()), //
    APPLICATION_COMMENT(USER_INVOCATION, PROCESS_RESOURCE, false, false, false, true, false, null, APPLICATION, null, Arrays.asList(new PrismActionRedaction()
            .withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT),
            new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder()
            .addResolution(CONTENT, NOT_EMPTY).addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0)).build()), //
    APPLICATION_COMPLETE(USER_INVOCATION, VIEW_EDIT_RESOURCE, false, true, false, true, false, APPLICATION_COMPLETE_CUSTOM, APPLICATION, null, null, null), //
    APPLICATION_COMPLETE_APPROVAL_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, true, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder().addResolution(CONTENT, NOT_EMPTY)
            .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0)).addResolution(TRANSITION_STATE, NOT_NULL).build()), //
    APPLICATION_COMPLETE_INTERVIEW_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, true, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder().addResolution(CONTENT, NOT_EMPTY)
            .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0)).addResolution(TRANSITION_STATE, NOT_NULL).build()), //
    APPLICATION_COMPLETE_REFERENCE_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, true, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder().addResolution(TRANSITION_STATE)
            .addResolution(CONTENT, NOT_EMPTY).addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0)).build()), //
    APPLICATION_COMPLETE_REVIEW_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, true, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder().addResolution(CONTENT, NOT_EMPTY)
            .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0)).addResolution(TRANSITION_STATE, NOT_NULL).build()), //
    APPLICATION_COMPLETE_VALIDATION_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, true, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder().addResolution(CONTENT, NOT_EMPTY)
            .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0)).addResolution(TRANSITION_STATE, NOT_NULL).build()), //
    APPLICATION_COMPLETE_VERIFICATION_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, true, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder().addResolution(CONTENT, NOT_EMPTY)
            .addResolution(TRANSITION_STATE).addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0)).build()), //
    APPLICATION_CONFIRM_ELIGIBILITY(USER_INVOCATION, PROCESS_RESOURCE, false, false, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder().addResolution(CONTENT, NOT_EMPTY)
            .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0)).addResolution(APPLICATION_ELIGIBLE, NOT_NULL).build()), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS(USER_INVOCATION, PROCESS_RESOURCE, false, false, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT),
            new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder()
            .addResolution(INTERVIEW_DATE_TIME, NOT_NULL).addResolution(INTERVIEWER_INSTRUCTIONS, NOT_EMPTY).addResolution(INTERVIEWEE_INSTRUCTIONS)
            .addResolution(INTERVIEW_LOCATION).build()), //
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION(USER_INVOCATION, PROCESS_RESOURCE, false, false, false, true, false, null, APPLICATION, null, null,
            PrismActionValidationDefinition.builder().addResolution(ASSIGNED_USERS)
                    .addResolution(POSITION_TITLE).addResolution(POSITION_DESCRIPTION).addResolution(POSITION_PROVISIONAL_START_DATE)
                    .addResolution(APPOINTMENT_CONDITIONS).build()), //
    APPLICATION_CONFIRM_REJECTION(USER_INVOCATION, PROCESS_RESOURCE, false, false, false, true, false, null, APPLICATION, null, null,
            PrismActionValidationDefinition.builder().addResolution(REJECTION_REASON, NOT_NULL).build()), //
    APPLICATION_CONFIRM_PRIMARY_SUPERVISION(USER_INVOCATION, PROCESS_RESOURCE, false, false, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder().addResolution(RECRUITER_ACCEPT_APPOINTMENT, NOT_NULL)
            .addResolution(CONTENT).addResolution(ASSIGNED_USERS).addResolution(POSITION_TITLE).addResolution(POSITION_DESCRIPTION)
            .addResolution(POSITION_PROVISIONAL_START_DATE).addResolution(APPOINTMENT_CONDITIONS)
            .setCustomValidator(new CommentConfirmSupervisionCustomValidator()).build()), //
    APPLICATION_CONFIRM_SECONDARY_SUPERVISION(USER_INVOCATION, PROCESS_RESOURCE, false, false, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder().addResolution(RECRUITER_ACCEPT_APPOINTMENT, NOT_NULL)
            .setCustomValidator(new CommentConfirmSupervisionCustomValidator()).build()), //
    APPLICATION_CORRECT(USER_INVOCATION, PROCESS_RESOURCE, false, false, false, true, false, null, APPLICATION, null, null, null), //
    APPLICATION_EMAIL_CREATOR(USER_INVOCATION, EMAIL_RESOURCE_CREATOR, false, false, false, false, false, null, APPLICATION, null, null, null), //
    APPLICATION_ESCALATE(SYSTEM_INVOCATION, ESCALATE_RESOURCE, false, false, false, false, false, null, APPLICATION, null, null, null), //
    APPLICATION_EXPORT(SYSTEM_INVOCATION, EXPORT_RESOURCE, false, false, false, true, false, null, APPLICATION, null, null, null), //
    APPLICATION_MOVE_TO_DIFFERENT_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, true, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder().addResolution(CONTENT, NOT_EMPTY)
            .addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0)).addResolution(TRANSITION_STATE, NOT_NULL).build()), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY(USER_INVOCATION, PROCESS_RESOURCE, false, false, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder()
            .addResolution(APPOINTMENT_PREFERENCES, new PrismActionValidationFieldResolution(SIZE, "min", 0)).build()), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK(USER_INVOCATION, PROCESS_RESOURCE, true, false, false, true, false, APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_CUSTOM,
            APPLICATION, null, Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT),
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY).addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
                    .addResolution(APPLICATION_RATING, NOT_NULL).addResolution(APPLICATION_INTERESTED, NOT_NULL).build()), //
    APPLICATION_PROVIDE_REFERENCE(USER_INVOCATION, PROCESS_RESOURCE, true, false, true, true, true, APPLICATION_PROVIDE_REFERENCE_CUSTOM, APPLICATION, null,
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT),
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder()
                    .addResolution(CONTENT, NOT_EMPTY).addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "max", 1))
                    .addResolution(APPLICATION_RATING, NOT_NULL).build()), //
    APPLICATION_PROVIDE_REVIEW(USER_INVOCATION, PROCESS_RESOURCE, true, false, false, true, false, APPLICATION_PROVIDE_REVIEW_CUSTOM, APPLICATION, null, Arrays
            .asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT),
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder()
            .addResolution(CONTENT, NOT_EMPTY).addResolution(DOCUMENTS, new PrismActionValidationFieldResolution(SIZE, "min", 0))
            .addResolution(APPLICATION_RATING, NOT_NULL).addResolution(APPLICATION_INTERESTED, NOT_NULL).build()), //
    APPLICATION_PURGE(SYSTEM_INVOCATION, PURGE_RESOURCE, false, false, false, true, false, null, APPLICATION, null, null, null), //
    APPLICATION_TERMINATE(SYSTEM_INVOCATION, PROPAGATE_RESOURCE, false, true, false, true, false, null, APPLICATION, null, null, null), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY(USER_INVOCATION, PROCESS_RESOURCE, false, false, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), PrismActionValidationDefinition.builder()
            .addResolution(APPOINTMENT_PREFERENCES, new PrismActionValidationFieldResolution(SIZE, "min", 0)).build()), //
    APPLICATION_VIEW_EDIT(USER_INVOCATION, VIEW_EDIT_RESOURCE, false, false, false, true, false, null, APPLICATION, null, Arrays.asList(
            new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT), new PrismActionRedaction().withRole(APPLICATION_REFEREE)
                    .withRedactionType(ALL_CONTENT)), null), //
    APPLICATION_WITHDRAW(USER_INVOCATION, WITHDRAW_RESOURCE, false, true, false, true, false, null, APPLICATION, null, null, null), //
    INSTITUTION_COMPLETE_APPROVAL_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, true, false, true, false, null, INSTITUTION, null, null, null), //
    INSTITUTION_STARTUP(SYSTEM_INVOCATION, INITIALISE_RESOURCE, false, false, false, true, false, null, INSTITUTION, null, null, null), //
    INSTITUTION_VIEW_EDIT(USER_INVOCATION, VIEW_EDIT_RESOURCE, false, false, false, false, false, null, INSTITUTION, null, null, null), //
    INSTITUTION_CORRECT(USER_INVOCATION, PROCESS_RESOURCE, false, true, false, true, false, null, INSTITUTION, null, null, null), //
    INSTITUTION_CREATE_PROGRAM(USER_INVOCATION, CREATE_RESOURCE, false, true, false, true, false, null, INSTITUTION, PROGRAM, null, null), //
    INSTITUTION_EMAIL_CREATOR(USER_INVOCATION, EMAIL_RESOURCE_CREATOR, false, false, false, false, false, null, INSTITUTION, null, null, null), //
    INSTITUTION_ESCALATE(SYSTEM_INVOCATION, ESCALATE_RESOURCE, false, false, false, false, false, null, INSTITUTION, null, null, null), //
    INSTITUTION_IMPORT_PROGRAM(SYSTEM_INVOCATION, CREATE_RESOURCE, false, true, false, true, false, null, INSTITUTION, PROGRAM, null, null), //
    INSTITUTION_WITHDRAW(USER_INVOCATION, WITHDRAW_RESOURCE, false, true, false, true, false, null, INSTITUTION, null, null, null), //
    PROGRAM_COMPLETE_APPROVAL_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, true, false, true, false, null, PROGRAM, null, null, null), //
    PROGRAM_CONCLUDE(SYSTEM_INVOCATION, PROPAGATE_RESOURCE, false, true, false, true, false, null, PROGRAM, null, null, null), //
    PROGRAM_VIEW_EDIT(USER_INVOCATION, VIEW_EDIT_RESOURCE, false, false, false, false, false, null, PROGRAM, null, null, null), //
    PROGRAM_CORRECT(USER_INVOCATION, PROCESS_RESOURCE, false, true, false, true, false, null, PROGRAM, null, null, null), //
    PROGRAM_CREATE_APPLICATION(USER_INVOCATION, CREATE_RESOURCE, false, true, false, true, false, null, PROGRAM, APPLICATION, null, null), //
    PROGRAM_CREATE_PROJECT(USER_INVOCATION, CREATE_RESOURCE, false, true, false, true, false, null, PROGRAM, PROJECT, null, null), //
    PROGRAM_EMAIL_CREATOR(USER_INVOCATION, EMAIL_RESOURCE_CREATOR, false, false, false, false, false, null, PROGRAM, null, null, null), //
    PROGRAM_ESCALATE(SYSTEM_INVOCATION, ESCALATE_RESOURCE, false, false, false, false, false, null, PROGRAM, null, null, null), //
    PROGRAM_RESTORE(SYSTEM_INVOCATION, PROPAGATE_RESOURCE, false, true, false, true, false, null, PROGRAM, null, null, null), //
    PROGRAM_WITHDRAW(USER_INVOCATION, WITHDRAW_RESOURCE, false, true, false, true, false, null, PROGRAM, null, null, null), //
    PROJECT_COMPLETE_APPROVAL_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, true, false, true, false, null, PROJECT, null, null, null), //
    PROJECT_CONCLUDE(SYSTEM_INVOCATION, PROPAGATE_RESOURCE, false, true, false, true, false, null, PROJECT, null, null, null), //
    PROJECT_VIEW_EDIT(USER_INVOCATION, VIEW_EDIT_RESOURCE, false, false, false, false, false, null, PROJECT, null, null, null), //
    PROJECT_CORRECT(USER_INVOCATION, PROCESS_RESOURCE, false, true, false, true, false, null, PROJECT, null, null, null), //
    PROJECT_CREATE_APPLICATION(USER_INVOCATION, CREATE_RESOURCE, false, true, false, true, false, null, PROJECT, APPLICATION, null, null), //
    PROJECT_EMAIL_CREATOR(USER_INVOCATION, EMAIL_RESOURCE_CREATOR, false, false, false, false, false, null, PROJECT, null, null, null), //
    PROJECT_ESCALATE(SYSTEM_INVOCATION, ESCALATE_RESOURCE, false, false, false, false, false, null, PROJECT, null, null, null), //
    PROJECT_RESTORE(SYSTEM_INVOCATION, PROPAGATE_RESOURCE, false, true, false, true, false, null, PROJECT, null, null, null), //
    PROJECT_SUSPEND(SYSTEM_INVOCATION, PROPAGATE_RESOURCE, false, true, false, true, false, null, PROJECT, null, null, null), //
    PROJECT_TERMINATE(SYSTEM_INVOCATION, PROPAGATE_RESOURCE, false, true, false, true, false, null, PROJECT, null, null, null), //
    PROJECT_WITHDRAW(USER_INVOCATION, WITHDRAW_RESOURCE, false, true, false, true, false, null, PROJECT, null, null, null), //
    SYSTEM_VIEW_EDIT(USER_INVOCATION, VIEW_EDIT_RESOURCE, false, false, false, false, false, null, SYSTEM, null, null, null), //
    SYSTEM_CREATE_INSTITUTION(USER_INVOCATION, CREATE_RESOURCE, false, true, false, true, false, null, SYSTEM, INSTITUTION, null, null), //
    SYSTEM_STARTUP(SYSTEM_INVOCATION, INITIALISE_RESOURCE, false, false, false, true, false, null, SYSTEM, SYSTEM, null, null), //
    SYSTEM_MANAGE_ACCOUNT(USER_INVOCATION, MANAGE_ACCOUNT, false, false, false, false, false, null, SYSTEM, null, null, null), //
    SYSTEM_VIEW_APPLICATION_LIST(USER_INVOCATION, VIEW_RESOURCE_LIST, false, false, false, false, false, null, SYSTEM, null, null, null), //
    SYSTEM_VIEW_INSTITUTION_LIST(USER_INVOCATION, VIEW_RESOURCE_LIST, false, false, false, false, false, null, SYSTEM, null, null, null), //
    SYSTEM_VIEW_PROGRAM_LIST(USER_INVOCATION, VIEW_RESOURCE_LIST, false, false, false, false, false, null, SYSTEM, null, null, null), //
    SYSTEM_VIEW_PROJECT_LIST(USER_INVOCATION, VIEW_RESOURCE_LIST, false, false, false, false, false, null, SYSTEM, null, null, null);

    private PrismActionType actionType;

    private PrismActionCategory actionCategory;

    private boolean ratingAction;

    private boolean transitionAction;

    private boolean declinableAction;

    private boolean visibleAction;

    private boolean emphasizedAction;

    private PrismActionCustomQuestionDefinition actionCustomQuestion;

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

    private PrismAction(PrismActionType actionType, PrismActionCategory actionCategory, boolean ratingAction, boolean transitionAction,
            boolean declinableAction, boolean visibleAction, boolean emphasizedAction, PrismActionCustomQuestionDefinition actionCustomQuestion,
            PrismScope scope, PrismScope creationScope, List<PrismActionRedaction> redactions, PrismActionValidationDefinition validationDefinition) {
        this.actionType = actionType;
        this.actionCategory = actionCategory;
        this.ratingAction = ratingAction;
        this.transitionAction = transitionAction;
        this.declinableAction = declinableAction;
        this.visibleAction = visibleAction;
        this.emphasizedAction = emphasizedAction;
        this.actionCustomQuestion = actionCustomQuestion;
        this.scope = scope;
        this.creationScope = creationScope;
        this.redactions = redactions;
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

    public final boolean isTransitionAction() {
        return transitionAction;
    }

    public final boolean isDeclinableAction() {
        return declinableAction;
    }

    public final boolean isVisibleAction() {
        return visibleAction;
    }

    public final boolean isEmphasizedAction() {
        return emphasizedAction;
    }

    public final PrismActionCustomQuestionDefinition getActionCustomQuestion() {
        return actionCustomQuestion;
    }

    public PrismScope getScope() {
        return scope;
    }

    public PrismScope getCreationScope() {
        return creationScope;
    }

    public List<PrismActionRedaction> getRedactions() {
        return redactions == null ? Collections.<PrismActionRedaction> emptyList() : redactions;
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
