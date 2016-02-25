package uk.co.alumeni.prism.domain.definitions.workflow;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_HIRING_MANAGERS;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_INTERVIEWERS;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_REVIEWERS;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_REJECTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_PARTNER_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REFERENCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REVIEW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_TERMINATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_COMPLETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_COMPLETE_PARENT_APPROVAL_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_TERMINATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_VIEW_EDIT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.INSTITUTION_COMPLETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_COMPLETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_COMPLETE_PARENT_APPROVAL_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_TERMINATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_VIEW_EDIT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_COMPLETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_COMPLETE_PARENT_APPROVAL_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_TERMINATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_VIEW_EDIT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_INSTITUTION_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_APPOINTEE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CONFIRMED_INTERVIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_HIRING_MANAGER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_POTENTIAL_INTERVIEWEE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_POTENTIAL_INTERVIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_REVIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_SCHEDULED_INTERVIEWEE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_SCHEDULED_INTERVIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_FEEDBACK;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_PARTNER_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REJECTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_RESERVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_UNSUBMITTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVAL_PARENT_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_DISABLED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_REJECTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_UNSUBMITTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_DISABLED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_REJECTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_UNSUBMITTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_APPROVAL_PARENT_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_REJECTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_UNSUBMITTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_APPROVAL_PARENT_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_DISABLED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_REJECTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_UNSUBMITTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_CONFIRMED_OFFER_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PROVIDED_HIRING_MANAGER_APPROVAL_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PROVIDED_REVIEW_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.DEPARTMENT_APPROVED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.DEPARTMENT_COMPLETED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.DEPARTMENT_CREATED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.DEPARTMENT_UPDATED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.INSTITUTION_APPROVED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.INSTITUTION_COMPLETED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.INSTITUTION_CREATED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.INSTITUTION_UPDATED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_APPROVED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_COMPLETED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_CREATED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_UPDATED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_APPROVED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_COMPLETED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_CREATED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_UPDATED_OUTCOME;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

public enum PrismStateTransitionGroup {

