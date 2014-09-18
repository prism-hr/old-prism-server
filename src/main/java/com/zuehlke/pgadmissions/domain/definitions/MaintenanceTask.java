package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.services.lifecycle.helpers.AbstractServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.AdvertServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.DocumentServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperInstitution;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperSystem;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.NotificationServiceHelperRecommendation;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.NotificationServiceHelperWorkflow;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ProgramServiceHelper;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.StateServiceHelperEscalation;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.StateServiceHelperPropagation;

public enum MaintenanceTask {

    SYSTEM_EXECUTE_ESCALATED_STATE_TRANSITION(StateServiceHelperEscalation.class, false),
    SYSTEM_EXECUTE_PROPAGATED_STATE_TRANSITION(StateServiceHelperPropagation.class, false),
    SYSTEM_IMPORT_SYSTEM_REFERENCE_DATA(ImportedEntityServiceHelperSystem.class, true),
    SYSTEM_IMPORT_INSTITUTION_REFERENCE_DATA(ImportedEntityServiceHelperInstitution.class, true),
    SYSTEM_UPDATE_PROGRAM_STUDY_OPTION(ProgramServiceHelper.class, true),
    SYSTEM_UPDATE_ADVERT_CLOSING_DATE(AdvertServiceHelper.class, true),
    SYSTEM_SEND_DEFERRED_WORKFLOW_NOTIFICATION(NotificationServiceHelperWorkflow.class, false),
    SYSTEM_SEND_RECOMMENDATION_NOTIFICATION(NotificationServiceHelperRecommendation.class, true),
    SYSTEM_DELETE_UNUSED_DOCUMENT(DocumentServiceHelper.class, true);
    
    private Class<? extends AbstractServiceHelper> executor;
    
    boolean parallelize;

    private MaintenanceTask(Class<? extends AbstractServiceHelper> executor, boolean parallelize) {
        this.executor = executor;
        this.parallelize = parallelize;
    }

    public final Class<? extends AbstractServiceHelper> getExecutor() {
        return executor;
    }

    public final boolean isParallelize() {
        return parallelize;
    }
    
}
