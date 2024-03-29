package uk.co.alumeni.prism.domain.definitions.workflow;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import uk.co.alumeni.prism.workflow.notification.property.*;

import java.util.List;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.*;

public enum PrismNotificationDefinitionProperty {

    TEMPLATE_INITIATOR_FULL_NAME(TEMPLATE_GLOBAL, true, TemplateInitiatorFullNameBuilder.class), //
    TEMPLATE_INITIATOR_EMAIL(TEMPLATE_GLOBAL, true, TemplateInitiatorEmailBuilder.class), //
    TEMPLATE_RECIPIENT_FULL_NAME(TEMPLATE_GLOBAL, true, TemplateRecipientFullNameBuilder.class), //
    TEMPLATE_RECIPIENT_FIRST_NAME(TEMPLATE_GLOBAL, true, TemplateRecipientFirstNameBuilder.class), //
    TEMPLATE_RECIPIENT_LAST_NAME(TEMPLATE_GLOBAL, true, TemplateRecipientLastNameBuilder.class), //
    TEMPLATE_RECIPIENT_EMAIL(TEMPLATE_GLOBAL, true, TemplateRecipientEmailBuilder.class), //
    TEMPLATE_SIGNATORY_FULL_NAME(TEMPLATE_GLOBAL, true, TemplateSignatoryFullNameBuilder.class), //
    TEMPLATE_SIGNATORY_EMAIL(TEMPLATE_GLOBAL, true, TemplateSignatoryEmailBuilder.class), //
    TEMPLATE_SYSTEM_NAME(TEMPLATE_GLOBAL, true, TemplateSystemNameBuilder.class), //
    TEMPLATE_SYSTEM_HOMEPAGE(TEMPLATE_GLOBAL, false, TemplateSystemHomepageBuilder.class), //
    TEMPLATE_SYSTEM_HELPDESK(TEMPLATE_GLOBAL, false, TemplateSystemHelpdeskBuilder.class), //
    TEMPLATE_RESOURCE_NAME(TEMPLATE_GLOBAL, true, TemplateResourceNameBuilder.class), //
    TEMPLATE_RESOURCE_CODE(TEMPLATE_GLOBAL, true, TemplateResourceCodeBuilder.class), //
    TEMPLATE_RESOURCE_SCOPE(TEMPLATE_GLOBAL, true, TemplateResourceScopeBuilder.class), //
    TEMPLATE_PARENT_RESOURCE_NAME(TEMPLATE_GLOBAL, true, TemplateParentResourceNameBuilder.class), //
    TEMPLATE_PARENT_RESOURCE_CODE(TEMPLATE_GLOBAL, true, TemplateParentResourceCodeBuilder.class), //
    TEMPLATE_INVITATION_MESSAGE(TEMPLATE_GLOBAL, true, TemplateInvitationMessageBuilder.class), //
    TEMPLATE_BUFFERED(TEMPLATE_GLOBAL, true, TemplateBufferedBuilder.class), //
    ACTION_COMPLETE(ACTION_GLOBAL, false, ActionCompleteBuilder.class), //
    ACTION_VIEW_EDIT(ACTION_GLOBAL, false, ActionViewEditBuilder.class), //
    INVITATION_ACCEPT(INVITATION_GLOBAL, false, InvitationAcceptBuilder.class), //
    COMMENT_CONTENT(COMMENT_GLOBAL, true, CommentContentBuilder.class), //
    COMMENT_DATE_TIME(COMMENT_GLOBAL, true, CommentDateTimeBuilder.class), //
    COMMENT_TRANSITION_OUTCOME(COMMENT_TRANSITION, true, CommentTransitionOutcomeBuilder.class), //
    MESSAGE_INITIATOR_FULL_NAME(MESSAGE_GLOBAL, true, MessageInitiatorFullNameBuilder.class), //
    MESSAGE_SUBJECT(MESSAGE_GLOBAL, true, MessageSubjectBuilder.class), //
    TARGET_RESOURCE_OTHER_NAME(TARGET_GLOBAL, true, TargetResourceOtherNameBuilder.class), //
    TARGET_RESOURCE_ACCEPT_NAME(TARGET_GLOBAL, true, TargetResourceAcceptNameBuilder.class), //
    APPLICATION_CREATOR_EMAIL(APPLICATION_GLOBAL, true, ApplicationCreatorEmailBuilder.class), //
    APPLICATION_CREATOR_FULL_NAME(APPLICATION_GLOBAL, true, ApplicationCreatorFullNameBuilder.class), //
    APPLICATION_CODE(APPLICATION_GLOBAL, true, ApplicationCodeBuilder.class), //
    APPLICATION_OPPORTUNITY_TYPE(APPLICATION_GLOBAL, true, ApplicationOpportunityTypeBuilder.class), //
    APPLICATION_OPPORTUNITY_CATEGORY(APPLICATION_GLOBAL, true, ApplicationOpportunityTypeBuilder.class), //
    APPLICATION_INTERVIEW_DATE_TIME(APPLICATION_INTERVIEW_SCHEDULED, true, ApplicationInterviewDateTimeBuilder.class), //
    APPLICATION_INTERVIEW_TIME_ZONE(APPLICATION_INTERVIEW_SCHEDULED, true, ApplicationInterviewTimeZoneBuilder.class), //
    APPLICATION_INTERVIEWER_INSTRUCTIONS(APPLICATION_INTERVIEW_SCHEDULED, true, ApplicationInterviewerInstructionsBuilder.class), //
    APPLICATION_INTERVIEWEE_INSTRUCTIONS(APPLICATION_INTERVIEW_SCHEDULED, true, ApplicationIntervieweeInstructionsBuilder.class), //
    APPLICATION_INTERVIEW_LOCATION(APPLICATION_INTERVIEW_SCHEDULED, false, ApplicationInterviewLocationBuilder.class), //
    APPLICATION_INTERVIEW_AVAILABLE(APPLICATION_INTERVIEW_SCHEDULED, false, ApplicationInterviewAvailableBuilder.class), //
    APPLICATION_MANAGER(APPLICATION_APPROVED, true, ApplicationManagerBuilder.class), //
    APPLICATION_POSITION_NAME(APPLICATION_APPROVED, true, ApplicationPositionNameBuilder.class), //
    APPLICATION_POSITION_DESCRIPTION(APPLICATION_APPROVED, true, ApplicationPositionDescriptionBuilder.class), //
    APPLICATION_START_DATE(APPLICATION_APPROVED, true, ApplicationStartDateBuilder.class), //
    APPLICATION_OFFER_CONDITION(APPLICATION_APPROVED, true, ApplicationOfferConditionBuilder.class), //
    APPLICATION_REJECTION_REASON(APPLICATION_REJECTED, true, ApplicationRejectionReasonBuilder.class), //
    PROJECT_NAME(PROJECT_GLOBAL, true, ProjectNameBuilder.class), //
    PROJECT_CODE(PROJECT_GLOBAL, true, ProjectCodeBuilder.class), //
    PROJECT_USER_CONTACT(PROJECT_GLOBAL, true, ProjectUserContactBuilder.class), //
    PROJECT_CREATOR_FULL_NAME(PROJECT_GLOBAL, true, ProjectCreatorFullNameBuilder.class), //
    PROGRAM_NAME(PROGRAM_GLOBAL, true, ProgramNameBuilder.class), //
    PROGRAM_CODE(PROGRAM_GLOBAL, true, ProgramCodeBuilder.class), //
    PROGRAM_USER_CONTACT(PROGRAM_GLOBAL, true, ProgramUserContactBuilder.class), //
    PROGRAM_CREATOR_FULL_NAME(PROGRAM_GLOBAL, true, ProgramCreatorFullNameBuilder.class), //
    DEPARTMENT_NAME(DEPARTMENT_GLOBAL, true, DepartmentNameBuilder.class), //
    DEPARTMENT_CODE(DEPARTMENT_GLOBAL, true, DepartmentCodeBuilder.class), //
    DEPARTMENT_USER_CONTACT(DEPARTMENT_GLOBAL, true, DepartmentUserContactBuilder.class), //
    DEPARTMENT_CREATOR_FULL_NAME(DEPARTMENT_GLOBAL, true, DepartmentCreatorFullNameBuilder.class), //
    INSTITUTION_NAME(INSTITUTION_GLOBAL, true, InstitutionNameBuilder.class), //
    INSTITUTION_CODE(INSTITUTION_GLOBAL, true, InstitutionCodeBuilder.class), //
    INSTITUTION_USER_CONTACT(INSTITUTION_GLOBAL, true, InstitutionUserContactBuilder.class), //
    INSTITUTION_CREATOR_FULL_NAME(INSTITUTION_GLOBAL, true, InstitutionCreatorFullNameBuilder.class), //
    SYSTEM_APPLICATION_HOMEPAGE(SYSTEM_APPLICATION, false, SystemApplicationHomepageBuilder.class), //
    SYSTEM_PROJECT_HOMEPAGE(SYSTEM_PROJECT, false, SystemProjectHomepageBuilder.class), //
    SYSTEM_PROGRAM_HOMEPAGE(SYSTEM_PROGRAM, false, SystemProgramHomepageBuilder.class), //
    SYSTEM_DEPARTMENT_HOMEPAGE(SYSTEM_DEPARTMENT, false, SystemDepartmentHomepageBuilder.class), //
    SYSTEM_INSTITUTION_HOMEPAGE(SYSTEM_INSTITUTION, false, SystemInstitutionHomepageBuilder.class), //
    SYSTEM_USER_ACCOUNT_ACTIVATION(SYSTEM_USER_ACTIVATION, false, SystemUserAccountActivationBuilder.class), //
    SYSTEM_USER_ACCOUNT_VIEW(SYSTEM_USER_ACCOUNT, false, SystemUserAccountViewBuilder.class), //
    SYSTEM_USER_NEW_PASSWORD(SYSTEM_USER_PASSWORD, false, SystemUserNewPasswordBuilder.class), //
    SYSTEM_ACTIVITY_SUMMARY(SYSTEM_ACTIVITY, false, SystemActivitySummaryBuilder.class), //
    SYSTEM_ADVERT_RECOMMENDATION(SYSTEM_ACTIVITY, false, SystemAdvertRecommendationBuilder.class),
    SYSTEM_REMINDER_SUMMARY(SYSTEM_REMINDER, false, SystemReminderSummaryBuilder.class),
    CANDIDATE_FULL_NAME(CANDIDATE_GLOBAL, true, CandidateFullNameBuilder.class),
    CANDIDATE_PROFILE(CANDIDATE_GLOBAL, false, CandidateProfileBuilder.class);

