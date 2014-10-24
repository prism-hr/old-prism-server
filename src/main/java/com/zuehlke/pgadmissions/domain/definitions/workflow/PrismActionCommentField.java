package com.zuehlke.pgadmissions.domain.definitions.workflow;

public enum PrismActionCommentField {

    CONTENT("content"),
    TRANSITION_STATE("transitionState"),
    ASSIGNED_USERS("assignedUsers"),
    DOCUMENTS("documents"),
    QUALIFIED("qualified"),
    COMPETENT_IN_WORK_LANGUAGE("competentInWorkLanguage"),
    RESIDENCE_STATE("residenceState"),
    SUITABLE_FOR_INSTITUTION("suitableForInstitution"),
    SUITABLE_FOR_OPPORTUNITY("suitableForOpportunity"),
    DESIRE_TO_INTERVIEW("desireToInterview"),
    DESIRE_TO_RECRUIT("desireToRecruit"),
    RATING("rating"),
    INTERVIEW_DATE_TIME("interviewDateTime"),
    INTERVIEW_TIME_ZONE("interviewTimeZone"),
    INTERVIEW_DURATION("interviewDuration"),
    INTERVIEWEE_INSTRUCTIONS("intervieweeInstructions"),
    INTERVIEWER_INSTRUCTIONS("interviewerInstructions"),
    INTERVIEW_LOCATION("interviewLocation"),
    APPOINTMENT_TIMESLOTS("appointmentTimeslots"),
    APPOINTMENT_PREFERENCES("appointmentPreferences"),
    RECRUITER_ACCEPT_APPOINTMENT("recruiterAcceptAppointment"),
    POSITION_TITLE("positionTitle"),
    POSITION_DESCRIPTION("positionDescription"),
    POSITION_PROVISIONAL_START_DATE("positionProvisionalStartDate"),
    APPOINTMENT_CONDITIONS("appointmentConditions"),
    DECLINED_RESPONSE("declinedResponse"),
    REJECTION_REASON("rejectionReason");

    private String fieldName;

    PrismActionCommentField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
