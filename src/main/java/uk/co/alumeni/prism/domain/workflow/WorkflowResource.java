package uk.co.alumeni.prism.domain.workflow;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.invokeMethod;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.System;

public abstract class WorkflowResource implements UniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract uk.co.alumeni.prism.domain.resource.System getSystem();

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
        return firstNonNull(getSystem(), getInstitution(), getDepartment(), getProgram(), getProject());
    }

    public void setResource(Resource resource) {
        invokeMethod(this, "set" + resource.getResourceScope().getUpperCamelName(), resource);
    }

    @Override
    public EntitySignature getEntitySignature() {
        Resource resource = getResource();
        return new EntitySignature().addProperty(resource.getResourceScope().getLowerCamelName(), resource);
    }

}
