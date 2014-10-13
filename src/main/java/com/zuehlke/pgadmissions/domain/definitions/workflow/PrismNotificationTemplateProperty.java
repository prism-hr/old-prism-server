package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.APPLICATION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.APPLICATION_RECOMMENDATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.APPLICATION_REJECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.APPLICATION_SUBMITTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.INSTITUTION_DATA_IMPORT_ERROR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.INSTITUTION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.INTERVIEW_SCHEDULED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.PROGRAM_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.PROJECT_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.SYSTEM_ACCOUNT_ACTIVATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.SYSTEM_ACTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.SYSTEM_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.SYSTEM_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.SYSTEM_PASSWORD;

import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

public enum PrismNotificationTemplateProperty {

    USER(SYSTEM_GLOBAL, "get", "user", "displayName"), //
    USER_FIRST_NAME(SYSTEM_GLOBAL, "get", "user", "firstName"), //
    USER_LAST_NAME(SYSTEM_GLOBAL, "get", "user", "lastName"), //
    USER_EMAIL(SYSTEM_GLOBAL, "get", "user", "email"), //
    USER_ACTIVATION_CODE(SYSTEM_GLOBAL, "get", "user", "activationCode"), //
    AUTHOR(SYSTEM_GLOBAL, "get", "sender", "displayName"), //
    AUTHOR_EMAIL(SYSTEM_GLOBAL, "get", "sender", "email"), //
    INVOKER(SYSTEM_COMMENT, "get", "comment", "user", "displayName"), //
    INVOKER_EMAIL(SYSTEM_COMMENT, "get", "comment", "user", "email"), //
    OUTCOME(SYSTEM_COMMENT, "getCommentOutcome"), //
    DATE_TIME(SYSTEM_COMMENT, "getCommentDateTime"), //
    APPLICANT(APPLICATION_GLOBAL, "get", "resource", "application", "user", "displayName"), //
    APPLICATION_CODE(APPLICATION_GLOBAL, "get", "resource", "application", "code"), //
    PROJECT_OR_PROGRAM_TITLE(APPLICATION_GLOBAL, "getProjectOrProgramTitle"), //
    PROJECT_OR_PROGRAM_CODE(APPLICATION_GLOBAL, "getProjectOrProgramCode"), //
    STUDY_OPTION(APPLICATION_SUBMITTED, "get", "resource", "application", "programDetail", "studyOption", "name"), //
    REJECTION_REASON(APPLICATION_REJECTION, "getRejectionReason"), //
    PROGRAM_CODE(PROGRAM_GLOBAL, "get", "resource", "program", "code"), //
    PROGRAM_TITLE(PROGRAM_GLOBAL, "get", "resource", "program", "title"), //
    PROJECT_CODE(PROJECT_GLOBAL, "get", "resource", "project", "code"), //
    PROJECT_TITLE(PROJECT_GLOBAL, "get", "resource", "project", "title"), //
    INSTITUTION_CODE(INSTITUTION_GLOBAL, "get", "resource", "institution", "code"), //
    INSTITUTION_TITLE(INSTITUTION_GLOBAL, "get", "resource", "institution", "title"), //
    INSTITUTION_HOMEPAGE_LINK(INSTITUTION_GLOBAL, "getInstitutionHomepageLink"), //
    INTERVIEW_DATE_TIME(INTERVIEW_SCHEDULED, "getInterviewDateTime"), //
    INTERVIEW_TIME_ZONE(INTERVIEW_SCHEDULED, "getInterviewTimeZone"), //
    INTERVIEWER_INSTRUCTIONS(INTERVIEW_SCHEDULED, "get", "comment", "interviewerInstructions"), //
    INTERVIEWEE_INSTRUCTIONS(INTERVIEW_SCHEDULED, "getIntervieweeInstructions"), //
    ACTION_CONTROL(SYSTEM_ACTION, "getActionControl"), //
    ACTIVATE_ACCOUNT_CONTROL(SYSTEM_ACCOUNT_ACTIVATION, "getActivateAccountControl"), //
    HOMEPAGE_CONTROL(SYSTEM_GLOBAL, "getHomepageControl"), //
    VIEW_EDIT_CONTROL(SYSTEM_GLOBAL, "getViewEditControl"), //
    INTERVIEW_LOCATION_CONTROL(INTERVIEW_SCHEDULED, "getInterviewDirectionsControl"), //
    HELPDESK_CONTROL(SYSTEM_GLOBAL, "getHelpdeskControl"), //
    NEW_PASSWORD(SYSTEM_PASSWORD, "get", "newPassword"), //
    RECOMMENDATIONS(APPLICATION_RECOMMENDATION, "get", "recommendations"), //
    ERROR_MESSAGE(INSTITUTION_DATA_IMPORT_ERROR, "get", "errorMessage"), //
    SYSTEM_NAME(SYSTEM_GLOBAL, "get", "resource", "system", "title");

    private PrismNotificationTemplatePropertyCategory category;

    private String getterMethod;

    private String[] methodArguments;

    private static ListMultimap<PrismNotificationTemplatePropertyCategory, PrismNotificationTemplateProperty> categoriesToProperties = LinkedListMultimap
            .create();

    static {
        for (PrismNotificationTemplateProperty property : PrismNotificationTemplateProperty.values()) {
            categoriesToProperties.put(property.getCategory(), property);
        }
    }

    PrismNotificationTemplateProperty(PrismNotificationTemplatePropertyCategory category, String getterMethod, String... methodArguments) {
        this.category = category;
        this.getterMethod = getterMethod;
        this.methodArguments = methodArguments;
    }

    public PrismNotificationTemplatePropertyCategory getCategory() {
        return category;
    }

    public String getGetterMethod() {
        return getterMethod;
    }

    public String[] getMethodArguments() {
        return methodArguments;
    }

    public static List<PrismNotificationTemplateProperty> getProperties(PrismNotificationTemplatePropertyCategory category) {
        return categoriesToProperties.get(category);
    }
}
