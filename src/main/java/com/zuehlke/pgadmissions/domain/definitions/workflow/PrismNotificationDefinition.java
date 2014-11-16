package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationPurpose.REMINDER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationPurpose.REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationPurpose.UPDATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.ACTION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.APPLICATION_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.APPLICATION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.APPLICATION_INTERVIEW_SCHEDULED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.APPLICATION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.COMMENT_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.COMMENT_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.INSTITUTION_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.INSTITUTION_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.PROGRAM_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.PROJECT_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.SYSTEM_APPLICATION_MARKETING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.SYSTEM_APPLICATION_SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.SYSTEM_INSTITUTION_SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.SYSTEM_PROGRAM_SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.SYSTEM_PROJECT_SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.SYSTEM_USER_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.SYSTEM_USER_PASSWORD;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory.TEMPLATE_GLOBAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType.INDIVIDUAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType.SYNDICATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public enum PrismNotificationDefinition {

    APPLICATION_COMPLETE_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, COMMENT_GLOBAL,
            TEMPLATE_GLOBAL)), //
    APPLICATION_COMPLETE_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_COMPLETE_REQUEST_REMINDER(INDIVIDUAL, REMINDER, APPLICATION, Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL,
            TEMPLATE_GLOBAL)), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL,
            APPLICATION_INTERVIEW_SCHEDULED, ACTION_GLOBAL, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL,
            APPLICATION_APPROVED, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_CONFIRM_REJECTION_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL,
            APPLICATION_REJECTED, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL,
            COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL,
            ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_REMINDER(INDIVIDUAL, REMINDER, APPLICATION, Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL,
            ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST(INDIVIDUAL, REQUEST, APPLICATION, Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL,
            TEMPLATE_GLOBAL)), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER(INDIVIDUAL, REMINDER, APPLICATION, Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, ACTION_GLOBAL,
            TEMPLATE_GLOBAL)), //
    APPLICATION_TERMINATE_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL, APPLICATION_REJECTED,
            COMMENT_GLOBAL, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION(INDIVIDUAL, UPDATE, APPLICATION, Lists.newArrayList(INSTITUTION_GLOBAL, APPLICATION_GLOBAL,
            ACTION_GLOBAL, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    INSTITUTION_COMPLETE_APPROVAL_STAGE_NOTIFICATION(INDIVIDUAL, UPDATE, INSTITUTION, Lists.newArrayList(INSTITUTION_GLOBAL, COMMENT_GLOBAL,
            COMMENT_TRANSITION, TEMPLATE_GLOBAL)), //
    INSTITUTION_CORRECT_REQUEST(INDIVIDUAL, REQUEST, INSTITUTION, Lists.newArrayList(INSTITUTION_GLOBAL, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    INSTITUTION_CORRECT_REQUEST_REMINDER(INDIVIDUAL, REMINDER, INSTITUTION, Lists.newArrayList(INSTITUTION_GLOBAL, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    INSTITUTION_STARTUP_NOTIFICATION(INDIVIDUAL, UPDATE, INSTITUTION, Lists.newArrayList(INSTITUTION_GLOBAL, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    INSTITUTION_IMPORT_ERROR_NOTIFICATION(INDIVIDUAL, UPDATE, INSTITUTION, Lists.newArrayList(INSTITUTION_GLOBAL, INSTITUTION_APPROVED, COMMENT_GLOBAL,
            TEMPLATE_GLOBAL)), //
    PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION(INDIVIDUAL, UPDATE, PROGRAM, Lists.newArrayList(INSTITUTION_GLOBAL, PROGRAM_GLOBAL, COMMENT_GLOBAL,
            COMMENT_TRANSITION, TEMPLATE_GLOBAL)), //
    PROGRAM_CORRECT_REQUEST(INDIVIDUAL, REQUEST, PROGRAM, Lists.newArrayList(INSTITUTION_GLOBAL, PROGRAM_GLOBAL, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    PROGRAM_CORRECT_REQUEST_REMINDER(INDIVIDUAL, REMINDER, PROGRAM, Lists.newArrayList(INSTITUTION_GLOBAL, PROGRAM_GLOBAL, COMMENT_GLOBAL, TEMPLATE_GLOBAL)), //
    PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION(INDIVIDUAL, UPDATE, PROJECT, Lists.newArrayList(INSTITUTION_GLOBAL, PROGRAM_GLOBAL, PROJECT_GLOBAL,
            COMMENT_GLOBAL, COMMENT_TRANSITION, TEMPLATE_GLOBAL)), //
    PROJECT_CORRECT_REQUEST(INDIVIDUAL, REQUEST, PROJECT, Lists.newArrayList(INSTITUTION_GLOBAL, PROGRAM_GLOBAL, PROJECT_GLOBAL, COMMENT_GLOBAL,
            TEMPLATE_GLOBAL)), //
    PROJECT_CORRECT_REQUEST_REMINDER(INDIVIDUAL, REMINDER, PROJECT, Lists.newArrayList(INSTITUTION_GLOBAL, PROGRAM_GLOBAL, PROJECT_GLOBAL, COMMENT_GLOBAL,
            TEMPLATE_GLOBAL)), //
    SYSTEM_INVITATION_NOTIFICATION(INDIVIDUAL, UPDATE, SYSTEM, Lists.newArrayList(SYSTEM_USER_ACCOUNT, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    SYSTEM_COMPLETE_REGISTRATION_REQUEST(INDIVIDUAL, REQUEST, SYSTEM, Lists.newArrayList(SYSTEM_USER_ACCOUNT, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    SYSTEM_PASSWORD_NOTIFICATION(INDIVIDUAL, UPDATE, SYSTEM, Lists.newArrayList(SYSTEM_USER_PASSWORD, TEMPLATE_GLOBAL)), //
    SYSTEM_APPLICATION_TASK_REQUEST(SYNDICATED, REQUEST, SYSTEM, Lists.newArrayList(SYSTEM_APPLICATION_SYNDICATED, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    SYSTEM_APPLICATION_TASK_REQUEST_REMINDER(SYNDICATED, REMINDER, SYSTEM, Lists.newArrayList(SYSTEM_APPLICATION_SYNDICATED, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    SYSTEM_APPLICATION_UPDATE_NOTIFICATION(SYNDICATED, UPDATE, SYSTEM, Lists.newArrayList(SYSTEM_APPLICATION_SYNDICATED, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    SYSTEM_APPLICATION_RECOMMENDATION_NOTIFICATION(SYNDICATED, UPDATE, SYSTEM, Lists.newArrayList(SYSTEM_APPLICATION_MARKETING, TEMPLATE_GLOBAL)), //
    SYSTEM_INSTITUTION_TASK_REQUEST(SYNDICATED, REQUEST, SYSTEM, Lists.newArrayList(SYSTEM_INSTITUTION_SYNDICATED, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    SYSTEM_INSTITUTION_TASK_REQUEST_REMINDER(SYNDICATED, REMINDER, SYSTEM, Lists.newArrayList(SYSTEM_INSTITUTION_SYNDICATED, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    SYSTEM_INSTITUTION_UPDATE_NOTIFICATION(SYNDICATED, UPDATE, SYSTEM, Lists.newArrayList(SYSTEM_INSTITUTION_SYNDICATED, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    SYSTEM_PROGRAM_TASK_REQUEST(SYNDICATED, REQUEST, SYSTEM, Lists.newArrayList(SYSTEM_PROGRAM_SYNDICATED, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    SYSTEM_PROGRAM_TASK_REQUEST_REMINDER(SYNDICATED, REMINDER, SYSTEM, Lists.newArrayList(SYSTEM_PROGRAM_SYNDICATED, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    SYSTEM_PROGRAM_UPDATE_NOTIFICATION(SYNDICATED, UPDATE, SYSTEM, Lists.newArrayList(SYSTEM_PROGRAM_SYNDICATED, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    SYSTEM_PROJECT_TASK_REQUEST(SYNDICATED, REQUEST, SYSTEM, Lists.newArrayList(SYSTEM_PROJECT_SYNDICATED, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    SYSTEM_PROJECT_TASK_REQUEST_REMINDER(SYNDICATED, REMINDER, SYSTEM, Lists.newArrayList(SYSTEM_PROJECT_SYNDICATED, ACTION_GLOBAL, TEMPLATE_GLOBAL)), //
    SYSTEM_PROJECT_UPDATE_NOTIFICATION(SYNDICATED, UPDATE, SYSTEM, Lists.newArrayList(SYSTEM_PROJECT_SYNDICATED, ACTION_GLOBAL, TEMPLATE_GLOBAL)); //

    private final PrismNotificationType notificationType;

    private final PrismNotificationPurpose notificationPurpose;

    private final PrismScope scope;

    private final List<PrismNotificationTemplatePropertyCategory> propertyCategories;

    private static final HashMap<PrismNotificationDefinition, PrismReminderDefinition> reminderDefinitions = Maps.newHashMap();

    static {
        buildReminderDefinition(APPLICATION_COMPLETE_REQUEST, APPLICATION_COMPLETE_REQUEST_REMINDER, 7);
        buildReminderDefinition(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_REMINDER, 1);
        buildReminderDefinition(APPLICATION_PROVIDE_REFERENCE_REQUEST, APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER, 7);
        buildReminderDefinition(INSTITUTION_CORRECT_REQUEST, INSTITUTION_CORRECT_REQUEST_REMINDER, 3);
        buildReminderDefinition(PROGRAM_CORRECT_REQUEST, PROGRAM_CORRECT_REQUEST_REMINDER, 3);
        buildReminderDefinition(PROJECT_CORRECT_REQUEST, PROJECT_CORRECT_REQUEST_REMINDER, 3);
        buildReminderDefinition(SYSTEM_APPLICATION_TASK_REQUEST, SYSTEM_APPLICATION_TASK_REQUEST_REMINDER, 3);
        buildReminderDefinition(SYSTEM_INSTITUTION_TASK_REQUEST, SYSTEM_INSTITUTION_TASK_REQUEST_REMINDER, 3);
        buildReminderDefinition(SYSTEM_PROGRAM_TASK_REQUEST, SYSTEM_PROGRAM_TASK_REQUEST_REMINDER, 3);
        buildReminderDefinition(SYSTEM_PROJECT_TASK_REQUEST, SYSTEM_PROJECT_TASK_REQUEST_REMINDER, 3);
    }

    private PrismNotificationDefinition(PrismNotificationType notificationType, PrismNotificationPurpose notificationPurpose, PrismScope scope,
            List<PrismNotificationTemplatePropertyCategory> propertyCategories) {
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

    public List<PrismNotificationTemplatePropertyCategory> getPropertyCategories() {
        return propertyCategories;
    }

    public String getInitialTemplateSubject() {
        return name().toLowerCase() + "_subject.ftl";
    }

    public String getInitialTemplateContent() {
        return name().toLowerCase() + "_content.ftl";
    }

    public static final HashMap<PrismNotificationDefinition, PrismReminderDefinition> getReminderDefinitions() {
        return reminderDefinitions;
    }

    public final PrismNotificationDefinition getReminderDefinition() {
        PrismReminderDefinition reminder = reminderDefinitions.get(this);
        return reminder == null ? null : reminder.getDefinition();
    }

    public final Integer getDefaultReminderDuration() {
        PrismReminderDefinition reminder = reminderDefinitions.get(this);
        return reminder == null ? null : reminder.getDefaultDuration();
    }

    private static void buildReminderDefinition(PrismNotificationDefinition template, PrismNotificationDefinition reminder, Integer defaultDuration) {
        reminderDefinitions.put(template, new PrismReminderDefinition().withDefinition(reminder).withDefaultDuration(defaultDuration));
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
