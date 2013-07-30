package com.zuehlke.pgadmissions.domain.enums;

public enum ReminderType {
    REFERENCE("Reference"), INTERVIEW_SCHEDULE("Interview Scheduling"), TASK("Task");

    private final String displayValue;

    private ReminderType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String displayValue() {
        return displayValue;
    }

}
