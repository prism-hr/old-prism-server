package uk.co.alumeni.prism.domain.definitions.workflow;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_FEEDBACK;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REJECTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_UNSUBMITTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public enum PrismStateTransitionGroup {

    APPLICATION_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_UNSUBMITTED) //
                    .withTransitionAction(PrismAction.APPLICATION_COMPLETE)), //

    APPLICATION_COMPLETE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_VALIDATION) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_VALIDATION_PENDING_COMPLETION) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_OUTCOME)),

    APPLICATION_COMPLETE_STATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REFERENCE) //
                    .withTransitionAction(PrismAction.APPLICATION_PROVIDE_REFERENCE) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME) //
                    .withRoleTransitions(APPLICATION_CREATE_REFEREE_GROUP), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REVIEW) //
                    .withTransitionAction(PrismAction.APPLICATION_ASSIGN_REVIEWERS) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW) //
                    .withTransitionAction(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVAL) //
                    .withTransitionAction(PrismAction.APPLICATION_ASSIGN_HIRING_MANAGERS) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVED) //
                    .withTransitionAction(PrismAction.APPLICATION_CONFIRM_OFFER) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED) //
                    .withTransitionAction(PrismAction.APPLICATION_CONFIRM_REJECTION) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME)), //

    APPLICATION_WITHDRAW_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST)), //

    APPLICATION_ESCALATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_COMPLETED) //
                    .withTransitionAction(PrismAction.APPLICATION_ESCALATE)), //

    APPLICATION_PROVIDE_REVIEW_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REVIEW_PENDING_FEEDBACK) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROVIDED_REVIEW_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REVIEW_PENDING_COMPLETION) //
                    .withTransitionAction(PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROVIDED_REVIEW_OUTCOME)),

    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_AVAILABILITY) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_SCHEDULING) //
                    .withTransitionAction(PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME)), //

    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_FEEDBACK) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_INTERVIEW_PENDING_COMPLETION) //
                    .withTransitionAction(PrismAction.APPLICATION_COMPLETE_INTERVIEW_STAGE) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME)), //

    APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVAL_PENDING_COMPLETION) //
                    .withTransitionAction(PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROVIDED_HIRING_MANAGER_APPROVAL_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVAL_PENDING_FEEDBACK) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROVIDED_HIRING_MANAGER_APPROVAL_OUTCOME)), //

    APPLICATION_CONFIRM_OFFER_ACCEPTANCE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_APPROVED_COMPLETED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST)), //

    APPLICATION_CONFIRM_REJECTION_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_COMPLETED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST)), //

    APPLICATION_TERMINATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(APPLICATION_REJECTED_COMPLETED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST)), //

    PROJECT_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_UNSUBMITTED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_CREATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVAL_PARENT_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_CREATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_CREATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVED) //
                    .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_CREATED_OUTCOME)), //

    PROJECT_COMPLETE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVAL_PARENT_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_COMPLETED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_COMPLETED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVED) //
                    .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_COMPLETED_OUTCOME)), //


    PROJECT_APPROVE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVED) //
                    .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_APPROVED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_REJECTED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_APPROVED_OUTCOME)), //

    PROJECT_VIEW_EDIT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVED) //
                    .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_UPDATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_DISABLED_COMPLETED) //
                    .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_UPDATED_OUTCOME) //
                    .withPropagatedActions(PrismAction.APPLICATION_TERMINATE)), //

    PROJECT_ENDORSE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROJECT_APPROVED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST)), //

    PROGRAM_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_UNSUBMITTED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_CREATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVAL_PARENT_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_CREATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_CREATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVED) //
                    .withTransitionAction(PrismAction.PROGRAM_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_CREATED_OUTCOME)), //

    PROGRAM_COMPLETE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVAL_PARENT_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_COMPLETED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_COMPLETED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVED) //
                    .withTransitionAction(PrismAction.PROGRAM_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_COMPLETED_OUTCOME)), //

    PROGRAM_APPROVE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_APPROVED_OUTCOME) //
                    .withPropagatedActions(PrismAction.PROJECT_COMPLETE_PARENT_APPROVAL_STAGE),
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_REJECTED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_APPROVED_OUTCOME)), //

    PROGRAM_VIEW_EDIT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVED) //
                    .withTransitionAction(PrismAction.PROGRAM_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_UPDATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_DISABLED_COMPLETED) //
                    .withTransitionAction(PrismAction.PROGRAM_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_UPDATED_OUTCOME) //
                    .withPropagatedActions(PrismAction.PROJECT_TERMINATE)), //

    PROGRAM_ENDORSE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(PROGRAM_APPROVED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST)), //

    DEPARTMENT_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_UNSUBMITTED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.DEPARTMENT_CREATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVAL_PARENT_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.DEPARTMENT_CREATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.DEPARTMENT_CREATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVED) //
                    .withTransitionAction(PrismAction.DEPARTMENT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.DEPARTMENT_CREATED_OUTCOME)), //

    DEPARTMENT_COMPLETE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVAL_PARENT_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.DEPARTMENT_COMPLETED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.DEPARTMENT_COMPLETED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVED) //
                    .withTransitionAction(PrismAction.DEPARTMENT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.DEPARTMENT_COMPLETED_OUTCOME)), //

    DEPARTMENT_APPROVE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.DEPARTMENT_APPROVED_OUTCOME) //
                    .withPropagatedActions(PrismAction.PROJECT_COMPLETE_PARENT_APPROVAL_STAGE, PrismAction.PROGRAM_COMPLETE_PARENT_APPROVAL_STAGE),
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_REJECTED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.DEPARTMENT_APPROVED_OUTCOME)), //

    DEPARTMENT_VIEW_EDIT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_APPROVED) //
                    .withTransitionAction(PrismAction.DEPARTMENT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.DEPARTMENT_UPDATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(DEPARTMENT_DISABLED_COMPLETED) //
                    .withTransitionAction(PrismAction.DEPARTMENT_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.DEPARTMENT_UPDATED_OUTCOME) //
                    .withPropagatedActions(PrismAction.PROGRAM_TERMINATE, PrismAction.PROJECT_TERMINATE, PrismAction.APPLICATION_TERMINATE)), //

    INSTITUTION_CREATE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_UNSUBMITTED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_CREATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_CREATED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVED) //
                    .withTransitionAction(PrismAction.INSTITUTION_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_CREATED_OUTCOME)), //

    INSTITUTION_COMPLETE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVAL) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_COMPLETED_OUTCOME), //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVED) //
                    .withTransitionAction(PrismAction.INSTITUTION_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_COMPLETED_OUTCOME)), //

    INSTITUTION_APPROVE_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVED) //
                    .withTransitionAction(PrismAction.INSTITUTION_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_APPROVED_OUTCOME) //
                    .withPropagatedActions(PrismAction.PROJECT_COMPLETE_PARENT_APPROVAL_STAGE, PrismAction.PROGRAM_COMPLETE_PARENT_APPROVAL_STAGE, PrismAction.DEPARTMENT_COMPLETE_PARENT_APPROVAL_STAGE),
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_REJECTED) //
                    .withTransitionAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST)
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_APPROVED_OUTCOME)),

    INSTITUTION_VIEW_EDIT_TRANSITION( //
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_APPROVED) //
                    .withTransitionAction(PrismAction.INSTITUTION_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_UPDATED_OUTCOME),
            new PrismStateTransition() //
                    .withTransitionState(INSTITUTION_DISABLED_COMPLETED) //
                    .withTransitionAction(PrismAction.INSTITUTION_VIEW_EDIT) //
                    .withStateTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_UPDATED_OUTCOME) //
                    .withPropagatedActions(PrismAction.DEPARTMENT_TERMINATE, PrismAction.PROGRAM_TERMINATE, PrismAction.PROJECT_TERMINATE, PrismAction.APPLICATION_TERMINATE));

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
        List<PrismState> exclusionsAsList = Arrays.asList(exclusions);
        List<PrismStateTransition> stateTransitions = Lists.newLinkedList();
        for (PrismStateTransition stateTransition : getStateTransitions()) {
            PrismState transitionState = stateTransition.getTransitionState();
            if (!exclusionsAsList.contains(transitionState)) {
                List<PrismRoleTransition> definedRoleTransitions = stateTransition.getRoleTransitions();
                List<PrismStateTermination> definedStateTerminations = stateTransition.getStateTerminations();
                List<PrismAction> definedPropagatedActions = stateTransition.getPropagatedActions();
                stateTransitions.add(new PrismStateTransition() //
                        .withTransitionState(stateTransition.getTransitionState()) //
                        .withTransitionAction(stateTransition.getTransitionAction()) //
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
