package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_ADDITIONAL_INFORMATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_ADDRESS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_DOCUMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_EMPLOYMENT_POSITION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_FUNDING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_LANGUAGE_QUALIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_PASSPORT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_PERSONAL_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_PRIZE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_PROGRAM_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_QUALIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_REPORT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_STATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_WORKFLOW;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.INSTITUTION_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.INSTITUTION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.INSTITUTION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.INSTITUTION_STATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.INSTITUTION_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.PROGRAM_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.PROGRAM_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.PROGRAM_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.PROGRAM_STATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.PROGRAM_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.PROJECT_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.PROJECT_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.PROJECT_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.PROJECT_STATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.PROJECT_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum PrismDisplayPropertyDefinition {

    SYSTEM_SYSTEM(SYSTEM_GLOBAL, "System", SYSTEM), //
    SYSTEM_INSTITUTION(SYSTEM_GLOBAL, "Institution", SYSTEM), //
    SYSTEM_PROGRAM(SYSTEM_GLOBAL, "Program", SYSTEM), //
    SYSTEM_PROJECT(SYSTEM_GLOBAL, "Project", SYSTEM), //
    SYSTEM_APPLICATION(SYSTEM_GLOBAL, "Application", SYSTEM), //
    SYSTEM_DATE_FORMAT(SYSTEM_GLOBAL, "dd MMM yyyy", SYSTEM), //
    SYSTEM_DATE_TIME_FORMAT(SYSTEM_GLOBAL, "dd MMM yyyy HH:mm", SYSTEM), //
    SYSTEM_TIME_FORMAT(SYSTEM_GLOBAL, "HH:mm", SYSTEM), //
    SYSTEM_YES(SYSTEM_GLOBAL, "Yes", SYSTEM), //
    SYSTEM_NO(SYSTEM_GLOBAL, "No", SYSTEM), //
    SYSTEM_VALUE_PROVIDED(SYSTEM_GLOBAL, "Provided", SYSTEM), //
    SYSTEM_VALUE_NOT_PROVIDED(SYSTEM_GLOBAL, "Not Provided", SYSTEM), //
    SYSTEM_ID(SYSTEM_GLOBAL, "Id", SYSTEM), //
    SYSTEM_TITLE(SYSTEM_GLOBAL, "Title", SYSTEM), //
    SYSTEM_NAME(SYSTEM_GLOBAL, "Name", SYSTEM), //
    SYSTEM_FIRST_NAME(SYSTEM_GLOBAL, "First Name", SYSTEM), //
    SYSTEM_FIRST_NAME_2(SYSTEM_GLOBAL, "First Name 2", SYSTEM), //
    SYSTEM_FIRST_NAME_3(SYSTEM_GLOBAL, "First Name 3", SYSTEM), //
    SYSTEM_LAST_NAME(SYSTEM_GLOBAL, "Last Name", SYSTEM), //
    SYSTEM_EMAIL(SYSTEM_GLOBAL, "Email", SYSTEM), //
    SYSTEM_TELEPHONE(SYSTEM_GLOBAL, "Telephone Number", SYSTEM), //
    SYSTEM_SKYPE(SYSTEM_GLOBAL, "Skype", SYSTEM), //
    SYSTEM_ADDRESS(SYSTEM_GLOBAL, "Address", SYSTEM), //
    SYSTEM_RATING(SYSTEM_GLOBAL, "Rating", SYSTEM), //
    SYSTEM_AVERAGE_RATING(SYSTEM_GLOBAL, "Average Rating", SYSTEM), //
    SYSTEM_TOTAL_RATING(SYSTEM_GLOBAL, "Total Ratings", SYSTEM), //
    SYSTEM_APPENDIX(SYSTEM_GLOBAL, "Appendix", SYSTEM), //
    SYSTEM_SEE(SYSTEM_GLOBAL, "See", SYSTEM), //
    SYSTEM_PAGE(SYSTEM_GLOBAL, "Page", SYSTEM), //
    SYSTEM_CREATED_DATE(SYSTEM_GLOBAL, "Created Date", SYSTEM), //
    SYSTEM_CLOSING_DATE(SYSTEM_GLOBAL, "Closing Date", SYSTEM), //
    SYSTEM_SUBMITTED_DATE(SYSTEM_GLOBAL, "Submitted Date", SYSTEM), //
    SYSTEM_UPDATED_DATE(SYSTEM_GLOBAL, "Updated Date", SYSTEM), //
    SYSTEM_ACADEMIC_YEAR(SYSTEM_GLOBAL, "Academic Year", SYSTEM), //
    SYSTEM_STATE(SYSTEM_GLOBAL, "State", SYSTEM), //
    SYSTEM_COMMENT_HEADER(SYSTEM_GLOBAL, "Comment", SYSTEM), //
    SYSTEM_EMAIL_LINK_MESSAGE(SYSTEM_GLOBAL, "If you are unable to follow the links in this message, copy and paste them directly into your browser", SYSTEM), //
    SYSTEM_TELEPHONE_PLACEHOLDER(SYSTEM_GLOBAL, "+44 (0) 0000 000 000", SYSTEM), //
    SYSTEM_IP_PLACEHOLDER(SYSTEM_GLOBAL, "127.0.0.1", SYSTEM), //
    SYSTEM_REFER_TO_DOCUMENT(SYSTEM_GLOBAL, "Refer to attached document", SYSTEM), //
    SYSTEM_OTHER(SYSTEM_GLOBAL, "Other", SYSTEM), //
    SYSTEM_NONE(SYSTEM_GLOBAL, "None", SYSTEM), //
    SYSTEM_PROCEED(SYSTEM_GLOBAL, "Proceed", SYSTEM), //
    SYSTEM_DECLINE(SYSTEM_GLOBAL, "Decline", SYSTEM), //
    SYSTEM_ACTIVATE_ACCOUNT(SYSTEM_GLOBAL, "Activate Account", SYSTEM), //
    SYSTEM_HELPDESK(SYSTEM_GLOBAL, "Get Help", SYSTEM), //
    SYSTEM_VIEW_EDIT(SYSTEM_GLOBAL, "View/Edit", SYSTEM), //
    SYSTEM_NEW_PASSWORD(SYSTEM_GLOBAL, "New password", SYSTEM), //
    SYSTEM_HOMEPAGE(SYSTEM_GLOBAL, "Homepage", SYSTEM), //
    SYSTEM_NOTIFICATION_TEMPLATE_PROPERTY_ERROR(SYSTEM_GLOBAL, "Property value unavailable", SYSTEM), //
    SYSTEM_HELPDESK_REPORT(SYSTEM_GLOBAL, "Please report this matter to our helpdesk", SYSTEM), //
    SYSTEM_APPLICATION_LIST(SYSTEM_GLOBAL, "Applications", SYSTEM), //
    SYSTEM_PROJECT_LIST(SYSTEM_GLOBAL, "Projects", SYSTEM), //
    SYSTEM_PROGRAM_LIST(SYSTEM_GLOBAL, "Programs", SYSTEM), //
    SYSTEM_INSTITUTION_LIST(SYSTEM_GLOBAL, "Institutions", SYSTEM), //
    SYSTEM_USER_ACCOUNT(SYSTEM_GLOBAL, "User Account", SYSTEM), //
    SYSTEM_COMMENT_CONTENT_NOT_PROVIDED(SYSTEM_GLOBAL, "No comment provided", SYSTEM), //
    SYSTEM_APPLY(SYSTEM_GLOBAL, "Apply Now", SYSTEM), //
    SYSTEM_COMMENT_UPDATED_USER_ROLE(SYSTEM_COMMENT, "Updated system user roles", SYSTEM), //
    SYSTEM_COMMENT_UPDATED_NOTIFICATION(SYSTEM_COMMENT, "Updated system notification configuration", SYSTEM), //
    SYSTEM_COMMENT_RESTORED_NOTIFICATION_GLOBAL(SYSTEM_COMMENT, "Restored system global notification configuration", SYSTEM), //
    SYSTEM_COMMENT_UPDATED_STATE_DURATION(SYSTEM_COMMENT, "Updated system state duration configuration", SYSTEM), //
    SYSTEM_COMMENT_RESTORED_STATE_DURATION_GLOBAL(SYSTEM_COMMENT, "Restored system global state duration configuration", SYSTEM), //
    SYSTEM_COMMENT_UPDATED_ACTION_PROPERTY(SYSTEM_COMMENT, "Updated system action property configuration", SYSTEM), //
    SYSTEM_COMMENT_RESTORED_ACTION_PROPERTY_GLOBAL(SYSTEM_COMMENT, "Restored system global action property configuration", SYSTEM), //
    SYSTEM_COMMENT_UPDATED_WORKFLOW_PROPERTY(SYSTEM_COMMENT, "Updated system workflow property configuration", SYSTEM), //
    SYSTEM_COMMENT_RESTORED_WORKFLOW_PROPERTY_GLOBAL(SYSTEM_COMMENT, "Restored system global workflow property configuration", SYSTEM), //
    SYSTEM_COMMENT_UPDATED_DISPLAY_PROPERTY(SYSTEM_COMMENT, "Updated system display property configuration", SYSTEM), //
    SYSTEM_COMMENT_RESTORED_DISPLAY_PROPERTY_GLOBAL(SYSTEM_COMMENT, "Restored system global display property configuration", SYSTEM), //
    SYSTEM_COMMENT_INITIALIZED_SYSTEM(SYSTEM_COMMENT, "System initialised and ready to use", SYSTEM), //
    SYSTEM_COMMENT_INITIALIZED_INSTITUTION(SYSTEM_COMMENT, "Institution initialised and ready to use", SYSTEM), //
    SYSTEM_COMMENT_CUSTOM_FORM_WEIGHT_ERROR(SYSTEM_COMMENT, "The weights for your rating fields must add up to one", SYSTEM), //
    INSTITUTION_HEADER(INSTITUTION_GLOBAL, "Institution", INSTITUTION), //
    INSTITUTION_COMMENT_APPROVED(
            INSTITUTION_COMMENT,
            "We are pleased to tell you that your institution has been approved. We will send you a message with further instructions when your institution is ready to use. If you do not receive this message within 1 hour, please contact our helpdesk",
            INSTITUTION), //
    INSTITUTION_COMMENT_CORRECTION(INSTITUTION_COMMENT,
            "Further information is required to activate your institution. Please login to address the reviewers comments", INSTITUTION), //
    INSTITUTION_COMMENT_REJECTED(INSTITUTION_COMMENT, "We are sorry to inform you that your institution has been rejected", INSTITUTION), //
    INSTITUTION_COMMENT_UPDATED(INSTITUTION_COMMENT, "Updated institution", INSTITUTION), //
    INSTITUTION_COMMENT_UPDATED_USER_ROLE(INSTITUTION_COMMENT, "Updated institution user roles", INSTITUTION), //
    INSTITUTION_COMMENT_UPDATED_NOTIFICATION(INSTITUTION_COMMENT, "Updated institution notification configuration", INSTITUTION), //
    INSTITUTION_COMMENT_RESTORED_NOTIFICATION_CONFIGURATION_DEFAULT(INSTITUTION_COMMENT, "Restored system default notification configuration", INSTITUTION), //
    INSTITUTION_COMMENT_RESTORED_NOTIFICATION_GLOBAL(INSTITUTION_COMMENT, "Restored institution global notification configuration", INSTITUTION), //
    INSTITUTION_COMMENT_UPDATED_STATE_DURATION(INSTITUTION_COMMENT, "Updated institution notification configuration", INSTITUTION), //
    INSTITUTION_COMMENT_RESTORED_STATE_DURATION_DEFAULT(INSTITUTION_COMMENT, "Restored system default state duration configuration", INSTITUTION), //
    INSTITUTION_COMMENT_RESTORED_STATE_DURATION_GLOBAL(INSTITUTION_COMMENT, "Restored institution global state duration configuration", INSTITUTION), //
    INSTITUTION_COMMENT_UPDATED_ACTION_PROPERTY(INSTITUTION_COMMENT, "Updated institution action property configuration", INSTITUTION), //
    INSTITUTION_COMMENT_RESTORED_ACTION_PROPERTY_DEFAULT(INSTITUTION_COMMENT, "Restored system default action property configuration", INSTITUTION), //
    INSTITUTION_COMMENT_RESTORED_ACTION_PROPERTY_GLOBAL(INSTITUTION_COMMENT, "Restored institution global action property configuration", INSTITUTION), //
    INSTITUTION_COMMENT_UPDATED_WORKFLOW_PROPERTY(INSTITUTION_COMMENT, "Updated institution workflow property configuration", INSTITUTION), //
    INSTITUTION_COMMENT_RESTORED_WORKFLOW_PROPERTY_DEFAULT(INSTITUTION_COMMENT, "Restored system default workflow property configuration", INSTITUTION), //
    INSTITUTION_COMMENT_RESTORED_WORKFLOW_PROPERTY_GLOBAL(INSTITUTION_COMMENT, "Restored institution global workflow property configuration", INSTITUTION), //
    INSTITUTION_COMMENT_UPDATED_DISPLAY_PROPERTY(INSTITUTION_COMMENT, "Updated institution display property configuration", INSTITUTION), //
    INSTITUTION_COMMENT_RESTORED_DISPLAY_PROPERTY_DEFAULT(INSTITUTION_COMMENT, "Restored system default display property configuration", INSTITUTION), //
    INSTITUTION_COMMENT_RESTORED_DISPLAY_PROPERTY_GLOBAL(INSTITUTION_COMMENT, "Restored institution global display property configuration", INSTITUTION), //
    PROGRAM_HEADER(PROGRAM_GLOBAL, "Program", PROGRAM), //
    PROGRAM_STUDY_OPTION(PROGRAM_GLOBAL, "Study Option", PROGRAM), //
    PROGRAM_STUDY_OPTION_FULL_TIME(PROGRAM_GLOBAL, "Full Time", PROGRAM), //
    PROGRAM_STUDY_OPTION_PART_TIME(PROGRAM_GLOBAL, "Part Time", PROGRAM), //
    PROGRAM_STUDY_OPTION_MODULAR_FLEXIBLE(PROGRAM_GLOBAL, "Modular/Flexible", PROGRAM), //
    PROGRAM_CATEGORY_STUDY(PROGRAM_GLOBAL, "Degrees", PROGRAM), //
    PROGRAM_CATEGORY_FUNDING(PROGRAM_GLOBAL, "Scholarships", PROGRAM), //
    PROGRAM_CATEGORY_EXPERIENCE(PROGRAM_GLOBAL, "Internships", PROGRAM), //
    PROGRAM_CATEGORY_WORK(PROGRAM_GLOBAL, "Jobs", PROGRAM), //
    PROGRAM_CATEGORY_LEARNING(PROGRAM_GLOBAL, "Courses", PROGRAM), //
    PROGRAM_TYPE_STUDY_UNDERGRADUATE(PROGRAM_GLOBAL, "Undergraduate Study", PROGRAM), //
    PROGRAM_TYPE_STUDY_POSTGRADUATE_TAUGHT(PROGRAM_GLOBAL, "Postgraduate (Taught) Study", PROGRAM), //
    PROGRAM_TYPE_STUDY_POSTGRADUATE_RESEARCH(PROGRAM_GLOBAL, "Postgraduate (Research) Study", PROGRAM), //
    PROGRAM_TYPE_SCHOLARSHIP_UNDERGRADUATE(PROGRAM_GLOBAL, "Undergraduate Study Scholarship", PROGRAM), //
    PROGRAM_TYPE_SCHOLARSHIP_POSTGRADUATE_TAUGHT(PROGRAM_GLOBAL, "Postgraduate (Taught) Study Scholarship", PROGRAM), //
    PROGRAM_TYPE_SCHOLARSHIP_POSTGRADUATE_RESEARCH(PROGRAM_GLOBAL, "Postgraduate (Research) Study Scholarship", PROGRAM), //
    PROGRAM_TYPE_WORK_EXPERIENCE(PROGRAM_GLOBAL, "Work Experience", PROGRAM), //
    PROGRAM_TYPE_EMPLOYMENT(PROGRAM_GLOBAL, "Employment", PROGRAM), //
    PROGRAM_TYPE_EMPLOYMENT_SECONDMENT(PROGRAM_GLOBAL, "Secondment", PROGRAM), //
    PROGRAM_TYPE_TRAINING(PROGRAM_GLOBAL, "Training", PROGRAM), //
    PROGRAM_COMMENT_APPROVED(PROGRAM_COMMENT,
            "We are pleased to tell you that your program has been approved. You may now login to create users and projects, and manage your recruitment",
            PROGRAM), //
    PROGRAM_COMMENT_CORRECTION(PROGRAM_COMMENT, "Further information is required to activate your program. Please login to address the reviewers comments",
            PROGRAM), //
    PROGRAM_COMMENT_REJECTED(PROGRAM_COMMENT, "We are sorry to inform you that your program has been rejected", PROGRAM), //
    PROGRAM_COMMENT_UPDATED(PROGRAM_COMMENT, "Updated program", PROGRAM), //
    PROGRAM_COMMENT_UPDATED_USER_ROLE(PROGRAM_COMMENT, "Updated program user roles", PROGRAM), //
    PROGRAM_COMMENT_UPDATED_NOTIFICATION(PROGRAM_COMMENT, "Updated program notification configuration", PROGRAM), //
    PROGRAM_COMMENT_RESTORED_NOTIFICATION_DEFAULT(PROGRAM_COMMENT, "Restored institution default notification configuration", PROGRAM), //
    PROGRAM_COMMENT_UPDATED_STATE_DURATION(PROGRAM_COMMENT, "Updated program state duration configuration", PROGRAM), //
    PROGRAM_COMMENT_RESTORED_STATE_DURATION_DEFAULT(PROGRAM_COMMENT, "Restored institution default state duration configuration", PROGRAM), //
    PROGRAM_COMMENT_UPDATED_ACTION_PROPERTY(PROGRAM_COMMENT, "Updated program action property configuration", PROGRAM), //
    PROGRAM_COMMENT_RESTORED_ACTION_PROPERTY_DEFAULT(PROGRAM_COMMENT, "Restored institution action property configuration", PROGRAM), //
    PROGRAM_COMMENT_UPDATED_WORKFLOW_PROPERTY(PROGRAM_COMMENT, "Updated program workflow property configuration", PROGRAM), //
    PROGRAM_COMMENT_RESTORED_WORKFLOW_PROPERTY_DEFAULT(PROGRAM_COMMENT, "Restored institution workflow property configuration", PROGRAM), //
    PROGRAM_COMMENT_UPDATED_DISPLAY_PROPERTY(PROGRAM_COMMENT, "Updated program display property configuration", PROGRAM), //
    PROGRAM_COMMENT_RESTORED_DISPLAY_PROPERTY_DEFAULT(PROGRAM_COMMENT, "Restored institution display property configuration", PROGRAM), //
    PROGRAM_COMMENT_UPDATED_ADVERT(PROGRAM_COMMENT, "Updated program advert", PROGRAM), //
    PROGRAM_COMMENT_UPDATED_FEE_AND_PAYMENT(PROGRAM_COMMENT, "Updated program fees and payments", PROGRAM), //
    PROGRAM_COMMENT_UPDATED_CATEGORY(PROGRAM_COMMENT, "Updated program categories", PROGRAM), //
    PROGRAM_COMMENT_UPDATED_CLOSING_DATE(PROGRAM_COMMENT, "Updated program closing dates", PROGRAM), //
    PROJECT_HEADER(PROJECT_GLOBAL, "Project", PROJECT), //
    PROJECT_COMMENT_APPROVED(PROJECT_COMMENT,
            "We are pleased to tell you that your project has been approved. You may now login to create users and manage your recruitment", PROJECT), //
    PROJECT_COMMENT_CORRECTION(PROJECT_COMMENT, "Further information is required to activate your project. Please login to address the reviewers comments",
            PROJECT), //
    PROJECT_COMMENT_REJECTED(PROJECT_COMMENT, "We are sorry to inform you that your project has been rejected", PROJECT), //
    PROJECT_COMMENT_UPDATED(PROJECT_COMMENT, "Updated project", PROJECT), //
    PROJECT_COMMENT_UPDATED_USER_ROLE(PROJECT_COMMENT, "Updated project user roles", PROJECT), //
    PROJECT_COMMENT_UPDATED_ADVERT(PROJECT_COMMENT, "Updated project advert", PROJECT), //
    PROJECT_COMMENT_UPDATED_FEE_AND_PAYMENT(PROJECT_COMMENT, "Updated project fees and payments", PROJECT), //
    PROJECT_COMMENT_UPDATED_CATEGORY(PROJECT_COMMENT, "Updated project categories", PROJECT), //
    PROJECT_COMMENT_UPDATED_CLOSING_DATE(PROJECT_COMMENT, "Updated project closing dates", PROJECT), //
    APPLICATION_HEADER(APPLICATION_GLOBAL, "Application", APPLICATION), //
    APPLICATION_CREATOR(APPLICATION_GLOBAL, "Applicant", APPLICATION), //
    APPLICATION_QUALIFICATION_TYPE(APPLICATION_GLOBAL, "Qualification Type", APPLICATION), //
    APPLICATION_PROOF_OF_AWARD(APPLICATION_GLOBAL, "Proof of Award", APPLICATION), //
    APPLICATION_START_DATE(APPLICATION_GLOBAL, "Start Date", APPLICATION), //
    APPLICATION_CONFIRMED_START_DATE(APPLICATION_GLOBAL, "Confirmed Start Date", APPLICATION), //
    APPLICATION_CONFIRMED_OFFER_TYPE(APPLICATION_GLOBAL, "Confirmed Offer Type", APPLICATION), //
    APPLICATION_OFFER_CONDITIONAL(APPLICATION_GLOBAL, "Conditional", APPLICATION), //
    APPLICATION_OFFER_UNCONDITIONAL(APPLICATION_GLOBAL, "Unconditional", APPLICATION), //
    APPLICATION_PREFERRED_START_DATE(APPLICATION_GLOBAL, "Preferred Start Date", APPLICATION), //
    APPLICATION_END_DATE(APPLICATION_GLOBAL, "End Date", APPLICATION), //
    APPLICATION_REFERRAL_SOURCE(APPLICATION_GLOBAL, "Referral Source", APPLICATION), //
    APPLICATION_REFERRER(APPLICATION_GLOBAL, "Referrer", APPLICATION), //
    APPLICATION_PRIMARY_THEME(APPLICATION_GLOBAL, "Primary Themes", APPLICATION), //
    APPLICATION_SECONDARY_THEME(APPLICATION_GLOBAL, "Secondary Themes", APPLICATION), //
    APPLICATION_PROVIDED_REFERENCES(APPLICATION_GLOBAL, "Provided References", APPLICATION), //
    APPLICATION_DECLINED_REFERENCES(APPLICATION_GLOBAL, "Declined References", APPLICATION), //
    APPLICATION_PREVIOUS_APPLICATION(APPLICATION_GLOBAL, "Previously Applied (?)", APPLICATION), //
    APPLICATION_STUDY_LOCATION(APPLICATION_PROGRAM_DETAIL, "Preferred Study Location", APPLICATION), //
    APPLICATION_STUDY_DIVISION(APPLICATION_PROGRAM_DETAIL, "Preferred Study Department", APPLICATION), //
    APPLICATION_STUDY_AREA(APPLICATION_PROGRAM_DETAIL, "Preferred Study Area", APPLICATION), //
    APPLICATION_STUDY_APPLICATION_ID(APPLICATION_PROGRAM_DETAIL, "Study Application Id", APPLICATION), //
    APPLICATION_SUBMISSION_DATE(APPLICATION_GLOBAL, "Submission Date", APPLICATION), //
    APPLICATION_EMPLOYER_NAME(APPLICATION_GLOBAL, "Employer Name", APPLICATION), //
    APPLICATION_POSITION_TITLE(APPLICATION_GLOBAL, "Position Title", APPLICATION), //
    APPLICATION_CODE(APPLICATION_GLOBAL, "Application Code", APPLICATION), //
    APPLICATION_PROGRAM_DETAIL_HEADER(APPLICATION_PROGRAM_DETAIL, "Program Detail", APPLICATION), //
    APPLICATION_SUPERVISOR_HEADER(APPLICATION_SUPERVISOR, "Supervisors", APPLICATION), //
    APPLICATION_SUPERVISOR_SUBHEADER(APPLICATION_SUPERVISOR, "Supervisor", APPLICATION), //
    APPLICATION_SUPERVISOR_AWARE_OF_APPLICATION(APPLICATION_SUPERVISOR, "Is this supervisor aware of your application?", APPLICATION), //
    APPLICATION_PERSONAL_DETAIL_HEADER(APPLICATION_PERSONAL_DETAIL, "Applicant Detail", APPLICATION), //
    APPLICATION_PERSONAL_DETAIL_GENDER(APPLICATION_PERSONAL_DETAIL, "Gender", APPLICATION), //
    APPLICATION_PERSONAL_DETAIL_DATE_OF_BIRTH(APPLICATION_PERSONAL_DETAIL, "Date of Birth", APPLICATION), //
    APPLICATION_PERSONAL_DETAIL_COUNTRY_OF_BIRTH(APPLICATION_PERSONAL_DETAIL, "Country of Birth", APPLICATION), //
    APPLICATION_PERSONAL_DETAIL_COUNTRY_OF_DOMICILE(APPLICATION_PERSONAL_DETAIL, "Country of Domicile", APPLICATION), //
    APPLICATION_PERSONAL_DETAIL_NATIONALITY(APPLICATION_PERSONAL_DETAIL, "Nationality", APPLICATION), //
    APPLICATION_PERSONAL_DETAIL_ETHNICITY(APPLICATION_PERSONAL_DETAIL, "Ethnicity", APPLICATION), //
    APPLICATION_PERSONAL_DETAIL_DISABILITY(APPLICATION_PERSONAL_DETAIL, "Disability", APPLICATION), //
    APPLICATION_PERSONAL_DETAIL_REQUIRE_VISA(APPLICATION_PERSONAL_DETAIL, "Do you Require a Visa to Study in the UK?", APPLICATION), //
    APPLICATION_PERSONAL_DETAIL_PASSPORT_AVAILABLE(APPLICATION_PERSONAL_DETAIL, "Do you have a passport?", APPLICATION), //
    APPLICATION_PERSONAL_DETAIL_WORK_LANGUAGE_FIRST_LANGUAGE(APPLICATION_PERSONAL_DETAIL, "Is the specified language of work your first language?", APPLICATION), //
    APPLICATION_PERSONAL_DETAIL_LANGUAGE_QUALIFICATION_AVAILABLE(APPLICATION_PERSONAL_DETAIL, "Do you have a language qualification?", APPLICATION), //
    APPLICATION_PASSPORT_HEADER(APPLICATION_PASSPORT, "Passport", APPLICATION), //
    APPLICATION_PASSPORT_NUMBER(APPLICATION_PASSPORT, "Passport Number", APPLICATION), //
    APPLICATION_PASSPORT_NAME(APPLICATION_PASSPORT, "Name on Passport", APPLICATION), //
    APPLICATION_PASSPORT_ISSUE_DATE(APPLICATION_PASSPORT, "Issue Date", APPLICATION), //
    APPLICATION_PASSPORT_EXPIRY_DATE(APPLICATION_PASSPORT, "Expiry Date", APPLICATION), //
    APPLICATION_LANGUAGE_QUALIFICATION_HEADER(APPLICATION_LANGUAGE_QUALIFICATION, "Language Qualification", APPLICATION), //
    APPLICATION_LANGUAGE_QUALIFICATION_EXAM_DATE(APPLICATION_LANGUAGE_QUALIFICATION, "Date of Examination", APPLICATION), //
    APPLICATION_LANGUAGE_QUALIFICATION_OVERALL_SCORE(APPLICATION_LANGUAGE_QUALIFICATION, "Overall Score", APPLICATION), //
    APPLICATION_LANGUAGE_QUALIFICATION_READING_SCORE(APPLICATION_LANGUAGE_QUALIFICATION, "Reading Score", APPLICATION), //
    APPLICATION_LANGUAGE_QUALIFICATION_WRITING_SCORE(APPLICATION_LANGUAGE_QUALIFICATION, "Essay/Writing Score", APPLICATION), //
    APPLICATION_LANGUAGE_QUALIFICATION_SPEAKING_SCORE(APPLICATION_LANGUAGE_QUALIFICATION, "Speaking Score", APPLICATION), //
    APPLICATION_LANGUAGE_QUALIFICATION_LISTENING_SCORE(APPLICATION_LANGUAGE_QUALIFICATION, "Listening Score", APPLICATION), //
    APPLICATION_LANGUAGE_QUALIFICATION_APPENDIX(APPLICATION_LANGUAGE_QUALIFICATION, "Language Qualification Transcript", APPLICATION), //
    APPLICATION_ADDRESS_HEADER(APPLICATION_ADDRESS, "Address Detail", APPLICATION), //
    APPLICATION_ADDRESS_CURRENT(APPLICATION_ADDRESS, "Current Address", APPLICATION), //
    APPLICATION_ADDRESS_CONTACT(APPLICATION_ADDRESS, "Contact Address", APPLICATION), //
    APPLICATION_QUALIFICATION_HEADER(APPLICATION_QUALIFICATION, "Qualifications", APPLICATION), //
    APPLICATION_QUALIFICATION_SUBHEADER(APPLICATION_QUALIFICATION, "Qualification", APPLICATION), //
    APPLICATION_QUALIFICATION_COUNTRY(APPLICATION_QUALIFICATION, "Study Country", APPLICATION), //
    APPLICATION_QUALIFICATION_PROVIDER(APPLICATION_QUALIFICATION, "Study/Qualification Provider", APPLICATION), //
    APPLICATION_QUALIFICATION_TITLE(APPLICATION_QUALIFICATION, "Qualification Title", APPLICATION), //
    APPLICATION_QUALIFICATION_SUBJECT(APPLICATION_QUALIFICATION, "Qualification Subject", APPLICATION), //
    APPLICATION_QUALIFICATION_LANGUAGE(APPLICATION_QUALIFICATION, "Language of Study", APPLICATION), //
    APPLICATION_QUALIFICATION_EXPECTED_RESULT(APPLICATION_QUALIFICATION, "Expected Grade/Result/GPA", APPLICATION), //
    APPLICATION_QUALIFICATION_CONFIRMED_RESULT(APPLICATION_QUALIFICATION, "Confirmed Grade/Result/GPA", APPLICATION), //
    APPLICATION_QUALIFICATION_EXPECTED_AWARD_DATE(APPLICATION_QUALIFICATION, "Expected Award Date", APPLICATION), //
    APPLICATION_QUALIFICATION_CONFIRMED_AWARD_DATE(APPLICATION_QUALIFICATION, "Confirmed Award Date", APPLICATION), //
    APPLICATION_QUALIFICATION_INTERIM_TRANSCRIPT(APPLICATION_QUALIFICATION, "Interim Transcript/Results", APPLICATION), //
    APPLICATION_QUALIFICATION_FINAL_TRANSCRIPT(APPLICATION_QUALIFICATION, "Final Transcript/Results", APPLICATION), //
    APPLICATION_QUALIFICATION_APPENDIX(APPLICATION_QUALIFICATION, "Qualification Transcript", APPLICATION), //
    APPLICATION_QUALIFICATION_EQUIVALENT_HEADER(APPLICATION_QUALIFICATION, "Equivalent Experience", APPLICATION), //
    APPLICATION_QUALIFICATION_EXPERIENCE_MESSAGE(
            APPLICATION_QUALIFICATION,
            "We consider that the applicant has experience equivalent to the typical academic entrance requirements for our program. It is therefore our recommendation that an appointment be made",
            APPLICATION), //
    APPLICATION_EMPLOYMENT_POSITION_HEADER(APPLICATION_EMPLOYMENT_POSITION, "Employment Positions", APPLICATION), //
    APPLICATION_EMPLOYMENT_POSITION_SUBHEADER(APPLICATION_EMPLOYMENT_POSITION, "Position", APPLICATION), //
    APPLICATION_EMPLOYMENT_POSITION_EMPLOYER_ADDRESS(APPLICATION_EMPLOYMENT_POSITION, "Employer Address", APPLICATION), //
    APPLICATION_EMPLOYMENT_POSITION_REMIT(APPLICATION_EMPLOYMENT_POSITION, "Position Remit", APPLICATION), //
    APPLICATION_EMPLOYMENT_POSITION_IS_CURRENT(APPLICATION_EMPLOYMENT_POSITION, "Is this your Current Position?", APPLICATION), //
    APPLICATION_FUNDING_HEADER(APPLICATION_FUNDING, "Funding Awards", APPLICATION), //
    APPLICATION_FUNDING_SUBHEADER(APPLICATION_FUNDING, "Award", APPLICATION), //
    APPLICATION_FUNDING_TYPE(APPLICATION_FUNDING, "Award Type", APPLICATION), //
    APPLICATION_FUNDING_SPONSOR(APPLICATION_FUNDING, "Award Sponsor", APPLICATION), //
    APPLICATION_FUNDING_DESCRIPTION(APPLICATION_FUNDING, "Award Description", APPLICATION), //
    APPLICATION_FUNDING_VALUE(APPLICATION_FUNDING, "Award Value", APPLICATION), //
    APPLICATION_FUNDING_AWARD_DATE(APPLICATION_FUNDING, "Award Date", APPLICATION), //
    APPLICATION_FUNDING_TERMS(APPLICATION_FUNDING, "Award Terms", APPLICATION), //
    APPLICATION_FUNDING_APPENDIX(APPLICATION_FUNDING, "Proof of Funding", APPLICATION), //
    APPLICATION_PRIZE_HEADER(APPLICATION_PRIZE, "Prizes", APPLICATION), //
    APPLICATION_PRIZE_SUBHEADER(APPLICATION_PRIZE, "Prize", APPLICATION), //
    APPLICATION_PRIZE_PROVIDER(APPLICATION_PRIZE, "Awarding Body", APPLICATION), //
    APPLICATION_PRIZE_TITLE(APPLICATION_PRIZE, "Title", APPLICATION), //
    APPLICATION_PRIZE_DESCRIPTION(APPLICATION_PRIZE, "Description", APPLICATION), //
    APPLICATION_PRIZE_AWARD_DATE(APPLICATION_PRIZE, "Award Dateo", APPLICATION), //
    APPLICATION_REFEREE_HEADER(APPLICATION_REFEREE, "Referees", APPLICATION), //
    APPLICATION_REFEREE_SUBHEADER(APPLICATION_REFEREE, "Referee", APPLICATION), //
    APPLICATION_REFEREE_REFERENCE_APPENDIX(APPLICATION_REFEREE, "Reference", APPLICATION), //
    APPLICATION_REFEREE_REFERENCE_COMMENT(APPLICATION_REFEREE, "Reference Comment", APPLICATION), //
    APPLICATION_REFEREE_REFERENCE_COMMENT_EQUIVALENT(
            APPLICATION_REFEREE,
            "Having considered the whole application, including both written and spoken feedback from referees, we are happy to make an appointment based upon the information available to us",
            APPLICATION), //
    APPLICATION_DOCUMENT_HEADER(APPLICATION_DOCUMENT, "Documents", APPLICATION), //
    APPLICATION_DOCUMENT_PERSONAL_STATEMENT_APPENDIX(APPLICATION_DOCUMENT, "Personal Statement", APPLICATION), //
    APPLICATION_DOCUMENT_CV_APPENDIX(APPLICATION_DOCUMENT, "CV/Resume", APPLICATION), //
    APPLICATION_DOCUMENT_RESEARCH_STATEMENT_APPENDIX(APPLICATION_DOCUMENT, "Research Statement", APPLICATION), //
    APPLICATION_DOCUMENT_COVERING_LETTER_APPENDIX(APPLICATION_DOCUMENT, "Covering Letter", APPLICATION), //
    APPLICATION_ADDITIONAL_INFORMATION_HEADER(APPLICATION_ADDITIONAL_INFORMATION, "Additional Information", APPLICATION), //
    APPLICATION_ADDITIONAL_INFORMATION_CONVICTION(APPLICATION_ADDITIONAL_INFORMATION, "Unspent Criminal Convictions", APPLICATION), //
    APPLICATION_COMMENT_DECLINED_REFEREE(APPLICATION_COMMENT, "Declined to provide a reference", APPLICATION), //
    APPLICATION_COMMENT_RECOMMENDED_OFFER_CONDITION(APPLICATION_COMMENT, "Recommended offer conditions", APPLICATION), //
    APPLICATION_COMMENT_REJECTION_SYSTEM(APPLICATION_COMMENT, "We are currently unable to offer you a position", APPLICATION), //
    APPLICATION_COMMENT_DIRECTIONS(APPLICATION_COMMENT, "Directions", APPLICATION), //
    APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED(APPLICATION_COMMENT, "No directions provided. Please contact the interviewer for further information",
            APPLICATION), //
    APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL(APPLICATION_COMMENT, "Updated the program details section", APPLICATION), //
    APPLICATION_COMMENT_UPDATED_SUPERVISOR(APPLICATION_COMMENT, "Updated the supervisors section", APPLICATION), //
    APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL(APPLICATION_COMMENT, "Updated the personal detail section", APPLICATION), //
    APPLICATION_COMMENT_UPDATED_ADDRESS(APPLICATION_COMMENT, "Updated the address section", APPLICATION), //
    APPLICATION_COMMENT_UPDATED_QUALIFICATION(APPLICATION_COMMENT, "Updated the qualification section", APPLICATION), //
    APPLICATION_COMMENT_UPDATED_EMPLOYMENT(APPLICATION_COMMENT, "Updated the employment section", APPLICATION), //
    APPLICATION_COMMENT_UPDATED_FUNDING(APPLICATION_COMMENT, "Updated the funding section", APPLICATION), //
    APPLICATION_COMMENT_UPDATED_REFEREE(APPLICATION_COMMENT, "Updated the referee section", APPLICATION), //
    APPLICATION_COMMENT_UPDATED_DOCUMENT(APPLICATION_COMMENT, "Updated the document section", APPLICATION), //
    APPLICATION_COMMENT_UPDATED_ADDITIONAL_INFORMATION(APPLICATION_COMMENT, "Updated the additional information section", APPLICATION), APPLICATION_CONFIRM_ELIGIBILITY_DURATION_LABEL(
            APPLICATION_STATE_DURATION, "Eligibility Confirmation Duration", APPLICATION), //
    APPLICATION_CONFIRM_ELIGIBILITY_DURATION_TOOLTIP(APPLICATION_STATE_DURATION,
            "The length of time you expect it to take to confirm the elibility of an applicant", APPLICATION), //
    APPLICATION_PROVIDE_REFERENCE_DURATION_LABEL(APPLICATION_STATE_DURATION, "Reference Duration", APPLICATION), //
    APPLICATION_PROVIDE_REFERENCE_DURATION_TOOLTIP(APPLICATION_STATE_DURATION, "The length of time you expect it to take to collect applicant references",
            APPLICATION), //
    APPLICATION_PROVIDE_REVIEW_DURATION_LABEL(APPLICATION_STATE_DURATION, "Review Duration", APPLICATION), //
    APPLICATION_PROVIDE_REVIEW_DURATION_TOOLTIP(APPLICATION_STATE_DURATION, "The length of time you expect it to take to collect applicant reviews",
            APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION_LABEL(APPLICATION_STATE_DURATION, "Interview Scheduling Duration", APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION_TOOLTIP(APPLICATION_STATE_DURATION,
            "The length of time you expect it to take to schedule an interview", APPLICATION), APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION_LABEL(
            APPLICATION_STATE_DURATION, "Interview Feedback Duration", APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION_TOOLTIP(APPLICATION_STATE_DURATION,
            "The length of time you expect it to take to collect feedback on an interview", APPLICATION), //
    APPLICATION_CONFIRM_SUPERVISION_DURATION_LABEL(APPLICATION_STATE_DURATION, "Supervision Confirmation Duration", APPLICATION), //
    APPLICATION_CONFIRM_SUPERVISION_DURATION_TOOLTIP(APPLICATION_STATE_DURATION,
            "The length of time you expect it to take for a supervisor to confirm that they are willing to supervise an applicant", APPLICATION), //
    APPLICATION_ESCALATE_DURATION_LABEL(APPLICATION_STATE_DURATION, "Escalation Duration", APPLICATION), //
    APPLICATION_ESCALATE_DURATION_TOOLTIP(APPLICATION_STATE_DURATION,
            "The length of time you wish to allow an application that is being processed to remain dormant, before it is automatically rejected or withdrawn",
            APPLICATION), //
    APPLICATION_PURGE_DURATION_LABEL(APPLICATION_STATE_DURATION, "Expiry Duration", APPLICATION), //
    APPLICATION_PURGE_DURATION_TOOLTIP(APPLICATION_STATE_DURATION,
            "The length of time you wish to keep information about a rejected or widthrawn application on record", APPLICATION), //
    PROJECT_ESCALATE_DURATION_LABEL(PROJECT_STATE_DURATION, "Escalation Duration", APPLICATION), //
    PROJECT_ESCALATE_DURATION_TOOLTIP(
            PROJECT_STATE_DURATION,
            "The length of time you wish to allow a new project request that is being processed to remain dormant, before it is automatically rejected or withdrawn",
            APPLICATION), //
    PROGRAM_ESCALATE_DURATION_LABEL(PROGRAM_STATE_DURATION, "Escalation Duration", APPLICATION), //
    PROGRAM_ESCALATE_DURATION_TOOLTIP(
            PROGRAM_STATE_DURATION,
            "The length of time you wish to allow a new program request that is being processed to remain dormant, before it is automatically rejected or withdrawn",
            APPLICATION), //
    INSTITUTION_ESCALATE_DURATION_LABEL(INSTITUTION_STATE_DURATION, "Escalation Duration", APPLICATION), //
    INSTITUTION_ESCALATE_DURATION_TOOLTIP(
            INSTITUTION_STATE_DURATION,
            "The length of time you wish to allow a new institution request that is being processed to remain dormant, before it is automatically rejected or withdrawn",
            APPLICATION), //
    APPLICATION_STUDY_DETAIL_LABEL(APPLICATION_WORKFLOW, "Advanced Study Preferences", APPLICATION), //
    APPLICATION_STUDY_DETAIL_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of advanced study preferences", APPLICATION), //
    APPLICATION_THEME_PRIMARY_LABEL(APPLICATION_WORKFLOW, "Primary Themes", APPLICATION), //
    APPLICATION_THEME_PRIMARY_TOOLTIP(APPLICATION_WORKFLOW, "Enable selection of primary study themes", APPLICATION), //
    APPLICATION_THEME_SECONDARY_LABEL(APPLICATION_WORKFLOW, "Secondary Themes", APPLICATION), //
    APPLICATION_THEME_SECONDARY_TOOLTIP(APPLICATION_WORKFLOW, "Enable selection of secondary study themes", APPLICATION), //
    APPLICATION_CREATOR_DEMOGRAPHIC_LABEL(APPLICATION_WORKFLOW, "Equal Opportunities Information", APPLICATION), //
    APPLICATION_CREATOR_DEMOGRAPHIC_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of applicant equal opportunities information", APPLICATION), //
    APPLICATION_LANGUAGE_LABEL(APPLICATION_WORKFLOW, "Language Competence", APPLICATION), //
    APPLICATION_LANGUAGE_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of language competence information", APPLICATION), //
    APPLICATION_LANGUAGE_PROOF_OF_AWARD_LABEL(APPLICATION_WORKFLOW, "Proof of Language Competence", APPLICATION), //
    APPLICATION_LANGUAGE_PROOF_OF_AWARD_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of documentary proof of language competence", APPLICATION), //
    APPLICATION_RESIDENCE_LABEL(APPLICATION_WORKFLOW, "Right of Residence", APPLICATION), //
    APPLICATION_RESIDENCE_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of right of residence information", APPLICATION), //
    APPLICATION_QUALIFICATION_LABEL(APPLICATION_WORKFLOW, "Qualifications", APPLICATION), //
    APPLICATION_QUALIFICATION_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of qualification information", APPLICATION), //
    APPLICATION_QUALIFICATION_PROOF_OF_AWARD_LABEL(APPLICATION_WORKFLOW, "Proof of Qualification", APPLICATION), //
    APPLICATION_QUALIFICATION_PROOF_OF_AWARD_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of documentary proof of qualification", APPLICATION), //
    APPLICATION_EMPLOYMENT_POSITION_LABEL(APPLICATION_WORKFLOW, "Employment History", APPLICATION), //
    APPLICATION_EMPLOYMENT_POSITION_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of employment history information", APPLICATION), //
    APPLICATION_FUNDING_LABEL(APPLICATION_WORKFLOW, "Funding", APPLICATION), //
    APPLICATION_FUNDING_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of funding information", APPLICATION), //
    APPLICATION_FUNDING_PROOF_OF_AWARD_LABEL(APPLICATION_WORKFLOW, "Proof of Funding", APPLICATION), //
    APPLICATION_FUNDING_PROOF_OF_AWARD_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of documentary proof of funding", APPLICATION), //
    APPLICATION_PRIZE_LABEL(APPLICATION_WORKFLOW, "Prizes", APPLICATION), //
    APPLICATION_PRIZE_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of information about prizes and achievements", APPLICATION), //
    APPLICATION_DOCUMENT_PERSONAL_STATEMENT_LABEL(APPLICATION_WORKFLOW, "Personal Statement", APPLICATION), //
    APPLICATION_DOCUMENT_PERSONAL_STATEMENT_TOOLTIP(APPLICATION_WORKFLOW, "Enable upload of a personal statement", APPLICATION), //
    APPLICATION_DOCUMENT_RESEARCH_STATEMENT_LABEL(APPLICATION_WORKFLOW, "Research Statement", APPLICATION), //
    APPLICATION_DOCUMENT_RESEARCH_STATEMENT_TOOLTIP(APPLICATION_WORKFLOW, "Enable upload of a research statement", APPLICATION), //
    APPLICATION_DOCUMENT_CV_LABEL(APPLICATION_WORKFLOW, "CV/Resume", APPLICATION), //
    APPLICATION_DOCUMENT_CV_TOOLTIP(APPLICATION_WORKFLOW, "Enable upload of a cv/resume", APPLICATION), //
    APPLICATION_DOCUMENT_COVERING_LETTER_LABEL(APPLICATION_WORKFLOW, "Covering Letter", APPLICATION), //
    APPLICATION_DOCUMENT_COVERING_LETTER_TOOLTIP(APPLICATION_WORKFLOW, "Enable upload of a covering letter", APPLICATION), //
    APPLICATION_CRIMINAL_CONVICTION_LABEL(APPLICATION_WORKFLOW, "Criminal Convictions", APPLICATION), //
    APPLICATION_CRIMINAL_CONVICTION_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of criminal conviction information", APPLICATION), //
    APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR_LABEL(APPLICATION_WORKFLOW, "Provisional Supervisor Assignment", APPLICATION), //
    APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR_TOOLTIP(APPLICATION_WORKFLOW, "Specify how many provisional supervisors may be assigned during application",
            APPLICATION), //
    APPLICATION_ASSIGN_REFEREE_LABEL(APPLICATION_WORKFLOW, "Referee Assignment", APPLICATION), //
    APPLICATION_ASSIGN_REFEREE_TOOLTIP(APPLICATION_WORKFLOW, "Specify how many referees may be assigned during application", APPLICATION), //
    APPLICATION_ASSIGN_REVIEWER_LABEL(APPLICATION_WORKFLOW, "Reviewer Assignment", APPLICATION), //
    APPLICATION_ASSIGN_REVIEWER_TOOLTIP(APPLICATION_WORKFLOW, "Specify how many reviewers may be assigned during any application processing action",
            APPLICATION), //
    APPLICATION_ASSIGN_INTERVIEWER_LABEL(APPLICATION_WORKFLOW, "Interviewer Assignment", APPLICATION), //
    APPLICATION_ASSIGN_INTERVIEWER_TOOLTIP(APPLICATION_WORKFLOW, "Specify how many interviewers may be assigned during any application processing action",
            APPLICATION), //
    APPLICATION_ASSIGN_PRIMARY_SUPERVISOR_LABEL(APPLICATION_WORKFLOW, "Primary Supervisor Assignment", APPLICATION), //
    APPLICATION_ASSIGN_PRIMARY_SUPERVISOR_TOOLTIP(APPLICATION_WORKFLOW,
            "Specify how many primary supervisors may be assigned during any application processing action", APPLICATION), //
    APPLICATION_ASSIGN_SECONDARY_SUPERVISOR_LABEL(APPLICATION_WORKFLOW, "Secondary Supervisor Assignment", APPLICATION), //
    APPLICATION_ASSIGN_SECONDARY_SUPERVISOR_TOOLTIP(APPLICATION_WORKFLOW,
            "Specify how many secondary supervisors may be assigned during any application processing action", APPLICATION), //
    APPLICATION_POSITION_DETAIL_LABEL(APPLICATION_WORKFLOW, "Project Detail", APPLICATION), //
    APPLICATION_POSITION_DETAIL_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of advanced project information", APPLICATION), //
    APPLICATION_OFFER_DETAIL_LABEL(APPLICATION_WORKFLOW, "Offer Detail", APPLICATION), //
    APPLICATION_OFFER_DETAIL_TOOLTIP(APPLICATION_WORKFLOW, "Enable collection of advanced terms of offer information", APPLICATION), //
    APPLICATION_COMPLETE_NOTIFICATION_LABEL(APPLICATION_NOTIFICATION, "Application Complete Notification", APPLICATION), //
    APPLICATION_COMPLETE_NOTIFICATION_TOOLTIP(APPLICATION_NOTIFICATION, "Confirmation of submission of an application", APPLICATION), //
    APPLICATION_COMPLETE_REQUEST_LABEL(APPLICATION_NOTIFICATION, "Application Complete Request", APPLICATION), //
    APPLICATION_COMPLETE_REQUEST_TOOLTIP(APPLICATION_NOTIFICATION, "First reminder to complete a saved application", APPLICATION), //
    APPLICATION_COMPLETE_REQUEST_REMINDER_LABEL(APPLICATION_NOTIFICATION, "Application Complete Reminder", APPLICATION), //
    APPLICATION_COMPLETE_REQUEST_REMINDER_TOOLTIP(APPLICATION_NOTIFICATION, "Follow up reminders to complete a saved application", APPLICATION), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_LABEL(APPLICATION_NOTIFICATION, "Application Confirm Interview Notification", APPLICATION), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_TOOLTIP(APPLICATION_NOTIFICATION, "Confirmation of arrangements for an interview", APPLICATION), //
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION_LABEL(APPLICATION_NOTIFICATION, "Application Confirm Offer Notification", APPLICATION), //
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION_TOOLTIP(APPLICATION_NOTIFICATION, "Confirmation of an offer recommendation", APPLICATION), //
    APPLICATION_CONFIRM_REJECTION_NOTIFICATION_LABEL(APPLICATION_NOTIFICATION, "Application Confirm Rejection Notification", APPLICATION), //
    APPLICATION_CONFIRM_REJECTION_NOTIFICATION_TOOLTIP(APPLICATION_NOTIFICATION, "Confirmation of a rejection", APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION_LABEL(APPLICATION_NOTIFICATION, "Application Interview Availability Notification", APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION_TOOLTIP(APPLICATION_NOTIFICATION, "Notification of submission of preferences for an interview",
            APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_LABEL(APPLICATION_NOTIFICATION, "Application Interview Availability Request", APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_TOOLTIP(APPLICATION_NOTIFICATION, "First notification to provide preferences for an interview",
            APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_REMINDER_LABEL(APPLICATION_NOTIFICATION, "Application Interview Availability Reminder", APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_REMINDER_TOOLTIP(APPLICATION_NOTIFICATION, "Follow up reminder to provide preferences for an interview",
            APPLICATION), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST_LABEL(APPLICATION_NOTIFICATION, "Application Reference Request", APPLICATION), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST_TOOLTIP(APPLICATION_NOTIFICATION, "First request to provide a references for an applicant", APPLICATION), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER_LABEL(APPLICATION_NOTIFICATION, "Application Reference Reminder", APPLICATION), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER_TOOLTIP(APPLICATION_NOTIFICATION, "Follow up reminder to provide references for an applicant", APPLICATION), //
    APPLICATION_TERMINATE_NOTIFICATION_LABEL(APPLICATION_NOTIFICATION, "Application Terminate Notification", APPLICATION), //
    APPLICATION_TERMINATE_NOTIFICATION_TOOLTIP(APPLICATION_NOTIFICATION, "Notification of an application having been terminated by the system", APPLICATION), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION_LABEL(APPLICATION_NOTIFICATION, "Application Update Interview Availability Notification",
            APPLICATION), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION_TOOLTIP(APPLICATION_NOTIFICATION, "Notification of revision of preferences for an interview",
            APPLICATION), //
    INSTITUTION_COMPLETE_APPROVAL_STAGE_NOTIFICATION_LABEL(INSTITUTION_NOTIFICATION, "Institution Complete Approval Notification", INSTITUTION), //
    INSTITUTION_COMPLETE_APPROVAL_STAGE_NOTIFICATION_TOOLTIP(INSTITUTION_NOTIFICATION, "Notification that a new institution request has been moderated",
            INSTITUTION), //
    INSTITUTION_CORRECT_REQUEST_LABEL(INSTITUTION_NOTIFICATION, "Institution Correct Request", INSTITUTION), //
    INSTITUTION_CORRECT_REQUEST_TOOLTIP(INSTITUTION_NOTIFICATION, "First request to provide clarifications/corrections to a new institution request",
            INSTITUTION), //
    INSTITUTION_CORRECT_REQUEST_REMINDER_LABEL(INSTITUTION_NOTIFICATION, "Institution Correct Reminder", INSTITUTION), //
    INSTITUTION_CORRECT_REQUEST_REMINDER_TOOLTIP(INSTITUTION_NOTIFICATION,
            "Follup reminder to provide clarifications/corrections to a new institution request", INSTITUTION), //
    INSTITUTION_IMPORT_ERROR_NOTIFICATION_LABEL(INSTITUTION_NOTIFICATION, "Institution Import Error Notification", INSTITUTION), //
    INSTITUTION_IMPORT_ERROR_NOTIFICATION_TOOLTIP(INSTITUTION_NOTIFICATION, "Notification of failure to import institution reference data", INSTITUTION), //
    INSTITUTION_STARTUP_NOTIFICATION_LABEL(INSTITUTION_NOTIFICATION, "Institution Startup Notification", INSTITUTION), //
    INSTITUTION_STARTUP_NOTIFICATION_TOOLTIP(INSTITUTION_NOTIFICATION, "Notification that an institution has been initialised and is ready to use", INSTITUTION), //
    PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION_LABEL(PROGRAM_NOTIFICATION, "Program Complete Approval Notification", PROGRAM), //
    PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION_TOOLTIP(PROGRAM_NOTIFICATION, "Notification that a new program request has been moderated", PROGRAM), //
    PROGRAM_CORRECT_REQUEST_LABEL(PROGRAM_NOTIFICATION, "Program Correct Request", PROGRAM), //
    PROGRAM_CORRECT_REQUEST_TOOLTIP(PROGRAM_NOTIFICATION, "First request to provide clarifications/corrections to a new program request", PROGRAM), //
    PROGRAM_CORRECT_REQUEST_REMINDER_LABEL(PROGRAM_NOTIFICATION, "Program Correct Reminder", PROGRAM), //
    PROGRAM_CORRECT_REQUEST_REMINDER_TOOLTIP(PROGRAM_NOTIFICATION, "Follow up reminder to provide clarifications/corrections to a new program request", PROGRAM), //
    PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION_LABEL(PROJECT_NOTIFICATION, "Project Complete Approval Notification", PROJECT), //
    PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION_TOOLTIP(PROJECT_NOTIFICATION, "Notification that a new project request has been moderated", PROJECT), //
    PROJECT_CORRECT_REQUEST_LABEL(PROJECT_NOTIFICATION, "Project Correct Request", PROJECT), //
    PROJECT_CORRECT_REQUEST_TOOLTIP(PROJECT_NOTIFICATION, "First request to provide clarifications/corrections to a new project request", PROJECT), //
    PROJECT_CORRECT_REQUEST_REMINDER_LABEL(PROJECT_NOTIFICATION, "Project Correct Reminder", PROJECT), //
    PROJECT_CORRECT_REQUEST_REMINDER_TOOLTIP(PROJECT_NOTIFICATION, "Follow up request to provide clarifications/corrections to a new project request", PROJECT), //
    SYSTEM_APPLICATION_RECOMMENDATION_NOTIFICATION_LABEL(SYSTEM_NOTIFICATION, "System Recommendation Notification", SYSTEM), //
    SYSTEM_APPLICATION_RECOMMENDATION_NOTIFICATION_TOOLTIP(SYSTEM_NOTIFICATION, "Notification of recommended programs/projects to a potential applicant",
            SYSTEM), //
    SYSTEM_APPLICATION_TASK_REQUEST_LABEL(SYSTEM_NOTIFICATION, "System Application Task Request", SYSTEM), //
    SYSTEM_APPLICATION_TASK_REQUEST_TOOLTIP(SYSTEM_NOTIFICATION, "First request to perform tasks on applications", SYSTEM), //
    SYSTEM_APPLICATION_TASK_REQUEST_REMINDER_LABEL(SYSTEM_NOTIFICATION, "System Application Task Reminder", SYSTEM), //
    SYSTEM_APPLICATION_TASK_REQUEST_REMINDER_TOOLTIP(SYSTEM_NOTIFICATION, "Follow up reminder to perform tasks on applications", SYSTEM), //
    SYSTEM_APPLICATION_UPDATE_NOTIFICATION_LABEL(SYSTEM_NOTIFICATION, "System Application Update Notification", SYSTEM), //
    SYSTEM_APPLICATION_UPDATE_NOTIFICATION_TOOLTIP(SYSTEM_NOTIFICATION, "Notification of updates to a given user's applications", SYSTEM), //
    SYSTEM_COMPLETE_REGISTRATION_REQUEST_LABEL(SYSTEM_NOTIFICATION, "System Complete Registration Request", SYSTEM), //
    SYSTEM_COMPLETE_REGISTRATION_REQUEST_TOOLTIP(SYSTEM_NOTIFICATION, "Request to complete registration by confirming username/password", SYSTEM), //
    SYSTEM_INSTITUTION_TASK_REQUEST_LABEL(SYSTEM_NOTIFICATION, "System Institution Task Request", SYSTEM), //
    SYSTEM_INSTITUTION_TASK_REQUEST_TOOLTIP(SYSTEM_NOTIFICATION, "First request to perform tasks on institutions", SYSTEM), //
    SYSTEM_INSTITUTION_TASK_REQUEST_REMINDER_LABEL(SYSTEM_NOTIFICATION, "System Institution Task Reminder", SYSTEM), //
    SYSTEM_INSTITUTION_TASK_REQUEST_REMINDER_TOOLTIP(SYSTEM_NOTIFICATION, "Follow up reminder to perform tasks on institutions", SYSTEM), //
    SYSTEM_INSTITUTION_UPDATE_NOTIFICATION_LABEL(SYSTEM_NOTIFICATION, "System Institution Update Notification", SYSTEM), //
    SYSTEM_INSTITUTION_UPDATE_NOTIFICATION_TOOLTIP(SYSTEM_NOTIFICATION, "Notification of updates to a given user's institutions", SYSTEM), //
    SYSTEM_INVITATION_NOTIFICATION_LABEL(SYSTEM_NOTIFICATION, "System Invitation Notification", SYSTEM), //
    SYSTEM_INVITATION_NOTIFICATION_TOOLTIP(SYSTEM_NOTIFICATION, "Notification of invitation to register by another user", SYSTEM), //
    SYSTEM_PASSWORD_NOTIFICATION_LABEL(SYSTEM_NOTIFICATION, "System Password Notification", SYSTEM), //
    SYSTEM_PASSWORD_NOTIFICATION_TOOLTIP(SYSTEM_NOTIFICATION, "Notification of a temporary system access to allow a new password to be set", SYSTEM), //
    SYSTEM_PROGRAM_TASK_REQUEST_LABEL(SYSTEM_NOTIFICATION, "System Program Task Request", SYSTEM), //
    SYSTEM_PROGRAM_TASK_REQUEST_TOOLTIP(SYSTEM_NOTIFICATION, "First request to perform tasks on programs", SYSTEM), //
    SYSTEM_PROGRAM_TASK_REQUEST_REMINDER_LABEL(SYSTEM_NOTIFICATION, "System Program Task Reminder", SYSTEM), //
    SYSTEM_PROGRAM_TASK_REQUEST_REMINDER_TOOLTIP(SYSTEM_NOTIFICATION, "Follow up reminder to perform tasks on programs", SYSTEM), //
    SYSTEM_PROGRAM_UPDATE_NOTIFICATION_LABEL(SYSTEM_NOTIFICATION, "System Program Update Notification", SYSTEM), //
    SYSTEM_PROGRAM_UPDATE_NOTIFICATION_TOOLTIP(SYSTEM_NOTIFICATION, "Notification of updates to a given user's programs", SYSTEM), //
    SYSTEM_PROJECT_TASK_REQUEST_LABEL(SYSTEM_NOTIFICATION, "System Project Task Request", SYSTEM), //
    SYSTEM_PROJECT_TASK_REQUEST_TOOLTIP(SYSTEM_NOTIFICATION, "First request to perform tasks on projects", SYSTEM), //
    SYSTEM_PROJECT_TASK_REQUEST_REMINDER_LABEL(SYSTEM_NOTIFICATION, "System Project Task Reminder", SYSTEM), //
    SYSTEM_PROJECT_TASK_REQUEST_REMINDER_TOOLTIP(SYSTEM_NOTIFICATION, "Follow up request to perform tasks on projects", SYSTEM), //
    SYSTEM_PROJECT_UPDATE_NOTIFICATION_LABEL(SYSTEM_NOTIFICATION, "System Project Update Notification", SYSTEM), //
    SYSTEM_PROJECT_UPDATE_NOTIFICATION_TOOLTIP(SYSTEM_NOTIFICATION, "Notification of updates to a given user's projects", SYSTEM), //
    APPLICATION_VERIFICATION_INSTANCE_COUNT(APPLICATION_REPORT, "Verification State Count", APPLICATION), //
    APPLICATION_VERIFICATION_INSTANCE_DURATION_AVERAGE(APPLICATION_REPORT, "Verification Duration Average", APPLICATION), //
    APPLICATION_REFERENCE_INSTANCE_COUNT(APPLICATION_REPORT, "Reference State Count", APPLICATION), //
    APPLICATION_REFERENCE_INSTANCE_DURATION_AVERAGE(APPLICATION_REPORT, "Reference Duration Average", APPLICATION), //
    APPLICATION_REVIEW_INSTANCE_COUNT(APPLICATION_REPORT, "Review State Count", APPLICATION), //
    APPLICATION_REVIEW_INSTANCE_DURATION_AVERAGE(APPLICATION_REPORT, "Review State Duration Average", APPLICATION), //
    APPLICATION_INTERVIEW_INSTANCE_COUNT(APPLICATION_REPORT, "Interview State Count", APPLICATION), //
    APPLICATION_INTERVIEW_INSTANCE_DURATION_AVERAGE(APPLICATION_REPORT, "Interview State Duration Average", APPLICATION), //
    APPLICATION_APPROVAL_INSTANCE_COUNT(APPLICATION_REPORT, "Approval State Count", APPLICATION), //
    APPLICATION_APPROVAL_INSTANCE_DURATION_AVERAGE(APPLICATION_REPORT, "Approval State Duration Average", APPLICATION), //
    APPLICATION_UNSUBMITTED_STATE_GROUP(APPLICATION_STATE_GROUP, "Unsubmitted", APPLICATION), //
    APPLICATION_VALIDATION_STATE_GROUP(APPLICATION_STATE_GROUP, "Validation", APPLICATION), //
    APPLICATION_VERIFICATION_STATE_GROUP(APPLICATION_STATE_GROUP, "Eligibity Confirmation", APPLICATION), //
    APPLICATION_REFERENCE_STATE_GROUP(APPLICATION_STATE_GROUP, "Reference", APPLICATION), //
    APPLICATION_REVIEW_STATE_GROUP(APPLICATION_STATE_GROUP, "Review", APPLICATION), //
    APPLICATION_INTERVIEW_STATE_GROUP(APPLICATION_STATE_GROUP, "Interview", APPLICATION), //
    APPLICATION_APPROVAL_STATE_GROUP(APPLICATION_STATE_GROUP, "Approval", APPLICATION), //
    APPLICATION_APPROVED_STATE_GROUP(APPLICATION_STATE_GROUP, "Approved", APPLICATION), //
    APPLICATION_REJECTED_STATE_GROUP(APPLICATION_STATE_GROUP, "Rejected", APPLICATION), //
    APPLICATION_WITHDRAWN_STATE_GROUP(APPLICATION_STATE_GROUP, "Withdrawn", APPLICATION), //
    PROJECT_APPROVAL_STATE_GROUP(PROJECT_STATE_GROUP, "Approval", PROJECT), //
    PROJECT_APPROVED_STATE_GROUP(PROJECT_STATE_GROUP, "Approved", PROJECT), //
    PROJECT_REJECTED_STATE_GROUP(PROJECT_STATE_GROUP, "Rejected", PROJECT), //
    PROJECT_DISABLED_STATE_GROUP(PROJECT_STATE_GROUP, "Disabled", PROJECT), //
    PROJECT_WITHDRAWN_STATE_GROUP(PROJECT_STATE_GROUP, "Withdrawn", PROJECT), //
    PROGRAM_APPROVAL_STATE_GROUP(PROGRAM_STATE_GROUP, "Approval", PROGRAM), //
    PROGRAM_APPROVED_STATE_GROUP(PROGRAM_STATE_GROUP, "Approved", PROGRAM), //
    PROGRAM_REJECTED_STATE_GROUP(PROGRAM_STATE_GROUP, "Rejected", PROGRAM), //
    PROGRAM_DISABLED_STATE_GROUP(PROGRAM_STATE_GROUP, "Disabled", PROGRAM), //
    PROGRAM_WITHDRAWN_STATE_GROUP(PROGRAM_STATE_GROUP, "Withdrawn", PROGRAM), //
    INSTITUTION_APPROVAL_STATE_GROUP(INSTITUTION_STATE_GROUP, "Approval", INSTITUTION), //
    INSTITUTION_APPROVED_STATE_GROUP(INSTITUTION_STATE_GROUP, "Approved", INSTITUTION), //
    INSTITUTION_REJECTED_STATE_GROUP(INSTITUTION_STATE_GROUP, "Rejected", INSTITUTION), //
    INSTITUTION_WITHDRAWN_STATE_GROUP(INSTITUTION_STATE_GROUP, "Withdrawn", INSTITUTION), //
    SYSTEM_RUNNING_STATE_GROUP(SYSTEM_STATE_GROUP, "Running", SYSTEM); //

    private PrismDisplayPropertyCategory displayCategory;

    private String defaultValue;

    private PrismScope scope;

    private static final HashMultimap<PrismDisplayPropertyCategory, PrismDisplayPropertyDefinition> categoryProperties = HashMultimap.create();

    static {
        for (PrismDisplayPropertyDefinition property : values()) {
            categoryProperties.put(property.displayCategory, property);
        }
    }

    private PrismDisplayPropertyDefinition(PrismDisplayPropertyCategory category, String defaultValue, PrismScope scope) {
        this.displayCategory = category;
        this.defaultValue = defaultValue;
        this.scope = scope;
    }

    public final PrismDisplayPropertyCategory getDisplayCategory() {
        return displayCategory;
    }

    public final String getDefaultValue() {
        return defaultValue;
    }

    public final PrismScope getScope() {
        return scope;
    }

    public static final Set<PrismDisplayPropertyDefinition> getByCategory(PrismDisplayPropertyCategory category) {
        return categoryProperties.get(category);
    }

}
