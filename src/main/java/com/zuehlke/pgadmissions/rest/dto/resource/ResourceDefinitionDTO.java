package com.zuehlke.pgadmissions.rest.dto.resource;

import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;

import com.zuehlke.pgadmissions.rest.dto.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;

public class ResourceDefinitionDTO {

    private Integer workflowPropertyConfigurationVersion;

    @Valid
    private InstitutionDTO institution;

    @Valid
    private ResourceParentDivisionDTO department;

    @Valid
    private ResourceOpportunityDTO program;

    @Valid
    private ResourceOpportunityDTO project;

    @Valid
    private ApplicationDTO application;

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

    public ResourceParentDivisionDTO getDepartment() {
        return department;
    }

    public void setDepartment(ResourceParentDivisionDTO department) {
        this.department = department;
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

    public ApplicationDTO getApplication() {
        return application;
    }

    public void setApplication(ApplicationDTO application) {
        this.application = application;
    }

    public ResourceDTO getResource() {
        return ObjectUtils.firstNonNull(application, project, program, department, institution).getParentResource();
    }

}
