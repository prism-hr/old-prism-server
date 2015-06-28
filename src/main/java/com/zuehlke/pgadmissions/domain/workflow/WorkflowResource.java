package com.zuehlke.pgadmissions.domain.workflow;

import org.apache.commons.lang3.ObjectUtils;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

public abstract class WorkflowResource implements UniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract System getSystem();
    
    public abstract void setSystem(System system);

    public abstract Institution getInstitution();

    public abstract void setInstitution(Institution institution);
    
    public abstract Department getDepartment();
    
    public abstract void setDepartment(Department department);
    
    public abstract Program getProgram();

    public abstract void setProgram(Program program);
    
    public abstract Project getProject();
    
    public abstract void setProject(Project project);

    public Resource getResource() {
        return ObjectUtils.firstNonNull(getSystem(), getInstitution(), getDepartment(), getProgram(), getProject());
    }

    public void setResource(Resource resource) {
        PrismReflectionUtils.invokeMethod(this, "set" + resource.getResourceScope().getUpperCamelName(), resource);
    }

    @Override
    public ResourceSignature getResourceSignature() {
        Resource resource = getResource();
        return new ResourceSignature().addProperty(resource.getResourceScope().getLowerCamelName(), resource);
    }

}
