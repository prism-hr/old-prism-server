package com.zuehlke.pgadmissions.domain.workflow;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.Resume;

public abstract class WorkflowResourceExecution extends WorkflowResource {

    public abstract Application getApplication();

    public abstract void setApplication(Application application);

    public abstract Resume getResume();

    public abstract void setResume(Resume resume);

    @Override
    @SuppressWarnings("unchecked")
    public Resource<?> getResource() {
        return firstNonNull(super.getResource(), getApplication(), getResume());
    }

}
