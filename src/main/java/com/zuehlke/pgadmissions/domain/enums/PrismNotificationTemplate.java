package com.zuehlke.pgadmissions.domain.enums;

import java.util.HashMap;

import com.google.common.collect.Maps;

public enum PrismNotificationTemplate {

    APPLICATION_COMPLETE_NOTIFICATION(PrismNotificationType.INDIVIDUAL, "application_complete_notification_title.ftl", "application_complete_notification.ftl"), //
    APPLICATION_COMPLETE_REQUEST(PrismNotificationType.INDIVIDUAL, "application_complete_request_title.ftl", "application_complete_request.ftl"), //
    APPLICATION_COMPLETE_REQUEST_REMINDER(PrismNotificationType.INDIVIDUAL, "application_complete_request_reminder_title.ftl",
            "application_complete_request_reminder.ftl"), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION(PrismNotificationType.INDIVIDUAL,
            "application_confirm_interview_arrangments_notification_title.ftl", "application_confirm_interview_arrangments_notification.ftl"), //
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION(PrismNotificationType.INDIVIDUAL, "application_confirm_offer_recommendation_notification_title.ftl",
            "application_confirm_offer_recommendation_notification.ftl"), //
    APPLICATION_CONFIRM_REJECTION_NOTIFICATION(PrismNotificationType.INDIVIDUAL, "application_confirm_rejection_notification_title.ftl",
            "application_confirm_rejection_notification.ftl"), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION(PrismNotificationType.INDIVIDUAL,
            "application_provide_interview_availability_notification_title.ftl", "application_provide_interview_availability_notification.ftl"), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST(PrismNotificationType.INDIVIDUAL, "application_provide_interview_availability_request_title.ftl",
            "application_provide_interview_availability_request.ftl"), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_REMINDER(PrismNotificationType.INDIVIDUAL,
            "application_provide_interview_availability_request_reminder_title.ftl", "application_provide_interview_availability_request_reminder.ftl"), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST(PrismNotificationType.INDIVIDUAL, "application_provide_reference_request_title.ftl",
            "application_provide_reference_request.ftl"), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER(PrismNotificationType.INDIVIDUAL, "application_provide_reference_request_reminder_title.ftl",
            "application_provide_reference_request_reminder.ftl"), //
    APPLICATION_TERMINATE_NOTIFICATION(PrismNotificationType.INDIVIDUAL, "application_terminate_notification_title.ftl",
            "application_terminate_notification.ftl"), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION(PrismNotificationType.INDIVIDUAL,
            "application_update_interview_availability_notification_title.ftl", "application_update_interview_availability_notification.ftl"), //
    PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION(PrismNotificationType.INDIVIDUAL, "program_complete_approval_stage_notification_title.ftl",
            "program_complete_approval_stage_notification.ftl"), //
    SYSTEM_COMPLETE_REGISTRATION_REQUEST(PrismNotificationType.INDIVIDUAL, "system_complete_registration_request_title.ftl",
            "system_complete_registration_request.ftl"), //
    SYSTEM_IMPORT_ERROR_NOTIFICATION(PrismNotificationType.INDIVIDUAL, "system_import_error_notification_title.ftl", "system_import_error_notification.ftl"), //
    SYSTEM_PASSWORD_NOTIFICATION(PrismNotificationType.INDIVIDUAL, "system_password_notification_title.ftl", "system_password_notification.ftl"), //
    APPLICATION_TASK_REQUEST(PrismNotificationType.SYNDICATED, "application_task_request_title.ftl", "application_task_request.ftl"), //
    APPLICATION_TASK_REQUEST_REMINDER(PrismNotificationType.SYNDICATED, "application_task_request_reminder_title.ftl", "application_task_request_reminder.ftl"), //
    APPLICATION_UPDATE_NOTIFICATION(PrismNotificationType.SYNDICATED, "application_update_notification_title.ftl", "application_update_notification.ftl"), //
    PROGRAM_TASK_REQUEST(PrismNotificationType.SYNDICATED, "program_task_request_title.ftl", "program_task_request.ftl"), //
    PROGRAM_TASK_REQUEST_REMINDER(PrismNotificationType.SYNDICATED, "program_task_request_reminder_title.ftl", "program_task_request_reminder.ftl"), //
    PROGRAM_UPDATE_NOTIFICATION(PrismNotificationType.SYNDICATED, "program_update_notification_title.ftl", "program_update_notification.ftl"), //
    PROJECT_TASK_REQUEST(PrismNotificationType.SYNDICATED, "project_task_request_title.ftl", "project_task_request.ftl"), //
    PROJECT_TASK_REQUEST_REMINDER(PrismNotificationType.SYNDICATED, "project_task_request_reminder_title.ftl", "project_task_request_reminder.ftl"), //
    PROJECT_UPDATE_NOTIFICATION(PrismNotificationType.SYNDICATED, "project_update_notification_title.ftl", "project_update_notification.ftl");

