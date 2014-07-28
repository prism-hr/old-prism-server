package com.zuehlke.pgadmissions.domain;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public abstract class Resource implements IUniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);
    
    public abstract String getCode();
    
    public abstract void setCode(String code);

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

    public abstract State getPreviousState();

    public abstract void setPreviousState(State previousState);

    public abstract LocalDate getDueDate();

    public abstract void setDueDate(LocalDate dueDate);

    public abstract DateTime getCreatedTimestamp();

    public abstract void setCreatedTimestamp(DateTime createdTimestamp);

    public abstract DateTime getUpdatedTimestamp();

    public abstract void setUpdatedTimestamp(DateTime updatedTimestamp);

    public LocalDate getDueDateBaseline() {
        return new LocalDate();
    }

    public Resource getParentResource() {
        PrismScope resourceScope = PrismScope.getResourceScope(this.getClass());
        switch (resourceScope) {
        case SYSTEM:
            return this;
        case INSTITUTION:
            return getSystem();
        case PROGRAM:
            return getInstitution();
        case PROJECT:
            return getProgram();
        case APPLICATION:
            Resource project = getProject();
            return project == null ? getProgram() : project;
        }
        throw new Error();
    }

    public void setParentResource(Resource parentResource) {
        if (parentResource.getId() != null) {
            setProject(parentResource.getProject());
            setProgram(parentResource.getProgram());
            setInstitution(parentResource.getInstitution());
            setSystem(parentResource.getSystem());
        }
    }

    public PrismScope getResourceScope() {
        return PrismScope.getResourceScope(this.getClass());
    }

    public Resource getEnclosingResource(PrismScope resourceScope) {
        if (getResourceScope().equals(resourceScope)) {
            return this;
        } else {
            try {
                return (Resource) PropertyUtils.getSimpleProperty(this, resourceScope.getLowerCaseName());
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }
    
}
