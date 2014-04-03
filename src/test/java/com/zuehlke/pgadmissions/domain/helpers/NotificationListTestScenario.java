package com.zuehlke.pgadmissions.domain.helpers;

public enum NotificationListTestScenario {

    TASK_REMINDER_SUCCESS(new NotificationListTestCase[] { NotificationListTestCase.DUE_TASK_REMINDER }), //
    TASK_REMINDER_FAILURE(new NotificationListTestCase[] { NotificationListTestCase.RECEIVED_RECENT_TASK_NOTIFICATION,
            NotificationListTestCase.NOTIFICATION_WINDOW_EXPIRED, NotificationListTestCase.NO_URGENT_ACTIONS, NotificationListTestCase.ACTION_NOT_SYNDICATED,
            NotificationListTestCase.USER_ACCOUNT_DISABLED }), //
    TASK_NOTIFICATION_SUCCESS(new NotificationListTestCase[] { NotificationListTestCase.DUE_TASK_NOTIFICATION_NEVER_RECEIVED,
            NotificationListTestCase.DUE_TASK_NOTIFICATION_RECEIVED_AGES_AGO }), //
    TASK_NOTIFICATION_FAILURE(new NotificationListTestCase[] { NotificationListTestCase.RECEIVED_RECENT_TASK_NOTIFICATION,
            NotificationListTestCase.NOTIFICATION_WINDOW_EXPIRED, NotificationListTestCase.NO_URGENT_ACTIONS, NotificationListTestCase.ACTION_NOT_SYNDICATED,
            NotificationListTestCase.USER_ACCOUNT_DISABLED }), //
    UPDATE_NOTIFICATION_SUCCESS(new NotificationListTestCase[] { NotificationListTestCase.DUE_UPDATE_NOTIFICATION_NEVER_RECEIVED,
            NotificationListTestCase.DUE_UPDATE_NOTIFICATION_RECEIVED_AGES_AGO }), //
    UPDATE_NOTIFICATION_FAILURE(new NotificationListTestCase[] { NotificationListTestCase.RECEIVED_RECENT_UPDATE_NOTIFICATION,
            NotificationListTestCase.ROLE_NOT_SUBSCRIBED_TO_UPDATES, NotificationListTestCase.NO_UPDATES, NotificationListTestCase.OLD_UPDATES,
            NotificationListTestCase.USER_ACCOUNT_DISABLED }), //
    OPPORTUNITY_REQUEST_NOTIFICATION_SUCCESS(new NotificationListTestCase[] { NotificationListTestCase.OPPORTUNITY_REQUEST_NOTIFICATION_NEVER_RECEIVED,
            NotificationListTestCase.OPPORTUNITY_REQUEST_NOTIFICATION_RECEIVED_AGES_AGO }), //
    OPPORTUNITY_REQUEST_NOTIFICATION_FAILURE(new NotificationListTestCase[] { NotificationListTestCase.RECEIVED_RECENT_OPPORTUNITY_REQUEST_NOTIFICATION,
            NotificationListTestCase.ROLE_NOT_SUPERADMINISTRATOR, NotificationListTestCase.USER_ACCOUNT_DISABLED });

    private NotificationListTestCase[] testCases;

    private NotificationListTestScenario(NotificationListTestCase[] testCases) {
        this.testCases = testCases;
    }

    public NotificationListTestCase[] getTestCases() {
        return testCases;
    }

}