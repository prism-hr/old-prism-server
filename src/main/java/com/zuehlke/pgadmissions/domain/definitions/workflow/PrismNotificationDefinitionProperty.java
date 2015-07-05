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
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_USER_ACTIVATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_USER_PASSWORD;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.TEMPLATE_GLOBAL;

import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.zuehlke.pgadmissions.workflow.notification.property.ActionCompleteBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ActionViewEditBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationCodeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationConfirmedOfferConditionBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationConfirmedPositionDescriptionBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationConfirmedPositionTitleBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationConfirmedPrimarySupervisorBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationConfirmedSecondarySupervisorBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationConfirmedStartDateBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationCreatorFullNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationInterviewDateTimeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationInterviewLocationBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationInterviewTimeZoneBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationIntervieweeInstructionsBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationInterviewerInstructionsBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationOpportunityTypeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationParentResourceCodeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationParentResourceTitleBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationRejectionReasonBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.CommentContentBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.CommentDateTimeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.CommentTransitionOutcomeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.InstitutionCodeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.InstitutionDataImportErrorBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.InstitutionHomepageBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.InstitutionTitleBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.InstitutionUserContactBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.NotificationPropertyBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ProgramCodeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ProgramTitleBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ProgramUserContactBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ProjectCodeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ProjectTitleBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ProjectUserContactBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.SystemApplicationHomepageBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.SystemApplicationRecommendationBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.SystemInstitutionHomepageBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.SystemProgramHomepageBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.SystemProjectHomepageBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.SystemUserAccountActivationBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.SystemUserAccountManagementBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.SystemUserNewPasswordBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateAuthorEmailBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateAuthorFullNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateInvokerEmailBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateInvokerFullNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateSystemHelpdeskBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateSystemHomepageBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateSystemTitleBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateUserActivationCodeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateUserEmailBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateUserFirstNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateUserFullNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateUserLastNameBuilder;

public enum PrismNotificationDefinitionProperty {

