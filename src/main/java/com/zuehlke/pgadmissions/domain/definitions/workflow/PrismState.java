package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApproval;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovalPendingCompletion;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovalPendingFeedback;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApproved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApprovedCompleted;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationRejectedPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationRejectedPendingExport;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReviewPendingCompletion;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReviewPendingFeedback;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationUnsubmitted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationUnsubmittedPendingCompletion;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidationPendingCompletion;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidationPendingFeedback;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWithdrawn;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWithdrawnCompleted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApprovalPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApproved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproval;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramDeactivated;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramDisabled;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramDisabledCompleted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramDisabledPendingImportReactivation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramDisabledPendingReactivation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramRejected;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramWithdrawn;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectDeactivated;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectDisabled;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectDisabledCompleted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectDisabledPendingProgramReactivation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectDisabledPendingReactivation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismSystemApproved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismWithdrawnPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismWorkflowState;

public enum PrismState {

    APPLICATION_APPROVAL(5, null, PrismScope.APPLICATION, PrismApplicationApproval.class), //
    APPLICATION_APPROVAL_PENDING_COMPLETION(null, null, PrismScope.APPLICATION, PrismApplicationApprovalPendingCompletion.class), //
    APPLICATION_APPROVAL_PENDING_FEEDBACK(null, 7, PrismScope.APPLICATION, PrismApplicationApprovalPendingFeedback.class), //
    APPLICATION_APPROVED(6, null, PrismScope.APPLICATION, PrismApplicationApproved.class), //
    APPLICATION_APPROVED_COMPLETED(null, null, PrismScope.APPLICATION, PrismApplicationApprovedCompleted.class), //
    APPLICATION_APPROVED_PENDING_CORRECTION(null, null, PrismScope.APPLICATION, PrismApplicationApprovedPendingCorrection.class), //
    APPLICATION_APPROVED_PENDING_EXPORT(null, null, PrismScope.APPLICATION, PrismApplicationApprovedPendingExport.class), //
    APPLICATION_INTERVIEW(4, null, PrismScope.APPLICATION, PrismApplicationInterview.class), //
    APPLICATION_INTERVIEW_PENDING_AVAILABILITY(null, 3, PrismScope.APPLICATION, PrismApplicationInterviewPendingAvailability.class), //
    APPLICATION_INTERVIEW_PENDING_COMPLETION(null, null, PrismScope.APPLICATION, PrismApplicationInterviewPendingCompletion.class), //
    APPLICATION_INTERVIEW_PENDING_FEEDBACK(null, 7, PrismScope.APPLICATION, PrismApplicationInterviewPendingFeedback.class), //
    APPLICATION_INTERVIEW_PENDING_INTERVIEW(null, null, PrismScope.APPLICATION, PrismApplicationInterviewPendingInterview.class), //
    APPLICATION_INTERVIEW_PENDING_SCHEDULING(null, null, PrismScope.APPLICATION, PrismApplicationInterviewPendingScheduling.class), //
    APPLICATION_REJECTED(7, null, PrismScope.APPLICATION, PrismApplicationRejected.class), //
    APPLICATION_REJECTED_COMPLETED(null, null, PrismScope.APPLICATION, PrismApplicationRejectedCompleted.class), //
    APPLICATION_REJECTED_PENDING_CORRECTION(null, null, PrismScope.APPLICATION, PrismApplicationRejectedPendingCorrection.class), //
    APPLICATION_REJECTED_PENDING_EXPORT(null, null, PrismScope.APPLICATION, PrismApplicationRejectedPendingExport.class), //
    APPLICATION_REVIEW(3, null, PrismScope.APPLICATION, PrismApplicationReview.class), //
    APPLICATION_REVIEW_PENDING_COMPLETION(null, null, PrismScope.APPLICATION, PrismApplicationReviewPendingCompletion.class), //
    APPLICATION_REVIEW_PENDING_FEEDBACK(null, 7, PrismScope.APPLICATION, PrismApplicationReviewPendingFeedback.class), //
    APPLICATION_UNSUBMITTED(1, 28, PrismScope.APPLICATION, PrismApplicationUnsubmitted.class), //
    APPLICATION_UNSUBMITTED_PENDING_COMPLETION(null, null, PrismScope.APPLICATION, PrismApplicationUnsubmittedPendingCompletion.class), //
    APPLICATION_VALIDATION(2, null, PrismScope.APPLICATION, PrismApplicationValidation.class), //
    APPLICATION_VALIDATION_PENDING_COMPLETION(null, null, PrismScope.APPLICATION, PrismApplicationValidationPendingCompletion.class), //
    APPLICATION_VALIDATION_PENDING_FEEDBACK(null, 3, PrismScope.APPLICATION, PrismApplicationValidationPendingFeedback.class), //
    APPLICATION_WITHDRAWN(0, null, PrismScope.APPLICATION, PrismApplicationWithdrawn.class), //
    APPLICATION_WITHDRAWN_COMPLETED(null, null, PrismScope.APPLICATION, PrismApplicationWithdrawnCompleted.class), //
    APPLICATION_WITHDRAWN_PENDING_CORRECTION(null, null, PrismScope.APPLICATION, PrismWithdrawnPendingCorrection.class), //
    INSTITUTION_APPROVED(1, null, PrismScope.INSTITUTION, PrismInstitutionApproved.class), //
    PROGRAM_APPROVAL(1, null, PrismScope.PROGRAM, PrismProgramApproval.class), //
    PROGRAM_APPROVAL_PENDING_CORRECTION(null, null, PrismScope.PROGRAM, PrismApprovalPendingCorrection.class), //
    PROGRAM_APPROVED(2, null, PrismScope.PROGRAM, PrismProgramApproved.class), //
    PROGRAM_DEACTIVATED(null, null, PrismScope.PROGRAM, PrismProgramDeactivated.class), //
    PROGRAM_DISABLED(4, 28, PrismScope.PROGRAM, PrismProgramDisabled.class), //
    PROGRAM_DISABLED_COMPLETED(null, null, PrismScope.PROGRAM, PrismProgramDisabledCompleted.class), //
    PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION(null, 28, PrismScope.PROGRAM, PrismProgramDisabledPendingImportReactivation.class), //
    PROGRAM_DISABLED_PENDING_REACTIVATION(null, 28, PrismScope.PROGRAM, PrismProgramDisabledPendingReactivation.class), //
    PROGRAM_REJECTED(3, null, PrismScope.PROGRAM, PrismProgramRejected.class), //
    PROGRAM_WITHDRAWN(0, null, PrismScope.PROGRAM, PrismProgramWithdrawn.class), //
    PROJECT_APPROVED(1, null, PrismScope.PROJECT, PrismProjectApproved.class), //
    PROJECT_DEACTIVATED(null, null, PrismScope.PROJECT, PrismProjectDeactivated.class), //
    PROJECT_DISABLED(2, 28, PrismScope.PROJECT, PrismProjectDisabled.class), //
    PROJECT_DISABLED_COMPLETED(null, null, PrismScope.PROJECT, PrismProjectDisabledCompleted.class), //
    PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION(null, null, PrismScope.PROJECT, PrismProjectDisabledPendingProgramReactivation.class), //
    PROJECT_DISABLED_PENDING_REACTIVATION(null, 28, PrismScope.PROJECT, PrismProjectDisabledPendingReactivation.class), //
    SYSTEM_APPROVED(1, null, PrismScope.SYSTEM, PrismSystemApproved.class);

