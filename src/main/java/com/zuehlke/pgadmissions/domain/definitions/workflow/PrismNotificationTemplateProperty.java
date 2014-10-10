package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.ACTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.APPLICATION_REJECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.CONFIRM_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.ERROR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.NEW_PASSWORD;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.RECOMMENDATION;

import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

public enum PrismNotificationTemplateProperty {

    USER(GLOBAL, "get", "user", "displayName"),
    USER_FIRST_NAME(GLOBAL, "get", "user", "firstName"),
    USER_LAST_NAME(GLOBAL, "get", "user", "lastName"),
    USER_EMAIL(GLOBAL, "get", "user", "email"),
    USER_ACTIVATION_CODE(GLOBAL, "get", "user", "activationCode"),
    AUTHOR(GLOBAL, "get", "sender", "displayName"),
    AUTHOR_EMAIL(GLOBAL, "get", "sender", "email"),

    APPLICANT(APPLICATION, "get", "resource", "application", "user", "displayName"),
    APPLICATION_CODE(APPLICATION, "get", "resource", "application", "code"),
    PROJECT_OR_PROGRAM_TITLE(APPLICATION, "getProjectOrProgramTitle"),
    PROJECT_OR_PROGRAM_CODE(APPLICATION, "getPropertyOrProgramCode"),
    
    REJECTION_REASON(APPLICATION_REJECTION, "getRejectionReason"),

    PROGRAM_CODE(PROGRAM, "get", "resource", "program", "code"),
    PROGRAM_TITLE(PROGRAM, "get", "resource", "program", "title"),

    PROJECT_CODE(PROJECT, "get", "resource", "project", "code"),
    PROJECT_TITLE(PROJECT, "get", "resource", "project", "title"),

    INSTITUTION_CODE(INSTITUTION, "get", "resource", "institution", "code"),
    INSTITUTION_TITLE(INSTITUTION, "get", "resource", "institution", "title"),

    ACTION_CONTROL(ACTION, "getActionControl"),
    DIRECTIONS_CONTROL(CONFIRM_INTERVIEW, "getDirectionsControl"),
    VIEW_EDIT_CONTROL(GLOBAL, "getViewEditControl"),
    NEW_PASSWORD_CONTROL(NEW_PASSWORD, "getNewPasswordControl"),

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
