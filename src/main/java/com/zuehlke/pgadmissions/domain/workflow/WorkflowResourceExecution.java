package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import org.apache.commons.lang3.ObjectUtils;

public abstract class WorkflowResourceExecution extends WorkflowResource {

    public abstract Project getProject();

    public abstract void setProject(Project project);

    public abstract Application getApplication();

    public abstract void setApplication(Application application);

    @Override
    public Resource getResource() {
        return ObjectUtils.firstNonNull(super.getResource(), getProject(), getApplication());
    }

}
