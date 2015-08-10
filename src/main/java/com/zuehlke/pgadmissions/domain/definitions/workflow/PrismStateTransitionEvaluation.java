package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application.*;
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

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;

public enum PrismStateTransitionEvaluation {

    APPLICATION_COMPLETED_OUTCOME(false, APPLICATION, ApplicationCompletedResolver.class), //
    APPLICATION_CONFIRMED_SUPERVISION_OUTCOME(false, APPLICATION, ApplicationConfirmedSupervisionResolver.class), //
    APPLICATION_COMPLETED_STATE_OUTCOME(true, APPLICATION, ApplicationCompletedStateResolver.class), //
    APPLICATION_EXPORTED_OUTCOME(false, APPLICATION, ApplicationExportedResolver.class), //
    APPLICATION_PROVIDED_INTERVIEW_AVAILABILITY_OUTCOME(false, APPLICATION, ApplicationProvidedInterviewAvailabilityResolver.class), //
    APPLICATION_PROVIDED_INTERVIEW_FEEDBACK_OUTCOME(false, APPLICATION, ApplicationProvidedInterviewFeedbackResolver.class), //
    APPLICATION_ASSIGNED_REVIEWER_OUTCOME(false, APPLICATION, ApplicationAssignedReviewerResolver.class), //
    APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME(false, APPLICATION, ApplicationAssignedInterviewerResolver.class), //
    APPLICATION_ASSIGNED_SUPERVISOR_OUTCOME(false, APPLICATION, ApplicationAssignedSupervisorResolver.class), //
    APPLICATION_CONFIRMED_INTERVIEW_OUTCOME(false, APPLICATION, ApplicationConfirmedInterviewResolver.class), //
    APPLICATION_ESCALATED_OUTCOME(false, APPLICATION, ApplicationEscalatedResolver.class), //
    APPLICATION_APPROVED_OUTCOME(false, APPLICATION, ApplicationApprovedResolver.class), //
    APPLICATION_REJECTED_OUTCOME(false, APPLICATION, ApplicationRejectedResolver.class), //
    APPLICATION_TERMINATED_OUTCOME(false, APPLICATION, ApplicationEscalatedResolver.class), //
    APPLICATION_WITHDRAWN_OUTCOME(false, APPLICATION, ApplicationWithdrawnResolver.class), //
    APPLICATION_CONFIRMED_ELIGIBILITY_OUTCOME(false, APPLICATION, ApplicationConfirmedEligibilityResolver.class), //
    APPLICATION_PROVIDED_REFERENCE_OUTCOME(false, APPLICATION, ApplicationProvidedReferenceResolver.class), //
    APPLICATION_COMPLETED_VERIFICATION_STATE_OUTCOME(true, APPLICATION, ApplicationCompletedVerificationStateResolver.class), //
    APPLICATION_COMPLETED_REFERENCE_STATE_OUTCOME(true, APPLICATION, ApplicationCompletedReferenceStateResolver.class), //
    APPLICATION_PROVIDED_REVIEW_OUTCOME(false, APPLICATION, ApplicationProvidedReviewResolver.class), //
    APPLICATION_UPDATED_INTERVIEW_AVAILABILITY_OUTCOME(false, APPLICATION, ApplicationUpdateInterviewAvailabilityResolver.class), //
    APPLICATION_PURGED_OUTCOME(false, APPLICATION, ApplicationPurgedResolver.class), //
    INSTITUTION_APPROVED_OUTCOME(true, INSTITUTION, InstitutionApprovedResolver.class), //
    INSTITUTION_UPDATED_OUTCOME(true, INSTITUTION, InstitutionUpdatedResolver.class), //
    INSTITUTION_CREATED_OUTCOME(false, INSTITUTION, InstitutionCreatedResolver.class), //
    DEPARTMENT_APPROVED_OUTCOME(true, DEPARTMENT, DepartmentApprovedResolver.class), //
    DEPARTMENT_UPDATED_OUTCOME(true, DEPARTMENT, DepartmentUpdatedResolver.class), //
    DEPARTMENT_CREATED_OUTCOME(false, DEPARTMENT, DepartmentCreatedResolver.class), //
    PROGRAM_APPROVED_OUTCOME(true, PROGRAM, ProgramApprovedResolver.class), //
    PROGRAM_UPDATED_OUTCOME(true, PROGRAM, ProgramUpdatedResolver.class), //
    PROGRAM_CREATED_OUTCOME(false, PROGRAM, ProgramCreatedResolver.class), //
    PROJECT_APPROVED_OUTCOME(true, PROJECT, ProjectApprovedResolver.class), //
    PROJECT_UPDATED_OUTCOME(true, PROJECT, ProjectUpdatedResolver.class), //
    PROJECT_CREATED_OUTCOME(false, PROJECT, ProjectCreatedResolver.class);

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
