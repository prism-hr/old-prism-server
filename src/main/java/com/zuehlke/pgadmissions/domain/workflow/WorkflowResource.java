package com.zuehlke.pgadmissions.domain.workflow;

import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;

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
        System system = getSystem();
        Institution institution = getInstitution();
        Program program = getProgram();
        if (system != null) {
            return system;
        } else if (institution != null) {
            return institution;
        }
        return program;
    }

    public void setResource(Resource resource) {
        setSystem(null);
        setInstitution(null);
        setProgram(null);

        PrismScope resourceScope = resource.getResourceScope();

        switch (resourceScope) {
        case SYSTEM:
            setSystem(resource.getSystem());
            break;
        case INSTITUTION:
            setInstitution(resource.getInstitution());
            break;
        case PROGRAM:
            setProgram(resource.getProgram());
            break;
        default:
            throw new Error();
        }
    }

    @Override
    public ResourceSignature getResourceSignature() {
        Resource resource = getResource();
        return new ResourceSignature().addProperty(resource.getResourceScope().getLowerCaseName(), resource);
    }

}
