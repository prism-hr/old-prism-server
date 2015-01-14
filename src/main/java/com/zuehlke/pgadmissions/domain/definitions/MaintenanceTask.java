package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.services.lifecycle.helpers.AbstractServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.AdvertServiceHelperClosingDate;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.AdvertServiceHelperExchangeRate;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ApplicationExportServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.DocumentServiceHelperDelete;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.DocumentServiceHelperExport;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperInstitution;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperSystem;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.InstitutionServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.NotificationServiceHelperRecommendation;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.NotificationServiceHelperWorkflow;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ProgramServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.StateServiceHelperEscalation;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.StateServiceHelperPropagation;

public enum MaintenanceTask {

    SYSTEM_EXPORT_APPLICATION(ApplicationExportServiceHelper.class, false, false), //
    SYSTEM_EXECUTE_ESCALATED_STATE_TRANSITION(StateServiceHelperEscalation.class, false, false), //
    SYSTEM_EXECUTE_PROPAGATED_STATE_TRANSITION(StateServiceHelperPropagation.class, false, false), //
    SYSTEM_IMPORT_SYSTEM_REFERENCE_DATA(ImportedEntityServiceHelperSystem.class, false, true), //
    SYSTEM_IMPORT_INSTITUTION_REFERENCE_DATA(ImportedEntityServiceHelperInstitution.class, false, false), //
    SYSTEM_STARTUP_INSTITUTION(InstitutionServiceHelper.class, false, true), //
    SYSTEM_UPDATE_PROGRAM_STUDY_OPTION(ProgramServiceHelper.class, false, true), //
    SYSTEM_UPDATE_ADVERT_CLOSING_DATE(AdvertServiceHelperClosingDate.class, false, true), //
    SYSTEM_UPDATE_ADVERT_EXCHANGE_RATE(AdvertServiceHelperExchangeRate.class, false, true), //
    SYSTEM_SEND_DEFERRED_WORKFLOW_NOTIFICATION(NotificationServiceHelperWorkflow.class, false, false), //
    SYSTEM_SEND_RECOMMENDATION_NOTIFICATION(NotificationServiceHelperRecommendation.class, false, true), //
    SYSTEM_EXPORT_DOCUMENT(DocumentServiceHelperExport.class, true, true), //
    SYSTEM_DELETE_UNUSED_DOCUMENT(DocumentServiceHelperDelete.class, true, true);

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
