package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ProjectBuilder{
    private Integer id;
    private RegisteredUser author;
    private Program program;
    private Advert advert;
    private Date closingDate;
    private RegisteredUser primarySupervisor;
    private RegisteredUser secondarySupervisor;

    public ProjectBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ProjectBuilder author(RegisteredUser author) {
        this.author = author;
        return this;
    }

    public ProjectBuilder program(Program program) {
        this.program = program;
        return this;
    }

    public ProjectBuilder advert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public ProjectBuilder closingDate(Date closingDate) {
        this.closingDate = closingDate;
        return this;
    }

    public ProjectBuilder primarySupervisor(RegisteredUser primarySupervisor) {
        this.primarySupervisor = primarySupervisor;
        return this;
    }

    public ProjectBuilder secondarySupervisor(RegisteredUser secondarySupervisor) {
        this.secondarySupervisor = secondarySupervisor;
        return this;
    }
    
    public Project build(){
    	Project project = new Project();
	 	project.setId( id) ;
	 	project.setAdvert( advert) ;
	    project.setAuthor( author) ;
	    project.setProgram( program) ;
	    project.setClosingDate( closingDate) ;
	    project.setPrimarySupervisor( primarySupervisor) ;
	    project.setSecondarySupervisor( secondarySupervisor) ;
	    return project;
    }

}
