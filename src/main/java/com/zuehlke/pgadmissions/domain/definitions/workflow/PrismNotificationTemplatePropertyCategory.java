package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.List;

public enum PrismNotificationTemplatePropertyCategory {

    GLOBAL, //
    APPLICATION, //
    APPLICATION_REJECTION, //
    PROGRAM, //
    PROJECT, //
    INSTITUTION, //
    COMMENT, //
    INTERVIEW, //
    INTERVIEW_SCHEDULED, //
    ERROR, //
    RECOMMENDATION, //
    ACTION, //
    CONFIRM_INTERVIEW, //
    NEW_PASSWORD,
    ACCOUNT_ACTIVATION;

    public List<PrismNotificationTemplateProperty> getProperties() {
        return PrismNotificationTemplateProperty.getProperties(this);
    }

}
