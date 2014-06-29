package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.HashMap;
import java.util.List;

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
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApprovalPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApproved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramApproval;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWithdrawnCompleted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismWithdrawnPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismWorkflowState;

public enum PrismState {

    APPLICATION_APPROVAL(5, null, PrismScope.APPLICATION, new PrismApplicationApproval()), //
    APPLICATION_APPROVAL_PENDING_COMPLETION(null, null, PrismScope.APPLICATION, new PrismApplicationApprovalPendingCompletion()), //
    APPLICATION_APPROVAL_PENDING_FEEDBACK(null, 7, PrismScope.APPLICATION, new PrismApplicationApprovalPendingFeedback()), //
    APPLICATION_APPROVED(6, null, PrismScope.APPLICATION, new PrismApplicationApproved()), //
    APPLICATION_APPROVED_COMPLETED(null, null, PrismScope.APPLICATION, new PrismApplicationApprovedCompleted()), //
    APPLICATION_APPROVED_PENDING_CORRECTION(null, null, PrismScope.APPLICATION, new PrismApplicationApprovedPendingCorrection()), //
    APPLICATION_APPROVED_PENDING_EXPORT(null, null, PrismScope.APPLICATION, new PrismApplicationApprovedPendingExport()), //
    APPLICATION_INTERVIEW(4, null, PrismScope.APPLICATION, new PrismApplicationInterview()), //
    APPLICATION_INTERVIEW_PENDING_AVAILABILITY(null, 3, PrismScope.APPLICATION, new PrismApplicationInterviewPendingAvailability()), //
    APPLICATION_INTERVIEW_PENDING_COMPLETION(null, null, PrismScope.APPLICATION, new PrismApplicationInterviewPendingCompletion()), //
    APPLICATION_INTERVIEW_PENDING_FEEDBACK(null, 7, PrismScope.APPLICATION, new PrismApplicationInterviewPendingFeedback()), //
    APPLICATION_INTERVIEW_PENDING_INTERVIEW(null, null, PrismScope.APPLICATION, new PrismApplicationInterviewPendingInterview()), //
    APPLICATION_INTERVIEW_PENDING_SCHEDULING(null, null, PrismScope.APPLICATION, new PrismApplicationInterviewPendingScheduling()), //
    APPLICATION_REJECTED(7, null, PrismScope.APPLICATION, new PrismApplicationRejected()), //
    APPLICATION_REJECTED_COMPLETED(null, null, PrismScope.APPLICATION, new PrismApplicationRejectedCompleted()), //
    APPLICATION_REJECTED_PENDING_CORRECTION(null, null, PrismScope.APPLICATION, new PrismApplicationRejectedPendingCorrection()), //
    APPLICATION_REJECTED_PENDING_EXPORT(null, null, PrismScope.APPLICATION, new PrismApplicationRejectedPendingExport()), //
    APPLICATION_REVIEW(3, null, PrismScope.APPLICATION, new PrismApplicationReview()), //
    APPLICATION_REVIEW_PENDING_COMPLETION(null, null, PrismScope.APPLICATION, new PrismApplicationReviewPendingCompletion()), //
    APPLICATION_REVIEW_PENDING_FEEDBACK(null, 7, PrismScope.APPLICATION, new PrismApplicationReviewPendingFeedback()), //
    APPLICATION_UNSUBMITTED(1, 28, PrismScope.APPLICATION, new PrismApplicationUnsubmitted()), //
    APPLICATION_UNSUBMITTED_PENDING_COMPLETION(null, null, PrismScope.APPLICATION, new PrismApplicationUnsubmittedPendingCompletion()), //
    APPLICATION_VALIDATION(2, null, PrismScope.APPLICATION, new PrismApplicationValidation()), //
    APPLICATION_VALIDATION_PENDING_COMPLETION(null, null, PrismScope.APPLICATION, new PrismApplicationValidationPendingCompletion()), //
    APPLICATION_VALIDATION_PENDING_FEEDBACK(null, 3, PrismScope.APPLICATION, new PrismApplicationValidationPendingFeedback()), //
    APPLICATION_WITHDRAWN(0, null, PrismScope.APPLICATION, new PrismApplicationWithdrawn()), //
    APPLICATION_WITHDRAWN_COMPLETED(null, null, PrismScope.APPLICATION, new PrismApplicationWithdrawnCompleted()), //
    APPLICATION_WITHDRAWN_PENDING_CORRECTION(null, null, PrismScope.APPLICATION, new PrismWithdrawnPendingCorrection()), //
    INSTITUTION_APPROVED(1, null, PrismScope.INSTITUTION, new PrismInstitutionApproved()), //
    PROGRAM_APPROVAL(1, null, PrismScope.PROGRAM, new PrismProgramApproval()), //
    PROGRAM_APPROVAL_PENDING_CORRECTION(null, null, PrismScope.PROGRAM, new PrismApprovalPendingCorrection()), //
    PROGRAM_APPROVED(2, null, PrismScope.PROGRAM, new PrismProgramApproved()), //
    PROGRAM_DEACTIVATED(null, null, PrismScope.PROGRAM, new PrismProgramDeactivated()), //
    PROGRAM_DISABLED(4, 28, PrismScope.PROGRAM, new PrismProgramDisabled()), //
    PROGRAM_DISABLED_COMPLETED(null, null, PrismScope.PROGRAM, new PrismProgramDisabledCompleted()), //
    PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION(null, 28, PrismScope.PROGRAM, new PrismProgramDisabledPendingImportReactivation()), //
    PROGRAM_DISABLED_PENDING_REACTIVATION(null, 28, PrismScope.PROGRAM, new PrismProgramDisabledPendingReactivation()), //
    PROGRAM_REJECTED(3, null, PrismScope.PROGRAM, new PrismProgramRejected()), //
    PROGRAM_WITHDRAWN(0, null, PrismScope.PROGRAM, new PrismProgramWithdrawn()), //
    PROJECT_APPROVED(1, null, PrismScope.PROJECT, new PrismProjectApproved()), //
    PROJECT_DEACTIVATED(null, null, PrismScope.PROJECT, new PrismProjectDeactivated()), //
    PROJECT_DISABLED(2, 28, PrismScope.PROJECT, new PrismProjectDisabled()), //
    PROJECT_DISABLED_COMPLETED(null, null, PrismScope.PROJECT, new PrismProjectDisabledCompleted()), //
    PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION(null, null, PrismScope.PROJECT, new PrismProjectDisabledPendingProgramReactivation()), //
    PROJECT_DISABLED_PENDING_REACTIVATION(null, 28, PrismScope.PROJECT, new PrismProjectDisabledPendingReactivation()), //
    SYSTEM_APPROVED(1, null, PrismScope.SYSTEM, new PrismSystemApproved());
    
