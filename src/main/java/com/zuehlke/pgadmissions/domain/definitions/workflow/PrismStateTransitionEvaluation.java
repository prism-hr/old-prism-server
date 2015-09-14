package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationAssignedInterviewerResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationAssignedReviewerResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationAssignedSupervisorResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationCompletedReferenceStateResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationCompletedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationCompletedStateResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationCompletedVerificationStateResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationConfirmedEligibilityResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationConfirmedInterviewResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationConfirmedManagementResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationIdentifiedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationProvidedInterviewAvailabilityResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationProvidedInterviewFeedbackResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationProvidedReferenceResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationProvidedReviewResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationPurgedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.ApplicationUpdateInterviewAvailabilityResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.department.DepartmentApprovedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.department.DepartmentCreatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.department.DepartmentUpdatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.institution.InstitutionApprovedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.institution.InstitutionCreatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.institution.InstitutionUpdatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.program.ProgramApprovedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.program.ProgramCreatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.program.ProgramUpdatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.project.ProjectApprovedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.project.ProjectCreatedResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.project.ProjectUpdatedResolver;

public enum PrismStateTransitionEvaluation {

    APPLICATION_COMPLETED_OUTCOME(false, APPLICATION, ApplicationCompletedResolver.class), //
    APPLICATION_CONFIRMED_APPOINTMENT_OUTCOME(false, APPLICATION, ApplicationConfirmedManagementResolver.class), //
    APPLICATION_COMPLETED_STATE_OUTCOME(true, APPLICATION, ApplicationCompletedStateResolver.class), //
    APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME(false, APPLICATION, ApplicationProvidedInterviewAvailabilityResolver.class), //
    APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME(false, APPLICATION, ApplicationProvidedInterviewFeedbackResolver.class), //
    APPLICATION_ASSIGNED_REVIEWER_OUTCOME(false, APPLICATION, ApplicationAssignedReviewerResolver.class), //
    APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME(false, APPLICATION, ApplicationAssignedInterviewerResolver.class), //
    APPLICATION_ASSIGNED_SUPERVISOR_OUTCOME(false, APPLICATION, ApplicationAssignedSupervisorResolver.class), //
    APPLICATION_CONFIRMED_INTERVIEW_OUTCOME(false, APPLICATION, ApplicationConfirmedInterviewResolver.class), //
    APPLICATION_CONFIRMED_ELIGIBILITY_OUTCOME(false, APPLICATION, ApplicationConfirmedEligibilityResolver.class), //
    APPLICATION_PROVIDED_REFERENCE_OUTCOME(false, APPLICATION, ApplicationProvidedReferenceResolver.class), //
    APPLICATION_COMPLETED_VERIFICATION_STATE_OUTCOME(true, APPLICATION, ApplicationCompletedVerificationStateResolver.class), //
    APPLICATION_COMPLETED_REFERENCE_STATE_OUTCOME(true, APPLICATION, ApplicationCompletedReferenceStateResolver.class), //
    APPLICATION_PROVIDED_REVIEW_OUTCOME(false, APPLICATION, ApplicationProvidedReviewResolver.class), //
    APPLICATION_UPDATED_INTERVIEW_AVAILABILITY_OUTCOME(false, APPLICATION, ApplicationUpdateInterviewAvailabilityResolver.class), //
    APPLICATION_PURGED_OUTCOME(false, APPLICATION, ApplicationPurgedResolver.class), //
    APPLICATION_IDENTIFIED_OUTCOME(false, APPLICATION, ApplicationIdentifiedResolver.class), //

    PROJECT_APPROVED_OUTCOME(true, PROJECT, ProjectApprovedResolver.class), //
    PROJECT_UPDATED_OUTCOME(true, PROJECT, ProjectUpdatedResolver.class), //
    PROJECT_CREATED_OUTCOME(false, PROJECT, ProjectCreatedResolver.class), //

    PROGRAM_APPROVED_OUTCOME(true, PROGRAM, ProgramApprovedResolver.class), //
    PROGRAM_UPDATED_OUTCOME(true, PROGRAM, ProgramUpdatedResolver.class), //
    PROGRAM_CREATED_OUTCOME(false, PROGRAM, ProgramCreatedResolver.class),

    DEPARTMENT_APPROVED_OUTCOME(true, DEPARTMENT, DepartmentApprovedResolver.class), //
    DEPARTMENT_UPDATED_OUTCOME(true, DEPARTMENT, DepartmentUpdatedResolver.class), //
    DEPARTMENT_CREATED_OUTCOME(false, DEPARTMENT, DepartmentCreatedResolver.class), //

    INSTITUTION_APPROVED_OUTCOME(true, INSTITUTION, InstitutionApprovedResolver.class), //
    INSTITUTION_UPDATED_OUTCOME(true, INSTITUTION, InstitutionUpdatedResolver.class), //
    INSTITUTION_CREATED_OUTCOME(false, INSTITUTION, InstitutionCreatedResolver.class);

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
