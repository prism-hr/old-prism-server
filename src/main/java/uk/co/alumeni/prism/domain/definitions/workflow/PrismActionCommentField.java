package uk.co.alumeni.prism.domain.definitions.workflow;

import com.google.common.base.CaseFormat;

public enum PrismActionCommentField {

    CONTENT,
    TRANSITION_STATE,
    ASSIGNED_USERS,
    DOCUMENTS,
    APPLICATION_ELIGIBLE,
    APPLICATION_INTERESTED,
    APPLICATION_RATING,
    INTERVIEW_DATE_TIME("interviewAppointment.interviewDateTime"),
    INTERVIEW_TIME_ZONE("interviewAppointment.interviewTimeZone"),
    INTERVIEW_DURATION("interviewAppointment.interviewDuration"),
    INTERVIEWEE_INSTRUCTIONS("interviewInstruction.intervieweeInstructions"),
    INTERVIEWER_INSTRUCTIONS("interviewInstruction.interviewerInstructions"),
    INTERVIEW_LOCATION("interviewInstruction.interviewLocation"),
    APPOINTMENT_TIMESLOTS,
    APPOINTMENT_PREFERENCES,
    RECRUITER_ACCEPT_APPOINTMENT,
    REJECTION_REASON,
    APPLICATION_RESERVE_STATUS;

    private String propertyPath;

    private PrismActionCommentField(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    private PrismActionCommentField() {
        this.propertyPath = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
    }

    public String getPropertyPath() {
        return propertyPath;
    }
}
