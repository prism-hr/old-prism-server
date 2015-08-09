package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_INTERVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_REVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_SUPERVISORS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_ELIGIBILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_REJECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PURGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_RESERVE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_TERMINATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_TERMINATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_TERMINATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_INSTITUTION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED_PURGED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED_RETAINED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_COMPLETED_PURGED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_COMPLETED_RETAINED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_PENDING_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_RESERVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_UNSUBMITTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VERIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_PURGED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_RETAINED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_PURGED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_RETAINED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVAL_PENDING_CORRECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_COMPLETED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_CONFIRMED_SUPERVISION_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_ESCALATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_EXPORTED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PROVIDED_REVIEW_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PURGED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_REJECTED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_WITHDRAWN_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.DEPARTMENT_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.DEPARTMENT_CREATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.INSTITUTION_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.INSTITUTION_CREATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.INSTITUTION_UPDATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_CREATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_UPDATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_CREATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_UPDATED_OUTCOME;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public enum PrismStateTransitionGroup {

    APPLICATION_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_UNSUBMITTED) //
                    .withTransitionAction(APPLICATION_COMPLETE)), //

    APPLICATION_COMPLETE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_VALIDATION) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withTransitionEvaluation(APPLICATION_COMPLETED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_VALIDATION_PENDING_COMPLETION) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withTransitionEvaluation(APPLICATION_COMPLETED_OUTCOME)),

    APPLICATION_COMPLETE_STATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_VERIFICATION) //
                    .withTransitionAction(APPLICATION_CONFIRM_ELIGIBILITY) //
                    .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REFERENCE) //
                    .withTransitionAction(APPLICATION_PROVIDE_REFERENCE) //
                    .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME) //
                    .withRoleTransitions(APPLICATION_CREATE_REFEREE_GROUP), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REVIEW) //
                    .withTransitionAction(APPLICATION_ASSIGN_REVIEWERS) //
                    .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW) //
                    .withTransitionAction(APPLICATION_ASSIGN_INTERVIEWERS) //
                    .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVAL) //
                    .withTransitionAction(APPLICATION_ASSIGN_SUPERVISORS) //
                    .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVED) //
                    .withTransitionAction(APPLICATION_CONFIRM_OFFER_RECOMMENDATION) //
                    .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_RESERVED) //
                    .withTransitionAction(APPLICATION_RESERVE) //
                    .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED) //
                    .withTransitionAction(APPLICATION_CONFIRM_REJECTION) //
                    .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME)), //

    APPLICATION_WITHDRAW_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_WITHDRAWN_PENDING_EXPORT) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withTransitionEvaluation(APPLICATION_WITHDRAWN_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withTransitionEvaluation(APPLICATION_WITHDRAWN_OUTCOME)), //

    APPLICATION_ESCALATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_PENDING_EXPORT) //
                    .withTransitionAction(APPLICATION_ESCALATE) //
                    .withTransitionEvaluation(APPLICATION_ESCALATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_COMPLETED) //
                    .withTransitionAction(APPLICATION_ESCALATE) //
                    .withTransitionEvaluation(APPLICATION_ESCALATED_OUTCOME)), //

    APPLICATION_PROVIDE_REVIEW_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REVIEW_PENDING_FEEDBACK) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withTransitionEvaluation(APPLICATION_PROVIDED_REVIEW_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REVIEW_PENDING_COMPLETION) //
                    .withTransitionAction(PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE) //
                    .withTransitionEvaluation(APPLICATION_PROVIDED_REVIEW_OUTCOME)),

    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_AVAILABILITY) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withTransitionEvaluation(APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_SCHEDULING) //
                    .withTransitionAction(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
                    .withTransitionEvaluation(APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME)), //

    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_FEEDBACK) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withTransitionEvaluation(APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_COMPLETION) //
                    .withTransitionAction(PrismAction.APPLICATION_COMPLETE_INTERVIEW_STAGE) //
                    .withTransitionEvaluation(APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME)), //

    APPLICATION_CONFIRM_SUPERVISION_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVAL_PENDING_COMPLETION) //
                    .withTransitionAction(PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE) //
                    .withTransitionEvaluation(APPLICATION_CONFIRMED_SUPERVISION_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVAL_PENDING_FEEDBACK) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withTransitionEvaluation(APPLICATION_CONFIRMED_SUPERVISION_OUTCOME)), //

    APPLICATION_CONFIRM_OFFER_RECOMMENDATION_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVED_COMPLETED) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withTransitionEvaluation(APPLICATION_APPROVED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVED_PENDING_EXPORT) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withTransitionEvaluation(APPLICATION_APPROVED_OUTCOME)), //

    APPLICATION_APPROVED_EXPORT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVED_COMPLETED) //
                    .withTransitionAction(APPLICATION_EXPORT) //
                    .withTransitionEvaluation(APPLICATION_EXPORTED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVED_PENDING_CORRECTION) //
                    .withTransitionAction(APPLICATION_EXPORT) //
                    .withTransitionEvaluation(APPLICATION_EXPORTED_OUTCOME)),

    APPLICATION_CONFIRM_REJECTION_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_COMPLETED) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withTransitionEvaluation(APPLICATION_REJECTED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_PENDING_EXPORT) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withTransitionEvaluation(APPLICATION_REJECTED_OUTCOME)), //

    APPLICATION_REJECTED_EXPORT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_COMPLETED) //
                    .withTransitionAction(APPLICATION_EXPORT) //
                    .withTransitionEvaluation(APPLICATION_EXPORTED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_PENDING_CORRECTION) //
                    .withTransitionAction(APPLICATION_EXPORT) //
                    .withTransitionEvaluation(APPLICATION_EXPORTED_OUTCOME)),

    APPLICATION_WITHDRAWN_EXPORT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED) //
                    .withTransitionAction(APPLICATION_EXPORT) //
                    .withTransitionEvaluation(APPLICATION_EXPORTED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_WITHDRAWN_PENDING_CORRECTION) //
                    .withTransitionAction(APPLICATION_EXPORT) //
                    .withTransitionEvaluation(APPLICATION_EXPORTED_OUTCOME)),

    APPLICATION_APPROVED_PURGED_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVED_COMPLETED_RETAINED) //
                    .withTransitionAction(APPLICATION_PURGE) //
                    .withTransitionEvaluation(APPLICATION_PURGED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVED_COMPLETED_PURGED) //
                    .withTransitionAction(APPLICATION_PURGE) //
                    .withTransitionEvaluation(APPLICATION_PURGED_OUTCOME)), //

    APPLICATION_REJECTED_PURGED_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_COMPLETED_RETAINED) //
                    .withTransitionAction(APPLICATION_PURGE) //
                    .withTransitionEvaluation(APPLICATION_PURGED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_COMPLETED_PURGED) //
                    .withTransitionAction(APPLICATION_PURGE) //
                    .withTransitionEvaluation(APPLICATION_PURGED_OUTCOME)), //

    APPLICATION_WITHDRAWN_PURGED_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED_RETAINED) //
                    .withTransitionAction(APPLICATION_PURGE) //
                    .withTransitionEvaluation(APPLICATION_PURGED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED_PURGED) //
                    .withTransitionAction(APPLICATION_PURGE) //
                    .withTransitionEvaluation(APPLICATION_PURGED_OUTCOME)), //

    APPLICATION_WITHDRAWN_UNSUBMITTED_PURGED_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_RETAINED) //
                    .withTransitionAction(APPLICATION_PURGE) //
                    .withTransitionEvaluation(APPLICATION_PURGED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_PURGED) //
                    .withTransitionAction(APPLICATION_PURGE) //
                    .withTransitionEvaluation(APPLICATION_PURGED_OUTCOME)), //

    PROJECT_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
                    .withTransitionEvaluation(PROJECT_CREATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVED) //
                    .withTransitionAction(PROJECT_VIEW_EDIT) //
                    .withTransitionEvaluation(PROJECT_CREATED_OUTCOME)), //

    PROJECT_APPROVE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVED) //
                    .withTransitionAction(PROJECT_VIEW_EDIT) //
                    .withTransitionEvaluation(PROJECT_APPROVED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_REJECTED) //
                    .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
                    .withTransitionEvaluation(PROJECT_APPROVED_OUTCOME)), //

    PROJECT_VIEW_EDIT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVED) //
                    .withTransitionAction(PROJECT_VIEW_EDIT) //
                    .withTransitionEvaluation(PROJECT_UPDATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PrismState.PROJECT_DISABLED_COMPLETED) //
                    .withTransitionAction(PROJECT_VIEW_EDIT) //
                    .withTransitionEvaluation(PROJECT_UPDATED_OUTCOME)), //

    PROJECT_RESTORE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVAL) //
                    .withTransitionAction(PROJECT_VIEW_EDIT),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVAL_PENDING_CORRECTION) //
                    .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT)),

    PROGRAM_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
                    .withTransitionEvaluation(PROGRAM_CREATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVED) //
                    .withTransitionAction(PROGRAM_VIEW_EDIT) //
                    .withTransitionEvaluation(PROGRAM_CREATED_OUTCOME)), //

    PROGRAM_APPROVE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVED) //
                    .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
                    .withTransitionEvaluation(PROGRAM_APPROVED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_REJECTED) //
                    .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
                    .withTransitionEvaluation(PROGRAM_APPROVED_OUTCOME)), //

    PROGRAM_VIEW_EDIT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVED) //
                    .withTransitionAction(PROGRAM_VIEW_EDIT) //
                    .withTransitionEvaluation(PROGRAM_UPDATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_DISABLED_COMPLETED) //
                    .withTransitionAction(PROGRAM_VIEW_EDIT) //
                    .withTransitionEvaluation(PROGRAM_UPDATED_OUTCOME) //
                    .withPropagatedActions(PROJECT_TERMINATE)), //

    DEPARTMENT_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withTransitionEvaluation(DEPARTMENT_CREATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVED) //
                    .withTransitionAction(DEPARTMENT_VIEW_EDIT) //
                    .withTransitionEvaluation(DEPARTMENT_CREATED_OUTCOME)), //

    DEPARTMENT_APPROVE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVED) //
                    .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withTransitionEvaluation(DEPARTMENT_APPROVED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_REJECTED) //
                    .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withTransitionEvaluation(DEPARTMENT_APPROVED_OUTCOME)), //

    DEPARTMENT_VIEW_EDIT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVED) //
                    .withTransitionAction(DEPARTMENT_VIEW_EDIT) //
                    .withTransitionEvaluation(PROGRAM_UPDATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_DISABLED_COMPLETED) //
                    .withTransitionAction(DEPARTMENT_VIEW_EDIT) //
                    .withTransitionEvaluation(PROGRAM_UPDATED_OUTCOME) //
                    .withPropagatedActions(PROGRAM_TERMINATE, PROJECT_TERMINATE)), //

    INSTITUTION_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST) //
                    .withTransitionEvaluation(INSTITUTION_CREATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVED) //
                    .withTransitionAction(INSTITUTION_VIEW_EDIT) //
                    .withTransitionEvaluation(INSTITUTION_CREATED_OUTCOME)), //

    INSTITUTION_APPROVE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVED) //
                    .withTransitionAction(INSTITUTION_VIEW_EDIT) //
                    .withTransitionEvaluation(INSTITUTION_APPROVED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_REJECTED) //
                    .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST)
                    .withTransitionEvaluation(INSTITUTION_APPROVED_OUTCOME)),

    INSTITUTION_VIEW_EDIT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVED) //
                    .withTransitionAction(INSTITUTION_VIEW_EDIT) //
                    .withTransitionEvaluation(INSTITUTION_UPDATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_DISABLED_COMPLETED) //
                    .withTransitionAction(INSTITUTION_VIEW_EDIT) //
                    .withTransitionEvaluation(INSTITUTION_UPDATED_OUTCOME) //
                    .withPropagatedActions(DEPARTMENT_TERMINATE, PROGRAM_TERMINATE, PROJECT_TERMINATE));

    private PrismStateTransition[] stateTransitionTemplates;

    private PrismStateTransitionGroup(PrismStateTransition... stateTransitions) {
        this.stateTransitionTemplates = stateTransitions;
    }

    public PrismStateTransition[] getStateTransitions() {
        return stateTransitionTemplates;
    }

    public PrismStateTransition[] withRoleTransitions(PrismRoleTransitionGroup... roleTransitionGroups) {
        List<PrismRoleTransition> roleTransitions = Lists.newLinkedList();
        for (PrismRoleTransitionGroup roleTransitionGroup : roleTransitionGroups) {
            roleTransitions.addAll(Lists.newArrayList(roleTransitionGroup.getRoleTransitions()));
        }
        return withRoleTransitionsAndStateTerminations(roleTransitions, Collections.<PrismStateTermination> emptyList());
    }

    public PrismStateTransition[] withRoleTransitionsAndStateTerminations(PrismStateTerminationGroup stateTerminationGroup,
            PrismRoleTransitionGroup... roleTransitionGroups) {
        List<PrismRoleTransition> roleTransitions = Lists.newLinkedList();
        for (PrismRoleTransitionGroup roleTransitionGroup : roleTransitionGroups) {
            roleTransitions.addAll(Lists.newArrayList(roleTransitionGroup.getRoleTransitions()));
        }
        return withRoleTransitionsAndStateTerminations(roleTransitions, Lists.newArrayList(stateTerminationGroup.getStateTerminations()));
    }

    public PrismStateTransition[] withRoleTransitionsAndExclusions(List<PrismState> exclusions, PrismRoleTransitionGroup... roleTransitionGroups) {
        List<PrismRoleTransition> roleTransitions = Lists.newLinkedList();
        for (PrismRoleTransitionGroup roleTransitionGroup : roleTransitionGroups) {
            roleTransitions.addAll(Lists.newArrayList(roleTransitionGroup.getRoleTransitions()));
        }
        return withRoleTransitionsAndStateTerminations(roleTransitions, Collections.<PrismStateTermination> emptyList(),
                exclusions.toArray(new PrismState[exclusions.size()]));
    }

    public PrismStateTransition[] withExclusions(List<PrismState> exclusions) {
        return withRoleTransitionsAndStateTerminations(Collections.<PrismRoleTransition> emptyList(), Collections.<PrismStateTermination> emptyList(),
                exclusions.toArray(new PrismState[exclusions.size()]));
    }

    private PrismStateTransition[] withRoleTransitionsAndStateTerminations(List<PrismRoleTransition> roleTransitions,
            List<PrismStateTermination> stateTerminations, PrismState... exclusions) {
        List<PrismState> exclusionsAsList = Arrays.asList(exclusions);
        List<PrismStateTransition> stateTransitions = Lists.newLinkedList();
        for (PrismStateTransition stateTransition : getStateTransitions()) {
            PrismState transitionState = stateTransition.getTransitionState();
            if (!exclusionsAsList.contains(transitionState)) {
                List<PrismRoleTransition> definedRoleTransitions = stateTransition.getRoleTransitions();
                List<PrismStateTermination> definedStateTerminations = stateTransition.getStateTerminations();
                stateTransitions.add(new PrismStateTransition() //
                        .withTransitionState(stateTransition.getTransitionState()) //
                        .withTransitionAction(stateTransition.getTransitionAction()) //
                        .withTransitionEvaluation(stateTransition.getTransitionEvaluation()) //
                        .withRoleTransitions(definedRoleTransitions.toArray(new PrismRoleTransition[definedRoleTransitions.size()])) //
                        .withRoleTransitions(roleTransitions.toArray(new PrismRoleTransition[roleTransitions.size()])) //
                        .withStateTerminations(definedStateTerminations.toArray(new PrismStateTermination[definedStateTerminations.size()])) //
                        .withStateTerminations(stateTerminations.toArray(new PrismStateTermination[stateTerminations.size()])));
            }
        }
        return stateTransitions.toArray(new PrismStateTransition[stateTransitions.size()]);
    }
}
