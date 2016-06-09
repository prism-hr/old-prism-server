package uk.co.alumeni.prism.domain.definitions;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;

public enum PrismDisplayPropertyCategory {

    SYSTEM_GLOBAL(SYSTEM),
    SYSTEM_COMMENT(SYSTEM),
    SYSTEM_MESSAGE(SYSTEM),
    SYSTEM_NOTIFICATION(SYSTEM),
    SYSTEM_STATE_GROUP(SYSTEM),
    SYSTEM_STATE_TRANSITION(SYSTEM),
    SYSTEM_ACTION(SYSTEM),
    SYSTEM_ROLE(SYSTEM),
    SYSTEM_YES_NO_UNSURE(SYSTEM),
    SYSTEM_SCOPE(SYSTEM),
    SYSTEM_OPPORTUNITY_CATEGORY(SYSTEM),
    SYSTEM_OPPORTUNITY_TYPE(SYSTEM),
    SYSTEM_STUDY_OPTION(SYSTEM),
    SYSTEM_AGE_RANGE(SYSTEM),
    SYSTEM_ETHNICITY(SYSTEM),
    SYSTEM_DISABILITY(SYSTEM),
    SYSTEM_GENDER(SYSTEM),
    SYSTEM_DOMICILE(SYSTEM),
    SYSTEM_REJECTION_REASON(SYSTEM),
    SYSTEM_DURATION(SYSTEM),
    SYSTEM_ENTITY(SYSTEM),
    SYSTEM_FILTER_PROPERTY(SYSTEM),
    SYSTEM_FILTER_EXPRESSION(SYSTEM),
    SYSTEM_ADVERT_BENEFIT(SYSTEM),
    SYSTEM_ADVERT_INDUSTRY(SYSTEM),
    SYSTEM_ADVERT_FUNCTION(SYSTEM),
    SYSTEM_DISPLAY_CATEGORY(SYSTEM),
    SYSTEM_REPORT_INDICATOR_GROUP(SYSTEM),
    SYSTEM_VALIDATION_ERROR(SYSTEM),
    SYSTEM_INTEGRATION(SYSTEM),
    SYSTEM_STATE_DURATION(SYSTEM),
    SYSTEM_MONTH(SYSTEM),
    SYSTEM_DECLINE_ACTION(SYSTEM),
    SYSTEM_GENERAL(SYSTEM),
    SYSTEM_CONTACT_SECTION(SYSTEM),
    SYSTEM_ACTIVITY_SECTION(SYSTEM),
    SYSTEM_CONNECT_SECTION(SYSTEM),
    SYSTEM_AUTHENTICATE(SYSTEM),
    SYSTEM_OPPORTUNITIES(SYSTEM),
    SYSTEM_OPPORTUNITIES_ENQUIRY(SYSTEM),
    SYSTEM_ADVERTISE(SYSTEM),
    SYSTEM_INVITE(SYSTEM),
    SYSTEM_RESOURCES(SYSTEM),
    SYSTEM_RESOURCE(SYSTEM),
    SYSTEM_ACCOUNT(SYSTEM),
    SYSTEM_ADDRESS(SYSTEM),
    SYSTEM_RESOURCE_PARENT(SYSTEM),
    SYSTEM_RESOURCE_OPPORTUNITY(SYSTEM),
    SYSTEM_CREATE_RESOURCE_FAMILY(SYSTEM),
    SYSTEM_RESOURCE_CONFIGURATION(SYSTEM),
    SYSTEM_RESOURCE_SHARE(SYSTEM),
    SYSTEM_MANAGE_USERS(SYSTEM),
    SYSTEM_TRANSLATIONS_CONFIGURATION(SYSTEM),
    SYSTEM_PERFORMANCE_INDICATOR(SYSTEM),
    SYSTEM_RESOURCE_TARGETS(SYSTEM),
    SYSTEM_RESOURCE_COMPETENCES(SYSTEM),
    SYSTEM_RESOURCE_ADVERT(SYSTEM),
    SYSTEM_RESOURCE_FINANCIAL_DETAILS(SYSTEM),
    SYSTEM_CANDIDATE(SYSTEM),
    INSTITUTION_COMMENT(SYSTEM),
    DEPARTMENT_COMMENT(SYSTEM),
    PROGRAM_COMMENT(SYSTEM),
    PROJECT_COMMENT(SYSTEM),
    APPLICATION_GLOBAL(SYSTEM),
    APPLICATION_PROGRAM_DETAIL(SYSTEM),
    PROFILE_PERSONAL_DETAIL(SYSTEM),
    PROFILE_ADDRESS(SYSTEM),
    PROFILE_QUALIFICATION(SYSTEM),
    PROFILE_EMPLOYMENT_POSITION(SYSTEM),
    PROFILE_REFEREE(SYSTEM),
    PROFILE_DOCUMENT(SYSTEM),
    PROFILE_ADDITIONAL_INFORMATION(SYSTEM),
    APPLICATION_FORM(SYSTEM),
    PROFILE_FORM(SYSTEM),
    PROFILE_UPDATE(SYSTEM),
    APPLICATION_ACTION(SYSTEM),
    APPLICATION_COMMENT(SYSTEM),
    APPLICATION_REPORT(SYSTEM);

    private PrismScope scope;

    PrismDisplayPropertyCategory(PrismScope scope) {
        this.scope = scope;
    }

    public PrismScope getScope() {
        return scope;
    }
}
