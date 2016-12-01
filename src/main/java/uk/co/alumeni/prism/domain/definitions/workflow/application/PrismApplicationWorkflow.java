package uk.co.alumeni.prism.domain.definitions.workflow.application;

import com.google.common.collect.Lists;
import uk.co.alumeni.prism.domain.definitions.workflow.*;

import java.util.List;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_UPDATE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationEvaluation.APPLICATION_REFERENCED_TERMINATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.*;

public class PrismApplicationWorkflow {

    public static PrismStateAction applicationComment() {
        return new PrismStateAction() //
                .withAction(APPLICATION_COMMENT)
                .withStateActionAssignments(APPLICATION_PARENT_VIEWER_GROUP) //
                .withStateActionAssignments(APPLICATION_VIEWER_REFEREE) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP)
                .withPartnerStateActionAssignments(DEPARTMENT_STAFF_GROUP)
                .withPartnerStateActionAssignments(INSTITUTION_STAFF_GROUP); //
    }

    public static PrismStateAction applicationCommentViewerReferee() {
        return applicationComment()
                .withStateActionAssignments(APPLICATION_VIEWER_REFEREE);
    }

    public static PrismStateAction applicationCommentViewerRefereeViewerRecruiter() {
        return applicationCommentViewerReferee() //
                .withStateActionAssignments(APPLICATION_VIEWER_RECRUITER);
    }

    public static PrismStateAction applicationCompleteState(PrismAction action, PrismState state, PrismRoleGroup assignees) {
        return applicationCompleteStateAbstract(action, assignees)
                .withStateTransitions(APPLICATION_COMPLETE_STATE_TRANSITION //
                        .withExclusions(applicationNextStateExclusions(state)));
    }

    public static PrismStateAction applicationCompleteState(PrismAction action, PrismState state, PrismRoleGroup assignees,
            PrismRoleTransitionGroup... roleTransitions) {
        return applicationCompleteStateAbstract(action, assignees)
                .withStateTransitions(APPLICATION_COMPLETE_STATE_TRANSITION //
                        .withRoleTransitionsAndExclusions(applicationNextStateExclusions(state), roleTransitions));
    }

    public static PrismStateAction applicationSendMessage() {
        return new PrismStateAction() //
                .withAction(APPLICATION_SEND_MESSAGE) //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_CREATOR) //
                .withStateActionAssignments(APPLICATION_PARENT_VIEWER_GROUP, APPLICATION_PARENT_VIEWER_GROUP) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, APPLICATION_CREATOR) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withPartnerStateActionAssignments(DEPARTMENT_STAFF_GROUP, APPLICATION_CREATOR) //
                .withPartnerStateActionAssignments(INSTITUTION_STAFF_GROUP, APPLICATION_CREATOR) //
                .withPartnerStateActionRecipientAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP, PARTNERSHIP_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction applicationSendMessageViewerReferee() {
        return applicationSendMessage()
                .withStateActionAssignment(APPLICATION_VIEWER_REFEREE, APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_VIEWER_REFEREE);
    }

    public static PrismStateAction applicationSendMessageViewerRefereeViewerRecruiter() {
        return applicationSendMessageViewerReferee() //
                .withStateActionAssignment(APPLICATION_VIEWER_RECRUITER, APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_VIEWER_RECRUITER);
    }

    public static PrismStateAction applicationEscalate(PrismState state) {
        return applicationEscalateAbstract() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_ESCALATE));
    }

    public static PrismStateAction applicationEscalate(PrismRoleTransitionGroup... roleTransitions) {
        return applicationEscalateAbstract() //
                .withStateTransitions(APPLICATION_ESCALATE_TRANSITION //
                        .withRoleTransitions(roleTransitions)); //
    }

    public static PrismStateAction applicationEscalate(PrismState state, PrismRoleTransitionGroup... roleTransitions) {
        return applicationEscalateAbstract() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_ESCALATE) //
                        .withRoleTransitions(roleTransitions));
    }

    public static PrismStateAction applicationUploadReference(PrismState state) {
        return new PrismStateAction() //
                .withAction(APPLICATION_UPLOAD_REFERENCE) //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_UPLOAD_REFERENCE) //
                        .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP) //
                        .withStateTerminations(new PrismStateTermination() //
                                .withTerminationState(APPLICATION_REFERENCE) //
                                .withStateTerminationEvaluation(APPLICATION_REFERENCED_TERMINATION)));
    }

    public static PrismStateAction applicationViewEdit() {
        return new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_VIEW_EDIT) //
                .withStateActionAssignments(INSTITUTION_ADMINISTRATOR, APPLICATION_VIEW_AS_APPROVER) //
                .withStateActionAssignments(INSTITUTION_APPROVER, APPLICATION_VIEW_AS_APPROVER) //
                .withStateActionAssignments(INSTITUTION_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withStateActionAssignments(DEPARTMENT_ADMINISTRATOR, APPLICATION_VIEW_AS_APPROVER) //
                .withStateActionAssignments(DEPARTMENT_APPROVER, APPLICATION_VIEW_AS_APPROVER) //
                .withStateActionAssignments(DEPARTMENT_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withStateActionAssignments(PROGRAM_ADMINISTRATOR, APPLICATION_VIEW_AS_APPROVER) //
                .withStateActionAssignments(PROGRAM_APPROVER, APPLICATION_VIEW_AS_APPROVER) //
                .withStateActionAssignments(PROGRAM_VIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withStateActionAssignments(PROJECT_ADMINISTRATOR, APPLICATION_VIEW_AS_APPROVER) //
                .withStateActionAssignments(PROJECT_APPROVER, APPLICATION_VIEW_AS_APPROVER) //
                .withStateActionAssignments(PROJECT_VIEWER, APPLICATION_VIEW_AS_RECRUITER)
                .withStateActionAssignments(APPLICATION_CREATOR, APPLICATION_VIEW_AS_CREATOR) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, APPLICATION_VIEW_AS_PARTNER) //
                .withPartnerStateActionAssignments(DEPARTMENT_STAFF_GROUP, APPLICATION_VIEW_AS_PARTNER) //
                .withPartnerStateActionAssignments(INSTITUTION_STAFF_GROUP, APPLICATION_VIEW_AS_PARTNER);
    }

    public static PrismStateAction applicationViewEdit(PrismState state) {
        return applicationViewEdit(state, APPLICATION_UPDATE_REFEREE_GROUP.getRoleTransitions());
    }

    public static PrismStateAction applicationViewEdit(PrismState state, PrismRoleTransition... roleTransitions) {
        return applicationViewEdit(applicationViewEdit(), state, roleTransitions);
    }

    public static PrismStateAction applicationViewEditViewerReferee() {
        return applicationViewEdit()
                .withStateActionAssignments(APPLICATION_VIEWER_REFEREE);
    }

    public static PrismStateAction applicationViewEditViewerReferee(PrismState state, PrismRoleTransition... roleTransitions) {
        return applicationViewEdit(applicationViewEditViewerReferee(), state, roleTransitions);
    }

    public static PrismStateAction applicationViewEditViewerRefereeViewerRecruiter(PrismState state) {
        return applicationViewEditViewerReferee(state, APPLICATION_UPDATE_REFEREE_GROUP.getRoleTransitions()) //
                .withStateActionAssignments(APPLICATION_VIEWER_RECRUITER, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationWithdrawAbstract() {
        return new PrismStateAction() //
                .withAction(APPLICATION_WITHDRAW) //
                .withStateActionAssignments(APPLICATION_CREATOR);
    }

    public static PrismStateAction applicationWithdrawSubmitted(PrismRoleGroup notifications, PrismRoleTransitionGroup... roleTransitions) {
        return applicationWithdrawAbstract() //
                .withStateTransitions(APPLICATION_WITHDRAW_TRANSITION //
                        .withRoleTransitions(roleTransitions));
    }

    public static PrismStateAction applicationWithdrawSubmitted(PrismRoleGroup notifications, PrismStateTerminationGroup stateTerminations,
            PrismRoleTransitionGroup... roleTransitions) {
        return applicationWithdrawAbstract() //
                .withStateTransitions(APPLICATION_WITHDRAW_TRANSITION //
                        .withStateTerminationsAndRoleTransitions(stateTerminations, roleTransitions));
    }

    private static PrismStateAction applicationCompleteStateAbstract(PrismAction action, PrismRoleGroup assignees) {
        return new PrismStateAction() //
                .withAction(action) //
                .withReplicableSequenceStart() //
                .withStateActionAssignments(assignees);
    }

    private static PrismStateAction applicationEscalateAbstract() {
        return new PrismStateAction() //
                .withAction(APPLICATION_ESCALATE);
    }

    private static List<PrismState> applicationNextStateExclusions(PrismState state) {
        List<PrismState> exclusions = Lists.newArrayList();
        if (state.name().equals(state.getStateGroup().name())) {
            exclusions.add(state);
        }
        return exclusions;
    }

    public static PrismStateAction applicationViewEdit(PrismStateAction stateAction, PrismState state, PrismRoleTransition... roleTransitions) {
        return stateAction.withStateTransitions(new PrismStateTransition() //
                .withTransitionState(state) //
                .withTransitionAction(APPLICATION_VIEW_EDIT) //
                .withRoleTransitions(roleTransitions));
    }

}
