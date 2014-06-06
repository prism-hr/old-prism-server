package com.zuehlke.pgadmissions.domain.enums;

public enum PrismNotificationTemplate {
    
    APPLICATION_COMPLETE_NOTIFICATION("Application Submission Notification"), //
    APPLICATION_COMPLETE_REQUEST("Request to Complete Application"), //
    APPLICATION_COMPLETE_REQUEST_REMINDER("Reminder to Complete Application"), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWEE("Interview Arrangements Confirmation (Interviewee)"), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER("Interview Arrangements Confirmation (Interviewer)"), //
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION("Offer notification"), //
    APPLICATION_CONFIRM_REJECTION_NOTIFICATION("Rejection Notification"), //
    APPLICATION_CORRECT_REQUEST("Request to Correct Application"), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION("Interview Preferences Provided Notification"), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST("Request to Provide Interview Preferences"), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_REMINDER("Reminder to Provide Interview Preferences"), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST("Request to Provide Reference"), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER("Reminder to Provide Reference"), //
    APPLICATION_TASK_REQUEST("Request to Process Applications"), //
    APPLICATION_TASK_REQUEST_REMINDER("Reminder to Process Applications"), //
    APPLICATION_TERMINATE_NOTIFICATION("Application Termination Notification"), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION("Interview Preferences Updated Notification"), //
    APPLICATION_UPDATE_NOTIFICATION("Applications Updated Notification"), //
    PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION("Program Approval Completed Notification"), //
    PROGRAM_TASK_REQUEST("Request to Process Programs"), //
    PROGRAM_TASK_REQUEST_REMINDER("Reminder to Process Programs"), //
    PROGRAM_UPDATE_NOTIFICATION("Programs Updated Notification"), //
    PROJECT_TASK_REQUEST("Request to Process Projects"), //
    PROJECT_TASK_REQUEST_REMINDER("Reminder to Process Projects"), //
    PROJECT_UPDATE_NOTIFICATION("Projects Updated Notification"), //
    SYSTEM_COMPLETE_REGISTRATION_REQUEST("Request to Complete Registration"), //
    SYSTEM_IMPORT_ERROR_NOTIFICATION("Data Import Error Notification"), //
    SYSTEM_PASSWORD_NOTIFICATION("New Password Notification");

    private final String displayValue;

    private PrismNotificationTemplate(String displayValue) {
        this.displayValue = displayValue;
    }

    public String displayValue() {
        return displayValue;
    }
}
