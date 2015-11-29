package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_TERMINATE_NOTIFICATION;

import java.util.List;

import com.google.common.collect.Lists;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTermination;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationEvaluation;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;

public class PrismApplicationWorkflow {

    public static PrismStateAction applicationComment() {
        return new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_COMMENT)
                .withAssignments(PrismRoleGroup.APPLICATION_PARENT_VIEWER_GROUP) //
                .withAssignments(PrismRole.APPLICATION_VIEWER_REFEREE) //
                .withPartnerAssignments(PrismRole.INSTITUTION_ADMINISTRATOR) //
                .withPartnerAssignments(PrismRole.INSTITUTION_APPROVER) //
                .withPartnerAssignments(PrismRole.DEPARTMENT_ADMINISTRATOR) //
                .withPartnerAssignments(PrismRole.DEPARTMENT_APPROVER); //
    }

    public static PrismStateAction applicationCommentWithViewerRecruiter() {
        return applicationComment() //
                .withAssignments(PrismRole.APPLICATION_VIEWER_RECRUITER);
    }

    public static PrismStateAction applicationCompleteState(PrismAction action, PrismState state, PrismRoleGroup assignees) {
        return applicationCompleteStateAbstract(action, assignees)
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION //
                        .withExclusions(applicationNextStateExclusions(state)));
    }

    public static PrismStateAction applicationCompleteState(PrismAction action, PrismState state, PrismRoleGroup assignees,
            PrismRoleTransitionGroup... roleTransitions) {
        return applicationCompleteStateAbstract(action, assignees)
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION //
                        .withRoleTransitionsAndExclusions(applicationNextStateExclusions(state), roleTransitions));
    }

    public static PrismStateAction applicationEmailCreator() {
        return new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_EMAIL_CREATOR) //
                .withAssignments(PrismRoleGroup.APPLICATION_PARENT_VIEWER_GROUP) //
                .withAssignments(PrismRole.APPLICATION_VIEWER_REFEREE) //
                .withPartnerAssignments(PrismRole.INSTITUTION_ADMINISTRATOR) //
                .withPartnerAssignments(PrismRole.INSTITUTION_APPROVER) //
                .withPartnerAssignments(PrismRole.DEPARTMENT_ADMINISTRATOR) //
                .withPartnerAssignments(PrismRole.DEPARTMENT_APPROVER);
    }

    public static PrismStateAction applicationEmailCreatorWithViewerRecruiter() {
        return applicationEmailCreator() //
                .withAssignments(PrismRole.APPLICATION_VIEWER_RECRUITER);
    }

    public static PrismStateAction applicationEscalate(PrismState state) {
        return applicationEscalateAbstract() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(PrismAction.APPLICATION_ESCALATE));
    }

    public static PrismStateAction applicationEscalate(PrismRoleTransitionGroup... roleTransitions) {
        return applicationEscalateAbstract() //
                .withNotifications(PrismRole.APPLICATION_CREATOR, APPLICATION_TERMINATE_NOTIFICATION) //
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_ESCALATE_TRANSITION //
                        .withRoleTransitions(roleTransitions)); //
    }

    public static PrismStateAction applicationEscalate(PrismState state, PrismRoleTransitionGroup... roleTransitions) {
        return applicationEscalateAbstract() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(PrismAction.APPLICATION_ESCALATE) //
                        .withRoleTransitions(roleTransitions));
    }

    public static PrismStateAction applicationTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_TERMINATE);
    }

    public static PrismStateAction applicationTerminateUnsubmitted() {
        return applicationTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST));
    }

    public static PrismStateAction applicationTerminateSubmitted() {
        return applicationTerminateAbstract() //
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_TERMINATE_TRANSITION);
    }

    public static PrismStateAction applicationTerminateSubmitted(PrismStateTerminationGroup stateTerminations, PrismRoleTransitionGroup... roleTransitions) {
        return applicationTerminateAbstract() //
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_TERMINATE_TRANSITION //
                        .withStateTerminationsAndRoleTransitions(stateTerminations, roleTransitions));
    }

    public static PrismStateAction applicationUploadReference(PrismState state) {
        return new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_UPLOAD_REFERENCE) //
                .withAssignments(PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(PrismAction.APPLICATION_UPLOAD_REFERENCE) //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP) //
                        .withStateTerminations(new PrismStateTermination() //
                                .withTerminationState(PrismState.APPLICATION_REFERENCE) //
                                .withStateTerminationEvaluation(PrismStateTerminationEvaluation.APPLICATION_REFERENCED_TERMINATION)));
    }

    public static PrismStateAction applicationViewEdit(PrismState state, PrismRoleTransition... roleTransitions) {
        return applicationViewEdit() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(PrismAction.APPLICATION_VIEW_EDIT) //
                        .withRoleTransitions(roleTransitions));
    }

    public static PrismStateAction applicationViewEdit() {
        return new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_VIEW_EDIT) //
                .withAssignments(PrismRole.INSTITUTION_ADMINISTRATOR, PrismActionEnhancement.APPLICATION_VIEW_AS_APPROVER) //
                .withAssignments(PrismRole.INSTITUTION_APPROVER, PrismActionEnhancement.APPLICATION_VIEW_AS_APPROVER) //
                .withAssignments(PrismRole.INSTITUTION_VIEWER, PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PrismRole.DEPARTMENT_ADMINISTRATOR, PrismActionEnhancement.APPLICATION_VIEW_AS_APPROVER) //
                .withAssignments(PrismRole.DEPARTMENT_APPROVER, PrismActionEnhancement.APPLICATION_VIEW_AS_APPROVER) //
                .withAssignments(PrismRole.DEPARTMENT_VIEWER, PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PrismRole.PROGRAM_ADMINISTRATOR, PrismActionEnhancement.APPLICATION_VIEW_AS_APPROVER) //
                .withAssignments(PrismRole.PROGRAM_APPROVER, PrismActionEnhancement.APPLICATION_VIEW_AS_APPROVER) //
                .withAssignments(PrismRole.PROGRAM_VIEWER, PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER) //
                .withAssignments(PrismRole.PROJECT_ADMINISTRATOR, PrismActionEnhancement.APPLICATION_VIEW_AS_APPROVER) //
                .withAssignments(PrismRole.PROJECT_APPROVER, PrismActionEnhancement.APPLICATION_VIEW_AS_APPROVER) //
                .withAssignments(PrismRole.PROJECT_VIEWER, PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER)
                .withAssignments(PrismRole.APPLICATION_CREATOR, PrismActionEnhancement.APPLICATION_VIEW_AS_CREATOR) //
                .withAssignments(PrismRole.APPLICATION_VIEWER_REFEREE, PrismActionEnhancement.APPLICATION_VIEW_AS_REFEREE) //
                .withPartnerAssignments(PrismRole.INSTITUTION_ADMINISTRATOR, PrismActionEnhancement.APPLICATION_VIEW_AS_PARTNER) //
                .withPartnerAssignments(PrismRole.INSTITUTION_APPROVER, PrismActionEnhancement.APPLICATION_VIEW_AS_PARTNER) //
                .withPartnerAssignments(PrismRole.DEPARTMENT_ADMINISTRATOR, PrismActionEnhancement.APPLICATION_VIEW_AS_PARTNER) //
                .withPartnerAssignments(PrismRole.DEPARTMENT_APPROVER, PrismActionEnhancement.APPLICATION_VIEW_AS_PARTNER);
    }

    public static PrismStateAction applicationViewEditWithViewerRecruiter(PrismState state) {
        return applicationViewEdit(state, PrismRoleTransitionGroup.APPLICATION_UPDATE_REFEREE_GROUP.getRoleTransitions()) //
                .withAssignments(PrismRole.APPLICATION_VIEWER_RECRUITER, PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationWithdrawAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_WITHDRAW) //
                .withAssignments(PrismRole.APPLICATION_CREATOR);
    }

    public static PrismStateAction applicationWithdrawSubmitted(PrismRoleGroup notifications, PrismRoleTransitionGroup... roleTransitions) {
        return applicationWithdrawAbstract() //
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_WITHDRAW_TRANSITION //
                        .withRoleTransitions(roleTransitions));
    }

    public static PrismStateAction applicationWithdrawSubmitted(PrismRoleGroup notifications, PrismStateTerminationGroup stateTerminations,
            PrismRoleTransitionGroup... roleTransitions) {
        return applicationWithdrawAbstract() //
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_WITHDRAW_TRANSITION //
                        .withStateTerminationsAndRoleTransitions(stateTerminations, roleTransitions));
    }

    private static PrismStateAction applicationCompleteStateAbstract(PrismAction action, PrismRoleGroup assignees) {
        return new PrismStateAction() //
                .withAction(action) //
                .withAssignments(assignees);
    }

    private static PrismStateAction applicationEscalateAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_ESCALATE);
    }

    private static List<PrismState> applicationNextStateExclusions(PrismState state) {
        List<PrismState> exclusions = Lists.newArrayList();
        if (state.name().equals(state.getStateGroup().name())) {
            exclusions.add(state);
        }
        return exclusions;
    }

}
