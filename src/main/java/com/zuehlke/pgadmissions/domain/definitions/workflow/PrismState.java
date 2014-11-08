package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDuration.APPLICATION_CONFIRM_SUPERVISION_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDuration.APPLICATION_ESCALATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDuration.APPLICATION_INTERVIEW_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDuration.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDuration.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDuration.APPLICATION_PROVIDE_REVIEW_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDuration.APPLICATION_PURGE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDuration.INSTITUTION_ESCALATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDuration.PROGRAM_APPROVE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDuration.PROGRAM_ESCALATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDuration.PROJECT_APPROVE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDuration.PROJECT_ESCALATE_DURATION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApproval;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovalPendingCompletion;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovalPendingFeedback;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApproved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedCompleted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedCompletedPurged;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedPendingExport;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterview;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingAvailability;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingCompletion;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingFeedback;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingInterview;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingScheduling;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationRejected;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationRejectedCompleted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationRejectedCompletedPurged;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationRejectedPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationRejectedPendingExport;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReviewPendingCompletion;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReviewPendingFeedback;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationUnsubmitted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationUnsubmittedPendingCompletion;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWithdrawnCompleted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWithdrawnCompletedPurged;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWithdrawnCompletedUnsubmitted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWithdrawnPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWithdrawnPendingExport;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApproval;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApprovalPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApproved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApprovedCompleted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionRejected;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionWithdrawn;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproval;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApprovalPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramDeactivated;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramDisabledCompleted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramDisabledPendingImportReactivation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramDisabledPendingReactivation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramRejected;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramWithdrawn;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproval;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApprovalPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectDeactivated;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectDisabledCompleted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectDisabledPendingProgramReactivation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectDisabledPendingReactivation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectRejected;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectWithdrawn;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismSystemApproved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismWorkflowState;

public enum PrismState {

