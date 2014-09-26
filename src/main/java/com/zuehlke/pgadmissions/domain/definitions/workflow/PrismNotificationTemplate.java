package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.HashMap;

import com.google.common.collect.Maps;

public enum PrismNotificationTemplate {

    APPLICATION_COMPLETE_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION), //
    APPLICATION_COMPLETE_REQUEST(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REQUEST, PrismScope.APPLICATION), //
    APPLICATION_COMPLETE_REQUEST_REMINDER(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REMINDER, PrismScope.APPLICATION), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION), //
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION), //
    APPLICATION_CONFIRM_REJECTION_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REQUEST, PrismScope.APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_REMINDER(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REMINDER, PrismScope.APPLICATION), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REQUEST, PrismScope.APPLICATION), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REMINDER, PrismScope.APPLICATION), //
    APPLICATION_TERMINATE_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION), //
    INSTITUTION_COMPLETE_APPROVAL_STAGE_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.INSTITUTION), //    
    INSTITUTION_IMPORT_ERROR_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.SYSTEM), //
    PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.PROGRAM), //
    PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.PROJECT), //
    SYSTEM_COMPLETE_REGISTRATION_REQUEST(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REQUEST, PrismScope.SYSTEM), //
    SYSTEM_PASSWORD_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.SYSTEM), //
    SYSTEM_APPLICATION_TASK_REQUEST(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REQUEST, PrismScope.SYSTEM), //
    SYSTEM_APPLICATION_TASK_REQUEST_REMINDER(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REMINDER, PrismScope.SYSTEM), //
    SYSTEM_APPLICATION_UPDATE_NOTIFICATION(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.UPDATE, PrismScope.SYSTEM), //
    SYSTEM_INSTITUTION_TASK_REQUEST(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REQUEST, PrismScope.SYSTEM), //
    SYSTEM_INSTITUTION_TASK_REQUEST_REMINDER(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REMINDER, PrismScope.SYSTEM), //
    SYSTEM_INSTITUTION_UPDATE_NOTIFICATION(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.UPDATE, PrismScope.SYSTEM), //
    SYSTEM_PROGRAM_TASK_REQUEST(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REQUEST, PrismScope.SYSTEM), //
    SYSTEM_PROGRAM_TASK_REQUEST_REMINDER(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REMINDER, PrismScope.SYSTEM), //
    SYSTEM_PROGRAM_UPDATE_NOTIFICATION(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.UPDATE, PrismScope.SYSTEM),
    SYSTEM_PROJECT_TASK_REQUEST(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REQUEST, PrismScope.SYSTEM), //
    SYSTEM_PROJECT_TASK_REQUEST_REMINDER(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REMINDER, PrismScope.SYSTEM), //
    SYSTEM_PROJECT_UPDATE_NOTIFICATION(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.UPDATE, PrismScope.SYSTEM),
    SYSTEM_RECOMMENDATION_NOTIFICATION(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.UPDATE, PrismScope.SYSTEM);

    private final PrismNotificationType notificationType;

    private final PrismNotificationPurpose notificationPurpose;

    private final PrismScope scope;

    private static final HashMap<PrismNotificationTemplate, PrismReminderDefinition> reminderDefinitions = Maps.newHashMap();

    static {
        PrismNotificationTemplate.buildReminderDefinition(PrismNotificationTemplate.APPLICATION_COMPLETE_REQUEST,
                PrismNotificationTemplate.APPLICATION_COMPLETE_REQUEST_REMINDER, 7);
        PrismNotificationTemplate.buildReminderDefinition(PrismNotificationTemplate.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST,
                PrismNotificationTemplate.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_REMINDER, 1);
        PrismNotificationTemplate.buildReminderDefinition(PrismNotificationTemplate.APPLICATION_PROVIDE_REFERENCE_REQUEST,
                PrismNotificationTemplate.APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER, 7);
        PrismNotificationTemplate.buildReminderDefinition(PrismNotificationTemplate.SYSTEM_APPLICATION_TASK_REQUEST,
                PrismNotificationTemplate.SYSTEM_APPLICATION_TASK_REQUEST_REMINDER, 3);
        PrismNotificationTemplate.buildReminderDefinition(PrismNotificationTemplate.SYSTEM_INSTITUTION_TASK_REQUEST,
                PrismNotificationTemplate.SYSTEM_INSTITUTION_TASK_REQUEST_REMINDER, 3);
        PrismNotificationTemplate.buildReminderDefinition(PrismNotificationTemplate.SYSTEM_PROGRAM_TASK_REQUEST,
                PrismNotificationTemplate.SYSTEM_PROGRAM_TASK_REQUEST_REMINDER, 3);
        PrismNotificationTemplate.buildReminderDefinition(PrismNotificationTemplate.SYSTEM_PROJECT_TASK_REQUEST,
                PrismNotificationTemplate.SYSTEM_PROJECT_TASK_REQUEST_REMINDER, 3);
    }

    private PrismNotificationTemplate(PrismNotificationType notificationType, PrismNotificationPurpose notificationPurpose, PrismScope scope) {
        this.notificationType = notificationType;
        this.notificationPurpose = notificationPurpose;
        this.scope = scope;
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

    public String getInitialTemplateSubject() {
        return name().toLowerCase() + "_subject.ftl";
    }

    public String getInitialTemplateContent() {
        return name().toLowerCase() + "_content.ftl";
    }

    public static HashMap<PrismNotificationTemplate, PrismReminderDefinition> getReminderdefinitions() {
        return reminderDefinitions;
    }

    public static PrismNotificationTemplate getReminderTemplate(PrismNotificationTemplate template) {
        PrismReminderDefinition definition = reminderDefinitions.get(template);
        return definition != null ? definition.getTemplate() : null;
    }

    public static Integer getReminderInterval(PrismNotificationTemplate template) {
        PrismReminderDefinition definition = reminderDefinitions.get(template);
        return definition != null ? reminderDefinitions.get(template).getInterval() : null;
    }

    private static void buildReminderDefinition(PrismNotificationTemplate template, PrismNotificationTemplate reminder, int interval) {
        reminderDefinitions.put(template, new PrismReminderDefinition().withTemplate(reminder).withInterval(interval));
    }

    public static class PrismReminderDefinition {

        private PrismNotificationTemplate template;

        private Integer interval;

        public PrismNotificationTemplate getTemplate() {
            return template;
        }

        public int getInterval() {
            return interval;
        }

        public PrismNotificationTemplate.PrismReminderDefinition withTemplate(PrismNotificationTemplate template) {
            this.template = template;
            return this;
        }

        public PrismNotificationTemplate.PrismReminderDefinition withInterval(int interval) {
            this.interval = interval;
            return this;
        }

    }

}