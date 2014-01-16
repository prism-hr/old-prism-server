package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.dto.ProjectDTO;

public class ProjectDTOBuilder {

    private Integer id;
    private Program program;
    private String title;
    private String description;
    private String funding;
    private Boolean closingDateSpecified;
    private Date closingDate;
    private Boolean active;
    private Person administrator;
    private Boolean administratorSpecified;
    private Person primarySupervisor;
    private Boolean secondarySupervisorSpecified;
    private Person secondarySupervisor;

    public ProjectDTO build() {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(id);
        projectDTO.setProgram(program);
        projectDTO.setTitle(title);
        projectDTO.setDescription(description);
        projectDTO.setFunding(funding);
        projectDTO.setClosingDateSpecified(closingDateSpecified);
        projectDTO.setClosingDate(closingDate);
        projectDTO.setActive(active);
        projectDTO.setAdministratorSpecified(administratorSpecified);
        projectDTO.setAdministrator(administrator);
        projectDTO.setPrimarySupervisor(primarySupervisor);
        projectDTO.setSecondarySupervisorSpecified(secondarySupervisorSpecified);
        projectDTO.setSecondarySupervisor(secondarySupervisor);
        return projectDTO;
    }

    public ProjectDTOBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ProjectDTOBuilder program(Program program) {
        this.program = program;
        return this;
    }

    public ProjectDTOBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ProjectDTOBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProjectDTOBuilder funding(String funding) {
        this.funding = funding;
        return this;
    }

    public ProjectDTOBuilder closingDateSpecified(Boolean closingDateSpecified) {
        this.closingDateSpecified = closingDateSpecified;
        return this;
    }

    public ProjectDTOBuilder closingDate(Date closingDate) {
        this.closingDate = closingDate;
        return this;
    }

    public ProjectDTOBuilder active(Boolean active) {
        this.active = active;
        return this;
    }

    public ProjectDTOBuilder administrator(Person administrator) {
        this.administrator = administrator;
        return this;
    }

    public ProjectDTOBuilder administratorSpecified(Boolean administratorSpecified) {
        this.administratorSpecified = administratorSpecified;
        return this;
    }

    public ProjectDTOBuilder primarySupervisor(Person primarySupervisor) {
        this.primarySupervisor = primarySupervisor;
        return this;
    }

    public ProjectDTOBuilder secondarySupervisorSpecified(Boolean secondarySupervisorSpecified) {
        this.secondarySupervisorSpecified = secondarySupervisorSpecified;
        return this;
    }

    public ProjectDTOBuilder secondarySupervisor(Person secondarySupervisor) {
        this.secondarySupervisor = secondarySupervisor;
        return this;
    }

}
