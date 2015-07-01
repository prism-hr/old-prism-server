package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;

public class ResourceDefinitionDTO {

    private Integer workflowPropertyConfigurationVersion;

    @Valid
    private InstitutionDTO institution;

    @Valid
    private ResourceOpportunityDTO program;

    @Valid
    private ResourceOpportunityDTO project;

    @Valid
    private ResourceDTO application;

    public Integer getWorkflowPropertyConfigurationVersion() {
        return workflowPropertyConfigurationVersion;
    }

    public void setWorkflowPropertyConfigurationVersion(Integer workflowPropertyConfigurationVersion) {
        this.workflowPropertyConfigurationVersion = workflowPropertyConfigurationVersion;
    }

    public InstitutionDTO getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDTO institution) {
        this.institution = institution;
    }

    public ResourceOpportunityDTO getProgram() {
        return program;
    }

    public void setProgram(ResourceOpportunityDTO program) {
        this.program = program;
    }

    public ResourceOpportunityDTO getProject() {
        return project;
    }

    public void setProject(ResourceOpportunityDTO project) {
        this.project = project;
    }

    public ResourceDTO getApplication() {
        return application;
    }

    public void setApplication(ResourceDTO application) {
        this.application = application;
    }

    public ResourceDTO getResource() {
        return ObjectUtils.firstNonNull(application, project, program, institution);
    }

}
