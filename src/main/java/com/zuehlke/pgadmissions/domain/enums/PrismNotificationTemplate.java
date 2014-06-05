package com.zuehlke.pgadmissions.domain.enums;

public enum PrismNotificationTemplate {

    APPLICATION_PROVIDE_REFERENCE_REQUEST("Reference Request"), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER("Reference Request Reminder"), // sent by timer //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER("Interview Confirmation - Staff"), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST("Interview Scheduling Request"), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_REMINDER("Interview Scheduling Reminder"), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION("Interview Availability Confirmation"), //
    SYSTEM_PASSWORD_NOTIFICATION("New Password Confirmation"), // send on event //
    SYSTEM_COMPLETE_REGISTRATION_REQUEST("Confirmation of Registration"), //
    SYSTEM_IMPORT_ERROR_NOTIFICATION("Import Error"), // sent on event //
    APPLICATION_CONFIRM_REJECTION_NOTIFICATION("Rejected Notification"), // sent on event //
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION("Approved Notification"), //
    APPLICATION_COMPLETE_NOTIFICATION("Receipt of Application"), // sent on event //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWEE("Interview Confirmation - Applicant"), //
    PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION("Opportunity Request Outcome"), //
    SYSTEM_REGISTRATION_REQUEST("New User Invitation"), //
    APPLICATION_UPDATE_NOTIFICATION("Update Notification"), //
    APPLICATION_TASK_REQUEST("Task Notification"), //
    APPLICATION_TASK_REQUEST_REMINDER("Task Reminder"), //
    PROGRAM_TASK_REQUEST("Opportunity Request Notification"), //
    APPLICATION_COMPLETE_REQUEST(""), //
    APPLICATION_COMPLETE_REQUEST_REMINDER(""), //
    APPLICATION_CORRECT_REQUEST(""), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION(""), //
    PROGRAM_TASK_REQUEST_REMINDER(""), //
    PROGRAM_UPDATE_NOTIFICATION(""), //
    PROJECT_TASK_REQUEST(""), //
    PROJECT_TASK_REQUEST_REMINDER(""), //
    ;

    private final String displayValue;

    private PrismNotificationTemplate(String displayValue) {
        this.displayValue = displayValue;
    }

    public String displayValue() {
        return displayValue;
    }
}
