package com.zuehlke.pgadmissions.dto;

public enum ApplicationFormAction {

    VIEW_EDIT("view", "View / Edit"), //
    VIEW("view", "View"), //
    EMAIL_APPLICANT("emailApplicant", "Email applicant"), //
    COMMENT("comment", "Comment"), //
    WITHDRAW("withdraw", "Withdraw"), //
    CONFIRM_ELIGIBILITY("validate", "Confirm Eligibility"), //
    ADD_REFERENCE("reference", "Add reference"), //
    COMPLETE_VALIDATION_STAGE("validate", "Complete Validation Stage"), //
    ASSIGN_REVIEWERS("validate", "Assign Reviewers"), //
    COMPLETE_REVIEW_STAGE("validate", "Complete Review Stage"), //
    ADD_REVIEW("review", "Add review"), //
    COMPLETE_INTERVIEW_STAGE("validate", "Complete Interview Stage"), //
    ASSIGN_INTERVIEWERS("validate", "Assign Interviewers"), //
    CONFIRM_INTERVIEW_TIME("interviewConfirm", "Confirm Interview Arrangements"), //
    PROVIDE_INTERVIEW_AVAILABILITY("interviewVote", "Provide Interview Availability"), //
    ADD_INTERVIEW_FEEDBACK("interviewFeedback", "Add interview feedback"), //
    REVISE_APPROVAL("restartApproval", "Revise Approval"), //
    APPROVE("validate", "Approve"), //
    ASSIGN_SUPERVISORS("validate", "Assign Supervisors"), //
    REVISE_APPROVAL_AS_ADMINISTRATOR("restartApprovalAsAdministrator", "Revise Approval"), //
    CONFIRM_SUPERVISION("confirmSupervision", "Confirm supervision"), //
    ;

    private final String id;

    private final String displayName;

    private ApplicationFormAction(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
    
}
