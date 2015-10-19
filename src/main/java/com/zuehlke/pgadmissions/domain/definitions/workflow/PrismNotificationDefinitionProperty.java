package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.ACTION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.APPLICATION_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.APPLICATION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.APPLICATION_INTERVIEW_SCHEDULED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.APPLICATION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.COMMENT_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.COMMENT_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.DEPARTMENT_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.INSTITUTION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.PROGRAM_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.PROJECT_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_APPLICATION_MARKETING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_APPLICATION_SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_DEPARTMENT_SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_INSTITUTION_SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_PROGRAM_SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_PROJECT_SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_USER_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_USER_ACTIVATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_USER_PASSWORD;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.TARGET_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.TEMPLATE_GLOBAL;

import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.zuehlke.pgadmissions.workflow.notification.property.ActionCompleteBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ActionViewEditBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationCodeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationCreatorFullNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationInterviewDateTimeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationInterviewLocationBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationInterviewTimeZoneBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationIntervieweeInstructionsBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationInterviewerInstructionsBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationManagerBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationOfferAcceptanceBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationOfferConditionBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationOpportunityTypeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationPositionDescriptionBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationPositionNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationRejectionReasonBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ApplicationStartDateBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.CommentContentBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.CommentDateTimeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.CommentTransitionOutcomeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.DepartmentCodeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.DepartmentNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.DepartmentUserContactBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.InstitutionCodeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.InstitutionNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.InstitutionUserContactBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.NotificationPropertyBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ProgramCodeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ProgramNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ProgramUserContactBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ProjectCodeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ProjectNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.ProjectUserContactBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.SystemApplicationHomepageBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.SystemApplicationRecommendationBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.SystemDepartmentHomepageBuilder;
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
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateParentResourceCodeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateParentResourceNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateSystemHelpdeskBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateSystemHomepageBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateSystemNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateUserActivationCodeBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateUserEmailBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateUserFirstNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateUserFullNameBuilder;
import com.zuehlke.pgadmissions.workflow.notification.property.TemplateUserLastNameBuilder;

public enum PrismNotificationDefinitionProperty {