    private final PrismNotificationType notificationType;

    private String initialTemplateSubject;

    private final String initialTemplateContent;

    private static final HashMap<PrismNotificationTemplate, ReminderDefinition> reminderDefinitions = Maps.newHashMap();

    static {
        PrismNotificationTemplate.buildReminderDefinition(PrismNotificationTemplate.APPLICATION_COMPLETE_NOTIFICATION,
                PrismNotificationTemplate.APPLICATION_COMPLETE_REQUEST_REMINDER, 7);
        PrismNotificationTemplate.buildReminderDefinition(PrismNotificationTemplate.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST,
                PrismNotificationTemplate.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_REMINDER, 1);
        PrismNotificationTemplate.buildReminderDefinition(PrismNotificationTemplate.APPLICATION_PROVIDE_REFERENCE_REQUEST,
                PrismNotificationTemplate.APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER, 7);
        PrismNotificationTemplate.buildReminderDefinition(PrismNotificationTemplate.APPLICATION_TASK_REQUEST,
                PrismNotificationTemplate.APPLICATION_TASK_REQUEST_REMINDER, 3);
        PrismNotificationTemplate.buildReminderDefinition(PrismNotificationTemplate.PROGRAM_TASK_REQUEST,
                PrismNotificationTemplate.PROGRAM_TASK_REQUEST_REMINDER, 3);
        PrismNotificationTemplate.buildReminderDefinition(PrismNotificationTemplate.PROJECT_TASK_REQUEST,
                PrismNotificationTemplate.PROJECT_TASK_REQUEST_REMINDER, 3);
    }

    private PrismNotificationTemplate(PrismNotificationType notificationType, String initialTemplateSubject, String initialTemplateContent) {
        this.notificationType = notificationType;
        this.initialTemplateSubject = initialTemplateSubject;
        this.initialTemplateContent = initialTemplateContent;
    }

    public PrismNotificationType getNotificationType() {
        return notificationType;
    }

    public String getInitialTemplateSubject() {
        return initialTemplateSubject;
    }

    public String getInitialTemplateContent() {
        return initialTemplateContent;
    }

    public static HashMap<PrismNotificationTemplate, ReminderDefinition> getReminderdefinitions() {
        return reminderDefinitions;
    }

    public static PrismNotificationTemplate getReminderTemplate(PrismNotificationTemplate template) {
        ReminderDefinition definition = reminderDefinitions.get(template);
        return definition != null ? definition.getTemplate() : null;
    }

    public static Integer getReminderInterval(PrismNotificationTemplate template) {
        ReminderDefinition definition = reminderDefinitions.get(template);
        return definition != null ? reminderDefinitions.get(template).getInterval() : null;
    }

    private static void buildReminderDefinition(PrismNotificationTemplate template, PrismNotificationTemplate reminder, int interval) {
        ReminderDefinition definition = new PrismNotificationTemplate.ReminderDefinition(template, interval);
        reminderDefinitions.put(template, definition);
    }

    private static class ReminderDefinition {

        private PrismNotificationTemplate template;

        private int interval;

        public ReminderDefinition(PrismNotificationTemplate template, int interval) {
            this.template = template;
            this.interval = interval;
        }

        public PrismNotificationTemplate getTemplate() {
            return template;
        }

        public int getInterval() {
            return interval;
        }

    }

}
