package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;

public enum PrismDisplayPropertyCategory {

    SYSTEM_GLOBAL(SYSTEM),
    SYSTEM_COMMENT(SYSTEM),
    SYSTEM_NOTIFICATION(SYSTEM),
    SYSTEM_STATE_GROUP(SYSTEM),
    SYSTEM_STATE_TRANSITION(SYSTEM),
    SYSTEM_ACTION(SYSTEM),
    SYSTEM_ROLE(SYSTEM),
    SYSTEM_PROGRAM_TYPE(SYSTEM),
    SYSTEM_YES_NO_UNSURE(SYSTEM),
    SYSTEM_STUDY_OPTION(SYSTEM),
    SYSTEM_PROGRAM_CATEGORY(SYSTEM),
    SYSTEM_DURATION(SYSTEM),
    SYSTEM_FILTER_PROPERTY(SYSTEM),
    SYSTEM_FILTER_EXPRESSION(SYSTEM),
    SYSTEM_ADVERT_INDUSTRY(SYSTEM),
    SYSTEM_ADVERT_FUNCTION(SYSTEM),
    SYSTEM_ADVERT_DOMAIN(SYSTEM),
    SYSTEM_NOTIFICATION_TEMPLATE(SYSTEM),
    SYSTEM_LOCALE(SYSTEM),
    SYSTEM_REFEREE_TYPE(SYSTEM),
    SYSTEM_RESERVE_STATUS(SYSTEM),
    SYSTEM_VALIDATION_ERROR(SYSTEM),
    SYSTEM_INTEGRATION(SYSTEM),
    SYSTEM_STATE_DURATION(SYSTEM),
    SYSTEM_WORKFLOW(SYSTEM),
    SYSTEM_DECLINE_ACTION(SYSTEM),
    SYSTEM_HTML_GENERAL_SECTION(SYSTEM),
    SYSTEM_AUTHENTICATE_SECTION(SYSTEM),
    SYSTEM_OPPORTUNITIES_SECTION(SYSTEM),
    SYSTEM_ADVERTISE_SECTION(SYSTEM),
    SYSTEM_RESOURCES_SECTION(SYSTEM),
    SYSTEM_RESOURCE_SECTION(SYSTEM),
    SYSTEM_ACCOUNT_SECTION(SYSTEM),
    SYSTEM_HTML_FIELDS(SYSTEM),
    SYSTEM_HTML_COMMON(SYSTEM),
    SYSTEM_HTML_ADDRESS(SYSTEM),
    SYSTEM_HTML_RESOURCE_CONFIGURATION(SYSTEM),
    SYSTEM_HTML_MANAGE_USERS(SYSTEM),
    SYSTEM_HTML_EMAIL_TEMPLATE_CONFIGURATION(SYSTEM),
    SYSTEM_HTML_CUSTOM_FORMS_CONFIGURATION(SYSTEM),
    SYSTEM_HTML_WORKFLOW_CONFIGURATION(SYSTEM),
    SYSTEM_HTML_TRANSLATIONS_CONFIGURATION(SYSTEM),
    SYSTEM_ERROR_MESSAGES(SYSTEM),
    INSTITUTION_COMMENT(INSTITUTION),
    PROGRAM_COMMENT(PROGRAM),
    PROGRAM_FORM(PROGRAM),
    PROGRAM_ADVERT_DETAILS(PROGRAM),
    PROGRAM_ADVERT_FEES_AND_PAYMENTS(PROGRAM),
    PROGRAM_ADVERT_CATEGORIES(PROGRAM),
    PROGRAM_ADVERT_CLOSING_DATES(PROGRAM),
    PROJECT_COMMENT(PROJECT),
    APPLICATION_GLOBAL(APPLICATION),
    APPLICATION_PROGRAM_DETAIL(APPLICATION),
    APPLICATION_STUDY_DETAIL(APPLICATION),
    APPLICATION_SUPERVISOR(APPLICATION),
    APPLICATION_PERSONAL_DETAIL(APPLICATION),
    APPLICATION_ADDRESS(APPLICATION),
    APPLICATION_QUALIFICATION(APPLICATION),
    APPLICATION_EMPLOYMENT_POSITION(APPLICATION),
    APPLICATION_FUNDING(APPLICATION),
    APPLICATION_PRIZE(APPLICATION),
    APPLICATION_REFEREE(APPLICATION),
    APPLICATION_DOCUMENT(APPLICATION),
    APPLICATION_ADDITIONAL_INFORMATION(APPLICATION),
    APPLICATION_FORM(APPLICATION),
    APPLICATION_ACTION(APPLICATION),
    APPLICATION_COMMENT(APPLICATION),
    APPLICATION_REPORT(APPLICATION);

    private PrismScope scope;

    PrismDisplayPropertyCategory(PrismScope scope) {
        this.scope = scope;
    }

    public PrismScope getScope() {
        return scope;
    }
}
