package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.*;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationEvaluation.*;

public enum PrismState {

    APPLICATION_UNSUBMITTED(PrismStateGroup.APPLICATION_UNSUBMITTED, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationUnsubmitted.class), //
    APPLICATION_UNSUBMITTED_PENDING_COMPLETION(PrismStateGroup.APPLICATION_UNSUBMITTED, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationUnsubmittedPendingCompletion.class), //
    APPLICATION_VALIDATION(PrismStateGroup.APPLICATION_VALIDATION, null, APPLICATION_CLOSING_DATE, false, false, APPLICATION, PrismApplicationValidation.class), //
    APPLICATION_VALIDATION_PENDING_COMPLETION(PrismStateGroup.APPLICATION_VALIDATION, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationValidationPendingCompletion.class), //
    APPLICATION_VERIFICATION(PrismStateGroup.APPLICATION_VERIFICATION, APPLICATION_CONFIRM_ELIGIBILITY_DURATION, null, true, false, APPLICATION,
            PrismApplicationVerification.class), //
    APPLICATION_VERIFICATION_PENDING_COMPLETION(PrismStateGroup.APPLICATION_VERIFICATION, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationVerificationPendingCompletion.class), //
    APPLICATION_REFERENCE(PrismStateGroup.APPLICATION_REFERENCE, APPLICATION_PROVIDE_REFERENCE_DURATION, null, true, false, APPLICATION,
            PrismApplicationReference.class), //
    APPLICATION_REFERENCE_PENDING_COMPLETION(PrismStateGroup.APPLICATION_REFERENCE, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationReferencePendingCompletion.class), //
    APPLICATION_REVIEW(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION, PrismApplicationReview.class), //
    APPLICATION_REVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_PROVIDE_REVIEW_DURATION, null, false, false, APPLICATION,
            PrismApplicationReviewPendingFeedback.class), //
    APPLICATION_REVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationReviewPendingCompletion.class), //
    APPLICATION_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationInterview.class), //
    APPLICATION_INTERVIEW_PENDING_AVAILABILITY(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION, null, false, false,
            APPLICATION, PrismApplicationInterviewPendingAvailability.class), //
    APPLICATION_INTERVIEW_PENDING_SCHEDULING(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationInterviewPendingScheduling.class), //
    APPLICATION_INTERVIEW_PENDING_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, null, APPLICATION_INTERVIEW_DATE, false, false, APPLICATION,
            PrismApplicationInterviewPendingInterview.class), //
    APPLICATION_INTERVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION, null, false, false,
            APPLICATION, PrismApplicationInterviewPendingFeedback.class), //
    APPLICATION_INTERVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationInterviewPendingCompletion.class), //
    APPLICATION_APPROVAL(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION, PrismApplicationApproval.class), //
    APPLICATION_APPROVAL_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_CONFIRM_SUPERVISION_DURATION, null, false, false, APPLICATION,
            PrismApplicationApprovalPendingFeedback.class), //
    APPLICATION_APPROVAL_PENDING_COMPLETION(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationApprovalPendingCompletion.class), //
    APPLICATION_APPROVED(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION, PrismApplicationApproved.class), //
    APPLICATION_APPROVED_PENDING_EXPORT(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationApprovedPendingExport.class), //
    APPLICATION_APPROVED_PENDING_CORRECTION(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationApprovedPendingCorrection.class), //
    APPLICATION_APPROVED_COMPLETED(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_PURGE_DURATION, null, false, false, APPLICATION,
            PrismApplicationApprovedCompleted.class), //
    APPLICATION_APPROVED_COMPLETED_PURGED(PrismStateGroup.APPLICATION_APPROVED, null, null, false, true, APPLICATION,
            PrismApplicationApprovedCompletedPurged.class), //
    APPLICATION_REJECTED(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION, PrismApplicationRejected.class), //
    APPLICATION_REJECTED_PENDING_EXPORT(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationRejectedPendingExport.class), //
    APPLICATION_REJECTED_PENDING_CORRECTION(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationRejectedPendingCorrection.class), //
    APPLICATION_REJECTED_COMPLETED(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_PURGE_DURATION, null, false, false, APPLICATION,
            PrismApplicationRejectedCompleted.class), //
    APPLICATION_REJECTED_COMPLETED_PURGED(PrismStateGroup.APPLICATION_REJECTED, null, null, false, true, APPLICATION,
            PrismApplicationRejectedCompletedPurged.class), //
    APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_PURGE_DURATION, null, false, false, APPLICATION,
            PrismApplicationWithdrawnCompletedUnsubmitted.class), //
    APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_PURGED(PrismStateGroup.APPLICATION_WITHDRAWN, null, null, false, false, APPLICATION,
            PrismApplicationWithdrawnCompletedPurged.class), //
    APPLICATION_WITHDRAWN_PENDING_EXPORT(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationWithdrawnPendingExport.class), //
    APPLICATION_WITHDRAWN_PENDING_CORRECTION(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_ESCALATE_DURATION, null, false, false, APPLICATION,
            PrismApplicationWithdrawnPendingCorrection.class), //
    APPLICATION_WITHDRAWN_COMPLETED(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_PURGE_DURATION, null, false, false, APPLICATION,
            PrismApplicationWithdrawnCompleted.class), //
    APPLICATION_WITHDRAWN_COMPLETED_PURGED(PrismStateGroup.APPLICATION_WITHDRAWN, null, null, false, true, APPLICATION,
            PrismApplicationWithdrawnCompletedPurged.class), //
    PROJECT_APPROVAL(PrismStateGroup.PROJECT_APPROVAL, PROJECT_ESCALATE_DURATION, null, false, false, PROJECT, PrismProjectApproval.class), //
    PROJECT_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROJECT_APPROVAL, PROJECT_ESCALATE_DURATION, null, false, false, PROJECT,
            PrismProjectApprovalPendingCorrection.class), //
    PROJECT_APPROVED(PrismStateGroup.PROJECT_APPROVED, null, PROJECT_END_DATE, false, false, PROJECT, PrismProjectApproved.class), //
    PROJECT_DEACTIVATED(PrismStateGroup.PROJECT_APPROVED, null, PROJECT_END_DATE, false, false, PROJECT, PrismProjectDeactivated.class), //
    PROJECT_DISABLED_PENDING_REACTIVATION(PrismStateGroup.PROJECT_DISABLED, PROJECT_ESCALATE_DURATION, null, false, false, PROJECT,
            PrismProjectDisabledPendingReactivation.class), //
    PROJECT_DISABLED_COMPLETED(PrismStateGroup.PROJECT_DISABLED, null, null, false, false, PROJECT, PrismProjectDisabledCompleted.class), //
    PROJECT_REJECTED(PrismStateGroup.PROJECT_REJECTED, null, null, false, false, PROJECT, PrismProjectRejected.class), //
    PROJECT_WITHDRAWN(PrismStateGroup.PROJECT_WITHDRAWN, null, null, false, false, PROJECT, PrismProjectWithdrawn.class), //
    PROGRAM_APPROVAL(PrismStateGroup.PROGRAM_APPROVAL, PROGRAM_ESCALATE_DURATION, null, false, false, PROGRAM, PrismProgramApproval.class), //
    PROGRAM_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROGRAM_APPROVAL, PROGRAM_ESCALATE_DURATION, null, false, false, PROGRAM,
            PrismProgramApprovalPendingCorrection.class), //
    PROGRAM_APPROVED(PrismStateGroup.PROGRAM_APPROVED, null, PROGRAM_END_DATE, false, false, PROGRAM, PrismProgramApproved.class), //
    PROGRAM_DEACTIVATED(PrismStateGroup.PROGRAM_APPROVED, null, PROGRAM_END_DATE, false, false, PROGRAM, PrismProgramDeactivated.class), //
    PROGRAM_DISABLED_PENDING_REACTIVATION(PrismStateGroup.PROGRAM_DISABLED, PROGRAM_ESCALATE_DURATION, null, false, false, PROGRAM,
            PrismProgramDisabledPendingReactivation.class), //
    PROGRAM_DISABLED_COMPLETED(PrismStateGroup.PROGRAM_DISABLED, null, null, false, false, PROGRAM, PrismProgramDisabledCompleted.class), //
    PROGRAM_REJECTED(PrismStateGroup.PROGRAM_REJECTED, null, null, false, false, PROGRAM, PrismProgramRejected.class), //
    PROGRAM_WITHDRAWN(PrismStateGroup.PROGRAM_WITHDRAWN, null, null, false, false, PROGRAM, PrismProgramWithdrawn.class), //
    INSTITUTION_APPROVAL(PrismStateGroup.INSTITUTION_APPROVAL, INSTITUTION_ESCALATE_DURATION, null, false, false, INSTITUTION, PrismInstitutionApproval.class), //
    INSTITUTION_APPROVAL_PENDING_CORRECTION(PrismStateGroup.INSTITUTION_APPROVAL, INSTITUTION_ESCALATE_DURATION, null, false, false, INSTITUTION,
            PrismInstitutionApprovalPendingCorrection.class), //
    INSTITUTION_APPROVED(PrismStateGroup.INSTITUTION_APPROVED, null, null, false, false, INSTITUTION, PrismInstitutionApproved.class), //
    INSTITUTION_APPROVED_COMPLETED(PrismStateGroup.INSTITUTION_APPROVED, null, null, false, false, INSTITUTION, PrismInstitutionApprovedCompleted.class), //
    INSTITUTION_REJECTED(PrismStateGroup.INSTITUTION_REJECTED, null, null, false, false, INSTITUTION, PrismInstitutionRejected.class), //
    INSTITUTION_WITHDRAWN(PrismStateGroup.INSTITUTION_WITHDRAWN, null, null, false, false, INSTITUTION, PrismInstitutionWithdrawn.class), //
    SYSTEM_RUNNING(PrismStateGroup.SYSTEM_RUNNING, null, null, false, false, SYSTEM, PrismSystemApproved.class);

    private PrismStateGroup stateGroup;

    private PrismStateDurationDefinition defaultDuration;

    private PrismStateDurationEvaluation stateDurationEvaluation;

    private boolean parallelizable;

    private boolean hidden;

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

    private PrismState(PrismStateGroup stateGroup, PrismStateDurationDefinition defaultDuration, PrismStateDurationEvaluation stateDurationEvaluation,
            boolean parallelizable, boolean hidden, PrismScope scope, Class<? extends PrismWorkflowState> workflowStateClass) {
        this.stateGroup = stateGroup;
        this.defaultDuration = defaultDuration;
        this.stateDurationEvaluation = stateDurationEvaluation;
        this.parallelizable = parallelizable;
        this.hidden = hidden;
        this.scope = scope;
        this.workflowStateClass = workflowStateClass;
    }

    public PrismStateGroup getStateGroup() {
        return stateGroup;
    }

    public final PrismStateDurationDefinition getDefaultDuration() {
        return defaultDuration;
    }

    public final PrismStateDurationEvaluation getStateDurationEvaluation() {
        return stateDurationEvaluation;
    }

    public final boolean isParallelizable() {
        return parallelizable;
    }

    public final boolean isHidden() {
        return hidden;
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
