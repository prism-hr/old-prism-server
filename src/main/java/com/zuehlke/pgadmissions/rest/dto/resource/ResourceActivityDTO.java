package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

public class ResourceActivityDTO {

    @Valid
    private ResourceCreationDTO institution;

    @Valid
    private ResourceCreationDTO department;

    @Valid
    private ResourceCreationDTO program;

    @Valid
    private ResourceCreationDTO project;

    public ResourceCreationDTO getInstitution() {
        return institution;
    }

    public void setInstitution(ResourceCreationDTO institution) {
        this.institution = institution;
    }

    public ResourceCreationDTO getDepartment() {
        return department;
    }

    public void setDepartment(ResourceCreationDTO department) {
        this.department = department;
    }

    public ResourceCreationDTO getProgram() {
        return program;
    }

    public void setProgram(ResourceCreationDTO program) {
        this.program = program;
    }

    public ResourceCreationDTO getProject() {
        return project;
    }

    public void setProject(ResourceCreationDTO project) {
        this.project = project;
    }

    public List<ResourceCreationDTO> getResources() {
        return Stream.of(institution, department, program, project).filter(r -> r != null).collect(Collectors.toList());
    }

}