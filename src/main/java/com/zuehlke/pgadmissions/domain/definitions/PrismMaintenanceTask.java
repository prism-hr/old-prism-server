package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.services.lifecycle.helpers.AdvertServiceHelperClosingDate;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.AdvertServiceHelperExchangeRate;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.DocumentServiceHelperDelete;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.DocumentServiceHelperExport;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.EmailBounceServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.InstitutionServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.NotificationServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.PrismServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.StateServiceHelperEscalation;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.StateServiceHelperPropagation;

public enum PrismMaintenanceTask {

    SYSTEM_IMPORT_UCAS_INSTITUTION(InstitutionServiceHelper.class),
    SYSTEM_EXECUTE_ESCALATED_STATE_TRANSITION(StateServiceHelperEscalation.class),
    SYSTEM_EXECUTE_PROPAGATED_STATE_TRANSITION(StateServiceHelperPropagation.class),
    SYSTEM_SEND_USER_ACTIVITY_NOTIFICATION(NotificationServiceHelper.class), 
    SYSTEM_UPDATE_ADVERT_CLOSING_DATE(AdvertServiceHelperClosingDate.class),
    SYSTEM_UPDATE_ADVERT_EXCHANGE_RATE(AdvertServiceHelperExchangeRate.class),
    SYSTEM_EXPORT_DOCUMENT(DocumentServiceHelperExport.class),
    SYSTEM_DELETE_DOCUMENT(DocumentServiceHelperDelete.class),
    SYSTEM_EMAIL_BOUNCE_HANDLE(EmailBounceServiceHelper.class);

    private Class<? extends PrismServiceHelper> executor;

    PrismMaintenanceTask(Class<? extends PrismServiceHelper> executor) {
        this.executor = executor;
    }

    public final Class<? extends PrismServiceHelper> getExecutor() {
        return executor;
    }

}
