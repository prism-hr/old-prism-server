package com.zuehlke.pgadmissions.domain.enums;

import java.util.HashMap;

import com.google.common.collect.Maps;

public enum PrismNotificationTemplate {

    APPLICATION_COMPLETE_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION,
            "application_complete_notification_subject.ftl", "application_complete_notification_content.ftl"), //
    APPLICATION_COMPLETE_REQUEST(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REQUEST, PrismScope.APPLICATION,
            "application_complete_request_subject.ftl", "application_complete_request_content.ftl"), //
    APPLICATION_COMPLETE_REQUEST_REMINDER(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REMINDER, PrismScope.APPLICATION,
            "application_complete_request_reminder_subject.ftl", "application_complete_request_reminder_content.ftl"), //
    APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION,
            "application_confirm_interview_arrangments_notification_subject.ftl", "application_confirm_interview_arrangments_notification_content.ftl"), //
    APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION,
            "application_confirm_offer_recommendation_notification_subject.ftl", "application_confirm_offer_recommendation_notification_content.ftl"), //
    APPLICATION_CONFIRM_REJECTION_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION,
            "application_confirm_rejection_notification_subject.ftl", "application_confirm_rejection_notification_content.ftl"), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION,
            "application_provide_interview_availability_notification_subject.ftl", "application_provide_interview_availability_notification_content.ftl"), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REQUEST, PrismScope.APPLICATION,
            "application_provide_interview_availability_request_subject.ftl", "application_provide_interview_availability_request_content.ftl"), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_REMINDER(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REMINDER, PrismScope.APPLICATION,
            "application_provide_interview_availability_request_reminder_subject.ftl",
            "application_provide_interview_availability_request_reminder_content.ftl"), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REQUEST, PrismScope.APPLICATION,
            "application_provide_reference_request_subject.ftl", "application_provide_reference_request_content.ftl"), //
    APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REMINDER, PrismScope.APPLICATION,
            "application_provide_reference_request_reminder_subject.ftl", "application_provide_reference_request_reminder_content.ftl"), //
    APPLICATION_TASK_REQUEST(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REQUEST, PrismScope.APPLICATION,
            "application_task_request_subject.ftl", "application_task_request_content.ftl"), //
    APPLICATION_TASK_REQUEST_REMINDER(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REMINDER, PrismScope.APPLICATION,
            "application_task_request_reminder_subject.ftl", "application_task_request_reminder_content.ftl"), //
    APPLICATION_TERMINATE_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION,
            "application_terminate_notification_subject.ftl", "application_terminate_notification_content.ftl"), //
    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION,
            "application_update_interview_availability_notification_subject.ftl", "application_update_interview_availability_notification_content.ftl"), //
    APPLICATION_UPDATE_NOTIFICATION(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.UPDATE, PrismScope.APPLICATION,
            "application_update_notification_subject.ftl", "application_update_notification_content.ftl"), //
    PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.PROGRAM,
            "program_complete_approval_stage_notification_subject.ftl", "program_complete_approval_stage_notification_content.ftl"), //
    PROGRAM_TASK_REQUEST(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REQUEST, PrismScope.PROGRAM, "program_task_request_subject.ftl",
            "program_task_request_content.ftl"), //
    PROGRAM_TASK_REQUEST_REMINDER(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REMINDER, PrismScope.PROGRAM,
            "program_task_request_reminder_subject.ftl", "program_task_request_reminder_content.ftl"), //
    PROGRAM_UPDATE_NOTIFICATION(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.UPDATE, PrismScope.PROGRAM,
            "program_update_notification_subject.ftl", "program_update_notification_content.ftl"), //
    PROJECT_TASK_REQUEST(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REQUEST, PrismScope.PROJECT, "project_task_request_subject.ftl",
            "project_task_request_content.ftl"), //
    PROJECT_TASK_REQUEST_REMINDER(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.REMINDER, PrismScope.PROJECT,
            "project_task_request_reminder_subject.ftl", "project_task_request_reminder_content.ftl"), //
    PROJECT_UPDATE_NOTIFICATION(PrismNotificationType.SYNDICATED, PrismNotificationPurpose.UPDATE, PrismScope.PROJECT,
            "project_update_notification_subject.ftl", "project_update_notification_content.ftl"), //
    SYSTEM_COMPLETE_REGISTRATION_REQUEST(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.REQUEST, PrismScope.SYSTEM,
            "system_complete_registration_request_subject.ftl", "system_complete_registration_request_content.ftl"), //
    SYSTEM_IMPORT_ERROR_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.SYSTEM,
            "system_import_error_notification_subject.ftl", "system_import_error_notification_content.ftl"), //
    SYSTEM_PASSWORD_NOTIFICATION(PrismNotificationType.INDIVIDUAL, PrismNotificationPurpose.UPDATE, PrismScope.SYSTEM,
            "system_password_notification_subject.ftl", "system_password_notification_content.ftl");

    private final PrismNotificationType notificationType;

    private final PrismNotificationPurpose notificationPurpose;

    private final PrismScope scope;

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

    private PrismNotificationTemplate(PrismNotificationType notificationType, PrismNotificationPurpose notificationPurpose, PrismScope scope,
            String initialTemplateSubject, String initialTemplateContent) {
        this.notificationType = notificationType;
        this.notificationPurpose = notificationPurpose;
        this.scope = scope;
        this.initialTemplateSubject = initialTemplateSubject;
        this.initialTemplateContent = initialTemplateContent;
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
