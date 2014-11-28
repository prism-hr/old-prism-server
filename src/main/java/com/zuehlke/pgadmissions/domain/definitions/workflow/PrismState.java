package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_CONFIRM_ELIGIBILITY_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_CONFIRM_SUPERVISION_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_ESCALATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PROVIDE_REFERENCE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PROVIDE_REVIEW_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PURGE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.INSTITUTION_ESCALATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.PROGRAM_ESCALATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.PROJECT_ESCALATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationEvaluation.APPLICATION_CLOSING_DATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationEvaluation.APPLICATION_INTERVIEW_DATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationEvaluation.PROGRAM_END_DATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationEvaluation.PROJECT_END_DATE;

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
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReference;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReferencePendingCompletion;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidationPendingCompletion;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerificationPendingCompletion;
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

    APPLICATION_UNSUBMITTED(PrismStateGroup.APPLICATION_UNSUBMITTED, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION, PrismApplicationUnsubmitted.class), //
    APPLICATION_UNSUBMITTED_PENDING_COMPLETION(PrismStateGroup.APPLICATION_UNSUBMITTED, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationUnsubmittedPendingCompletion.class), //
    APPLICATION_VALIDATION(PrismStateGroup.APPLICATION_VALIDATION, null, APPLICATION_CLOSING_DATE, false, APPLICATION, PrismApplicationValidation.class), //
    APPLICATION_VALIDATION_PENDING_COMPLETION(PrismStateGroup.APPLICATION_VALIDATION, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationValidationPendingCompletion.class), //
    APPLICATION_VERIFICATION(PrismStateGroup.APPLICATION_VERIFICATION, APPLICATION_CONFIRM_ELIGIBILITY_DURATION, null, true, APPLICATION,
            PrismApplicationVerification.class), //
    APPLICATION_VERIFICATION_PENDING_COMPLETION(PrismStateGroup.APPLICATION_VERIFICATION, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationVerificationPendingCompletion.class), //
    APPLICATION_REFERENCE(PrismStateGroup.APPLICATION_REFERENCE, APPLICATION_PROVIDE_REFERENCE_DURATION, null, true, APPLICATION,
            PrismApplicationReference.class), //
    APPLICATION_REFERENCE_PENDING_COMPLETION(PrismStateGroup.APPLICATION_REFERENCE, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationReferencePendingCompletion.class), //
    APPLICATION_REVIEW(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION, PrismApplicationReview.class), //
    APPLICATION_REVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_PROVIDE_REVIEW_DURATION, null, false, APPLICATION,
            PrismApplicationReviewPendingFeedback.class), //
    APPLICATION_REVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationReviewPendingCompletion.class), //
    APPLICATION_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION, PrismApplicationInterview.class), //
    APPLICATION_INTERVIEW_PENDING_AVAILABILITY(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION, null, false,
            APPLICATION, PrismApplicationInterviewPendingAvailability.class), //
    APPLICATION_INTERVIEW_PENDING_SCHEDULING(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationInterviewPendingScheduling.class), //
    APPLICATION_INTERVIEW_PENDING_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, null, APPLICATION_INTERVIEW_DATE, false, APPLICATION,
            PrismApplicationInterviewPendingInterview.class), //
    APPLICATION_INTERVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION, null, false, APPLICATION,
            PrismApplicationInterviewPendingFeedback.class), //
    APPLICATION_INTERVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationInterviewPendingCompletion.class), //
    APPLICATION_APPROVAL(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION, PrismApplicationApproval.class), //
    APPLICATION_APPROVAL_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_CONFIRM_SUPERVISION_DURATION, null, false, APPLICATION,
            PrismApplicationApprovalPendingFeedback.class), //
    APPLICATION_APPROVAL_PENDING_COMPLETION(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationApprovalPendingCompletion.class), //
    APPLICATION_APPROVED(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION, PrismApplicationApproved.class), //
    APPLICATION_APPROVED_PENDING_EXPORT(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationApprovedPendingExport.class), //
    APPLICATION_APPROVED_PENDING_CORRECTION(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationApprovedPendingCorrection.class), //
    APPLICATION_APPROVED_COMPLETED(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_PURGE_DURATION, null, false, APPLICATION,
            PrismApplicationApprovedCompleted.class), //
    APPLICATION_APPROVED_COMPLETED_PURGED(PrismStateGroup.APPLICATION_APPROVED, null, null, false, APPLICATION, PrismApplicationApprovedCompletedPurged.class), //
    APPLICATION_REJECTED(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION, PrismApplicationRejected.class), //
    APPLICATION_REJECTED_PENDING_EXPORT(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationRejectedPendingExport.class), //
    APPLICATION_REJECTED_PENDING_CORRECTION(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationRejectedPendingCorrection.class), //
    APPLICATION_REJECTED_COMPLETED(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_PURGE_DURATION, null, false, APPLICATION,
            PrismApplicationRejectedCompleted.class), //
    APPLICATION_REJECTED_COMPLETED_PURGED(PrismStateGroup.APPLICATION_REJECTED, null, null, false, APPLICATION, PrismApplicationRejectedCompletedPurged.class), //
    APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_PURGE_DURATION, null, false, APPLICATION,
            PrismApplicationWithdrawnCompletedUnsubmitted.class), //
    APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_PURGED(PrismStateGroup.APPLICATION_WITHDRAWN, null, null, false, APPLICATION,
            PrismApplicationWithdrawnCompletedPurged.class), //
    APPLICATION_WITHDRAWN_PENDING_EXPORT(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationWithdrawnPendingExport.class), //
    APPLICATION_WITHDRAWN_PENDING_CORRECTION(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_ESCALATE_DURATION, null, false, APPLICATION,
            PrismApplicationWithdrawnPendingCorrection.class), //
    APPLICATION_WITHDRAWN_COMPLETED(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_PURGE_DURATION, null, false, APPLICATION,
            PrismApplicationWithdrawnCompleted.class), //
    APPLICATION_WITHDRAWN_COMPLETED_PURGED(PrismStateGroup.APPLICATION_WITHDRAWN, null, null, false, APPLICATION,
            PrismApplicationWithdrawnCompletedPurged.class), //
    PROJECT_APPROVAL(PrismStateGroup.PROJECT_APPROVAL, PROJECT_ESCALATE_DURATION, null, false, PROJECT, PrismProjectApproval.class), //
    PROJECT_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROJECT_APPROVAL, PROJECT_ESCALATE_DURATION, null, false, PROJECT,
            PrismProjectApprovalPendingCorrection.class), //
    PROJECT_APPROVED(PrismStateGroup.PROJECT_APPROVED, null, PROJECT_END_DATE, false, PROJECT, PrismProjectApproved.class), //
    PROJECT_DEACTIVATED(PrismStateGroup.PROJECT_APPROVED, null, PROJECT_END_DATE, false, PROJECT, PrismProjectDeactivated.class), //
    PROJECT_DISABLED_PENDING_REACTIVATION(PrismStateGroup.PROJECT_DISABLED, PROJECT_ESCALATE_DURATION, null, false, PROJECT,
            PrismProjectDisabledPendingReactivation.class), //
    PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION(PrismStateGroup.PROJECT_DISABLED, PROJECT_ESCALATE_DURATION, null, false, PROJECT,
            PrismProjectDisabledPendingProgramReactivation.class), //
    PROJECT_DISABLED_COMPLETED(PrismStateGroup.PROJECT_DISABLED, null, null, false, PROJECT, PrismProjectDisabledCompleted.class), //
    PROJECT_REJECTED(PrismStateGroup.PROJECT_REJECTED, null, null, false, PROJECT, PrismProjectRejected.class), //
    PROJECT_WITHDRAWN(PrismStateGroup.PROJECT_WITHDRAWN, null, null, false, PROJECT, PrismProjectWithdrawn.class), //
    PROGRAM_APPROVAL(PrismStateGroup.PROGRAM_APPROVAL, PROGRAM_ESCALATE_DURATION, null, false, PROGRAM, PrismProgramApproval.class), //
    PROGRAM_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROGRAM_APPROVAL, PROGRAM_ESCALATE_DURATION, null, false, PROGRAM,
            PrismProgramApprovalPendingCorrection.class), //
    PROGRAM_APPROVED(PrismStateGroup.PROGRAM_APPROVED, null, PROGRAM_END_DATE, false, PROGRAM, PrismProgramApproved.class), //
    PROGRAM_DEACTIVATED(PrismStateGroup.PROGRAM_APPROVED, null, PROGRAM_END_DATE, false, PROGRAM, PrismProgramDeactivated.class), //
    PROGRAM_DISABLED_PENDING_REACTIVATION(PrismStateGroup.PROGRAM_DISABLED, PROGRAM_ESCALATE_DURATION, null, false, PROGRAM,
            PrismProgramDisabledPendingReactivation.class), //
    PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION(PrismStateGroup.PROGRAM_DISABLED, PROGRAM_ESCALATE_DURATION, null, false, PROGRAM,
            PrismProgramDisabledPendingImportReactivation.class), //
    PROGRAM_DISABLED_COMPLETED(PrismStateGroup.PROGRAM_DISABLED, null, null, false, PROGRAM, PrismProgramDisabledCompleted.class), //
    PROGRAM_REJECTED(PrismStateGroup.PROGRAM_REJECTED, null, null, false, PROGRAM, PrismProgramRejected.class), //
    PROGRAM_WITHDRAWN(PrismStateGroup.PROGRAM_WITHDRAWN, null, null, false, PROGRAM, PrismProgramWithdrawn.class), //
    INSTITUTION_APPROVAL(PrismStateGroup.INSTITUTION_APPROVAL, INSTITUTION_ESCALATE_DURATION, null, false, INSTITUTION, PrismInstitutionApproval.class), //
    INSTITUTION_APPROVAL_PENDING_CORRECTION(PrismStateGroup.INSTITUTION_APPROVAL, INSTITUTION_ESCALATE_DURATION, null, false, INSTITUTION,
            PrismInstitutionApprovalPendingCorrection.class), //
    INSTITUTION_APPROVED(PrismStateGroup.INSTITUTION_APPROVED, null, null, false, INSTITUTION, PrismInstitutionApproved.class), //
    INSTITUTION_APPROVED_COMPLETED(PrismStateGroup.INSTITUTION_APPROVED, null, null, false, INSTITUTION, PrismInstitutionApprovedCompleted.class), //
    INSTITUTION_REJECTED(PrismStateGroup.INSTITUTION_REJECTED, null, null, false, INSTITUTION, PrismInstitutionRejected.class), //
    INSTITUTION_WITHDRAWN(PrismStateGroup.INSTITUTION_WITHDRAWN, null, null, false, INSTITUTION, PrismInstitutionWithdrawn.class), //
    SYSTEM_RUNNING(PrismStateGroup.SYSTEM_RUNNING, null, null, false, SYSTEM, PrismSystemApproved.class);

    private PrismStateGroup stateGroup;

    private PrismStateDurationDefinition defaultDuration;

    private PrismStateDurationEvaluation stateDurationEvaluation;

    private boolean parallelizable;

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
            boolean parallelizable, PrismScope scope, Class<? extends PrismWorkflowState> workflowStateClass) {
        this.stateGroup = stateGroup;
        this.defaultDuration = defaultDuration;
        this.stateDurationEvaluation = stateDurationEvaluation;
        this.parallelizable = parallelizable;
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
