package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CORRECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_EMAIL_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_FORGET_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PURGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_TERMINATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_UPLOAD_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_TERMINATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_VIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_UPDATE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationEvaluation.APPLICATION_REFERENCED_TERMINATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_ESCALATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_TERMINATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_WITHDRAW_TRANSITION;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTermination;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup;

public class PrismApplicationWorkflow {

    public static PrismStateAction applicationComment() {
        return new PrismStateAction() //
                .withAction(APPLICATION_COMMENT)
                .withAssignments(APPLICATION_PARENT_VIEWER_GROUP) //
                .withAssignments(APPLICATION_VIEWER_REFEREE); //
    }

    public static PrismStateAction applicationCommentWithViewerRecruiter() {
        return applicationComment() //
                .withAssignments(APPLICATION_VIEWER_RECRUITER);
    }

    public static PrismStateAction applicationCommentWithViewerRecruiterAndAdministrator() {
        return applicationCommentWithViewerRecruiter() //
                .withAssignments(APPLICATION_ADMINISTRATOR);
    }

    public static PrismStateAction applicationCompleteState(PrismAction action, PrismState state, PrismRoleGroup assignees) {
        return applicationCompleteStateAbstract(action, assignees)
                .withTransitions(APPLICATION_COMPLETE_STATE_TRANSITION //
                        .withExclusions(applicationNextStateExclusions(state)));
    }

    public static PrismStateAction applicationCompleteState(PrismAction action, PrismState state, PrismRoleGroup assignees,
            PrismRoleTransitionGroup... roleTransitions) {
        return applicationCompleteStateAbstract(action, assignees)
                .withTransitions(APPLICATION_COMPLETE_STATE_TRANSITION //
                        .withRoleTransitionsAndExclusions(applicationNextStateExclusions(state), roleTransitions));
    }

