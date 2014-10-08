package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_PERSONAL_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_PERSONAL_DETAIL_PASSPORT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_PROGRAM_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_REJECTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.SYSTEM_GLOBAL;

public enum PrismDisplayProperty {

    SYSTEM_DATE_FORMAT(SYSTEM_GLOBAL, "dd MMM yyyy"), //
    SYSTEM_YES(SYSTEM_GLOBAL, "Yes"), //
    SYSTEM_NO(SYSTEM_GLOBAL, "No"), //
    SYSTEM_VALUE_PROVIDED(SYSTEM_GLOBAL, "Provided"), //
    SYSTEM_VALUE_NOT_PROVIDED(SYSTEM_GLOBAL, "Not Provided"), //
    SYSTEM_TITLE(SYSTEM_GLOBAL, "Title"), //
    SYSTEM_FIRST_NAME(SYSTEM_GLOBAL, "First Name"), //
    SYSTEM_FIRST_NAME_2(SYSTEM_GLOBAL, "First Name 2"), //
    SYSTEM_FIRST_NAME_3(SYSTEM_GLOBAL, "First Name 3"), //
    SYSTEM_LAST_NAME(SYSTEM_GLOBAL, "Last Name"), //
    SYSTEM_EMAIL(SYSTEM_GLOBAL, "Email"), //
    SYSTEM_TELEPHONE(SYSTEM_GLOBAL, "Telephone Number"), //
    SYSTEM_SKYPE(SYSTEM_GLOBAL, "Skype"), //
    SYSTEM_RATING(SYSTEM_GLOBAL, "Rating"), //
    SYSTEM_AVERAGE_RATING(SYSTEM_GLOBAL, "Average Rating"), //
    APPLICATION_HEADER(APPLICATION_GLOBAL, "Application"), //
    APPLICATION_CREATOR(APPLICATION_GLOBAL, "Applicant"), //
    APPLICATION_PROGRAM_DETAIL_HEADER(APPLICATION_PROGRAM_DETAIL, "Program Detail"), //
    APPLICATION_PROGRAM_DETAIL_START_DATE(APPLICATION_PROGRAM_DETAIL, "Start Date"), //
    APPLICATION_PROGRAM_DETAIL_CONFIRMED_START_DATE(APPLICATION_PROGRAM_DETAIL, "Confirmed Start Date"), //
    APPLICATION_PROGRAM_DETAIL_REFERRAL_SOURCE(APPLICATION_PROGRAM_DETAIL, "How did you find us?"), //
    APPLICATION_SUPERVISOR_HEADER(APPLICATION_SUPERVISOR, "Supervisors"), //
    APPLICATION_SUPERVISOR_SUBHEADER(APPLICATION_SUPERVISOR, "Supervisor"), //
    APPLICATION_SUPERVISOR_AWARE_OF_APPLICATION(APPLICATION_SUPERVISOR, "Is this supervisor aware of your application?"), //
    APPLICATION_PERSONAL_DETAIL_HEADER(APPLICATION_PERSONAL_DETAIL, "Applicant Detail"), //
    APPLICATION_PERSONAL_DETAIL_GENDER(APPLICATION_PERSONAL_DETAIL, "Gender"), //
    APPLICATION_PERSONAL_DETAIL_DATE_OF_BIRTH(APPLICATION_PERSONAL_DETAIL, "Date of Birth"), //
    APPLICATION_PERSONAL_DETAIL_COUNTRY_OF_BIRTH(APPLICATION_PERSONAL_DETAIL, "Country of Birth"), //
    APPLICATION_PERSONAL_DETAIL_COUNTRY_OF_DOMICILE(APPLICATION_PERSONAL_DETAIL, "Country of Domicile"), //
    APPLICATION_PERSONAL_DETAIL_NATIONALITY(APPLICATION_PERSONAL_DETAIL, "Nationality"), //
    APPLICATION_PERSONAL_DETAIL_ETHNICITY(APPLICATION_PERSONAL_DETAIL, "Ethnicity"), //
    APPLICATION_PERSONAL_DETAIL_DISABILITY(APPLICATION_PERSONAL_DETAIL, "Disability"), //
    APPLICATION_PERSONAL_DETAIL_REQUIRE_VISA(APPLICATION_PERSONAL_DETAIL, "Do you Require a Visa to Study in the UK?"), //
    APPLICATION_PERSONAL_DETAIL_PASSPORT_AVAILABLE(APPLICATION_PERSONAL_DETAIL, "Do you have a passport?"), //
    APPLICATION_PERSONAL_DETAIL_WORK_LANGUAGE_FIRST_LANGUAGE(APPLICATION_PERSONAL_DETAIL, "Is the specified language of work your first language?"), //
    APPLICATION_PERSONAL_DETAIL_LANGUAGE_QUALIFICATION_AVAILABLE(APPLICATION_PERSONAL_DETAIL, "Do you have a language qualification?"), //
    APPLICATION_PERSONAL_DETAIL_PASSPORT_HEADER(APPLICATION_PERSONAL_DETAIL_PASSPORT, "Passport"), //
    APPLICATION_PERSONAL_DETAIL_PASSPORT_NUMBER(APPLICATION_PERSONAL_DETAIL_PASSPORT, "Passport Number"), //
    APPLICATION_PERSONAL_DETAIL_PASSPORT_NAME(APPLICATION_PERSONAL_DETAIL_PASSPORT, "Name on Passport"), //
    APPLICATION_PERSONAL_DETAIL_PASSPORT_ISSUE_DATE(APPLICATION_PERSONAL_DETAIL_PASSPORT, "Issue Date"), //
    APPLICATION_PERSONAL_DETAIL_PASSPORT_EXPIRY_DATE(APPLICATION_PERSONAL_DETAIL_PASSPORT, "Expiry Date"), //
    APPLICATION_REJECTION_SYSTEM(APPLICATION_REJECTION, "The opportunity that you applied for has been discontinued.");

    private PrismDisplayCategory category;

    private String defaultValue;

    private PrismDisplayProperty(PrismDisplayCategory category, String defaultValue) {
        this.category = category;
        this.defaultValue = defaultValue;
    }

    public final PrismDisplayCategory getCategory() {
        return category;
    }

    public final String getDefaultValue() {
        return defaultValue;
    }

}
