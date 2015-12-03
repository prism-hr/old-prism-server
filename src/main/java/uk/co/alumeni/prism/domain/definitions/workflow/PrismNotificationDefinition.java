package uk.co.alumeni.prism.domain.definitions.workflow;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.ACTION_GLOBAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.APPLICATION_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.APPLICATION_GLOBAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.APPLICATION_INTERVIEW_SCHEDULED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.APPLICATION_REJECTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.COMMENT_GLOBAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.COMMENT_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.DEPARTMENT_GLOBAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.INSTITUTION_GLOBAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.PROGRAM_GLOBAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.PROJECT_GLOBAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_ACTIVITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_USER_ACTIVATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.SYSTEM_USER_PASSWORD;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.TARGET_GLOBAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory.TEMPLATE_GLOBAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationPurpose.REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationPurpose.UPDATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationType.INDIVIDUAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationType.SYNDICATED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.List;

import com.google.common.collect.Lists;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismLocalizableDefinition;

public enum PrismNotificationDefinition implements PrismLocalizableDefinition {

    APPLICATION_COMPLETE_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, PrismNotificationDefinitionPropertyCategory.COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_COMPLETE_VALIDATION_STAGE_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_COMPLETE_REVIEW_STAGE_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_COMPLETE_INTERVIEW_STAGE_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_COMPLETE_APPROVAL_STAGE_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_COMPLETE_REFERENCE_STAGE_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, APPLICATION_INTERVIEW_SCHEDULED, ACTION_GLOBAL, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, APPLICATION_INTERVIEW_SCHEDULED, ACTION_GLOBAL, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_CONFIRM_OFFER_ACCEPTANCE_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, APPLICATION_APPROVED, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_CONFIRM_OFFER_ACCEPTANCE_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, APPLICATION_APPROVED, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_CONFIRM_REJECTION_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, APPLICATION_REJECTED, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_PROVIDE_REVIEW_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_REVERSE_REJECTION_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, APPLICATION_REJECTED, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_TERMINATE_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, APPLICATION_REJECTED, COMMENT_GLOBAL, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //

    PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION(INDIVIDUAL, UPDATE, PROJECT, //
            Lists.newArrayList(INSTITUTION_GLOBAL, PROJECT_GLOBAL, ACTION_GLOBAL, COMMENT_GLOBAL, COMMENT_TRANSITION, TEMPLATE_GLOBAL)), //
    PROJECT_CORRECT_REQUEST(INDIVIDUAL, REQUEST, PROJECT, //
            Lists.newArrayList(INSTITUTION_GLOBAL, PROJECT_GLOBAL, ACTION_GLOBAL, COMMENT_GLOBAL, COMMENT_TRANSITION, TEMPLATE_GLOBAL)), //

    PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION(INDIVIDUAL, UPDATE, PROGRAM, //
            Lists.newArrayList(INSTITUTION_GLOBAL, PROGRAM_GLOBAL, ACTION_GLOBAL, COMMENT_GLOBAL, COMMENT_TRANSITION, TEMPLATE_GLOBAL)), //
    PROGRAM_CORRECT_REQUEST(INDIVIDUAL, REQUEST, PROGRAM, //
            Lists.newArrayList(INSTITUTION_GLOBAL, PROGRAM_GLOBAL, ACTION_GLOBAL, COMMENT_GLOBAL, COMMENT_TRANSITION, TEMPLATE_GLOBAL)), //

    DEPARTMENT_COMPLETE_APPROVAL_STAGE_NOTIFICATION(INDIVIDUAL, UPDATE, DEPARTMENT, //
            Lists.newArrayList(INSTITUTION_GLOBAL, DEPARTMENT_GLOBAL, ACTION_GLOBAL, COMMENT_GLOBAL, COMMENT_TRANSITION, TEMPLATE_GLOBAL)), //
    DEPARTMENT_CORRECT_REQUEST(INDIVIDUAL, REQUEST, DEPARTMENT, //
            Lists.newArrayList(INSTITUTION_GLOBAL, DEPARTMENT_GLOBAL, ACTION_GLOBAL, COMMENT_GLOBAL, COMMENT_TRANSITION, TEMPLATE_GLOBAL)), //