    TEMPLATE_INITIATOR_FULL_NAME(TEMPLATE_GLOBAL, true, TemplateInvokerFullNameBuilder.class), //
    TEMPLATE_INITIATOR_EMAIL(TEMPLATE_GLOBAL, true, TemplateInvokerEmailBuilder.class), //
    TEMPLATE_RECIPIENT_FULL_NAME(TEMPLATE_GLOBAL, true, TemplateUserFullNameBuilder.class), //
    TEMPLATE_RECIPIENT_FIRST_NAME(TEMPLATE_GLOBAL, true, TemplateUserFirstNameBuilder.class), //
    TEMPLATE_RECIPIENT_LAST_NAME(TEMPLATE_GLOBAL, true, TemplateUserLastNameBuilder.class), //
    TEMPLATE_RECIPIENT_EMAIL(TEMPLATE_GLOBAL, true, TemplateUserEmailBuilder.class), //
    TEMPLATE_RECIPIENT_ACTIVATION_CODE(TEMPLATE_GLOBAL, true, TemplateUserActivationCodeBuilder.class), //
    TEMPLATE_SIGNATORY_FULL_NAME(TEMPLATE_GLOBAL, true, TemplateAuthorFullNameBuilder.class), //
    TEMPLATE_SIGNATORY_EMAIL(TEMPLATE_GLOBAL, true, TemplateAuthorEmailBuilder.class), //
    TEMPLATE_SYSTEM_NAME(TEMPLATE_GLOBAL, true, TemplateSystemNameBuilder.class), //
    TEMPLATE_SYSTEM_HOMEPAGE(TEMPLATE_GLOBAL, true, TemplateSystemHomepageBuilder.class), //
    TEMPLATE_SYSTEM_HELPDESK(TEMPLATE_GLOBAL, false, TemplateSystemHelpdeskBuilder.class), //
    TEMPLATE_PARENT_RESOURCE_NAME(TEMPLATE_GLOBAL, true, TemplateParentResourceNameBuilder.class), //
    TEMPLATE_PARENT_RESOURCE_CODE(TEMPLATE_GLOBAL, true, TemplateParentResourceCodeBuilder.class), //
    ACTION_COMPLETE(ACTION_GLOBAL, false, ActionCompleteBuilder.class), //
    ACTION_VIEW_EDIT(ACTION_GLOBAL, false, ActionViewEditBuilder.class), //
    COMMENT_CONTENT(COMMENT_GLOBAL, true, CommentContentBuilder.class), //
    COMMENT_DATE_TIME(COMMENT_GLOBAL, true, CommentDateTimeBuilder.class), //
    COMMENT_TRANSITION_OUTCOME(COMMENT_TRANSITION, true, CommentTransitionOutcomeBuilder.class), //
    TARGET_RESOURCE_OTHER_NAME(TARGET_GLOBAL, true, null), //
    TARGET_RESOURCE_ACCEPT_NAME(TARGET_GLOBAL, true, null), //
    APPLICATION_CREATOR_EMAIL(APPLICATION_GLOBAL, true, ApplicationCreatorFullNameBuilder.class), //
    APPLICATION_CREATOR_FULL_NAME(APPLICATION_GLOBAL, true, ApplicationCreatorFullNameBuilder.class), //
    APPLICATION_CODE(APPLICATION_GLOBAL, true, ApplicationCodeBuilder.class), //
    APPLICATION_OPPORTUNITY_TYPE(APPLICATION_GLOBAL, true, ApplicationOpportunityTypeBuilder.class), //
    APPLICATION_OPPORTUNITY_CATEGORY(APPLICATION_GLOBAL, true, ApplicationOpportunityTypeBuilder.class), //
    APPLICATION_INTERVIEW_DATE_TIME(APPLICATION_INTERVIEW_SCHEDULED, true, ApplicationInterviewDateTimeBuilder.class), //
    APPLICATION_INTERVIEW_TIME_ZONE(APPLICATION_INTERVIEW_SCHEDULED, true, ApplicationInterviewTimeZoneBuilder.class), //
    APPLICATION_INTERVIEWER_INSTRUCTIONS(APPLICATION_INTERVIEW_SCHEDULED, true, ApplicationInterviewerInstructionsBuilder.class), //
    APPLICATION_INTERVIEWEE_INSTRUCTIONS(APPLICATION_INTERVIEW_SCHEDULED, true, ApplicationIntervieweeInstructionsBuilder.class), //
    APPLICATION_INTERVIEW_LOCATION(APPLICATION_INTERVIEW_SCHEDULED, false, ApplicationInterviewLocationBuilder.class), //
    APPLICATION_POSITION_NAME(APPLICATION_APPROVED, true, ApplicationPositionNameBuilder.class), //
    APPLICATION_POSITION_DESCRIPTION(APPLICATION_APPROVED, true, ApplicationPositionDescriptionBuilder.class), //
    APPLICATION_START_DATE(APPLICATION_APPROVED, true, ApplicationStartDateBuilder.class), //
    APPLICATION_MANAGER(APPLICATION_APPROVED, true, ApplicationManagerBuilder.class), //
    APPLICATION_OFFER_CONDITION(APPLICATION_APPROVED, true, ApplicationOfferConditionBuilder.class), //
    APPLICATION_OFFER_ACCEPTANCE(APPLICATION_APPROVED, true, ApplicationOfferAcceptanceBuilder.class), //
    APPLICATION_REJECTION_REASON(APPLICATION_REJECTED, true, ApplicationRejectionReasonBuilder.class), //
    PROJECT_NAME(PROJECT_GLOBAL, true, ProjectNameBuilder.class), //
    PROJECT_CODE(PROJECT_GLOBAL, true, ProjectCodeBuilder.class), //
    PROJECT_CONTACT(PROJECT_GLOBAL, true, ProjectUserContactBuilder.class), //
    PROGRAM_NAME(PROGRAM_GLOBAL, true, ProgramNameBuilder.class), //
    PROGRAM_CODE(PROGRAM_GLOBAL, true, ProgramCodeBuilder.class), //
    PROGRAM_CONTACT(PROGRAM_GLOBAL, true, ProgramUserContactBuilder.class), //
    DEPARTMENT_NAME(DEPARTMENT_GLOBAL, true, DepartmentNameBuilder.class), //
    DEPARTMENT_CODE(DEPARTMENT_GLOBAL, true, DepartmentCodeBuilder.class), //
    DEPARTMENT_CONTACT(DEPARTMENT_GLOBAL, true, DepartmentUserContactBuilder.class), //
    INSTITUTION_NAME(INSTITUTION_GLOBAL, true, InstitutionNameBuilder.class), //
    INSTITUTION_CODE(INSTITUTION_GLOBAL, true, InstitutionCodeBuilder.class), //
    INSTITUTION_CONTACT(INSTITUTION_GLOBAL, true, InstitutionUserContactBuilder.class), //
    SYSTEM_APPLICATION_HOMEPAGE(SYSTEM_APPLICATION_SYNDICATED, false, SystemApplicationHomepageBuilder.class), //
    SYSTEM_APPLICATION_RECOMMENDATION(SYSTEM_APPLICATION_MARKETING, false, SystemApplicationRecommendationBuilder.class), //
    SYSTEM_PROJECT_HOMEPAGE(SYSTEM_PROJECT_SYNDICATED, false, SystemProjectHomepageBuilder.class), //
    SYSTEM_PROGRAM_HOMEPAGE(SYSTEM_PROGRAM_SYNDICATED, false, SystemProgramHomepageBuilder.class), //
    SYSTEM_DEPARTMENT_HOMEPAGE(SYSTEM_DEPARTMENT_SYNDICATED, false, SystemDepartmentHomepageBuilder.class), //
    SYSTEM_INSTITUTION_HOMEPAGE(SYSTEM_INSTITUTION_SYNDICATED, false, SystemInstitutionHomepageBuilder.class), //
    SYSTEM_NEW_PASSWORD(SYSTEM_USER_PASSWORD, false, SystemUserNewPasswordBuilder.class), //
    SYSTEM_ACCOUNT_MANAGEMENT(SYSTEM_USER_ACCOUNT, false, SystemUserAccountManagementBuilder.class), //
    SYSTEM_ACCOUNT_ACTIVATION(SYSTEM_USER_ACTIVATION, false, SystemUserAccountActivationBuilder.class);

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
