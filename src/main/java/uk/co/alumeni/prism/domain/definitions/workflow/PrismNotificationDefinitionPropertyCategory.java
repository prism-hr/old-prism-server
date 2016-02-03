package uk.co.alumeni.prism.domain.definitions.workflow;

import java.util.List;

public enum PrismNotificationDefinitionPropertyCategory {

    TEMPLATE_GLOBAL, //
    ACTION_GLOBAL, //
    COMMENT_GLOBAL, //
    COMMENT_TRANSITION, //
    TARGET_GLOBAL, //
    APPLICATION_GLOBAL, //
    APPLICATION_INTERVIEW_SCHEDULED, //
    APPLICATION_APPROVED, //
    APPLICATION_REJECTED, //
    PROJECT_GLOBAL, //
    PROGRAM_GLOBAL, //
    DEPARTMENT_GLOBAL, //
    INSTITUTION_GLOBAL, //
    INSTITUTION_APPROVED, //
    SYSTEM_APPLICATION, //
    SYSTEM_PROJECT, //
    SYSTEM_PROGRAM, //
    SYSTEM_DEPARTMENT, //
    SYSTEM_INSTITUTION, //
    SYSTEM_USER_PASSWORD, //
    SYSTEM_USER_ACTIVATION, //
    SYSTEM_ACTIVITY, //
    SYSTEM_REMINDER;

    public List<PrismNotificationDefinitionProperty> getProperties() {
        return PrismNotificationDefinitionProperty.getProperties(this);
    }

}
