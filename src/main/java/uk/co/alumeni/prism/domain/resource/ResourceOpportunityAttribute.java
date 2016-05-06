package uk.co.alumeni.prism.domain.resource;

import org.apache.commons.lang3.ObjectUtils;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.utils.PrismReflectionUtils;

public abstract class ResourceOpportunityAttribute implements UniqueEntity {

    public abstract Program getProgram();

    public abstract void setProgram(Program program);

    public abstract Project getProject();

    public abstract void setProject(Project project);

    public ResourceParent getResource() {
        return ObjectUtils.firstNonNull(getProject(), getProgram());
    }

    public void setResource(ResourceParent resource) {
        PrismReflectionUtils.invokeMethod(this, "set" + resource.getResourceScope().getUpperCamelName(), resource);
    }

    public EntitySignature getEntitySignature() {
        ResourceParent resource = getResource();
        return new EntitySignature().addProperty(resource.getResourceScope().getLowerCamelName(), resource);
    }

}
