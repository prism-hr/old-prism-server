package com.zuehlke.pgadmissions.rest.dto.resource;

import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;

import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDTO;

public class ResourceDefinitionDTO {

    @Valid
    private InstitutionDTO institution;

    @Valid
    private ResourceParentDivisionDTO department;

    @Valid
    private ResourceOpportunityDTO program;

    @Valid
    private ProjectDTO project;

    @Valid
    private ApplicationDTO application;

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

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public ApplicationDTO getApplication() {
        return application;
    }

    public void setApplication(ApplicationDTO application) {
        this.application = application;
    }
    
    public ResourceCreationDTO getResource() {
        return ObjectUtils.firstNonNull(application, project, program, department, institution);
    }

    public ResourceDTO getParentResource() {
        return ObjectUtils.firstNonNull(application, project, program, department, institution).getParentResource();
    }

}
