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
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.INSTITUTION_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.INSTITUTION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.PROGRAM_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.PROGRAM_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.PROJECT_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.PROJECT_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.SYSTEM_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayCategory.SYSTEM_GLOBAL;

import java.util.Set;

import com.google.common.collect.HashMultimap;

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
    SYSTEM_COMMENT_HEADER(SYSTEM_GLOBAL, "Comment"), //
    SYSTEM_EMAIL_LINK_MESSAGE(SYSTEM_GLOBAL, "If you are unable to follow the links in this message, copy and paste them directly into your browser"), //
    SYSTEM_TELEPHONE_PLACEHOLDER(SYSTEM_GLOBAL, "+44 (0) 0000 000 000"), //
    SYSTEM_IP_PLACEHOLDER(SYSTEM_GLOBAL, "127.0.0.1"), //
    SYSTEM_REFER_TO_DOCUMENT(SYSTEM_GLOBAL, "Refer to attached document"), //
    SYSTEM_OTHER(SYSTEM_GLOBAL, "Other"), //
    SYSTEM_NONE(SYSTEM_GLOBAL, "none"), //
    SYSTEM_PROCEED(SYSTEM_GLOBAL, "Proceed"), //
    SYSTEM_DECLINE(SYSTEM_GLOBAL, "Decline"), //
    SYSTEM_ACTIVATE_ACCOUNT(SYSTEM_GLOBAL, "Activate Account"), //
    SYSTEM_HELPDESK(SYSTEM_GLOBAL, "Get Help"), //
    SYSTEM_VIEW_EDIT(SYSTEM_GLOBAL, "View/Edit"), //
    SYSTEM_NEW_PASSWORD(SYSTEM_GLOBAL, "New password"), //
    SYSTEM_HOMEPAGE(SYSTEM_GLOBAL, "Homepage"), //
    SYSTEM_NOTIFICATION_TEMPLATE_PROPERTY_ERROR(SYSTEM_GLOBAL, "Property value unavailable"), //
    SYSTEM_HELPDESK_REPORT(SYSTEM_GLOBAL, "Please report this matter to our helpdesk"), //
    SYSTEM_APPLICATION_LIST(SYSTEM_GLOBAL, "Applications"), //
    SYSTEM_PROJECT_LIST(SYSTEM_GLOBAL, "Projects"), //
    SYSTEM_PROGRAM_LIST(SYSTEM_GLOBAL, "Programs"), //
    SYSTEM_INSTITUTION_LIST(SYSTEM_GLOBAL, "Institutions"), //
    SYSTEM_USER_ACCOUNT(SYSTEM_GLOBAL, "User Account"), //
    SYSTEM_COMMENT_CONTENT_NOT_PROVIDED(SYSTEM_GLOBAL, "No comment provided"), //
    SYSTEM_APPLY(SYSTEM_GLOBAL, "Apply Now"), //
    SYSTEM_COMMENT_UPDATED_USER_ROLE(SYSTEM_COMMENT, "Updated system user roles"), //
    SYSTEM_COMMENT_UPDATED_NOTIFICATION(SYSTEM_COMMENT, "Updated system notification configuration"), //
    SYSTEM_COMMENT_RESTORED_NOTIFICATION_GLOBAL(SYSTEM_COMMENT, "Restored system global notification template"), //
    SYSTEM_COMMENT_INITIALIZED_SYSTEM(SYSTEM_COMMENT, "System initialised and ready to use"), //
    SYSTEM_COMMENT_INITIALIZED_INSTITUTION(SYSTEM_COMMENT, "Institution initialised and ready to use"), //
    INSTITUTION_HEADER(INSTITUTION_GLOBAL, "Institution"), //
    INSTITUTION_COMMENT_APPROVED(
            INSTITUTION_COMMENT,
            "We are pleased to tell you that your institution has been approved. We will send you a message with further instructions when your institution is ready to use. If you do not receive this message within 1 hour, please contact our helpdesk"), //
    INSTITUTION_COMMENT_CORRECTION(INSTITUTION_COMMENT,
            "Further information is required to activate your institution. Please login to address the reviewers comments"), //
    INSTITUTION_COMMENT_REJECTED(INSTITUTION_COMMENT, "We are sorry to inform you that your institution has been rejected"), //
    INSTITUTION_COMMENT_UPDATED(INSTITUTION_COMMENT, "Updated institution"), //
    INSTITUTION_COMMENT_UPDATED_USER_ROLE(INSTITUTION_COMMENT, "Updated institution user roles"), //
    INSTITUTION_COMMENT_UPDATED_NOTIFICATION(INSTITUTION_COMMENT, "Updated institution notification configuration"), //
    INSTITUTION_COMMENT_RESTORED_NOTIFICATION_DEFAULT(INSTITUTION_COMMENT, "Restored system default notification template"), //
    INSTITUTION_COMMENT_RESTORED_NOTIFICATION_GLOBAL(INSTITUTION_COMMENT, "Restored institution global notification template"), //
    PROGRAM_HEADER(PROGRAM_GLOBAL, "Program"), //
    PROGRAM_STUDY_OPTION(PROGRAM_GLOBAL, "Study Option"), //
    PROGRAM_STUDY_OPTION_FULL_TIME(PROGRAM_GLOBAL, "Full Time"), //
    PROGRAM_STUDY_OPTION_PART_TIME(PROGRAM_GLOBAL, "Part Time"), //
    PROGRAM_STUDY_OPTION_MODULAR_FLEXIBLE(PROGRAM_GLOBAL, "Modular/Flexible"), //
    PROGRAM_CATEGORY_STUDY(PROGRAM_GLOBAL, "Degrees"), //
    PROGRAM_CATEGORY_FUNDING(PROGRAM_GLOBAL, "Scholarships"), //
    PROGRAM_CATEGORY_EXPERIENCE(PROGRAM_GLOBAL, "Internships"), //
    PROGRAM_CATEGORY_WORK(PROGRAM_GLOBAL, "Jobs"), //
    PROGRAM_CATEGORY_LEARNING(PROGRAM_GLOBAL, "Courses"), //
    PROGRAM_TYPE_STUDY_UNDERGRADUATE(PROGRAM_GLOBAL, "Undergraduate Study"), //
    PROGRAM_TYPE_STUDY_POSTGRADUATE_TAUGHT(PROGRAM_GLOBAL, "Postgraduate (Taught) Study"), //
    PROGRAM_TYPE_STUDY_POSTGRADUATE_RESEARCH(PROGRAM_GLOBAL, "Postgraduate (Research) Study"), //
    PROGRAM_TYPE_SCHOLARSHIP_UNDERGRADUATE(PROGRAM_GLOBAL, "Undergraduate Study Scholarship"), //
    PROGRAM_TYPE_SCHOLARSHIP_POSTGRADUATE_TAUGHT(PROGRAM_GLOBAL, "Postgraduate (Taught) Study Scholarship"), //
    PROGRAM_TYPE_SCHOLARSHIP_POSTGRADUATE_RESEARCH(PROGRAM_GLOBAL, "Postgraduate (Research) Study Scholarship"), //
    PROGRAM_TYPE_WORK_EXPERIENCE(PROGRAM_GLOBAL, "Work Experience"), //
    PROGRAM_TYPE_EMPLOYMENT(PROGRAM_GLOBAL, "Employment"), //
    PROGRAM_TYPE_EMPLOYMENT_SECONDMENT(PROGRAM_GLOBAL, "Secondment"), //
    PROGRAM_TYPE_TRAINING(PROGRAM_GLOBAL, "Training"), //
    PROGRAM_COMMENT_APPROVED(PROGRAM_COMMENT,
            "We are pleased to tell you that your program has been approved. You may now login to create users and projects, and manage your recruitment"), //
    PROGRAM_COMMENT_CORRECTION(PROGRAM_COMMENT, "Further information is required to activate your program. Please login to address the reviewers comments"), //
    PROGRAM_COMMENT_REJECTED(PROGRAM_COMMENT, "We are sorry to inform you that your program has been rejected"), //
    PROGRAM_COMMENT_UPDATED(PROGRAM_COMMENT, "Updated program"), //
    PROGRAM_COMMENT_UPDATED_USER_ROLE(PROGRAM_COMMENT, "Updated program user roles"), //
    PROGRAM_COMMENT_UPDATED_NOTIFICATION(PROGRAM_COMMENT, "Updated program notification configuration"), //
    PROGRAM_COMMENT_RESTORED_NOTIFICATION_DEFAULT(PROGRAM_COMMENT, "Restored institution default notification template"), //
    PROGRAM_COMMENT_UPDATED_ADVERT(PROGRAM_COMMENT, "Updated program advert"), //
    PROGRAM_COMMENT_UPDATED_FEE_AND_PAYMENT(PROGRAM_COMMENT, "Updated program fees and payments"), //
    PROGRAM_COMMENT_UPDATED_CATEGORY(PROGRAM_COMMENT, "Updated program categories"), //
    PROGRAM_COMMENT_UPDATED_CLOSING_DATE(PROGRAM_COMMENT, "Updated program closing dates"), //
    PROJECT_HEADER(PROJECT_GLOBAL, "Project"), //
    PROJECT_COMMENT_APPROVED(PROJECT_COMMENT,
            "We are pleased to tell you that your project has been approved. You may now login to create users and manage your recruitment"), //
    PROJECT_COMMENT_CORRECTION(PROJECT_COMMENT, "Further information is required to activate your project. Please login to address the reviewers comments"), //
    PROJECT_COMMENT_REJECTED(PROJECT_COMMENT, "We are sorry to inform you that your project has been rejected"), //
    PROJECT_COMMENT_UPDATED(PROJECT_COMMENT, "Updated project"), //
    PROJECT_COMMENT_UPDATED_USER_ROLE(PROJECT_COMMENT, "Updated project user roles"), //
    PROJECT_COMMENT_UPDATED_ADVERT(PROJECT_COMMENT, "Updated project advert"), //
    PROJECT_COMMENT_UPDATED_FEE_AND_PAYMENT(PROJECT_COMMENT, "Updated project fees and payments"), //
    PROJECT_COMMENT_UPDATED_CATEGORY(PROJECT_COMMENT, "Updated project categories"), //
    PROJECT_COMMENT_UPDATED_CLOSING_DATE(PROJECT_COMMENT, "Updated project closing dates"), //
    APPLICATION_HEADER(APPLICATION_GLOBAL, "Application"), //
    APPLICATION_CREATOR(APPLICATION_GLOBAL, "Applicant"), //
    APPLICATION_QUALIFICATION_TYPE(APPLICATION_GLOBAL, "Qualification Type"), //
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
            "We consider that the applicant has experience equivalent to the typical academic entrance requirements for our program. It is therefore our recommendation that an appointment be made"), //
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
            "Having considered the whole application, including both written and spoken feedback from referees, we are happy to make an appointment based upon the information available to us"), //
    APPLICATION_DOCUMENT_HEADER(APPLICATION_DOCUMENT, "Documents"), //
    APPLICATION_DOCUMENT_PERSONAL_STATEMENT_APPENDIX(APPLICATION_DOCUMENT, "Personal Statement"), //
    APPLICATION_DOCUMENT_CV_APPENDIX(APPLICATION_DOCUMENT, "CV/Resume"), //
    APPLICATION_ADDITIONAL_INFORMATION_HEADER(APPLICATION_ADDITIONAL_INFORMATION, "Additional Information"), //
    APPLICATION_ADDITIONAL_INFORMATION_CONVICTION(APPLICATION_ADDITIONAL_INFORMATION, "Unspent Criminal Convictions"), //
    APPLICATION_COMMENT_DECLINED_REFEREE(APPLICATION_COMMENT, "Declined to provide a reference"), //
    APPLICATION_COMMENT_RECOMMENDED_OFFER_CONDITION(APPLICATION_COMMENT, "Recommended offer conditions"), //
    APPLICATION_COMMENT_REJECTION_SYSTEM(APPLICATION_COMMENT, "The opportunity that you applied for has been discontinued"), //
    APPLICATION_COMMENT_DIRECTIONS(APPLICATION_COMMENT, "Directions"), //
    APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED(APPLICATION_COMMENT, "No directions provided. Please contact the interviewer for further information"), //
    APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL(APPLICATION_COMMENT, "Updated the program details section"), //
    APPLICATION_COMMENT_UPDATED_SUPERVISOR(APPLICATION_COMMENT, "Updated the supervisors section"), //
    APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL(APPLICATION_COMMENT, "Updated the personal detail section"), //
    APPLICATION_COMMENT_UPDATED_ADDRESS(APPLICATION_COMMENT, "Updated the address section"), //
    APPLICATION_COMMENT_UPDATED_QUALIFICATION(APPLICATION_COMMENT, "Updated the qualification section"), //
    APPLICATION_COMMENT_UPDATED_EMPLOYMENT(APPLICATION_COMMENT, "Updated the employment section"), //
    APPLICATION_COMMENT_UPDATED_FUNDING(APPLICATION_COMMENT, "Updated the funding section"), //
    APPLICATION_COMMENT_UPDATED_REFEREE(APPLICATION_COMMENT, "Updated the referee section"), //
    APPLICATION_COMMENT_UPDATED_DOCUMENT(APPLICATION_COMMENT, "Updated the document section"), //
    APPLICATION_COMMENT_UPDATED_ADDITIONAL_INFORMATION(APPLICATION_COMMENT, "Updated the additional information section");

    private PrismDisplayCategory category;

    private String defaultValue;

    private static final HashMultimap<PrismDisplayCategory, PrismDisplayProperty> categoryProperties = HashMultimap.create();

    static {
        for (PrismDisplayProperty property : values()) {
            categoryProperties.put(property.category, property);
        }
    }

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

    public static final Set<PrismDisplayProperty> getByCategory(PrismDisplayCategory category) {
        return categoryProperties.get(category);
    }

}
