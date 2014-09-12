package com.zuehlke.pgadmissions.domain.definitions.workflow;

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
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWithdrawnCompletedUnsubmitted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWithdrawnPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWithdrawnPendingExport;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApproval;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApprovalPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismInstitutionApproved;
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

    APPLICATION_APPROVAL(PrismStateGroup.APPLICATION_APPROVAL, null, PrismScope.APPLICATION, PrismApplicationApproval.class), //
    APPLICATION_APPROVAL_PENDING_COMPLETION(PrismStateGroup.APPLICATION_APPROVAL, null, PrismScope.APPLICATION, PrismApplicationApprovalPendingCompletion.class), //
    APPLICATION_APPROVAL_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_APPROVAL, 7, PrismScope.APPLICATION, PrismApplicationApprovalPendingFeedback.class), //
    APPLICATION_APPROVED(PrismStateGroup.APPLICATION_APPROVED, null, PrismScope.APPLICATION, PrismApplicationApproved.class), //
    APPLICATION_APPROVED_COMPLETED(PrismStateGroup.APPLICATION_APPROVED, null, PrismScope.APPLICATION, PrismApplicationApprovedCompleted.class), //
    APPLICATION_APPROVED_PENDING_CORRECTION(PrismStateGroup.APPLICATION_APPROVED, 28, PrismScope.APPLICATION, PrismApplicationApprovedPendingCorrection.class), //
    APPLICATION_APPROVED_PENDING_EXPORT(PrismStateGroup.APPLICATION_APPROVED, null, PrismScope.APPLICATION, PrismApplicationApprovedPendingExport.class), //
    APPLICATION_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, null, PrismScope.APPLICATION, PrismApplicationInterview.class), //
    APPLICATION_INTERVIEW_PENDING_AVAILABILITY(PrismStateGroup.APPLICATION_INTERVIEW, //
            3, PrismScope.APPLICATION, PrismApplicationInterviewPendingAvailability.class), //
    APPLICATION_INTERVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_INTERVIEW, //
            null, PrismScope.APPLICATION, PrismApplicationInterviewPendingCompletion.class), //
    APPLICATION_INTERVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_INTERVIEW, 7, PrismScope.APPLICATION, PrismApplicationInterviewPendingFeedback.class), //
    APPLICATION_INTERVIEW_PENDING_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, //
            null, PrismScope.APPLICATION, PrismApplicationInterviewPendingInterview.class), //
    APPLICATION_INTERVIEW_PENDING_SCHEDULING(PrismStateGroup.APPLICATION_INTERVIEW, //
            null, PrismScope.APPLICATION, PrismApplicationInterviewPendingScheduling.class), //
    APPLICATION_REJECTED(PrismStateGroup.APPLICATION_REJECTED, null, PrismScope.APPLICATION, PrismApplicationRejected.class), //
    APPLICATION_REJECTED_COMPLETED(PrismStateGroup.APPLICATION_REJECTED, null, PrismScope.APPLICATION, PrismApplicationRejectedCompleted.class), //
    APPLICATION_REJECTED_PENDING_CORRECTION(PrismStateGroup.APPLICATION_REJECTED, 28, PrismScope.APPLICATION, PrismApplicationRejectedPendingCorrection.class), //
    APPLICATION_REJECTED_PENDING_EXPORT(PrismStateGroup.APPLICATION_REJECTED, null, PrismScope.APPLICATION, PrismApplicationRejectedPendingExport.class), //
    APPLICATION_REVIEW(PrismStateGroup.APPLICATION_REVIEW, null, PrismScope.APPLICATION, PrismApplicationReview.class), //
    APPLICATION_REVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_REVIEW, null, PrismScope.APPLICATION, PrismApplicationReviewPendingCompletion.class), //
    APPLICATION_REVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_REVIEW, 7, PrismScope.APPLICATION, PrismApplicationReviewPendingFeedback.class), //
    APPLICATION_UNSUBMITTED(PrismStateGroup.APPLICATION_UNSUBMITTED, 28, PrismScope.APPLICATION, PrismApplicationUnsubmitted.class), //
    APPLICATION_UNSUBMITTED_PENDING_COMPLETION(PrismStateGroup.APPLICATION_UNSUBMITTED, //
            28, PrismScope.APPLICATION, PrismApplicationUnsubmittedPendingCompletion.class), //
    APPLICATION_VALIDATION(PrismStateGroup.APPLICATION_VALIDATION, null, PrismScope.APPLICATION, PrismApplicationValidation.class), //
    APPLICATION_VALIDATION_PENDING_COMPLETION(PrismStateGroup.APPLICATION_VALIDATION, //
            null, PrismScope.APPLICATION, PrismApplicationValidationPendingCompletion.class), //
    APPLICATION_VALIDATION_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_VALIDATION, 3, PrismScope.APPLICATION, PrismApplicationValidationPendingFeedback.class), //
    APPLICATION_WITHDRAWN_PENDING_EXPORT(PrismStateGroup.APPLICATION_WITHDRAWN, null, PrismScope.APPLICATION, PrismApplicationWithdrawnPendingExport.class), //
    APPLICATION_WITHDRAWN_COMPLETED(PrismStateGroup.APPLICATION_WITHDRAWN, null, PrismScope.APPLICATION, PrismApplicationWithdrawnCompleted.class), //
    APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED(PrismStateGroup.APPLICATION_WITHDRAWN, null, PrismScope.APPLICATION,
            PrismApplicationWithdrawnCompletedUnsubmitted.class), //
    APPLICATION_WITHDRAWN_PENDING_CORRECTION(PrismStateGroup.APPLICATION_WITHDRAWN, //
            28, PrismScope.APPLICATION, PrismApplicationWithdrawnPendingCorrection.class), //
    INSTITUTION_APPROVAL(PrismStateGroup.INSTITUTION_APPROVAL, 28, PrismScope.INSTITUTION, PrismInstitutionApproval.class), //
    INSTITUTION_APPROVAL_PENDING_CORRECTION(PrismStateGroup.INSTITUTION_APPROVAL, 28, PrismScope.INSTITUTION, PrismInstitutionApprovalPendingCorrection.class), //
    INSTITUTION_APPROVED(PrismStateGroup.INSTITUTION_APPROVED, null, PrismScope.INSTITUTION, PrismInstitutionApproved.class), //
    INSTITUTION_REJECTED(PrismStateGroup.INSTITUTION_REJECTED, null, PrismScope.INSTITUTION, PrismInstitutionRejected.class), //
    INSTITUTION_WITHDRAWN(PrismStateGroup.INSTITUTION_WITHDRAWN, null, PrismScope.INSTITUTION, PrismInstitutionWithdrawn.class), //
    PROGRAM_APPROVAL(PrismStateGroup.PROGRAM_APPROVAL, 28, PrismScope.PROGRAM, PrismProgramApproval.class), //
    PROGRAM_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROGRAM_APPROVAL, 28, PrismScope.PROGRAM, PrismProgramApprovalPendingCorrection.class), //
    PROGRAM_APPROVED(PrismStateGroup.PROGRAM_APPROVED, null, PrismScope.PROGRAM, PrismProgramApproved.class), //
    PROGRAM_DEACTIVATED(PrismStateGroup.PROGRAM_APPROVED, null, PrismScope.PROGRAM, PrismProgramDeactivated.class), //
    PROGRAM_DISABLED_COMPLETED(PrismStateGroup.PROGRAM_DISABLED, null, PrismScope.PROGRAM, PrismProgramDisabledCompleted.class), //
    PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION(PrismStateGroup.PROGRAM_DISABLED, 28, PrismScope.PROGRAM, PrismProgramDisabledPendingImportReactivation.class), //
    PROGRAM_DISABLED_PENDING_REACTIVATION(PrismStateGroup.PROGRAM_DISABLED, 28, PrismScope.PROGRAM, PrismProgramDisabledPendingReactivation.class), //
    PROGRAM_REJECTED(PrismStateGroup.PROGRAM_REJECTED, null, PrismScope.PROGRAM, PrismProgramRejected.class), //
    PROGRAM_WITHDRAWN(PrismStateGroup.PROGRAM_WITHDRAWN, null, PrismScope.PROGRAM, PrismProgramWithdrawn.class), //
    PROJECT_APPROVAL(PrismStateGroup.PROJECT_APPROVAL, 28, PrismScope.PROJECT, PrismProjectApproval.class), //
    PROJECT_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROJECT_APPROVAL, 28, PrismScope.PROJECT, PrismProjectApprovalPendingCorrection.class), //
    PROJECT_APPROVED(PrismStateGroup.PROJECT_APPROVED, null, PrismScope.PROJECT, PrismProjectApproved.class), //
    PROJECT_DEACTIVATED(PrismStateGroup.PROJECT_APPROVED, null, PrismScope.PROJECT, PrismProjectDeactivated.class), //
    PROJECT_DISABLED_COMPLETED(PrismStateGroup.PROJECT_DISABLED, null, PrismScope.PROJECT, PrismProjectDisabledCompleted.class), //
    PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION(PrismStateGroup.PROJECT_DISABLED, //
            null, PrismScope.PROJECT, PrismProjectDisabledPendingProgramReactivation.class), //
    PROJECT_DISABLED_PENDING_REACTIVATION(PrismStateGroup.PROJECT_DISABLED, 28, PrismScope.PROJECT, PrismProjectDisabledPendingReactivation.class), //
    PROJECT_REJECTED(PrismStateGroup.PROJECT_REJECTED, null, PrismScope.PROJECT, PrismProjectRejected.class), //
    PROJECT_WITHDRAWN(PrismStateGroup.PROJECT_WITHDRAWN, null, PrismScope.PROJECT, PrismProjectWithdrawn.class), //
    SYSTEM_RUNNING(PrismStateGroup.SYSTEM_RUNNING, null, PrismScope.SYSTEM, PrismSystemApproved.class);

    private PrismStateGroup stateGroup;

    private Integer duration;

    private PrismScope scope;

    private Class<? extends PrismWorkflowState> workflowStateClass;

    private static final HashMap<PrismState, PrismWorkflowState> workflowStateDefinitions = Maps.newHashMap();

    static {
        for (PrismState state : PrismState.values()) {
            if (state.getWorkflowStateClassName() != null) {
                PrismWorkflowState workflowState = (PrismWorkflowState) BeanUtils.instantiate(state.getWorkflowStateClassName());
                workflowStateDefinitions.put(state, workflowState);
            }
        }
    }

    private PrismState(PrismStateGroup stateGroup, Integer duration, PrismScope scope, Class<? extends PrismWorkflowState> workflowStateClass) {
        this.stateGroup = stateGroup;
        this.duration = duration;
        this.scope = scope;
        this.workflowStateClass = workflowStateClass;
    }

    public PrismStateGroup getStateGroup() {
        return stateGroup;
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
        return workflowStateDefinitions.containsKey(state) ? workflowStateDefinitions.get(state).getStateActions() : new ArrayList<PrismStateAction>();
    }

    public static PrismStateAction getStateAction(PrismState state, PrismAction action) {
        return workflowStateDefinitions.containsKey(state) ? workflowStateDefinitions.get(state).getStateActionsByAction(action) : null;
    }

}