    TEMPLATE_USER_FULL_NAME(TEMPLATE_GLOBAL, true, TemplateUserFullNameBuilder.class), //
    TEMPLATE_USER_FIRST_NAME(TEMPLATE_GLOBAL, true, TemplateUserFirstNameBuilder.class), //
    TEMPLATE_USER_LAST_NAME(TEMPLATE_GLOBAL, true, TemplateUserLastNameBuilder.class), //
    TEMPLATE_USER_EMAIL(TEMPLATE_GLOBAL, true, TemplateUserEmailBuilder.class), //
    TEMPLATE_USER_ACTIVATION_CODE(TEMPLATE_GLOBAL, true, TemplateUserActivationCodeBuilder.class), //
    TEMPLATE_AUTHOR_FULL_NAME(TEMPLATE_GLOBAL, true, TemplateAuthorFullNameBuilder.class), //
    TEMPLATE_AUTHOR_EMAIL(TEMPLATE_GLOBAL, true, TemplateAuthorEmailBuilder.class), //
    TEMPLATE_INVOKER_FULL_NAME(TEMPLATE_GLOBAL, true, TemplateInvokerFullNameBuilder.class), //
    TEMPLATE_INVOKER_EMAIL(TEMPLATE_GLOBAL, true, TemplateInvokerEmailBuilder.class), //
    TEMPLATE_SYSTEM_TITLE(TEMPLATE_GLOBAL, true, TemplateSystemTitleBuilder.class), //
    TEMPLATE_SYSTEM_HOMEPAGE(TEMPLATE_GLOBAL, true, TemplateSystemHomepageBuilder.class), //
    TEMPLATE_SYSTEM_HELPDESK(TEMPLATE_GLOBAL, false, TemplateSystemHelpdeskBuilder.class), //
    ACTION_COMPLETE(ACTION_GLOBAL, false, ActionCompleteBuilder.class), //
    ACTION_VIEW_EDIT(ACTION_GLOBAL, false, ActionViewEditBuilder.class), //
    COMMENT_CONTENT(COMMENT_GLOBAL, true, CommentContentBuilder.class), //
    COMMENT_DATE_TIME(COMMENT_GLOBAL, true, CommentDateTimeBuilder.class), //
    COMMENT_TRANSITION_OUTCOME(COMMENT_TRANSITION, true, CommentTransitionOutcomeBuilder.class), //
    APPLICATION_CREATOR_FULL_NAME(APPLICATION_GLOBAL, true, ApplicationCreatorFullNameBuilder.class), //
    APPLICATION_CODE(APPLICATION_GLOBAL, true, ApplicationCodeBuilder.class), //
    APPLICATION_PARENT_RESOURCE_TITLE(APPLICATION_GLOBAL, true, ApplicationParentResourceTitleBuilder.class), //
    APPLICATION_PARENT_RESOURCE_CODE(APPLICATION_GLOBAL, true, ApplicationParentResourceCodeBuilder.class), //
    APPLICATION_OPPORTUNITY_TYPE(APPLICATION_GLOBAL, true, ApplicationOpportunityTypeBuilder.class), //
    APPLICATION_INTERVIEW_DATE_TIME(APPLICATION_INTERVIEW_SCHEDULED, true, ApplicationInterviewDateTimeBuilder.class), //
    APPLICATION_INTERVIEW_TIME_ZONE(APPLICATION_INTERVIEW_SCHEDULED, true, ApplicationInterviewTimeZoneBuilder.class), //
    APPLICATION_INTERVIEWER_INSTRUCTIONS(APPLICATION_INTERVIEW_SCHEDULED, true, ApplicationInterviewerInstructionsBuilder.class), //
    APPLICATION_INTERVIEWEE_INSTRUCTIONS(APPLICATION_INTERVIEW_SCHEDULED, true, ApplicationIntervieweeInstructionsBuilder.class), //
    APPLICATION_INTERVIEW_LOCATION(APPLICATION_INTERVIEW_SCHEDULED, false, ApplicationInterviewLocationBuilder.class), //
    APPLICATION_CONFIRMED_POSITION_TITLE(APPLICATION_APPROVED, true, ApplicationConfirmedPositionTitleBuilder.class), //
    APPLICATION_CONFIRMED_POSITION_DESCRIPTION(APPLICATION_APPROVED, true, ApplicationConfirmedPositionDescriptionBuilder.class), //
    APPLICATION_CONFIRMED_START_DATE(APPLICATION_APPROVED, true, ApplicationConfirmedStartDateBuilder.class), //
    APPLICATION_CONFIRMED_PRIMARY_SUPERVISOR(APPLICATION_APPROVED, true, ApplicationConfirmedPrimarySupervisorBuilder.class), //
    APPLICATION_CONFIRMED_SECONDARY_SUPERVISOR(APPLICATION_APPROVED, true, ApplicationConfirmedSecondarySupervisorBuilder.class), //
    APPLICATION_CONFIRMED_OFFER_CONDITION(APPLICATION_APPROVED, true, ApplicationConfirmedOfferConditionBuilder.class), //
    APPLICATION_REJECTION_REASON(APPLICATION_REJECTED, true, ApplicationRejectionReasonBuilder.class), //
    PROJECT_TITLE(PROJECT_GLOBAL, true, ProjectTitleBuilder.class), //
    PROJECT_CODE(PROJECT_GLOBAL, true, ProjectCodeBuilder.class), //
    PROJECT_USER_CONTACT(PROJECT_GLOBAL, true, ProjectUserContactBuilder.class), //
    PROGRAM_TITLE(PROGRAM_GLOBAL, true, ProgramTitleBuilder.class), //
    PROGRAM_CODE(PROGRAM_GLOBAL, true, ProgramCodeBuilder.class), //
    PROGRAM_USER_CONTACT(PROGRAM_GLOBAL, true, ProgramUserContactBuilder.class), //
    INSTITUTION_TITLE(INSTITUTION_GLOBAL, true, InstitutionTitleBuilder.class), //
    INSTITUTION_CODE(INSTITUTION_GLOBAL, true, InstitutionCodeBuilder.class), //
    INSTITUTION_USER_CONTACT(INSTITUTION_GLOBAL, true, InstitutionUserContactBuilder.class), //
    INSTITUTION_HOMEPAGE(INSTITUTION_GLOBAL, true, InstitutionHomepageBuilder.class), //
    INSTITUTION_DATA_IMPORT_ERROR(INSTITUTION_APPROVED, true, InstitutionDataImportErrorBuilder.class), //
    SYSTEM_APPLICATION_HOMEPAGE(SYSTEM_APPLICATION_SYNDICATED, false, SystemApplicationHomepageBuilder.class), //
    SYSTEM_APPLICATION_RECOMMENDATION(SYSTEM_APPLICATION_MARKETING, false, SystemApplicationRecommendationBuilder.class), //
    SYSTEM_PROJECT_HOMEPAGE(SYSTEM_PROJECT_SYNDICATED, false, SystemProjectHomepageBuilder.class), //
    SYSTEM_PROGRAM_HOMEPAGE(SYSTEM_PROGRAM_SYNDICATED, false, SystemProgramHomepageBuilder.class), //
    SYSTEM_INSTITUTION_HOMEPAGE(SYSTEM_INSTITUTION_SYNDICATED, false, SystemInstitutionHomepageBuilder.class), //
    SYSTEM_USER_NEW_PASSWORD(SYSTEM_USER_PASSWORD, false, SystemUserNewPasswordBuilder.class), //
    SYSTEM_USER_ACCOUNT_MANAGEMENT(SYSTEM_USER_ACCOUNT, false, SystemUserAccountManagementBuilder.class), //
    SYSTEM_USER_ACCOUNT_ACTIVATION(SYSTEM_USER_ACTIVATION, false, SystemUserAccountActivationBuilder.class);

    private PrismNotificationDefinitionPropertyCategory category;

    private boolean escapeHtml;

    private Class<? extends NotificationPropertyBuilder> builder;

    private static ListMultimap<PrismNotificationDefinitionPropertyCategory, PrismNotificationDefinitionProperty> categoryProperties = LinkedListMultimap
            .create();

    static {
        for (PrismNotificationDefinitionProperty property : PrismNotificationDefinitionProperty.values()) {
            categoryProperties.put(property.getCategory(), property);
        }
    }

    private PrismNotificationDefinitionProperty(PrismNotificationDefinitionPropertyCategory category, boolean escapeHtml,
            Class<? extends NotificationPropertyBuilder> builder) {
        this.category = category;
        this.escapeHtml = escapeHtml;
        this.builder = builder;
    }

    public PrismNotificationDefinitionPropertyCategory getCategory() {
        return category;
    }

    public boolean isEscapeHtml() {
        return escapeHtml;
    }

    public Class<? extends NotificationPropertyBuilder> getBuilder() {
        return builder;
    }

    public static List<PrismNotificationDefinitionProperty> getProperties(PrismNotificationDefinitionPropertyCategory category) {
        return categoryProperties.get(category);
    }

}
