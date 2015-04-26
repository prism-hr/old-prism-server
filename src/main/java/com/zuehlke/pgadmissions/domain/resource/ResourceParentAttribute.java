package com.zuehlke.pgadmissions.domain.resource;

import org.apache.commons.lang3.ObjectUtils;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

public abstract class ResourceParentAttribute implements UniqueEntity {

    public abstract Institution getInstitution();
    
    public abstract void setInstitution(Institution institution);
    
    public abstract Program getProgram();
    
    public abstract void setProgram(Program program);
    
    public abstract Project getProject();
    
    public abstract void setProject(Project project);
    
    public ResourceParent getResource() {
        return ObjectUtils.firstNonNull(getProject(), getProgram(), getInstitution());
    }
    
    public void setResource(ResourceParent resource) {
        PrismReflectionUtils.invokeMethod(this, "set" + resource.getResourceScope().getUpperCamelName(), resource);
    }
    
    public ResourceSignature getResourceSignature() {
        ResourceParent resource = getResource();
        return new ResourceSignature().addProperty(resource.getResourceScope().getLowerCamelName(), resource);
    }
    
}
