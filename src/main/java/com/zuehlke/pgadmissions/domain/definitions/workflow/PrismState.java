package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWithdrawnCompleted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWithdrawnPendingExport;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApproved;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismWithdrawnPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismWorkflowState;

public enum PrismState {

    APPLICATION_APPROVAL(false, false, 5, null, PrismScope.APPLICATION, PrismApplicationApproval.class), //
    APPLICATION_APPROVAL_PENDING_COMPLETION(false, false, null, null, PrismScope.APPLICATION, PrismApplicationApprovalPendingCompletion.class), //
    APPLICATION_APPROVAL_PENDING_FEEDBACK(false, false, null, 7, PrismScope.APPLICATION, PrismApplicationApprovalPendingFeedback.class), //
    APPLICATION_APPROVED(false, false, 6, null, PrismScope.APPLICATION, PrismApplicationApproved.class), //
    APPLICATION_APPROVED_COMPLETED(false, true, null, null, PrismScope.APPLICATION, PrismApplicationApprovedCompleted.class), //
    APPLICATION_APPROVED_PENDING_CORRECTION(false, false, null, null, PrismScope.APPLICATION, PrismApplicationApprovedPendingCorrection.class), //
    APPLICATION_APPROVED_PENDING_EXPORT(false, false, null, null, PrismScope.APPLICATION, PrismApplicationApprovedPendingExport.class), //
    APPLICATION_INTERVIEW(false, false, 4, null, PrismScope.APPLICATION, PrismApplicationInterview.class), //
    APPLICATION_INTERVIEW_PENDING_AVAILABILITY(false, false, null, 3, PrismScope.APPLICATION, PrismApplicationInterviewPendingAvailability.class), //
    APPLICATION_INTERVIEW_PENDING_COMPLETION(false, false, null, null, PrismScope.APPLICATION, PrismApplicationInterviewPendingCompletion.class), //
    APPLICATION_INTERVIEW_PENDING_FEEDBACK(false, false, null, 7, PrismScope.APPLICATION, PrismApplicationInterviewPendingFeedback.class), //
    APPLICATION_INTERVIEW_PENDING_INTERVIEW(false, false, null, null, PrismScope.APPLICATION, PrismApplicationInterviewPendingInterview.class), //
    APPLICATION_INTERVIEW_PENDING_SCHEDULING(false, false, null, null, PrismScope.APPLICATION, PrismApplicationInterviewPendingScheduling.class), //
    APPLICATION_REJECTED(false, false, 7, null, PrismScope.APPLICATION, PrismApplicationRejected.class), //
    APPLICATION_REJECTED_COMPLETED(false, true, null, null, PrismScope.APPLICATION, PrismApplicationRejectedCompleted.class), //
    APPLICATION_REJECTED_PENDING_CORRECTION(false, false, null, null, PrismScope.APPLICATION, PrismApplicationRejectedPendingCorrection.class), //
    APPLICATION_REJECTED_PENDING_EXPORT(false, false, null, null, PrismScope.APPLICATION, PrismApplicationRejectedPendingExport.class), //
    APPLICATION_REVIEW(false, false, 3, null, PrismScope.APPLICATION, PrismApplicationReview.class), //
    APPLICATION_REVIEW_PENDING_COMPLETION(false, false, null, null, PrismScope.APPLICATION, PrismApplicationReviewPendingCompletion.class), //
    APPLICATION_REVIEW_PENDING_FEEDBACK(false, false, null, 7, PrismScope.APPLICATION, PrismApplicationReviewPendingFeedback.class), //
    APPLICATION_UNSUBMITTED(true, false, 1, 28, PrismScope.APPLICATION, PrismApplicationUnsubmitted.class), //
    APPLICATION_UNSUBMITTED_PENDING_COMPLETION(false, false, null, null, PrismScope.APPLICATION, PrismApplicationUnsubmittedPendingCompletion.class), //
    APPLICATION_VALIDATION(false, false, 2, null, PrismScope.APPLICATION, PrismApplicationValidation.class), //
    APPLICATION_VALIDATION_PENDING_COMPLETION(false, false, null, null, PrismScope.APPLICATION, PrismApplicationValidationPendingCompletion.class), //
    APPLICATION_VALIDATION_PENDING_FEEDBACK(false, false, null, 3, PrismScope.APPLICATION, PrismApplicationValidationPendingFeedback.class), //
    APPLICATION_WITHDRAWN(false, false, 0, null, PrismScope.APPLICATION, null), //
    APPLICATION_WITHDRAWN_PENDING_EXPORT(false, false, 0, null, PrismScope.APPLICATION, PrismApplicationWithdrawnPendingExport.class), //
    APPLICATION_WITHDRAWN_COMPLETED(false, true, null, null, PrismScope.APPLICATION, PrismApplicationWithdrawnCompleted.class), //
    APPLICATION_WITHDRAWN_PENDING_CORRECTION(false, false, null, null, PrismScope.APPLICATION, PrismWithdrawnPendingCorrection.class), //
    INSTITUTION_APPROVED(true, true, 1, null, PrismScope.INSTITUTION, PrismInstitutionApproved.class), //
    PROGRAM_APPROVAL(true, false, 1, null, PrismScope.PROGRAM, PrismProgramApproval.class), //
    PROGRAM_APPROVAL_PENDING_CORRECTION(false, false, null, null, PrismScope.PROGRAM, PrismProgramApprovalPendingCorrection.class), //
    PROGRAM_APPROVED(true, false, 2, null, PrismScope.PROGRAM, PrismProgramApproved.class), //
    PROGRAM_DEACTIVATED(false, false, null, null, PrismScope.PROGRAM, PrismProgramDeactivated.class), //
    PROGRAM_DISABLED(false, false, 4, 28, PrismScope.PROGRAM, null), //
    PROGRAM_DISABLED_COMPLETED(false, true, null, null, PrismScope.PROGRAM, PrismProgramDisabledCompleted.class), //
    PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION(false, false, null, 28, PrismScope.PROGRAM, PrismProgramDisabledPendingImportReactivation.class), //
    PROGRAM_DISABLED_PENDING_REACTIVATION(false, false, null, 28, PrismScope.PROGRAM, PrismProgramDisabledPendingReactivation.class), //
    PROGRAM_REJECTED(false, true, 3, null, PrismScope.PROGRAM, PrismProgramRejected.class), //
    PROGRAM_WITHDRAWN(false, true, 0, null, PrismScope.PROGRAM, PrismProgramWithdrawn.class), //
    PROJECT_APPROVAL(true, false, 1, null, PrismScope.PROJECT, PrismProjectApproval.class), //
    PROJECT_APPROVAL_PENDING_CORRECTION(false, false, null, null, PrismScope.PROGRAM, PrismProjectApprovalPendingCorrection.class), //
    PROJECT_APPROVED(true, false, 2, null, PrismScope.PROJECT, PrismProjectApproved.class), //
    PROJECT_DEACTIVATED(false, false, null, null, PrismScope.PROJECT, PrismProjectDeactivated.class), //
    PROJECT_DISABLED(false, false, 3, 28, PrismScope.PROJECT, null), //
    PROJECT_DISABLED_COMPLETED(false, true, null, null, PrismScope.PROJECT, PrismProjectDisabledCompleted.class), //
    PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION(false, false, null, null, PrismScope.PROJECT, PrismProjectDisabledPendingProgramReactivation.class), //
    PROJECT_DISABLED_PENDING_REACTIVATION(false, false, null, 28, PrismScope.PROJECT, PrismProjectDisabledPendingReactivation.class), //
    PROJECT_REJECTED(false, true, 3, null, PrismScope.PROGRAM, PrismProjectRejected.class), //
    PROJECT_WITHDRAWN(false, true, 0, null, PrismScope.PROGRAM, PrismProjectWithdrawn.class), //
    SYSTEM_APPROVED(true, true, 1, null, PrismScope.SYSTEM, PrismSystemApproved.class);