    public static PrismStateAction applicationCorrect(PrismState state) {
        return new PrismStateAction() //
                .withAction(APPLICATION_CORRECT) //
                .withRaisesUrgentFlag() //
                .withAssignments(INSTITUTION_ADMINISTRATOR) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST));
    }

    public static PrismStateAction applicationForgetExport(PrismState state) {
        return new PrismStateAction() //
                .withAction(APPLICATION_FORGET_EXPORT) //
                .withAssignments(INSTITUTION_ADMINISTRATOR) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST));
    }

    public static PrismStateAction applicationEmailCreator() {
        return new PrismStateAction() //
                .withAction(APPLICATION_EMAIL_CREATOR) //
                .withAssignments(APPLICATION_PARENT_VIEWER_GROUP) //
                .withAssignments(APPLICATION_VIEWER_REFEREE) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction applicationEmailCreatorWithViewerRecruiter() {
        return applicationEmailCreator() //
                .withAssignments(APPLICATION_VIEWER_RECRUITER);
    }

    public static PrismStateAction applicationEmailCreatorWithViewerRecruiterAndAdministrator() {
        return applicationEmailCreatorWithViewerRecruiter() //
                .withAssignments(APPLICATION_ADMINISTRATOR);
    }

    public static PrismStateAction applicationEscalate(PrismState state) {
        return applicationEscalateAbstract() //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_ESCALATE));
    }

    public static PrismStateAction applicationEscalate(PrismRoleTransitionGroup... roleTransitions) {
        return applicationEscalateAbstract() //
                .withNotifications(APPLICATION_CREATOR, APPLICATION_TERMINATE_NOTIFICATION) //
                .withTransitions(APPLICATION_ESCALATE_TRANSITION //
                        .withRoleTransitions(roleTransitions)); //
    }

    public static PrismStateAction applicationEscalate(PrismState state, PrismRoleTransitionGroup... roleTransitions) {
        return applicationEscalateAbstract() //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_ESCALATE) //
                        .withRoleTransitions(roleTransitions));
    }

    public static PrismStateAction applicationExport(PrismStateTransitionGroup stateTransitions) {
        return new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_EXPORT) //
                .withTransitions(stateTransitions);
    }

    public static PrismStateAction applicationPurge(PrismStateTransitionGroup stateTransitions) {
        return new PrismStateAction() //
                .withAction(APPLICATION_PURGE) //
                .withTransitions(stateTransitions);
    }

    public static PrismStateAction applicationTerminateUnsubmitted() {
        return applicationTerminateAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED) //
                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST));
    }

    public static PrismStateAction applicationTerminateSubmitted() {
        return applicationTerminateAbstract() //
                .withTransitions(APPLICATION_TERMINATE_TRANSITION);
    }

    public static PrismStateAction applicationTerminateSubmitted(PrismStateTerminationGroup stateTerminations, PrismRoleTransitionGroup... roleTransitions) {
        return applicationTerminateAbstract() //
                .withTransitions(APPLICATION_TERMINATE_TRANSITION //
                        .withStateTerminationsAndRoleTransitions(stateTerminations, roleTransitions));
    }

    public static PrismStateAction applicationUploadReference(PrismState state) {
        return new PrismStateAction() //
                .withAction(APPLICATION_UPLOAD_REFERENCE) //
                .withAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_UPLOAD_REFERENCE) //
                        .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP) //
                        .withStateTerminations(new PrismStateTermination() //
                                .withTerminationState(APPLICATION_REFERENCE) //
                                .withStateTerminationEvaluation(APPLICATION_REFERENCED_TERMINATION)));
    }

    public static PrismStateAction applicationViewEdit() {
        return new PrismStateAction() //
                .withAction(APPLICATION_VIEW_EDIT) //
                .withAssignments(INSTITUTION_ADMINISTRATOR, APPLICATION_VIEW_AS_APPROVER) //
                .withAssignments(INSTITUTION_APPROVER, APPLICATION_VIEW_AS_APPROVER) //
                .withAssignments(INSTITUTION_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(DEPARTMENT_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(DEPARTMENT_APPROVER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(DEPARTMENT_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_APPROVER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROJECT_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROJECT_PRIMARY_SUPERVISOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_AS_CREATOR) //
                .withAssignments(APPLICATION_VIEWER_REFEREE, APPLICATION_VIEW_AS_REFEREE) //
                .withAssignments(APPLICATION_VIEWER_RECRUITER, APPLICATION_VIEW_AS_RECRUITER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationViewEditCorrect() {
        return new PrismStateAction() //
                .withAction(APPLICATION_VIEW_EDIT) // //
                .withAssignments(INSTITUTION_ADMINISTRATOR, APPLICATION_VIEW_EDIT_AS_APPROVER) //
                .withAssignments(INSTITUTION_APPROVER, APPLICATION_VIEW_EDIT_AS_APPROVER) //
                .withAssignments(INSTITUTION_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(DEPARTMENT_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(DEPARTMENT_APPROVER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(DEPARTMENT_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_APPROVER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROJECT_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROJECT_PRIMARY_SUPERVISOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_AS_CREATOR) //
                .withAssignments(APPLICATION_VIEWER_REFEREE, APPLICATION_VIEW_AS_REFEREE) //
                .withAssignments(APPLICATION_VIEWER_RECRUITER, APPLICATION_VIEW_AS_RECRUITER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationViewEdit(PrismState state, PrismRoleTransition... roleTransitions) {
        return new PrismStateAction() //
                .withAction(APPLICATION_VIEW_EDIT) //
                .withAssignments(INSTITUTION_ADMINISTRATOR, APPLICATION_VIEW_EDIT_AS_APPROVER) //
                .withAssignments(INSTITUTION_APPROVER, APPLICATION_VIEW_EDIT_AS_APPROVER) //
                .withAssignments(INSTITUTION_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(DEPARTMENT_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(DEPARTMENT_APPROVER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(DEPARTMENT_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_APPROVER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROJECT_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROJECT_PRIMARY_SUPERVISOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_EDIT_AS_CREATOR) //
                .withAssignments(APPLICATION_VIEWER_REFEREE, APPLICATION_VIEW_AS_REFEREE) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, APPLICATION_VIEW_AS_RECRUITER)
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_VIEW_EDIT) //
                        .withRoleTransitions(roleTransitions));
    }

    public static PrismStateAction applicationViewEditWithViewerRecruiter(PrismState state) {
        return applicationViewEdit(state, APPLICATION_UPDATE_REFEREE_GROUP.getRoleTransitions()) //
                .withAssignments(APPLICATION_VIEWER_RECRUITER, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationViewEditWithViewerRecruiterAndAdministrator(PrismState state) {
        return applicationViewEditWithViewerRecruiter(state) //
                .withAssignments(APPLICATION_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationWithdrawUnsubmitted() {
        return applicationWithdrawAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED) //
                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST));
    }

    public static PrismStateAction applicationWithdrawSubmitted(PrismRoleGroup notifications, PrismRoleTransitionGroup... roleTransitions) {
        return applicationWithdrawAbstract() //
                .withTransitions(APPLICATION_WITHDRAW_TRANSITION //
                        .withRoleTransitions(roleTransitions));
    }

    public static PrismStateAction applicationWithdrawSubmitted(PrismRoleGroup notifications, PrismStateTerminationGroup stateTerminations,
            PrismRoleTransitionGroup... roleTransitions) {
        return applicationWithdrawAbstract() //
                .withTransitions(APPLICATION_WITHDRAW_TRANSITION //
                        .withStateTerminationsAndRoleTransitions(stateTerminations, roleTransitions));
    }

    private static PrismStateAction applicationCompleteStateAbstract(PrismAction action, PrismRoleGroup assignees) {
        return new PrismStateAction() //
                .withAction(action) //
                .withAssignments(assignees);
    }

    private static PrismStateAction applicationEscalateAbstract() {
        return new PrismStateAction() //
                .withAction(APPLICATION_ESCALATE);
    }

    private static PrismStateAction applicationTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(APPLICATION_TERMINATE);
    }

    private static PrismStateAction applicationWithdrawAbstract() {
        return new PrismStateAction() //
                .withAction(APPLICATION_WITHDRAW) //
                .withAssignments(APPLICATION_CREATOR);
    }

    private static List<PrismState> applicationNextStateExclusions(PrismState state) {
        List<PrismState> exclusions = Lists.newArrayList();
        if (state.name().equals(state.getStateGroup().name())) {
            exclusions.add(state);
        }
        return exclusions;
    }

}
