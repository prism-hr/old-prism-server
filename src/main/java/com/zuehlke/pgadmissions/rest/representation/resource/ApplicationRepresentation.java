package com.zuehlke.pgadmissions.rest.representation.resource;


import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

public class ApplicationRepresentation {

    private Integer id;

    private UserRepresentation user;

    private ProgramRepresentation program;

    private ProjectRepresentation project;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public ProgramRepresentation getProgram() {
        return program;
    }

    public void setProgram(ProgramRepresentation program) {
        this.program = program;
    }

    public ProjectRepresentation getProject() {
        return project;
    }

    public void setProject(ProjectRepresentation project) {
        this.project = project;
    }
}
