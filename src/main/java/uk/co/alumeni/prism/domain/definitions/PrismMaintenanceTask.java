package uk.co.alumeni.prism.domain.definitions;

import uk.co.alumeni.prism.services.lifecycle.helpers.*;

public enum PrismMaintenanceTask {

    SYSTEM_EXECUTE_PENDING_STATE_ACTION(StateServiceHelperPending.class), //
    SYSTEM_EXECUTE_ESCALATED_STATE_TRANSITION(StateServiceHelperEscalation.class), //
    SYSTEM_EXECUTE_PROPAGATED_STATE_TRANSITION(StateServiceHelperPropagation.class), //
    SYSTEM_SEND_USER_REMINDER_NOTIFICATION(NotificationServiceHelperReminder.class), //
    SYSTEM_SEND_USER_ACTIVITY_NOTIFICATION(NotificationServiceHelperActivity.class), //
    SYSTEM_SEND_USER_INVITATION_NOTIFICATION(NotificationServiceHelperInvitation.class), //
    SYSTEM_CREATE_ADVERT_TARGET(AdvertServiceHelperAdvertTargetPending.class), //
    SYSTEM_UPDATE_ADVERT_FINANCIAL_DETAIL(AdvertServiceHelperAdvertFinancialDetail.class), //
    SYSTEM_EXPORT_DOCUMENT(DocumentServiceHelperExport.class), //
    SYSTEM_DELETE_DOCUMENT(DocumentServiceHelperDelete.class), //
    SYSTEM_EMAIL_BOUNCE_HANDLE(EmailBounceServiceHelper.class), //
    SYSTEM_DELETE_USER_NOTIFICATION(NotificationServiceHelperUser.class), //
    SYSTEM_NOTIFY_MESSAGES(MessageServiceHelper.class), //
    SYSTEM_CACHE_USER_ACTIVITY(UserActivityCacheServiceHelper.class);

    private Class<? extends PrismServiceHelper> executor;

    PrismMaintenanceTask(Class<? extends PrismServiceHelper> executor) {
        this.executor = executor;
    }

    public final Class<? extends PrismServiceHelper> getExecutor() {
        return executor;
    }

}
