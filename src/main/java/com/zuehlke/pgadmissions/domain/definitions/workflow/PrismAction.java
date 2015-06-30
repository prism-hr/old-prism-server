package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.EMAIL_RESOURCE_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.ESCALATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.EXPORT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.IMPORT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.INITIALISE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.MANAGE_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.PROCESS_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.PROPAGATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.PURGE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.SPONSOR_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_EDIT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_RESOURCE_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.WITHDRAW_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition.APPLICATION_COMPLETE_CUSTOM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_CUSTOM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition.APPLICATION_PROVIDE_REFERENCE_CUSTOM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition.APPLICATION_PROVIDE_REVIEW_CUSTOM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType.ALL_ASSESSMENT_CONTENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType.ALL_CONTENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType.SYSTEM_INVOCATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType.USER_INVOCATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum PrismAction {

    APPLICATION_ASSIGN_INTERVIEWERS(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_ASSIGN_REVIEWERS(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_ASSIGN_SUPERVISORS(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_RESERVE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, null),
    APPLICATION_COMMENT(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_COMPLETE(USER_INVOCATION, VIEW_EDIT_RESOURCE, false, false, true, APPLICATION_COMPLETE_CUSTOM, APPLICATION, null), //
    APPLICATION_COMPLETE_VALIDATION_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_COMPLETE_VERIFICATION_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_COMPLETE_REFERENCE_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_COMPLETE_REVIEW_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_COMPLETE_INTERVIEW_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_COMPLETE_APPROVAL_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_COMPLETE_APPROVED_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_COMPLETE_RESERVED_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_COMPLETE_REJECTED_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_CONFIRM_ELIGIBILITY(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION,
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION,
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, null), //
    APPLICATION_CONFIRM_REJECTION(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, null), //
    APPLICATION_CONFIRM_PRIMARY_SUPERVISION(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION,
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_CONFIRM_SECONDARY_SUPERVISION(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION,
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_CORRECT(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, null), //
    APPLICATION_FORGET_EXPORT(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, null), //
    APPLICATION_EMAIL_CREATOR(USER_INVOCATION, EMAIL_RESOURCE_CREATOR, false, false, false, null, APPLICATION, null), //
    APPLICATION_ESCALATE(SYSTEM_INVOCATION, ESCALATE_RESOURCE, false, false, false, null, APPLICATION, null), //
    APPLICATION_EXPORT(SYSTEM_INVOCATION, EXPORT_RESOURCE, false, false, true, null, APPLICATION,
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_CONTENT),
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_RECRUITER).withRedactionType(ALL_CONTENT),
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT),
                    new PrismActionRedaction().withRole(INSTITUTION_ADMITTER).withRedactionType(ALL_CONTENT),
                    new PrismActionRedaction().withRole(PROGRAM_ADMINISTRATOR).withRedactionType(ALL_CONTENT),
                    new PrismActionRedaction().withRole(PROGRAM_APPROVER).withRedactionType(ALL_CONTENT),
                    new PrismActionRedaction().withRole(PROGRAM_VIEWER).withRedactionType(ALL_CONTENT),
                    new PrismActionRedaction().withRole(PROJECT_ADMINISTRATOR).withRedactionType(ALL_CONTENT),
                    new PrismActionRedaction().withRole(PROJECT_PRIMARY_SUPERVISOR).withRedactionType(ALL_CONTENT))),
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK(USER_INVOCATION, PROCESS_RESOURCE, true, false, true, APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_CUSTOM, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_PROVIDE_REFERENCE(USER_INVOCATION, PROCESS_RESOURCE, true, true, true, APPLICATION_PROVIDE_REFERENCE_CUSTOM, APPLICATION,
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_PROVIDE_REVIEW(USER_INVOCATION, PROCESS_RESOURCE, true, false, true, APPLICATION_PROVIDE_REVIEW_CUSTOM, APPLICATION,
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_PURGE(SYSTEM_INVOCATION, PURGE_RESOURCE, false, false, true, null, APPLICATION, null), //
    APPLICATION_REVERSE_REJECTION(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_UPLOAD_REFERENCE(USER_INVOCATION, PROCESS_RESOURCE, true, true, true, APPLICATION_PROVIDE_REFERENCE_CUSTOM, APPLICATION,
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_VIEW_EDIT(USER_INVOCATION, VIEW_EDIT_RESOURCE, false, false, true, null, APPLICATION, //
            Arrays.asList(new PrismActionRedaction().withRole(APPLICATION_CREATOR).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_POTENTIAL_INTERVIEWEE).withRedactionType(ALL_ASSESSMENT_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_REFEREE).withRedactionType(ALL_CONTENT), //
                    new PrismActionRedaction().withRole(APPLICATION_VIEWER_REFEREE).withRedactionType(ALL_CONTENT))), //
    APPLICATION_WITHDRAW(USER_INVOCATION, WITHDRAW_RESOURCE, false, false, true, null, APPLICATION, null), //
    INSTITUTION_COMPLETE_APPROVAL_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, INSTITUTION, null), //
    INSTITUTION_VIEW_EDIT(USER_INVOCATION, VIEW_EDIT_RESOURCE, false, false, false, null, INSTITUTION, null), //
    INSTITUTION_CORRECT(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, INSTITUTION, null), //
    INSTITUTION_CREATE_PROGRAM(USER_INVOCATION, CREATE_RESOURCE, false, false, true, null, INSTITUTION, null), //
    INSTITUTION_CREATE_PROJECT(USER_INVOCATION, CREATE_RESOURCE, false, false, true, null, INSTITUTION, null), //
    INSTITUTION_CREATE_APPLICATION(USER_INVOCATION, CREATE_RESOURCE, false, false, true, null, INSTITUTION, null), //
    INSTITUTION_EMAIL_CREATOR(USER_INVOCATION, EMAIL_RESOURCE_CREATOR, false, false, false, null, INSTITUTION, null), //
    INSTITUTION_ESCALATE(SYSTEM_INVOCATION, ESCALATE_RESOURCE, false, false, false, null, INSTITUTION, null), //
    INSTITUTION_IMPORT_PROGRAM(SYSTEM_INVOCATION, IMPORT_RESOURCE, false, false, false, null, INSTITUTION, null), //
    INSTITUTION_PROVIDE_SPONSORSHIP(USER_INVOCATION, SPONSOR_RESOURCE, false, false, true, null, INSTITUTION, null), //
    INSTITUTION_WITHDRAW(USER_INVOCATION, WITHDRAW_RESOURCE, false, false, true, null, INSTITUTION, null), //
    PROGRAM_STARTUP(SYSTEM_INVOCATION, PROPAGATE_RESOURCE, false, false, true, null, PROGRAM, null), //
    PROGRAM_COMPLETE_APPROVAL_PARTNER_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, PROGRAM, null), //
    PROGRAM_COMPLETE_APPROVAL_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, PROGRAM, null), //
    PROGRAM_VIEW_EDIT(USER_INVOCATION, VIEW_EDIT_RESOURCE, false, false, false, null, PROGRAM, null), //
    PROGRAM_CORRECT(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, PROGRAM, null), //
    PROGRAM_CREATE_APPLICATION(USER_INVOCATION, CREATE_RESOURCE, false, false, true, null, PROGRAM, null), //
    PROGRAM_CREATE_PROJECT(USER_INVOCATION, CREATE_RESOURCE, false, false, true, null, PROGRAM, null), //
    PROGRAM_EMAIL_CREATOR(USER_INVOCATION, EMAIL_RESOURCE_CREATOR, false, false, false, null, PROGRAM, null), //
    PROGRAM_ESCALATE(SYSTEM_INVOCATION, ESCALATE_RESOURCE, false, false, false, null, PROGRAM, null), //
    PROGRAM_RESTORE(SYSTEM_INVOCATION, PROCESS_RESOURCE, false, false, false, null, PROGRAM, null), //
    PROGRAM_PROVIDE_SPONSORSHIP(USER_INVOCATION, SPONSOR_RESOURCE, false, false, true, null, PROGRAM, null), //
    PROGRAM_WITHDRAW(USER_INVOCATION, WITHDRAW_RESOURCE, false, false, true, null, PROGRAM, null), //
    PROJECT_STARTUP(SYSTEM_INVOCATION, PROPAGATE_RESOURCE, false, false, true, null, PROJECT, null), //
    PROJECT_COMPLETE_APPROVAL_PARTNER_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, PROJECT, null), //
    PROJECT_COMPLETE_APPROVAL_STAGE(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, PROJECT, null), //
    PROJECT_VIEW_EDIT(USER_INVOCATION, VIEW_EDIT_RESOURCE, false, false, false, null, PROJECT, null), //
    PROJECT_CORRECT(USER_INVOCATION, PROCESS_RESOURCE, false, false, true, null, PROJECT, null), //
    PROJECT_CREATE_APPLICATION(USER_INVOCATION, CREATE_RESOURCE, false, false, true, null, PROJECT, null), //
    PROJECT_EMAIL_CREATOR(USER_INVOCATION, EMAIL_RESOURCE_CREATOR, false, false, false, null, PROJECT, null), //
    PROJECT_ESCALATE(SYSTEM_INVOCATION, ESCALATE_RESOURCE, false, false, false, null, PROJECT, null), //
    PROJECT_RESTORE(SYSTEM_INVOCATION, PROPAGATE_RESOURCE, false, false, false, null, PROJECT, null), //
    PROJECT_PROVIDE_SPONSORSHIP(USER_INVOCATION, SPONSOR_RESOURCE, false, false, true, null, PROJECT, null), //
    PROJECT_SUSPEND(SYSTEM_INVOCATION, PROPAGATE_RESOURCE, false, false, true, null, PROJECT, null), //
    PROJECT_TERMINATE(SYSTEM_INVOCATION, PROPAGATE_RESOURCE, false, false, true, null, PROJECT, null), //
    PROJECT_WITHDRAW(USER_INVOCATION, WITHDRAW_RESOURCE, false, false, true, null, PROJECT, null), //
    SYSTEM_VIEW_EDIT(USER_INVOCATION, VIEW_EDIT_RESOURCE, false, false, false, null, SYSTEM, null), //
    SYSTEM_CREATE_INSTITUTION(USER_INVOCATION, CREATE_RESOURCE, false, false, true, null, SYSTEM, null), //
    SYSTEM_STARTUP(SYSTEM_INVOCATION, INITIALISE_RESOURCE, false, false, true, null, SYSTEM, null), //
    SYSTEM_MANAGE_ACCOUNT(USER_INVOCATION, MANAGE_ACCOUNT, false, false, false, null, SYSTEM, null), //
    SYSTEM_VIEW_APPLICATION_LIST(USER_INVOCATION, VIEW_RESOURCE_LIST, false, false, false, null, SYSTEM, null), //
    SYSTEM_VIEW_INSTITUTION_LIST(USER_INVOCATION, VIEW_RESOURCE_LIST, false, false, false, null, SYSTEM, null), //
    SYSTEM_VIEW_PROGRAM_LIST(USER_INVOCATION, VIEW_RESOURCE_LIST, false, false, false, null, SYSTEM, null), //
    SYSTEM_VIEW_PROJECT_LIST(USER_INVOCATION, VIEW_RESOURCE_LIST, false, false, false, null, SYSTEM, null);

    private PrismActionType actionType;

    private PrismActionCategory actionCategory;

    private boolean ratingAction;

    private boolean declinableAction;

    private boolean visibleAction;

    private PrismActionCustomQuestionDefinition actionCustomQuestion;

    private PrismScope scope;

    private List<PrismActionRedaction> redactions;

    private PrismAction(PrismActionType actionType, PrismActionCategory actionCategory, boolean ratingAction, boolean declinableAction, boolean visibleAction,
            PrismActionCustomQuestionDefinition actionCustomQuestion, PrismScope scope, List<PrismActionRedaction> redactions) {
        this.actionType = actionType;
        this.actionCategory = actionCategory;
        this.ratingAction = ratingAction;
        this.declinableAction = declinableAction;
        this.visibleAction = visibleAction;
        this.actionCustomQuestion = actionCustomQuestion;
        this.scope = scope;
        this.redactions = redactions;
    }

    public PrismActionType getActionType() {
        return actionType;
    }

    public PrismActionCategory getActionCategory() {
        return actionCategory;
    }

    public boolean isRatingAction() {
        return ratingAction;
    }

    public boolean isDeclinableAction() {
        return declinableAction;
    }

    public boolean isVisibleAction() {
        return visibleAction;
    }

    public PrismActionCustomQuestionDefinition getActionCustomQuestion() {
        return actionCustomQuestion;
    }

    public PrismScope getScope() {
        return scope;
    }

    public List<PrismActionRedaction> getRedactions() {
        return redactions == null ? Collections.<PrismActionRedaction> emptyList() : redactions;
    }

}
