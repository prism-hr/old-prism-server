package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;

public abstract class WorkflowResourceExecution extends WorkflowResource {

    public abstract Project getProject();
    
    public abstract void setProject(Project project);
    
    public abstract Application getApplication();
    
    public abstract void setApplication(Application application);
    
    @Override
    public Resource getResource() {
        Resource resource = super.getResource();
        if (resource == null) {
            Project project = getProject();
            Application application = getApplication();
            if (project != null) {
                return project;
            }
            return application;
        }
        return resource;
    }
    
    @Override
    public void setResource(Resource resource) {
        setProject(null);
        setApplication(null);
        
        switch(resource.getResourceScope()) {
        case PROJECT:
            setProject(resource.getProject());
            break;
        case APPLICATION:
            setApplication(resource.getApplication());
            break;
        default:
            super.setResource(resource);
            break;
        }
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        Resource resource = getResource();
        return new ResourceSignature().addProperty(resource.getResourceScope().getLowerCaseName(), resource);
    }
    
}
