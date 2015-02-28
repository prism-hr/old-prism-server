package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.services.lifecycle.helpers.AbstractServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.AdvertServiceHelperClosingDate;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.AdvertServiceHelperExchangeRate;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ApplicationExportServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.DocumentServiceHelperDelete;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.DocumentServiceHelperExport;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.EmailBounceServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperInstitution;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperSystem;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.InstitutionServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.NotificationServiceHelperRecommendation;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.NotificationServiceHelperWorkflow;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ProgramServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.StateServiceHelperEscalation;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.StateServiceHelperPropagation;

public enum MaintenanceTask {

    SYSTEM_EXPORT_APPLICATION(ApplicationExportServiceHelper.class, true, false), //
    SYSTEM_EXECUTE_ESCALATED_STATE_TRANSITION(StateServiceHelperEscalation.class, true, false), //
    SYSTEM_EXECUTE_PROPAGATED_STATE_TRANSITION(StateServiceHelperPropagation.class, true, false), //
    SYSTEM_IMPORT_SYSTEM_REFERENCE_DATA(ImportedEntityServiceHelperSystem.class, true, true), //
    SYSTEM_IMPORT_INSTITUTION_REFERENCE_DATA(ImportedEntityServiceHelperInstitution.class, true, false), //
    SYSTEM_STARTUP_INSTITUTION(InstitutionServiceHelper.class, true, true), //
    SYSTEM_UPDATE_PROGRAM_STUDY_OPTION(ProgramServiceHelper.class, true, true), //
    SYSTEM_UPDATE_ADVERT_CLOSING_DATE(AdvertServiceHelperClosingDate.class, true, true), //
    SYSTEM_UPDATE_ADVERT_EXCHANGE_RATE(AdvertServiceHelperExchangeRate.class, true, true), //
    SYSTEM_SEND_DEFERRED_WORKFLOW_NOTIFICATION(NotificationServiceHelperWorkflow.class, true, false), //
    SYSTEM_SEND_RECOMMENDATION_NOTIFICATION(NotificationServiceHelperRecommendation.class, true, true), //
    SYSTEM_EXPORT_DOCUMENT(DocumentServiceHelperExport.class, true, true), //
    SYSTEM_DELETE_DOCUMENT(DocumentServiceHelperDelete.class, true, true),
    SYSTEM_EMAIL_BOUNCE_HANDLE(EmailBounceServiceHelper.class, true, true);

    private Class<? extends AbstractServiceHelper> executor;

    private boolean execute;

    boolean parallelize;

    private MaintenanceTask(Class<? extends AbstractServiceHelper> executor, boolean execute, boolean parallelize) {
        this.executor = executor;
        this.execute = execute;
        this.parallelize = parallelize;
    }

    public final Class<? extends AbstractServiceHelper> getExecutor() {
        return executor;
    }

    public final boolean isExecute() {
        return execute;
    }

    public final boolean isParallelize() {
        return parallelize;
    }

}
