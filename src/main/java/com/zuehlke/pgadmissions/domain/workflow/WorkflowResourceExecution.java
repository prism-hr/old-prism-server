package com.zuehlke.pgadmissions.domain.workflow;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Resource;

public abstract class WorkflowResourceExecution extends WorkflowResource {

    public abstract Application getApplication();

    public abstract void setApplication(Application application);

    @Override
    public Resource getResource() {
        return firstNonNull(super.getResource(), getApplication());
    }

}
