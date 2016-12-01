package uk.co.alumeni.prism.domain.definitions.workflow;

import org.springframework.beans.BeanUtils;
import uk.co.alumeni.prism.domain.definitions.workflow.application.*;
import uk.co.alumeni.prism.domain.definitions.workflow.department.*;
import uk.co.alumeni.prism.domain.definitions.workflow.institution.*;
import uk.co.alumeni.prism.domain.definitions.workflow.program.*;
import uk.co.alumeni.prism.domain.definitions.workflow.project.*;
import uk.co.alumeni.prism.domain.definitions.workflow.system.PrismSystemRunning;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.selection.ApplicationReferenceSelectionResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.selection.StateTransitionSelectionResolver;
import uk.co.alumeni.prism.workflow.selectors.action.ApplicationByReferencesProvidedSelector;
import uk.co.alumeni.prism.workflow.selectors.action.PrismResourceByParentResourceSelector;

import java.util.HashMap;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationEvaluation.*;

public enum PrismState {

    APPLICATION_UNSUBMITTED(PrismStateGroup.APPLICATION_UNSUBMITTED, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationUnsubmitted.class),
    APPLICATION_VALIDATION(PrismStateGroup.APPLICATION_VALIDATION, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationValidation.class),
    APPLICATION_MESSAGING(PrismStateGroup.APPLICATION_MESSAGING, APPLICATION_MESSAGE_DURATION, null,
            PrismApplicationMessaging.class),
    APPLICATION_MESSAGING_PENDING_COMPLETION(PrismStateGroup.APPLICATION_MESSAGING, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationMessagingPendingCompletion.class),
    APPLICATION_REVIEW(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationReview.class),
    APPLICATION_REVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_PROVIDE_REVIEW_DURATION, null,
            PrismApplicationReviewPendingFeedback.class),
    APPLICATION_REVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_REVIEW, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationReviewPendingCompletion.class),
    APPLICATION_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationInterview.class),
    APPLICATION_INTERVIEW_PENDING_AVAILABILITY(PrismStateGroup.APPLICATION_INTERVIEW,
            APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION, null, PrismApplicationInterviewPendingAvailability.class),
    APPLICATION_INTERVIEW_PENDING_SCHEDULING(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationInterviewPendingScheduling.class),
    APPLICATION_INTERVIEW_PENDING_INTERVIEW(PrismStateGroup.APPLICATION_INTERVIEW, null, APPLICATION_INTERVIEW_DATE,
            PrismApplicationInterviewPendingInterview.class),
    APPLICATION_INTERVIEW_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_INTERVIEW,
            APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION, null, PrismApplicationInterviewPendingFeedback.class),
    APPLICATION_INTERVIEW_PENDING_COMPLETION(PrismStateGroup.APPLICATION_INTERVIEW, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationInterviewPendingCompletion.class),
    APPLICATION_APPROVAL(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationApproval.class),
    APPLICATION_APPROVAL_PENDING_FEEDBACK(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_CONFIRM_APPOINTMENT_DURATION,
            null, PrismApplicationApprovalPendingFeedback.class),
    APPLICATION_APPROVAL_PENDING_COMPLETION(PrismStateGroup.APPLICATION_APPROVAL, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationApprovalPendingCompletion.class),
    APPLICATION_REFERENCE(PrismStateGroup.APPLICATION_REFERENCE, APPLICATION_PROVIDE_REFERENCE_DURATION, null,
            PrismApplicationReference.class, ApplicationReferenceSelectionResolver.class, ApplicationByReferencesProvidedSelector.class),
    APPLICATION_REFERENCE_PENDING_COMPLETION(PrismStateGroup.APPLICATION_REFERENCE, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationReferencePendingCompletion.class),
    APPLICATION_RESERVED(PrismStateGroup.APPLICATION_RESERVED, APPLICATION_RESERVE_DURATION, null, PrismApplicationReserved.class),
    APPLICATION_RESERVED_PENDING_COMPLETION(PrismStateGroup.APPLICATION_RESERVED, APPLICATION_RESERVE_ESCALATE_DURATION, null,
            PrismApplicationReservedPendingCompletion.class),
    APPLICATION_APPROVED(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationApproved.class),
    APPLICATION_APPROVED_PENDING_PARTNER_APPROVAL(PrismStateGroup.APPLICATION_APPROVED, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationApprovedPendingPartnerAcceptance.class),
    APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE(PrismStateGroup.APPLICATION_APPROVED, null, null, PrismApplicationApprovedPendingOfferAcceptance.class),
    APPLICATION_APPROVED_PENDING_OFFER_REVISION(PrismStateGroup.APPLICATION_APPROVED, null, null, PrismApplicationApprovedPendingOfferRevision.class),
    APPLICATION_APPROVED_PENDING_OFFER_REVISION_ACCEPTANCE(PrismStateGroup.APPLICATION_APPROVED, null, null,
            PrismApplicationApprovedPendingOfferRevisionAcceptance.class),
    APPLICATION_ACCEPTED(PrismStateGroup.APPLICATION_ACCEPTED, null, null, PrismApplicationAccepted.class),
    APPLICATION_REJECTED(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null, PrismApplicationRejected.class),
    APPLICATION_REJECTED_COMPLETED(PrismStateGroup.APPLICATION_REJECTED, null, null, PrismApplicationRejectedCompleted.class),
    APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED(PrismStateGroup.APPLICATION_WITHDRAWN, null, null, PrismApplicationWithdrawnCompletedUnsubmitted.class),
    APPLICATION_WITHDRAWN_COMPLETED(PrismStateGroup.APPLICATION_WITHDRAWN, null, null, PrismApplicationWithdrawnCompleted.class),

    PROJECT_UNSUBMITTED(PrismStateGroup.PROJECT_UNSUBMITTED, null, null, PrismProjectUnsubmitted.class),
    PROJECT_APPROVAL_PARENT_APPROVAL(PrismStateGroup.PROJECT_APPROVAL, PROJECT_ESCALATE_DURATION, null,
            PrismProjectParentApproval.class),
    PROJECT_APPROVAL(PrismStateGroup.PROJECT_APPROVAL, PROJECT_ESCALATE_DURATION, null, PrismProjectApproval.class),
    PROJECT_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROJECT_APPROVAL, PROJECT_ESCALATE_DURATION, null,
            PrismProjectApprovalPendingCorrection.class),
    PROJECT_APPROVED(PrismStateGroup.PROJECT_APPROVED, null, PROJECT_CLOSING_DATE, PrismProjectApproved.class),
    PROJECT_DISABLED_COMPLETED(PrismStateGroup.PROJECT_DISABLED, null, null, PrismProjectDisabledCompleted.class),
    PROJECT_REJECTED(PrismStateGroup.PROJECT_REJECTED, null, null, PrismProjectRejected.class),
    PROJECT_WITHDRAWN(PrismStateGroup.PROJECT_WITHDRAWN, null, null, PrismProjectWithdrawn.class),

    PROGRAM_UNSUBMITTED(PrismStateGroup.PROGRAM_UNSUBMITTED, null, null, PrismProgramUnsubmitted.class),
    PROGRAM_APPROVAL_PARENT_APPROVAL(PrismStateGroup.PROGRAM_APPROVAL, PROGRAM_ESCALATE_DURATION, null,
            PrismProgramParentApproval.class),
    PROGRAM_APPROVAL(PrismStateGroup.PROGRAM_APPROVAL, PROGRAM_ESCALATE_DURATION, null,
            PrismProgramApproval.class),
    PROGRAM_APPROVAL_PENDING_CORRECTION(PrismStateGroup.PROGRAM_APPROVAL, PROGRAM_ESCALATE_DURATION, null,
            PrismProgramApprovalPendingCorrection.class),
    PROGRAM_APPROVED(PrismStateGroup.PROGRAM_APPROVED, null, PROGRAM_CLOSING_DATE, PrismProgramApproved.class),
    PROGRAM_DISABLED_COMPLETED(PrismStateGroup.PROGRAM_DISABLED, null, null, PrismProgramDisabledCompleted.class),
    PROGRAM_REJECTED(PrismStateGroup.PROGRAM_REJECTED, null, null, PrismProgramRejected.class),
    PROGRAM_WITHDRAWN(PrismStateGroup.PROGRAM_WITHDRAWN, null, null, PrismProgramWithdrawn.class),

    DEPARTMENT_UNSUBMITTED(PrismStateGroup.DEPARTMENT_UNSUBMITTED, null, null, PrismDepartmentUnsubmitted.class),
    DEPARTMENT_APPROVAL_PARENT_APPROVAL(PrismStateGroup.DEPARTMENT_APPROVAL, DEPARTMENT_ESCALATE_DURATION, null,
            PrismDepartmentParentApproval.class),
    DEPARTMENT_APPROVAL(PrismStateGroup.DEPARTMENT_APPROVAL, DEPARTMENT_ESCALATE_DURATION, null,
            PrismDepartmentApproval.class),
    DEPARTMENT_APPROVAL_PENDING_CORRECTION(PrismStateGroup.DEPARTMENT_APPROVAL, DEPARTMENT_ESCALATE_DURATION, null, PrismDepartmentApprovalPendingCorrection.class),
    DEPARTMENT_APPROVED(PrismStateGroup.DEPARTMENT_APPROVED, null, null, PrismDepartmentApproved.class),
    DEPARTMENT_DISABLED_COMPLETED(PrismStateGroup.DEPARTMENT_DISABLED, null, null, PrismDepartmentDisabledCompleted.class),
    DEPARTMENT_REJECTED(PrismStateGroup.DEPARTMENT_REJECTED, null, null, PrismDepartmentRejected.class),
    DEPARTMENT_WITHDRAWN(PrismStateGroup.DEPARTMENT_WITHDRAWN, null, null, PrismDepartmentWithdrawn.class),

    INSTITUTION_UNSUBMITTED(PrismStateGroup.INSTITUTION_UNSUBMITTED, null, null, PrismInstitutionUnsubmitted.class),
    INSTITUTION_APPROVAL(PrismStateGroup.INSTITUTION_APPROVAL, INSTITUTION_ESCALATE_DURATION, null,
            PrismInstitutionApproval.class),
    INSTITUTION_APPROVAL_PENDING_CORRECTION(PrismStateGroup.INSTITUTION_APPROVAL, INSTITUTION_ESCALATE_DURATION, null,
            PrismInstitutionApprovalPendingCorrection.class),
    INSTITUTION_APPROVED(PrismStateGroup.INSTITUTION_APPROVED, null, null, PrismInstitutionApproved.class),
    INSTITUTION_DISABLED_COMPLETED(PrismStateGroup.INSTITUTION_DISABLED, null, null, PrismInstitutionDisabledCompleted.class),
    INSTITUTION_REJECTED(PrismStateGroup.INSTITUTION_REJECTED, null, null, PrismInstitutionRejected.class),
    INSTITUTION_WITHDRAWN(PrismStateGroup.INSTITUTION_WITHDRAWN, null, null, PrismInstitutionWithdrawn.class),

    SYSTEM_RUNNING(PrismStateGroup.SYSTEM_RUNNING, null, null, PrismSystemRunning.class);

    private static final HashMap<PrismState, PrismWorkflowState> workflowStateDefinitions = newHashMap();

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

    private Class<? extends StateTransitionSelectionResolver> stateTransitionSelectionResolver;

    private Class<? extends PrismResourceByParentResourceSelector> replicableActionExclusionSelector;

    private PrismState(PrismStateGroup stateGroup, PrismStateDurationDefinition defaultDuration, PrismStateDurationEvaluation stateDurationEvaluation,
            Class<? extends PrismWorkflowState> workflowStateClass) {
        this.stateGroup = stateGroup;
        this.defaultDuration = defaultDuration;
        this.stateDurationEvaluation = stateDurationEvaluation;
        this.workflowStateClass = workflowStateClass;
    }

    private PrismState(PrismStateGroup stateGroup, PrismStateDurationDefinition defaultDuration, PrismStateDurationEvaluation stateDurationEvaluation,
            Class<? extends PrismWorkflowState> workflowStateClass, Class<? extends StateTransitionSelectionResolver> stateTransitionSelectionResolver) {
        this(stateGroup, defaultDuration, stateDurationEvaluation, workflowStateClass);
        this.stateTransitionSelectionResolver = stateTransitionSelectionResolver;
    }

    private PrismState(PrismStateGroup stateGroup, PrismStateDurationDefinition defaultDuration, PrismStateDurationEvaluation stateDurationEvaluation,
            Class<? extends PrismWorkflowState> workflowStateClass, Class<? extends StateTransitionSelectionResolver> stateTransitionSelectionResolver,
            Class<? extends PrismResourceByParentResourceSelector> replicableActionExclusionSelector) {
        this(stateGroup, defaultDuration, stateDurationEvaluation, workflowStateClass, stateTransitionSelectionResolver);
        this.replicableActionExclusionSelector = replicableActionExclusionSelector;
    }

    public static Set<PrismStateAction> getStateActions(PrismState state) {
        return workflowStateDefinitions.containsKey(state) ? workflowStateDefinitions.get(state).getStateActions() : newLinkedHashSet();
    }

    public static PrismStateAction getStateAction(PrismState state, PrismAction action) {
        return workflowStateDefinitions.containsKey(state) ? workflowStateDefinitions.get(state).getStateActionsByAction(action) : null;
    }

    public PrismStateGroup getStateGroup() {
        return stateGroup;
    }

    public PrismStateDurationDefinition getDefaultDuration() {
        return defaultDuration;
    }

    public PrismStateDurationEvaluation getStateDurationEvaluation() {
        return stateDurationEvaluation;
    }

    public Class<? extends PrismWorkflowState> getWorkflowStateClass() {
        return workflowStateClass;
    }

    public Class<? extends StateTransitionSelectionResolver> getStateTransitionSelectionResolver() {
        return stateTransitionSelectionResolver;
    }

    public Class<? extends PrismResourceByParentResourceSelector> getReplicableActionExclusionSelector() {
        return replicableActionExclusionSelector;
    }

}
