package com.zuehlke.pgadmissions.rest.dto.resource;

import com.zuehlke.pgadmissions.domain.definitions.PrismScopeCreation;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceFamilyCreationDTO {

    @NotNull
    private PrismScopeCreation creation;

    @Valid
    @NotNull
    private ResourceCreationDTO institution;

    @Valid
    private ResourceCreationDTO department;

    @Valid
    private ResourceCreationDTO program;

    @Valid
    private ResourceCreationDTO project;

    @Valid
    @NotNull
    private UserDTO user;

    public PrismScopeCreation getCreation() {
        return creation;
    }

    public void setCreation(PrismScopeCreation creation) {
        this.creation = creation;
    }

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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public List<ResourceCreationDTO> getResources() {
        return Stream.of(institution, department, program, project).filter(r -> r != null).collect(Collectors.toList());
    }
}