    APPLICATION_APPROVAL(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_ESCALATE_DURATION, APPLICATION, PrismApplicationApproval.class), //
    APPLICATION_APPROVAL_PENDING_COMPLETION(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_ESCALATE_DURATION, APPLICATION,
            PrismApplicationApprovalPendingCompletion.class), //
    APPLICATION_APPROVAL_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_CONFIRM_SUPERVISION_DURATION, APPLICATION,
            PrismApplicationApprovalPendingFeedback.class), //
    APPLICATION_APPROVED(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, APPLICATION, PrismApplicationApproved.class), //
    APPLICATION_APPROVED_COMPLETED(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_PURGE_DURATION, APPLICATION, PrismApplicationApprovedCompleted.class), //
    APPLICATION_APPROVED_PENDING_CORRECTION(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, APPLICATION,
            PrismApplicationApprovedPendingCorrection.class), //
    APPLICATION_APPROVED_PENDING_EXPORT(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, APPLICATION,
            PrismApplicationApprovedPendingExport.class), //
    APPLICATION_APPROVED_COMPLETED_PURGED(PrismStateGroup.APPLICATION_APPROVED, null, APPLICATION, PrismApplicationApprovedCompletedPurged.class), //
    APPLICATION_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, APPLICATION, PrismApplicationInterview.class), //
    APPLICATION_INTERVIEW_PENDING_AVAILABILITY(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION, APPLICATION,
            PrismApplicationInterviewPendingAvailability.class), //
    APPLICATION_INTERVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, APPLICATION,
            PrismApplicationInterviewPendingCompletion.class), //
    APPLICATION_INTERVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION, APPLICATION,
            PrismApplicationInterviewPendingFeedback.class), //
    APPLICATION_INTERVIEW_PENDING_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_INTERVIEW_DURATION, APPLICATION,
            PrismApplicationInterviewPendingInterview.class), //
    APPLICATION_INTERVIEW_PENDING_SCHEDULING(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, APPLICATION,
            PrismApplicationInterviewPendingScheduling.class), //
    APPLICATION_REJECTED(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, APPLICATION, PrismApplicationRejected.class), //
    APPLICATION_REJECTED_COMPLETED(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_PURGE_DURATION, APPLICATION, PrismApplicationRejectedCompleted.class), //
    APPLICATION_REJECTED_PENDING_CORRECTION(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, APPLICATION,
            PrismApplicationRejectedPendingCorrection.class), //
    APPLICATION_REJECTED_PENDING_EXPORT(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, APPLICATION,
            PrismApplicationRejectedPendingExport.class), //
    APPLICATION_REJECTED_COMPLETED_PURGED(PrismStateGroup.APPLICATION_REJECTED, null, APPLICATION, PrismApplicationRejectedCompletedPurged.class), //
    APPLICATION_REVIEW(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_ESCALATE_DURATION, APPLICATION, PrismApplicationReview.class), //
    APPLICATION_REVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_ESCALATE_DURATION, APPLICATION,
            PrismApplicationReviewPendingCompletion.class), //
    APPLICATION_REVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_PROVIDE_REVIEW_DURATION, APPLICATION,
            PrismApplicationReviewPendingFeedback.class), //
    APPLICATION_UNSUBMITTED(PrismStateGroup.APPLICATION_UNSUBMITTED, APPLICATION_ESCALATE_DURATION, APPLICATION, PrismApplicationUnsubmitted.class), //
    APPLICATION_UNSUBMITTED_PENDING_COMPLETION(PrismStateGroup.APPLICATION_UNSUBMITTED, APPLICATION_ESCALATE_DURATION, APPLICATION,
            PrismApplicationUnsubmittedPendingCompletion.class), //
    APPLICATION_VALIDATION(PrismStateGroup.APPLICATION_VALIDATION, APPLICATION_ESCALATE_DURATION, APPLICATION, PrismApplicationValidation.class), //
    APPLICATION_WITHDRAWN_PENDING_EXPORT(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_ESCALATE_DURATION, APPLICATION,
            PrismApplicationWithdrawnPendingExport.class), //
    APPLICATION_WITHDRAWN_COMPLETED(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_PURGE_DURATION, APPLICATION, PrismApplicationWithdrawnCompleted.class), //
    APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_PURGE_DURATION, APPLICATION,
            PrismApplicationWithdrawnCompletedUnsubmitted.class), //
    APPLICATION_WITHDRAWN_PENDING_CORRECTION(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_ESCALATE_DURATION, APPLICATION,
            PrismApplicationWithdrawnPendingCorrection.class), //
    APPLICATION_WITHDRAWN_COMPLETED_PURGED(PrismStateGroup.APPLICATION_WITHDRAWN, null, APPLICATION, PrismApplicationWithdrawnCompletedPurged.class), //
    APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_PURGED(PrismStateGroup.APPLICATION_WITHDRAWN, null, APPLICATION, PrismApplicationWithdrawnCompletedPurged.class), //
    INSTITUTION_APPROVAL(PrismStateGroup.INSTITUTION_APPROVAL, INSTITUTION_ESCALATE_DURATION, INSTITUTION, PrismInstitutionApproval.class), //
    INSTITUTION_APPROVAL_PENDING_CORRECTION(PrismStateGroup.INSTITUTION_APPROVAL, INSTITUTION_ESCALATE_DURATION, INSTITUTION,
            PrismInstitutionApprovalPendingCorrection.class), //
    INSTITUTION_APPROVED(PrismStateGroup.INSTITUTION_APPROVED, null, INSTITUTION, PrismInstitutionApproved.class), //
    INSTITUTION_APPROVED_COMPLETED(PrismStateGroup.INSTITUTION_APPROVED, null, INSTITUTION, PrismInstitutionApprovedCompleted.class), //
    INSTITUTION_REJECTED(PrismStateGroup.INSTITUTION_REJECTED, null, INSTITUTION, PrismInstitutionRejected.class), //
    INSTITUTION_WITHDRAWN(PrismStateGroup.INSTITUTION_WITHDRAWN, null, INSTITUTION, PrismInstitutionWithdrawn.class), //
    PROGRAM_APPROVAL(PrismStateGroup.PROGRAM_APPROVAL, PROGRAM_ESCALATE_DURATION, PROGRAM, PrismProgramApproval.class), //
    PROGRAM_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROGRAM_APPROVAL, PROGRAM_ESCALATE_DURATION, PROGRAM, PrismProgramApprovalPendingCorrection.class), //
    PROGRAM_APPROVED(PrismStateGroup.PROGRAM_APPROVED, PROGRAM_APPROVE_DURATION, PROGRAM, PrismProgramApproved.class), //
    PROGRAM_DEACTIVATED(PrismStateGroup.PROGRAM_APPROVED, PROGRAM_APPROVE_DURATION, PROGRAM, PrismProgramDeactivated.class), //
    PROGRAM_DISABLED_COMPLETED(PrismStateGroup.PROGRAM_DISABLED, null, PROGRAM, PrismProgramDisabledCompleted.class), //
    PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION(PrismStateGroup.PROGRAM_DISABLED, PROGRAM_ESCALATE_DURATION, PROGRAM,
            PrismProgramDisabledPendingImportReactivation.class), //
    PROGRAM_DISABLED_PENDING_REACTIVATION(PrismStateGroup.PROGRAM_DISABLED, PROGRAM_ESCALATE_DURATION, PROGRAM, PrismProgramDisabledPendingReactivation.class), //
    PROGRAM_REJECTED(PrismStateGroup.PROGRAM_REJECTED, null, PROGRAM, PrismProgramRejected.class), //
    PROGRAM_WITHDRAWN(PrismStateGroup.PROGRAM_WITHDRAWN, null, PROGRAM, PrismProgramWithdrawn.class), //
    PROJECT_APPROVAL(PrismStateGroup.PROJECT_APPROVAL, APPLICATION_ESCALATE_DURATION, PROJECT, PrismProjectApproval.class), //
    PROJECT_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROJECT_APPROVAL, APPLICATION_ESCALATE_DURATION, PROJECT, PrismProjectApprovalPendingCorrection.class), //
    PROJECT_APPROVED(PrismStateGroup.PROJECT_APPROVED, APPLICATION_ESCALATE_DURATION, PROJECT, PrismProjectApproved.class), //
    PROJECT_DEACTIVATED(PrismStateGroup.PROJECT_APPROVED, PROJECT_APPROVE_DURATION, PROJECT, PrismProjectDeactivated.class), //
    PROJECT_DISABLED_COMPLETED(PrismStateGroup.PROJECT_DISABLED, null, PROJECT, PrismProjectDisabledCompleted.class), //
    PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION(PrismStateGroup.PROJECT_DISABLED, PROJECT_ESCALATE_DURATION, PROJECT,
            PrismProjectDisabledPendingProgramReactivation.class), //
    PROJECT_DISABLED_PENDING_REACTIVATION(PrismStateGroup.PROJECT_DISABLED, PROJECT_ESCALATE_DURATION, PROJECT, PrismProjectDisabledPendingReactivation.class), //
    PROJECT_REJECTED(PrismStateGroup.PROJECT_REJECTED, null, PROJECT, PrismProjectRejected.class), //
    PROJECT_WITHDRAWN(PrismStateGroup.PROJECT_WITHDRAWN, null, PROJECT, PrismProjectWithdrawn.class), //
    SYSTEM_RUNNING(PrismStateGroup.SYSTEM_RUNNING, null, SYSTEM, PrismSystemApproved.class);

    private PrismStateGroup stateGroup;

    private PrismDuration duration;

    private PrismScope scope;

    private Class<? extends PrismWorkflowState> workflowStateClass;

    private static final HashMap<PrismState, PrismWorkflowState> workflowStateDefinitions = Maps.newHashMap();

    static {
        for (PrismState state : PrismState.values()) {
            if (state.getWorkflowStateClassName() != null) {
                PrismWorkflowState workflowState = BeanUtils.instantiate(state.getWorkflowStateClassName());
                workflowStateDefinitions.put(state, workflowState);
            }
        }
    }

    private PrismState(PrismStateGroup stateGroup, PrismDuration duration, PrismScope scope, Class<? extends PrismWorkflowState> workflowStateClass) {
        this.stateGroup = stateGroup;
        this.duration = duration;
        this.scope = scope;
        this.workflowStateClass = workflowStateClass;
    }

    public PrismStateGroup getStateGroup() {
        return stateGroup;
    }

    public final PrismDuration getDuration() {
        return duration;
    }

    public PrismScope getScope() {
        return scope;
    }

    public Class<? extends PrismWorkflowState> getWorkflowStateClassName() {
        return workflowStateClass;
    }

    public static List<PrismStateAction> getStateActions(PrismState state) {
        return workflowStateDefinitions.containsKey(state) ? workflowStateDefinitions.get(state).getStateActions() : new ArrayList<PrismStateAction>();
    }

    public static PrismStateAction getStateAction(PrismState state, PrismAction action) {
        return workflowStateDefinitions.containsKey(state) ? workflowStateDefinitions.get(state).getStateActionsByAction(action) : null;
    }

}
