package com.zuehlke.pgadmissions.domain;

import org.apache.commons.beanutils.PropertyUtils;

import com.zuehlke.pgadmissions.domain.enums.PrismScope;

public abstract class PrismResource implements IUniqueResource {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract System getSystem();

    public abstract void setSystem(System system);

    public abstract Institution getInstitution();

    public abstract void setInstitution(Institution institution);

    public abstract Program getProgram();

    public abstract void setProgram(Program program);

    public abstract Project getProject();

    public abstract void setProject(Project project);

    public abstract Application getApplication();

    public abstract State getState();

    public abstract void setState(State state);

    public abstract User getUser();

    public abstract void setUser(User user);

    public PrismScope getResourceType() {
        return PrismScope.valueOf(this.getClass().getSimpleName().toUpperCase());
    }

    public PrismResource getEnclosingResource(PrismScope resourceType) {
        return getResourceType().equals(resourceType) ? this : getParentResource(resourceType);
    }

    public PrismResource getParentResource(PrismScope resourceType) {
        try {
            return (PrismResource) PropertyUtils.getSimpleProperty(this, resourceType.getLowerCaseName());
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void setParentResource(PrismResource parentResource) {
        if (parentResource.getId() != null) {
            setProject(parentResource.getProject());
            setProgram(parentResource.getProgram());
            setInstitution(parentResource.getInstitution());
            setSystem(parentResource.getSystem());
        }
    }

}
