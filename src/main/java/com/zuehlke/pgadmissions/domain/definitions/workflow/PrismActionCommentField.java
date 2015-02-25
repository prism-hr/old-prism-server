package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.base.CaseFormat;

public enum PrismActionCommentField {

	CONTENT, //
	TRANSITION_STATE, //
	ASSIGNED_USERS, //
	DOCUMENTS, //
	APPLICATION_ELIGIBLE, //
	APPLICATION_INTERESTED, //
	APPLICATION_RATING, //
	INTERVIEW_DATE_TIME("interviewAppointment.interviewDateTime"), //
	INTERVIEW_TIME_ZONE("interviewAppointment.interviewTimeZone"), //
	INTERVIEW_DURATION("interviewAppointment.interviewDuration"), //
	INTERVIEWEE_INSTRUCTIONS("interviewInstruction.intervieweeInstructions"), //
	INTERVIEWER_INSTRUCTIONS("interviewInstruction.interviewerInstructions"), //
	INTERVIEW_LOCATION("interviewInstruction.interviewLocation"), //
	APPOINTMENT_TIMESLOTS, //
	APPOINTMENT_PREFERENCES, //
	RECRUITER_ACCEPT_APPOINTMENT, //
	SHORTLISTER_ATTEND_PANEL, //
	REJECTION_REASON, //
	RESOURCE_BATCH;

	private String propertyPath;

	PrismActionCommentField(String propertyPath) {
		this.propertyPath = propertyPath;
	}

	PrismActionCommentField() {
		this.propertyPath = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
	}

	public String getPropertyPath() {
		return propertyPath;
	}
}
