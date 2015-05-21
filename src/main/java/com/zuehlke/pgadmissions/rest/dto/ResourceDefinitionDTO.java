package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;

public class ResourceDefinitionDTO {

    private Integer workflowPropertyConfigurationVersion;

    @Valid
    private InstitutionDTO institution;

    @Valid
    private OpportunityDTO program;

    @Valid
    private OpportunityDTO project;

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

    public OpportunityDTO getProgram() {
        return program;
    }

    public void setProgram(OpportunityDTO program) {
        this.program = program;
    }

    public OpportunityDTO getProject() {
        return project;
    }

    public void setProject(OpportunityDTO project) {
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
