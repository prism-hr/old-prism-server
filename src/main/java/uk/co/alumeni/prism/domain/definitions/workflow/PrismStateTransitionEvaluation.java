package uk.co.alumeni.prism.domain.definitions.workflow;

import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.application.ApplicationAssignedInterviewerResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.application.ApplicationCompletedReferenceStateResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.application.ApplicationCompletedStateResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.application.ApplicationConfirmedInterviewResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.application.ApplicationConfirmedManagementResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.application.ApplicationConfirmedOfferOutcome;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.application.ApplicationProvidedInterviewAvailabilityResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.application.ApplicationProvidedInterviewFeedbackResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.application.ApplicationProvidedPartnerApprovalResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.application.ApplicationProvidedReferenceResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.application.ApplicationProvidedReviewResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.application.ApplicationUpdateInterviewAvailabilityResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.department.DepartmentApprovedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.department.DepartmentCompletedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.department.DepartmentCreatedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.department.DepartmentUpdatedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.institution.InstitutionApprovedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.institution.InstitutionCompletedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.institution.InstitutionCreatedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.institution.InstitutionUpdatedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.program.ProgramApprovedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.program.ProgramCompletedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.program.ProgramCreatedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.program.ProgramUpdatedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.project.ProjectApprovedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.project.ProjectCompletedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.project.ProjectCreatedResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.project.ProjectUpdatedResolver;

public enum PrismStateTransitionEvaluation {

    APPLICATION_CONFIRMED_OFFER_OUTCOME(false, PrismScope.APPLICATION, ApplicationConfirmedOfferOutcome.class), //
    APPLICATION_COMPLETED_STATE_OUTCOME(true, PrismScope.APPLICATION, ApplicationCompletedStateResolver.class), //
    APPLICATION_PROVIDED_HIRING_MANAGER_APPROVAL_OUTCOME(false, PrismScope.APPLICATION, ApplicationConfirmedManagementResolver.class), //
    APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME(false, PrismScope.APPLICATION, ApplicationProvidedInterviewAvailabilityResolver.class), //
    APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME(false, PrismScope.APPLICATION, ApplicationProvidedInterviewFeedbackResolver.class), //
    APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME(false, PrismScope.APPLICATION, ApplicationAssignedInterviewerResolver.class), //
    APPLICATION_CONFIRMED_INTERVIEW_OUTCOME(false, PrismScope.APPLICATION, ApplicationConfirmedInterviewResolver.class), //
    APPLICATION_PROVIDED_REFERENCE_OUTCOME(false, PrismScope.APPLICATION, ApplicationProvidedReferenceResolver.class), //
    APPLICATION_COMPLETED_REFERENCE_STATE_OUTCOME(true, PrismScope.APPLICATION, ApplicationCompletedReferenceStateResolver.class), //
    APPLICATION_PROVIDED_REVIEW_OUTCOME(false, PrismScope.APPLICATION, ApplicationProvidedReviewResolver.class), //
    APPLICATION_PROVIDED_PARTNER_APPROVAL_OUTCOME(false, PrismScope.APPLICATION, ApplicationProvidedPartnerApprovalResolver.class), //
    APPLICATION_UPDATED_INTERVIEW_AVAILABILITY_OUTCOME(false, PrismScope.APPLICATION, ApplicationUpdateInterviewAvailabilityResolver.class), //

    PROJECT_CREATED_OUTCOME(false, PrismScope.PROJECT, ProjectCreatedResolver.class), //
    PROJECT_COMPLETED_OUTCOME(false, PrismScope.PROJECT, ProjectCompletedResolver.class), //
    PROJECT_APPROVED_OUTCOME(true, PrismScope.PROJECT, ProjectApprovedResolver.class), //
    PROJECT_UPDATED_OUTCOME(true, PrismScope.PROJECT, ProjectUpdatedResolver.class), //

    PROGRAM_CREATED_OUTCOME(false, PrismScope.PROGRAM, ProgramCreatedResolver.class), //
    PROGRAM_COMPLETED_OUTCOME(false, PrismScope.PROGRAM, ProgramCompletedResolver.class), //
    PROGRAM_APPROVED_OUTCOME(true, PrismScope.PROGRAM, ProgramApprovedResolver.class), //
    PROGRAM_UPDATED_OUTCOME(true, PrismScope.PROGRAM, ProgramUpdatedResolver.class), //

    DEPARTMENT_CREATED_OUTCOME(false, PrismScope.DEPARTMENT, DepartmentCreatedResolver.class), //
    DEPARTMENT_COMPLETED_OUTCOME(false, PrismScope.DEPARTMENT, DepartmentCompletedResolver.class), //
    DEPARTMENT_APPROVED_OUTCOME(true, PrismScope.DEPARTMENT, DepartmentApprovedResolver.class), //
    DEPARTMENT_UPDATED_OUTCOME(true, PrismScope.DEPARTMENT, DepartmentUpdatedResolver.class), //

    INSTITUTION_CREATED_OUTCOME(false, PrismScope.INSTITUTION, InstitutionCreatedResolver.class), //
    INSTITUTION_COMPLETED_OUTCOME(false, PrismScope.INSTITUTION, InstitutionCompletedResolver.class), //
    INSTITUTION_APPROVED_OUTCOME(true, PrismScope.INSTITUTION, InstitutionApprovedResolver.class), //
    INSTITUTION_UPDATED_OUTCOME(true, PrismScope.INSTITUTION, InstitutionUpdatedResolver.class);

    private boolean nextStateSelection;

    private PrismScope scope;

    private Class<? extends StateTransitionResolver<?>> resolver;

    PrismStateTransitionEvaluation(boolean nextStateSelection, PrismScope scope, Class<? extends StateTransitionResolver<?>> resolver) {
        this.nextStateSelection = nextStateSelection;
        this.scope = scope;
        this.resolver = resolver;
    }

    public final boolean isNextStateSelection() {
        return nextStateSelection;
    }

    public final PrismScope getScope() {
        return scope;
    }

    public Class<? extends StateTransitionResolver<?>> getResolver() {
        return resolver;
    }

}
