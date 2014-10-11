package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_ADDITIONAL_INFORMATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_ADDRESS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_DOCUMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_EMPLOYMENT_POSITION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_FUNDING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_LANGUAGE_QUALIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_PASSPORT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_PERSONAL_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_PROGRAM_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_QUALIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.APPLICATION_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.INSITUTION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.PROGRAM_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.PROJECT_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.SYSTEM_GLOBAL;

public enum PrismDisplayProperty {

    SYSTEM_DATE_FORMAT(SYSTEM_GLOBAL, "dd MMM yyyy"), //
    SYSTEM_DATE_TIME_FORMAT(SYSTEM_GLOBAL, "dd MMM yyyy HH:mm"), //
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
    SYSTEM_ADDRESS(SYSTEM_GLOBAL, "Address"), //
    SYSTEM_RATING(SYSTEM_GLOBAL, "Rating"), //
    SYSTEM_AVERAGE_RATING(SYSTEM_GLOBAL, "Average Rating"), //
    SYSTEM_APPENDIX(SYSTEM_GLOBAL, "Appendix"), //
    SYSTEM_SEE(SYSTEM_GLOBAL, "See"), //
    SYSTEM_PAGE(SYSTEM_GLOBAL, "Page"), //
    SYSTEM_CLOSING_DATE(SYSTEM_GLOBAL, "Closing Date"), //
    SYSTEM_COMMENT(SYSTEM_GLOBAL, "Comment"), //
    SYSTEM_EMAIL_LINK_MESSAGE(SYSTEM_GLOBAL, "If you are unable to follow the links in this message, copy and paste them directly into your browser."), //
    SYSTEM_TELEPHONE_PLACEHOLDER(SYSTEM_GLOBAL, "+44 (0) 0000 000 000"), //
    SYSTEM_IP_PLACEHOLDER(SYSTEM_GLOBAL, "127.0.0.1"), //
    SYSTEM_REFER_TO_DOCUMENT(SYSTEM_GLOBAL, "Refer to attached document."), //
    SYSTEM_OTHER(SYSTEM_GLOBAL, "Other"), //
    SYSTEM_NONE(SYSTEM_GLOBAL, "none"), //
    SYSTEM_PROCEED(SYSTEM_GLOBAL, "Proceed"), //
    SYSTEM_DECLINE(SYSTEM_GLOBAL, "Decline"), //
    SYSTEM_HELPDESK(SYSTEM_GLOBAL, "Get Help"), //
    INSTITUTION_HEADER(INSITUTION_GLOBAL, "Institution"), //
    PROGRAM_HEADER(PROGRAM_GLOBAL, "Program"), //
    PROGRAM_STUDY_OPTION(PROGRAM_GLOBAL, "Study Option"), //
    PROJECT_HEADER(PROJECT_GLOBAL, "Project"), //
    APPLICATION_HEADER(APPLICATION_GLOBAL, "Application"), //
    APPLICATION_CREATOR(APPLICATION_GLOBAL, "Applicant"), //
    APPLICATION_QUALIFIICATION_TYPE(APPLICATION_GLOBAL, "Qualification Type"), //
    APPLICATION_PROOF_OF_AWARD(APPLICATION_GLOBAL, "Proof of Award"), //
    APPLICATION_START_DATE(APPLICATION_GLOBAL, "Start Date"), //
    APPLICATION_CONFIRMED_START_DATE(APPLICATION_GLOBAL, "Confirmed Start Date"), //
    APPLICATION_PREFERRED_START_DATE(APPLICATION_GLOBAL, "Preferred Start Date"), //
    APPLICATION_END_DATE(APPLICATION_GLOBAL, "End Date"), //
    APPLICATION_REFERRAL_SOURCE(APPLICATION_GLOBAL, "How did you find us?"), //
    APPLICATION_SUBMISSION_DATE(APPLICATION_GLOBAL, "Submission Date"), //
    APPLICATION_EMPLOYER_NAME(APPLICATION_GLOBAL, "Employer Name"), //
    APPLICATION_POSITION_TITLE(APPLICATION_GLOBAL, "Position Title"), //
    APPLICATION_CODE(APPLICATION_GLOBAL, "Application Code"), //
    APPLICATION_PROGRAM_DETAIL_HEADER(APPLICATION_PROGRAM_DETAIL, "Program Detail"), //
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
    APPLICATION_PASSPORT_HEADER(APPLICATION_PASSPORT, "Passport"), //
    APPLICATION_PASSPORT_NUMBER(APPLICATION_PASSPORT, "Passport Number"), //
    APPLICATION_PASSPORT_NAME(APPLICATION_PASSPORT, "Name on Passport"), //
    APPLICATION_PASSPORT_ISSUE_DATE(APPLICATION_PASSPORT, "Issue Date"), //
    APPLICATION_PASSPORT_EXPIRY_DATE(APPLICATION_PASSPORT, "Expiry Date"), //
    APPLICATION_LANGUAGE_QUALIFICATION_HEADER(APPLICATION_LANGUAGE_QUALIFICATION, "Language Qualification"), //
    APPLICATION_LANGUAGE_QUALIFICATION_EXAM_DATE(APPLICATION_LANGUAGE_QUALIFICATION, "Date of Examination"), //
    APPLICATION_LANGUAGE_QUALIFICATION_OVERALL_SCORE(APPLICATION_LANGUAGE_QUALIFICATION, "Overall Score"), //
    APPLICATION_LANGUAGE_QUALIFICATION_READING_SCORE(APPLICATION_LANGUAGE_QUALIFICATION, "Reading Score"), //
    APPLICATION_LANGUAGE_QUALIFICATION_WRITING_SCORE(APPLICATION_LANGUAGE_QUALIFICATION, "Essay/Writing Score"), //
    APPLICATION_LANGUAGE_QUALIFICATION_SPEAKING_SCORE(APPLICATION_LANGUAGE_QUALIFICATION, "Speaking Score"), //
    APPLICATION_LANGUAGE_QUALIFICATION_LISTENING_SCORE(APPLICATION_LANGUAGE_QUALIFICATION, "Listening Score"), //
    APPLICATION_LANGUAGE_QUALIFICATION_APPENDIX(APPLICATION_LANGUAGE_QUALIFICATION, "Language Qualification Transcript"), //
    APPLICATION_ADDRESS_HEADER(APPLICATION_ADDRESS, "Address Detail"), //
    APPLICATION_ADDRESS_CURRENT(APPLICATION_ADDRESS, "Current Address"), //
    APPLICATION_ADDRESS_CONTACT(APPLICATION_ADDRESS, "Contact Address"), //
    APPLICATION_QUALIFICATION_HEADER(APPLICATION_QUALIFICATION, "Qualifications"), //
    APPLICATION_QUALIFICATION_SUBHEADER(APPLICATION_QUALIFICATION, "Qualification"), //
    APPLICATION_QUALIFICATION_COUNTRY(APPLICATION_QUALIFICATION, "Study Country"), //
    APPLICATION_QUALIFICATION_PROVIDER(APPLICATION_QUALIFICATION, "Study/Qualification Provider"), //
    APPLICATION_QUALIFICATION_TITLE(APPLICATION_QUALIFICATION, "Qualification Title"), //
    APPLICATION_QUALIFICATION_SUBJECT(APPLICATION_QUALIFICATION, "Qualification Subject"), //
    APPLICATION_QUALIFICATION_LANGUAGE(APPLICATION_QUALIFICATION, "Language of Study"), //
    APPLICATION_QUALIFICATION_EXPECTED_RESULT(APPLICATION_QUALIFICATION, "Expected Grade/Result/GPA"), //
    APPLICATION_QUALIFICATION_CONFIRMED_RESULT(APPLICATION_QUALIFICATION, "Confirmed Grade/Result/GPA"), //
    APPLICATION_QUALIFICATION_EXPECTED_AWARD_DATE(APPLICATION_QUALIFICATION, "Expected Award Date"), //
    APPLICATION_QUALIFICATION_CONFIRMED_AWARD_DATE(APPLICATION_QUALIFICATION, "Confirmed Award Date"), //
    APPLICATION_QUALIFICATION_INTERIM_TRANSCRIPT(APPLICATION_QUALIFICATION, "Interim Transcript/Results"), //
    APPLICATION_QUALIFICATION_FINAL_TRANSCRIPT(APPLICATION_QUALIFICATION, "Final Transcript/Results"), //
    APPLICATION_QUALIFICATION_APPENDIX(APPLICATION_QUALIFICATION, "Qualification Transcript"), //
    APPLICATION_QUALIFICATION_EQUIVALENT_HEADER(APPLICATION_QUALIFICATION, "Equivalent Experience"), //
    APPLICATION_QUALIFICATION_EXPERIENCE_MESSAGE(
            APPLICATION_QUALIFICATION,
            "We consider that the applicant has experience equivalent to the typical academic entrance requirements for our program. It is therefore our recommendation that an appointment be made."), //
    APPLICATION_EMPLOYMENT_POSITION_HEADER(APPLICATION_EMPLOYMENT_POSITION, "Employment Positions"), //
    APPLICATION_EMPLOYMENT_POSITION_SUBHEADER(APPLICATION_EMPLOYMENT_POSITION, "Position"), //
    APPLICATION_EMPLOYMENT_POSITION_EMPLOYER_ADDRESS(APPLICATION_EMPLOYMENT_POSITION, "Employer Address"), //
    APPLICATION_EMPLOYMENT_POSITION_REMIT(APPLICATION_EMPLOYMENT_POSITION, "Position Remit"), //
    APPLICATION_EMPLOYMENT_POSITION_IS_CURRENT(APPLICATION_EMPLOYMENT_POSITION, "Is this your Current Position?"), //
    APPLICATION_FUNDING_HEADER(APPLICATION_FUNDING, "Funding Awards"), //
    APPLICATION_FUNDING_SUBHEADER(APPLICATION_FUNDING, "Award"), //
    APPLICATION_FUNDING_TYPE(APPLICATION_FUNDING, "Award Type"), //
    APPLICATION_FUNDING_DESCRIPTION(APPLICATION_FUNDING, "Award Description"), //
    APPLICATION_FUNDING_VALUE(APPLICATION_FUNDING, "Award Value"), //
    APPLICATION_FUNDING_APPENDIX(APPLICATION_FUNDING, "Proof of Funding"), //
    APPLICATION_REFEREE_HEADER(APPLICATION_REFEREE, "Referees"), //
    APPLICATION_REFEREE_SUBHEADER(APPLICATION_REFEREE, "Referee"), //
    APPLICATION_REFEREE_REFERENCE_APPENDIX(APPLICATION_REFEREE, "Reference"), //
    APPLICATION_REFEREE_REFERENCE_COMMENT(APPLICATION_REFEREE, "Reference Comment"), //
    APPLICATION_REFEREE_REFERENCE_COMMENT_EQUIVALENT(
            APPLICATION_REFEREE,
            "Having considered the whole application, including both written and spoken feedback from referees, we are happy to make an appointment based upon the information available to us."), //
    APPLICATION_DOCUMENT_HEADER(APPLICATION_DOCUMENT, "Documents"), //
    APPLICATION_DOCUMENT_PERSONAL_STATEMENT_APPENDIX(APPLICATION_DOCUMENT, "Personal Statement"), //
    APPLICATION_DOUCMENT_CV_APPENDIX(APPLICATION_DOCUMENT, "CV/Resume"), //
    APPLICATION_ADDITIONAL_INFORMATION_HEADER(APPLICATION_ADDITIONAL_INFORMATION, "Additional Information"), //
    APPLICATION_ADDITIONAL_INFORMATION_CONVICTION(APPLICATION_ADDITIONAL_INFORMATION, "Unspent Criminal Convictions"), //
    APPLICATION_COMMENT_SUITABLE_FOR_INSTITUTION(APPLICATION_COMMENT, "Suitable for Recruiting Institution?"), //
    APPLICATION_COMMENT_SUITABLE_FOR_OPPORTUNITY(APPLICATION_COMMENT, "Suitable for Recruiting Position?"), //
    APPLICATION_COMMENT_DECLINED_REFEREE(APPLICATION_COMMENT, "Declined to provide a reference."), //
    APPLICATION_COMMENT_RECOMMENDED_OFFER_CONDITION(APPLICATION_COMMENT, "Recommended offer conditions"), //
    APPLICATION_COMMENT_REJECTION_SYSTEM(APPLICATION_COMMENT, "The opportunity that you applied for has been discontinued.");

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
