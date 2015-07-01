package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.services.lifecycle.helpers.*;

public enum PrismMaintenanceTask {

    SYSTEM_UPDATE_PROGRAM_STUDY_OPTION(ResourceServiceHelper.class);
//    SYSTEM_EXECUTE_ESCALATED_STATE_TRANSITION(StateServiceHelperEscalation.class),
//    SYSTEM_EXECUTE_PROPAGATED_STATE_TRANSITION(StateServiceHelperPropagation.class),
//    SYSTEM_IMPORT_INSTITUTION_REFERENCE_DATA(ImportedEntityServiceHelperInstitution.class),
//    SYSTEM_EXPORT_APPLICATION(ApplicationExportServiceHelper.class),
//    SYSTEM_SEND_DEFERRED_WORKFLOW_NOTIFICATION(NotificationServiceHelperWorkflow.class),
//    SYSTEM_IMPORT_SYSTEM_REFERENCE_DATA(ImportedEntityServiceHelperSystem.class),
//    SYSTEM_UPDATE_ADVERT_CLOSING_DATE(AdvertServiceHelperClosingDate.class),
//    SYSTEM_UPDATE_ADVERT_EXCHANGE_RATE(AdvertServiceHelperExchangeRate.class),
//    SYSTEM_SEND_RECOMMENDATION_NOTIFICATION(NotificationServiceHelperRecommendation.class),
//    SYSTEM_EXPORT_DOCUMENT(DocumentServiceHelperExport.class),
//    SYSTEM_DELETE_DOCUMENT(DocumentServiceHelperDelete.class),
//    SYSTEM_EMAIL_BOUNCE_HANDLE(EmailBounceServiceHelper.class);

    private Class<? extends AbstractServiceHelper> executor;

    PrismMaintenanceTask(Class<? extends AbstractServiceHelper> executor) {
        this.executor = executor;
    }

    public final Class<? extends AbstractServiceHelper> getExecutor() {
        return executor;
    }

}
