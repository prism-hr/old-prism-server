package com.zuehlke.pgadmissions.domain.resource;

import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public abstract class Resource implements IUniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract User getUser();

    public abstract void setUser(User user);

    public abstract String getCode();

    public abstract void setCode(String code);

    public abstract PrismLocale getLocale();

    public abstract System getSystem();

    public abstract void setSystem(System system);

    public abstract Institution getInstitution();

    public abstract void setInstitution(Institution institution);

    public abstract Program getProgram();

    public abstract void setProgram(Program program);

    public abstract Project getProject();

    public abstract void setProject(Project project);

    public abstract Application getApplication();

    public abstract String getReferrer();

    public abstract void setReferrer(String referrer);

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

    public abstract String getSequenceIdentifier();

    public abstract void setSequenceIdentifier(String sequenceIdentifier);

    public abstract void addComment(Comment comment);

    public String getHelpdeskDisplay() {
        if (getResourceScope() == PrismScope.SYSTEM) {
            return getSystem().getHelpdesk();
        }
        String helpdesk = getInstitution().getHelpdesk();
        return helpdesk == null ? getSystem().getHelpdesk() : helpdesk;
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
            return (Resource) ReflectionUtils.getProperty(this, resourceScope.getLowerCaseName());
        }
    }

    @Override
    public String toString() {
        return getResourceScope().name() + "#" + getId();
    }
}