    private Integer sequenceOrder;
    
    private Integer duration;
    
    private PrismScope scope;
    
    private PrismWorkflowState stateActions;
    
    private static final HashMap<PrismState, PrismState> parentState = Maps.newHashMap();
    
    static {
        parentState.put(APPLICATION_APPROVAL,APPLICATION_APPROVAL);
        parentState.put(APPLICATION_APPROVAL_PENDING_COMPLETION,APPLICATION_APPROVAL);
        parentState.put(APPLICATION_APPROVAL_PENDING_FEEDBACK,APPLICATION_APPROVAL);
        parentState.put(APPLICATION_APPROVED,APPLICATION_APPROVED);
        parentState.put(APPLICATION_APPROVED_COMPLETED,APPLICATION_APPROVED);
        parentState.put(APPLICATION_APPROVED_PENDING_CORRECTION,APPLICATION_APPROVED);
        parentState.put(APPLICATION_APPROVED_PENDING_EXPORT,APPLICATION_APPROVED);
        parentState.put(APPLICATION_INTERVIEW,APPLICATION_INTERVIEW);
        parentState.put(APPLICATION_INTERVIEW_PENDING_AVAILABILITY,APPLICATION_INTERVIEW);
        parentState.put(APPLICATION_INTERVIEW_PENDING_COMPLETION,APPLICATION_INTERVIEW);
        parentState.put(APPLICATION_INTERVIEW_PENDING_FEEDBACK,APPLICATION_INTERVIEW);
        parentState.put(APPLICATION_INTERVIEW_PENDING_INTERVIEW,APPLICATION_INTERVIEW);
        parentState.put(APPLICATION_INTERVIEW_PENDING_SCHEDULING,APPLICATION_INTERVIEW);
        parentState.put(APPLICATION_REJECTED,APPLICATION_REJECTED);
        parentState.put(APPLICATION_REJECTED_COMPLETED,APPLICATION_REJECTED);
        parentState.put(APPLICATION_REJECTED_PENDING_CORRECTION,APPLICATION_REJECTED);
        parentState.put(APPLICATION_REJECTED_PENDING_EXPORT,APPLICATION_REJECTED);
        parentState.put(APPLICATION_REVIEW,APPLICATION_REVIEW);
        parentState.put(APPLICATION_REVIEW_PENDING_COMPLETION,APPLICATION_REVIEW);
        parentState.put(APPLICATION_REVIEW_PENDING_FEEDBACK,APPLICATION_REVIEW);
        parentState.put(APPLICATION_UNSUBMITTED,APPLICATION_UNSUBMITTED);
        parentState.put(APPLICATION_UNSUBMITTED_PENDING_COMPLETION,APPLICATION_UNSUBMITTED);
        parentState.put(APPLICATION_VALIDATION,APPLICATION_VALIDATION);
        parentState.put(APPLICATION_VALIDATION_PENDING_COMPLETION,APPLICATION_VALIDATION);
        parentState.put(APPLICATION_VALIDATION_PENDING_FEEDBACK,APPLICATION_VALIDATION);
        parentState.put(APPLICATION_WITHDRAWN,APPLICATION_WITHDRAWN);
        parentState.put(APPLICATION_WITHDRAWN_COMPLETED,APPLICATION_WITHDRAWN);
        parentState.put(APPLICATION_WITHDRAWN_PENDING_CORRECTION,APPLICATION_WITHDRAWN);
        parentState.put(INSTITUTION_APPROVED,INSTITUTION_APPROVED);
        parentState.put(PROGRAM_APPROVAL,PROGRAM_APPROVAL);
        parentState.put(PROGRAM_APPROVAL_PENDING_CORRECTION,PROGRAM_APPROVAL);
        parentState.put(PROGRAM_APPROVED,PROGRAM_APPROVED);
        parentState.put(PROGRAM_DEACTIVATED,PROGRAM_APPROVED);
        parentState.put(PROGRAM_DISABLED,PROGRAM_DISABLED);
        parentState.put(PROGRAM_DISABLED_COMPLETED,PROGRAM_DISABLED);
        parentState.put(PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION,PROGRAM_DISABLED);
        parentState.put(PROGRAM_DISABLED_PENDING_REACTIVATION,PROGRAM_DISABLED);
        parentState.put(PROGRAM_REJECTED,PROGRAM_REJECTED);
        parentState.put(PROGRAM_WITHDRAWN,PROGRAM_WITHDRAWN);
        parentState.put(PROJECT_APPROVED,PROJECT_APPROVED);
        parentState.put(PROJECT_DEACTIVATED,PROJECT_APPROVED);
        parentState.put(PROJECT_DISABLED,PROJECT_DISABLED);
        parentState.put(PROJECT_DISABLED_COMPLETED,PROJECT_DISABLED);
        parentState.put(PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION,PROJECT_DISABLED);
        parentState.put(PROJECT_DISABLED_PENDING_REACTIVATION,PROJECT_DISABLED);
        parentState.put(SYSTEM_APPROVED,SYSTEM_APPROVED);
    }

    private PrismState(Integer sequenceOrder, Integer duration, PrismScope scope, PrismWorkflowState definition) {
        this.sequenceOrder = sequenceOrder;
        this.duration = duration;
        this.scope = scope;
        this.stateActions = definition;
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
    
    public List<PrismStateAction> getStateActions() {
        return stateActions.getStateActions();
    }
    
    public static PrismState getParentState(PrismState state) {
        return parentState.get(state);
    }
    
}
