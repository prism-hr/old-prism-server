package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.ACTION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.APPLICATION_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.APPLICATION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.APPLICATION_INTERVIEW_SCHEDULED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.APPLICATION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.COMMENT_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.COMMENT_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.INSTITUTION_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.INSTITUTION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.PROGRAM_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.PROJECT_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_APPLICATION_MARKETING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_APPLICATION_SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_INSTITUTION_SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_PROGRAM_SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_PROJECT_SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_USER_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_USER_PASSWORD;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.TEMPLATE_GLOBAL;

import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

public enum PrismNotificationDefinitionProperty {

    TEMPLATE_USER_FULL_NAME(TEMPLATE_GLOBAL, true), //
    TEMPLATE_USER_FIRST_NAME(TEMPLATE_GLOBAL, true), //
    TEMPLATE_USER_LAST_NAME(TEMPLATE_GLOBAL, true), //
    TEMPLATE_USER_EMAIL(TEMPLATE_GLOBAL, true), //
    TEMPLATE_USER_ACTIVATION_CODE(TEMPLATE_GLOBAL, true), //
    TEMPLATE_AUTHOR_FULL_NAME(TEMPLATE_GLOBAL, true), //
    TEMPLATE_AUTHOR_EMAIL(TEMPLATE_GLOBAL, true), //
    TEMPLATE_INVOKER_FULL_NAME(TEMPLATE_GLOBAL, true), //
    TEMPLATE_INVOKER_EMAIL(TEMPLATE_GLOBAL, true), //
    TEMPLATE_SYSTEM_TITLE(TEMPLATE_GLOBAL, true), //
    TEMPLATE_SYSTEM_HOMEPAGE(TEMPLATE_GLOBAL, true), //
    TEMPLATE_HELPDESK(TEMPLATE_GLOBAL, false), //
    ACTION_VIEW_EDIT(ACTION_GLOBAL, false), //
    ACTION_COMPLETE(ACTION_GLOBAL, false), //
    COMMENT_CONTENT(COMMENT_GLOBAL, true), //
    COMMENT_DATE_TIME(COMMENT_GLOBAL, true), //
    COMMENT_TRANSITION_OUTCOME(COMMENT_TRANSITION, true), //
    APPLICATION_CREATOR_FULL_NAME(APPLICATION_GLOBAL, true), //
    APPLICATION_CODE(APPLICATION_GLOBAL, true), //
    APPLICATION_PROJECT_OR_PROGRAM_TITLE(APPLICATION_GLOBAL, true), //
    APPLICATION_PROJECT_OR_PROGRAM_CODE(APPLICATION_GLOBAL, true), //
    APPLICATION_PROGRAM_TYPE(APPLICATION_GLOBAL, true), //
    APPLICATION_INTERVIEW_DATE_TIME(APPLICATION_INTERVIEW_SCHEDULED, true), //
    APPLICATION_INTERVIEW_TIME_ZONE(APPLICATION_INTERVIEW_SCHEDULED, true), //
    APPLICATION_INTERVIEWER_INSTRUCTIONS(APPLICATION_INTERVIEW_SCHEDULED, true), //
    APPLICATION_INTERVIEWEE_INSTRUCTIONS(APPLICATION_INTERVIEW_SCHEDULED, true), //
    APPLICATION_INTERVIEW_LOCATION(APPLICATION_INTERVIEW_SCHEDULED, false), //
    APPLICATION_CONFIRMED_POSITION_TITLE(APPLICATION_APPROVED, true), //
    APPLICATION_CONFIRMED_POSITION_DESCRIPTION(APPLICATION_APPROVED, true), //
    APPLICATION_CONFIRMED_START_DATE(APPLICATION_APPROVED, true), //
    APPLICATION_CONFIRMED_PRIMARY_SUPERVISOR(APPLICATION_APPROVED, true), //
    APPLICATION_CONFIRMED_SECONDARY_SUPERVISOR(APPLICATION_APPROVED, true), //
    APPLICATION_CONFIRMED_OFFER_CONDITIONS(APPLICATION_APPROVED, true), //
    APPLICATION_REJECTION_REASON(APPLICATION_REJECTED, true), //
    PROJECT_TITLE(PROJECT_GLOBAL, true), //
    PROJECT_CODE(PROJECT_GLOBAL, true), //
    PROGRAM_TITLE(PROGRAM_GLOBAL, true), //
    PROGRAM_CODE(PROGRAM_GLOBAL, true), //
    INSTITUTION_TITLE(INSTITUTION_GLOBAL, true), //
    INSTITUTION_CODE(INSTITUTION_GLOBAL, true), //
    INSTITUTION_HOMEPAGE(INSTITUTION_GLOBAL, true), //
    INSTITUTION_DATA_IMPORT_ERROR(INSTITUTION_APPROVED, true), //
    SYSTEM_APPLICATION_HOMEPAGE(SYSTEM_APPLICATION_SYNDICATED, false), //
    SYSTEM_APPLICATION_RECOMMENDATION(SYSTEM_APPLICATION_MARKETING, true), //
    SYSTEM_PROJECT_HOMEPAGE(SYSTEM_PROJECT_SYNDICATED, false), //
    SYSTEM_PROGRAM_HOMEPAGE(SYSTEM_PROGRAM_SYNDICATED, false), //
    SYSTEM_INSTITUTION_HOMEPAGE(SYSTEM_INSTITUTION_SYNDICATED, false), //
    SYSTEM_USER_NEW_PASSWORD(SYSTEM_USER_PASSWORD, false), //
    SYSTEM_USER_ACCOUNT_MANAGEMENT(SYSTEM_USER_PASSWORD, false), //
    SYSTEM_USER_ACCOUNT_ACTIVATION(SYSTEM_USER_ACCOUNT, false);

    private PrismNotificationDefinitionPropertyCategory category;

    private boolean escapeHtml;

    private static ListMultimap<PrismNotificationDefinitionPropertyCategory, PrismNotificationDefinitionProperty> categoryProperties = LinkedListMultimap.create();

    static {
        for (PrismNotificationDefinitionProperty property : PrismNotificationDefinitionProperty.values()) {
            categoryProperties.put(property.getCategory(), property);
        }
    }

    PrismNotificationDefinitionProperty(PrismNotificationDefinitionPropertyCategory category, boolean escapeHtml) {
        this.escapeHtml = escapeHtml;
        this.category = category;
    }

    public PrismNotificationDefinitionPropertyCategory getCategory() {
        return category;
    }

    public final boolean isEscapeHtml() {
        return escapeHtml;
    }

    public static List<PrismNotificationDefinitionProperty> getProperties(PrismNotificationDefinitionPropertyCategory category) {
        return categoryProperties.get(category);
    }

}
