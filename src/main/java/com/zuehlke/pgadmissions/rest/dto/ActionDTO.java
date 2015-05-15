package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ObjectUtils;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class ActionDTO {

    @NotNull
    private PrismAction actionId;

    private Integer workflowPropertyConfigurationVersion;

    @Valid
    private InstitutionDTO newInstitution;

    @Valid
    private OpportunityDTO newProgram;

    @Valid
    private OpportunityDTO newProject;

    @Valid
    private ResourceDTO newApplication;

    private String referer;

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public Integer getWorkflowPropertyConfigurationVersion() {
        return workflowPropertyConfigurationVersion;
    }

    public void setWorkflowPropertyConfigurationVersion(Integer workflowPropertyConfigurationVersion) {
        this.workflowPropertyConfigurationVersion = workflowPropertyConfigurationVersion;
    }

    public InstitutionDTO getNewInstitution() {
        return newInstitution;
    }

    public void setNewInstitution(InstitutionDTO newInstitution) {
        this.newInstitution = newInstitution;
    }

    public OpportunityDTO getNewProgram() {
        return newProgram;
    }

    public void setNewProgram(OpportunityDTO newProgram) {
        this.newProgram = newProgram;
    }

    public OpportunityDTO getNewProject() {
        return newProject;
    }

    public void setNewProject(OpportunityDTO newProject) {
        this.newProject = newProject;
    }

    public ResourceDTO getNewApplication() {
        return newApplication;
    }

    public void setNewApplication(ResourceDTO newApplication) {
        this.newApplication = newApplication;
    }

    public ResourceDTO getNewResource() {
        return ObjectUtils.firstNonNull(newApplication, newProject, newProgram, newInstitution);
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public ActionDTO withActionId(PrismAction actionId) {
        this.actionId = actionId;
        return this;
    }

}
