package com.zuehlke.pgadmissions.domain;

import org.apache.commons.beanutils.PropertyUtils;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public abstract class Resource implements IUniqueResource {

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

    public PrismScope getResourceScope() {
        return PrismScope.getResourceScope(this.getClass());
    }

    public Resource getEnclosingResource(PrismScope resourceScope) {
        return getResourceScope().equals(resourceScope) ? this : getParentResource(resourceScope);
    }

    public Resource getParentResource(PrismScope resourceScope) {
        try {
            return (Resource) PropertyUtils.getSimpleProperty(this, resourceScope.getLowerCaseName());
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void setParentResource(Resource parentResource) {
        if (parentResource.getId() != null) {
            setProject(parentResource.getProject());
            setProgram(parentResource.getProgram());
            setInstitution(parentResource.getInstitution());
            setSystem(parentResource.getSystem());
        }
    }

}
