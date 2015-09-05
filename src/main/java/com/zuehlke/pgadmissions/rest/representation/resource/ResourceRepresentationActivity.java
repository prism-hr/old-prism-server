package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class ResourceRepresentationActivity extends ResourceRepresentationSimple {

    private UserRepresentationSimple user;

    private ResourceRepresentationSimple institution;

    private ResourceRepresentationSimple department;

    private ResourceRepresentationSimple program;

    private ResourceRepresentationSimple project;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public ResourceRepresentationSimple getInstitution() {
        return institution;
    }

    public void setInstitution(ResourceRepresentationSimple institution) {
        this.institution = institution;
    }

    public ResourceRepresentationSimple getDepartment() {
        return department;
    }

    public void setDepartment(ResourceRepresentationSimple department) {
        this.department = department;
    }

    public ResourceRepresentationSimple getProgram() {
        return program;
    }

    public void setProgram(ResourceRepresentationSimple program) {
        this.program = program;
    }

    public ResourceRepresentationSimple getProject() {
        return project;
    }

    public void setProject(ResourceRepresentationSimple project) {
        this.project = project;
    }

}
