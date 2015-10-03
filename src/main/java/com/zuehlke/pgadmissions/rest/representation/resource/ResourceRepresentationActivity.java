package com.zuehlke.pgadmissions.rest.representation.resource;

import static com.zuehlke.pgadmissions.PrismConstants.SPACE;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ObjectUtils.compare;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class ResourceRepresentationActivity extends ResourceRepresentationSimple implements Comparable<ResourceRepresentationActivity> {

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

    public void setParentResource(ResourceRepresentationSimple parentResource) {
        setProperty(this, parentResource.getScope().getLowerCamelName(), parentResource);
    }

    public ResourceRepresentationActivity withScope(PrismScope scope) {
        setScope(scope);
        return this;
    }

    public ResourceRepresentationActivity withId(Integer id) {
        setId(id);
        return this;
    }

    public ResourceRepresentationActivity withCode(String code) {
        setCode(code);
        return this;
    }

    public ResourceRepresentationActivity withProject(ResourceRepresentationSimple project) {
        setProject(project);
        return this;
    }

    public ResourceRepresentationActivity withProgram(ResourceRepresentationSimple program) {
        setProgram(program);
        return this;
    }

    public ResourceRepresentationActivity withDepartment(ResourceRepresentationSimple department) {
        setDepartment(department);
        return this;
    }

    public ResourceRepresentationActivity withInstitution(ResourceRepresentationSimple institution) {
        setInstitution(institution);
        return this;
    }

    public String getDisplayName() {
        return Joiner.on(SPACE).skipNulls().join(asList(project, program, department, institution));
    }

    @Override
    public int compareTo(ResourceRepresentationActivity other) {
        int compare = compare(institution.getName(), other.getInstitution().getName());

        ResourceRepresentationIdentity otherDepartment = other.getDepartment();
        compare = compare == 0 ? compare((department == null ? null : department.getName()), (otherDepartment == null ? null : otherDepartment.getName()), true) : compare;

        ResourceRepresentationIdentity otherProgram = other.getProgram();
        compare = compare == 0 ? compare((program == null ? null : program.getName()), (otherProgram == null ? null : otherProgram.getName()), true) : compare;

        ResourceRepresentationIdentity otherProject = other.getProject();
        return compare == 0 ? compare((project == null ? null : project.getName()), (otherProject == null ? null : otherProject.getName()), true) : compare;
    }

}
