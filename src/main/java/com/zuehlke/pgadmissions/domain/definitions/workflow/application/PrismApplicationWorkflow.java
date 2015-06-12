package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CORRECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_FORGET_EXPORT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PURGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_UPLOAD_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CORRECT_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_TERMINATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_VIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationEvaluation.APPLICATION_REFERENCED_TERMINATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_ESCALATE_TRANSITION;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup;

public class PrismApplicationWorkflow {

    public static PrismStateAction applicationComment() {
        return new PrismStateAction() //
                .withAction(APPLICATION_COMMENT)
                .withAssignments(APPLICATION_PARENT_VIEWER_GROUP) //
                .withAssignments(APPLICATION_VIEWER_REFEREE) //
                .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION); //
    }

    public static PrismStateAction applicationCommentWithViewerRecruiter() {
        return applicationComment() //
                .withAssignments(APPLICATION_VIEWER_RECRUITER);
    }

    public static PrismStateAction applicationCommentWithViewerRecruiterAndAdministrator() {
        return applicationCommentWithViewerRecruiter() //
                .withAssignments(APPLICATION_ADMINISTRATOR) //
                .withNotifications(APPLICATION_ADMINISTRATOR, SYSTEM_APPLICATION_UPDATE_NOTIFICATION);
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
                .withNotification(APPLICATION_CORRECT_REQUEST) //
                .withAssignments(INSTITUTION_ADMINISTRATOR) //
                .withNotifications(INSTITUTION_ADMINISTRATOR, SYSTEM_APPLICATION_UPDATE_NOTIFICATION)
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
                .withAction(PrismAction.APPLICATION_EMAIL_CREATOR) //
                .withAssignments(APPLICATION_PARENT_VIEWER_GROUP) //
                .withAssignments(APPLICATION_VIEWER_REFEREE);
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

    public static PrismStateAction applicationUploadReference(PrismState state) {
        return new PrismStateAction() //
                .withAction(APPLICATION_UPLOAD_REFERENCE) //
                .withAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
                .withNotifications(APPLICATION_CREATOR, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
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
                .withAssignments(INSTITUTION_ADMINISTRATOR, APPLICATION_VIEW_AS_ADMITTER) //
                .withAssignments(INSTITUTION_ADMITTER, APPLICATION_VIEW_AS_ADMITTER) //
                .withAssignments(PROGRAM_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_APPROVER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROJECT_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROJECT_PRIMARY_SUPERVISOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_AS_CREATOR) //
                .withAssignments(APPLICATION_VIEWER_REFEREE, APPLICATION_VIEW_AS_REFEREE) //
                .withAssignments(APPLICATION_VIEWER_RECRUITER, APPLICATION_VIEW_AS_RECRUITER) //
                .withPartnerAssignments(INSTITUTION_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationViewEditCorrect() {
        return new PrismStateAction() //
                .withAction(APPLICATION_VIEW_EDIT) // //
                .withAssignments(INSTITUTION_ADMINISTRATOR, APPLICATION_VIEW_EDIT_AS_ADMITTER) //
                .withAssignments(INSTITUTION_ADMITTER, APPLICATION_VIEW_AS_ADMITTER) //
                .withAssignments(PROGRAM_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_APPROVER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROJECT_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROJECT_PRIMARY_SUPERVISOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_AS_CREATOR) //
                .withAssignments(APPLICATION_VIEWER_REFEREE, APPLICATION_VIEW_AS_REFEREE) //
                .withAssignments(APPLICATION_VIEWER_RECRUITER, APPLICATION_VIEW_AS_RECRUITER) //
                .withPartnerAssignments(INSTITUTION_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationViewEdit(PrismState state, PrismRoleTransition... roleTransitions) {
        return new PrismStateAction() //
                .withAction(APPLICATION_VIEW_EDIT) //
                .withAssignments(INSTITUTION_ADMINISTRATOR, APPLICATION_VIEW_EDIT_AS_ADMITTER) //
                .withAssignments(INSTITUTION_ADMITTER, APPLICATION_VIEW_EDIT_AS_ADMITTER) //
                .withAssignments(PROGRAM_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_APPROVER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROGRAM_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROJECT_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PROJECT_PRIMARY_SUPERVISOR, APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_EDIT_AS_CREATOR) //
                .withAssignments(APPLICATION_VIEWER_REFEREE, APPLICATION_VIEW_AS_REFEREE) //
                .withPartnerAssignments(INSTITUTION_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER)
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_VIEW_EDIT) //
                        .withRoleTransitions(roleTransitions));
    }

    public static PrismStateAction applicationViewEditWithViewerRecruiter(PrismState state) {
        return applicationViewEdit(state, APPLICATION_CREATE_REFEREE_GROUP.getRoleTransitions()) //
                .withAssignments(APPLICATION_VIEWER_RECRUITER, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationViewEditWithViewerRecruiterAndAdministrator(PrismState state) {
        return applicationViewEditWithViewerRecruiter(state) //
                .withAssignments(APPLICATION_ADMINISTRATOR, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationWithdraw() {
        return applicationWithdrawAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED) //
                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST));
    }

    public static PrismStateAction applicationWithdraw(PrismRoleGroup notifications, PrismRoleTransitionGroup... roleTransitions) {
        return applicationWithdrawAbstract() //
                .withNotifications(notifications, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
                .withTransitions(APPLICATION_WITHDRAW_TRANSITION //
                        .withRoleTransitions(roleTransitions));
    }

    private static PrismStateAction applicationCompleteStateAbstract(PrismAction action, PrismRoleGroup assignees) {
        return new PrismStateAction() //
                .withAction(action) //
                .withAssignments(assignees) //
                .withNotifications(assignees, SYSTEM_APPLICATION_UPDATE_NOTIFICATION);
    }

    private static PrismStateAction applicationEscalateAbstract() {
        return new PrismStateAction() //
                .withAction(APPLICATION_ESCALATE);
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
