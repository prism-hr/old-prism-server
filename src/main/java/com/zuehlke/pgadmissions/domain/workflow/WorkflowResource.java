package com.zuehlke.pgadmissions.domain.workflow;

import org.apache.commons.lang3.ObjectUtils;

import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

public abstract class WorkflowResource implements IUniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract System getSystem();

    public abstract Institution getInstitution();

    public abstract Program getProgram();

    public abstract void setSystem(System system);

    public abstract void setInstitution(Institution institution);

    public abstract void setProgram(Program program);

    public Resource getResource() {
        return ObjectUtils.firstNonNull(getSystem(), getInstitution(), getProgram());
    }

    public void setResource(Resource resource) {
        ReflectionUtils.invokeMethod(this, "set" + resource.getClass().getSimpleName(), resource);
    }

    @Override
    public ResourceSignature getResourceSignature() {
        Resource resource = getResource();
        return new ResourceSignature().addProperty(resource.getResourceScope().getLowerCamelName(), resource);
    }

}
