package com.zuehlke.pgadmissions.domain;

import org.apache.commons.beanutils.PropertyUtils;

import com.zuehlke.pgadmissions.domain.enums.PrismResourceType;

public abstract class PrismResource {

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
    
    public PrismResourceType getResourceType() {
        return PrismResourceType.valueOf(this.getClass().getSimpleName().toUpperCase());
    }
    
    public PrismResource getEnclosingResource(PrismResourceType resourceType) {
        return getResourceType().equals(resourceType) ? this : getParentResource(resourceType);
    }

    public PrismResource getParentResource(PrismResourceType resourceType) {
        try {
            return (PrismResource) PropertyUtils.getSimpleProperty(this, resourceType.getLowerCaseName());
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void setParentResource(PrismResource enclosingResource) {
        Program enclosingProgram = enclosingResource.getProgram();
        Institution enclosingInstitution = enclosingResource.getInstitution();
        System enclosingSystem = enclosingResource.getSystem();

        if (enclosingSystem == null || (!(enclosingResource instanceof System) && enclosingInstitution == null)
                || (!(enclosingResource instanceof System || enclosingResource instanceof Institution) && enclosingProgram == null)) {
            throw new Error("Attempted to create new " + enclosingResource.getResourceType().toString() + " with invalid parent scope");
        }

        setProject(enclosingResource.getProject());
        setProgram(enclosingResource.getProgram());
        setInstitution(enclosingResource.getInstitution());
        setSystem(enclosingResource.getSystem());
    }
    
    public boolean isFertile() {
        return this.getState().isFertileState();
    }

}