    private PrismNotificationDefinitionPropertyCategory notificationDefinitionCategory;

    private boolean escapeHtml;

    private Class<? extends NotificationPropertyBuilder> builder;

    private static ListMultimap<PrismNotificationDefinitionPropertyCategory, PrismNotificationDefinitionProperty> propertiesByCategory =
            LinkedListMultimap.create();

    static {
        for (PrismNotificationDefinitionProperty property : PrismNotificationDefinitionProperty.values()) {
            propertiesByCategory.put(property.getNotificationDefinitionCategory(), property);
        }
    }

    private PrismNotificationDefinitionProperty(PrismNotificationDefinitionPropertyCategory notificationDefinitionCategory, boolean escapeHtml,
            Class<? extends NotificationPropertyBuilder> builder) {
        this.notificationDefinitionCategory = notificationDefinitionCategory;
        this.escapeHtml = escapeHtml;
        this.builder = builder;
    }

    public PrismNotificationDefinitionPropertyCategory getNotificationDefinitionCategory() {
        return notificationDefinitionCategory;
    }

    public boolean isEscapeHtml() {
        return escapeHtml;
    }

    public Class<? extends NotificationPropertyBuilder> getBuilder() {
        return builder;
    }

    public static List<PrismNotificationDefinitionProperty> getProperties(PrismNotificationDefinitionPropertyCategory notificationDefinitionCategory) {
        return propertiesByCategory.get(notificationDefinitionCategory);
    }

}
