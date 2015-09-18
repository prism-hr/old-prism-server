package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_ACTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_ADDITIONAL_INFORMATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_ADDRESS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_DOCUMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_EMPLOYMENT_POSITION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_FORM;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_PERSONAL_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_PROGRAM_DETAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_QUALIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.APPLICATION_REPORT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.DEPARTMENT_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.INSTITUTION_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.PROGRAM_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.PROJECT_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_ACCOUNT_SECTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_ACTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_ADVERTISE_SECTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_ADVERT_FUNCTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_ADVERT_INDUSTRY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_AUTHENTICATE_SECTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_DECLINE_ACTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_DISPLAY_PROPERTY_CATEGORY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_FILTER_EXPRESSION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_FILTER_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_HTML_ADDRESS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_HTML_COMMON;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_HTML_EMAIL_TEMPLATE_CONFIGURATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_HTML_FIELDS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_HTML_GENERAL_SECTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_HTML_MANAGE_USERS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_HTML_RESOURCE_CONFIGURATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_HTML_RESOURCE_OPPORTUNITY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_HTML_RESOURCE_PARENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_HTML_TRANSLATIONS_CONFIGURATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_HTML_WORKFLOW_CONFIGURATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_IMPORTED_ENTITY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_INTEGRATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_MONTH;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_NOTIFICATION_TEMPLATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_OPPORTUNITIES_ENQUIRY_SECTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_OPPORTUNITIES_SECTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_OPPORTUNITY_CATEGORY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_OPPORTUNITY_TYPE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_PERFORMANCE_INDICATOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_REFEREE_TYPE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_REPORT_INDICATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_RESERVE_STATUS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_RESOURCES_SECTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_RESOURCE_ADVERT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_RESOURCE_COMPETENCES;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_RESOURCE_FINANCIAL_DETAILS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_RESOURCE_SECTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_RESOURCE_TARGETS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_ROLE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_STATE_DURATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_STATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_STUDY_OPTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_VALIDATION_ERROR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory.SYSTEM_YES_NO_UNSURE;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum PrismDisplayPropertyDefinition implements PrismConfigurationCategorizable<PrismDisplayPropertyCategory> {

    /*
     * *************** SYSTEM GLOBAL *********************
     */

    SYSTEM_ORGANIZATION(SYSTEM_GLOBAL, "Organization"),
    SYSTEM_ORGANIZATIONS(SYSTEM_GLOBAL, "Organizations"),
    SYSTEM_DIVISION(SYSTEM_GLOBAL, "Department"),
    SYSTEM_DASHBOARD(SYSTEM_GLOBAL, "Dashboard"),
    SYSTEM_DIVISIONS(SYSTEM_GLOBAL, "Departments"),
    SYSTEM_OPPORTUNITY(SYSTEM_GLOBAL, "Opportunity"),
    SYSTEM_PROMOTED_BY(SYSTEM_GLOBAL, "Promoted by"),
    SYSTEM_OPPORTUNITIES(SYSTEM_GLOBAL, "Opportunities"),
    SYSTEM_EMPLOYERS(SYSTEM_GLOBAL, "Employers"),
    SYSTEM_UNIVERSITIES(SYSTEM_GLOBAL, "Universities"),
    SYSTEM_DEADLINE(SYSTEM_GLOBAL, "Deadline"),
    SYSTEM_DEADLINES(SYSTEM_GLOBAL, "Deadlines"),
    SYSTEM_SYSTEM(SYSTEM_GLOBAL, "System"),
    SYSTEM_SYSTEMS(SYSTEM_GLOBAL, "Systems"),
    SYSTEM_INSTITUTION(SYSTEM_GLOBAL, "Organization"),
    SYSTEM_INSTITUTIONS(SYSTEM_GLOBAL, "Organizations"),
    SYSTEM_DEPARTMENT(SYSTEM_GLOBAL, "Department"),
    SYSTEM_DEPARTMENTS(SYSTEM_GLOBAL, "Departments"),
    SYSTEM_PROGRAM(SYSTEM_GLOBAL, "Program"),
    SYSTEM_PROGRAMS(SYSTEM_GLOBAL, "Programs"),
    SYSTEM_PROJECT(SYSTEM_GLOBAL, "Position"),
    SYSTEM_PROJECTS(SYSTEM_GLOBAL, "Positions"),
    SYSTEM_CLOSING_DATE(SYSTEM_GLOBAL, "Closing Date"),
    SYSTEM_CLOSING_DATES(SYSTEM_GLOBAL, "Closing Dates"),
    SYSTEM_PANEL_DEADLINE(SYSTEM_GLOBAL, "Panel Deadline"),
    SYSTEM_PANEL_DEADLINES(SYSTEM_GLOBAL, "Panel Deadlines"),
    SYSTEM_APPLICATION(SYSTEM_GLOBAL, "Application"),
    SYSTEM_APPLICATIONS(SYSTEM_GLOBAL, "Applications"),
    SYSTEM_DATE_FORMAT(SYSTEM_GLOBAL, "dd MMM yyyy"),
    SYSTEM_DATE_TIME_FORMAT(SYSTEM_GLOBAL, "dd MMM yyyy HH:mm"),
    SYSTEM_TIME_FORMAT(SYSTEM_GLOBAL, "HH:mm"),
    SYSTEM_YES(SYSTEM_GLOBAL, "Yes"),
    SYSTEM_NO(SYSTEM_GLOBAL, "No"),
    SYSTEM_LOWER_IN(SYSTEM_GLOBAL, "in"),
    SYSTEM_LOWER_AT(SYSTEM_GLOBAL, "at"),
    SYSTEM_LOWER_OR(SYSTEM_GLOBAL, "or"),
    SYSTEM_VALUE_PROVIDED(SYSTEM_GLOBAL, "Provided"),
    SYSTEM_VALUE_NOT_PROVIDED(SYSTEM_GLOBAL, "Not Provided"),
    SYSTEM_VALUE_SPECIFIED(SYSTEM_GLOBAL, "Specified"),
    SYSTEM_VALUE_NOT_SPECIFIED(SYSTEM_GLOBAL, "Not Specified"),
    SYSTEM_ID(SYSTEM_GLOBAL, "Id"),
    SYSTEM_NAME(SYSTEM_GLOBAL, "Name"),
    SYSTEM_FIRST_NAME(SYSTEM_GLOBAL, "First Name"),
    SYSTEM_FIRST_NAME_2(SYSTEM_GLOBAL, "First Name 2"),
    SYSTEM_FIRST_NAME_3(SYSTEM_GLOBAL, "First Name 3"),
    SYSTEM_EMAIL(SYSTEM_GLOBAL, "Email"),
    SYSTEM_ADDRESS(SYSTEM_GLOBAL, "Address"),
    SYSTEM_RATING(SYSTEM_GLOBAL, "Rating"),
    SYSTEM_AVERAGE_RATING(SYSTEM_GLOBAL, "Average Rating"),
    SYSTEM_TOTAL_RATING(SYSTEM_GLOBAL, "Total Ratings"),
    SYSTEM_APPENDIX(SYSTEM_GLOBAL, "Appendix"),
    SYSTEM_SEE(SYSTEM_GLOBAL, "See"),
    SYSTEM_PAGE(SYSTEM_GLOBAL, "Page"),
    SYSTEM_CREATED_DATE(SYSTEM_GLOBAL, "Created Date"),
    SYSTEM_SUBMITTED_DATE(SYSTEM_GLOBAL, "Submitted Date"),
    SYSTEM_UPDATED_DATE(SYSTEM_GLOBAL, "Updated Date"),
    SYSTEM_ACADEMIC_YEAR(SYSTEM_GLOBAL, "Academic Year"),
    SYSTEM_STATE(SYSTEM_GLOBAL, "State"),
    SYSTEM_COMMENT_HEADER(SYSTEM_GLOBAL, "Comment"),
    SYSTEM_EMAIL_LINK_MESSAGE(SYSTEM_GLOBAL, "If you are unable to follow the links in this message, copy and paste them directly into your browser"),
    SYSTEM_TELEPHONE_PLACEHOLDER(SYSTEM_GLOBAL, "+44 (0) 0000 000 000"),
    SYSTEM_IP_PLACEHOLDER(SYSTEM_GLOBAL, "127.0.0.1"),
    SYSTEM_REFER_TO_DOCUMENT(SYSTEM_GLOBAL, "Refer to attached document"),
    SYSTEM_OTHER(SYSTEM_GLOBAL, "Other"),
    SYSTEM_NONE(SYSTEM_GLOBAL, "None"),
    SYSTEM_OPTION_SELECT(SYSTEM_GLOBAL, "Select ..."),
    SYSTEM_PROCEED(SYSTEM_GLOBAL, "Proceed"),
    SYSTEM_DECLINE(SYSTEM_GLOBAL, "Decline"),
    SYSTEM_ACTIVATE_ACCOUNT(SYSTEM_GLOBAL, "Activate Account"),
    SYSTEM_HELPDESK(SYSTEM_GLOBAL, "Get Help"),
    SYSTEM_VIEW_EDIT(SYSTEM_GLOBAL, "View/Edit"),
    SYSTEM_NEW_PASSWORD(SYSTEM_GLOBAL, "New password"),
    SYSTEM_HOMEPAGE(SYSTEM_GLOBAL, "Homepage"),
    SYSTEM_NOTIFICATION_TEMPLATE_PROPERTY_ERROR(SYSTEM_GLOBAL, "Property value unavailable"),
    SYSTEM_HELPDESK_REPORT(SYSTEM_GLOBAL, "Please report this matter to our helpdesk"),
    SYSTEM_USER_ACCOUNT(SYSTEM_GLOBAL, "User Account"),
    SYSTEM_APPLY(SYSTEM_GLOBAL, "Apply Now"),
    SYSTEM_DESCRIPTION(SYSTEM_GLOBAL, "The Opportunity Portal"),
    SYSTEM_LINK(SYSTEM_GLOBAL, "Link"),
    SYSTEM_NOW(SYSTEM_GLOBAL, "Now"),
    SYSTEM_NA(SYSTEM_GLOBAL, "N/A"),
    SYSTEM_NO_DIAGNOSTIC_INFORMATION(SYSTEM_GLOBAL, "Diagnostic information not available"),
    SYSTEM_EXTERNAL_HOMEPAGE(SYSTEM_GLOBAL, "External Homepage"),
    SYSTEM_ALREADY_REGISTERED(SYSTEM_GLOBAL, "Already registered with"),

    /*
     * *************** SYSTEM COMMENT *********************
     */

    SYSTEM_COMMENT_UPDATED_USER_ROLE(SYSTEM_COMMENT, "Updated system user roles"),
    SYSTEM_COMMENT_UPDATED_NOTIFICATION(SYSTEM_COMMENT, "Updated system notification configuration"),
    SYSTEM_COMMENT_RESTORED_NOTIFICATION_GLOBAL(SYSTEM_COMMENT, "Restored system global notification configuration"),
    SYSTEM_COMMENT_UPDATED_STATE_DURATION(SYSTEM_COMMENT, "Updated system state duration configuration"),
    SYSTEM_COMMENT_RESTORED_STATE_DURATION_GLOBAL(SYSTEM_COMMENT, "Restored system global state duration configuration"),
    SYSTEM_COMMENT_RESTORED_ACTION_PROPERTY_GLOBAL(SYSTEM_COMMENT, "Restored system global action property configuration"),
    SYSTEM_COMMENT_UPDATED_WORKFLOW_PROPERTY(SYSTEM_COMMENT, "Updated system workflow property configuration"),
    SYSTEM_COMMENT_RESTORED_WORKFLOW_PROPERTY_GLOBAL(SYSTEM_COMMENT, "Restored system global workflow property configuration"),
    SYSTEM_COMMENT_UPDATED_DISPLAY_PROPERTY(SYSTEM_COMMENT, "Updated system display property configuration"),
    SYSTEM_COMMENT_RESTORED_DISPLAY_PROPERTY_GLOBAL(SYSTEM_COMMENT, "Restored system global display property configuration"),
    SYSTEM_COMMENT_INITIALIZED_SYSTEM(SYSTEM_COMMENT, "System initialised and ready to use"),
    SYSTEM_COMMENT_CONTENT_NOT_PROVIDED(SYSTEM_COMMENT, "No comment provided"),

    /*
     * *************** SYSTEM STATE GROUP *********************
     */

    SYSTEM_STATE_GROUP_APPLICATION_UNSUBMITTED(SYSTEM_STATE_GROUP, "Unsubmitted"),
    SYSTEM_STATE_GROUP_APPLICATION_VALIDATION(SYSTEM_STATE_GROUP, "Preliminary Screening"),
    SYSTEM_STATE_GROUP_APPLICATION_REFERENCE(SYSTEM_STATE_GROUP, "Reference Collection"),
    SYSTEM_STATE_GROUP_APPLICATION_REVIEW(SYSTEM_STATE_GROUP, "Application Review"),
    SYSTEM_STATE_GROUP_APPLICATION_INTERVIEW(SYSTEM_STATE_GROUP, "Interview"),
    SYSTEM_STATE_GROUP_APPLICATION_APPROVAL(SYSTEM_STATE_GROUP, "Hiring Manager Approval"),
    SYSTEM_STATE_GROUP_APPLICATION_APPROVED(SYSTEM_STATE_GROUP, "Appointment Approved"),
    SYSTEM_STATE_GROUP_APPLICATION_REJECTED(SYSTEM_STATE_GROUP, "Candidate Rejected"),
    SYSTEM_STATE_GROUP_APPLICATION_WITHDRAWN(SYSTEM_STATE_GROUP, "Candidate Withdrawn"),
    SYSTEM_STATE_GROUP_DEPARTMENT_APPROVAL(SYSTEM_STATE_GROUP, "Department Approval"),
    SYSTEM_STATE_GROUP_DEPARTMENT_APPROVED(SYSTEM_STATE_GROUP, "Department Approved"),
    SYSTEM_STATE_GROUP_DEPARTMENT_REJECTED(SYSTEM_STATE_GROUP, "Department Rejected"),
    SYSTEM_STATE_GROUP_DEPARTMENT_DISABLED(SYSTEM_STATE_GROUP, "Department Disabled"),
    SYSTEM_STATE_GROUP_DEPARTMENT_WITHDRAWN(SYSTEM_STATE_GROUP, "Department Withdrawn"),
    SYSTEM_STATE_GROUP_INSTITUTION_APPROVAL(SYSTEM_STATE_GROUP, "Institution Approval"),
    SYSTEM_STATE_GROUP_INSTITUTION_APPROVED(SYSTEM_STATE_GROUP, "Institution Approved"),
    SYSTEM_STATE_GROUP_INSTITUTION_REJECTED(SYSTEM_STATE_GROUP, "Institution Rejected"),
    SYSTEM_STATE_GROUP_INSTITUTION_DISABLED(SYSTEM_STATE_GROUP, "Institution Disabled"),
    SYSTEM_STATE_GROUP_INSTITUTION_WITHDRAWN(SYSTEM_STATE_GROUP, "Institution Withdrawn"),
    SYSTEM_STATE_GROUP_PROGRAM_APPROVAL(SYSTEM_STATE_GROUP, "Program Approval"),
    SYSTEM_STATE_GROUP_PROGRAM_APPROVED(SYSTEM_STATE_GROUP, "Program Approved"),
    SYSTEM_STATE_GROUP_PROGRAM_REJECTED(SYSTEM_STATE_GROUP, "Program Rejected"),
    SYSTEM_STATE_GROUP_PROGRAM_DISABLED(SYSTEM_STATE_GROUP, "Program Disabled"),
    SYSTEM_STATE_GROUP_PROGRAM_WITHDRAWN(SYSTEM_STATE_GROUP, "Program Withdrawn"),
    SYSTEM_STATE_GROUP_PROJECT_APPROVAL(SYSTEM_STATE_GROUP, "Project Approval"),
    SYSTEM_STATE_GROUP_PROJECT_APPROVED(SYSTEM_STATE_GROUP, "Project Approved"),
    SYSTEM_STATE_GROUP_PROJECT_REJECTED(SYSTEM_STATE_GROUP, "Project Rejected"),
    SYSTEM_STATE_GROUP_PROJECT_DISABLED(SYSTEM_STATE_GROUP, "Project Disabled"),
    SYSTEM_STATE_GROUP_PROJECT_WITHDRAWN(SYSTEM_STATE_GROUP, "Project Withdrawn"),
    SYSTEM_STATE_GROUP_SYSTEM_RUNNING(SYSTEM_STATE_GROUP, "System Running"),

    /*
     * *************** SYSTEM STATE TRANSITION *********************
     */

    SYSTEM_STATE_TRANSITION_APPLICATION_APPROVAL(SYSTEM_STATE_TRANSITION, "Request hiring manager approval"),
    SYSTEM_STATE_TRANSITION_APPLICATION_APPROVED(SYSTEM_STATE_TRANSITION, "Confirm an appointment"),
    SYSTEM_STATE_TRANSITION_APPLICATION_INTERVIEW(SYSTEM_STATE_TRANSITION, "Arrange an interview"),
    SYSTEM_STATE_TRANSITION_APPLICATION_REJECTED(SYSTEM_STATE_TRANSITION, "Issue a rejection"),
    SYSTEM_STATE_TRANSITION_APPLICATION_REFERENCE(SYSTEM_STATE_TRANSITION, "Collect references"),
    SYSTEM_STATE_TRANSITION_APPLICATION_REVIEW(SYSTEM_STATE_TRANSITION, "Collect reviews"),
    SYSTEM_STATE_TRANSITION_INSTITUTION_APPROVAL_PENDING_CORRECTION(SYSTEM_STATE_TRANSITION, "Request further information"),
    SYSTEM_STATE_TRANSITION_INSTITUTION_APPROVED(SYSTEM_STATE_TRANSITION, "Approve organization"),
    SYSTEM_STATE_TRANSITION_INSTITUTION_REJECTED(SYSTEM_STATE_TRANSITION, "Reject organization"),
    SYSTEM_STATE_TRANSITION_DEPARTMENT_APPROVAL_PENDING_CORRECTION(SYSTEM_STATE_TRANSITION, "Request further information"),
    SYSTEM_STATE_TRANSITION_DEPARTMENT_APPROVED(SYSTEM_STATE_TRANSITION, "Approve department"),
    SYSTEM_STATE_TRANSITION_DEPARTMENT_REJECTED(SYSTEM_STATE_TRANSITION, "Reject department"),
    SYSTEM_STATE_TRANSITION_PROGRAM_APPROVAL_PENDING_CORRECTION(SYSTEM_STATE_TRANSITION, "Request further information"),
    SYSTEM_STATE_TRANSITION_PROGRAM_APPROVED(SYSTEM_STATE_TRANSITION, "Approve program"),
    SYSTEM_STATE_TRANSITION_PROGRAM_REJECTED(SYSTEM_STATE_TRANSITION, "Reject program"),
    SYSTEM_STATE_TRANSITION_PROJECT_APPROVAL_PENDING_CORRECTION(SYSTEM_STATE_TRANSITION, "Request further information"),
    SYSTEM_STATE_TRANSITION_PROJECT_APPROVED(SYSTEM_STATE_TRANSITION, "Approve project"),
    SYSTEM_STATE_TRANSITION_PROJECT_REJECTED(SYSTEM_STATE_TRANSITION, "Reject project"),

    /*
     * *************** SYSTEM ACTION *********************
     */

    SYSTEM_ACTION_APPLICATION_ASSESS_ELIGIBILITY(SYSTEM_ACTION, "Assess Eligibility"),
    SYSTEM_ACTION_APPLICATION_ASSIGN_INTERVIEWERS(SYSTEM_ACTION, "Assign Interviewers"),
    SYSTEM_ACTION_APPLICATION_ASSIGN_REVIEWERS(SYSTEM_ACTION, "Assign Reviewers"),
    SYSTEM_ACTION_APPLICATION_ASSIGN_HIRING_MANAGERS(SYSTEM_ACTION, "Assign Hiring Managers"),
    SYSTEM_ACTION_APPLICATION_RESERVE(SYSTEM_ACTION, "Commit To Reserve List"),
    SYSTEM_ACTION_APPLICATION_COMMENT(SYSTEM_ACTION, "Comment"),
    SYSTEM_ACTION_APPLICATION_COMPLETE(SYSTEM_ACTION, "Complete Application"),
    SYSTEM_ACTION_APPLICATION_COMPLETE_VALIDATION_STAGE(SYSTEM_ACTION, "Complete Screening Stage"),
    SYSTEM_ACTION_APPLICATION_COMPLETE_REFERENCE_STAGE(SYSTEM_ACTION, "Complete Reference Stage"),
    SYSTEM_ACTION_APPLICATION_COMPLETE_REVIEW_STAGE(SYSTEM_ACTION, "Complete Review Stage"),
    SYSTEM_ACTION_APPLICATION_COMPLETE_INTERVIEW_STAGE(SYSTEM_ACTION, "Complete Interview Stage"),
    SYSTEM_ACTION_APPLICATION_COMPLETE_APPROVAL_STAGE(SYSTEM_ACTION, "Complete Approval Stage"),
    SYSTEM_ACTION_APPLICATION_COMPLETE_APPROVED_STAGE(SYSTEM_ACTION, "Move to Different Stage"),
    SYSTEM_ACTION_APPLICATION_COMPLETE_RESERVED_STAGE(SYSTEM_ACTION, "Move to Different Stage"),
    SYSTEM_ACTION_APPLICATION_COMPLETE_REJECTED_STAGE(SYSTEM_ACTION, "Move to Different Stage"),
    SYSTEM_ACTION_APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS(SYSTEM_ACTION, "Confirm Interview Arrangements"),
    SYSTEM_ACTION_APPLICATION_CONFIRM_OFFER_RECOMMENDATION(SYSTEM_ACTION, "Confirm Appointment"),
    SYSTEM_ACTION_APPLICATION_CONFIRM_REJECTION(SYSTEM_ACTION, "Confirm Rejection"),
    SYSTEM_ACTION_APPLICATION_CONFIRM_APPOINTMENT(SYSTEM_ACTION, "Confirm Appointment"),
    SYSTEM_ACTION_APPLICATION_EMAIL_CREATOR(SYSTEM_ACTION, "Email Creator"),
    SYSTEM_ACTION_APPLICATION_ESCALATE(SYSTEM_ACTION, "Escalate"),
    SYSTEM_ACTION_APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY(SYSTEM_ACTION, "Provide Interview Availability"),
    SYSTEM_ACTION_APPLICATION_PROVIDE_INTERVIEW_FEEDBACK(SYSTEM_ACTION, "Provide Interview Feedback"),
    SYSTEM_ACTION_APPLICATION_PROVIDE_REFERENCE(SYSTEM_ACTION, "Provide Reference"),
    SYSTEM_ACTION_APPLICATION_PROVIDE_REVIEW(SYSTEM_ACTION, "Provide Review"),
    SYSTEM_ACTION_APPLICATION_REVERSE_REJECTION(SYSTEM_ACTION, "Reverse Rejection"),
    SYSTEM_ACTION_APPLICATION_TERMINATE(SYSTEM_ACTION, "Terminate"),
    SYSTEM_ACTION_APPLICATION_UPDATE_INTERVIEW_AVAILABILITY(SYSTEM_ACTION, "Update Interview Availability"),
    SYSTEM_ACTION_APPLICATION_UPLOAD_REFERENCE(SYSTEM_ACTION, "Upload Reference"),
    SYSTEM_ACTION_APPLICATION_VIEW_EDIT(SYSTEM_ACTION, "View / Edit"),
    SYSTEM_ACTION_APPLICATION_WITHDRAW(SYSTEM_ACTION, "Withdraw"),
    SYSTEM_ACTION_INSTITUTION_COMPLETE_APPROVAL_STAGE(SYSTEM_ACTION, "Complete Approval Stage"),
    SYSTEM_ACTION_INSTITUTION_VIEW_EDIT(SYSTEM_ACTION, "View / Edit"),
    SYSTEM_ACTION_INSTITUTION_CORRECT(SYSTEM_ACTION, "Correct"),
    SYSTEM_ACTION_INSTITUTION_CREATE_INSTITUTION(SYSTEM_ACTION, "Create Organization"),
    SYSTEM_ACTION_INSTITUTION_CREATE_DEPARTMENT(SYSTEM_ACTION, "Create Department"),
    SYSTEM_ACTION_INSTITUTION_CREATE_PROGRAM(SYSTEM_ACTION, "Create Program"),
    SYSTEM_ACTION_INSTITUTION_CREATE_PROJECT(SYSTEM_ACTION, "Create Position"),
    SYSTEM_ACTION_INSTITUTION_CREATE_APPLICATION(SYSTEM_ACTION, "Create Application"),
    SYSTEM_ACTION_INSTITUTION_DISABLE(SYSTEM_ACTION, "Disable"), //
    SYSTEM_ACTION_INSTITUTION_EMAIL_CREATOR(SYSTEM_ACTION, "Email Creator"),
    SYSTEM_ACTION_INSTITUTION_ESCALATE(SYSTEM_ACTION, "Escalate"),
    SYSTEM_ACTION_INSTITUTION_WITHDRAW(SYSTEM_ACTION, "Withdraw"),
    SYSTEM_ACTION_INSTITUTION_TERMINATE(SYSTEM_ACTION, "Terminate"),
    SYSTEM_ACTION_INSTITUTION_RESTORE(SYSTEM_ACTION, "Restore"),
    SYSTEM_ACTION_DEPARTMENT_COMPLETE_APPROVAL_STAGE(SYSTEM_ACTION, "Complete Approval Stage"),
    SYSTEM_ACTION_DEPARTMENT_VIEW_EDIT(SYSTEM_ACTION, "View / Edit"),
    SYSTEM_ACTION_DEPARTMENT_CORRECT(SYSTEM_ACTION, "Correct"),
    SYSTEM_ACTION_DEPARTMENT_CREATE_APPLICATION(SYSTEM_ACTION, "Create Application"),
    SYSTEM_ACTION_DEPARTMENT_CREATE_PROGRAM(SYSTEM_ACTION, "Create Program"),
    SYSTEM_ACTION_DEPARTMENT_CREATE_PROJECT(SYSTEM_ACTION, "Create Position"),
    SYSTEM_ACTION_DEPARTMENT_DISABLE(SYSTEM_ACTION, "Disable"), //
    SYSTEM_ACTION_DEPARTMENT_EMAIL_CREATOR(SYSTEM_ACTION, "Email Creator"),
    SYSTEM_ACTION_DEPARTMENT_ESCALATE(SYSTEM_ACTION, "Escalate"),
    SYSTEM_ACTION_DEPARTMENT_RESTORE(SYSTEM_ACTION, "Restore"),
    SYSTEM_ACTION_DEPARTMENT_WITHDRAW(SYSTEM_ACTION, "Withdraw"),
    SYSTEM_ACTION_DEPARTMENT_TERMINATE(SYSTEM_ACTION, "Terminate"),
    SYSTEM_ACTION_PROGRAM_COMPLETE_APPROVAL_STAGE(SYSTEM_ACTION, "Complete Approval Stage"),
    SYSTEM_ACTION_PROGRAM_VIEW_EDIT(SYSTEM_ACTION, "View / Edit"),
    SYSTEM_ACTION_PROGRAM_CORRECT(SYSTEM_ACTION, "Correct"),
    SYSTEM_ACTION_PROGRAM_CREATE_APPLICATION(SYSTEM_ACTION, "Create Application"),
    SYSTEM_ACTION_PROGRAM_CREATE_PROJECT(SYSTEM_ACTION, "Create Position"),
    SYSTEM_ACTION_PROGRAM_DISABLE(SYSTEM_ACTION, "Disable"), //
    SYSTEM_ACTION_PROGRAM_EMAIL_CREATOR(SYSTEM_ACTION, "Email Creator"),
    SYSTEM_ACTION_PROGRAM_ESCALATE(SYSTEM_ACTION, "Escalate"),
    SYSTEM_ACTION_PROGRAM_RESTORE(SYSTEM_ACTION, "Restore"),
    SYSTEM_ACTION_PROGRAM_WITHDRAW(SYSTEM_ACTION, "Withdraw"),
    SYSTEM_ACTION_PROGRAM_TERMINATE(SYSTEM_ACTION, "Terminate"),
    SYSTEM_ACTION_PROJECT_COMPLETE_APPROVAL_STAGE(SYSTEM_ACTION, "Complete Approval Stage"),
    SYSTEM_ACTION_PROJECT_VIEW_EDIT(SYSTEM_ACTION, "View / Edit"),
    SYSTEM_ACTION_PROJECT_CORRECT(SYSTEM_ACTION, "Correct"),
    SYSTEM_ACTION_PROJECT_CREATE_APPLICATION(SYSTEM_ACTION, "Create Application"),
    SYSTEM_ACTION_PROJECT_DISABLE(SYSTEM_ACTION, "Disable"), //
    SYSTEM_ACTION_PROJECT_EMAIL_CREATOR(SYSTEM_ACTION, "Email Creator"),
    SYSTEM_ACTION_PROJECT_ESCALATE(SYSTEM_ACTION, "Escalate"),
    SYSTEM_ACTION_PROJECT_RESTORE(SYSTEM_ACTION, "Restore"),
    SYSTEM_ACTION_PROJECT_TERMINATE(SYSTEM_ACTION, "Terminate"),
    SYSTEM_ACTION_PROJECT_WITHDRAW(SYSTEM_ACTION, "Withdraw"),
    SYSTEM_ACTION_SYSTEM_STARTUP(SYSTEM_ACTION, "Startup"),
    SYSTEM_ACTION_SYSTEM_VIEW_EDIT(SYSTEM_ACTION, "View / Edit"),
    SYSTEM_ACTION_SYSTEM_CREATE_INSTITUTION(SYSTEM_ACTION, "Create Organization"),
    SYSTEM_ACTION_SYSTEM_VIEW_INSTITUTION_LIST(SYSTEM_ACTION, "View Organizations"),
    SYSTEM_ACTION_SYSTEM_VIEW_DEPARTMENT_LIST(SYSTEM_ACTION, "View Departments"),
    SYSTEM_ACTION_SYSTEM_VIEW_PROGRAM_LIST(SYSTEM_ACTION, "View Programs"),
    SYSTEM_ACTION_SYSTEM_VIEW_PROJECT_LIST(SYSTEM_ACTION, "View Positions"),
    SYSTEM_ACTION_SYSTEM_VIEW_APPLICATION_LIST(SYSTEM_ACTION, "View Applications"),
    SYSTEM_ACTION_SYSTEM_MANAGE_ACCOUNT(SYSTEM_ACTION, "Manage Account"),

    /*
     * *************** SYSTEM ROLE *********************
     */

    SYSTEM_ROLE_APPLICATION_ADMINISTRATOR(SYSTEM_ROLE, "Administrator"),
    SYSTEM_ROLE_APPLICATION_CREATOR(SYSTEM_ROLE, "Creator"),
    SYSTEM_ROLE_APPLICATION_INTERVIEWEE(SYSTEM_ROLE, "Interviewee"),
    SYSTEM_ROLE_APPLICATION_INTERVIEWER(SYSTEM_ROLE, "Interviewer"),
    SYSTEM_ROLE_APPLICATION_POTENTIAL_INTERVIEWEE(SYSTEM_ROLE, "Potential Interviewee"),
    SYSTEM_ROLE_APPLICATION_POTENTIAL_INTERVIEWER(SYSTEM_ROLE, "Potential Interviewer"),
    SYSTEM_ROLE_APPLICATION_HIRING_MANAGER(SYSTEM_ROLE, "Hiring Manager"),
    SYSTEM_ROLE_APPLICATION_REFEREE(SYSTEM_ROLE, "Referee"),
    SYSTEM_ROLE_APPLICATION_REVIEWER(SYSTEM_ROLE, "Reviewer"),
    SYSTEM_ROLE_APPLICATION_VIEWER_RECRUITER(SYSTEM_ROLE, "Viewer Recruiter"),
    SYSTEM_ROLE_APPLICATION_VIEWER_REFEREE(SYSTEM_ROLE, "Viewer Referee"),
    SYSTEM_ROLE_DEPARTMENT_ADMINISTRATOR(SYSTEM_ROLE, "Administrator"),
    SYSTEM_ROLE_DEPARTMENT_APPROVER(SYSTEM_ROLE, "Approver"),
    SYSTEM_ROLE_DEPARTMENT_VIEWER(SYSTEM_ROLE, "Viewer"),
    SYSTEM_ROLE_DEPARTMENT_VIEWER_UNVERIFIED(SYSTEM_ROLE, "Unverified Viewer"),
    SYSTEM_ROLE_DEPARTMENT_STUDENT(SYSTEM_ROLE, "Student"),
    SYSTEM_ROLE_DEPARTMENT_STUDENT_UNVERIFIED(SYSTEM_ROLE, "Unverified student"),
    SYSTEM_ROLE_DEPARTMENT_EMPLOYEE(SYSTEM_ROLE, "Employee"),
    SYSTEM_ROLE_DEPARTMENT_EMPLOYEE_UNVERIFIED(SYSTEM_ROLE, "Unverified Employee"),
    SYSTEM_ROLE_INSTITUTION_ADMINISTRATOR(SYSTEM_ROLE, "Administrator"),
    SYSTEM_ROLE_INSTITUTION_APPROVER(SYSTEM_ROLE, "Approver"),
    SYSTEM_ROLE_INSTITUTION_VIEWER(SYSTEM_ROLE, "Viewer"),
    SYSTEM_ROLE_INSTITUTION_VIEWER_UNVERIFIED(SYSTEM_ROLE, "Unverified Viewer"),
    SYSTEM_ROLE_INSTITUTION_STUDENT(SYSTEM_ROLE, "Student"),
    SYSTEM_ROLE_INSTITUTION_STUDENT_UNVERIFIED(SYSTEM_ROLE, "Unverified student"),
    SYSTEM_ROLE_INSTITUTION_EMPLOYEE(SYSTEM_ROLE, "Employee"),
    SYSTEM_ROLE_INSTITUTION_EMPLOYEE_UNVERIFIED(SYSTEM_ROLE, "Unverified Employee"),
    SYSTEM_ROLE_PROGRAM_ADMINISTRATOR(SYSTEM_ROLE, "Administrator"),
    SYSTEM_ROLE_PROGRAM_APPROVER(SYSTEM_ROLE, "Approver"),
    SYSTEM_ROLE_PROGRAM_VIEWER(SYSTEM_ROLE, "Viewer"),
    SYSTEM_ROLE_PROJECT_ADMINISTRATOR(SYSTEM_ROLE, "Administrator"),
    SYSTEM_ROLE_PROJECT_APPROVER(SYSTEM_ROLE, "Approver"),
    SYSTEM_ROLE_PROJECT_VIEWER(SYSTEM_ROLE, "Viewer"),
    SYSTEM_ROLE_SYSTEM_ADMINISTRATOR(SYSTEM_ROLE, "Administrator"),

    /*
     * *************** SYSTEM OPPORTUNITY TYPE *********************
     */

    /*
     * *************** SYSTEM OPPORTUNITY CATEGORY *********************
     */

    SYSTEM_OPPORTUNITY_CATEGORY_STUDY(SYSTEM_OPPORTUNITY_CATEGORY, "Study"),
    SYSTEM_OPPORTUNITY_CATEGORY_PERSONAL_DEVELOPMENT(SYSTEM_OPPORTUNITY_CATEGORY, "Personal Development"),
    SYSTEM_OPPORTUNITY_CATEGORY_EXPERIENCE(SYSTEM_OPPORTUNITY_CATEGORY, "Work Experience"),
    SYSTEM_OPPORTUNITY_CATEGORY_WORK(SYSTEM_OPPORTUNITY_CATEGORY, "Employment"),

    SYSTEM_OPPORTUNITY_TYPE_STUDY_UNDERGRADUATE(SYSTEM_OPPORTUNITY_TYPE, "Undergraduate Study"),
    SYSTEM_OPPORTUNITY_TYPE_STUDY_POSTGRADUATE_TAUGHT(SYSTEM_OPPORTUNITY_TYPE, "Postgraduate Study"),
    SYSTEM_OPPORTUNITY_TYPE_STUDY_POSTGRADUATE_RESEARCH(SYSTEM_OPPORTUNITY_TYPE, "Postgraduate Research"),
    SYSTEM_OPPORTUNITY_TYPE_TRAINING(SYSTEM_OPPORTUNITY_TYPE, "Vocational Training"),
    SYSTEM_OPPORTUNITY_TYPE_WORK_EXPERIENCE(SYSTEM_OPPORTUNITY_TYPE, "Work Experience"),
    SYSTEM_OPPORTUNITY_TYPE_ON_COURSE_PLACEMENT(SYSTEM_OPPORTUNITY_TYPE, "On Course Placement"),
    SYSTEM_OPPORTUNITY_TYPE_VOLUNTEERING(SYSTEM_OPPORTUNITY_TYPE, "Volunteering"),
    SYSTEM_OPPORTUNITY_TYPE_EMPLOYMENT(SYSTEM_OPPORTUNITY_TYPE, "Employment"),
    SYSTEM_OPPORTUNITY_TYPE_EMPLOYMENT_SECONDMENT(SYSTEM_OPPORTUNITY_TYPE, "Secondment"),

    SYSTEM_OPPORTUNITY_TYPE_PAID_TOC(SYSTEM_OPPORTUNITY_TYPE, "In order to advertise an opportunity of this type we need you to confirm that: (a) you are happy to pay the successful applicant a minimum of the national minimum wage, or; (b) that you are committed to rewarding the successful applicant in some other fair way for their effort, for example through the granting of shares in a company. Please check to confirm. Alternatively, you can offer the opportunity as volunteering or on course placement."),
    SYSTEM_OPPORTUNITY_TYPE_OPTIONAL_TOC(SYSTEM_OPPORTUNITY_TYPE, "By offering an opportunity of this type you are confirming that the successful applicant will work with you as a required component of their course of study. In order to do this, you must accept that the providing university or department has the final right of acceptance in your recruitment process.While we recommend that do you pay the successful applicant the national minimum wage, or reward them in some other fair way, you are not legally required to do so."),
    SYSTEM_OPPORTUNITY_TYPE_UNPAID_TOC(SYSTEM_OPPORTUNITY_TYPE, "By offering an opportunity of this type, you are accepting that you are not legally entitled to bind the successful applicant into any kind of fixed work pattern or contract for employment/delivery. Alternatively, you can offer the opportunity as work experience, employment, or secondment."),o

    /*
     * *************** SYSTEM YES NO UNSURE *********************
     */

    SYSTEM_YES_NO_UNSURE_YES(SYSTEM_YES_NO_UNSURE, "Yes"),
    SYSTEM_YES_NO_UNSURE_NO(SYSTEM_YES_NO_UNSURE, "No"),
    SYSTEM_YES_NO_UNSURE_UNSURE(SYSTEM_YES_NO_UNSURE, "Unsure"),

    /*
     * *************** SYSTEM STUDY OPTION *********************
     */

    SYSTEM_STUDY_OPTION_FULL_TIME(SYSTEM_STUDY_OPTION, "Full Time"),
    SYSTEM_STUDY_OPTION_PART_TIME(SYSTEM_STUDY_OPTION, "Part Time"),

    /*
     * *************** SYSTEM DURATION *********************
     */

    SYSTEM_DURATION_UNIT_DAY(SYSTEM_DURATION, "Day"),
    SYSTEM_DURATION_UNIT_WEEK(SYSTEM_DURATION, "Week"),
    SYSTEM_DURATION_UNIT_MONTH(SYSTEM_DURATION, "Month"),
    SYSTEM_DURATION_UNIT_YEAR(SYSTEM_DURATION, "Year"),

    SYSTEM_DURATION_UNIT_PER_DAY(SYSTEM_DURATION, "per Day"),
    SYSTEM_DURATION_UNIT_PER_WEEK(SYSTEM_DURATION, "per Week"),
    SYSTEM_DURATION_UNIT_PER_MONTH(SYSTEM_DURATION, "per Month"),
    SYSTEM_DURATION_UNIT_PER_YEAR(SYSTEM_DURATION, "per Year"),

    /*
     * *************** SYSTEM FILTER PROPERTY *********************
     */

    SYSTEM_FILTER_PROPERTY_USER(SYSTEM_FILTER_PROPERTY, "Creator"),
    SYSTEM_FILTER_PROPERTY_CODE(SYSTEM_FILTER_PROPERTY, "Code"),
    SYSTEM_FILTER_PROPERTY_NAME(SYSTEM_FILTER_PROPERTY, "Name"),
    SYSTEM_FILTER_PROPERTY_INSTITUTION_NAME(SYSTEM_FILTER_PROPERTY, "Organization"),
    SYSTEM_FILTER_PROPERTY_DEPARTMENT_NAME(SYSTEM_FILTER_PROPERTY, "Department"),
    SYSTEM_FILTER_PROPERTY_PROGRAM_NAME(SYSTEM_FILTER_PROPERTY, "Program"),
    SYSTEM_FILTER_PROPERTY_PROJECT_NAME(SYSTEM_FILTER_PROPERTY, "Position"),
    SYSTEM_FILTER_PROPERTY_STATE_GROUP_NAME(SYSTEM_FILTER_PROPERTY, "State"),
    SYSTEM_FILTER_PROPERTY_CREATED_TIMESTAMP(SYSTEM_FILTER_PROPERTY, "Created Date"),
    SYSTEM_FILTER_PROPERTY_SUBMITTED_TIMESTAMP(SYSTEM_FILTER_PROPERTY, "Submitted Date"),
    SYSTEM_FILTER_PROPERTY_UPDATED_TIMESTAMP(SYSTEM_FILTER_PROPERTY, "Last Updated Date"),
    SYSTEM_FILTER_PROPERTY_DUE_DATE(SYSTEM_FILTER_PROPERTY, "Due Date"),
    SYSTEM_FILTER_PROPERTY_CLOSING_DATE(SYSTEM_FILTER_PROPERTY, "Closing Date"),
    SYSTEM_FILTER_PROPERTY_CONFIRMED_START_DATE(SYSTEM_FILTER_PROPERTY, "Confirmed Start Date"),
    SYSTEM_FILTER_PROPERTY_RATING(SYSTEM_FILTER_PROPERTY, "Rating"),
    SYSTEM_FILTER_PROPERTY_PROJECT_USER(SYSTEM_FILTER_PROPERTY, "Role Holder"),
    SYSTEM_FILTER_PROPERTY_PROGRAM_USER(SYSTEM_FILTER_PROPERTY, "Role Holder"),
    SYSTEM_FILTER_PROPERTY_DEPARTMENT_USER(SYSTEM_FILTER_PROPERTY, "Role Holder"),
    SYSTEM_FILTER_PROPERTY_INSTITUTION_USER(SYSTEM_FILTER_PROPERTY, "Role Holder"),
    SYSTEM_FILTER_PROPERTY_RESERVE_STATUS(SYSTEM_FILTER_PROPERTY, "Reserve State"),

    /*
     * *************** SYSTEM FILTER EXPRESSION *********************
     */

    SYSTEM_FILTER_EXPRESSION_BETWEEN(SYSTEM_FILTER_EXPRESSION, "Between"),
    SYSTEM_FILTER_EXPRESSION_BETWEEN_NEGATED(SYSTEM_FILTER_EXPRESSION, "Not Between"),
    SYSTEM_FILTER_EXPRESSION_CONTAIN(SYSTEM_FILTER_EXPRESSION, "Containing"),
    SYSTEM_FILTER_EXPRESSION_CONTAIN_NEGATED(SYSTEM_FILTER_EXPRESSION, "Not Containing"),
    SYSTEM_FILTER_EXPRESSION_EQUAL(SYSTEM_FILTER_EXPRESSION, "Equal"),
    SYSTEM_FILTER_EXPRESSION_EQUAL_NEGATED(SYSTEM_FILTER_EXPRESSION, "Not equal"),
    SYSTEM_FILTER_EXPRESSION_GREATER(SYSTEM_FILTER_EXPRESSION, "Greater"),
    SYSTEM_FILTER_EXPRESSION_GREATER_NEGATED(SYSTEM_FILTER_EXPRESSION, "Not greater than"),
    SYSTEM_FILTER_EXPRESSION_LESSER(SYSTEM_FILTER_EXPRESSION, "Less than"),
    SYSTEM_FILTER_EXPRESSION_LESSER_NEGATED(SYSTEM_FILTER_EXPRESSION, "Not less than"),
    SYSTEM_FILTER_EXPRESSION_NOT_SPECIFIED(SYSTEM_FILTER_EXPRESSION, "Not specified"),

    /*
     * *************** SYSTEM IMPORTED ENTITY *********************
     */

    SYSTEM_ENTITY_IMPORTED_AGE_RANGE(SYSTEM_IMPORTED_ENTITY, "Age Range"),
    SYSTEM_ENTITY_IMPORTED_COUNTRY(SYSTEM_IMPORTED_ENTITY, "Country"),
    SYSTEM_ENTITY_IMPORTED_DISABILITY(SYSTEM_IMPORTED_ENTITY, "Disability"),
    SYSTEM_ENTITY_IMPORTED_DOMICILE(SYSTEM_IMPORTED_ENTITY, "Domicile"),
    SYSTEM_ENTITY_IMPORTED_ETHNICITY(SYSTEM_IMPORTED_ENTITY, "Ethnicity"),
    SYSTEM_ENTITY_IMPORTED_NATIONALITY(SYSTEM_IMPORTED_ENTITY, "Nationality"),
    SYSTEM_ENTITY_IMPORTED_PROGRAM(SYSTEM_IMPORTED_ENTITY, "Program"),
    SYSTEM_ENTITY_IMPORTED_QUALIFICATION_TYPE(SYSTEM_IMPORTED_ENTITY, "Type"),
    SYSTEM_ENTITY_IMPORTED_REFERRAL_SOURCE(SYSTEM_IMPORTED_ENTITY, "Referral Source"),
    SYSTEM_ENTITY_IMPORTED_FUNDING_SOURCE(SYSTEM_IMPORTED_ENTITY, "Funding Source"),
    SYSTEM_ENTITY_IMPORTED_LANGUAGE_QUALIFICATION_TYPE(SYSTEM_IMPORTED_ENTITY, "Language Qualification Type"),
    SYSTEM_ENTITY_IMPORTED_TITLE(SYSTEM_IMPORTED_ENTITY, "Title"),
    SYSTEM_ENTITY_IMPORTED_GENDER(SYSTEM_IMPORTED_ENTITY, "Gender"),
    SYSTEM_ENTITY_IMPORTED_REJECTION_REASON(SYSTEM_IMPORTED_ENTITY, "Rejection Reason"),
    SYSTEM_ENTITY_IMPORTED_STUDY_OPTION(SYSTEM_IMPORTED_ENTITY, "Mode of Engagement"),
    SYSTEM_ENTITY_IMPORTED_OPPORTUNITY_TYPE(SYSTEM_IMPORTED_ENTITY, "Opportunity Type"),
    SYSTEM_ENTITY_IMPORTED_INSTITUTION(SYSTEM_IMPORTED_ENTITY, "Organization"),

    /*
     * *************** SYSTEM ADVERT INDUSTRY *********************
     */

    SYSTEM_ADVERT_INDUSTRY_ACCOUNTING(SYSTEM_ADVERT_INDUSTRY, "Accounting"),
    SYSTEM_ADVERT_INDUSTRY_AIRLINES_AVIATION(SYSTEM_ADVERT_INDUSTRY, "Airlines/Aviation"),
    SYSTEM_ADVERT_INDUSTRY_ALTERNATIVE_DISPUTE_RESOLUTION(SYSTEM_ADVERT_INDUSTRY, "Alternative Dispute Resolution"),
    SYSTEM_ADVERT_INDUSTRY_ALTERNATIVE_MEDICINE(SYSTEM_ADVERT_INDUSTRY, "Alternative Medicine"),
    SYSTEM_ADVERT_INDUSTRY_ANIMATION(SYSTEM_ADVERT_INDUSTRY, "Animation"),
    SYSTEM_ADVERT_INDUSTRY_APPAREL_FASHION(SYSTEM_ADVERT_INDUSTRY, "Apparel & Fashion"),
    SYSTEM_ADVERT_INDUSTRY_ARCHITECTURE_PLANNING(SYSTEM_ADVERT_INDUSTRY, "Architecture & Planning"),
    SYSTEM_ADVERT_INDUSTRY_ARTS_CRAFTS(SYSTEM_ADVERT_INDUSTRY, "Arts and Crafts"),
    SYSTEM_ADVERT_INDUSTRY_AUTOMOTIVE(SYSTEM_ADVERT_INDUSTRY, "Automotive"),
    SYSTEM_ADVERT_INDUSTRY_AVIATION_AEROSPACE(SYSTEM_ADVERT_INDUSTRY, "Aviation & Aerospace"),
    SYSTEM_ADVERT_INDUSTRY_BANKING(SYSTEM_ADVERT_INDUSTRY, "Banking"),
    SYSTEM_ADVERT_INDUSTRY_BIOTECHNOLOGY(SYSTEM_ADVERT_INDUSTRY, "Biotechnology"),
    SYSTEM_ADVERT_INDUSTRY_BROADCAST_MEDIA(SYSTEM_ADVERT_INDUSTRY, "Broadcast Media"),
    SYSTEM_ADVERT_INDUSTRY_BUILDING_MATERIALS(SYSTEM_ADVERT_INDUSTRY, "Building Materials"),
    SYSTEM_ADVERT_INDUSTRY_BUSINESS_SUPPLIES_EQUIPMENT(SYSTEM_ADVERT_INDUSTRY, "Business Supplies and Equipment"),
    SYSTEM_ADVERT_INDUSTRY_CAPITAL_MARKETS(SYSTEM_ADVERT_INDUSTRY, "Capital Markets"),
    SYSTEM_ADVERT_INDUSTRY_CHEMICALS(SYSTEM_ADVERT_INDUSTRY, "Chemicals"),
    SYSTEM_ADVERT_INDUSTRY_CIVIC_SOCIAL_ORGANIZATION(SYSTEM_ADVERT_INDUSTRY, "Civic & Social Organization"),
    SYSTEM_ADVERT_INDUSTRY_CIVIL_ENGINEERING(SYSTEM_ADVERT_INDUSTRY, "Civil Engineering"),
    SYSTEM_ADVERT_INDUSTRY_COMMERCIAL_REAL_ESTATE(SYSTEM_ADVERT_INDUSTRY, "Commercial Real Estate"),
    SYSTEM_ADVERT_INDUSTRY_COMPUTER_NETWORK_SECURITY(SYSTEM_ADVERT_INDUSTRY, "Computer & Network Security"),
    SYSTEM_ADVERT_INDUSTRY_COMPUTER_GAMES(SYSTEM_ADVERT_INDUSTRY, "Computer Games"),
    SYSTEM_ADVERT_INDUSTRY_COMPUTER_HARDWARE(SYSTEM_ADVERT_INDUSTRY, "Computer Hardware"),
    SYSTEM_ADVERT_INDUSTRY_COMPUTER_NETWORKING(SYSTEM_ADVERT_INDUSTRY, "Computer Networking"),
    SYSTEM_ADVERT_INDUSTRY_COMPUTER_SOFTWARE(SYSTEM_ADVERT_INDUSTRY, "Computer Software"),
    SYSTEM_ADVERT_INDUSTRY_CONSTRUCTION(SYSTEM_ADVERT_INDUSTRY, "Construction"),
    SYSTEM_ADVERT_INDUSTRY_CONSUMER_ELECTRONICS(SYSTEM_ADVERT_INDUSTRY, "Consumer Electronics"),
    SYSTEM_ADVERT_INDUSTRY_CONSUMER_GOODS(SYSTEM_ADVERT_INDUSTRY, "Consumer Goods"),
    SYSTEM_ADVERT_INDUSTRY_CONSUMER_SERVICES(SYSTEM_ADVERT_INDUSTRY, "Consumer Services"),
    SYSTEM_ADVERT_INDUSTRY_COSMETICS(SYSTEM_ADVERT_INDUSTRY, "Cosmetics"),
    SYSTEM_ADVERT_INDUSTRY_DAIRY(SYSTEM_ADVERT_INDUSTRY, "Dairy"),
    SYSTEM_ADVERT_INDUSTRY_DEFENSE_SPACE(SYSTEM_ADVERT_INDUSTRY, "Defence & Space"),
    SYSTEM_ADVERT_INDUSTRY_DESIGN(SYSTEM_ADVERT_INDUSTRY, "Design"),
    SYSTEM_ADVERT_INDUSTRY_EDUCATION_MANAGEMENT(SYSTEM_ADVERT_INDUSTRY, "Education Management"),
    SYSTEM_ADVERT_INDUSTRY_E_LEARNING(SYSTEM_ADVERT_INDUSTRY, "E-Learning"),
    SYSTEM_ADVERT_INDUSTRY_ELECTRICAL_ELECTRONIC_MANUFACTURING(SYSTEM_ADVERT_INDUSTRY, "Electrical/Electronic Manufacturing"),
    SYSTEM_ADVERT_INDUSTRY_ENTERTAINMENT(SYSTEM_ADVERT_INDUSTRY, "Entertainment"),
    SYSTEM_ADVERT_INDUSTRY_ENVIRONMENTAL_SERVICES(SYSTEM_ADVERT_INDUSTRY, "Environmental Services"),
    SYSTEM_ADVERT_INDUSTRY_EVENTS_SERVICES(SYSTEM_ADVERT_INDUSTRY, "Events Services"),
    SYSTEM_ADVERT_INDUSTRY_EXECUTIVE_OFFICE(SYSTEM_ADVERT_INDUSTRY, "Executive Office"),
    SYSTEM_ADVERT_INDUSTRY_FACILITIES_SERVICES(SYSTEM_ADVERT_INDUSTRY, "Facilities Services"),
    SYSTEM_ADVERT_INDUSTRY_FARMING(SYSTEM_ADVERT_INDUSTRY, "Farming"),
    SYSTEM_ADVERT_INDUSTRY_FINANCIAL_SERVICES(SYSTEM_ADVERT_INDUSTRY, "Financial Services"),
    SYSTEM_ADVERT_INDUSTRY_FINE_ART(SYSTEM_ADVERT_INDUSTRY, "Fine Art"),
    SYSTEM_ADVERT_INDUSTRY_FISHERY(SYSTEM_ADVERT_INDUSTRY, "Fishery"),
    SYSTEM_ADVERT_INDUSTRY_FOOD_BEVERAGES(SYSTEM_ADVERT_INDUSTRY, "Food & Beverages"),
    SYSTEM_ADVERT_INDUSTRY_FOOD_PRODUCTION(SYSTEM_ADVERT_INDUSTRY, "Food Production"),
    SYSTEM_ADVERT_INDUSTRY_FUND_RAISING(SYSTEM_ADVERT_INDUSTRY, "Fund-Raising"),
    SYSTEM_ADVERT_INDUSTRY_FURNITURE(SYSTEM_ADVERT_INDUSTRY, "Furniture"),
    SYSTEM_ADVERT_INDUSTRY_GAMBLING_CASINOS(SYSTEM_ADVERT_INDUSTRY, "Gambling & Casinos"),
    SYSTEM_ADVERT_INDUSTRY_GLASS_CERAMICS_CONCRETE(SYSTEM_ADVERT_INDUSTRY, "Glass, Ceramics & Concrete"),
    SYSTEM_ADVERT_INDUSTRY_GOVERNMENT_ADMINISTRATION(SYSTEM_ADVERT_INDUSTRY, "Government Administration"),
    SYSTEM_ADVERT_INDUSTRY_GOVERNMENT_RELATIONS(SYSTEM_ADVERT_INDUSTRY, "Government Relations"),
    SYSTEM_ADVERT_INDUSTRY_GRAPHIC_DESIGN(SYSTEM_ADVERT_INDUSTRY, "Graphic Design"),
    SYSTEM_ADVERT_INDUSTRY_HEALTH_WELLNESS_FITNESS(SYSTEM_ADVERT_INDUSTRY, "Health, Wellness and Fitness"),
    SYSTEM_ADVERT_INDUSTRY_HIGHER_EDUCATION(SYSTEM_ADVERT_INDUSTRY, "Higher Education"),
    SYSTEM_ADVERT_INDUSTRY_HOSPITAL_HEALTH_CARE(SYSTEM_ADVERT_INDUSTRY, "Hospital & Health Care"),
    SYSTEM_ADVERT_INDUSTRY_HOSPITALITY(SYSTEM_ADVERT_INDUSTRY, "Hospitality"),
    SYSTEM_ADVERT_INDUSTRY_HUMAN_RESOURCES(SYSTEM_ADVERT_INDUSTRY, "Human Resources"),
    SYSTEM_ADVERT_INDUSTRY_IMPORT_EXPORT(SYSTEM_ADVERT_INDUSTRY, "Import and Export"),
    SYSTEM_ADVERT_INDUSTRY_INDIVIDUAL_FAMILY_SERVICES(SYSTEM_ADVERT_INDUSTRY, "Individual & Family Services"),
    SYSTEM_ADVERT_INDUSTRY_INDUSTRIAL_AUTOMATION(SYSTEM_ADVERT_INDUSTRY, "Industrial Automation"),
    SYSTEM_ADVERT_INDUSTRY_INFORMATION_SERVICES(SYSTEM_ADVERT_INDUSTRY, "Information Services"),
    SYSTEM_ADVERT_INDUSTRY_INFORMATION_TECHNOLOGY_SERVICES(SYSTEM_ADVERT_INDUSTRY, "Information Technology and Services"),
    SYSTEM_ADVERT_INDUSTRY_INSURANCE(SYSTEM_ADVERT_INDUSTRY, "Insurance"),
    SYSTEM_ADVERT_INDUSTRY_INTERNATIONAL_AFFAIRS(SYSTEM_ADVERT_INDUSTRY, "International Affairs"),
    SYSTEM_ADVERT_INDUSTRY_INTERNATIONAL_TRADE_DEVELOPMENT(SYSTEM_ADVERT_INDUSTRY, "International Trade and Development"),
    SYSTEM_ADVERT_INDUSTRY_INTERNET(SYSTEM_ADVERT_INDUSTRY, "Internet"),
    SYSTEM_ADVERT_INDUSTRY_INVESTMENT_BANKING(SYSTEM_ADVERT_INDUSTRY, "Investment Banking"),
    SYSTEM_ADVERT_INDUSTRY_INVESTMENT_MANAGEMENT(SYSTEM_ADVERT_INDUSTRY, "Investment Management"),
    SYSTEM_ADVERT_INDUSTRY_JUDICIARY(SYSTEM_ADVERT_INDUSTRY, "Judiciary"),
    SYSTEM_ADVERT_INDUSTRY_LAW_ENFORCEMENT(SYSTEM_ADVERT_INDUSTRY, "Law Enforcement"),
    SYSTEM_ADVERT_INDUSTRY_LAW_PRACTICE(SYSTEM_ADVERT_INDUSTRY, "Law Practice"),
    SYSTEM_ADVERT_INDUSTRY_LEGAL_SERVICES(SYSTEM_ADVERT_INDUSTRY, "Legal Services"),
    SYSTEM_ADVERT_INDUSTRY_LEGISLATIVE_OFFICE(SYSTEM_ADVERT_INDUSTRY, "Legislative Office"),
    SYSTEM_ADVERT_INDUSTRY_LEISURE_TRAVEL_TOURISM(SYSTEM_ADVERT_INDUSTRY, "Leisure, Travel & Tourism"),
    SYSTEM_ADVERT_INDUSTRY_LIBRARIES(SYSTEM_ADVERT_INDUSTRY, "Libraries"),
    SYSTEM_ADVERT_INDUSTRY_LOGISTICS_SUPPLY_CHAIN(SYSTEM_ADVERT_INDUSTRY, "Logistics and Supply Chain"),
    SYSTEM_ADVERT_INDUSTRY_LUXURY_GOODS_JEWELRY(SYSTEM_ADVERT_INDUSTRY, "Luxury Goods & Jewellery"),
    SYSTEM_ADVERT_INDUSTRY_MACHINERY(SYSTEM_ADVERT_INDUSTRY, "Machinery"),
    SYSTEM_ADVERT_INDUSTRY_MANAGEMENT_CONSULTING(SYSTEM_ADVERT_INDUSTRY, "Management Consulting"),
    SYSTEM_ADVERT_INDUSTRY_MARITIME(SYSTEM_ADVERT_INDUSTRY, "Maritime"),
    SYSTEM_ADVERT_INDUSTRY_MARKET_RESEARCH(SYSTEM_ADVERT_INDUSTRY, "Market Research"),
    SYSTEM_ADVERT_INDUSTRY_MARKETING_ADVERTISING(SYSTEM_ADVERT_INDUSTRY, "Marketing and Advertising"),
    SYSTEM_ADVERT_INDUSTRY_MECHANICAL_OR_INDUSTRIAL_ENGINEERING(SYSTEM_ADVERT_INDUSTRY, "Mechanical or Industrial Engineering"),
    SYSTEM_ADVERT_INDUSTRY_MEDIA_PRODUCTION(SYSTEM_ADVERT_INDUSTRY, "Media Production"),
    SYSTEM_ADVERT_INDUSTRY_MEDICAL_DEVICES(SYSTEM_ADVERT_INDUSTRY, "Medical Devices"),
    SYSTEM_ADVERT_INDUSTRY_MEDICAL_PRACTICE(SYSTEM_ADVERT_INDUSTRY, "Medical Practice"),
    SYSTEM_ADVERT_INDUSTRY_MENTAL_HEALTH_CARE(SYSTEM_ADVERT_INDUSTRY, "Mental Health Care"),
    SYSTEM_ADVERT_INDUSTRY_MILITARY(SYSTEM_ADVERT_INDUSTRY, "Military"),
    SYSTEM_ADVERT_INDUSTRY_MINING_METALS(SYSTEM_ADVERT_INDUSTRY, "Mining & Metals"),
    SYSTEM_ADVERT_INDUSTRY_MOTION_PICTURES_FILM(SYSTEM_ADVERT_INDUSTRY, "Motion Pictures and Film"),
    SYSTEM_ADVERT_INDUSTRY_MUSEUMS_INSTITUTIONS(SYSTEM_ADVERT_INDUSTRY, "Museums and Organizations"),
    SYSTEM_ADVERT_INDUSTRY_MUSIC(SYSTEM_ADVERT_INDUSTRY, "Music"),
    SYSTEM_ADVERT_INDUSTRY_NANOTECHNOLOGY(SYSTEM_ADVERT_INDUSTRY, "Nanotechnology"),
    SYSTEM_ADVERT_INDUSTRY_NEWSPAPERS(SYSTEM_ADVERT_INDUSTRY, "Newspapers"),
    SYSTEM_ADVERT_INDUSTRY_NON_PROFIT_ORGANIZATION_MANAGEMENT(SYSTEM_ADVERT_INDUSTRY, "Non-Profit Organization Management"),
    SYSTEM_ADVERT_INDUSTRY_OIL_ENERGY(SYSTEM_ADVERT_INDUSTRY, "Oil & Energy"),
    SYSTEM_ADVERT_INDUSTRY_ONLINE_MEDIA(SYSTEM_ADVERT_INDUSTRY, "Online Media"),
    SYSTEM_ADVERT_INDUSTRY_OUTSOURCING_OFFSHORING(SYSTEM_ADVERT_INDUSTRY, "Outsourcing/Offshoring"),
    SYSTEM_ADVERT_INDUSTRY_PACKAGE_FREIGHT_DELIVERY(SYSTEM_ADVERT_INDUSTRY, "Package/Freight Delivery"),
    SYSTEM_ADVERT_INDUSTRY_PACKAGING_CONTAINERS(SYSTEM_ADVERT_INDUSTRY, "Packaging and Containers"),
    SYSTEM_ADVERT_INDUSTRY_PAPER_FOREST_PRODUCTS(SYSTEM_ADVERT_INDUSTRY, "Paper & Forest Products"),
    SYSTEM_ADVERT_INDUSTRY_PERFORMING_ARTS(SYSTEM_ADVERT_INDUSTRY, "Performing Arts"),
    SYSTEM_ADVERT_INDUSTRY_PHARMACEUTICALS(SYSTEM_ADVERT_INDUSTRY, "Pharmaceuticals"),
    SYSTEM_ADVERT_INDUSTRY_PHILANTHROPY(SYSTEM_ADVERT_INDUSTRY, "Philanthropy"),
    SYSTEM_ADVERT_INDUSTRY_PHOTOGRAPHY(SYSTEM_ADVERT_INDUSTRY, "Photography"),
    SYSTEM_ADVERT_INDUSTRY_PLASTICS(SYSTEM_ADVERT_INDUSTRY, "Plastics"),
    SYSTEM_ADVERT_INDUSTRY_POLITICAL_ORGANIZATION(SYSTEM_ADVERT_INDUSTRY, "Political Organization"),
    SYSTEM_ADVERT_INDUSTRY_PRIMARY_SECONDARY_EDUCATION(SYSTEM_ADVERT_INDUSTRY, "Primary/Secondary Education"),
    SYSTEM_ADVERT_INDUSTRY_PRINTING(SYSTEM_ADVERT_INDUSTRY, "Printing"),
    SYSTEM_ADVERT_INDUSTRY_PROFESSIONAL_TRAINING_COACHING(SYSTEM_ADVERT_INDUSTRY, "Professional Training & Coaching"),
    SYSTEM_ADVERT_INDUSTRY_PROGRAM_DEVELOPMENT(SYSTEM_ADVERT_INDUSTRY, "Program Development"),
    SYSTEM_ADVERT_INDUSTRY_PUBLIC_POLICY(SYSTEM_ADVERT_INDUSTRY, "Public Policy"),
    SYSTEM_ADVERT_INDUSTRY_PUBLIC_RELATIONS_COMMUNICATIONS(SYSTEM_ADVERT_INDUSTRY, "Public Relations and Communications"),
    SYSTEM_ADVERT_INDUSTRY_PUBLIC_SAFETY(SYSTEM_ADVERT_INDUSTRY, "Public Safety"),
    SYSTEM_ADVERT_INDUSTRY_PUBLISHING(SYSTEM_ADVERT_INDUSTRY, "Publishing"),
    SYSTEM_ADVERT_INDUSTRY_RAILROAD_MANUFACTURE(SYSTEM_ADVERT_INDUSTRY, "Railway Manufacture"),
    SYSTEM_ADVERT_INDUSTRY_RANCHING(SYSTEM_ADVERT_INDUSTRY, "Ranching"),
    SYSTEM_ADVERT_INDUSTRY_REAL_ESTATE(SYSTEM_ADVERT_INDUSTRY, "Real Estate"),
    SYSTEM_ADVERT_INDUSTRY_RECREATIONAL_FACILITIES_SERVICES(SYSTEM_ADVERT_INDUSTRY, "Recreational Facilities and Services"),
    SYSTEM_ADVERT_INDUSTRY_RELIGIOUS_INSTITUTIONS(SYSTEM_ADVERT_INDUSTRY, "Religious Organizations"),
    SYSTEM_ADVERT_INDUSTRY_RENEWABLES_ENVIRONMENT(SYSTEM_ADVERT_INDUSTRY, "Renewables & Environment"),
    SYSTEM_ADVERT_INDUSTRY_RESEARCH(SYSTEM_ADVERT_INDUSTRY, "Research"),
    SYSTEM_ADVERT_INDUSTRY_RESTAURANTS(SYSTEM_ADVERT_INDUSTRY, "Restaurants"),
    SYSTEM_ADVERT_INDUSTRY_RETAIL(SYSTEM_ADVERT_INDUSTRY, "Retail"),
    SYSTEM_ADVERT_INDUSTRY_SECURITY_INVESTIGATIONS(SYSTEM_ADVERT_INDUSTRY, "Security and Investigations"),
    SYSTEM_ADVERT_INDUSTRY_SEMICONDUCTORS(SYSTEM_ADVERT_INDUSTRY, "Semiconductors"),
    SYSTEM_ADVERT_INDUSTRY_SHIPBUILDING(SYSTEM_ADVERT_INDUSTRY, "Shipbuilding"),
    SYSTEM_ADVERT_INDUSTRY_SPORTING_GOODS(SYSTEM_ADVERT_INDUSTRY, "Sporting Goods"),
    SYSTEM_ADVERT_INDUSTRY_SPORTS(SYSTEM_ADVERT_INDUSTRY, "Sports"),
    SYSTEM_ADVERT_INDUSTRY_STAFFING_RECRUITING(SYSTEM_ADVERT_INDUSTRY, "Staffing and Recruiting"),
    SYSTEM_ADVERT_INDUSTRY_SUPERMARKETS(SYSTEM_ADVERT_INDUSTRY, "Supermarkets"),
    SYSTEM_ADVERT_INDUSTRY_TELECOMMUNICATIONS(SYSTEM_ADVERT_INDUSTRY, "Telecommunications"),
    SYSTEM_ADVERT_INDUSTRY_TEXTILES(SYSTEM_ADVERT_INDUSTRY, "Textiles"),
    SYSTEM_ADVERT_INDUSTRY_THINK_TANKS(SYSTEM_ADVERT_INDUSTRY, "Think Tanks"),
    SYSTEM_ADVERT_INDUSTRY_TOBACCO(SYSTEM_ADVERT_INDUSTRY, "Tobacco"),
    SYSTEM_ADVERT_INDUSTRY_TRANSLATION_LOCALIZATION(SYSTEM_ADVERT_INDUSTRY, "Translation and Localization"),
    SYSTEM_ADVERT_INDUSTRY_TRANSPORTATION_TRUCKING_RAILROAD(SYSTEM_ADVERT_INDUSTRY, "Transportation/Trucking/Railway"),
    SYSTEM_ADVERT_INDUSTRY_UTILITIES(SYSTEM_ADVERT_INDUSTRY, "Utilities"),
    SYSTEM_ADVERT_INDUSTRY_VENTURE_CAPITAL_PRIVATE_EQUITY(SYSTEM_ADVERT_INDUSTRY, "Venture Capital & Private Equity"),
    SYSTEM_ADVERT_INDUSTRY_VETERINARY(SYSTEM_ADVERT_INDUSTRY, "Veterinary"),
    SYSTEM_ADVERT_INDUSTRY_WAREHOUSING(SYSTEM_ADVERT_INDUSTRY, "Warehousing"),
    SYSTEM_ADVERT_INDUSTRY_WHOLESALE(SYSTEM_ADVERT_INDUSTRY, "Wholesale"),
    SYSTEM_ADVERT_INDUSTRY_WINE_SPIRITS(SYSTEM_ADVERT_INDUSTRY, "Wine and Spirits"),
    SYSTEM_ADVERT_INDUSTRY_WIRELESS(SYSTEM_ADVERT_INDUSTRY, "Wireless"),
    SYSTEM_ADVERT_INDUSTRY_WRITING_EDITING(SYSTEM_ADVERT_INDUSTRY, "Writing and Editing"),

    /*
     * *************** SYSTEM ADVERT FUNCTION *********************
     */

    SYSTEM_ADVERT_FUNCTION_ACCOUNTING_AUDITING(SYSTEM_ADVERT_FUNCTION, "Accounting/Auditing"),
    SYSTEM_ADVERT_FUNCTION_ADMINISTRATIVE(SYSTEM_ADVERT_FUNCTION, "Administrative"),
    SYSTEM_ADVERT_FUNCTION_ADVERTISING(SYSTEM_ADVERT_FUNCTION, "Advertising"),
    SYSTEM_ADVERT_FUNCTION_ANALYST(SYSTEM_ADVERT_FUNCTION, "Analyst"),
    SYSTEM_ADVERT_FUNCTION_ART_CREATIVE(SYSTEM_ADVERT_FUNCTION, "Art/Creative"),
    SYSTEM_ADVERT_FUNCTION_BUSINESS_DEVELOPMENT(SYSTEM_ADVERT_FUNCTION, "Business Development"),
    SYSTEM_ADVERT_FUNCTION_CONSULTING(SYSTEM_ADVERT_FUNCTION, "Consulting"),
    SYSTEM_ADVERT_FUNCTION_CUSTOMER_SERVICE(SYSTEM_ADVERT_FUNCTION, "Customer Service"),
    SYSTEM_ADVERT_FUNCTION_DISTRIBUTION(SYSTEM_ADVERT_FUNCTION, "Distribution"),
    SYSTEM_ADVERT_FUNCTION_DESIGN(SYSTEM_ADVERT_FUNCTION, "Design"),
    SYSTEM_ADVERT_FUNCTION_EDUCATION(SYSTEM_ADVERT_FUNCTION, "Education"),
    SYSTEM_ADVERT_FUNCTION_ENGINEERING(SYSTEM_ADVERT_FUNCTION, "Engineering"),
    SYSTEM_ADVERT_FUNCTION_FINANCE(SYSTEM_ADVERT_FUNCTION, "Finance"),
    SYSTEM_ADVERT_FUNCTION_GENERAL_BUSINESS(SYSTEM_ADVERT_FUNCTION, "General Business"),
    SYSTEM_ADVERT_FUNCTION_HEALTH_CARE_PROVIDER(SYSTEM_ADVERT_FUNCTION, "Health Care Provider"),
    SYSTEM_ADVERT_FUNCTION_HUMAN_RESOURCES(SYSTEM_ADVERT_FUNCTION, "Human Resources"),
    SYSTEM_ADVERT_FUNCTION_INFORMATION_TECHNOLOGY(SYSTEM_ADVERT_FUNCTION, "Information Technology"),
    SYSTEM_ADVERT_FUNCTION_LEGAL(SYSTEM_ADVERT_FUNCTION, "Legal"),
    SYSTEM_ADVERT_FUNCTION_MANAGEMENT(SYSTEM_ADVERT_FUNCTION, "Management"),
    SYSTEM_ADVERT_FUNCTION_MANUFACTURING(SYSTEM_ADVERT_FUNCTION, "Manufacturing"),
    SYSTEM_ADVERT_FUNCTION_MARKETING(SYSTEM_ADVERT_FUNCTION, "Marketing"),
    SYSTEM_ADVERT_FUNCTION_OTHER(SYSTEM_ADVERT_FUNCTION, "Other"),
    SYSTEM_ADVERT_FUNCTION_PUBLIC_RELATIONS(SYSTEM_ADVERT_FUNCTION, "Public Relations"),
    SYSTEM_ADVERT_FUNCTION_PURCHASING(SYSTEM_ADVERT_FUNCTION, "Purchasing"),
    SYSTEM_ADVERT_FUNCTION_PRODUCT_MANAGEMENT(SYSTEM_ADVERT_FUNCTION, "Product Management"),
    SYSTEM_ADVERT_FUNCTION_PROJECT_MANAGEMENT(SYSTEM_ADVERT_FUNCTION, "Position Management"),
    SYSTEM_ADVERT_FUNCTION_PRODUCTION(SYSTEM_ADVERT_FUNCTION, "Production"),
    SYSTEM_ADVERT_FUNCTION_QUALITY_ASSURANCE(SYSTEM_ADVERT_FUNCTION, "Quality Assurance"),
    SYSTEM_ADVERT_FUNCTION_RESEARCH(SYSTEM_ADVERT_FUNCTION, "Research"),
    SYSTEM_ADVERT_FUNCTION_SALES(SYSTEM_ADVERT_FUNCTION, "Sales"),
    SYSTEM_ADVERT_FUNCTION_SCIENCE(SYSTEM_ADVERT_FUNCTION, "Science"),
    SYSTEM_ADVERT_FUNCTION_STRATEGY_PLANNING(SYSTEM_ADVERT_FUNCTION, "Strategy/Planning"),
    SYSTEM_ADVERT_FUNCTION_SUPPLY_CHAIN(SYSTEM_ADVERT_FUNCTION, "Supply Chain"),
    SYSTEM_ADVERT_FUNCTION_TRAINING(SYSTEM_ADVERT_FUNCTION, "Training"),
    SYSTEM_ADVERT_FUNCTION_WRITING_EDITING(SYSTEM_ADVERT_FUNCTION, "Writing/Editing"),

    /*
     * *************** SYSTEM NOTIFICATION TEMPLATE *********************
     */

    SYSTEM_NOTIFICATION_TEMPLATE_APPLICATION_COMPLETE_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE, "Complete Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE,
            "Confirm Interview Arrangements Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE,
            "Confirm Offer Recommendation Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_APPLICATION_CONFIRM_REJECTION_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE, "Confirm Rejection Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE,
            "Provide Interview Availability Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST(SYSTEM_NOTIFICATION_TEMPLATE, "Provide Interview Availability Request"),
    SYSTEM_NOTIFICATION_TEMPLATE_APPLICATION_PROVIDE_REFERENCE_REQUEST(SYSTEM_NOTIFICATION_TEMPLATE, "Provide Reference Request"),
    SYSTEM_NOTIFICATION_TEMPLATE_APPLICATION_RESERVE_NOTIFICATION(SYSTEM_NOTIFICATION, "Application Reserve Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_APPLICATION_REVERSE_REJECTION_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE, "Rejection Reversed Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_APPLICATION_TERMINATE_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE, "Terminate Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE,
            "Update Interview Availability Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_INSTITUTION_COMPLETE_APPROVAL_STAGE_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE, "Complete Approval Stage Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_INSTITUTION_CORRECT_REQUEST(SYSTEM_NOTIFICATION_TEMPLATE, "Correct Request"),
    SYSTEM_NOTIFICATION_TEMPLATE_DEPARTMENT_COMPLETE_APPROVAL_STAGE_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE, "Complete Approval Stage Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_DEPARTMENT_CORRECT_REQUEST(SYSTEM_NOTIFICATION_TEMPLATE, "Correct Request"),
    SYSTEM_NOTIFICATION_TEMPLATE_PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE, "Complete Approval Stage Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_PROGRAM_CORRECT_REQUEST(SYSTEM_NOTIFICATION_TEMPLATE, "Correct Request"),
    SYSTEM_NOTIFICATION_TEMPLATE_PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE, "Complete Approval Stage Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_PROJECT_CORRECT_REQUEST(SYSTEM_NOTIFICATION_TEMPLATE, "Correct Request"),
    SYSTEM_NOTIFICATION_TEMPLATE_SYSTEM_COMPLETE_REGISTRATION_REQUEST(SYSTEM_NOTIFICATION_TEMPLATE, "Complete Registration Request"),
    SYSTEM_NOTIFICATION_TEMPLATE_SYSTEM_PASSWORD_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE, "Password Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_SYSTEM_ACTIVITY_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE, "Activity Update Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_SYSTEM_INVITATION_NOTIFICATION(SYSTEM_NOTIFICATION_TEMPLATE, "Invitation Notification"),
    SYSTEM_NOTIFICATION_TEMPLATE_NO_RECOMMENDATIONS(SYSTEM_NOTIFICATION_TEMPLATE, "We are sorry to inform that we have no current recommendations"),

    /*
     * *************** SYSTEM REFEREE TYPE *********************
     */

    SYSTEM_REFEREE_TYPE_ACADEMIC(SYSTEM_REFEREE_TYPE, "Academic"),
    SYSTEM_REFEREE_TYPE_EMPLOYER(SYSTEM_REFEREE_TYPE, "Employer"),
    SYSTEM_REFEREE_TYPE_OTHER(SYSTEM_REFEREE_TYPE, "Other"),

    /*
     * *************** SYSTEM RESERVE STATUS *********************
     */

    SYSTEM_RESERVE_STATUS_FIRST(SYSTEM_RESERVE_STATUS, "First Reserve Group"),
    SYSTEM_RESERVE_STATUS_SECOND(SYSTEM_RESERVE_STATUS, "Second Reserve Group"),
    SYSTEM_RESERVE_STATUS_THIRD(SYSTEM_RESERVE_STATUS, "Third Reserve Group"),

    /*
     * *************** SYSTEM DISPLAY PROPERTY CATEGORY *********************
     */

    SYSTEM_DISPLAY_CATEGORY_SYSTEM_GLOBAL(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Global"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_COMMENT(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Comment"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_NOTIFICATION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Notification"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_STATE_GROUP(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "State Group"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_STATE_TRANSITION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Transition"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_ACTION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Action"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_ROLE(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Role"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_OPPORTUNITY_TYPE(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Opportunity Type"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_YES_NO_UNSURE(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Yes/No/Unsure"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_STUDY_OPTION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Mode of Engagement"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_PROGRAM_CATEGORY(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Program Category"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_DURATION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Duration"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_FILTER_PROPERTY(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Filter Property"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_FILTER_EXPRESSION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Filter Expression"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_ADVERT_INDUSTRY(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Advert Industry"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_ADVERT_FUNCTION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Advert Function"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_ADVERT_DOMAIN(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Advert Domain"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_NOTIFICATION_TEMPLATE(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Notification Template"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_REFEREE_TYPE(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Referee Type"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_RESERVE_STATUS(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Reserve Status"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_DISPLAY_PROPERTY_CATEGORY(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Display Property Category"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_VALIDATION_ERROR(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Validation Error"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_INTEGRATION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Integration"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_STATE_DURATION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "State Duration"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_WORKFLOW(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Workflow"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_DECLINE_ACTION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Decline Action"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_HTML_GENERAL_SECTION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "General Section"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_AUTHENTICATE_SECTION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Authenticate Section"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_OPPORTUNITIES_SECTION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Opportunities Section"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_ADVERTISE_SECTION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Advertise Section"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_RESOURCES_SECTION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Resources Section"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_RESOURCE_SECTION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Resource Section"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_ACCOUNT_SECTION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Account Section"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_HTML_FIELDS(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "HTML Fields"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_HTML_COMMON(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "HTML Common"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_HTML_ADDRESS(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Address Fields"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_HTML_RESOURCE_CONFIGURATION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Resource Configuration Section"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_HTML_MANAGE_USERS(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Manage Users Section"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_HTML_EMAIL_TEMPLATE_CONFIGURATION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Email Template Configuration"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_HTML_WORKFLOW_CONFIGURATION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Workflow Configuration"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_HTML_TRANSLATIONS_CONFIGURATION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Customization"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_ERROR_MESSAGES(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Error Messages"),
    SYSTEM_DISPLAY_CATEGORY_INSTITUTION_COMMENT(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Comment"),
    SYSTEM_DISPLAY_CATEGORY_PROGRAM_COMMENT(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Comment"),
    SYSTEM_DISPLAY_CATEGORY_PROGRAM_FORM(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Program Form"),
    SYSTEM_DISPLAY_CATEGORY_PROGRAM_ADVERT_DETAILS(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Advert Details Section"),
    SYSTEM_DISPLAY_CATEGORY_PROGRAM_ADVERT_FEES_AND_PAYMENTS(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Fees and Payments Section"),
    SYSTEM_DISPLAY_CATEGORY_PROGRAM_ADVERT_CATEGORIES(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Advert Categories Section"),
    SYSTEM_DISPLAY_CATEGORY_SYSTEM_RESOURCE_ADVERT(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Closing Dates Section"),
    SYSTEM_DISPLAY_CATEGORY_PROJECT_COMMENT(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Comment"),
    SYSTEM_DISPLAY_CATEGORY_APPLICATION_GLOBAL(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Global"),
    SYSTEM_DISPLAY_CATEGORY_APPLICATION_PROGRAM_DETAIL(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Application Detail Section"),
    SYSTEM_DISPLAY_CATEGORY_APPLICATION_PERSONAL_DETAIL(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Personal Detail Section"),
    SYSTEM_DISPLAY_CATEGORY_APPLICATION_ADDRESS(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Address Section"),
    SYSTEM_DISPLAY_CATEGORY_APPLICATION_QUALIFICATION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Qualification Section"),
    SYSTEM_DISPLAY_CATEGORY_APPLICATION_EMPLOYMENT_POSITION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Employment Position Section"),
    SYSTEM_DISPLAY_CATEGORY_APPLICATION_REFEREE(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Referee Section"),
    SYSTEM_DISPLAY_CATEGORY_APPLICATION_DOCUMENT(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Document Section"),
    SYSTEM_DISPLAY_CATEGORY_APPLICATION_ADDITIONAL_INFORMATION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Additional Information Section"),
    SYSTEM_DISPLAY_CATEGORY_APPLICATION_FORM(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Application Form"),
    SYSTEM_DISPLAY_CATEGORY_APPLICATION_ACTION(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Action Section"),
    SYSTEM_DISPLAY_CATEGORY_APPLICATION_COMMENT(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Comment"),
    SYSTEM_DISPLAY_CATEGORY_APPLICATION_REPORT(SYSTEM_DISPLAY_PROPERTY_CATEGORY, "Report"),

    /*
     * *************** SYSTEM REPORT INDICATOR GROUP *********************
     */

    SYSTEM_REPORT_INDICATOR_ADVERT_COUNT(SYSTEM_REPORT_INDICATOR_GROUP, "Advert Count"),
    SYSTEM_REPORT_INDICATOR_ADVERT_COUNT_CUMULATIVE(SYSTEM_REPORT_INDICATOR_GROUP, "Advert Count (Cumulative)"),
    SYSTEM_REPORT_INDICATOR_APPLICATION_COUNT(SYSTEM_REPORT_INDICATOR_GROUP, "Application Count"),
    SYSTEM_REPORT_INDICATOR_APPLICATION_COUNT_CUMULATIVE(SYSTEM_REPORT_INDICATOR_GROUP, "Application Count (Cumulative)"),
    SYSTEM_REPORT_INDICATOR_APPLICATION_RATIO(SYSTEM_REPORT_INDICATOR_GROUP, "Application Ratio"),
    SYSTEM_REPORT_INDICATOR_AVERAGE_RATING(SYSTEM_REPORT_INDICATOR_GROUP, "Average Rating"),
    SYSTEM_REPORT_INDICATOR_AVERAGE_PROCESSING_TIME(SYSTEM_REPORT_INDICATOR_GROUP, "Processing Time"),

    /*
     * *************** SYSTEM VALIDATION ERROR *********************
     */

    SYSTEM_VALIDATION_UNKNOWN(SYSTEM_VALIDATION_ERROR, "This field is invalid."),
    SYSTEM_VALIDATION_REQUIRED(SYSTEM_VALIDATION_ERROR, "This field is required."),
    SYSTEM_VALIDATION_EMAIL(SYSTEM_VALIDATION_ERROR, "This field must be a valid email address."),
    SYSTEM_VALIDATION_NUMBER(SYSTEM_VALIDATION_ERROR, "This field must be a number."),
    SYSTEM_VALIDATION_MIN(SYSTEM_VALIDATION_ERROR, "This field must be at least {1}."),
    SYSTEM_VALIDATION_MAX(SYSTEM_VALIDATION_ERROR, "This field must be at most {1}."),
    SYSTEM_VALIDATION_MINLENGTH(SYSTEM_VALIDATION_ERROR, "This field must be at least {1} character(s)."),
    SYSTEM_VALIDATION_MAXLENGTH(SYSTEM_VALIDATION_ERROR, "This field must be less than {1} character(s)."),
    SYSTEM_VALIDATION_ARRAY_MINLENGTH(SYSTEM_VALIDATION_ERROR, "You have to select at least {1} element(s)."),
    SYSTEM_VALIDATION_ARRAY_MAXLENGTH(SYSTEM_VALIDATION_ERROR, "You have to select at most {1} element(s)."),
    SYSTEM_VALIDATION_PATTERN(SYSTEM_VALIDATION_ERROR, "This field is invalid."),
    SYSTEM_VALIDATION_URL(SYSTEM_VALIDATION_ERROR, "This field must be a valid URL."),
    SYSTEM_VALIDATION_TA_MAX_TEXT(SYSTEM_VALIDATION_ERROR, "This field is too long."),
    SYSTEM_VALIDATION_TIMESLOTS_REQUIRED(SYSTEM_VALIDATION_ERROR, "You have to specify at least one date and time."),
    SYSTEM_VALIDATION_SELECTION(SYSTEM_VALIDATION_ERROR, "You have to make a selection."),
    SYSTEM_VALIDATION_MIN_ASSIGNED_USERS(SYSTEM_VALIDATION_ERROR, "You have to assign more users."),
    SYSTEM_VALIDATION_MAX_ASSIGNED_USERS(SYSTEM_VALIDATION_ERROR, "Too many users selected."),

    SYSTEM_VALIDATION_BAD_CREDENTIALS(SYSTEM_VALIDATION_ERROR, "Invalid Username or Password."),
    SYSTEM_VALIDATION_INVALID_PASSWORD(SYSTEM_VALIDATION_ERROR, "Invalid Password."),
    SYSTEM_VALIDATION_ACCOUNT_NOT_ACTIVATED(SYSTEM_VALIDATION_ERROR, "Account is not activated."),
    SYSTEM_VALIDATION_EMAIL_ALREADY_IN_USE(SYSTEM_VALIDATION_ERROR, "Given email is already in use."),
    SYSTEM_VALIDATION_USER_ALREADY_LINKED(SYSTEM_VALIDATION_ERROR, "User is already linked."),

    SYSTEM_VALIDATION_SECTION_NOT_COMPLETED(SYSTEM_VALIDATION_ERROR, "This section needs to be completed."),
    SYSTEM_VALIDATION_SECTION_ITEM_NOT_COMPLETED(SYSTEM_VALIDATION_ERROR, "At least one of the items is not completed."),
    SYSTEM_VALIDATION_SECTION_MIN_ITEMS(SYSTEM_VALIDATION_ERROR, "Min items: {{min}}."),
    SYSTEM_VALIDATION_SECTION_MAX_ITEMS(SYSTEM_VALIDATION_ERROR, "Max items: {{max}}."),

    SYSTEM_DUPLICATE_INSTITUTION(SYSTEM_VALIDATION_ERROR, "Organization already exists."),
    SYSTEM_DUPLICATE_DEPARTMENT(SYSTEM_VALIDATION_ERROR, "Department already exists."),
    SYSTEM_DUPLICATE_PROGRAM(SYSTEM_VALIDATION_ERROR, "Program already exists."),
    SYSTEM_DUPLICATE_PROJECT(SYSTEM_VALIDATION_ERROR, "Position already exists."),

    /*
     * *************** SYSTEM INTEGRATION *********************
     */

    SYSTEM_ADDRESS_LINE_MOCK(SYSTEM_INTEGRATION, "Address Line"),
    SYSTEM_ADDRESS_CODE_MOCK(SYSTEM_INTEGRATION, "Address Code"),
    SYSTEM_PHONE_MOCK(SYSTEM_INTEGRATION, "0000000000"),

    /*
     * *************** SYSTEM STATE DURATION *********************
     */

    SYSTEM_APPLICATION_CONFIRM_ELIGIBILITY_DURATION_LABEL(SYSTEM_STATE_DURATION, "Eligibility Confirmation Duration"),
    SYSTEM_APPLICATION_CONFIRM_ELIGIBILITY_DURATION_HINT(SYSTEM_STATE_DURATION,
            "The length of time you expect it to take to confirm the eligibility of an applicant"),
    SYSTEM_APPLICATION_PROVIDE_REFERENCE_DURATION_LABEL(SYSTEM_STATE_DURATION, "Reference Duration"),
    SYSTEM_APPLICATION_PROVIDE_REFERENCE_DURATION_HINT(SYSTEM_STATE_DURATION, "The length of time you expect it to take to collect applicant references"),
    SYSTEM_APPLICATION_PROVIDE_REVIEW_DURATION_LABEL(SYSTEM_STATE_DURATION, "Review Duration"),
    SYSTEM_APPLICATION_PROVIDE_REVIEW_DURATION_HINT(SYSTEM_STATE_DURATION, "The length of time you expect it to take to collect applicant reviews"),
    SYSTEM_APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION_LABEL(SYSTEM_STATE_DURATION, "Interview Scheduling Duration"),
    SYSTEM_APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION_HINT(SYSTEM_STATE_DURATION, "The length of time you expect it to take to schedule an interview"),
    SYSTEM_APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION_LABEL(SYSTEM_STATE_DURATION, "Interview Feedback Duration"),
    SYSTEM_APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION_HINT(SYSTEM_STATE_DURATION,
            "The length of time you expect it to take to collect feedback on an interview"),
    SYSTEM_APPLICATION_CONFIRM_APPOINTMENT_DURATION_LABEL(SYSTEM_STATE_DURATION, "Appointment Confirmation Duration"),
    SYSTEM_APPLICATION_CONFIRM_APPOINTMENT_DURATION_HINT(SYSTEM_STATE_DURATION,
            "The length of time you expect it to take for a hiring manager to provide confirmation of appointment"),
    SYSTEM_APPLICATION_ESCALATE_DURATION_LABEL(SYSTEM_STATE_DURATION, "Escalation Duration"),
    SYSTEM_APPLICATION_ESCALATE_DURATION_HINT(SYSTEM_STATE_DURATION,
            "The length of time you wish to allow an application that is being processed to remain dormant, before it is automatically rejected or withdrawn"),
    SYSTEM_APPLICATION_RESERVE_DURATION_LABEL(SYSTEM_STATE_DURATION, "Reserve Duration"),
    SYSTEM_APPLICATION_RESERVE_DURATION_HINT(SYSTEM_STATE_DURATION, "The length of time you expect an application to be held in reserve for"),
    SYSTEM_APPLICATION_PURGE_DURATION_LABEL(SYSTEM_STATE_DURATION, "Expiry Duration"),
    SYSTEM_APPLICATION_PURGE_DURATION_HINT(SYSTEM_STATE_DURATION,
            "The length of time you wish to keep information about a rejected or withdrawn application on record"),
    SYSTEM_PROJECT_ESCALATE_DURATION_LABEL(SYSTEM_STATE_DURATION, "Escalation Duration"),
    SYSTEM_PROJECT_ESCALATE_DURATION_HINT(SYSTEM_STATE_DURATION,
            "The length of time you wish to allow a new position request that is being processed to remain dormant, before it is automatically rejected or withdrawn"),
    SYSTEM_PROGRAM_ESCALATE_DURATION_LABEL(SYSTEM_STATE_DURATION, "Escalation Duration"),
    SYSTEM_PROGRAM_ESCALATE_DURATION_HINT(SYSTEM_STATE_DURATION,
            "The length of time you wish to allow a new program request that is being processed to remain dormant, before it is automatically rejected or withdrawn"),
    SYSTEM_INSTITUTION_ESCALATE_DURATION_LABEL(SYSTEM_STATE_DURATION, "Escalation Duration"),
    SYSTEM_INSTITUTION_ESCALATE_DURATION_HINT(SYSTEM_STATE_DURATION,
            "The length of time you wish to allow a new organization request that is being processed to remain dormant, before it is automatically rejected or withdrawn"),

    /*
     * *************** SYSTEM MONTH *********************
     */

    SYSTEM_MONTH_JANUARY(SYSTEM_MONTH, "January"),
    SYSTEM_MONTH_FEBRUARY(SYSTEM_MONTH, "February"),
    SYSTEM_MONTH_MARCH(SYSTEM_MONTH, "March"),
    SYSTEM_MONTH_APRIL(SYSTEM_MONTH, "April"),
    SYSTEM_MONTH_MAY(SYSTEM_MONTH, "May"),
    SYSTEM_MONTH_JUNE(SYSTEM_MONTH, "June"),
    SYSTEM_MONTH_JULY(SYSTEM_MONTH, "July"),
    SYSTEM_MONTH_AUGUST(SYSTEM_MONTH, "August"),
    SYSTEM_MONTH_SEPTEMBER(SYSTEM_MONTH, "September"),
    SYSTEM_MONTH_OCTOBER(SYSTEM_MONTH, "October"),
    SYSTEM_MONTH_NOVEMBER(SYSTEM_MONTH, "November"),
    SYSTEM_MONTH_DECEMBER(SYSTEM_MONTH, "December"),

    /*
     * *************** SYSTEM DECLINE ACTION *********************
     */

    SYSTEM_DECLINE_DECLINE(SYSTEM_DECLINE_ACTION, "Decline"),
    SYSTEM_DECLINE_APPLICANT(SYSTEM_DECLINE_ACTION, "Applicant:"),
    SYSTEM_DECLINE_INSTITUTION(SYSTEM_DECLINE_ACTION, "Organization:"),
    SYSTEM_DECLINE_DEPARTMENT(SYSTEM_DECLINE_ACTION, "Department:"),
    SYSTEM_DECLINE_PROGRAM(SYSTEM_DECLINE_ACTION, "Program:"),
    SYSTEM_DECLINE_PROJECT(SYSTEM_DECLINE_ACTION, "Position:"),
    SYSTEM_DECLINE_PROMPT(SYSTEM_DECLINE_ACTION, "You have chosen to decline. Are you sure?"),
    SYSTEM_DECLINE_CONFIRMATION(SYSTEM_DECLINE_ACTION, "You have declined to perform the action."),

    /*
     * *************** SYSTEM HTML GENERAL SECTION *********************
     */

    SYSTEM_HTML_GENERAL_SUPPORT(SYSTEM_HTML_GENERAL_SECTION, "Support"),
    SYSTEM_HTML_GENERAL_PASSWORD_PLACEHOLDER(SYSTEM_HTML_GENERAL_SECTION, "password"),
    SYSTEM_HTML_GENERAL_FIELD_EMAIL_LABEL(SYSTEM_HTML_GENERAL_SECTION, "Email"),
    SYSTEM_HTML_GENERAL_FIELD_EMAIL_PLACEHOLDER(SYSTEM_HTML_GENERAL_SECTION, "email"),
    SYSTEM_HTML_GENERAL_FIELD_EMAIL_HINT(SYSTEM_HTML_GENERAL_SECTION, "Please enter your e-mail address."),
    SYSTEM_HTML_GENERAL_FIELD_PASSWORD_LABEL(SYSTEM_HTML_GENERAL_SECTION, "Password"),
    SYSTEM_HTML_GENERAL_FIELD_PASSWORD_HINT(SYSTEM_HTML_GENERAL_SECTION, "Please enter your password."),
    SYSTEM_HTML_GENERAL_FIELD_PASSWORD_REGISTRATION_HINT(SYSTEM_HTML_GENERAL_SECTION,
            "Please enter a password with a minimum of 8 and a maximum of 15 characters."),
    SYSTEM_HTML_GENERAL_FIELD_CONFIRM_PASSWORD_LABEL(SYSTEM_HTML_GENERAL_SECTION, "Confirm"),
    SYSTEM_HTML_GENERAL_FIELD_CONFIRM_PASSWORD_HINT(SYSTEM_HTML_GENERAL_SECTION, "Please confirm your password."),
    SYSTEM_HTML_GENERAL_FIELD_FIRST_NAME_LABEL(SYSTEM_HTML_GENERAL_SECTION, "First Name"),
    SYSTEM_HTML_GENERAL_FIELD_FIRST_NAME_HINT(SYSTEM_HTML_GENERAL_SECTION, "Please enter your first name."),
    SYSTEM_HTML_GENERAL_FIELD_FIRST_NAMES_LABEL(SYSTEM_HTML_GENERAL_SECTION, "First Names"),
    SYSTEM_HTML_GENERAL_FIELD_FIRST_NAMES_HINT(SYSTEM_HTML_GENERAL_SECTION, "Please enter your first name(s)."),
    SYSTEM_HTML_GENERAL_FIELD_LAST_NAME_LABEL(SYSTEM_HTML_GENERAL_SECTION, "Last Name"),
    SYSTEM_HTML_GENERAL_FIELD_LAST_NAME_HINT(SYSTEM_HTML_GENERAL_SECTION, "Please enter your last name."),
    SYSTEM_HTML_GENERAL_HEADER_PROFILE(SYSTEM_HTML_GENERAL_SECTION, "Profile"),
    SYSTEM_HTML_GENERAL_HEADER_LOGOUT(SYSTEM_HTML_GENERAL_SECTION, "Logout"),
    SYSTEM_HTML_GENERAL_ENQUIRY_SUBJECT(SYSTEM_HTML_GENERAL_SECTION, "Question Regarding UCL Prism Application {{code}}"),
    SYSTEM_HTML_GENERAL_PRIVACY_POLICY(SYSTEM_HTML_GENERAL_SECTION, "Privacy Policy"),
    SYSTEM_HTML_GENERAL_TERMS_AND_CONDITIONS(SYSTEM_HTML_GENERAL_SECTION, "Terms & Conditions"),
    SYSTEM_HTML_SEARCH_OPPORTUNITIES(SYSTEM_HTML_GENERAL_SECTION, "Search Opportunities"),
    SYSTEM_HTML_ADVERTISE_OPPORTUNITIES(SYSTEM_HTML_GENERAL_SECTION, "Advertise Opportunity"),
    SYSTEM_HTML_ACTION_PERFORMED_CONFIRMATION(SYSTEM_HTML_GENERAL_SECTION, "Action completed successfully"),

    /*
     * *************** SYSTEM AUTHENTICATE SECTION *********************
     */

    SYSTEM_AUTHENTICATE_METHOD_OR(SYSTEM_AUTHENTICATE_SECTION, "OR"),
    SYSTEM_AUTHENTICATE_SOCIAL_SIGN_IN_BUTTON(SYSTEM_HTML_GENERAL_SECTION, "Social Sign in"),
    SYSTEM_AUTHENTICATE_NOT_REGISTERED_BUTTON(SYSTEM_HTML_GENERAL_SECTION, "Not Registered?"),
    SYSTEM_AUTHENTICATE_FORGOT_PASSWORD_BUTTON(SYSTEM_HTML_GENERAL_SECTION, "Forgot Password?"),
    SYSTEM_AUTHENTICATE_REGISTER_BUTTON(SYSTEM_AUTHENTICATE_SECTION, "Register"),
    SYSTEM_AUTHENTICATE_SIGN_IN_BUTTON(SYSTEM_AUTHENTICATE_SECTION, "Sign In"),
    SYSTEM_AUTHENTICATE_SIGN_IN_WITH_SOCIAL_MEDIA(SYSTEM_AUTHENTICATE_SECTION, "Sign in with Social Media"),
    SYSTEM_AUTHENTICATE_SIGN_IN_WITH_PRISM(SYSTEM_AUTHENTICATE_SECTION, "Sign in with PRiSM"),
    SYSTEM_AUTHENTICATE_REGISTER_WITH_SOCIAL_MEDIA(SYSTEM_AUTHENTICATE_SECTION, "Register with Social Media"),
    SYSTEM_AUTHENTICATE_REGISTER_WITH_PRISM(SYSTEM_AUTHENTICATE_SECTION, "Register with PRiSM"),
    SYSTEM_AUTHENTICATE_SOCIAL_REGISTER(SYSTEM_AUTHENTICATE_SECTION, "Register with Social Media"),
    SYSTEM_AUTHENTICATE_ALREADY_REGISTERED(SYSTEM_AUTHENTICATE_SECTION, "Already Registered?"),
    SYSTEM_AUTHENTICATE_USE_ANOTHER_METHOD(SYSTEM_AUTHENTICATE_SECTION, "Use another method"),
    SYSTEM_AUTHENTICATE_ACCOUNT_ACTIVATED(SYSTEM_AUTHENTICATE_SECTION, "Your account has been activated. Click below to sign in."),
    SYSTEM_AUTHENTICATE_CONFIRM_DETAILS(SYSTEM_AUTHENTICATE_SECTION, "Please confirm your details."),
    SYSTEM_AUTHENTICATE_PASSWORD_RESENT_CONFIRMATION(SYSTEM_AUTHENTICATE_SECTION,
            "An e-mail with the new password will be sent to {{forgottenPasswordEmail}} shortly."),
    SYSTEM_AUTHENTICATE_ASSOCIATE_WITH_CURRENT_USER(SYSTEM_AUTHENTICATE_SECTION, "Connect to LinkedIn networks to obtain your profile picture"),
    SYSTEM_AUTHENTICATE_JUST_REGISTERED_ALMOST_THERE_MESSAGE(SYSTEM_AUTHENTICATE_SECTION, "Almost there..."),
    SYSTEM_AUTHENTICATE_JUST_REGISTERED_ACTIVATION_MESSAGE(SYSTEM_AUTHENTICATE_SECTION,
            "Thank you for your registration. To activate your account follow the activation link in our confirmation email."),
    SYSTEM_AUTHENTICATE_JUST_REGISTERED_INSTITUTION_ACTIVATION_MESSAGE(SYSTEM_AUTHENTICATE_SECTION,
            "Thank you for your registration. We are very pleased to have you on board. Please activate your account by following the link in our confirmation email. We will approve your organization within 24 hours. As soon as you receive our approval notification, you will be able to post opportunities and target students and graduates in PRiSM."),
    SYSTEM_AUTHENTICATE_APPLY_TO_MODAL_TITLE(SYSTEM_AUTHENTICATE_SECTION, "Apply to {{name}}"),
    SYSTEM_AUTHENTICATE_LOGIN_MODAL_TITLE(SYSTEM_AUTHENTICATE_SECTION, "Login"),
    SYSTEM_AUTHENTICATE_ASSOCIATE_WITH_CURRENT_USER_MODAL_TITLE(SYSTEM_AUTHENTICATE_SECTION, "Add profile picture"),
    SYSTEM_AUTHENTICATE_WARNING_BLOCKER_HEADER_MODAL(SYSTEM_AUTHENTICATE_SECTION, "You have warnings"),
    SYSTEM_AUTHENTICATE_WARNING_BLOCKER_TITLE_MODAL(SYSTEM_AUTHENTICATE_SECTION, "Your attention is required"),
    SYSTEM_AUTHENTICATE_WARNING_BLOCKER_CONTENT_MODAL(
            SYSTEM_AUTHENTICATE_SECTION,
            "Our system has detected that a plugin that blocks adverts is installed in your browser. This may be blocking essential functions of our site. We would advise that you disable it for our pages."),

    /*
     * *************** SYSTEM OPPORTUNITIES SECTION *********************
     */

    SYSTEM_OPPORTUNITIES_ALL_OPPORTUNITY_CATEGORIES(SYSTEM_OPPORTUNITIES_SECTION, "All"),
    SYSTEM_OPPORTUNITIES_APPLICANTS_SEARCH_QUERY_PLACEHOLDER(SYSTEM_OPPORTUNITIES_SECTION, "keywords e.g. Software Engineer"),
    SYSTEM_OPPORTUNITIES_INSTITUTIONS_SEARCH_QUERY_PLACEHOLDER(SYSTEM_OPPORTUNITIES_SECTION, "keywords e.g. Cambridge University"),
    SYSTEM_OPPORTUNITIES_INSTITUTIONS(SYSTEM_OPPORTUNITIES_SECTION, "Universities"),
    SYSTEM_OPPORTUNITIES_ORGANIZATIONS(SYSTEM_OPPORTUNITIES_SECTION, "Organizations"),
    SYSTEM_OPPORTUNITIES_EMPLOYERS(SYSTEM_OPPORTUNITIES_SECTION, "Browse Employers"),
    SYSTEM_OPPORTUNITIES_DEPARTMENTS(SYSTEM_OPPORTUNITIES_SECTION, "Browse Departments"),
    SYSTEM_OPPORTUNITIES_CONNECT(SYSTEM_GLOBAL, "Connect"),
    SYSTEM_OPPORTUNITIES_CREATE_EMPLOYER(SYSTEM_GLOBAL, "Create Employer"),
    SYSTEM_OPPORTUNITIES_INVITE_EMPLOYER(SYSTEM_GLOBAL, "Invite Employer"),
    SYSTEM_OPPORTUNITIES_CREATE_DEPARTMENT(SYSTEM_GLOBAL, "Create Department"),
    SYSTEM_OPPORTUNITIES_INVITE_DEPARTMENT(SYSTEM_GLOBAL, "Invite Department"),
    SYSTEM_OPPORTUNITIES_JOIN(SYSTEM_GLOBAL, "Join"),
    SYSTEM_OPPORTUNITIES_JOIN_STAFF(SYSTEM_GLOBAL, "Join as Staff"),
    SYSTEM_OPPORTUNITIES_JOIN_STUDENT(SYSTEM_GLOBAL, "Join as Student/Graduate"),
    SYSTEM_OPPORTUNITIES_SEARCH(SYSTEM_OPPORTUNITIES_SECTION, "Search"),
    SYSTEM_OPPORTUNITIES_ADVERTISE_NOW(SYSTEM_OPPORTUNITIES_SECTION, "Create Advert"),
    SYSTEM_OPPORTUNITIES_FILTER_CATEGORY_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Category"),
    SYSTEM_OPPORTUNITIES_FILTER_TYPE_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Type"),
    SYSTEM_OPPORTUNITIES_FILTER_KEYWORD_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Keyword"),
    SYSTEM_OPPORTUNITIES_FILTER_KEYWORD_PLACEHOLDER(SYSTEM_OPPORTUNITIES_SECTION, "Keyword, e.g. mathematics"),
    SYSTEM_OPPORTUNITIES_FILTER_LOCATION_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Location"),
    SYSTEM_OPPORTUNITIES_FILTER_LOCATION_PLACEHOLDER(SYSTEM_OPPORTUNITIES_SECTION, "location e.g. London"),
    SYSTEM_OPPORTUNITIES_FILTER_COURSE_TYPE_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Availability"),
    SYSTEM_OPPORTUNITIES_FILTER_DURATION_MONTHLY(SYSTEM_OPPORTUNITIES_SECTION, "Monthly"),
    SYSTEM_OPPORTUNITIES_FILTER_DURATION_ANNUAL(SYSTEM_OPPORTUNITIES_SECTION, "Annual"),
    SYSTEM_OPPORTUNITIES_FILTER_FEE_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Fee Level"),
    SYSTEM_OPPORTUNITIES_FILTER_SALARY(SYSTEM_OPPORTUNITIES_SECTION, "Salary Level"),
    SYSTEM_OPPORTUNITIES_FILTER_STIPEND(SYSTEM_OPPORTUNITIES_SECTION, "Stipend Level"),
    SYSTEM_OPPORTUNITIES_FILTER_SALARY_STIPEND(SYSTEM_OPPORTUNITIES_SECTION, "Salary/Stipend Level"),
    SYSTEM_OPPORTUNITIES_FILTER_DURATION(SYSTEM_OPPORTUNITIES_SECTION, "Duration"),
    SYSTEM_OPPORTUNITIES_FILTER_INSTITUTION_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Organization"),
    SYSTEM_OPPORTUNITIES_FILTER_CLEAR(SYSTEM_OPPORTUNITIES_SECTION, "Clear"),
    SYSTEM_OPPORTUNITIES_LOAD_MORE(SYSTEM_OPPORTUNITIES_SECTION, "Load More"),
    SYSTEM_OPPORTUNITIES_PROPERTY_CLOSING_DATE_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Closing Date"),
    SYSTEM_OPPORTUNITIES_PROPERTY_NO_CLOSING_DATE(SYSTEM_OPPORTUNITIES_SECTION, "No closing date"),
    SYSTEM_OPPORTUNITIES_PROPERTY_STUDY_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Available:"),
    SYSTEM_OPPORTUNITIES_PROPERTY_STUDY_DURATION_FROM(SYSTEM_OPPORTUNITIES_SECTION, "from {{studyDurationMinimum}} Months"),
    SYSTEM_OPPORTUNITIES_PROPERTY_STUDY_DURATION_FROM_TO(SYSTEM_OPPORTUNITIES_SECTION, "from {{studyDurationMinimum}} to {{studyDurationMaximum}} Months"),
    SYSTEM_OPPORTUNITIES_PROPERTY_STUDY_FROM(SYSTEM_OPPORTUNITIES_SECTION, "from {{studyDurationMinimum}}"),
    SYSTEM_OPPORTUNITIES_PROPERTY_FEE_PAY_FROM_TO(SYSTEM_OPPORTUNITIES_SECTION, "{{minimum}} to {{maximum}} {{currency}}"),
    SYSTEM_OPPORTUNITIES_PROPERTY_FEE_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Fees:"),
    SYSTEM_OPPORTUNITIES_PROPERTY_PAY_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Payments:"),
    SYSTEM_OPPORTUNITIES_PROPERTY_LOCATIONS_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Locations:"),
    SYSTEM_OPPORTUNITIES_PROPERTY_FUNCTIONS_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Functions:"),
    SYSTEM_OPPORTUNITIES_PROPERTY_INDUSTRIES_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Industries:"),
    SYSTEM_OPPORTUNITIES_PROPERTY_COMPETENCIES_LABEL(SYSTEM_OPPORTUNITIES_SECTION, "Competencies:"),
    SYSTEM_OPPORTUNITIES_FURTHER_INFORMATION(SYSTEM_OPPORTUNITIES_SECTION, "Further information"),
    SYSTEM_OPPORTUNITIES_READ_MORE(SYSTEM_OPPORTUNITIES_SECTION, "Read More"),
    SYSTEM_OPPORTUNITIES_READ_LESS(SYSTEM_OPPORTUNITIES_SECTION, "Read Less"),
    SYSTEM_OPPORTUNITIES_ENQUIRE(SYSTEM_OPPORTUNITIES_SECTION, "Enquiries"),
    SYSTEM_OPPORTUNITIES_ENQUIRE_QUESTION(SYSTEM_OPPORTUNITIES_SECTION, "Ask a Question"),
    SYSTEM_OPPORTUNITIES_ENQUIRE_FORWARD_CV(SYSTEM_OPPORTUNITIES_SECTION, "Interested in Joining us?"),
    SYSTEM_OPPORTUNITIES_ENQUIRE_WORK_EXPERIENCE(SYSTEM_OPPORTUNITIES_SECTION, "Considering Work Experience?"),
    SYSTEM_OPPORTUNITIES_ENQUIRE_SUBMIT(SYSTEM_OPPORTUNITIES_SECTION, "Submit"),
    SYSTEM_OPPORTUNITIES_APPLY_NOW(SYSTEM_OPPORTUNITIES_SECTION, "Apply Now"),
    SYSTEM_OPPORTUNITIES_NOT_ACCEPTING_APPLICATIONS(SYSTEM_OPPORTUNITIES_SECTION, "Not accepting applications at the current time."),
    SYSTEM_OPPORTUNITIES_NO_OPPORTUNITIES(SYSTEM_OPPORTUNITIES_SECTION, "Can't find the opportunity that you're looking for?"),
    SYSTEM_OPPORTUNITIES_NO_DEPARTMENTS(SYSTEM_OPPORTUNITIES_SECTION, "Can't find the department that you're looking for?"),
    SYSTEM_OPPORTUNITIES_NO_EMPLOYERS(SYSTEM_OPPORTUNITIES_SECTION, "Can't find the employer that you're looking for?"),
    SYSTEM_OPPORTUNITIES_RELATED_INSTITUTIONS(SYSTEM_OPPORTUNITIES_SECTION, "Related Organizations"),
    SYSTEM_OPPORTUNITIES_RELATED_DEPARTMENTS(SYSTEM_OPPORTUNITIES_SECTION, "Related Departments"),
    SYSTEM_OPPORTUNITIES_RELATED_PROGRAMS(SYSTEM_OPPORTUNITIES_SECTION, "Related Programs"),
    SYSTEM_OPPORTUNITIES_RELATED_PROJECTS(SYSTEM_OPPORTUNITIES_SECTION, "Related Positions"),
    SYSTEM_OPPORTUNITIES_ADVERT_TAB_MAIN_DESCRIPTION(SYSTEM_OPPORTUNITIES_SECTION, "Description"),
    SYSTEM_OPPORTUNITIES_ADVERT_TAB_MAIN_OPPORTUNITIES(SYSTEM_OPPORTUNITIES_SECTION, "Opportunities"),
    SYSTEM_OPPORTUNITIES_ADVERT_TAB_MAIN_DEPARTMENTS(SYSTEM_OPPORTUNITIES_SECTION, "Departments"),
    SYSTEM_OPPORTUNITIES_ADVERT_TAB_MAIN_PARTNERS(SYSTEM_OPPORTUNITIES_SECTION, "Recruitment Partners"),
    SYSTEM_OPPORTUNITIES_ADVERT_TAB_MAIN_EMPLOYERS(SYSTEM_OPPORTUNITIES_SECTION, "Employers"),
    SYSTEM_OPPORTUNITIES_ADVERT_TAB_MAIN_LOCATION(SYSTEM_OPPORTUNITIES_SECTION, "Location"),
    SYSTEM_OPPORTUNITIES_ADVERT_TAB_MAIN_COMPETENCES(SYSTEM_OPPORTUNITIES_SECTION, "Person Specification"),

    /*
     * *************** SYSTEM OPPORTUNITIES ENQUIRY FORM SECTION *********************
     */

    SYSTEM_ENQUIRE_FORM_EMAIL_LABEL(SYSTEM_OPPORTUNITIES_ENQUIRY_SECTION, "Your Email"),
    SYSTEM_ENQUIRE_FORM_EMAIL_HINT(SYSTEM_OPPORTUNITIES_ENQUIRY_SECTION, "Please enter your e-mail address."),
    SYSTEM_ENQUIRE_FORM_ENQUIRE_MESSAGE_LABEL(SYSTEM_OPPORTUNITIES_ENQUIRY_SECTION, "Your Message"),
    SYSTEM_ENQUIRE_FORM_ENQUIRE_MESSAGE_HINT(SYSTEM_OPPORTUNITIES_ENQUIRY_SECTION, "Please enter your message."),

    /*
     * *************** SYSTEM ADVERTISE SECTION *********************
     */

    SYSTEM_ADVERTISE_LOOKUP_ORGANIZATION_LABEL(SYSTEM_ADVERTISE_SECTION, "Lookup Organization"),
    SYSTEM_ADVERTISE_LOOKUP_ORGANIZATION_HINT(SYSTEM_ADVERTISE_SECTION, "Find your organization within the list"),
    SYSTEM_ADVERTISE_LOOKUP_GOOGLE_ORGANIZATION_LABEL(SYSTEM_ADVERTISE_SECTION, "Lookup Organization"),
    SYSTEM_ADVERTISE_LOOKUP_GOOGLE_ORGANIZATION_HINT(SYSTEM_ADVERTISE_SECTION, "Start typing the name of your organization"),
    SYSTEM_ADVERTISE_LOOKUP_GOOGLE_ORGANIZATION_PLACEHOLDER(SYSTEM_ADVERTISE_SECTION, "e.g. Google Inc"),
    SYSTEM_ADVERTISE_SELECT_PARENT_LABEL(SYSTEM_ADVERTISE_SECTION, "Select Department / Program"),
    SYSTEM_ADVERTISE_SELECT_PARENT_HINT(SYSTEM_ADVERTISE_SECTION, "Select your preferred Department / Program"),
    SYSTEM_ADVERTISE_CANNOT_FIND_MY_ORGANIZATION(SYSTEM_ADVERTISE_SECTION, "I cannot find my organization"),
    SYSTEM_ADVERTISE_ALREADY_REGISTERED_ORGANIZATION(SYSTEM_ADVERTISE_SECTION, "My organization is registered"),
    SYSTEM_ADVERTISE_CONNECTING(SYSTEM_ADVERTISE_SECTION, "Connecting with"),
    SYSTEM_ADVERTISE_REGISTER_INSTITUTION_TITLE(SYSTEM_ADVERTISE_SECTION, "Register your Organization"),
    SYSTEM_ADVERTISE_REGISTER_DEPARTMENT_TITLE(SYSTEM_ADVERTISE_SECTION, "Create Department"),
    SYSTEM_ADVERTISE_REGISTER_PROGRAM_TITLE(SYSTEM_ADVERTISE_SECTION, "Create Program"),
    SYSTEM_ADVERTISE_REGISTER_PROJECT_TITLE(SYSTEM_ADVERTISE_SECTION, "Create Position"),
    SYSTEM_ADVERTISE_MY_INSTITUTION_LABEL(SYSTEM_ADVERTISE_SECTION, "My Organization"),
    SYSTEM_ADVERTISE_MY_INSTITUTION_HINT(SYSTEM_ADVERTISE_SECTION, "Specify whether your organization is the host organization, or a different organization"),
    SYSTEM_ADVERTISE_INSTITUTION_OTHER_HINT(SYSTEM_ADVERTISE_SECTION, "Other Organization"),
    SYSTEM_ADVERTISE_EMPLOYERS_AND_RECRUITERS(SYSTEM_ADVERTISE_SECTION, "Employers & Recruiters"),
    SYSTEM_ADVERTISE_EMPLOYERS_AND_RECRUITERS_FREE(SYSTEM_ADVERTISE_SECTION, "Advertising with PRiSM is free"),
    SYSTEM_ADVERTISE_EMPLOYERS_AND_RECRUITERS_VALUE(SYSTEM_ADVERTISE_SECTION, "You can target as many university departments as you like, you only pay to process the applications and candidates that are of interest to you."),
    SYSTEM_ADVERTISE_EMPLOYERS_AND_RECRUITERS_ORGANIZATION_ALREADY_REGISTERED(SYSTEM_ADVERTISE_SECTION, "If you have already registered your organization, you don't need to do this again. Just login with the account that you provided, and nagivate to your organizations in the resources menu."),
    SYSTEM_ADVERTISE_UNIVERSITIES(SYSTEM_ADVERTISE_SECTION, "Universities & Departments"),
    SYSTEM_ADVERTISE_UNIVERSITIES_FREE(SYSTEM_ADVERTISE_SECTION, "PRiSM is free for universities"),
    SYSTEM_ADVERTISE_UNIVERSITIES_VALUE(SYSTEM_ADVERTISE_SECTION, "You can connect with as many employers as you like, you only pay if you want to make it free for employers to process applications from your students and graduates."),
    SYSTEM_ADVERTISE_UNIVERSITIES_ORGANIZATION_ALREADY_REGISTERED(SYSTEM_ADVERTISE_SECTION, "If you have already registered your department, you don't need to do this again. Just login with the account that you provided, and nagivate to your departments in the resources menu."),

    /*
     * *************** SYSTEM RESOURCES SECTION *********************
     */

    SYSTEM_RESOURCES_FILTER(SYSTEM_RESOURCES_SECTION, "Filter"),
    SYSTEM_RESOURCES_FILTER_CLEAR(SYSTEM_RESOURCES_SECTION, "Clear"),
    SYSTEM_RESOURCES_FILTER_ADVANCED(SYSTEM_RESOURCES_SECTION, "Advanced"),
    SYSTEM_RESOURCES_FILTER_ADVANCED_APPLIED(SYSTEM_RESOURCES_SECTION, "Advanced filter applied."),
    SYSTEM_RESOURCES_FILTER_QUICK_SEARCH_PLACEHOLDER(SYSTEM_RESOURCES_SECTION, "Quick Search..."),
    SYSTEM_RESOURCES_FILTER_URGENT_ONLY(SYSTEM_RESOURCES_SECTION, "Requiring Attention"),
    SYSTEM_RESOURCES_FILTER_TARGET_ONLY(SYSTEM_RESOURCES_SECTION, "From my Targets"),
    SYSTEM_RESOURCES_FILTER_SAVE_AS_DEFAULT(SYSTEM_RESOURCES_SECTION, "Save as Default Filter"),
    SYSTEM_RESOURCES_FILTER_LOAD_DEFAULT(SYSTEM_RESOURCES_SECTION, "Load Default Filter"),
    SYSTEM_RESOURCES_ACTION_PERFORMED(SYSTEM_RESOURCES_SECTION, "Thank you. Your action was successful."),
    SYSTEM_RESOURCES_ACTION_UNAVAILABLE(SYSTEM_RESOURCES_SECTION, "Thank you. Your action is no longer required."),
    SYSTEM_RESOURCES_ADVANCE_FILTER_TITLE(SYSTEM_RESOURCES_SECTION, "Advance Filter"),
    SYSTEM_RESOURCES_ADVANCE_FILTER_REQUIRE_ATTENTION(SYSTEM_RESOURCES_SECTION, "Requiring Attention"),
    SYSTEM_RESOURCES_ADVANCE_FILTER_MATCH_ANY(SYSTEM_RESOURCES_SECTION, "Match Any"),
    SYSTEM_RESOURCES_ADVANCE_FILTER_MATCH_ALL(SYSTEM_RESOURCES_SECTION, "Match All"),
    SYSTEM_RESOURCES_ADVANCE_FILTER_COLUMN_PLACEHOLDER(SYSTEM_RESOURCES_SECTION, "Column..."),
    SYSTEM_RESOURCES_ADVANCE_FILTER_BY_PLACEHOLDER(SYSTEM_RESOURCES_SECTION, "Filter by..."),
    SYSTEM_RESOURCES_NO_RESULTS(SYSTEM_RESOURCES_SECTION, "We cannot find any results for your filter. Did you try our advanced filter?"),
    SYSTEM_RESOURCES_DOWNLOAD_REPORT(SYSTEM_RESOURCES_SECTION, "Download Report"),
    SYSTEM_RESOURCES_DOWNLOAD_PDF(SYSTEM_RESOURCES_SECTION, "Download selected as PDF"),
    SYSTEM_RESOURCES_RESOURCE_COLUMN_NUMBER(SYSTEM_RESOURCES_SECTION, "Resource #"),
    SYSTEM_RESOURCES_RESOURCE_COLUMN_TITLE(SYSTEM_RESOURCES_SECTION, "Title"),
    SYSTEM_RESOURCES_RESOURCE_COLUMN_UPDATED(SYSTEM_RESOURCES_SECTION, "Updated"),
    SYSTEM_RESOURCES_RESOURCE_COLUMN_RATING(SYSTEM_RESOURCES_SECTION, "Rating"),
    SYSTEM_RESOURCES_RESOURCE_COLUMN_STATUS(SYSTEM_RESOURCES_SECTION, "Status"),
    SYSTEM_RESOURCES_RESOURCE_COLUMN_ACTIONS(SYSTEM_RESOURCES_SECTION, "Actions"),
    SYSTEM_RESOURCES_RESOURCE_RECENTLY_UPDATED(SYSTEM_RESOURCES_SECTION, "Recently Updated"),
    SYSTEM_RESOURCES_RESOURCE_PARENT_UNCOMPLETED(SYSTEM_HTML_FIELDS, "Advert has incomplete sections, click the link to edit"),
    SYSTEM_RESOURCES_LOAD_MORE(SYSTEM_RESOURCES_SECTION, "Load More"),
    SYSTEM_RESOURCES_SUMMARY_CREATED(SYSTEM_RESOURCES_SECTION, "Created:"),
    SYSTEM_RESOURCES_SUMMARY_AUTHOR(SYSTEM_RESOURCES_SECTION, "Author:"),
    SYSTEM_RESOURCES_SUMMARY_ACTIVE_DEPARTMENTS(SYSTEM_RESOURCES_SECTION, "Active Departments:"),
    SYSTEM_RESOURCES_SUMMARY_ACTIVE_PROGRAMS(SYSTEM_RESOURCES_SECTION, "Active Programs:"),
    SYSTEM_RESOURCES_SUMMARY_ACTIVE_PROJECTS(SYSTEM_RESOURCES_SECTION, "Active Positions:"),
    SYSTEM_RESOURCES_SUMMARY_APPLICATIONS(SYSTEM_RESOURCES_SECTION, "Applications"),
    SYSTEM_RESOURCES_SUMMARY_OCCURRENCES(SYSTEM_RESOURCES_SECTION, "Occurrence {{occurrences}} times"),
    SYSTEM_RESOURCES_SUMMARY_AVERAGE_DURATION(SYSTEM_RESOURCES_SECTION, "Duration {{duration}} days"),
    SYSTEM_RESOURCES_SUMMARY_TOTAL_LIVE(SYSTEM_RESOURCES_SECTION, "Total Live {{count}}"),
    SYSTEM_RESOURCES_SUMMARY_SUBMITTED(SYSTEM_RESOURCES_SECTION, "Submitted"),
    SYSTEM_RESOURCES_SUMMARY_APPROVED(SYSTEM_RESOURCES_SECTION, "Approved"),
    SYSTEM_RESOURCES_SUMMARY_REJECTED(SYSTEM_RESOURCES_SECTION, "Rejected"),
    SYSTEM_RESOURCES_SUMMARY_WITHDRAWN(SYSTEM_RESOURCES_SECTION, "Withdrawn"),
    SYSTEM_RESOURCES_SUMMARY_APPLICATION_SUBMITTED(SYSTEM_RESOURCES_SECTION, "Submitted"),
    SYSTEM_RESOURCES_SUMMARY_APPLICATION_NOT_SUBMITTED(SYSTEM_RESOURCES_SECTION, "Not Submitted"),
    SYSTEM_RESOURCES_SUMMARY_APPLICATION_CLOSING_DATE(SYSTEM_RESOURCES_SECTION, "Closing Date:"),
    SYSTEM_RESOURCES_SUMMARY_APPLICATION_NO_CLOSING_DATE(SYSTEM_RESOURCES_SECTION, "No Closing Date"),
    SYSTEM_RESOURCES_SUMMARY_APPLICATION_FROM_TO(SYSTEM_RESOURCES_SECTION, "{{startDate}} to {{endDate}}"),
    SYSTEM_RESOURCES_SUMMARY_APPLICATION_FROM_TO_NOW(SYSTEM_RESOURCES_SECTION, "{{startDate}} to now"),
    SYSTEM_RESOURCES_SUMMARY_APPLICATION_LATEST_QUALIFICATION(SYSTEM_RESOURCES_SECTION, "Latest Qualification:"),
    SYSTEM_RESOURCES_SUMMARY_APPLICATION_LATEST_POSITION(SYSTEM_RESOURCES_SECTION, "Latest Position:"),
    SYSTEM_RESOURCES_SUMMARY_APPLICATION_NO_DOCUMENTS(SYSTEM_RESOURCES_SECTION, "No Documents provided"),
    SYSTEM_RESOURCES_SUMMARY_APPLICATION_REFERENCES(SYSTEM_RESOURCES_SECTION, "References:"),

    /*
     * *************** SYSTEM RESOURCE SECTION *********************
     */

    SYSTEM_RESOURCE_ASSIGNED_USERS_INTERESTED(SYSTEM_RESOURCE_SECTION, "Users interested in applicant"),
    SYSTEM_RESOURCE_ASSIGNED_USERS_POTENTIALLY_INTERESTED(SYSTEM_RESOURCE_SECTION, "Other relevant users"),
    SYSTEM_RESOURCE_NEXT_STATE_LABEL(SYSTEM_RESOURCE_SECTION, "What do you want to do next?"),
    SYSTEM_RESOURCE_NEXT_STATE_HINT(SYSTEM_RESOURCE_SECTION, "Please choose the next state."),
    SYSTEM_RESOURCE_TIMELINE_HEADER(SYSTEM_RESOURCE_SECTION, "Timeline"),
    SYSTEM_RESOURCE_MANAGE_USERS_HEADER(SYSTEM_RESOURCE_SECTION, "Manage Users"),
    SYSTEM_RESOURCE_EMAIL_TEMPLATES_HEADER(SYSTEM_RESOURCE_SECTION, "Email Templates"),
    SYSTEM_RESOURCE_CONFIGURATION_HEADER(SYSTEM_RESOURCE_SECTION, "Configuration"),
    SYSTEM_RESOURCE_SUMMARY_HEADER(SYSTEM_RESOURCE_SECTION, "Summary"),
    SYSTEM_RESOURCE_DETAILS_INCOMPLETE(SYSTEM_RESOURCE_SECTION, "Upload personalized images and content to increase the interest in your advert."),
    SYSTEM_RESOURCE_DEPARTMENT_PROGRAMS_HEADER(SYSTEM_RESOURCE_SECTION, "Programs"),
    SYSTEM_RESOURCE_ADVERT_HEADER(SYSTEM_RESOURCE_SECTION, "Advert"),
    SYSTEM_RESOURCE_ADVERT_DETAILS_INCOMPLETE(SYSTEM_RESOURCE_SECTION, "Complete your advert, to help candidates understand whether they wish to apply."),
    SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER(SYSTEM_RESOURCE_SECTION, "Search Categories"),
    SYSTEM_RESOURCE_ADVERT_CATEGORIES_INCOMPLETE(SYSTEM_RESOURCE_SECTION, "Provide some search categories to help candidates discover your advert."),
    SYSTEM_RESOURCE_FEES_PAYMENTS_HEADER(SYSTEM_RESOURCE_SECTION, "Fees and Payments"),
    SYSTEM_RESOURCE_CLOSING_DATES_HEADER(SYSTEM_RESOURCE_SECTION, "Closing Dates"),
    SYSTEM_RESOURCE_TARGETS_HEADER(SYSTEM_RESOURCE_SECTION, "Target Universities"),
    SYSTEM_RESOURCE_TARGETS_INCOMPLETE(SYSTEM_RESOURCE_SECTION, "Tell us which organizations you want to target, so that we can find the most qualified candidates for you."),
    SYSTEM_RESOURCE_COMPETENCES_HEADER(SYSTEM_RESOURCE_SECTION, "Target Competences"),
    SYSTEM_RESOURCE_COMPETENCES_INCOMPLETE(SYSTEM_RESOURCE_SECTION, "Tell us what kind of people you want to target, so that we can find the most suitable candidates for you."),
    SYSTEM_RESOURCE_STATISTICS_HEADER(SYSTEM_RESOURCE_SECTION, "Statistics"),
    SYSTEM_RESOURCE_USER_BOUNCES_HEADER(SYSTEM_RESOURCE_SECTION, "Message Bounces"),
    SYSTEM_RESOURCE_UNREGISTERED_USERS_HEADER(SYSTEM_RESOURCE_SECTION, "Incomplete Registrations"),
    SYSTEM_RESOURCE_TRANSLATIONS_HEADER(SYSTEM_RESOURCE_SECTION, "Customization"),
    SYSTEM_RESOURCE_WORKFLOW_HEADER(SYSTEM_RESOURCE_SECTION, "Workflow and Data Entry Settings"),
    SYSTEM_RESOURCE_ACTIONS(SYSTEM_RESOURCE_SECTION, "Actions"),
    SYSTEM_RESOURCE_TIMELINE_MORE_ACTIONS(SYSTEM_RESOURCE_SECTION, "More Actions"),
    SYSTEM_RESOURCE_TIMELINE_NOW(SYSTEM_RESOURCE_SECTION, "Now"),
    SYSTEM_RESOURCE_TIMELINE_ON_BEHALF_OF(SYSTEM_RESOURCE_SECTION, "{{user}} on behalf of {{delegateUser}}"),
    SYSTEM_RESOURCE_APPLICATION_FORM_HEADER(SYSTEM_RESOURCE_SECTION, "Application Form"),

    /*
     * *************** SYSTEM ACCOUNT SECTION *********************
     */

    SYSTEM_ACCOUNT_MANAGE_ACCOUNT(SYSTEM_ACCOUNT_SECTION, "Manage Account"),
    SYSTEM_ACCOUNT_SOCIAL_CONNECTIONS_SUBHEADER(SYSTEM_ACCOUNT_SECTION, "Social networks connections"),
    SYSTEM_ACCOUNT_SOCIAL_CHANGE_PASSWORD_SUBHEADER(SYSTEM_ACCOUNT_SECTION, "Change password"),
    SYSTEM_ACCOUNT_DETAILS_SEND_RECOMMENDATIONS_LABEL(SYSTEM_ACCOUNT_SECTION, "Send recommendation notifications?"),
    SYSTEM_ACCOUNT_DETAILS_SEND_RECOMMENDATIONS_HINT(SYSTEM_ACCOUNT_SECTION, "Opt into receiving recommendation notifications from us"),
    SYSTEM_ACCOUNT_LINK_WITH_EXTERNAL_ACCOUNT_LABEL(SYSTEM_ACCOUNT_SECTION, "Link account to Social Networks"),
    SYSTEM_ACCOUNT_LINK_WITH_EXTERNAL_ACCOUNT_HINT(SYSTEM_ACCOUNT_SECTION, "Link your account to social networks that you use. This helps you to share information about your activities within PRiSM with friends and colleagues."),
    SYSTEM_ACCOUNT_LINK_WITH_EXTERNAL_ACCOUNT_LINKED(SYSTEM_ACCOUNT_SECTION, "Linked"),
    SYSTEM_ACCOUNT_LINK_WITH_EXTERNAL_ACCOUNT_UNLINK_BUTTON(SYSTEM_ACCOUNT_SECTION, "Unlink"),
    SYSTEM_ACCOUNT_LINKED_ACCOUNTS(SYSTEM_ACCOUNT_SECTION, "Linked Accounts"),
    SYSTEM_ACCOUNT_LINKED_SELECT_PRIMARY_SUBHEADER(SYSTEM_ACCOUNT_SECTION, "Select new primary account"),
    SYSTEM_ACCOUNT_LINKED_LINK_ACCOUNT_SUBHEADER(SYSTEM_ACCOUNT_SECTION, "Link new Account"),
    SYSTEM_ACCOUNT_LINKED_PRIMARY_LABEL(SYSTEM_ACCOUNT_SECTION, "Primary email account"),
    SYSTEM_ACCOUNT_LINKED_PRIMARY_BUTTON(SYSTEM_ACCOUNT_SECTION, "Select as Primary"),
    SYSTEM_ACCOUNT_LINKED_UNLINK(SYSTEM_ACCOUNT_SECTION, "Unlink"),
    SYSTEM_ACCOUNT_LINKED_LINK_ACCOUNT_BUTTON(SYSTEM_ACCOUNT_SECTION, "Link new Account"),
    SYSTEM_ACCOUNT_LINKED_CURRENT_PASSWORD_LABEL(SYSTEM_ACCOUNT_SECTION, "Current User Password"),
    SYSTEM_ACCOUNT_LINKED_CURRENT_PASSWORD_HINT(SYSTEM_ACCOUNT_SECTION, "Please enter your password."),
    SYSTEM_ACCOUNT_LINKED_OTHER_EMAIL_LABEL(SYSTEM_ACCOUNT_SECTION, "User Email to Link"),
    SYSTEM_ACCOUNT_LINKED_OTHER_EMAIL_HINT(SYSTEM_ACCOUNT_SECTION, "Enter email address you want to link with current account."),
    SYSTEM_ACCOUNT_LINKED_OTHER_PASSWORD_LABEL(SYSTEM_ACCOUNT_SECTION, "User Password to Link"),
    SYSTEM_ACCOUNT_LINKED_OTHER_PASSWORD_HINT(SYSTEM_ACCOUNT_SECTION, "Please enter the password of the other account."),
    SYSTEM_ACCOUNT_LINKED_LINK_BUTTON(SYSTEM_ACCOUNT_SECTION, "Link Accounts"),

    /*
     * *************** SYSTEM HTML FIELDS *********************
     */

    SYSTEM_HTML_FIELDS_ACTIONS_CONTROL_DOWNLOAD_AS_PDF(SYSTEM_HTML_FIELDS, "Download as PDF"),
    SYSTEM_HTML_ASSIGN_USERS_USER_NOT_IN_LIST(SYSTEM_HTML_FIELDS, "User not in the list?"),
    SYSTEM_HTML_DURATION_CONTROL_HOURS(SYSTEM_HTML_FIELDS, "Hours"),
    SYSTEM_HTML_DURATION_CONTROL_MINUTES(SYSTEM_HTML_FIELDS, "Minutes"),
    SYSTEM_HTML_APPLICATION_MULTIPLE_SECTION_UNCOMPLETED(SYSTEM_HTML_FIELDS, "Incomplete"),
    SYSTEM_HTML_APPLICATION_MULTIPLE_SECTION_COMPLETED(SYSTEM_HTML_FIELDS, "Completed"),
    SYSTEM_HTML_APPLICATION_MULTIPLE_SECTION_NO_MORE_ITEMS(SYSTEM_HTML_FIELDS, "You cannot provide more than {{maxItems}} item(s)."),
    SYSTEM_HTML_FILE_UPLOAD_PDF(SYSTEM_HTML_FIELDS, "Upload PDF"),
    SYSTEM_HTML_FILE_UPLOAD_IMAGE(SYSTEM_HTML_FIELDS, "Upload Logo"),
    SYSTEM_HTML_LOOKUP_USER_LABEL(SYSTEM_HTML_FIELDS, "Lookup User"),
    SYSTEM_HTML_LOOKUP_USER_PLACEHOLDER(SYSTEM_HTML_FIELDS, "e.g. first name, last name, or email"),
    SYSTEM_HTML_LOOKUP_USER_HINT(SYSTEM_HTML_FIELDS, "Start typing the first name, last name or email address of the user you are looking for."),
    SYSTEM_HTML_LOOKUP_CANNOT_FIND_USER(SYSTEM_HTML_FIELDS, "Cannot Find User?"),
    SYSTEM_HTML_LOOKUP_FIRST_NAME_LABEL(SYSTEM_HTML_FIELDS, "First Name"),
    SYSTEM_HTML_LOOKUP_FIRST_NAME_HINT(SYSTEM_HTML_FIELDS, "Please enter the first name of the user."),
    SYSTEM_HTML_LOOKUP_LAST_NAME_LABEL(SYSTEM_HTML_FIELDS, "Last Name"),
    SYSTEM_HTML_LOOKUP_LAST_NAME_HINT(SYSTEM_HTML_FIELDS, "Please enter the last name of the user."),
    SYSTEM_HTML_LOOKUP_EMAIL_LABEL(SYSTEM_HTML_FIELDS, "Email"),
    SYSTEM_HTML_LOOKUP_EMAIL_HINT(SYSTEM_HTML_FIELDS, "Please enter user's e-mail address."),
    SYSTEM_HTML_LOOKUP_PROGRAM_LABEL(SYSTEM_HTML_FIELDS, "Lookup Program"),
    SYSTEM_HTML_LOOKUP_PROGRAM_PLACEHOLDER(SYSTEM_HTML_FIELDS, "e.g. program name, organization name"),
    SYSTEM_HTML_LOOKUP_PROGRAM_CANNOT_FIND(SYSTEM_HTML_FIELDS, "Cannot Find Your Program?"),
    SYSTEM_HTML_LOOKUP_PROGRAM_DOMICILE_LABEL(SYSTEM_HTML_FIELDS, "Organization Country"),
    SYSTEM_HTML_LOOKUP_PROGRAM_DOMICILE_HINT(SYSTEM_HTML_FIELDS, "The country in which the organization is located."),
    SYSTEM_HTML_LOOKUP_PROGRAM_INSTITUTION_LABEL(SYSTEM_HTML_FIELDS, "Organization Name"),
    SYSTEM_HTML_LOOKUP_PROGRAM_INSTITUTION_HINT(SYSTEM_HTML_FIELDS, "The name of the organization."),
    SYSTEM_HTML_LOOKUP_PROGRAM_PROGRAM_LABEL(SYSTEM_HTML_FIELDS, "Program Name"),
    SYSTEM_HTML_LOOKUP_PROGRAM_PROGRAM_HINT(SYSTEM_HTML_FIELDS, "The name of the program."),
    SYSTEM_HTML_LOOKUP_PROGRAM_QUALIFICATION_TYPE_LABEL(SYSTEM_HTML_FIELDS, "Qualification Type"),
    SYSTEM_HTML_LOOKUP_PROGRAM_QUALIFICATION_TYPE_HINT(SYSTEM_HTML_FIELDS, "Select the type of the qualification from the list. If you cannot find an appropriate type, select 'Other'."),
    SYSTEM_HTML_LOOKUP_PROGRAM_HOMEPAGE_LABEL(SYSTEM_HTML_FIELDS, "Program Homepage"),
    SYSTEM_HTML_LOOKUP_PROGRAM_HOMEPAGE_HINT(SYSTEM_HTML_FIELDS, "URL of the program homepage"),
    SYSTEM_HTML_LOOKUP_RETURN_TO_SEARCH(SYSTEM_HTML_FIELDS, "Return to search"),
    SYSTEM_HTML_RATING_WEIGHTS_CONFIGURATION_SUM_LABEL(SYSTEM_HTML_FIELDS, "Weighting Sum"),
    SYSTEM_HTML_RATING_WEIGHTS_CONFIGURATION_SUM_HINT(SYSTEM_HTML_FIELDS, "Specify (between 0 and 1) the importance that you place upon this criterion"),

    /*
     * *************** SYSTEM HTML COMMON *********************
     */

    SYSTEM_HTML_COMMON_EDIT_BUTTON(SYSTEM_HTML_COMMON, "Edit"),
    SYSTEM_HTML_COMMON_SAVE_BUTTON(SYSTEM_HTML_COMMON, "Save"),
    SYSTEM_HTML_COMMON_UPDATE_BUTTON(SYSTEM_HTML_COMMON, "Update"),
    SYSTEM_HTML_COMMON_CANCEL_BUTTON(SYSTEM_HTML_COMMON, "Cancel"),
    SYSTEM_HTML_COMMON_CLEAR_BUTTON(SYSTEM_HTML_COMMON, "Clear"),
    SYSTEM_HTML_COMMON_CLOSE_BUTTON(SYSTEM_HTML_COMMON, "Close"),
    SYSTEM_HTML_COMMON_DELETE_BUTTON(SYSTEM_HTML_COMMON, "Delete"),
    SYSTEM_HTML_COMMON_NEXT_BUTTON(SYSTEM_HTML_COMMON, "Next"),
    SYSTEM_HTML_COMMON_CONFIRM_BUTTON(SYSTEM_HTML_COMMON, "Confirm"),
    SYSTEM_HTML_COMMON_SUBMIT_BUTTON(SYSTEM_HTML_COMMON, "Submit"),
    SYSTEM_HTML_COMMON_ADD_BUTTON(SYSTEM_HTML_COMMON, "Add"),

    /*
     * *************** SYSTEM HTML ADDRESS *********************
     */

    SYSTEM_ADDRESS_LINE1_LABEL(SYSTEM_HTML_ADDRESS, "Building name / number & street"),
    SYSTEM_ADDRESS_LINE1_HINT(SYSTEM_HTML_ADDRESS, "The building name/number and street of the address."),
    SYSTEM_ADDRESS_TOWN_LABEL(SYSTEM_HTML_ADDRESS, "Town / city / suburb"),
    SYSTEM_ADDRESS_TOWN_HINT(SYSTEM_HTML_ADDRESS, "The town/city/suburb of the address."),
    SYSTEM_ADDRESS_REGION_LABEL(SYSTEM_HTML_ADDRESS, "State / county / region"),
    SYSTEM_ADDRESS_REGION_HINT(SYSTEM_HTML_ADDRESS, "The town/city/suburb of the address."),
    SYSTEM_ADDRESS_CODE_LABEL(SYSTEM_HTML_ADDRESS, "Post / zip / area code"),
    SYSTEM_ADDRESS_CODE_HINT(SYSTEM_HTML_ADDRESS, "The post/zip/area code of the address (if applicable)."),
    SYSTEM_ADDRESS_DOMICILE_LABEL(SYSTEM_HTML_ADDRESS, "Country"),
    SYSTEM_ADDRESS_DOMICILE_HINT(SYSTEM_HTML_ADDRESS, "The country of your address."),
    SYSTEM_ADDRESS_CONTACT_SAME_AS_CURRENT_LABEL(SYSTEM_HTML_ADDRESS, "Is this the same as your current address?"),
    SYSTEM_ADDRESS_CONTACT_SAME_AS_CURRENT_HINT(SYSTEM_HTML_ADDRESS,
            "This tells us that your contact address is the same as your current address. If you wish to specify a different address, you may choose to."),

    /*
     * *************** SYSTEM HTML RESOURCE PARENT *********************
     */

    SYSTEM_RESOURCE_PARENT_FORM_BACKGROUND_UPLOAD_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Please Upload a background image to create your landing page"),
    SYSTEM_RESOURCE_PARENT_OPPORTUNITY_TYPE_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Type"),
    SYSTEM_RESOURCE_PARENT_OPPORTUNITY_TYPE_HINT(SYSTEM_HTML_RESOURCE_PARENT, "The type of the opportunity"),
    SYSTEM_RESOURCE_PARENT_INSTITUTION_TITLE_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Organization Name"),
    SYSTEM_RESOURCE_PARENT_INSTITUTION_TITLE_HINT(SYSTEM_HTML_RESOURCE_PARENT, "The Name or your Organization."),
    SYSTEM_RESOURCE_PARENT_DEPARTMENT_TITLE_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Department Name"),
    SYSTEM_RESOURCE_PARENT_DEPARTMENT_TITLE_HINT(SYSTEM_HTML_RESOURCE_PARENT, "The Name or your Department."),
    SYSTEM_RESOURCE_PARENT_DEPARTMENT_IMPORTED_PROGRAMS_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Programs"),
    SYSTEM_RESOURCE_PARENT_DEPARTMENT_IMPORTED_PROGRAMS_HINT(SYSTEM_HTML_RESOURCE_PARENT, "Link the programs that your Department offers so that we identify your students when they sign up"),
    SYSTEM_RESOURCE_PARENT_TITLE_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Title"),
    SYSTEM_RESOURCE_PARENT_TITLE_HINT(SYSTEM_HTML_RESOURCE_PARENT, "The title of your opportunity."),
    SYSTEM_RESOURCE_PARENT_INSTITUTION_SUMMARY_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Summary"),
    SYSTEM_RESOURCE_PARENT_INSTITUTION_SUMMARY_HINT(SYSTEM_HTML_RESOURCE_PARENT, "A brief description of your organization."),
    SYSTEM_RESOURCE_PARENT_SUMMARY_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Summary"),
    SYSTEM_RESOURCE_PARENT_SUMMARY_HINT(SYSTEM_HTML_RESOURCE_PARENT, "A brief description of your opportunity."),
    SYSTEM_RESOURCE_PARENT_USE_OUR_ENGINE_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Use our recruitment engine?"),
    SYSTEM_RESOURCE_PARENT_USE_OUR_ENGINE_HINT(SYSTEM_HTML_RESOURCE_PARENT, "Specify whether you wish to use our recruitment engine, or link to your own."),
    SYSTEM_RESOURCE_PARENT_APPLY_HOMEPAGE_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Application Page URL"),
    SYSTEM_RESOURCE_PARENT_APPLY_HOMEPAGE_HINT(SYSTEM_HTML_RESOURCE_PARENT, "Link to your application page for the opportunity."),
    SYSTEM_RESOURCE_PARENT_TELEPHONE_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Contact Telephone"),
    SYSTEM_RESOURCE_PARENT_TELEPHONE_HINT(SYSTEM_HTML_RESOURCE_PARENT, "Provide us with a telephone number that we can contact you at should we need to."),
    SYSTEM_RESOURCE_PARENT_RESOURCE_CONDITIONS_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Processing Options"),
    SYSTEM_RESOURCE_PARENT_RESOURCE_CONDITIONS_HINT(SYSTEM_HTML_RESOURCE_PARENT, "Configure the processing options that you wish to enable"),
    SYSTEM_RESOURCE_PARENT_ACCEPT_APPLICATION_LABEL(SYSTEM_ADVERTISE_SECTION, "Enable CV Forwarding"),
    SYSTEM_RESOURCE_PARENT_ACCEPT_APPLICATION_HINT(SYSTEM_ADVERTISE_SECTION, "Allow students and graduates to forward their CVs to you"),
    SYSTEM_RESOURCE_PARENT_ACCEPT_DEPARTMENT_LABEL(SYSTEM_ADVERTISE_SECTION, "Enable new Departments?"),
    SYSTEM_RESOURCE_PARENT_ACCEPT_DEPARTMENT_HINT(SYSTEM_ADVERTISE_SECTION, "Allow registered users within your organization to create new Departments"),
    SYSTEM_RESOURCE_PARENT_ACCEPT_PROGRAM_LABEL(SYSTEM_ADVERTISE_SECTION, "Enable new Programs?"),
    SYSTEM_RESOURCE_PARENT_ACCEPT_PROGRAM_HINT(SYSTEM_ADVERTISE_SECTION, "Allow registered users within your organization to create new programs"),
    SYSTEM_RESOURCE_PARENT_ACCEPT_PROJECT_LABEL(SYSTEM_ADVERTISE_SECTION, "Enable new Positions?"),
    SYSTEM_RESOURCE_PARENT_ACCEPT_PROJECT_HINT(SYSTEM_ADVERTISE_SECTION, "Allow registered users within your organization to create new positions"),
    SYSTEM_RESOURCE_PARENT_ACCEPT_PARTNER_LABEL(SYSTEM_ADVERTISE_SECTION, "Allow partnerships"),
    SYSTEM_RESOURCE_PARENT_ACCEPT_PARTNER_HINT(SYSTEM_ADVERTISE_SECTION, "Allow third party organizations to offer positions"),
    SYSTEM_RESOURCE_PARENT_SOCIAL_LINKS(SYSTEM_ADVERTISE_SECTION, "Social Links Placement"),
    SYSTEM_RESOURCE_PARENT_ACCEPT_PUBLIC_MODE(SYSTEM_ADVERTISE_SECTION, "Accept from external organizations"),
    SYSTEM_RESOURCE_PARENT_STUDY_OPTIONS_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Work/Engagement Options"),
    SYSTEM_RESOURCE_PARENT_STUDY_OPTIONS_HINT(SYSTEM_HTML_RESOURCE_PARENT,
            "The modes of participation that your opportunity offers (e.g. full-time, part-time, modular/flexible)."),
    SYSTEM_RESOURCE_PARENT_DURATION_MINIMUM_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Duration Minimum (Months)"),
    SYSTEM_RESOURCE_PARENT_DURATION_MINIMUM_HINT(SYSTEM_HTML_RESOURCE_PARENT, "The minimum anticipated duration of study for your opportunity."),
    SYSTEM_RESOURCE_PARENT_DURATION_MAXIMUM_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Duration Maximum (Months)"),
    SYSTEM_RESOURCE_PARENT_DURATION_MAXIMUM_HINT(SYSTEM_HTML_RESOURCE_PARENT, "The maximum anticipated duration of study for your opportunity."),
    SYSTEM_RESOURCE_PARENT_DEPARTMENT_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Department"),
    SYSTEM_RESOURCE_PARENT_DEPARTMENT_HINT(SYSTEM_HTML_RESOURCE_PARENT, "Specify the Department that this program/position belongs to"),
    SYSTEM_RESOURCE_PARENT_STUDY_LOCATIONS_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Possible Locations"),
    SYSTEM_RESOURCE_PARENT_STUDY_LOCATIONS_HINT(SYSTEM_HTML_RESOURCE_PARENT,
            "The locations at which it is possible for successful applicants of your opportunity to attend at."),
    SYSTEM_RESOURCE_PARENT_BUSINESS_YEAR_START_MONTH_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Business Year Start Month"),
    SYSTEM_RESOURCE_PARENT_BUSINESS_YEAR_START_MONTH_HINT(SYSTEM_HTML_RESOURCE_PARENT,
            "The month of the year that your business year starts from. We use this knowledge to provide you with reports in a format the makes the greatest sense to you."),
    SYSTEM_RESOURCE_PARENT_BUSINESS_CURRENCY_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Currency"),
    SYSTEM_RESOURCE_PARENT_BUSINESS_CURRENCY_HINT(SYSTEM_HTML_RESOURCE_PARENT, "The currency that your organization typically does business in"),
    SYSTEM_RESOURCE_PARENT_MINIMUM_WAGE_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Minimum Hourly Wage"),
    SYSTEM_RESOURCE_PARENT_MINIMUM_WAGE_HINT(SYSTEM_HTML_RESOURCE_PARENT, "Tell us what is the minimum wage that your organization would pay staff and/or work experience personnel. We need to know this so that we can verify that you meet sector requirements for the treatment of work experience personel"),
    SYSTEM_RESOURCE_PARENT_OPPORTUNITY_CATEGORIES_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Opportunity Categories"),
    SYSTEM_RESOURCE_PARENT_OPPORTUNITY_CATEGORIES_HINT(SYSTEM_HTML_RESOURCE_PARENT, "Tell us which opportunity categories are you planning to advertise in future."),
    SYSTEM_RESOURCE_PARENT_INSTITUTION_LOGO_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Logo"),
    SYSTEM_RESOURCE_PARENT_INSTITUTION_LOGO_HINT(SYSTEM_HTML_RESOURCE_PARENT, "Upload the logo for your organization."),
    SYSTEM_RESOURCE_PARENT_RATING_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Rate Employer"),
    SYSTEM_RESOURCE_PARENT_RATING_HINT(SYSTEM_HTML_RESOURCE_PARENT, "To help your students decide whether to pursue work experience or employment with this employer, tell us how you rate them."),
    SYSTEM_RESOURCE_PARENT_PROVIDE_ENDORSEMENT_LABEL(SYSTEM_HTML_RESOURCE_PARENT, "Endorse Employer"),
    SYSTEM_RESOURCE_PARENT_PROVIDE_ENDORSEMENT_HINT(SYSTEM_HTML_RESOURCE_PARENT, "Confirm that you are happy to endorse this employer. Please be aware that declining this request will mean that your students cannot see their advert."),

    /*
     * *************** SYSTEM HTML RESOURCE OPPORTUNITY *********************
     */

    SYSTEM_RESOURCE_OPPORTUNITY_RATING_LABEL(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "Rate Opportunity"),
    SYSTEM_RESOURCE_OPPORTUNITY_RATING_HINT(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "To help your students decide whether to pursue this opportunity, tell us how you rate it."),
    SYSTEM_RESOURCE_OPPORTUNITY_PROVIDE_ENDORSEMENT_LABEL(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "Endorse Opportunity"),
    SYSTEM_RESOURCE_OPPORTUNITY_PROVIDE_ENDORSEMENT_HINT(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "Confirm that you are happy to endorse this opportunity. Please be aware that declining this request will mean that your students cannot apply for it."),
    SYSTEM_RESOURCE_OPPORTUNITY_ACCEPT_APPLICATION_LABEL(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "Enable applications"),
    SYSTEM_RESOURCE_OPPORTUNITY_ACCEPT_APPLICATION_HINT(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "Allow visitors to your advert to submit applications directly to you"),
    SYSTEM_RESOURCE_OPPORTUNITY_ACCEPT_DEPARTMENT_LABEL(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "Enable Departments"),
    SYSTEM_RESOURCE_OPPORTUNITY_ACCEPT_DEPARTMENT_HINT(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "Allow users to create new Departments"),
    SYSTEM_RESOURCE_OPPORTUNITY_ACCEPT_PROGRAM_LABEL(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "Enable programs"),
    SYSTEM_RESOURCE_OPPORTUNITY_ACCEPT_PROGRAM_HINT(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "Allow users to create new programs"),
    SYSTEM_RESOURCE_OPPORTUNITY_ACCEPT_PROJECT_LABEL(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "Enable positions"),
    SYSTEM_RESOURCE_OPPORTUNITY_ACCEPT_PROJECT_HINT(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "Allow users to create new positions"),
    SYSTEM_RESOURCE_OPPORTUNITY_ACCEPT_PARTNER_LABEL(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "Allow partnerships"),
    SYSTEM_RESOURCE_OPPORTUNITY_ACCEPT_PARTNER_HINT(SYSTEM_HTML_RESOURCE_OPPORTUNITY, "Allow third party organizations to offer positions"),

    /*
     * *************** SYSTEM HTML RESOURCE CONFIGURATION *********************
     */

    SYSTEM_RESOURCE_CONFIGURATION_SCOPE_LABEL(SYSTEM_HTML_RESOURCE_CONFIGURATION, "Resource Scope"),
    SYSTEM_RESOURCE_CONFIGURATION_SCOPE_HINT(SYSTEM_HTML_RESOURCE_CONFIGURATION,
            "The scope of the resource (e.g. system, organization, Department, program, position, application) that you wish to define configuration for."),
    SYSTEM_RESOURCE_CONFIGURATION_OPPORTUNITY_TYPE_LABEL(SYSTEM_HTML_RESOURCE_CONFIGURATION, "Opportunity Type"),
    SYSTEM_RESOURCE_CONFIGURATION_OPPORTUNITY_TYPE_HINT(
            SYSTEM_HTML_RESOURCE_CONFIGURATION,
            "The type of the program that you wish to define configuration for. Think about this in terms of the terminology that will make sense for different types of recruitment (e.g. student, intern, employee, etc)."),
    SYSTEM_RESOURCE_CONFIGURATION_OVERRIDE_LOCAL_VERSIONS_BUTTON(SYSTEM_HTML_RESOURCE_CONFIGURATION, "Override Local Versions"),
    SYSTEM_RESOURCE_CONFIGURATION_RESTORE_DEFAULT_BUTTON(SYSTEM_HTML_RESOURCE_CONFIGURATION, "Restore Default"),

    /*
     * *************** SYSTEM RESOURCE TARGETS *********************
     */

    SYSTEM_RESOURCE_TARGETS_INSTITUTIONS_LABEL(SYSTEM_RESOURCE_TARGETS, "Targeted Institutions"),
    SYSTEM_RESOURCE_TARGETS_DEPARTMENTS_LABEL(SYSTEM_RESOURCE_TARGETS, "Targeted Departments"),
    SYSTEM_RESOURCE_TARGETS_UNIVERSITY_PIN_LABEL(SYSTEM_RESOURCE_TARGETS, "University"),
    SYSTEM_RESOURCE_TARGETS_COLLEGE_PIN_LABEL(SYSTEM_RESOURCE_TARGETS, "College"),
    SYSTEM_RESOURCE_TARGETS_FURTHER_DETAILS(SYSTEM_RESOURCE_TARGETS, "Targeting Options"),
    SYSTEM_RESOURCE_TARGETS_MAP_VIEW_HEADER(SYSTEM_RESOURCE_TARGETS, "Map View"),
    SYSTEM_RESOURCE_TARGETS_ORGANIZATION_VIEW_HEADER(SYSTEM_RESOURCE_TARGETS, "Institution Details"),
    SYSTEM_RESOURCE_TARGETS_SELECTED(SYSTEM_RESOURCE_TARGETS, "Selected Targets"),
    SYSTEM_RESOURCE_TARGETS_ADD_MORE(SYSTEM_RESOURCE_TARGETS, "Add More Targets"),
    SYSTEM_RESOURCE_TARGETS_INVITE_TARGET_DEPARTMENT(SYSTEM_RESOURCE_TARGETS, "Invite Target Department"),
    SYSTEM_RESOURCE_TARGETS_TARGET(SYSTEM_RESOURCE_TARGETS, "Target"),
    SYSTEM_RESOURCE_TARGETS_TARGET_INSTITUTION(SYSTEM_RESOURCE_TARGETS, "Target whole Institution"),
    SYSTEM_RESOURCE_TARGETS_SUBJECT_AREA_SEARCH_LABEL(SYSTEM_RESOURCE_TARGETS, "Search by Subject Area"),
    SYSTEM_RESOURCE_TARGETS_RELEVANCE_LABEL(SYSTEM_RESOURCE_TARGETS, "Relevance:"),
    SYSTEM_RESOURCE_TARGETS_SUBJECT_AREA_SEARCH_HINT(SYSTEM_RESOURCE_TARGETS, "e.g. Medicine, Computer Science"),
    SYSTEM_RESOURCE_TARGETS_ORGANIZATION_SEARCH_LABEL(SYSTEM_RESOURCE_TARGETS, "Search by Institution"),
    SYSTEM_RESOURCE_TARGETS_ORGANIZATION_SEARCH_HINT(SYSTEM_RESOURCE_TARGETS, "e.g. University of Oxford, University College London"),
    SYSTEM_RESOURCE_TARGETS_INSTRUCTION(SYSTEM_RESOURCE_TARGETS, "Search for targets by subject area, department or institution name."),
    SYSTEM_RESOURCE_TARGETS_INSTRUCTION_WARNING(SYSTEM_RESOURCE_TARGETS, "Target organizations will be able to see all of your selected targets."),

    /*
     * *************** SYSTEM RESOURCE COMPETENCES *********************
     */

    SYSTEM_RESOURCE_COMPETENCES_NAME_LABEL(SYSTEM_RESOURCE_COMPETENCES, "Competence Name"),
    SYSTEM_RESOURCE_COMPETENCES_NAME_HINT(SYSTEM_RESOURCE_COMPETENCES, "Specify the competences that you are looking for in your applicants."),
    SYSTEM_RESOURCE_COMPETENCES_ESSENTIAL(SYSTEM_RESOURCE_COMPETENCES, "Essential"),
    SYSTEM_RESOURCE_COMPETENCES_EXPECTED(SYSTEM_RESOURCE_COMPETENCES, "Expected"),
    SYSTEM_RESOURCE_COMPETENCES_DESIRABLE(SYSTEM_RESOURCE_COMPETENCES, "Desirable"),

    /*
     * *************** SYSTEM RESOURCE ADVERT *********************
     */

    SYSTEM_ADVERT_DESCRIPTION_LABEL(SYSTEM_RESOURCE_ADVERT, "Description"),
    SYSTEM_ADVERT_DESCRIPTION_HINT(SYSTEM_RESOURCE_ADVERT, "Detailed description of your advert."),
    SYSTEM_ADVERT_HOMEPAGE_LABEL(SYSTEM_RESOURCE_ADVERT, "Homepage URL"),
    SYSTEM_ADVERT_HOMEPAGE_HINT(SYSTEM_RESOURCE_ADVERT, "Specify the web address at which further information about your opportunity can be found"),
    SYSTEM_ADVERT_USE_DIFFERENT_ADDRESS_LABEL(SYSTEM_RESOURCE_ADVERT, "Use different address"),
    SYSTEM_ADVERT_USE_DIFFERENT_ADDRESS_HINT(SYSTEM_RESOURCE_ADVERT,
            "Specify an address here if the address at which your opportunity is based is different from that of your organization"),
    SYSTEM_ADVERT_CATEGORIES_INDUSTRIES_LABEL(SYSTEM_RESOURCE_ADVERT, "Industries"),
    SYSTEM_ADVERT_CATEGORIES_INDUSTRIES_HINT(SYSTEM_RESOURCE_ADVERT, "Specify the industries that your advert is relevant to."),
    SYSTEM_ADVERT_CATEGORIES_FUNCTIONS_LABEL(SYSTEM_RESOURCE_ADVERT, "Functions"),
    SYSTEM_ADVERT_CATEGORIES_FUNCTIONS_HINT(SYSTEM_RESOURCE_ADVERT, "Specify the job functions that your advert is relevant to."),
    SYSTEM_ADVERT_CLOSING_DATES_CLOSING_DATE_LABEL(SYSTEM_RESOURCE_ADVERT, "Closing Date"),
    SYSTEM_ADVERT_CLOSING_DATES_CLOSING_DATE_HINT(SYSTEM_RESOURCE_ADVERT, "The closing date for applications."),

    SYSTEM_FINANCIAL_DETAILS_SPECIFY_FEE_LABEL(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Would you like to specify fees?"),
    SYSTEM_FINANCIAL_DETAILS_SPECIFY_FEE_HINT(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "If you are charging fees (e.g. for study) specify here"),
    SYSTEM_FINANCIAL_DETAILS_FEE_CURRENCY_LABEL(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Fee Currency"),
    SYSTEM_FINANCIAL_DETAILS_FEE_CURRENCY_HINT(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Please specify the default fee currency of your advert."),
    SYSTEM_FINANCIAL_DETAILS_FEE_INTERVAL_LABEL(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Fee Interval"),
    SYSTEM_FINANCIAL_DETAILS_FEE_INTERVAL_HINT(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Please specify how regularly you collect fees"),
    SYSTEM_FINANCIAL_DETAILS_FEE_MINIMUM_LABEL(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Fee minimum"),
    SYSTEM_FINANCIAL_DETAILS_FEE_MINIMUM_HINT(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Please specify the minimum fee level"),
    SYSTEM_FINANCIAL_DETAILS_FEE_MAXIMUM_LABEL(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Fee maximum"),
    SYSTEM_FINANCIAL_DETAILS_FEE_MAXIMUM_HINT(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Please specify the maximum fee level"),
    SYSTEM_FINANCIAL_DETAILS_SPECIFY_PAY_LABEL(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Would you like to specify pay?"),
    SYSTEM_FINANCIAL_DETAILS_SPECIFY_PAY_HINT(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "If you are paying a salary or fee (e.g. for work) specify here"),
    SYSTEM_FINANCIAL_DETAILS_PAY_CURRENCY_LABEL(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Pay Currency"),
    SYSTEM_FINANCIAL_DETAILS_PAY_CURRENCY_HINT(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Please specify the default pay currency of your advert."),
    SYSTEM_FINANCIAL_DETAILS_PAY_INTERVAL_LABEL(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Pay Interval"),
    SYSTEM_FINANCIAL_DETAILS_PAY_INTERVAL_HINT(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Please specify how regularly you make payments"),
    SYSTEM_FINANCIAL_DETAILS_PAY_MINIMUM_LABEL(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Pay minimum"),
    SYSTEM_FINANCIAL_DETAILS_PAY_MINIMUM_HINT(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Please specify the minimum pay level"),
    SYSTEM_FINANCIAL_DETAILS_PAY_MAXIMUM_LABEL(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Pay maximum"),
    SYSTEM_FINANCIAL_DETAILS_PAY_MAXIMUM_HINT(SYSTEM_RESOURCE_FINANCIAL_DETAILS, "Please specify the maximum pay level"),

    /*
     * *************** SYSTEM HTML MANAGE USERS *********************
     */

    SYSTEM_MANAGE_USERS_ROLES_LABEL(SYSTEM_HTML_MANAGE_USERS, "Roles"),
    SYSTEM_MANAGE_USERS_ROLES_HINT(SYSTEM_HTML_MANAGE_USERS, "Select the roles that you wish to assign."),
    SYSTEM_MANAGE_USERS_ADD_USER_BUTTON(SYSTEM_HTML_MANAGE_USERS, "Add User"),
    SYSTEM_MANAGE_USERS_EXISTING_USERS_SUBHEADER(SYSTEM_HTML_MANAGE_USERS, "Existing users and roles"),

    /*
     * *************** SYSTEM HTML EMAIL TEMPLATE CONFIGURATION *********************
     */

    SYSTEM_EMAIL_CONFIGURATION_TEMPLATE_LABEL(SYSTEM_HTML_EMAIL_TEMPLATE_CONFIGURATION, "Email Template"),
    SYSTEM_EMAIL_CONFIGURATION_TEMPLATE_HINT(SYSTEM_HTML_EMAIL_TEMPLATE_CONFIGURATION, "Select the template you wish to configure"),
    SYSTEM_EMAIL_CONFIGURATION_SUBJECT_LABEL(SYSTEM_HTML_EMAIL_TEMPLATE_CONFIGURATION, "Subject"),
    SYSTEM_EMAIL_CONFIGURATION_SUBJECT_HINT(SYSTEM_HTML_EMAIL_TEMPLATE_CONFIGURATION, "Edit to amend the email subject"),
    SYSTEM_EMAIL_CONFIGURATION_CONTENT_LABEL(SYSTEM_HTML_EMAIL_TEMPLATE_CONFIGURATION, "Content"),
    SYSTEM_EMAIL_CONFIGURATION_CONTENT_HINT(SYSTEM_HTML_EMAIL_TEMPLATE_CONFIGURATION, "Edit to amend the email content"),
    SYSTEM_EMAIL_CONFIGURATION_REMINDER_INTERVAL_LABEL(SYSTEM_HTML_EMAIL_TEMPLATE_CONFIGURATION, "Reminder Interval"),
    SYSTEM_EMAIL_CONFIGURATION_REMINDER_INTERVAL_HINT(SYSTEM_HTML_EMAIL_TEMPLATE_CONFIGURATION, "Increase or reduce the frequency of reminder messages"),

    /*
     * *************** SYSTEM HTML WORKFLOW CONFIGURATION *********************
     */

    SYSTEM_WORKFLOW_CONFIGURATION_WORKFLOW_SUBHEADER(SYSTEM_HTML_WORKFLOW_CONFIGURATION, "Workflow Configuration"),
    SYSTEM_WORKFLOW_CONFIGURATION_STAGE_DURATION_SUBHEADER(SYSTEM_HTML_WORKFLOW_CONFIGURATION, "Stage Durations"),
    SYSTEM_WORKFLOW_CONFIGURATION_FIELD_PROPERTY_COLUMN(SYSTEM_HTML_WORKFLOW_CONFIGURATION, "Field Property"),
    SYSTEM_WORKFLOW_CONFIGURATION_ENABLED_COLUMN(SYSTEM_HTML_WORKFLOW_CONFIGURATION, "Enabled"),
    SYSTEM_WORKFLOW_CONFIGURATION_CONFIGURATION_COLUMN(SYSTEM_HTML_WORKFLOW_CONFIGURATION, "Configuration"),
    SYSTEM_WORKFLOW_CONFIGURATION_PROPERTY_COLUMN(SYSTEM_HTML_WORKFLOW_CONFIGURATION, "Property"),
    SYSTEM_WORKFLOW_CONFIGURATION_DURATION_COLUMN(SYSTEM_HTML_WORKFLOW_CONFIGURATION, "Duration"),
    SYSTEM_WORKFLOW_CONFIGURATION_MIN(SYSTEM_HTML_WORKFLOW_CONFIGURATION, "Min"),
    SYSTEM_WORKFLOW_CONFIGURATION_MAX(SYSTEM_HTML_WORKFLOW_CONFIGURATION, "Max"),
    SYSTEM_WORKFLOW_CONFIGURATION_DAYS(SYSTEM_HTML_WORKFLOW_CONFIGURATION, "Days"),
    SYSTEM_WORKFLOW_CONFIGURATION_REQUIRED(SYSTEM_HTML_WORKFLOW_CONFIGURATION, "Required"),

    /*
     * *************** SYSTEM HTML TRANSLATIONS CONFIGURATION *******************
     */

    SYSTEM_TRANSLATIONS_CONFIGURATION_CATEGORY_LABEL(SYSTEM_HTML_TRANSLATIONS_CONFIGURATION, "Category"),
    SYSTEM_TRANSLATIONS_CONFIGURATION_CATEGORY_HINT(SYSTEM_HTML_TRANSLATIONS_CONFIGURATION, "Select the category of property you wish to configure"),

    /*
     * *************** SYSTEM PERFORMANCE INDICATORS *********************
     */

    SYSTEM_PERFORMANCE_INDICATOR_ADVERT_COUNT(SYSTEM_PERFORMANCE_INDICATOR, "Total Adverts Published"),
    SYSTEM_PERFORMANCE_INDICATOR_SUBMITTED_APPLICATION_COUNT(SYSTEM_PERFORMANCE_INDICATOR, "Total Applications Submitted"),
    SYSTEM_PERFORMANCE_INDICATOR_APPROVED_APPLICATION_COUNT(SYSTEM_PERFORMANCE_INDICATOR, "Total Applications Approved"),
    SYSTEM_PERFORMANCE_INDICATOR_REJECTED_APPLICATION_COUNT(SYSTEM_PERFORMANCE_INDICATOR, "Total Applications Rejected"),
    SYSTEM_PERFORMANCE_INDICATOR_WITHDRAWN_APPLICATION_COUNT(SYSTEM_PERFORMANCE_INDICATOR, "Total Applications Withdrawn"),
    SYSTEM_PERFORMANCE_INDICATOR_SUBMITTED_APPLICATION_RATIO(SYSTEM_PERFORMANCE_INDICATOR, "Applications Submitted / Advert"),
    SYSTEM_PERFORMANCE_INDICATOR_APPROVED_APPLICATION_RATIO(SYSTEM_PERFORMANCE_INDICATOR, "Applications Approved / Advert"),
    SYSTEM_PERFORMANCE_INDICATOR_REJECTED_APPLICATION_RATIO(SYSTEM_PERFORMANCE_INDICATOR, "Applications Rejected / Advert"),
    SYSTEM_PERFORMANCE_INDICATOR_WITHDRAWN_APPLICATION_RATIO(SYSTEM_PERFORMANCE_INDICATOR, "Applications Withdrawn / Advert"),
    SYSTEM_PERFORMANCE_INDICATOR_AVERAGE_RATING(SYSTEM_PERFORMANCE_INDICATOR, "Average Applicant Rating (1 - 5)"),
    SYSTEM_PERFORMANCE_INDICATOR_AVERAGE_PROCESSING_TIME(SYSTEM_PERFORMANCE_INDICATOR, "Average Application Processing Time (Days)"),

    /*
     * *************** INSTITUTION COMMENT *********************
     */

    INSTITUTION_COMMENT_APPROVAL(INSTITUTION_COMMENT, "Your Institution has been submitted for approval. We will notify you when it is ready to use"),
    INSTITUTION_COMMENT_APPROVED(INSTITUTION_COMMENT, "We are pleased to tell you that your organization has been approved. You may now login to create other users, Departments, programs and positions, and manage your student/graduate recruitmemt"),
    INSTITUTION_COMMENT_CORRECTION(INSTITUTION_COMMENT, "Further information is required to activate your organization. Please login to address the reviewers comments"),
    INSTITUTION_COMMENT_REJECTED(INSTITUTION_COMMENT, "We are sorry to inform you that your organization has been rejected"),
    INSTITUTION_COMMENT_UPDATED(INSTITUTION_COMMENT, "Updated organization"),
    INSTITUTION_COMMENT_UPDATED_USER_ROLE(INSTITUTION_COMMENT, "Updated organization user roles"),
    INSTITUTION_COMMENT_UPDATED_NOTIFICATION(INSTITUTION_COMMENT, "Updated organization notification configuration"),
    INSTITUTION_COMMENT_RESTORED_NOTIFICATION_CONFIGURATION_DEFAULT(INSTITUTION_COMMENT, "Restored system default notification configuration"),
    INSTITUTION_COMMENT_RESTORED_NOTIFICATION_GLOBAL(INSTITUTION_COMMENT, "Restored organization global notification configuration"),
    INSTITUTION_COMMENT_UPDATED_STATE_DURATION(INSTITUTION_COMMENT, "Updated organization notification configuration"),
    INSTITUTION_COMMENT_RESTORED_STATE_DURATION_DEFAULT(INSTITUTION_COMMENT, "Restored system default state duration configuration"),
    INSTITUTION_COMMENT_RESTORED_STATE_DURATION_GLOBAL(INSTITUTION_COMMENT, "Restored organization global state duration configuration"),
    INSTITUTION_COMMENT_RESTORED_ACTION_PROPERTY_GLOBAL(INSTITUTION_COMMENT, "Restored organization global action property configuration"),
    INSTITUTION_COMMENT_UPDATED_WORKFLOW_PROPERTY(INSTITUTION_COMMENT, "Updated organization workflow property configuration"),
    INSTITUTION_COMMENT_RESTORED_WORKFLOW_PROPERTY_DEFAULT(INSTITUTION_COMMENT, "Restored system default workflow property configuration"),
    INSTITUTION_COMMENT_RESTORED_WORKFLOW_PROPERTY_GLOBAL(INSTITUTION_COMMENT, "Restored organization global workflow property configuration"),
    INSTITUTION_COMMENT_UPDATED_DISPLAY_PROPERTY(INSTITUTION_COMMENT, "Updated organization display property configuration"),
    INSTITUTION_COMMENT_RESTORED_DISPLAY_PROPERTY_DEFAULT(INSTITUTION_COMMENT, "Restored system default display property configuration"),
    INSTITUTION_COMMENT_RESTORED_DISPLAY_PROPERTY_GLOBAL(INSTITUTION_COMMENT, "Restored organization global display property configuration"),
    INSTITUTION_COMMENT_UPDATED_ADVERT(PROGRAM_COMMENT, "Updated organization advert"),
    INSTITUTION_COMMENT_UPDATED_CATEGORY(PROGRAM_COMMENT, "Updated organization categories"),
    INSTITUTION_COMMENT_UPDATED_TARGET(PROGRAM_COMMENT, "Updated organization targets"),
    INSTITUTION_COMMENT_UPDATED_COMPETENCE(PROGRAM_COMMENT, "Updated organizations competences"),
    INSTITUTION_COMMENT_UPDATED_EMAIL_LIST(INSTITUTION_COMMENT, "Updated email lists"),

    /*
     * *************** DEPARTMENT COMMENT *********************
     */

    DEPARTMENT_COMMENT_APPROVAL(DEPARTMENT_COMMENT, "Your Department has been submitted for approval. We will notify you when it is ready to use"),
    DEPARTMENT_COMMENT_APPROVED(DEPARTMENT_COMMENT,
            "We are pleased to tell you that your Department has been approved. You may now login to create other users, programs and positions, and manage your student/graduate recruitmemt"),
    DEPARTMENT_COMMENT_CORRECTION(DEPARTMENT_COMMENT,
            "Further information is required to activate your Department. Please login to address the reviewers comments"),
    DEPARTMENT_COMMENT_REJECTED(DEPARTMENT_COMMENT, "We are sorry to inform you that your Department has been rejected"),
    DEPARTMENT_COMMENT_UPDATED(DEPARTMENT_COMMENT, "Updated Department"),
    DEPARTMENT_COMMENT_UPDATED_USER_ROLE(DEPARTMENT_COMMENT, "Updated Department user roles"),
    DEPARTMENT_COMMENT_UPDATED_NOTIFICATION(DEPARTMENT_COMMENT, "Updated Department notification configuration"),
    DEPARTMENT_COMMENT_RESTORED_NOTIFICATION_DEFAULT(DEPARTMENT_COMMENT, "Restored organization default notification configuration"),
    DEPARTMENT_COMMENT_UPDATED_STATE_DURATION(DEPARTMENT_COMMENT, "Updated Department state duration configuration"),
    DEPARTMENT_COMMENT_RESTORED_STATE_DURATION_DEFAULT(DEPARTMENT_COMMENT, "Restored organization default state duration configuration"),
    DEPARTMENT_COMMENT_UPDATED_WORKFLOW_PROPERTY(DEPARTMENT_COMMENT, "Updated Department workflow property configuration"),
    DEPARTMENT_COMMENT_RESTORED_WORKFLOW_PROPERTY_DEFAULT(DEPARTMENT_COMMENT, "Restored organization default workflow property configuration"),
    DEPARTMENT_COMMENT_UPDATED_DISPLAY_PROPERTY(DEPARTMENT_COMMENT, "Updated Department display property configuration"),
    DEPARTMENT_COMMENT_RESTORED_DISPLAY_PROPERTY_DEFAULT(DEPARTMENT_COMMENT, "Restored organization default display property configuration"),
    DEPARTMENT_COMMENT_UPDATED_ADVERT(DEPARTMENT_COMMENT, "Updated Department advert"),
    DEPARTMENT_COMMENT_UPDATED_CATEGORY(DEPARTMENT_COMMENT, "Updated Department categories"),
    DEPARTMENT_COMMENT_UPDATED_TARGET(PROGRAM_COMMENT, "Updated Department targets"),
    DEPARTMENT_COMMENT_UPDATED_COMPETENCE(PROGRAM_COMMENT, "Updated Department competences"),
    DEPARTMENT_COMMENT_UPDATED_IMPORTED_PROGRAMS(PROGRAM_COMMENT, "Updated Department programs"),
    DEPARTMENT_COMMENT_UPDATED_EMAIL_LIST(DEPARTMENT_COMMENT, "Updated email lists"),

    /*
     * *************** PROGRAM COMMENT *********************
     */

    PROGRAM_COMMENT_APPROVAL(PROGRAM_COMMENT, "Your program has been submitted for approval. We will notify you when it is ready to use"),
    PROGRAM_COMMENT_APPROVED(PROGRAM_COMMENT,
            "We are pleased to tell you that your program has been approved. You may now login to create other users and positions, and manage your student/graduate recruitmemt"),
    PROGRAM_COMMENT_CORRECTION(PROGRAM_COMMENT, "Further information is required to activate your program. Please login to address the reviewers comments"),
    PROGRAM_COMMENT_REJECTED(PROGRAM_COMMENT, "We are sorry to inform you that your program has been rejected"),
    PROGRAM_COMMENT_UPDATED(PROGRAM_COMMENT, "Updated program"),
    PROGRAM_COMMENT_UPDATED_USER_ROLE(PROGRAM_COMMENT, "Updated program user roles"),
    PROGRAM_COMMENT_UPDATED_NOTIFICATION(PROGRAM_COMMENT, "Updated program notification configuration"),
    PROGRAM_COMMENT_RESTORED_NOTIFICATION_DEFAULT(PROGRAM_COMMENT, "Restored Department default notification configuration"),
    PROGRAM_COMMENT_UPDATED_STATE_DURATION(PROGRAM_COMMENT, "Updated program state duration configuration"),
    PROGRAM_COMMENT_RESTORED_STATE_DURATION_DEFAULT(PROGRAM_COMMENT, "Restored Department default state duration configuration"),
    PROGRAM_COMMENT_UPDATED_WORKFLOW_PROPERTY(PROGRAM_COMMENT, "Updated program workflow property configuration"),
    PROGRAM_COMMENT_RESTORED_WORKFLOW_PROPERTY_DEFAULT(PROGRAM_COMMENT, "Restored Department default workflow property configuration"),
    PROGRAM_COMMENT_UPDATED_DISPLAY_PROPERTY(PROGRAM_COMMENT, "Updated program display property configuration"),
    PROGRAM_COMMENT_RESTORED_DISPLAY_PROPERTY_DEFAULT(PROGRAM_COMMENT, "Restored Department default display property configuration"),
    PROGRAM_COMMENT_UPDATED_ADVERT(PROGRAM_COMMENT, "Updated program advert"),
    PROGRAM_COMMENT_UPDATED_CATEGORY(PROGRAM_COMMENT, "Updated program categories"),
    PROGRAM_COMMENT_UPDATED_FEE_AND_PAYMENT(PROGRAM_COMMENT, "Updated program fees and payments"),
    PROGRAM_COMMENT_UPDATED_CLOSING_DATE(PROGRAM_COMMENT, "Updated program closing dates"),
    PROGRAM_COMMENT_UPDATED_TARGET(PROGRAM_COMMENT, "Updated program targets"),
    PROGRAM_COMMENT_UPDATED_COMPETENCE(PROGRAM_COMMENT, "Updated program competences"),
    PROGRAM_COMMENT_UPDATED_EMAIL_LIST(PROGRAM_COMMENT, "Updated email lists"),

    /*
     * *************** PROJECT COMMENT *********************
     */

    PROJECT_COMMENT_APPROVAL(PROJECT_COMMENT, "Your position has been submitted for approval. We will notify you when it is ready to use"),
    PROJECT_COMMENT_APPROVED(PROJECT_COMMENT,
            "We are pleased to tell you that your position has been approved. You may now login to create other users and manage your student/graduate recruitmemt"),
    PROJECT_COMMENT_CORRECTION(PROJECT_COMMENT, "Further information is required to activate your position. Please login to address the reviewers comments"),
    PROJECT_COMMENT_REJECTED(PROJECT_COMMENT, "We are sorry to inform you that your position has been rejected"),
    PROJECT_COMMENT_UPDATED(PROJECT_COMMENT, "Updated position"),
    PROJECT_COMMENT_UPDATED_USER_ROLE(PROJECT_COMMENT, "Updated position user roles"),
    PROJECT_COMMENT_UPDATED_NOTIFICATION(PROGRAM_COMMENT, "Updated position notification configuration"),
    PROJECT_COMMENT_RESTORED_NOTIFICATION_DEFAULT(PROGRAM_COMMENT, "Restored program default notification configuration"),
    PROJECT_COMMENT_UPDATED_STATE_DURATION(PROGRAM_COMMENT, "Updated position state duration configuration"),
    PROJECT_COMMENT_RESTORED_STATE_DURATION_DEFAULT(PROGRAM_COMMENT, "Restored program default state duration configuration"),
    PROJECT_COMMENT_UPDATED_WORKFLOW_PROPERTY(PROGRAM_COMMENT, "Updated position workflow property configuration"),
    PROJECT_COMMENT_RESTORED_WORKFLOW_PROPERTY_DEFAULT(PROGRAM_COMMENT, "Restored program default workflow property configuration"),
    PROJECT_COMMENT_UPDATED_DISPLAY_PROPERTY(PROGRAM_COMMENT, "Updated position display property configuration"),
    PROJECT_COMMENT_RESTORED_DISPLAY_PROPERTY_DEFAULT(PROGRAM_COMMENT, "Restored program default display property configuration"),
    PROJECT_COMMENT_UPDATED_ADVERT(PROJECT_COMMENT, "Updated position advert"),
    PROJECT_COMMENT_UPDATED_CATEGORY(PROJECT_COMMENT, "Updated position categories"),
    PROJECT_COMMENT_UPDATED_FEE_AND_PAYMENT(PROJECT_COMMENT, "Updated position fees and payments"),
    PROJECT_COMMENT_UPDATED_CLOSING_DATE(PROJECT_COMMENT, "Updated position closing dates"),
    PROJECT_COMMENT_UPDATED_TARGET(PROJECT_COMMENT, "Updated position targets"),
    PROJECT_COMMENT_UPDATED_COMPETENCE(PROGRAM_COMMENT, "Updated position competences"),
    PROJECT_COMMENT_UPDATED_EMAIL_LIST(PROJECT_COMMENT, "Updated email lists"),

    /*
     * *************** APPLICATION GLOBAL *********************
     */

    APPLICATION_HEADER(APPLICATION_GLOBAL, "Application"),
    APPLICATION_CREATOR(APPLICATION_GLOBAL, "Applicant"),
    APPLICATION_PROOF_OF_AWARD(APPLICATION_GLOBAL, "Proof of Award"),
    APPLICATION_CONFIRMED_START_DATE(APPLICATION_GLOBAL, "Confirmed Start Date"),
    APPLICATION_CONFIRMED_OFFER_TYPE(APPLICATION_GLOBAL, "Confirmed Offer Type"),
    APPLICATION_OFFER_CONDITIONAL(APPLICATION_GLOBAL, "Conditional"),
    APPLICATION_OFFER_UNCONDITIONAL(APPLICATION_GLOBAL, "Unconditional"),
    APPLICATION_PREFERRED_START_DATE(APPLICATION_GLOBAL, "Preferred Start Date"),
    APPLICATION_REFEREES(APPLICATION_GLOBAL, "Referees"),
    APPLICATION_PROVIDED_REFERENCES(APPLICATION_GLOBAL, "Provided References"),
    APPLICATION_DECLINED_REFERENCES(APPLICATION_GLOBAL, "Declined References"),
    APPLICATION_SUBMISSION_DATE(APPLICATION_GLOBAL, "Submission Date"),
    APPLICATION_CODE(APPLICATION_GLOBAL, "Application Code"),
    APPLICATION_LAST_EDITED(APPLICATION_GLOBAL, "Last Edited"),

    /*
     * *************** APPLICATION FORM *********************
     */

    APPLICATION_PROGRAM_DETAIL_HEADER(APPLICATION_PROGRAM_DETAIL, "Application Detail"),
    APPLICATION_PROGRAM_DETAIL_DESCRIPTION(APPLICATION_PROGRAM_DETAIL,
            "This section allows you to provide details about the program, position or organization that you are applying for."),
    APPLICATION_PROGRAM_DETAIL_PROGRAM_HINT(APPLICATION_PROGRAM_DETAIL, "The PRiSM Program that you are applying for."),
    APPLICATION_PROGRAM_DETAIL_PROJECT_HINT(APPLICATION_PROGRAM_DETAIL, "The PRiSM Position that you are applying for."),
    APPLICATION_PROGRAM_DETAIL_STUDY_OPTION_LABEL(APPLICATION_PROGRAM_DETAIL, "Mode of Engagement"),
    APPLICATION_PROGRAM_DETAIL_STUDY_OPTION_HINT(APPLICATION_PROGRAM_DETAIL, "Your preferred mode of engagement (e.g. full-time, part-time, flexible)."),
    APPLICATION_PROGRAM_DETAIL_START_DATE_LABEL(APPLICATION_PROGRAM_DETAIL, "Start Date"),
    APPLICATION_PROGRAM_DETAIL_START_DATE_HINT(APPLICATION_PROGRAM_DETAIL,
            "The date that you expect to start your study on, if successful in your application."),

    APPLICATION_PERSONAL_DETAIL_HEADER(APPLICATION_PERSONAL_DETAIL, "Personal Details"),
    APPLICATION_PERSONAL_DETAIL_DESCRIPTION(APPLICATION_PERSONAL_DETAIL, "This section allows you to provide details about yourself."),
    APPLICATION_PERSONAL_DETAIL_TITLE_LABEL(APPLICATION_PERSONAL_DETAIL, "Title"),
    APPLICATION_PERSONAL_DETAIL_TITLE_HINT(APPLICATION_PERSONAL_DETAIL, "The title that you wish us to address you by."),
    APPLICATION_PERSONAL_DETAIL_GENDER_LABEL(APPLICATION_PERSONAL_DETAIL, "Gender"),
    APPLICATION_PERSONAL_DETAIL_GENDER_HINT(
            APPLICATION_PERSONAL_DETAIL,
            "Your gender. We collect this infomration to comply with UK equal opportunities legislation. The information that you provide will not be considering during shortlisting and will not affect the outcome of your application."),
    APPLICATION_PERSONAL_DETAIL_DATE_OF_BIRTH_LABEL(APPLICATION_PERSONAL_DETAIL, "Date of Birth"),
    APPLICATION_PERSONAL_DETAIL_DATE_OF_BIRTH_HINT(
            APPLICATION_PERSONAL_DETAIL,
            "Your date of birth. We collect this information to comply with UK equal opportunities legislation. The information that you provide will not be considering during shortlisting and will not affect the outcome of your application."),
    APPLICATION_PERSONAL_DETAIL_NATIONALITIES_SUBHEADER(APPLICATION_PERSONAL_DETAIL, "Nationalities"),
    APPLICATION_PERSONAL_DETAIL_NATIONALITY_LABEL(APPLICATION_PERSONAL_DETAIL, "Nationality"),
    APPLICATION_PERSONAL_DETAIL_FIRST_NATIONALITY_LABEL(APPLICATION_PERSONAL_DETAIL, "Primary Nationality"),
    APPLICATION_PERSONAL_DETAIL_FIRST_NATIONALITY_HINT(
            APPLICATION_PERSONAL_DETAIL,
            "Select your primary nationality. We collect this information to comply with UK equal opportunities legislation. The information that you provide will not be considering during shortlisting and will not affect the outcome of your application."),
    APPLICATION_PERSONAL_DETAIL_RESIDENCE_HEADER(APPLICATION_PERSONAL_DETAIL, "Residence"),
    APPLICATION_PERSONAL_DETAIL_DOMICILE_LABEL(APPLICATION_PERSONAL_DETAIL, "Country of Residence"),
    APPLICATION_PERSONAL_DETAIL_DOMICILE_HINT(
            APPLICATION_PERSONAL_DETAIL,
            "The country in which you are ordinarily (e.g. normally) resident. Be aware that this may not be the country that you currently live in. We collect this information to comply with UK equal opportunities legislation. The information that you provide will not be considering during shortlisting and will not affect the outcome of your application."),
    APPLICATION_PERSONAL_DETAIL_VISA_REQUIRED_LABEL(APPLICATION_PERSONAL_DETAIL, "Do you Require a Visa to Study in the UK?"),
    APPLICATION_PERSONAL_DETAIL_VISA_REQUIRED_HINT(
            APPLICATION_PERSONAL_DETAIL,
            "Tell us whether you need a visa to study in the UK. We collect this information to comply with UK equal opportunities legislation. The information that you provide will not be considering during shortlisting and will not affect the outcome of your application."),
    APPLICATION_PERSONAL_DETAIL_CONTACT_DETAIL_HEADER(APPLICATION_PERSONAL_DETAIL, "Contact Details"),
    APPLICATION_PERSONAL_DETAIL_EMAIL_LABEL(APPLICATION_PERSONAL_DETAIL, "Email"),
    APPLICATION_PERSONAL_DETAIL_EMAIL_HINT(APPLICATION_PERSONAL_DETAIL, "Your email address."),
    APPLICATION_PERSONAL_DETAIL_TELEPHONE_LABEL(APPLICATION_PERSONAL_DETAIL, "Telephone Number"),
    APPLICATION_PERSONAL_DETAIL_TELEPHONE_HINT(APPLICATION_PERSONAL_DETAIL, "Your contact telephone number."),
    APPLICATION_PERSONAL_DETAIL_SKYPE_LABEL(APPLICATION_PERSONAL_DETAIL, "Skype"),
    APPLICATION_PERSONAL_DETAIL_SKYPE_HINT(APPLICATION_PERSONAL_DETAIL, "Your Skype address. We may you skype to contact you."),
    APPLICATION_PERSONAL_DETAIL_EQUAL_OPPORTUNITIES_HEADER(APPLICATION_PERSONAL_DETAIL, "Equal Opportunities"),
    APPLICATION_PERSONAL_DETAIL_ETHNICITY_LABEL(APPLICATION_PERSONAL_DETAIL, "Ethnicity"),
    APPLICATION_PERSONAL_DETAIL_ETHNICITY_HINT(
            APPLICATION_PERSONAL_DETAIL,
            "Please specify your ethnic background. We have to ask you for this to comply with UK equal opportunities legislation. The information that you provide will not be considered during shortlisting and will not affect the outcome of your application."),
    APPLICATION_PERSONAL_DETAIL_DISABILITY_LABEL(APPLICATION_PERSONAL_DETAIL, "Disability"),
    APPLICATION_PERSONAL_DETAIL_DISABILITY_HINT(
            APPLICATION_PERSONAL_DETAIL,
            "Please let us know if you have a recognised disability. We have to ask you for this to comply with UK equal opportunities legislation. The information that you provide will not be considered during shortlisting and will not affect the outcome of your application."),

    APPLICATION_ADDRESS_HEADER(APPLICATION_ADDRESS, "Address Detail"),
    APPLICATION_ADDRESS_DESCRIPTION(APPLICATION_ADDRESS, "Your current residence and contact address(es)."),
    APPLICATION_ADDRESS_CURRENT_HEADER(APPLICATION_ADDRESS, "Current Address"),
    APPLICATION_ADDRESS_CONTACT_HEADER(APPLICATION_ADDRESS, "Contact Address"),
    APPLICATION_ADDRESS_CONTACT_SAME_AS_CURRENT_LABEL(APPLICATION_ADDRESS, "Is this the same as your current address?"),
    APPLICATION_ADDRESS_CONTACT_SAME_AS_CURRENT_HINT(APPLICATION_ADDRESS,
            "This tells us that your contact address is the same as your current address. If you wish to specify a different address, you may choose to."),

    APPLICATION_QUALIFICATION_HEADER(APPLICATION_QUALIFICATION, "Qualifications"),
    APPLICATION_QUALIFICATION_DESCRIPTION(
            APPLICATION_QUALIFICATION,
            "This section allows you to provide details about your qualifications. Please only provide details of those qualifications that are relevant to your application."),
    APPLICATION_QUALIFICATION_SUBHEADER(APPLICATION_QUALIFICATION, "Qualification"),
    APPLICATION_QUALIFICATION_PROVIDER_PROGRAM_LABEL(APPLICATION_QUALIFICATION, "Provider & Program"),
    APPLICATION_QUALIFICATION_PROVIDER_PROGRAM_HINT(APPLICATION_QUALIFICATION, "The provider of the qualification, and the program that you are following/completed."),
    APPLICATION_QUALIFICATION_START_DATE_LABEL(APPLICATION_QUALIFICATION, "Start Date"),
    APPLICATION_QUALIFICATION_START_DATE_HINT(APPLICATION_QUALIFICATION, "The start date of study."),
    APPLICATION_QUALIFICATION_COMPLETED_LABEL(APPLICATION_QUALIFICATION, "Has this qualification been awarded?"),
    APPLICATION_QUALIFICATION_COMPLETED_HINT(APPLICATION_QUALIFICATION, "This tells us that the qualification has been awarded."),
    APPLICATION_QUALIFICATION_EXPECTED_RESULT_LABEL(APPLICATION_QUALIFICATION, "Expected Grade/Result/GPA"),
    APPLICATION_QUALIFICATION_CONFIRMED_RESULT_LABEL(APPLICATION_QUALIFICATION, "Confirmed Grade/Result/GPA"),
    APPLICATION_QUALIFICATION_RESULT_HINT(APPLICATION_QUALIFICATION, "Your score/grade/result (expected or confirmed)."),
    APPLICATION_QUALIFICATION_RESULT_PLACEHOLDER(APPLICATION_QUALIFICATION, "e.g. 2.1, Distinction"),
    APPLICATION_QUALIFICATION_EXPECTED_AWARD_DATE_LABEL(APPLICATION_QUALIFICATION, "Expected Award Date"),
    APPLICATION_QUALIFICATION_CONFIRMED_AWARD_DATE_LABEL(APPLICATION_QUALIFICATION, "Confirmed Award Date"),
    APPLICATION_QUALIFICATION_AWARD_DATE_HINT(APPLICATION_QUALIFICATION, "The award date of the qualification (expected or confirmed)."),
    APPLICATION_QUALIFICATION_DOCUMENT_LABEL(APPLICATION_QUALIFICATION, "Interim/Final Transcript (PDF)"),
    APPLICATION_QUALIFICATION_DOCUMENT_HINT(APPLICATION_QUALIFICATION,
            "Proof of the award of your progression towards the qualification. We require a complete grade transcript for university level qualifications."),
    APPLICATION_QUALIFICATION_STUDY_PERIOD_LABEL(APPLICATION_QUALIFICATION, "Study Period"),
    APPLICATION_QUALIFICATION_APPENDIX(APPLICATION_QUALIFICATION, "Qualification Transcript"),
    APPLICATION_QUALIFICATION_NO_ITEMS(APPLICATION_QUALIFICATION, "This Application doesn't contain any Qualification"),
    APPLICATION_QUALIFICATION_EQUIVALENT_HEADER(APPLICATION_QUALIFICATION, "Equivalent Experience"),
    APPLICATION_QUALIFICATION_EXPERIENCE_MESSAGE(
            APPLICATION_QUALIFICATION,
            "We consider that the applicant has experience equivalent to the typical academic entrance requirements for our program. It is therefore our recommendation that an appointment be made"),

    APPLICATION_EMPLOYMENT_POSITION_HEADER(APPLICATION_EMPLOYMENT_POSITION, "Employment Positions"),
    APPLICATION_EMPLOYMENT_POSITION_DESCRIPTION(APPLICATION_EMPLOYMENT_POSITION,
            "This section allows you to provide details about your employment history. Please only provide details that are relevant to your application."),
    APPLICATION_EMPLOYMENT_POSITION_SUBHEADER(APPLICATION_EMPLOYMENT_POSITION, "Position Details"),
    APPLICATION_EMPLOYMENT_POSITION_EMPLOYER_LABEL(APPLICATION_REFEREE, "Position & Employer"),
    APPLICATION_EMPLOYMENT_POSITION_EMPLOYER_HINT(APPLICATION_REFEREE, "The position that you held, and the organization that employed/employs you."),
    APPLICATION_EMPLOYMENT_POSITION_START_DATE_LABEL(APPLICATION_EMPLOYMENT_POSITION, "Start Date"),
    APPLICATION_EMPLOYMENT_POSITION_START_DATE_HINT(APPLICATION_EMPLOYMENT_POSITION, "The start date of the position."),
    APPLICATION_EMPLOYMENT_POSITION_CURRENT_LABEL(APPLICATION_EMPLOYMENT_POSITION, "Is this your current position?"),
    APPLICATION_EMPLOYMENT_POSITION_CURRENT_HINT(APPLICATION_EMPLOYMENT_POSITION, "This tells us that the position is an ongoing one."),
    APPLICATION_EMPLOYMENT_POSITION_END_DATE_LABEL(APPLICATION_EMPLOYMENT_POSITION, "End Date"),
    APPLICATION_EMPLOYMENT_POSITION_END_DATE_HINT(APPLICATION_EMPLOYMENT_POSITION, "The end date of the position."),
    APPLICATION_EMPLOYMENT_POSITION_PERIOD_LABEL(APPLICATION_QUALIFICATION, "Employment Period"),

    APPLICATION_REFEREE_HEADER(APPLICATION_REFEREE, "Referees"),
    APPLICATION_REFEREE_DESCRIPTION(APPLICATION_REFEREE,
            "Specify your referee(s) here. Should you be considered for appointment, each referee will be asked to provide a statement in support of your application."),
    APPLICATION_REFEREE_SUBHEADER(APPLICATION_REFEREE, "Referee"),
    APPLICATION_REFEREE_POSITION_DETAILS_SUBHEADER(APPLICATION_REFEREE, "Position Details"),
    APPLICATION_REFEREE_POSITION_EMPLOYER_LABEL(APPLICATION_REFEREE, "Postion & Employer"),
    APPLICATION_REFEREE_POSITION_EMPLOYER_HINT(APPLICATION_REFEREE, "The position that your referee holds, and the organization that employs them."),
    APPLICATION_REFEREE_PHONE_LABEL(APPLICATION_EMPLOYMENT_POSITION, "Telephone"),
    APPLICATION_REFEREE_PHONE_HINT(APPLICATION_EMPLOYMENT_POSITION, "The employer telephone number of the referee."),
    APPLICATION_REFEREE_SKYPE_LABEL(APPLICATION_EMPLOYMENT_POSITION, "Skype user name"),
    APPLICATION_REFEREE_SKYPE_HINT(APPLICATION_EMPLOYMENT_POSITION, "The Skype address of the referee."),
    APPLICATION_REFEREE_REFERENCE_APPENDIX(APPLICATION_REFEREE, "Reference"),
    APPLICATION_REFEREE_REFERENCE_COMMENT(APPLICATION_REFEREE, "Reference Comment"),
    APPLICATION_REFEREE_REFERENCE_COMMENT_EQUIVALENT(
            APPLICATION_REFEREE,
            "Having considered the whole application, including both written and spoken feedback from referees, we are happy to make an appointment based upon the information available to us"),

    APPLICATION_DOCUMENT_HEADER(APPLICATION_DOCUMENT, "Supporting Information"),
    APPLICATION_DOCUMENT_PERSONAL_SUMMARY_LABEL(APPLICATION_DOCUMENT, "Personal Summary"),
    APPLICATION_DOCUMENT_PERSONAL_SUMMARY_HINT(APPLICATION_DOCUMENT,
            "A concise overview of your skills, personal characteristics and achievements. Take your time with this. Employers use it to differentiate between applicants. It is your chance to promote yourself."),
    APPLICATION_DOCUMENT_CV_LABEL(APPLICATION_DOCUMENT, "CV / Resume (PDF)"),
    APPLICATION_DOCUMENT_CV_HINT(APPLICATION_DOCUMENT,
            "Your CV/resume. This should summarise your ambitions, and academic and professionl achievements. Please provide no more than 2 pages of A4."),
    APPLICATION_DOCUMENT_COVERING_LETTER_LABEL(APPLICATION_DOCUMENT, "Covering Letter (PDF)"),
    APPLICATION_DOCUMENT_COVERING_LETTER_HINT(APPLICATION_DOCUMENT,
            "Your covering letter. This should describe why your are suitable for the opportunity. Please provide no more than 2 pages of A4."),

    APPLICATION_ADDITIONAL_INFORMATION_HEADER(APPLICATION_ADDITIONAL_INFORMATION, "Additional Information"),
    APPLICATION_ADDITIONAL_INFORMATION_HAS_CONVICTION_LABEL(APPLICATION_ADDITIONAL_INFORMATION, "Do you have any unspent Criminal Convictions?"),
    APPLICATION_ADDITIONAL_INFORMATION_HAS_CONVICTION_HINT(APPLICATION_ADDITIONAL_INFORMATION,
            "Please tell us whether you have any unspent criminal convictions."),
    APPLICATION_ADDITIONAL_INFORMATION_CONVICTION_LABEL(APPLICATION_ADDITIONAL_INFORMATION, "Unspent Criminal Convictions"),
    APPLICATION_ADDITIONAL_INFORMATION_CONVICTION_HINT(APPLICATION_ADDITIONAL_INFORMATION, "Provide a short summary of each of your unspent convictions."),
    APPLICATION_ADDITIONAL_INFORMATION_EMPTY(APPLICATION_ADDITIONAL_INFORMATION, "No additional information is been provided for this Application"),

    APPLICATION_FORM_FOOTER_CONFIRM_TRUE(
            APPLICATION_FORM,
            "Confirm that the information that you have provided in this form is true and correct.<br/><em>Failure to provide true and correct information may result in a subsequent offer of study being withdrawn.</em>"),
    APPLICATION_FORM_FOOTER_KEEP_RECORD(APPLICATION_FORM,
            "Keep my application on record indefinitely.<br/><em>Uncheck this if you want your application to be destroyed when we have finished processing it.</em>"),
    APPLICATION_FORM_FOOTER_SEND_RECOMMENDATIONS(APPLICATION_FORM,
            "Send me recommendations about other opportunities to apply for.<br/><em>Uncheck this if you do not want us to send you recommendations.</em>"),
    APPLICATION_FORM_GO_TO_TIMELINE(APPLICATION_FORM, "Go to Timeline"),
    APPLICATION_FORM_SAVE_FOR_LATER(APPLICATION_FORM, "Save for later"),
    APPLICATION_FORM_SUBMIT_APPLICATION(APPLICATION_FORM, "Submit Application"),

    /*
     * *************** APPLICATION ACTION *********************
     */

    APPLICATION_ACTION_APPOINTMENT_PREFERENCES_SUBHEADER(APPLICATION_ACTION, "Please select the dates when you are available."),
    APPLICATION_ACTION_APPOINTMENT_PREFERENCES_SUBHEADER_BACKOUT(APPLICATION_ACTION, "Please tell us why you cannot make it and when we can reschedule."),
    APPLICATION_ACTION_CONFIRM_INTERVIEW_ARRANGEMENTS_SUBHEADER(APPLICATION_ACTION, "Please select the date when interview will take place."),
    APPLICATION_ACTION_COMMENT_CONTENT_LABEL(APPLICATION_ACTION, "Comments"),
    APPLICATION_ACTION_COMMENT_CONTENT_HINT(APPLICATION_ACTION, "Enter your comments here. The comments cannot be seen by the applicant"),
    APPLICATION_ACTION_DOCUMENTS_LABEL(APPLICATION_ACTION, "Attach Document (PDF)"),
    APPLICATION_ACTION_DOCUMENTS_HINT(APPLICATION_ACTION, "If you wish, you may attach documents (PDF) to your comments. The file size limit is 2Mb."),
    APPLICATION_ACTION_INTERESTED_IN_APPLICANT_LABEL(APPLICATION_ACTION, "Interested in this applicant?"),
    APPLICATION_ACTION_INTERESTED_IN_APPLICANT_HINT(APPLICATION_ACTION, "Tell us whether you would like to consider the applicant as a potential student."),
    APPLICATION_ACTION_RATING_LABEL(APPLICATION_ACTION, "How do they rate?"),
    APPLICATION_ACTION_RATING_HINT(APPLICATION_ACTION,
            "Specify how highly you rate the applicant in comparison to other applicants that you have seen, with 5 stars being the highest rating and 0 the lowest."),
    APPLICATION_ACTION_ELIGIBLE_LABEL(APPLICATION_ACTION, "Is the applicant Eligible?"),
    APPLICATION_ACTION_ELIGIBLE_HINT(APPLICATION_ACTION, "Assess whether the applicant is eligible."),
    APPLICATION_ACTION_SELECT_REFEREE_LABEL(APPLICATION_ACTION, "Referee"),
    APPLICATION_ACTION_SELECT_REFEREE_HINT(APPLICATION_ACTION, "Select the referee that you wish to upload a reference for"),
    APPLICATION_ACTION_PROVIDE_REFERENCE_LABEL(APPLICATION_ACTION, "Are you happy to provide a reference?"),
    APPLICATION_ACTION_PROVIDE_REFERENCE_HINT(APPLICATION_ACTION, "Confirm that you are happy to provide a reference. You may decline if you wish"),
    APPLICATION_ACTION_TRANSITION_STATE_LABEL(APPLICATION_ACTION, "Next Task"),
    APPLICATION_ACTION_TRANSITION_STATE_HINT(APPLICATION_ACTION, "Select the next task that you wish to perform."),
    APPLICATION_ACTION_SECONDARY_STATES_LABEL(APPLICATION_ACTION, "Other Tasks"),
    APPLICATION_ACTION_SECONDARY_STATES_HINT(APPLICATION_ACTION, "Select other tasks that you wish to perform in parallel."),
    APPLICATION_ACTION_SECONDARY_STATE_CURRENTLY_RUNNING(APPLICATION_ACTION, "(Currently Running)"),
    APPLICATION_ACTION_ASSIGN_REVIEWERS_LABEL(APPLICATION_ACTION, "Assign Reviewers"),
    APPLICATION_ACTION_ASSIGN_REVIEWERS_HINT(APPLICATION_ACTION, "Select the users you wish to assign and add them to the task list."),
    APPLICATION_ACTION_ASSIGN_INTERVIEWERS_LABEL(APPLICATION_ACTION, "Assign Interviewers"),
    APPLICATION_ACTION_ASSIGN_INTERVIEWERS_HINT(APPLICATION_ACTION, "Select the users you wish to assign and add them to the task list."),
    APPLICATION_ACTION_ASSIGN_HIRING_MANAGERS_LABEL(APPLICATION_ACTION, "Assign Hiring Managers"),
    APPLICATION_ACTION_ASSIGN_HIRING_MANAGERS_HINT(APPLICATION_ACTION, "Select the users you wish to assign and add them to the task list."),
    APPLICATION_ACTION_INTERVIEW_STATUS_LABEL(APPLICATION_ACTION, "Interview Status"),
    APPLICATION_ACTION_INTERVIEW_STATUS_HINT(APPLICATION_ACTION, "Specify whether the interview has taken place, been scheduled, or needs to be scheduled."),
    APPLICATION_ACTION_INTERVIEW_STATUS_TAKEN_PLACE(APPLICATION_ACTION, "Taken place"),
    APPLICATION_ACTION_INTERVIEW_STATUS_SCHEDULED(APPLICATION_ACTION, "Scheduled"),
    APPLICATION_ACTION_INTERVIEW_STATUS_TO_BE_SCHEDULED(APPLICATION_ACTION, "To be scheduled"),
    APPLICATION_ACTION_INTERVIEW_DATE_LABEL(APPLICATION_ACTION, "Interview Date"),
    APPLICATION_ACTION_INTERVIEW_DATE_HINT(APPLICATION_ACTION, "Specify the Date for the interview."),
    APPLICATION_ACTION_INTERVIEW_TIME_LABEL(APPLICATION_ACTION, "Interview Time"),
    APPLICATION_ACTION_INTERVIEW_TIME_HINT(APPLICATION_ACTION, "Specify what is the time of the interview."),
    APPLICATION_ACTION_INTERVIEW_PREFERRED_DATES_LABEL(APPLICATION_ACTION, "Preferred Dates"),
    APPLICATION_ACTION_INTERVIEW_PREFERRED_DATES_HINT(APPLICATION_ACTION, "Specify your preferred interview slots."),
    APPLICATION_ACTION_INTERVIEW_INTERVIEWER_INSTRUCTIONS_LABEL(APPLICATION_ACTION, "Interview Instructions"),
    APPLICATION_ACTION_INTERVIEW_INTERVIEWER_INSTRUCTIONS_HINT(APPLICATION_ACTION, "Specify any instructions for the interviewers."),
    APPLICATION_ACTION_INTERVIEW_INTERVIEWEE_INSTRUCTIONS_LABEL(APPLICATION_ACTION, "Interview Instructions (Applicant)"),
    APPLICATION_ACTION_INTERVIEW_INTERVIEWEE_INSTRUCTIONS_HINT(APPLICATION_ACTION, "Specify any interview instructions for the applicant."),
    APPLICATION_ACTION_INTERVIEW_LOCATION_LABEL(APPLICATION_ACTION, "Interview Location (URL)"),
    APPLICATION_ACTION_INTERVIEW_LOCATION_HINT(APPLICATION_ACTION, "Specify the interview location."),
    APPLICATION_ACTION_INTERVIEW_LOCATION_PLACEHOLDER(APPLICATION_ACTION, "e.g. http://www.ucl.ac.uk/locations/ucl-maps/"),
    APPLICATION_ACTION_INTERVIEW_TIMEZONE_LABEL(APPLICATION_ACTION, "Time Zone"),
    APPLICATION_ACTION_INTERVIEW_TIMEZONE_HINT(APPLICATION_ACTION, "Specify what timezone the interview will take place in."),
    APPLICATION_ACTION_INTERVIEW_DURATION_LABEL(APPLICATION_ACTION, "Interview Duration"),
    APPLICATION_ACTION_INTERVIEW_DURATION_HINT(APPLICATION_ACTION, "Specify what timezone the interview will take place in."),
    APPLICATION_ACTION_RECRUITER_ACCEPT_APPOINTMENT_LABEL(APPLICATION_ACTION, "Confirm that you are willing to provide primary supervision"),
    APPLICATION_ACTION_RECRUITER_ACCEPT_APPOINTMENT_HINT(
            APPLICATION_ACTION,
            "Confirm that you are willing to provide primary supervision. You will be able to suggest amendments to the proposed research programme when you have provided a positive confirmation."),
    APPLICATION_ACTION_RECRUITER_ACCEPT_APPOINTMENT_CONFIRM(APPLICATION_ACTION, "Confirm"),
    APPLICATION_ACTION_RECRUITER_ACCEPT_APPOINTMENT_DECLINE(APPLICATION_ACTION, "Decline"),
    APPLICATION_ACTION_DECLINE_SUPERVISION_REASON_LABEL(APPLICATION_ACTION, "Reason"),
    APPLICATION_ACTION_DECLINE_SUPERVISION_REASON_HINT(APPLICATION_ACTION, "Explain why you wish to decline to provide primary supervision."),
    APPLICATION_ACTION_POSITION_TITLE_LABEL(APPLICATION_ACTION, "Position Title"),
    APPLICATION_ACTION_POSITION_TITLE_HINT(APPLICATION_ACTION, "Enter the position title."),
    APPLICATION_ACTION_POSITION_DESCRIPTION_LABEL(APPLICATION_ACTION, "Position Abstract"),
    APPLICATION_ACTION_POSITION_DESCRIPTION_LABEL_HINT(
            APPLICATION_ACTION,
            "Enter a concise description of the position. This will constitute the 'ATAS Statement' if required by the applicant. It should summarise the scope and possible applications of the research that will take place. The applicant must agree with your description, as they will be required to provide an identical description in their ATAS Certificate Applicaton. We request an appropriate position description for all research admissions as a matter of policy."),
    APPLICATION_ACTION_POSITION_PROVISIONAL_START_DATE_LABEL(APPLICATION_ACTION, "Provisional Start Date"),
    APPLICATION_ACTION_POSITION_PROVISIONAL_START_DATE_HINT(
            APPLICATION_ACTION,
            "The applicant's provisional start date. Prism generates a default value for provisional start date based upon the applicant's application. If you wish to amend the value you may do so."),
    APPLICATION_ACTION_POSITION_OFFER_TYPE_LABEL(APPLICATION_ACTION, "Recommended Offer Type"),
    APPLICATION_ACTION_POSITION_OFFER_TYPE_HINT(APPLICATION_ACTION, "Specify whether you wish to recommend an unconditional or conditional offer."),
    APPLICATION_ACTION_POSITION_OFFER_TYPE_UNCONDITIONAL(APPLICATION_ACTION, "Unconditional"),
    APPLICATION_ACTION_POSITION_OFFER_TYPE_CONDITIONAL(APPLICATION_ACTION, "Conditional"),
    APPLICATION_ACTION_POSITION_APPOINTMENT_CONDITIONS_LABEL(APPLICATION_ACTION, "Recommended conditions"),
    APPLICATION_ACTION_POSITION_APPOINTMENT_CONDITIONS_HINT(APPLICATION_ACTION,
            "If you wish to recommend any specific conditions to the study offer you may do so here."),
    APPLICATION_ACTION_REJECTION_REASON_LABEL(APPLICATION_ACTION, "Reasons for Rejection"),
    APPLICATION_ACTION_REJECTION_REASON_HINT(APPLICATION_ACTION, "Specify the pertinent reason for rejection."),
    APPLICATION_ACTION_RESERVE_STATUS_LABEL(APPLICATION_ACTION, "Priority status"),
    APPLICATION_ACTION_RESERVE_STATUS_HINT(APPLICATION_ACTION,
            "Indicate whether you wish you wish to assign the applicant to the first, second or third reserve group"),
    APPLICATION_ACTION_DELEGATE_ADMINISTRATION_BUTTON(APPLICATION_ACTION, "Delegate Administration"),
    APPLICATION_ACTION_CANNOT_MAKE_IT_BUTTON(APPLICATION_ACTION, "I cannot make it"),
    APPLICATION_ACTION_START_AGAIN_BUTTON(APPLICATION_ACTION, "Start Again"),

    /*
     * *************** APPLICATION COMMENT *********************
     */

    APPLICATION_COMMENT_DECLINED_REFEREE(APPLICATION_COMMENT, "Declined to provide a reference"),
    APPLICATION_COMMENT_RECOMMENDED_OFFER_CONDITION(APPLICATION_COMMENT, "Recommended offer conditions"),
    APPLICATION_COMMENT_POSITION_DESCRIPTION_UNEXPORTABLE(APPLICATION_COMMENT, "Refer to program administrator for position description"),
    APPLICATION_COMMENT_REJECTION_SYSTEM(APPLICATION_COMMENT, "We are currently unable to offer you a position"),
    APPLICATION_COMMENT_DIRECTIONS(APPLICATION_COMMENT, "Directions"),
    APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED(APPLICATION_COMMENT, "No directions provided. Please contact the interviewer for further information"),
    APPLICATION_COMMENT_UPDATED_PROGRAM_DETAIL(APPLICATION_COMMENT, "Updated the application details section"),
    APPLICATION_COMMENT_UPDATED_PERSONAL_DETAIL(APPLICATION_COMMENT, "Updated the personal detail section"),
    APPLICATION_COMMENT_UPDATED_ADDRESS(APPLICATION_COMMENT, "Updated the address section"),
    APPLICATION_COMMENT_UPDATED_QUALIFICATION(APPLICATION_COMMENT, "Updated the qualification section"),
    APPLICATION_COMMENT_UPDATED_EMPLOYMENT(APPLICATION_COMMENT, "Updated the employment section"),
    APPLICATION_COMMENT_UPDATED_REFEREE(APPLICATION_COMMENT, "Updated the referee section"),
    APPLICATION_COMMENT_UPDATED_DOCUMENT(APPLICATION_COMMENT, "Updated the document section"),
    APPLICATION_COMMENT_UPDATED_ADDITIONAL_INFORMATION(APPLICATION_COMMENT, "Updated the additional information section"),

    /*
     * *************** APPLICATION REPORT *********************
     */

    APPLICATION_VERIFICATION_INSTANCE_COUNT(APPLICATION_REPORT, "Verification State Count"),
    APPLICATION_VERIFICATION_INSTANCE_DURATION_AVERAGE(APPLICATION_REPORT, "Verification Duration Average"),
    APPLICATION_REFERENCE_INSTANCE_COUNT(APPLICATION_REPORT, "Reference State Count"),
    APPLICATION_REFERENCE_INSTANCE_DURATION_AVERAGE(APPLICATION_REPORT, "Reference Duration Average"),
    APPLICATION_REVIEW_INSTANCE_COUNT(APPLICATION_REPORT, "Review State Count"),
    APPLICATION_REVIEW_INSTANCE_DURATION_AVERAGE(APPLICATION_REPORT, "Review State Duration Average"),
    APPLICATION_INTERVIEW_INSTANCE_COUNT(APPLICATION_REPORT, "Interview State Count"),
    APPLICATION_INTERVIEW_INSTANCE_DURATION_AVERAGE(APPLICATION_REPORT, "Interview State Duration Average"),
    APPLICATION_APPROVAL_INSTANCE_COUNT(APPLICATION_REPORT, "Approval State Count"),
    APPLICATION_APPROVAL_INSTANCE_DURATION_AVERAGE(APPLICATION_REPORT, "Approval State Duration Average");

    private static ListMultimap<PrismDisplayPropertyCategory, PrismDisplayPropertyDefinition> propertiesByCategory = ArrayListMultimap.create(4, 1000);

    private static ListMultimap<PrismScope, PrismDisplayPropertyDefinition> propertiesByScope = ArrayListMultimap.create(4, 1000);

    static {
        for (PrismDisplayPropertyDefinition propertyDefinition : PrismDisplayPropertyDefinition.values()) {
            PrismDisplayPropertyCategory category = propertyDefinition.getCategory();
            propertiesByCategory.put(category, propertyDefinition);
            propertiesByScope.put(category.getScope(), propertyDefinition);
        }
    }

    private PrismDisplayPropertyCategory category;

    private String defaultValue;

    PrismDisplayPropertyDefinition(PrismDisplayPropertyCategory category, String defaultValue) {
        this.category = category;
        this.defaultValue = defaultValue;
    }

    public static List<PrismDisplayPropertyDefinition> getProperties(PrismDisplayPropertyCategory category) {
        return propertiesByCategory.get(category);
    }

    public static List<PrismDisplayPropertyDefinition> getProperties(PrismScope scope) {
        return propertiesByScope.get(scope);
    }

    public static void main(String[] args) {
        System.out.println(PrismDisplayPropertyDefinition.values().length);
    }

    @Override
    public PrismDisplayPropertyCategory getCategory() {
        return category;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

}
