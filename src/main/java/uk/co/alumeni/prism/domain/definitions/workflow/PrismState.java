package uk.co.alumeni.prism.domain.definitions.workflow;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_CONFIRM_APPOINTMENT_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_ESCALATE_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_MESSAGE_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PROVIDE_REFERENCE_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_PROVIDE_REVIEW_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_RESERVE_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.APPLICATION_RESERVE_ESCALATE_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.DEPARTMENT_ESCALATE_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.INSTITUTION_ESCALATE_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.PROGRAM_ESCALATE_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationDefinition.PROJECT_ESCALATE_DURATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationEvaluation.APPLICATION_INTERVIEW_DATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationEvaluation.PROGRAM_CLOSING_DATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateDurationEvaluation.PROJECT_CLOSING_DATE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.BeanUtils;

import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproval;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApprovalPendingCompletion;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApprovalPendingFeedback;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproved;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApprovedCompleted;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApprovedPendingOfferAcceptance;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApprovedPendingOfferRevision;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApprovedPendingOfferRevisionAcceptance;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApprovedPendingPartnerAcceptance;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterviewPendingAvailability;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterviewPendingCompletion;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterviewPendingFeedback;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterviewPendingInterview;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterviewPendingScheduling;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationMessaging;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationMessagingPendingCompletion;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReference;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReferencePendingCompletion;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationRejected;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationRejectedCompleted;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReserved;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReservedPendingCompletion;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReviewPendingCompletion;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReviewPendingFeedback;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationUnsubmitted;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationValidation;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWithdrawnCompleted;
import uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWithdrawnCompletedUnsubmitted;
import uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentApproval;
import uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentApprovalPendingCorrection;
import uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentApproved;
import uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentDisabledCompleted;
import uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentParentApproval;
import uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentRejected;
import uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentUnsubmitted;
import uk.co.alumeni.prism.domain.definitions.workflow.department.PrismDepartmentWithdrawn;
import uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionApproval;
import uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionApprovalPendingCorrection;
import uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionApproved;
import uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionDisabledCompleted;
import uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionRejected;
import uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionUnsubmitted;
import uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWithdrawn;
import uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramApproval;
import uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramApprovalPendingCorrection;
import uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramApproved;
import uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramDisabledCompleted;
import uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramParentApproval;
import uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramRejected;
import uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramUnsubmitted;
import uk.co.alumeni.prism.domain.definitions.workflow.program.PrismProgramWithdrawn;
import uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectApproval;
import uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectApprovalPendingCorrection;
import uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectApproved;
import uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectDisabledCompleted;
import uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectParentApproval;
import uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectRejected;
import uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectUnsubmitted;
import uk.co.alumeni.prism.domain.definitions.workflow.project.PrismProjectWithdrawn;
import uk.co.alumeni.prism.domain.definitions.workflow.system.PrismSystemRunning;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.selection.ApplicationReferenceSelectionResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.selection.StateTransitionSelectionResolver;
import uk.co.alumeni.prism.workflow.selectors.action.ApplicationByReferencesProvidedSelector;
import uk.co.alumeni.prism.workflow.selectors.action.PrismResourceByParentResourceSelector;

import com.google.common.collect.Maps;

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
    APPLICATION_APPROVED_COMPLETED(PrismStateGroup.APPLICATION_APPROVED, null, null, PrismApplicationApprovedCompleted.class),
    APPLICATION_REJECTED(PrismStateGroup.APPLICATION_REJECTED, APPLICATION_ESCALATE_DURATION, null,
            PrismApplicationRejected.class),
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
    DEPARTMENT_APPROVAL_PENDING_CORRECTION(PrismStateGroup.DEPARTMENT_APPROVAL, DEPARTMENT_ESCALATE_DURATION, null,
            PrismDepartmentApprovalPendingCorrection.class),
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

    public static List<PrismStateAction> getStateActions(PrismState state) {
        return workflowStateDefinitions.containsKey(state) ? workflowStateDefinitions.get(state).getStateActions() : new ArrayList<PrismStateAction>();
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
