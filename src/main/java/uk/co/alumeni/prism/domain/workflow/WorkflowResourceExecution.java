package uk.co.alumeni.prism.domain.workflow;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.resource.Resource;

public abstract class WorkflowResourceExecution extends WorkflowResource {

    public abstract Application getApplication();

    public abstract void setApplication(Application application);

    @Override
    public Resource getResource() {
        return firstNonNull(super.getResource(), getApplication());
    }

}
