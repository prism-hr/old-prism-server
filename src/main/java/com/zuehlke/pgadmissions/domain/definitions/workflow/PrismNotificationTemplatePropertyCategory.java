package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.List;

public enum PrismNotificationTemplatePropertyCategory {

    SYSTEM_GLOBAL, //
    SYSTEM_PASSWORD, //
    SYSTEM_ACCOUNT_ACTIVATION, //
    SYSTEM_COMMENT, //
    SYSTEM_ACTION, //
    APPLICATION_GLOBAL, //
    APPLICATION_SUBMITTED, //
    APPLICATION_REJECTION, //
    APPLICATION_RECOMMENDATION, //
    PROGRAM_GLOBAL, //
    PROJECT_GLOBAL, //
    INSTITUTION_GLOBAL, //
    INSTITUTION_DATA_IMPORT_ERROR, //
    INTERVIEW_SCHEDULED;

    public List<PrismNotificationTemplateProperty> getProperties() {
        return PrismNotificationTemplateProperty.getProperties(this);
    }

}
