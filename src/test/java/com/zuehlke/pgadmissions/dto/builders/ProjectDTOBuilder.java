package com.zuehlke.pgadmissions.dto.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.utils.DateUtils;

public class ProjectDTOBuilder {

	private Integer id;
    private Program program;
    private String title;
    private String description;
    private String funding;
    private Boolean closingDateSpecified=false;
    private Date closingDate;
    private Boolean active=true;
    private Person primarySupervisor;
    private Boolean secondarySupervisorSpecified=false;
    private Person secondarySupervisor;


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


    /**
     * Sets the date with day as most significant value.
     * @param closingDate
     */
    public ProjectDTOBuilder closingDate(Date closingDate) {
        this.closingDate = closingDate == null? closingDate : DateUtils.truncateToDay(closingDate);
		return this;
    }


    public ProjectDTOBuilder active(Boolean active) {
        this.active = active;
		return this;
    }


	public ProjectDTOBuilder id(Integer id) {
		this.id = id;
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

	public ProjectDTO build(){
		ProjectDTO projectDTO = new ProjectDTO();
		projectDTO.setId(id);
	    projectDTO.setProgram(program);
	    projectDTO.setTitle( title);
	    projectDTO.setDescription(description);
	    projectDTO.setFunding(funding);
	    projectDTO.setClosingDateSpecified(closingDateSpecified);
	    projectDTO.setClosingDate(closingDate);
	    projectDTO.setActive(active);
	    projectDTO.setPrimarySupervisor(primarySupervisor);
	    projectDTO.setSecondarySupervisorSpecified(secondarySupervisorSpecified);
	    projectDTO.setSecondarySupervisor(secondarySupervisor);
	    return projectDTO;
	}
	
}