package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.List;

public enum PrismNotificationTemplatePropertyCategory {

    TEMPLATE_GLOBAL, //
    ACTION_GLOBAL, //
    COMMENT_GLOBAL, //
    APPLICATION_GLOBAL, //
    APPLICATION_INTERVIEW_SCHEDULED, //
    APPLICATION_APPROVED, //
    APPLICATION_REJECTED, //
    PROJECT_GLOBAL, //
    PROGRAM_GLOBAL, //
    INSTITUTION_GLOBAL, //
    INSTITUTION_APPROVED, //
    SYSTEM_APPLICATION_SYNDICATED, //
    SYSTEM_APPLICATION_MARKETING, //
    SYSTEM_PROJECT_SYNDICATED, //
    SYSTEM_PROGRAM_SYNDICATED, //
    SYSTEM_INSTITUTION_SYNDICATED, //
    SYSTEM_USER_PASSWORD, //
    SYSTEM_USER_ACCOUNT;

    public List<PrismNotificationTemplateProperty> getProperties() {
        return PrismNotificationTemplateProperty.getProperties(this);
    }

}