    INSTITUTION_COMPLETE_APPROVAL_STAGE_NOTIFICATION(INDIVIDUAL, UPDATE, INSTITUTION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, ACTION_GLOBAL, COMMENT_GLOBAL, COMMENT_TRANSITION, TEMPLATE_GLOBAL)), //
    INSTITUTION_CORRECT_REQUEST(INDIVIDUAL, REQUEST, INSTITUTION, //
            Lists.newArrayList(INSTITUTION_GLOBAL, ACTION_GLOBAL, COMMENT_GLOBAL, COMMENT_TRANSITION, TEMPLATE_GLOBAL)), //

    SYSTEM_CONNECTION_REQUEST(INDIVIDUAL, UPDATE, SYSTEM,//
            Lists.newArrayList(TARGET_GLOBAL, TEMPLATE_GLOBAL, ACTION_GLOBAL)), //
    SYSTEM_CONNECTION_NOTIFICATION(INDIVIDUAL, UPDATE, SYSTEM,//
            Lists.newArrayList(TARGET_GLOBAL, TEMPLATE_GLOBAL, ACTION_GLOBAL)), //
    SYSTEM_JOIN_REQUEST(INDIVIDUAL, UPDATE, SYSTEM,//
            Lists.newArrayList(TEMPLATE_GLOBAL, ACTION_GLOBAL)), //
    SYSTEM_JOIN_NOTIFICATION(INDIVIDUAL, UPDATE, SYSTEM,//
            Lists.newArrayList(TEMPLATE_GLOBAL, ACTION_GLOBAL)), //
    SYSTEM_USER_INVITATION_NOTIFICATION(INDIVIDUAL, UPDATE, SYSTEM,//
            Lists.newArrayList(TEMPLATE_GLOBAL, ACTION_GLOBAL)), //
    SYSTEM_ORGANIZATION_INVITATION_NOTIFICATION(INDIVIDUAL, UPDATE, SYSTEM,//
            Lists.newArrayList(TEMPLATE_GLOBAL, ACTION_GLOBAL, TARGET_GLOBAL)), //
    SYSTEM_COMPLETE_REGISTRATION_REQUEST(INDIVIDUAL, UPDATE, SYSTEM, //
            Lists.newArrayList(SYSTEM_USER_ACTIVATION, TEMPLATE_GLOBAL)), //
    SYSTEM_COMPLETE_REGISTRATION_FORGOTTEN_REQUEST(INDIVIDUAL, UPDATE, SYSTEM, //
            Lists.newArrayList(SYSTEM_USER_ACTIVATION, TEMPLATE_GLOBAL)), //
    SYSTEM_PASSWORD_NOTIFICATION(INDIVIDUAL, UPDATE, SYSTEM, //
            Lists.newArrayList(SYSTEM_USER_PASSWORD, TEMPLATE_GLOBAL)), //
    SYSTEM_ACTIVITY_NOTIFICATION(SYNDICATED, UPDATE, SYSTEM, //
            Lists.newArrayList(TEMPLATE_GLOBAL, ACTION_GLOBAL, SYSTEM_ACTIVITY, SYSTEM_USER_ACTIVATION));

    private final PrismNotificationType notificationType;

    private final PrismNotificationPurpose notificationPurpose;

    private final PrismScope scope;

    private final List<PrismNotificationDefinitionPropertyCategory> propertyCategories;

    private PrismNotificationDefinition(PrismNotificationType notificationType, PrismNotificationPurpose notificationPurpose, PrismScope scope,
                                        List<PrismNotificationDefinitionPropertyCategory> propertyCategories) {
        this.notificationType = notificationType;
        this.notificationPurpose = notificationPurpose;
        this.scope = scope;
        this.propertyCategories = propertyCategories;
    }

    public PrismNotificationType getNotificationType() {
        return notificationType;
    }

    public PrismNotificationPurpose getNotificationPurpose() {
        return notificationPurpose;
    }

    public PrismScope getScope() {
        return scope;
    }

    public List<PrismNotificationDefinitionPropertyCategory> getPropertyCategories() {
        return propertyCategories;
    }

    public String getInitialTemplateSubject() {
        return name().toLowerCase() + "_subject.ftl";
    }

    public String getInitialTemplateContent() {
        return name().toLowerCase() + "_content.ftl";
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_NOTIFICATION_" + name());
    }

    public static class PrismReminderDefinition {

        private PrismNotificationDefinition definition;

        private Integer defaultDuration;

        public PrismNotificationDefinition getDefinition() {
            return definition;
        }

        public final Integer getDefaultDuration() {
            return defaultDuration;
        }

        public final void setDefaultDuration(Integer defaultDuration) {
            this.defaultDuration = defaultDuration;
        }

        public PrismReminderDefinition withDefinition(PrismNotificationDefinition definition) {
            this.definition = definition;
            return this;
        }

        public PrismReminderDefinition withDefaultDuration(Integer defaultDuration) {
            this.defaultDuration = defaultDuration;
            return this;
        }

    }

}
