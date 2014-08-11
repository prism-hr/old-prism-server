package com.zuehlke.pgadmissions.rest;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.dto.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.ProgramDTO;
import com.zuehlke.pgadmissions.rest.dto.ProjectDTO;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;

public class ActionDTO {

    @NotNull
    private PrismAction actionId;
    
    @Valid
    private InstitutionDTO newInstitution;
    
    @Valid
    private ProgramDTO newProgram;
    
    @Valid
    private ProjectDTO newProject;
    
    @Valid
    private ApplicationDTO newApplication;

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public InstitutionDTO getNewInstitution() {
        return newInstitution;
    }

    public void setNewInstitution(InstitutionDTO newInstitution) {
        this.newInstitution = newInstitution;
    }

    public ProgramDTO getNewProgram() {
        return newProgram;
    }

    public void setNewProgram(ProgramDTO newProgram) {
        this.newProgram = newProgram;
    }

    public final ProjectDTO getNewProject() {
        return newProject;
    }

    public final void setNewProject(ProjectDTO newProject) {
        this.newProject = newProject;
    }

    public final ApplicationDTO getNewApplication() {
        return newApplication;
    }

    public final void setNewApplication(ApplicationDTO newApplication) {
        this.newApplication = newApplication;
    }

    public ActionDTO withAction(final PrismAction actionId) {
        this.actionId = actionId;
        return this;
    }
    
    public ActionDTO withNewInstitution(InstitutionDTO newInstitution) {
        this.newInstitution = newInstitution;
        return this;
    }
    
    public ActionDTO withNewProgram(ProgramDTO newProgram) {
        this.newProgram = newProgram;
        return this;
    }
    
    public ActionDTO withNewProject(ProjectDTO newProject) {
        this.newProject = newProject;
        return this;
    }
    
    public ActionDTO withNewApplication(ApplicationDTO newApplication) {
        this.newApplication = newApplication;
        return this;
    }


    public Object getAvailableResource() {
        List<Object> resourceDTOs = Lists.newArrayList();
        resourceDTOs.add(getNewInstitution());
        resourceDTOs.add(getNewProgram());
        resourceDTOs.add(getNewProject());
        resourceDTOs.add(getNewApplication());

        Collection<Object> notNullResourceDTOs = Collections2.filter(resourceDTOs, Predicates.notNull());

        if (notNullResourceDTOs.size() != 1) {
            throw new Error();
        }

        return notNullResourceDTOs.iterator().next();
    }
    
}
