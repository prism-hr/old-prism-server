package com.zuehlke.pgadmissions.domain.definitions;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.services.helpers.AbstractServiceHelper;
import com.zuehlke.pgadmissions.services.helpers.AdvertServiceHelper;
import com.zuehlke.pgadmissions.services.helpers.ApplicationExportServiceHelper;
import com.zuehlke.pgadmissions.services.helpers.DocumentServiceHelper;
import com.zuehlke.pgadmissions.services.helpers.ImportedEntityServiceHelperSystem;
import com.zuehlke.pgadmissions.services.helpers.NotificationServiceHelperRecommendation;
import com.zuehlke.pgadmissions.services.helpers.NotificationServiceHelperWorkflow;
import com.zuehlke.pgadmissions.services.helpers.ProgramServiceHelper;
import com.zuehlke.pgadmissions.services.helpers.StateServiceHelper;

public enum MaintenanceTask {

    SYSTEM_EXECUTE_DEFERRED_STATE_TRANSITION(StateServiceHelper.class),
    SYSTEM_IMPORT_SYSTEM_REFERENCE_DATA(ImportedEntityServiceHelperSystem.class),
    SYSTEM_IMPORT_INSTITUTION_REFERENCE_DATA(null),
    SYSTEM_UPDATE_PROGRAM_STUDY_OPTION(ProgramServiceHelper.class),
    SYSTEM_UPDATE_ADVERT_CLOSING_DATE(AdvertServiceHelper.class),
    SYSTEM_EXPORT_APPLICATION(ApplicationExportServiceHelper.class),
    SYSTEM_SEND_DEFERRED_WORKFLOW_NOTIFICATION(NotificationServiceHelperWorkflow.class),
    SYSTEM_SEND_RECOMMENDATION_NOTIFICATION(NotificationServiceHelperRecommendation.class),
    SYSTEM_DELETE_UNUSED_DOCUMENT(DocumentServiceHelper.class);
    
    private Class<? extends AbstractServiceHelper> executor;
    
    private static final HashMultimap<MaintenanceTask, MaintenanceTask> preconditions = HashMultimap.create();
    
    static {
        for (MaintenanceTask task : values()) {
            if (task != SYSTEM_EXECUTE_DEFERRED_STATE_TRANSITION) {
                preconditions.put(task, SYSTEM_EXECUTE_DEFERRED_STATE_TRANSITION);
            }
        }
        preconditions.put(SYSTEM_IMPORT_INSTITUTION_REFERENCE_DATA, SYSTEM_IMPORT_SYSTEM_REFERENCE_DATA);
        preconditions.put(SYSTEM_UPDATE_PROGRAM_STUDY_OPTION, SYSTEM_IMPORT_INSTITUTION_REFERENCE_DATA);
        preconditions.put(SYSTEM_UPDATE_ADVERT_CLOSING_DATE, SYSTEM_IMPORT_INSTITUTION_REFERENCE_DATA);
        preconditions.put(SYSTEM_EXPORT_APPLICATION, SYSTEM_IMPORT_INSTITUTION_REFERENCE_DATA);
    }

    private MaintenanceTask(Class<? extends AbstractServiceHelper> executor) {
        this.executor = executor;
    }

    public final Class<? extends AbstractServiceHelper> getExecutor() {
        return executor;
    }

    public final void setExecutor(Class<? extends AbstractServiceHelper> executor) {
        this.executor = executor;
    }
    
    public Set<MaintenanceTask> getPreconditions(MaintenanceTask task) {
        return preconditions.get(task);
    }
    
}
