package com.zuehlke.pgadmissions.domain.enums;

public enum PrismStateTransitionEvaluation {
    
    APPLICATION_COMPLETED_OUTCOME("getApplicationCompletedOutcome", PrismScope.APPLICATION), //
    APPLICATION_CONFIRM_SUPERVISION_OUTCOME("getApplicationConfirmSupervisionOutcome", PrismScope.APPLICATION), //
    APPLICATION_ELIGIBILITY_ASSESSED_OUTCOME("getApplicationEligibilityAssessedOutcome", PrismScope.APPLICATION), //
    APPLICATION_EXPORTED_OUTCOME("getApplicationExportedOutcome", PrismScope.APPLICATION), //
    APPLICATION_INTERVIEW_AVAILABILITY_OUTCOME("getApplicationInterviewAvailabilityOutcome", PrismScope.APPLICATION), //
    APPLICATION_INTERVIEW_FEEDBACK_OUTCOME("getApplicationInterviewFeedbackOutcome", PrismScope.APPLICATION), //
    APPLICATION_INTERVIEW_SCHEDULED_OUTCOME("getApplicationInterviewScheduledOutcome", PrismScope.APPLICATION), //
    APPLICATION_PROCESSING_COMPLETED_OUTCOME("getApplicationProcessingCompletedOutcome", PrismScope.APPLICATION), //
    APPLICATION_RECRUITMENT_OUTCOME("getApplicationRecruitmentOutcome", PrismScope.APPLICATION), //
    APPLICATION_REVIEW_OUTCOME("getApplicationReviewOutcome", PrismScope.APPLICATION), //
    APPLICATION_STATE_COMPLETED_OUTCOME("getApplicationStateCompletedOutcome", PrismScope.APPLICATION), //
    PROGRAM_APPROVED_OUTCOME("getProgramApprovedOutcome", PrismScope.PROGRAM), //
    PROGRAM_CONFIGURED_OUTCOME("getProgramConfiguredOutcome", PrismScope.PROGRAM), //
    PROGRAM_CREATED_OUTCOME("getProgramCreationOutcome", PrismScope.PROGRAM), //
    PROGRAM_EXPIRED_OUTCOME("getProgramExpiredOutcome", PrismScope.PROGRAM), //
    PROGRAM_REACTIVATED_OUTCOME("getProgramReactivatedOutcome", PrismScope.PROGRAM), //
    PROJECT_CONFIGURED_OUTCOME("getProjectConfiguredOutcome", PrismScope.PROJECT), //
    PROJECT_REACTIVATED_OUTCOME("getProjectReactivatedOutcome", PrismScope.PROJECT);
    
    public static String INCORRECT_PROCESSOR_TYPE = "Tried to invoke state transition processor on incorrect resource type";
    
    private String methodName;
    
    private PrismScope scope;
    
    private PrismStateTransitionEvaluation(String methodName, PrismScope scope) {
        this.methodName = methodName;
        this.scope = scope;
    }

    public String getMethodName() {
        return methodName;
    }

    public PrismScope getScope() {
        return scope;
    }

}
