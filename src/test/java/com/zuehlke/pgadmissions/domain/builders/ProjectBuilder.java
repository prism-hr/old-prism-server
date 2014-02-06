package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ProjectBuilder {
    private Integer id;
    private RegisteredUser author;
    private Program program;
    private Date closingDate;
    private RegisteredUser administrator;
    private RegisteredUser primarySupervisor;
    private RegisteredUser secondarySupervisor;
    private boolean enabled = true;

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

    public ProjectBuilder closingDate(Date closingDate) {
        this.closingDate = closingDate;
        return this;
    }

    public ProjectBuilder contactUser(RegisteredUser administrator) {
        this.administrator = administrator;
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

    public ProjectBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Project build() {
        Project project = new Project();
        project.setId(id);
        project.setAuthor(author);
        project.setProgram(program);
        project.setClosingDate(closingDate);
        project.setContactUser(administrator);
        project.setPrimarySupervisor(primarySupervisor);
        project.setSecondarySupervisor(secondarySupervisor);
        project.setEnabled(enabled);
        return project;
    }

}
