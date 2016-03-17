package uk.co.alumeni.prism.domain.definitions;

import uk.co.alumeni.prism.services.lifecycle.helpers.AdvertServiceHelperAdvertFinancialDetail;
import uk.co.alumeni.prism.services.lifecycle.helpers.AdvertServiceHelperAdvertTargetPending;
import uk.co.alumeni.prism.services.lifecycle.helpers.DocumentServiceHelperDelete;
import uk.co.alumeni.prism.services.lifecycle.helpers.DocumentServiceHelperExport;
import uk.co.alumeni.prism.services.lifecycle.helpers.EmailBounceServiceHelper;
import uk.co.alumeni.prism.services.lifecycle.helpers.MessageServiceHelper;
import uk.co.alumeni.prism.services.lifecycle.helpers.NotificationServiceHelperActivity;
import uk.co.alumeni.prism.services.lifecycle.helpers.NotificationServiceHelperInvitation;
import uk.co.alumeni.prism.services.lifecycle.helpers.NotificationServiceHelperReminder;
import uk.co.alumeni.prism.services.lifecycle.helpers.NotificationServiceHelperUser;
import uk.co.alumeni.prism.services.lifecycle.helpers.PrismServiceHelper;
import uk.co.alumeni.prism.services.lifecycle.helpers.StateServiceHelperEscalation;
import uk.co.alumeni.prism.services.lifecycle.helpers.StateServiceHelperPending;
import uk.co.alumeni.prism.services.lifecycle.helpers.StateServiceHelperPropagation;
import uk.co.alumeni.prism.services.lifecycle.helpers.UserServiceHelper;

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
    SYSTEM_CACHE_USER_ACTIVITY(UserServiceHelper.class);

    private Class<? extends PrismServiceHelper> executor;

    PrismMaintenanceTask(Class<? extends PrismServiceHelper> executor) {
        this.executor = executor;
    }

    public final Class<? extends PrismServiceHelper> getExecutor() {
        return executor;
    }

}