    private boolean initialState;

    private boolean finalState;

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
        parentState.put(APPLICATION_WITHDRAWN_PENDING_EXPORT, APPLICATION_WITHDRAWN);
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
        parentState.put(PROJECT_APPROVAL, PROJECT_APPROVAL);
        parentState.put(PROJECT_APPROVAL_PENDING_CORRECTION, PROJECT_APPROVAL);
        parentState.put(PROJECT_APPROVED, PROJECT_APPROVED);
        parentState.put(PROJECT_DEACTIVATED, PROJECT_APPROVED);
        parentState.put(PROJECT_DISABLED, PROJECT_DISABLED);
        parentState.put(PROJECT_DISABLED_COMPLETED, PROJECT_DISABLED);
        parentState.put(PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION, PROJECT_DISABLED);
        parentState.put(PROJECT_DISABLED_PENDING_REACTIVATION, PROJECT_DISABLED);
        parentState.put(PROJECT_REJECTED, PROJECT_REJECTED);
        parentState.put(PROJECT_WITHDRAWN, PROJECT_WITHDRAWN);        
        parentState.put(SYSTEM_APPROVED, SYSTEM_APPROVED);
    }

    static {
        for (PrismState state : PrismState.values()) {
            if (state.getWorkflowStateClassName() != null) {
                PrismWorkflowState workflowState = (PrismWorkflowState) BeanUtils.instantiate(state.getWorkflowStateClassName());
                workflowStateDefinitions.put(state, workflowState);
            }
        }
    }

    private static List<PrismState> initialStates = Lists.newArrayList();
    
    static {
        for (PrismState state : PrismState.values()) {
            if (state.isInitialState()) {
                initialStates.add(state);
            }
        }
    }
    
    private static List<PrismState> finalStates = Lists.newArrayList();
    
    static {
        for (PrismState state : PrismState.values()) {
            if (state.isFinalState()) {
                finalStates.add(state);
            }
        }
    }
    
    private PrismState(boolean initialState, boolean finalState, Integer sequenceOrder, Integer duration, PrismScope scope,
            Class<? extends PrismWorkflowState> workflowStateClass) {
        this.initialState = initialState;
        this.finalState = finalState;
        this.sequenceOrder = sequenceOrder;
        this.duration = duration;
        this.scope = scope;
        this.workflowStateClass = workflowStateClass;
    }

    public boolean isInitialState() {
        return initialState;
    }

    public boolean isFinalState() {
        return finalState;
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


    public static PrismState getParentState(PrismState state) {
        return parentState.get(state);
    }
    
    public static List<PrismStateAction> getStateActions(PrismState state) {
        return workflowStateDefinitions.containsKey(state) ? workflowStateDefinitions.get(state).getStateActions() : new ArrayList<PrismStateAction>();
    }

    public static PrismStateAction getStateAction(PrismState state, PrismAction action) {
        return workflowStateDefinitions.containsKey(state) ? workflowStateDefinitions.get(state).getStateActionsByAction(action) : null;
    }

    public static List<PrismState> getInitialStates() {
        return initialStates;
    }

    public static List<PrismState> getFinalStates() {
        return finalStates;
    }

}
