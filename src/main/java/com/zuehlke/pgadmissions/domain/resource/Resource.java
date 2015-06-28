package com.zuehlke.pgadmissions.domain.resource;

import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

public abstract class Resource implements UniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract User getUser();

    public abstract void setUser(User user);

    public abstract String getCode();

    public abstract void setCode(String code);

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

    public abstract Advert getAdvert();

    public abstract void setAdvert(Advert advert);

    public abstract Application getApplication();
    
    public abstract String getTitle();

    public abstract State getState();

    public abstract void setState(State state);

    public abstract State getPreviousState();

    public abstract void setPreviousState(State previousState);

    public abstract LocalDate getDueDate();

    public abstract void setDueDate(LocalDate dueDate);

    public abstract DateTime getCreatedTimestamp();

    public abstract void setCreatedTimestamp(DateTime createdTimestamp);

    public abstract DateTime getUpdatedTimestamp();

    public abstract void setUpdatedTimestamp(DateTime updatedTimestamp);

    public abstract LocalDate getLastRemindedRequestIndividual();

    public abstract void setLastRemindedRequestIndividual(LocalDate lastRemindedRequestIndividual);

    public abstract LocalDate getLastRemindedRequestSyndicated();

    public abstract void setLastRemindedRequestSyndicated(LocalDate lastRemindedRequestSyndicated);

    public abstract LocalDate getLastNotifiedUpdateSyndicated();

    public abstract void setLastNotifiedUpdateSyndicated(LocalDate lastNotifiedUpdateSyndicated);

    public abstract Integer getWorkflowPropertyConfigurationVersion();

    public abstract void setWorkflowPropertyConfigurationVersion(Integer workflowResourceConfigurationVersion);

    public abstract String getSequenceIdentifier();

    public abstract void setSequenceIdentifier(String sequenceIdentifier);

    public abstract Set<ResourceState> getResourceStates();

    public abstract Set<ResourcePreviousState> getResourcePreviousStates();

    public abstract Set<ResourceCondition> getResourceConditions();

    public abstract Set<Comment> getComments();

    public abstract Set<UserRole> getUserRoles();

    public void addComment(Comment comment) {
        getComments().add(comment);
    }

    public void addResourceState(ResourceState resourceState) {
        getResourceStates().add(resourceState);
    }

    public void addResourcePreviousState(ResourcePreviousState resourcePreviousState) {
        getResourcePreviousStates().add(resourcePreviousState);
    }

    public Resource getParentResource() {
        switch (PrismScope.getByResourceClass(this.getClass())) {
        case SYSTEM:
            return this;
        case INSTITUTION:
            return getSystem();
        case PROGRAM:
            return getInstitution();
        case PROJECT:
            return ObjectUtils.firstNonNull(getProgram(), getInstitution());
        case APPLICATION:
            return ObjectUtils.firstNonNull(getProject(), getProgram(), getInstitution());
        default:
            throw new UnsupportedOperationException();
        }
    }

    public void setParentResource(Resource parentResource) {
        if (parentResource.getId() != null) {
            setProject(parentResource.getProject());
            setProgram(parentResource.getProgram());
            setDepartment(parentResource.getDepartment());
            setInstitution(parentResource.getInstitution());
            setSystem(parentResource.getSystem());
        }
    }

    public PrismScope getResourceScope() {
        return PrismScope.getByResourceClass(this.getClass());
    }

    public Resource getEnclosingResource(PrismScope resourceScope) {
        return (Resource) PrismReflectionUtils.getProperty(this, resourceScope.getLowerCamelName());
    }

    @Override
    public String toString() {
        return getResourceScope().name() + "#" + getId();
    }

    public boolean sameAs(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final Resource other = (Resource) object;
        Integer id = getId();
        Integer otherId = other.getId();
        return id != null && otherId != null && id.equals(otherId);
    }

}