    APPLICATION_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_UNSUBMITTED) //
                    .withTransitionAction(APPLICATION_COMPLETE)), //

    APPLICATION_COMPLETE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_VALIDATION) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST)), //

    APPLICATION_COMPLETE_STATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REFERENCE) //
                    .withTransitionAction(APPLICATION_PROVIDE_REFERENCE) //
                    .withReplicableSequenceClose() //
                    .withStateTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME) //
                    .withRoleTransitions(APPLICATION_CREATE_REFEREE_GROUP), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REVIEW) //
                    .withTransitionAction(APPLICATION_ASSIGN_REVIEWERS) //
                    .withStateTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW) //
                    .withTransitionAction(APPLICATION_ASSIGN_INTERVIEWERS) //
                    .withStateTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVAL) //
                    .withTransitionAction(APPLICATION_ASSIGN_HIRING_MANAGERS) //
                    .withStateTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_RESERVED) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withReplicableSequenceClose() //
                    .withStateTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVED) //
                    .withTransitionAction(APPLICATION_CONFIRM_OFFER) //
                    .withStateTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED) //
                    .withTransitionAction(APPLICATION_CONFIRM_REJECTION) //
                    .withStateTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME)), //

    APPLICATION_ASSIGN_HIRING_MANAGERS_TRANSITION(
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVAL_PENDING_FEEDBACK) //
                    .withTransitionAction(APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL) //
                    .withReplicableSequenceClose() //
                    .withRoleTransitions(APPLICATION_CREATE_HIRING_MANAGER_GROUP)),

    APPLICATION_ASSIGN_INTERVIEWERS_TRANSITION(
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_AVAILABILITY) //
                    .withTransitionAction(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY) //
                    .withReplicableSequenceClose() //
                    .withStateTransitionEvaluation(APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME) //
                    .withRoleTransitions(APPLICATION_CREATE_POTENTIAL_INTERVIEWEE_GROUP, APPLICATION_CREATE_POTENTIAL_INTERVIEWER_GROUP), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_INTERVIEW) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withReplicableSequenceClose() //
                    .withStateTransitionEvaluation(APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME) //
                    .withRoleTransitions(APPLICATION_CREATE_SCHEDULED_INTERVIEWEE_GROUP, APPLICATION_CREATE_SCHEDULED_INTERVIEWER_GROUP),
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_FEEDBACK) //
                    .withTransitionAction(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK) //
                    .withReplicableSequenceClose() //
                    .withStateTransitionEvaluation(APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME) //
                    .withRoleTransitions(APPLICATION_CREATE_CONFIRMED_INTERVIEWER_GROUP)),

    APPLICATION_ASSIGN_REVIEWERS_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REVIEW_PENDING_FEEDBACK) //
                    .withTransitionAction(APPLICATION_PROVIDE_REVIEW) //
                    .withReplicableSequenceClose() //
                    .withReplicableSequenceFilterTheme() //
                    .withRoleTransitions(APPLICATION_CREATE_REVIEWER_GROUP)),

    APPLICATION_CONFIRM_OFFER_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVED_PENDING_PARTNER_APPROVAL)
                    .withTransitionAction(APPLICATION_PROVIDE_PARTNER_APPROVAL) //
                    .withReplicableSequenceClose() //
                    .withStateTransitionEvaluation(APPLICATION_CONFIRMED_OFFER_OUTCOME) //
                    .withRoleTransitions(APPLICATION_RETIRE_REFEREE_GROUP) //
                    .withStateTerminations(APPLICATION_TERMINATE_REFERENCE_GROUP.getStateTerminations()), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withReplicableSequenceClose() //
                    .withStateTransitionEvaluation(APPLICATION_CONFIRMED_OFFER_OUTCOME) //
                    .withRoleTransitions(APPLICATION_CREATE_APPOINTEE_GROUP, APPLICATION_RETIRE_REFEREE_GROUP)
                    .withStateTerminations(APPLICATION_TERMINATE_REFERENCE_GROUP.getStateTerminations())),

    APPLICATION_CONFIRM_REJECTION_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_COMPLETED) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST)
                    .withReplicableSequenceClose() //
                    .withRoleTransitions(APPLICATION_RETIRE_REFEREE_GROUP) //
                    .withStateTerminations(APPLICATION_TERMINATE_REFERENCE_GROUP.getStateTerminations())),

    APPLICATION_WITHDRAW_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST)), //

    APPLICATION_ESCALATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_COMPLETED) //
                    .withTransitionAction(APPLICATION_ESCALATE)), //

    APPLICATION_PROVIDE_REVIEW_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REVIEW_PENDING_FEEDBACK) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withStateTransitionEvaluation(APPLICATION_PROVIDED_REVIEW_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REVIEW_PENDING_COMPLETION) //
                    .withTransitionAction(APPLICATION_COMPLETE_REVIEW_STAGE) //
                    .withStateTransitionEvaluation(APPLICATION_PROVIDED_REVIEW_OUTCOME)),

    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_AVAILABILITY) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withStateTransitionEvaluation(APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_SCHEDULING) //
                    .withTransitionAction(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
                    .withStateTransitionEvaluation(APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME)), //

    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_FEEDBACK) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                    .withStateTransitionEvaluation(APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_COMPLETION) //
                    .withTransitionAction(PrismAction.APPLICATION_COMPLETE_INTERVIEW_STAGE) //
                    .withStateTransitionEvaluation(APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME)), //

    APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVAL_PENDING_COMPLETION) //
                    .withTransitionAction(PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE) //
                    .withStateTransitionEvaluation(APPLICATION_PROVIDED_HIRING_MANAGER_APPROVAL_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVAL_PENDING_FEEDBACK) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                    .withStateTransitionEvaluation(APPLICATION_PROVIDED_HIRING_MANAGER_APPROVAL_OUTCOME)), //

    APPLICATION_CONFIRM_OFFER_ACCEPTANCE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVED_COMPLETED) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST)), //

    APPLICATION_TERMINATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_COMPLETED) //
                    .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST)), //

    PROJECT_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_UNSUBMITTED) //
                    .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
                    .withStateTransitionEvaluation(PROJECT_CREATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVAL_PARENT_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
                    .withStateTransitionEvaluation(PROJECT_CREATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
                    .withStateTransitionEvaluation(PROJECT_CREATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVED) //
                    .withTransitionAction(PROJECT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PROJECT_CREATED_OUTCOME)), //

    PROJECT_COMPLETE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_UNSUBMITTED) //
                    .withTransitionAction(PROJECT_COMPLETE) //
                    .withStateTransitionEvaluation(PROJECT_COMPLETED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVAL_PARENT_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
                    .withStateTransitionEvaluation(PROJECT_COMPLETED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
                    .withStateTransitionEvaluation(PROJECT_COMPLETED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVED) //
                    .withTransitionAction(PROJECT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PROJECT_COMPLETED_OUTCOME)), //

    PROJECT_APPROVE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVED) //
                    .withTransitionAction(PROJECT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PROJECT_APPROVED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_REJECTED) //
                    .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
                    .withStateTransitionEvaluation(PROJECT_APPROVED_OUTCOME)), //

    PROJECT_VIEW_EDIT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVED) //
                    .withTransitionAction(PROJECT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PROJECT_UPDATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_DISABLED_COMPLETED) //
                    .withTransitionAction(PROJECT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PROJECT_UPDATED_OUTCOME) //
                    .withPropagatedActions(APPLICATION_TERMINATE)), //

    PROJECT_ENDORSE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVED) //
                    .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST)), //

    PROGRAM_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_UNSUBMITTED) //
                    .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PROGRAM_CREATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVAL_PARENT_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PROGRAM_CREATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PROGRAM_CREATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVED) //
                    .withTransitionAction(PROGRAM_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PROGRAM_CREATED_OUTCOME)), //

    PROGRAM_COMPLETE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_UNSUBMITTED) //
                    .withTransitionAction(PROGRAM_COMPLETE) //
                    .withStateTransitionEvaluation(PROGRAM_COMPLETED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVAL_PARENT_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PROGRAM_COMPLETED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PROGRAM_COMPLETED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVED) //
                    .withTransitionAction(PROGRAM_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PROGRAM_COMPLETED_OUTCOME)), //

    PROGRAM_APPROVE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVED) //
                    .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PROGRAM_APPROVED_OUTCOME) //
                    .withPropagatedActions(PROJECT_COMPLETE_PARENT_APPROVAL_STAGE),
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_REJECTED) //
                    .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PROGRAM_APPROVED_OUTCOME)), //

    PROGRAM_VIEW_EDIT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVED) //
                    .withTransitionAction(PROGRAM_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PROGRAM_UPDATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_DISABLED_COMPLETED) //
                    .withTransitionAction(PROGRAM_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PROGRAM_UPDATED_OUTCOME) //
                    .withPropagatedActions(PROJECT_TERMINATE)), //

    PROGRAM_ENDORSE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVED) //
                    .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST)), //

    DEPARTMENT_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_UNSUBMITTED) //
                    .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(DEPARTMENT_CREATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVAL_PARENT_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(DEPARTMENT_CREATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(DEPARTMENT_CREATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVED) //
                    .withTransitionAction(DEPARTMENT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(DEPARTMENT_CREATED_OUTCOME)), //

    DEPARTMENT_COMPLETE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_UNSUBMITTED) //
                    .withTransitionAction(DEPARTMENT_COMPLETE) //
                    .withStateTransitionEvaluation(DEPARTMENT_COMPLETED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVAL_PARENT_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(DEPARTMENT_COMPLETED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(DEPARTMENT_COMPLETED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVED) //
                    .withTransitionAction(DEPARTMENT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(DEPARTMENT_COMPLETED_OUTCOME)), //

    DEPARTMENT_APPROVE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVED) //
                    .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(DEPARTMENT_APPROVED_OUTCOME) //
                    .withPropagatedActions(PROJECT_COMPLETE_PARENT_APPROVAL_STAGE, PROGRAM_COMPLETE_PARENT_APPROVAL_STAGE),
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_REJECTED) //
                    .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(DEPARTMENT_APPROVED_OUTCOME)), //

    DEPARTMENT_VIEW_EDIT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVED) //
                    .withTransitionAction(DEPARTMENT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(DEPARTMENT_UPDATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_DISABLED_COMPLETED) //
                    .withTransitionAction(DEPARTMENT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(DEPARTMENT_UPDATED_OUTCOME) //
                    .withPropagatedActions(PROGRAM_TERMINATE, PROJECT_TERMINATE, APPLICATION_TERMINATE)), //

    INSTITUTION_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_UNSUBMITTED) //
                    .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST) //
                    .withStateTransitionEvaluation(INSTITUTION_CREATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST) //
                    .withStateTransitionEvaluation(INSTITUTION_CREATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVED) //
                    .withTransitionAction(INSTITUTION_VIEW_EDIT) //
                    .withStateTransitionEvaluation(INSTITUTION_CREATED_OUTCOME)), //

    INSTITUTION_COMPLETE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_UNSUBMITTED) //
                    .withTransitionAction(INSTITUTION_COMPLETE) //
                    .withStateTransitionEvaluation(INSTITUTION_COMPLETED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVAL) //
                    .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST) //
                    .withStateTransitionEvaluation(INSTITUTION_COMPLETED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVED) //
                    .withTransitionAction(INSTITUTION_VIEW_EDIT) //
                    .withStateTransitionEvaluation(INSTITUTION_COMPLETED_OUTCOME)), //

    INSTITUTION_APPROVE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVED) //
                    .withTransitionAction(INSTITUTION_VIEW_EDIT) //
                    .withStateTransitionEvaluation(INSTITUTION_APPROVED_OUTCOME) //
                    .withPropagatedActions(PROJECT_COMPLETE_PARENT_APPROVAL_STAGE, PROGRAM_COMPLETE_PARENT_APPROVAL_STAGE,
                            DEPARTMENT_COMPLETE_PARENT_APPROVAL_STAGE),
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_REJECTED) //
                    .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST)
                    .withStateTransitionEvaluation(INSTITUTION_APPROVED_OUTCOME)),

    INSTITUTION_VIEW_EDIT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVED) //
                    .withTransitionAction(INSTITUTION_VIEW_EDIT) //
                    .withStateTransitionEvaluation(INSTITUTION_UPDATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_DISABLED_COMPLETED) //
                    .withTransitionAction(INSTITUTION_VIEW_EDIT) //
                    .withStateTransitionEvaluation(INSTITUTION_UPDATED_OUTCOME) //
                    .withPropagatedActions(DEPARTMENT_TERMINATE, PROGRAM_TERMINATE, PROJECT_TERMINATE, APPLICATION_TERMINATE));

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
        return withStateTerminationsAndRoleTransitions(Collections.<PrismStateTermination> emptyList(), roleTransitions);
    }

    public PrismStateTransition[] withStateTerminationsAndRoleTransitions(PrismStateTerminationGroup stateTerminationGroup,
            PrismRoleTransitionGroup... roleTransitionGroups) {
        List<PrismRoleTransition> roleTransitions = Lists.newLinkedList();
        for (PrismRoleTransitionGroup roleTransitionGroup : roleTransitionGroups) {
            roleTransitions.addAll(Lists.newArrayList(roleTransitionGroup.getRoleTransitions()));
        }
        return withStateTerminationsAndRoleTransitions(Lists.newArrayList(stateTerminationGroup.getStateTerminations()), roleTransitions);
    }

    public PrismStateTransition[] withRoleTransitionsAndExclusions(List<PrismState> exclusions, PrismRoleTransitionGroup... roleTransitionGroups) {
        List<PrismRoleTransition> roleTransitions = Lists.newLinkedList();
        for (PrismRoleTransitionGroup roleTransitionGroup : roleTransitionGroups) {
            roleTransitions.addAll(Lists.newArrayList(roleTransitionGroup.getRoleTransitions()));
        }
        return withStateTerminationsAndRoleTransitions(Collections.<PrismStateTermination> emptyList(), roleTransitions,
                exclusions.toArray(new PrismState[exclusions.size()]));
    }

    public PrismStateTransition[] withExclusions(List<PrismState> exclusions) {
        return withStateTerminationsAndRoleTransitions(Collections.<PrismStateTermination> emptyList(), Collections.<PrismRoleTransition> emptyList(),
                exclusions.toArray(new PrismState[exclusions.size()]));
    }

    private PrismStateTransition[] withStateTerminationsAndRoleTransitions(List<PrismStateTermination> stateTerminations,
            List<PrismRoleTransition> roleTransitions, PrismState... exclusions) {
        List<PrismState> exclusionsList = Arrays.asList(exclusions);
        List<PrismStateTransition> stateTransitions = Lists.newLinkedList();
        for (PrismStateTransition stateTransition : getStateTransitions()) {
            PrismState transitionState = stateTransition.getTransitionState();
            if (!exclusionsList.contains(transitionState)) {
                Set<PrismRoleTransition> definedRoleTransitions = stateTransition.getRoleTransitions();
                Set<PrismStateTermination> definedStateTerminations = stateTransition.getStateTerminations();
                Set<PrismAction> definedPropagatedActions = stateTransition.getPropagatedActions();
                stateTransitions.add(new PrismStateTransition() //
                        .withTransitionState(stateTransition.getTransitionState()) //
                        .withTransitionAction(stateTransition.getTransitionAction()) //
                        .withReplicableSequenceClose(stateTransition.getReplicableSequenceClose()) //
                        .withReplicableSequenceFilterTheme(stateTransition.getReplicableSequenceFilterTheme()) //
                        .withReplicableSequenceFilterSecondaryTheme(stateTransition.getReplicableSequenceFilterSecondaryTheme()) //
                        .withReplicableSequenceFilterLocation(stateTransition.getReplicableSequenceFilterLocation()) //
                        .withReplicableSequenceFilterSecondaryLocation(stateTransition.getReplicableSequenceFilterSecondaryLocation()) //
                        .withStateTransitionEvaluation(stateTransition.getTransitionEvaluation()) //
                        .withRoleTransitions(definedRoleTransitions.toArray(new PrismRoleTransition[definedRoleTransitions.size()])) //
                        .withRoleTransitions(roleTransitions.toArray(new PrismRoleTransition[roleTransitions.size()])) //
                        .withStateTerminations(definedStateTerminations.toArray(new PrismStateTermination[definedStateTerminations.size()])) //
                        .withStateTerminations(stateTerminations.toArray(new PrismStateTermination[stateTerminations.size()])) //
                        .withPropagatedActions(definedPropagatedActions.toArray(new PrismAction[definedPropagatedActions.size()])));
            }
        }
        return stateTransitions.toArray(new PrismStateTransition[stateTransitions.size()]);
    }
}