    private Integer sequenceOrder;

    private Integer duration;

    private PrismScope scope;

    private Class<? extends PrismWorkflowState> workflowStateClass;

    private static final HashMap<PrismState, PrismState> parentState = Maps.newHashMap();

    private static final HashMap<PrismState, PrismWorkflowState> workflowStateDefinitions = Maps.newHashMap();

    static {
        parentState.put(APPLICATION_APPROVAL, APPLICATION_APPROVAL);
        parentState.put(APPLICATION_APPROVAL_PENDING_COMPLETION, APPLICATION_APPROVAL);
        parentState.put(APPLICATION_APPROVAL_PENDING_FEEDBACK, APPLICATION_APPROVAL);
        parentState.put(APPLICATION_APPROVED, APPLICATION_APPROVED);
        parentState.put(APPLICATION_APPROVED_COMPLETED, APPLICATION_APPROVED);
        parentState.put(APPLICATION_APPROVED_PENDING_CORRECTION, APPLICATION_APPROVED);
        parentState.put(APPLICATION_APPROVED_PENDING_EXPORT, APPLICATION_APPROVED);
        parentState.put(APPLICATION_INTERVIEW, APPLICATION_INTERVIEW);
        parentState.put(APPLICATION_INTERVIEW_PENDING_AVAILABILITY, APPLICATION_INTERVIEW);
        parentState.put(APPLICATION_INTERVIEW_PENDING_COMPLETION, APPLICATION_INTERVIEW);
        parentState.put(APPLICATION_INTERVIEW_PENDING_FEEDBACK, APPLICATION_INTERVIEW);
        parentState.put(APPLICATION_INTERVIEW_PENDING_INTERVIEW, APPLICATION_INTERVIEW);
        parentState.put(APPLICATION_INTERVIEW_PENDING_SCHEDULING, APPLICATION_INTERVIEW);
        parentState.put(APPLICATION_REJECTED, APPLICATION_REJECTED);
        parentState.put(APPLICATION_REJECTED_COMPLETED, APPLICATION_REJECTED);
        parentState.put(APPLICATION_REJECTED_PENDING_CORRECTION, APPLICATION_REJECTED);
        parentState.put(APPLICATION_REJECTED_PENDING_EXPORT, APPLICATION_REJECTED);
        parentState.put(APPLICATION_REVIEW, APPLICATION_REVIEW);
        parentState.put(APPLICATION_REVIEW_PENDING_COMPLETION, APPLICATION_REVIEW);
        parentState.put(APPLICATION_REVIEW_PENDING_FEEDBACK, APPLICATION_REVIEW);
        parentState.put(APPLICATION_UNSUBMITTED, APPLICATION_UNSUBMITTED);
        parentState.put(APPLICATION_UNSUBMITTED_PENDING_COMPLETION, APPLICATION_UNSUBMITTED);
        parentState.put(APPLICATION_VALIDATION, APPLICATION_VALIDATION);
        parentState.put(APPLICATION_VALIDATION_PENDING_COMPLETION, APPLICATION_VALIDATION);
        parentState.put(APPLICATION_VALIDATION_PENDING_FEEDBACK, APPLICATION_VALIDATION);
        parentState.put(APPLICATION_WITHDRAWN, APPLICATION_WITHDRAWN);
        parentState.put(APPLICATION_WITHDRAWN_COMPLETED, APPLICATION_WITHDRAWN);
        parentState.put(APPLICATION_WITHDRAWN_PENDING_CORRECTION, APPLICATION_WITHDRAWN);
        parentState.put(INSTITUTION_APPROVED, INSTITUTION_APPROVED);
        parentState.put(PROGRAM_APPROVAL, PROGRAM_APPROVAL);
        parentState.put(PROGRAM_APPROVAL_PENDING_CORRECTION, PROGRAM_APPROVAL);
        parentState.put(PROGRAM_APPROVED, PROGRAM_APPROVED);
        parentState.put(PROGRAM_DEACTIVATED, PROGRAM_APPROVED);
        parentState.put(PROGRAM_DISABLED, PROGRAM_DISABLED);
        parentState.put(PROGRAM_DISABLED_COMPLETED, PROGRAM_DISABLED);
        parentState.put(PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION, PROGRAM_DISABLED);
        parentState.put(PROGRAM_DISABLED_PENDING_REACTIVATION, PROGRAM_DISABLED);
        parentState.put(PROGRAM_REJECTED, PROGRAM_REJECTED);
        parentState.put(PROGRAM_WITHDRAWN, PROGRAM_WITHDRAWN);
        parentState.put(PROJECT_APPROVED, PROJECT_APPROVED);
        parentState.put(PROJECT_DEACTIVATED, PROJECT_APPROVED);
        parentState.put(PROJECT_DISABLED, PROJECT_DISABLED);
        parentState.put(PROJECT_DISABLED_COMPLETED, PROJECT_DISABLED);
        parentState.put(PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION, PROJECT_DISABLED);
        parentState.put(PROJECT_DISABLED_PENDING_REACTIVATION, PROJECT_DISABLED);
        parentState.put(SYSTEM_APPROVED, SYSTEM_APPROVED);
    }

    static {
        for (PrismState state : PrismState.values()) {
            try {
                PrismWorkflowState workflowState = (PrismWorkflowState) BeanUtils.instantiate(state.getWorkflowStateClassName());
                workflowStateDefinitions.put(state, workflowState);
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    private PrismState(Integer sequenceOrder, Integer duration, PrismScope scope, Class<? extends PrismWorkflowState> workflowStateClass) {
        this.sequenceOrder = sequenceOrder;
        this.duration = duration;
        this.scope = scope;
        this.workflowStateClass = workflowStateClass;
    }

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    public Integer getDuration() {
        return duration;
    }

    public PrismScope getScope() {
        return scope;
    }

    public Class<? extends PrismWorkflowState> getWorkflowStateClassName() {
        return workflowStateClass;
    }

    public static List<PrismStateAction> getStateActions(PrismState state) {
        return workflowStateDefinitions.get(state).getStateActions();
    }

    public static PrismStateAction getStateAction(PrismState state, PrismAction action) {
        return workflowStateDefinitions.get(state).getStateActionsByAction(action);
    }

    public static PrismState getParentState(PrismState state) {
        return parentState.get(state);
    }

}
