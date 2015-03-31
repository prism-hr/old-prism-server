package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_INTERVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_REVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_SUPERVISORS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_ELIGIBILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_REJECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_RESERVE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_IMPORT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_SUSPEND;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_TERMINATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
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
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_COMPLETED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_ESCALATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_WITHDRAWN_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.INSTITUTION_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_CREATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_ESCALATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_IMPORTED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROGRAM_UPDATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_APPROVED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_CREATED_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.PROJECT_UPDATED_OUTCOME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleTransitionGroup;
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

	APPLICATION_UNSUBMITTED(PrismStateGroup.APPLICATION_UNSUBMITTED, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationUnsubmitted.class),
	APPLICATION_UNSUBMITTED_PENDING_COMPLETION(PrismStateGroup.APPLICATION_UNSUBMITTED, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationUnsubmittedPendingCompletion.class),
	APPLICATION_VALIDATION(PrismStateGroup.APPLICATION_VALIDATION, null, APPLICATION_CLOSING_DATE, false, false, PrismApplicationValidation.class),
	APPLICATION_VALIDATION_PENDING_COMPLETION(PrismStateGroup.APPLICATION_VALIDATION, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationValidationPendingCompletion.class),
	APPLICATION_VERIFICATION(PrismStateGroup.APPLICATION_VERIFICATION, APPLICATION_CONFIRM_ELIGIBILITY_DURATION, null, true, false,
	        PrismApplicationVerification.class),
	APPLICATION_VERIFICATION_PENDING_COMPLETION(PrismStateGroup.APPLICATION_VERIFICATION, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationVerificationPendingCompletion.class),
	APPLICATION_REFERENCE(PrismStateGroup.APPLICATION_REFERENCE, APPLICATION_PROVIDE_REFERENCE_DURATION, null, true, false,
	        PrismApplicationReference.class),
	APPLICATION_REFERENCE_PENDING_COMPLETION(PrismStateGroup.APPLICATION_REFERENCE, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationReferencePendingCompletion.class),
	APPLICATION_REVIEW(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_ESCALATE_DURATION, null, false, false, PrismApplicationReview.class),
	APPLICATION_REVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_PROVIDE_REVIEW_DURATION, null, false, false,
	        PrismApplicationReviewPendingFeedback.class),
	APPLICATION_REVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationReviewPendingCompletion.class),
	APPLICATION_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationInterview.class),
	APPLICATION_INTERVIEW_PENDING_AVAILABILITY(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION, null, false, false,
	        PrismApplicationInterviewPendingAvailability.class),
	APPLICATION_INTERVIEW_PENDING_SCHEDULING(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationInterviewPendingScheduling.class),
	APPLICATION_INTERVIEW_PENDING_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, null, APPLICATION_INTERVIEW_DATE, false, false,
	        PrismApplicationInterviewPendingInterview.class),
	APPLICATION_INTERVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION, null, false, false,
	        PrismApplicationInterviewPendingFeedback.class),
	APPLICATION_INTERVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationInterviewPendingCompletion.class),
	APPLICATION_APPROVAL(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_ESCALATE_DURATION, null, false, false, PrismApplicationApproval.class),
	APPLICATION_APPROVAL_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_CONFIRM_SUPERVISION_DURATION, null, false, false,
	        PrismApplicationApprovalPendingFeedback.class),
	APPLICATION_APPROVAL_PENDING_COMPLETION(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationApprovalPendingCompletion.class),
	APPLICATION_APPROVED(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null, false, false, PrismApplicationApproved.class),
	APPLICATION_APPROVED_PENDING_EXPORT(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationApprovedPendingExport.class),
	APPLICATION_APPROVED_PENDING_CORRECTION(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationApprovedPendingCorrection.class),
	APPLICATION_APPROVED_COMPLETED(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_PURGE_DURATION, null, false, false,
	        PrismApplicationApprovedCompleted.class),
	APPLICATION_APPROVED_COMPLETED_PURGED(PrismStateGroup.APPLICATION_APPROVED, null, null, false, true, null),
	APPLICATION_REJECTED(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null, false, false, PrismApplicationRejected.class),
	APPLICATION_REJECTED_PENDING_EXPORT(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationRejectedPendingExport.class),
	APPLICATION_REJECTED_PENDING_CORRECTION(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationRejectedPendingCorrection.class),
	APPLICATION_REJECTED_COMPLETED(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_PURGE_DURATION, null, false, false,
	        PrismApplicationRejectedCompleted.class),
	APPLICATION_REJECTED_COMPLETED_PURGED(PrismStateGroup.APPLICATION_REJECTED, null, null, false, true, null),
	APPLICATION_RESERVED(PrismStateGroup.APPLICATION_RESERVED, APPLICATION_ESCALATE_DURATION, null, false, false, PrismApplicationReserved.class),
	APPLICATION_RESERVED_WAITING(PrismStateGroup.APPLICATION_RESERVED, APPLICATION_RESERVE_DURATION, null, false, false,
	        PrismApplicationReservedWaiting.class),
	APPLICATION_RESERVED_PENDING_REALLOCATION(PrismStateGroup.APPLICATION_RESERVED, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationReservedPendingReallocation.class),
	APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_PURGE_DURATION, null, false, false,
	        PrismApplicationWithdrawnCompletedUnsubmitted.class),
	APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED_PURGED(PrismStateGroup.APPLICATION_WITHDRAWN, null, null, false, false, null),
	APPLICATION_WITHDRAWN_PENDING_EXPORT(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationWithdrawnPendingExport.class),
	APPLICATION_WITHDRAWN_PENDING_CORRECTION(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_ESCALATE_DURATION, null, false, false,
	        PrismApplicationWithdrawnPendingCorrection.class),
	APPLICATION_WITHDRAWN_COMPLETED(PrismStateGroup.APPLICATION_WITHDRAWN, APPLICATION_PURGE_DURATION, null, false, false,
	        PrismApplicationWithdrawnCompleted.class),
	APPLICATION_WITHDRAWN_COMPLETED_PURGED(PrismStateGroup.APPLICATION_WITHDRAWN, null, null, false, true, null),
	PROJECT_APPROVAL(PrismStateGroup.PROJECT_APPROVAL, PROJECT_ESCALATE_DURATION, null, false, false, PrismProjectApproval.class),
	PROJECT_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROJECT_APPROVAL, PROJECT_ESCALATE_DURATION, null, false, false,
	        PrismProjectApprovalPendingCorrection.class),
	PROJECT_APPROVED(PrismStateGroup.PROJECT_APPROVED, null, PROJECT_END_DATE, false, false, PrismProjectApproved.class),
	PROJECT_DEACTIVATED(PrismStateGroup.PROJECT_APPROVED, null, PROJECT_END_DATE, false, false, PrismProjectDeactivated.class),
	PROJECT_DISABLED_PENDING_REACTIVATION(PrismStateGroup.PROJECT_DISABLED, PROJECT_ESCALATE_DURATION, null, false, false,
	        PrismProjectDisabledPendingReactivation.class),
	PROJECT_DISABLED_COMPLETED(PrismStateGroup.PROJECT_DISABLED, null, null, false, false, PrismProjectDisabledCompleted.class),
	PROJECT_REJECTED(PrismStateGroup.PROJECT_REJECTED, null, null, false, false, PrismProjectRejected.class),
	PROJECT_WITHDRAWN(PrismStateGroup.PROJECT_WITHDRAWN, null, null, false, false, PrismProjectWithdrawn.class),
	PROGRAM_APPROVAL(PrismStateGroup.PROGRAM_APPROVAL, PROGRAM_ESCALATE_DURATION, null, false, false, PrismProgramApproval.class),
	PROGRAM_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROGRAM_APPROVAL, PROGRAM_ESCALATE_DURATION, null, false, false,
	        PrismProgramApprovalPendingCorrection.class),
	PROGRAM_APPROVED(PrismStateGroup.PROGRAM_APPROVED, null, PROGRAM_END_DATE, false, false, PrismProgramApproved.class),
	PROGRAM_DEACTIVATED(PrismStateGroup.PROGRAM_APPROVED, null, PROGRAM_END_DATE, false, false, PrismProgramDeactivated.class),
	PROGRAM_DISABLED_PENDING_REACTIVATION(PrismStateGroup.PROGRAM_DISABLED, PROGRAM_ESCALATE_DURATION, null, false, false,
	        PrismProgramDisabledPendingReactivation.class),
	PROGRAM_DISABLED_COMPLETED(PrismStateGroup.PROGRAM_DISABLED, null, null, false, false, PrismProgramDisabledCompleted.class),
	PROGRAM_REJECTED(PrismStateGroup.PROGRAM_REJECTED, null, null, false, false, PrismProgramRejected.class),
	PROGRAM_WITHDRAWN(PrismStateGroup.PROGRAM_WITHDRAWN, null, null, false, false, PrismProgramWithdrawn.class),
	INSTITUTION_APPROVAL(PrismStateGroup.INSTITUTION_APPROVAL, INSTITUTION_ESCALATE_DURATION, null, false, false, PrismInstitutionApproval.class),
	INSTITUTION_APPROVAL_PENDING_CORRECTION(PrismStateGroup.INSTITUTION_APPROVAL, INSTITUTION_ESCALATE_DURATION, null, false, false,
	        PrismInstitutionApprovalPendingCorrection.class),
	INSTITUTION_APPROVED(PrismStateGroup.INSTITUTION_APPROVED, null, null, false, false, PrismInstitutionApproved.class),
	INSTITUTION_APPROVED_COMPLETED(PrismStateGroup.INSTITUTION_APPROVED, null, null, false, false, PrismInstitutionApprovedCompleted.class),
	INSTITUTION_REJECTED(PrismStateGroup.INSTITUTION_REJECTED, null, null, false, false, PrismInstitutionRejected.class),
	INSTITUTION_WITHDRAWN(PrismStateGroup.INSTITUTION_WITHDRAWN, null, null, false, false, PrismInstitutionWithdrawn.class),
	SYSTEM_RUNNING(PrismStateGroup.SYSTEM_RUNNING, null, null, false, false, PrismSystemRunning.class);

	private static final HashMap<PrismState, PrismWorkflowState> workflowStateDefinitions = Maps.newHashMap();

	static {
		for (PrismState state : PrismState.values()) {
			Class<? extends PrismWorkflowState> workflowStateClass = state.getWorkflowStateClass();
			if (workflowStateClass != null) {
				PrismWorkflowState workflowState = BeanUtils.instantiate(workflowStateClass);
				workflowStateDefinitions.put(state, workflowState);
			}
		}
	}

	private PrismStateGroup stateGroup;

	private PrismStateDurationDefinition defaultDuration;

	private PrismStateDurationEvaluation stateDurationEvaluation;

	private boolean parallelizable;

	private boolean hidden;

	private Class<? extends PrismWorkflowState> workflowStateClass;

	private PrismState(PrismStateGroup stateGroup, PrismStateDurationDefinition defaultDuration, PrismStateDurationEvaluation stateDurationEvaluation,
	        boolean parallelizable, boolean hidden, Class<? extends PrismWorkflowState> workflowStateClass) {
		this.stateGroup = stateGroup;
		this.defaultDuration = defaultDuration;
		this.stateDurationEvaluation = stateDurationEvaluation;
		this.parallelizable = parallelizable;
		this.hidden = hidden;
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

	public final boolean isParallelizable() {
		return parallelizable;
	}

	public final boolean isHidden() {
		return hidden;
	}

	public Class<? extends PrismWorkflowState> getWorkflowStateClass() {
		return workflowStateClass;
	}

	public enum PrismStateTransitionGroup {

		APPLICATION_COMPLETE_TRANSITION( //
		        new PrismStateTransition() //
		                .withTransitionState(APPLICATION_VALIDATION) //
		                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                .withTransitionEvaluation(APPLICATION_COMPLETED_OUTCOME), //
		        new PrismStateTransition() //
		                .withTransitionState(APPLICATION_VALIDATION_PENDING_COMPLETION) //
		                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                .withTransitionEvaluation(APPLICATION_COMPLETED_OUTCOME)),

		APPLICATION_COMPLETE_STATE_TRANSITION( //
		        new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REVIEW) //
		                .withTransitionAction(APPLICATION_ASSIGN_REVIEWERS) //
		                .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
		        new PrismStateTransition() //
		                .withTransitionState(APPLICATION_VERIFICATION) //
		                .withTransitionAction(APPLICATION_CONFIRM_ELIGIBILITY) //
		                .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
		        new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REFERENCE) //
		                .withTransitionAction(APPLICATION_PROVIDE_REFERENCE) //
		                .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
		        new PrismStateTransition() //
		                .withTransitionState(APPLICATION_INTERVIEW) //
		                .withTransitionAction(APPLICATION_ASSIGN_INTERVIEWERS) //
		                .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
		        new PrismStateTransition() //
		                .withTransitionState(APPLICATION_APPROVAL) //
		                .withTransitionAction(APPLICATION_ASSIGN_SUPERVISORS) //
		                .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
		        new PrismStateTransition() //
		                .withTransitionState(APPLICATION_APPROVED) //
		                .withTransitionAction(APPLICATION_CONFIRM_OFFER_RECOMMENDATION) //
		                .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
		        new PrismStateTransition() //
		                .withTransitionState(APPLICATION_RESERVED) //
		                .withTransitionAction(APPLICATION_RESERVE) //
		                .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME), //
		        new PrismStateTransition().withTransitionState(APPLICATION_REJECTED) //
		                .withTransitionAction(APPLICATION_CONFIRM_REJECTION) //
		                .withTransitionEvaluation(APPLICATION_COMPLETED_STATE_OUTCOME)), //

		APPLICATION_WITHDRAW_SUBMITTED_TRANSITION( //
		        new PrismStateTransition() //
		                .withTransitionState(APPLICATION_WITHDRAWN_PENDING_EXPORT) //
		                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                .withTransitionEvaluation(APPLICATION_WITHDRAWN_OUTCOME), //
		        new PrismStateTransition() //
		                .withTransitionState(APPLICATION_WITHDRAWN_COMPLETED) //
		                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                .withTransitionEvaluation(APPLICATION_WITHDRAWN_OUTCOME)), //

		APPLICATION_ESCALATE_SUBMITTED_TRANSITION( //
		        new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REJECTED_PENDING_EXPORT) //
		                .withTransitionAction(APPLICATION_ESCALATE) //
		                .withTransitionEvaluation(APPLICATION_ESCALATED_OUTCOME), //
		        new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REJECTED_COMPLETED) //
		                .withTransitionAction(APPLICATION_ESCALATE) //
		                .withTransitionEvaluation(APPLICATION_ESCALATED_OUTCOME)), //

		PROJECT_CREATE_TRANSITION( //
		        new PrismStateTransition() //
		                .withTransitionState(PROJECT_APPROVAL) //
		                .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
		                .withTransitionEvaluation(PROJECT_CREATED_OUTCOME),
		        new PrismStateTransition() //
		                .withTransitionState(PROJECT_APPROVED) //
		                .withTransitionAction(PROJECT_VIEW_EDIT) //
		                .withTransitionEvaluation(PROJECT_CREATED_OUTCOME)), //

		PROJECT_APPROVE_TRANSITION( //
		        new PrismStateTransition() //
		                .withTransitionState(PROJECT_APPROVED) //
		                .withTransitionAction(PROJECT_VIEW_EDIT) //
		                .withTransitionEvaluation(PROJECT_APPROVED_OUTCOME), //
		        new PrismStateTransition() //
		                .withTransitionState(PROJECT_REJECTED) //
		                .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST) //
		                .withTransitionEvaluation(PROJECT_APPROVED_OUTCOME)), //

		PROJECT_VIEW_EDIT_TRANSITION( //
		        new PrismStateTransition() //
		                .withTransitionState(PROJECT_APPROVED) //
		                .withTransitionAction(PROJECT_VIEW_EDIT) //
		                .withTransitionEvaluation(PROJECT_UPDATED_OUTCOME),
		        new PrismStateTransition() //
		                .withTransitionState(PROJECT_DEACTIVATED) //
		                .withTransitionAction(PROJECT_VIEW_EDIT) //
		                .withTransitionEvaluation(PROJECT_UPDATED_OUTCOME)), //

		PROGRAM_CREATE_TRANSITION( //
		        new PrismStateTransition() //
		                .withTransitionState(PROGRAM_APPROVAL) //
		                .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
		                .withTransitionEvaluation(PROGRAM_CREATED_OUTCOME),
		        new PrismStateTransition() //
		                .withTransitionState(PROGRAM_DEACTIVATED) //
		                .withTransitionAction(PROGRAM_VIEW_EDIT) //
		                .withTransitionEvaluation(PROGRAM_CREATED_OUTCOME)), //

		PROGRAM_IMPORT_TRANSITION( //
		        new PrismStateTransition() //
		                .withTransitionState(PROGRAM_APPROVED) //
		                .withTransitionAction(INSTITUTION_IMPORT_PROGRAM) //
		                .withTransitionEvaluation(PROGRAM_IMPORTED_OUTCOME),
		        new PrismStateTransition() //
		                .withTransitionState(PROGRAM_DEACTIVATED) //
		                .withTransitionAction(INSTITUTION_IMPORT_PROGRAM) //
		                .withTransitionEvaluation(PROGRAM_IMPORTED_OUTCOME)), //

		PROGRAM_APPROVE_TRANSITION( //
		        new PrismStateTransition() //
		                .withTransitionState(PROGRAM_APPROVED) //
		                .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
		                .withTransitionEvaluation(PROGRAM_APPROVED_OUTCOME), //
		        new PrismStateTransition() //
		                .withTransitionState(PROGRAM_REJECTED) //
		                .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST) //
		                .withTransitionEvaluation(PROGRAM_APPROVED_OUTCOME)), //

		PROGRAM_VIEW_EDIT_TRANSITION( //
		        new PrismStateTransition() //
		                .withTransitionState(PROGRAM_APPROVED) //
		                .withTransitionAction(PROGRAM_VIEW_EDIT) //
		                .withTransitionEvaluation(PROGRAM_UPDATED_OUTCOME),
		        new PrismStateTransition() //
		                .withTransitionState(PROGRAM_DEACTIVATED) //
		                .withTransitionAction(PROGRAM_VIEW_EDIT) //
		                .withTransitionEvaluation(PROGRAM_UPDATED_OUTCOME)), //

		PROGRAM_ESCALATE_APPROVED_TRANSITION_GROUP( //
		        new PrismStateTransition() //
		                .withTransitionState(PROGRAM_DISABLED_COMPLETED) //
		                .withTransitionAction(PROGRAM_ESCALATE) //
		                .withTransitionEvaluation(PROGRAM_ESCALATED_OUTCOME) //
		                .withPropagatedActions(PROJECT_TERMINATE),
		        new PrismStateTransition() //
		                .withTransitionState(PROGRAM_DISABLED_PENDING_REACTIVATION) //
		                .withTransitionAction(PROGRAM_ESCALATE) //
		                .withTransitionEvaluation(PROGRAM_ESCALATED_OUTCOME) //
		                .withPropagatedActions(PROJECT_SUSPEND)), //

		INSTITUTION_APPROVE_TRANSITION( //
		        new PrismStateTransition() //
		                .withTransitionState(INSTITUTION_APPROVED) //
		                .withTransitionAction(INSTITUTION_VIEW_EDIT) //
		                .withTransitionEvaluation(INSTITUTION_APPROVED_OUTCOME), //
		        new PrismStateTransition() //
		                .withTransitionState(PrismState.INSTITUTION_REJECTED) //
		                .withTransitionAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST)
		                .withTransitionEvaluation(INSTITUTION_APPROVED_OUTCOME));

		private PrismStateTransition[] stateTransitionTemplates;

		private Map<PrismState, PrismStateTransition> index = Maps.newLinkedHashMap();

		private PrismStateTransitionGroup(PrismStateTransition... stateTransitions) {
			this.stateTransitionTemplates = stateTransitions;
			for (PrismStateTransition stateTransition : getStateTransitions()) {
				index.put(stateTransition.getTransitionState(), stateTransition);
			}
		}

		public PrismStateTransition[] getStateTransitions() {
			return stateTransitionTemplates;
		}

		public PrismStateTransition getStateTransition(PrismState transitionState) {
			return index.get(transitionState);
		}

		public PrismStateTransition[] withRoleTransitions(PrismRoleTransitionGroup roleTransitionGroup) {
			return withRoleTransitionsAndStateTerminations(Lists.newArrayList(roleTransitionGroup.getRoleTransitions()),
			        Collections.<PrismStateTermination> emptyList());
		}

		public PrismStateTransition[] withRoleTransitions(List<PrismRoleTransition> roleTransitions) {
			return withRoleTransitionsAndStateTerminations(roleTransitions, Collections.<PrismStateTermination> emptyList());
		}

		public PrismStateTransition[] withRoleTransitionsAndStateTerminations(PrismRoleTransitionGroup roleTransitionGroup,
		        PrismStateTerminationGroup stateTerminationGroup) {
			return withRoleTransitionsAndStateTerminations(Lists.newArrayList(roleTransitionGroup.getRoleTransitions()),
			        Lists.newArrayList(stateTerminationGroup.getStateTerminations()));
		}

		public PrismStateTransition[] withRoleTransitionsAndStateTerminations(List<PrismRoleTransition> roleTransitions,
		        List<PrismStateTermination> stateTerminations) {
			List<PrismStateTransition> stateTransitions = Lists.newLinkedList();
			for (PrismStateTransition stateTransition : getStateTransitions()) {
				stateTransitions.add(new PrismStateTransition() //
				        .withTransitionState(stateTransition.getTransitionState()) //
				        .withTransitionAction(stateTransition.getTransitionAction()) //
				        .withTransitionEvaluation(stateTransition.getTransitionEvaluation()) //
				        .withRoleTransitions(roleTransitions.toArray(new PrismRoleTransition[roleTransitions.size()])) //
				        .withStateTerminations(stateTerminations.toArray(new PrismStateTermination[stateTerminations.size()])));
			}
			return stateTransitions.toArray(new PrismStateTransition[stateTransitions.size()]);
		}
	}

	public enum PrismStateTerminationGroup {

		APPLICATION_TERMINATE_GROUP( //
		        new PrismStateTermination() //
		                .withTerminationState(APPLICATION_REFERENCE), //
		        new PrismStateTermination() //
		                .withTerminationState(APPLICATION_VERIFICATION));

		private PrismStateTermination[] stateTerminations;

		private PrismStateTerminationGroup(PrismStateTermination... stateTerminations) {
			this.stateTerminations = stateTerminations;
		}

		public PrismStateTermination[] getStateTerminations() {
			return stateTerminations;
		}

	}

}
