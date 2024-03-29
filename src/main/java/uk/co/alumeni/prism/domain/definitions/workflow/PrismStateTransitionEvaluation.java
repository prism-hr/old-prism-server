package uk.co.alumeni.prism.domain.definitions.workflow;

import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.application.*;
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

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.*;

public enum PrismStateTransitionEvaluation {

    APPLICATION_CONFIRMED_OFFER_OUTCOME(false, APPLICATION, ApplicationConfirmedOfferResolver.class), //
    APPLICATION_CONFIRMED_OFFER_ACCEPTANCE_OUTCOME(false, APPLICATION, ApplicationConfirmedOfferAcceptanceResolver.class), //
    APPLICATION_COMPLETED_STATE_OUTCOME(true, APPLICATION, ApplicationCompletedStateResolver.class), //
    APPLICATION_PROVIDED_HIRING_MANAGER_APPROVAL_OUTCOME(false, APPLICATION, ApplicationConfirmedManagementResolver.class), //
    APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME(false, APPLICATION, ApplicationProvidedInterviewAvailabilityResolver.class), //
    APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME(false, APPLICATION, ApplicationProvidedInterviewFeedbackResolver.class), //
    APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME(false, APPLICATION, ApplicationAssignedInterviewerResolver.class), //
    APPLICATION_CONFIRMED_INTERVIEW_OUTCOME(false, APPLICATION, ApplicationConfirmedInterviewResolver.class), //
    APPLICATION_PROVIDED_REFERENCE_OUTCOME(false, APPLICATION, ApplicationProvidedReferenceResolver.class), //
    APPLICATION_COMPLETED_REFERENCE_STATE_OUTCOME(true, APPLICATION, ApplicationCompletedReferenceStateResolver.class), //
    APPLICATION_PROVIDED_REVIEW_OUTCOME(false, APPLICATION, ApplicationProvidedReviewResolver.class), //
    APPLICATION_PROVIDED_PARTNER_APPROVAL_OUTCOME(false, APPLICATION, ApplicationProvidedPartnerApprovalResolver.class), //
    APPLICATION_UPDATED_INTERVIEW_AVAILABILITY_OUTCOME(false, APPLICATION, ApplicationUpdateInterviewAvailabilityResolver.class), //

    PROJECT_CREATED_OUTCOME(false, PROJECT, ProjectCreatedResolver.class), //
    PROJECT_COMPLETED_OUTCOME(false, PROJECT, ProjectCompletedResolver.class), //
    PROJECT_APPROVED_OUTCOME(true, PROJECT, ProjectApprovedResolver.class), //
    PROJECT_UPDATED_OUTCOME(true, PROJECT, ProjectUpdatedResolver.class), //

    PROGRAM_CREATED_OUTCOME(false, PROGRAM, ProgramCreatedResolver.class), //
    PROGRAM_COMPLETED_OUTCOME(false, PROGRAM, ProgramCompletedResolver.class), //
    PROGRAM_APPROVED_OUTCOME(true, PROGRAM, ProgramApprovedResolver.class), //
    PROGRAM_UPDATED_OUTCOME(true, PROGRAM, ProgramUpdatedResolver.class), //

    DEPARTMENT_CREATED_OUTCOME(false, DEPARTMENT, DepartmentCreatedResolver.class), //
    DEPARTMENT_COMPLETED_OUTCOME(false, DEPARTMENT, DepartmentCompletedResolver.class), //
    DEPARTMENT_APPROVED_OUTCOME(true, DEPARTMENT, DepartmentApprovedResolver.class), //
    DEPARTMENT_UPDATED_OUTCOME(true, DEPARTMENT, DepartmentUpdatedResolver.class), //

    INSTITUTION_CREATED_OUTCOME(false, INSTITUTION, InstitutionCreatedResolver.class), //
    INSTITUTION_COMPLETED_OUTCOME(false, INSTITUTION, InstitutionCompletedResolver.class), //
    INSTITUTION_APPROVED_OUTCOME(true, INSTITUTION, InstitutionApprovedResolver.class), //
    INSTITUTION_UPDATED_OUTCOME(true, INSTITUTION, InstitutionUpdatedResolver.class);

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
