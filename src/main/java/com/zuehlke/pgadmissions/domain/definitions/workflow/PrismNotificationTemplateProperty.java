package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.*;

public enum PrismNotificationTemplateProperty {

    USER(GLOBAL, "get", "user", "displayName"),
    USER_FIRST_NAME(GLOBAL, "get", "user", "firstName"),
    USER_LAST_NAME(GLOBAL, "get", "user", "lastName"),
    USER_EMAIL(GLOBAL, "get", "user", "email"),
    USER_ACTIVATION_CODE(GLOBAL, "get", "user", "activationCode"),
    AUTHOR(GLOBAL, "get", "sender", "displayName"),
    AUTHOR_EMAIL(GLOBAL, "get", "sender", "email"),

    INVOKER(COMMENT, "get", "comment", "user", "displayName"),
    INVOKER_EMAIL(COMMENT, "get", "comment", "user", "email"),
    OUTCOME(COMMENT, "getCommentOutcome"),
    DATE_TIME(COMMENT, "getCommentDateTime"),

    APPLICANT(APPLICATION, "get", "resource", "application", "user", "displayName"),
    APPLICATION_CODE(APPLICATION, "get", "resource", "application", "code"),
    PROJECT_OR_PROGRAM_TITLE(APPLICATION, "getProjectOrProgramTitle"),
    PROJECT_OR_PROGRAM_CODE(APPLICATION, "getPropertyOrProgramCode"),
    STUDY_OPTION(APPLICATION, "get", "resource", "application", "programDetail", "studyOption", "name"),

    REJECTION_REASON(APPLICATION_REJECTION, "getRejectionReason"),

    PROGRAM_CODE(PROGRAM, "get", "resource", "program", "code"),
    PROGRAM_TITLE(PROGRAM, "get", "resource", "program", "title"),

    PROJECT_CODE(PROJECT, "get", "resource", "project", "code"),
    PROJECT_TITLE(PROJECT, "get", "resource", "project", "title"),

    INSTITUTION_CODE(INSTITUTION, "get", "resource", "institution", "code"),
    INSTITUTION_TITLE(INSTITUTION, "get", "resource", "institution", "title"),
    INSTITUTION_HOMEPAGE_LINK(INSTITUTION, "getInstitutionHomepageLink"),

    INTERVIEW_DATE_TIME(INTERVIEW_SCHEDULED, "getInterviewDateTime"),
    INTERVIEW_TIME_ZONE(INTERVIEW_SCHEDULED, "getInterviewTimeZone"),
    INTERVIEWER_INSTRUCTIONS(INTERVIEW_SCHEDULED, "get", "comment", "interviewerInstructions"),
    INTERVIEWEE_INSTRUCTIONS(INTERVIEW_SCHEDULED, "getIntervieweeInstructions"),

    ACTION_CONTROL(ACTION, "getActionControl"),
    ACTIVATE_ACCOUNT_CONTROL(ACCOUNT_ACTIVATION, "getActivateAccountControl"),
    HOMEPAGE_CONTROL(GLOBAL, "getHomepageControl"),
    VIEW_EDIT_CONTROL(GLOBAL, "getViewEditControl"),
    INTERVIEW_LOCATION_CONTROL(INTERVIEW_SCHEDULED, "getInterviewDirectionsControl"),
    HELPDESK_CONTROL(GLOBAL, "getHelpdeskControl"),

    NEW_PASSWORD(com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.NEW_PASSWORD, "get", "newPassword"),

    RECOMMENDATIONS(RECOMMENDATION, "get", "recommendations"),
    ERROR_MESSAGE(ERROR, "get", "errorMessage"),

    SYSTEM_NAME(GLOBAL, "get", "resource", "system", "title");

    private PrismNotificationTemplatePropertyCategory category;

    private String getterMethod;

    private String[] methodArguments;

    private static ListMultimap<PrismNotificationTemplatePropertyCategory, PrismNotificationTemplateProperty> categoriesToProperties = LinkedListMultimap.create();

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
