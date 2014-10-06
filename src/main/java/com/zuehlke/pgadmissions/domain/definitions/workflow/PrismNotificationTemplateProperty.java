package com.zuehlke.pgadmissions.domain.definitions.workflow;

public enum PrismNotificationTemplateProperty {

    USER("user"),
    USER_FIRST_NAME("userFirstName"),
    USER_LAST_NAME("userLastName"),
    USER_EMAIL("userEmail"),
    USER_ACTIVATION_CODE("userActivationCode"),
    AUTHOR("author"),
    AUTHOR_EMAIL("authorEmail"),

    APPLICANT("applicant"),
    APPLICATION_CODE("applicationCode"),
    PROJECT_OR_PROGRAM_TITLE("projectOrProgramTitle"),

    PROGRAM_CODE("programCode"),
    PROGRAM_TITLE("programTitle"),

    PROJECT_CODE("projectCode"),
    PROJECT_TITLE("projectTitle"),

    INSTITUTION_CODE("institutionCode"),
    INSTITUTION_TITLE("institutionTitle"),

    ACTION_CONTROL("actionControl"),
    DIRECTIONS_CONTROL("directionsControl"),
    VIEW_EDIT_CONTROL("viewEditControl"),
    NEW_PASSWORD_CONTROL("newPasswordControl"),

    RECOMMENDATIONS("recommendations"),
    ERROR_MESSAGE("errorMessage"),



    SYSTEM_NAME("systemName"),
    TIME("time"),
    HOST("host"),
    ;

    private final String propertyName;

    PrismNotificationTemplateProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
