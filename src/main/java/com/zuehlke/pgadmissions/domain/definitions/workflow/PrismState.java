package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_CONFIRM_ELIGIBILITY_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_CONFIRM_SUPERVISION_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_ESCALATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PROVIDE_REFERENCE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PROVIDE_REVIEW_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PURGE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_RESERVE_DURATION;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationRejectedPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationRejectedPendingExport;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReserved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReservedPendingReallocation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReservedWaiting;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramDisabledPendingReactivation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramRejected;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProgramWithdrawn;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproval;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApprovalPendingCorrection;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectApproved;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectDeactivated;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectDisabledCompleted;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectDisabledPendingReactivation;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectRejected;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismProjectWithdrawn;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismSystemRunning;
import com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismWorkflowState;

public enum PrismState {

	APPLICATION_UNSUBMITTED(PrismStateGroup.APPLICATION_UNSUBMITTED, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationUnsubmitted.class),
	APPLICATION_UNSUBMITTED_PENDING_COMPLETION(PrismStateGroup.APPLICATION_UNSUBMITTED, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationUnsubmittedPendingCompletion.class),
	APPLICATION_VALIDATION(PrismStateGroup.APPLICATION_VALIDATION, null, APPLICATION_CLOSING_DATE, PrismApplicationValidation.class),
	APPLICATION_VALIDATION_PENDING_COMPLETION(PrismStateGroup.APPLICATION_VALIDATION, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationValidationPendingCompletion.class),
	APPLICATION_VERIFICATION(PrismStateGroup.APPLICATION_VERIFICATION, APPLICATION_CONFIRM_ELIGIBILITY_DURATION, null,
	        PrismApplicationVerification.class),
	APPLICATION_VERIFICATION_PENDING_COMPLETION(PrismStateGroup.APPLICATION_VERIFICATION, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationVerificationPendingCompletion.class),
	APPLICATION_REFERENCE(PrismStateGroup.APPLICATION_REFERENCE, APPLICATION_PROVIDE_REFERENCE_DURATION, null,
	        PrismApplicationReference.class),
	APPLICATION_REFERENCE_PENDING_COMPLETION(PrismStateGroup.APPLICATION_REFERENCE, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationReferencePendingCompletion.class),
	APPLICATION_REVIEW(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_ESCALATE_DURATION, null, PrismApplicationReview.class),
	APPLICATION_REVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_PROVIDE_REVIEW_DURATION, null,
	        PrismApplicationReviewPendingFeedback.class),
	APPLICATION_REVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationReviewPendingCompletion.class),
	APPLICATION_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationInterview.class),
	APPLICATION_INTERVIEW_PENDING_AVAILABILITY(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION, null,
	        PrismApplicationInterviewPendingAvailability.class),
	APPLICATION_INTERVIEW_PENDING_SCHEDULING(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationInterviewPendingScheduling.class),
	APPLICATION_INTERVIEW_PENDING_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, null, APPLICATION_INTERVIEW_DATE,
	        PrismApplicationInterviewPendingInterview.class),
	APPLICATION_INTERVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION, null,
	        PrismApplicationInterviewPendingFeedback.class),
	APPLICATION_INTERVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationInterviewPendingCompletion.class),
	APPLICATION_APPROVAL(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_ESCALATE_DURATION, null, PrismApplicationApproval.class),
	APPLICATION_APPROVAL_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_CONFIRM_SUPERVISION_DURATION, null,
	        PrismApplicationApprovalPendingFeedback.class),
	APPLICATION_APPROVAL_PENDING_COMPLETION(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationApprovalPendingCompletion.class),
	APPLICATION_APPROVED(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null, PrismApplicationApproved.class),
	APPLICATION_APPROVED_PENDING_EXPORT(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationApprovedPendingExport.class),
	APPLICATION_APPROVED_PENDING_CORRECTION(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationApprovedPendingCorrection.class),
	APPLICATION_APPROVED_COMPLETED(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_PURGE_DURATION, null,
	        PrismApplicationApprovedCompleted.class),
	APPLICATION_APPROVED_COMPLETED_PURGED(PrismStateGroup.APPLICATION_APPROVED, null, null, null),
	APPLICATION_REJECTED(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null, PrismApplicationRejected.class),
	APPLICATION_REJECTED_PENDING_EXPORT(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationRejectedPendingExport.class),
	APPLICATION_REJECTED_PENDING_CORRECTION(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationRejectedPendingCorrection.class),
	APPLICATION_REJECTED_COMPLETED(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_PURGE_DURATION, null,
	        PrismApplicationRejectedCompleted.class),
	APPLICATION_REJECTED_COMPLETED_PURGED(PrismStateGroup.APPLICATION_REJECTED, null, null, null),
	APPLICATION_RESERVED(PrismStateGroup.APPLICATION_RESERVED, APPLICATION_ESCALATE_DURATION, null, PrismApplicationReserved.class),
	APPLICATION_RESERVED_WAITING(PrismStateGroup.APPLICATION_RESERVED, APPLICATION_RESERVE_DURATION, null,
	        PrismApplicationReservedWaiting.class),
	APPLICATION_RESERVED_PENDING_REALLOCATION(PrismStateGroup.APPLICATION_RESERVED, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationReservedPendingReallocation.class),
	APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_PURGE_DURATION, null,
	        PrismApplicationWithdrawnCompletedUnsubmitted.class),
	APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_PURGED(PrismStateGroup.APPLICATION_WITHDRAWN, null, null, null),
	APPLICATION_WITHDRAWN_PENDING_EXPORT(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationWithdrawnPendingExport.class),
	APPLICATION_WITHDRAWN_PENDING_CORRECTION(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_ESCALATE_DURATION, null,
	        PrismApplicationWithdrawnPendingCorrection.class),
	APPLICATION_WITHDRAWN_COMPLETED(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_PURGE_DURATION, null,
	        PrismApplicationWithdrawnCompleted.class),
	APPLICATION_WITHDRAWN_COMPLETED_PURGED(PrismStateGroup.APPLICATION_WITHDRAWN, null, null, null),
	PROJECT_APPROVAL(PrismStateGroup.PROJECT_APPROVAL, PROJECT_ESCALATE_DURATION, null, PrismProjectApproval.class),
	PROJECT_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROJECT_APPROVAL, PROJECT_ESCALATE_DURATION, null,
	        PrismProjectApprovalPendingCorrection.class),
	PROJECT_APPROVED(PrismStateGroup.PROJECT_APPROVED, null, PROJECT_END_DATE, PrismProjectApproved.class),
	PROJECT_DEACTIVATED(PrismStateGroup.PROJECT_APPROVED, null, PROJECT_END_DATE, PrismProjectDeactivated.class),
	PROJECT_DISABLED_PENDING_REACTIVATION(PrismStateGroup.PROJECT_DISABLED, PROJECT_ESCALATE_DURATION, null,
	        PrismProjectDisabledPendingReactivation.class),
	PROJECT_DISABLED_COMPLETED(PrismStateGroup.PROJECT_DISABLED, null, null, PrismProjectDisabledCompleted.class),
	PROJECT_REJECTED(PrismStateGroup.PROJECT_REJECTED, null, null, PrismProjectRejected.class),
	PROJECT_WITHDRAWN(PrismStateGroup.PROJECT_WITHDRAWN, null, null, PrismProjectWithdrawn.class),
	PROGRAM_APPROVAL(PrismStateGroup.PROGRAM_APPROVAL, PROGRAM_ESCALATE_DURATION, null, PrismProgramApproval.class),
	PROGRAM_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROGRAM_APPROVAL, PROGRAM_ESCALATE_DURATION, null,
	        PrismProgramApprovalPendingCorrection.class),
	PROGRAM_APPROVED(PrismStateGroup.PROGRAM_APPROVED, null, PROGRAM_END_DATE, PrismProgramApproved.class),
	PROGRAM_DEACTIVATED(PrismStateGroup.PROGRAM_APPROVED, null, PROGRAM_END_DATE, PrismProgramDeactivated.class),
	PROGRAM_DISABLED_PENDING_REACTIVATION(PrismStateGroup.PROGRAM_DISABLED, PROGRAM_ESCALATE_DURATION, null,
	        PrismProgramDisabledPendingReactivation.class),
	PROGRAM_DISABLED_COMPLETED(PrismStateGroup.PROGRAM_DISABLED, null, null, PrismProgramDisabledCompleted.class),
	PROGRAM_REJECTED(PrismStateGroup.PROGRAM_REJECTED, null, null, PrismProgramRejected.class),
	PROGRAM_WITHDRAWN(PrismStateGroup.PROGRAM_WITHDRAWN, null, null, PrismProgramWithdrawn.class),
	INSTITUTION_APPROVAL(PrismStateGroup.INSTITUTION_APPROVAL, INSTITUTION_ESCALATE_DURATION, null, PrismInstitutionApproval.class),
	INSTITUTION_APPROVAL_PENDING_CORRECTION(PrismStateGroup.INSTITUTION_APPROVAL, INSTITUTION_ESCALATE_DURATION, null,
	        PrismInstitutionApprovalPendingCorrection.class),
	INSTITUTION_APPROVED(PrismStateGroup.INSTITUTION_APPROVED, null, null, PrismInstitutionApproved.class),
	INSTITUTION_APPROVED_COMPLETED(PrismStateGroup.INSTITUTION_APPROVED, null, null, PrismInstitutionApprovedCompleted.class),
	INSTITUTION_REJECTED(PrismStateGroup.INSTITUTION_REJECTED, null, null, PrismInstitutionRejected.class),
	INSTITUTION_WITHDRAWN(PrismStateGroup.INSTITUTION_WITHDRAWN, null, null, PrismInstitutionWithdrawn.class),
	SYSTEM_RUNNING(PrismStateGroup.SYSTEM_RUNNING, null, null, PrismSystemRunning.class);

	private static final HashMap<PrismState, PrismWorkflowState> workflowStateDefinitions = Maps.newHashMap();

	static {
		for (PrismState state : PrismState.values()) {
			Class<? extends PrismWorkflowState> workflowStateClass = state.getWorkflowStateClass();
			if (workflowStateClass != null) {
				PrismWorkflowState workflowState = BeanUtils.instantiate(workflowStateClass).initialize(state);
				workflowStateDefinitions.put(state, workflowState);
			}
		}
	}

	private PrismStateGroup stateGroup;

	private PrismStateDurationDefinition defaultDuration;

	private PrismStateDurationEvaluation stateDurationEvaluation;

	private Class<? extends PrismWorkflowState> workflowStateClass;

	private PrismState(PrismStateGroup stateGroup, PrismStateDurationDefinition defaultDuration, PrismStateDurationEvaluation stateDurationEvaluation,
	        Class<? extends PrismWorkflowState> workflowStateClass) {
		this.stateGroup = stateGroup;
		this.defaultDuration = defaultDuration;
		this.stateDurationEvaluation = stateDurationEvaluation;
		this.workflowStateClass = workflowStateClass;
	}

	public static List<PrismStateAction> getStateActions(PrismState state) {
		return workflowStateDefinitions.containsKey(state) ? workflowStateDefinitions.get(state).getStateActions() : new ArrayList<PrismStateAction>();
	}

	public static PrismStateAction getStateAction(PrismState state, PrismAction action) {
		return workflowStateDefinitions.containsKey(state) ? workflowStateDefinitions.get(state).getStateActionsByAction(action) : null;
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

	public Class<? extends PrismWorkflowState> getWorkflowStateClass() {
		return workflowStateClass;
	}

}
