package com.zuehlke.pgadmissions.rest.representation.resource;

import static com.zuehlke.pgadmissions.PrismConstants.SPACE;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;
import static java.util.Arrays.asList;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
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
    public int hashCode() {
        return Objects.hashCode(institution, department, project, program);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ResourceRepresentationActivity other = (ResourceRepresentationActivity) object;
        return Objects.equal(institution, other.getInstitution()) && Objects.equal(department, other.getDepartment()) && Objects.equal(program, other.getProgram())
                && Objects.equal(project, other.getProject());
    }

    @Override
    public int compareTo(ResourceRepresentationIdentity other) {
        if (ResourceRepresentationActivity.class.isAssignableFrom(other.getClass())) {
            ResourceRepresentationActivity otherActivity = (ResourceRepresentationActivity) other;
            int compare = institution.compareTo(otherActivity.getInstitution());
            compare = compare == 0 ? ObjectUtils.compare(department, otherActivity.getDepartment(), true) : compare;
            compare = compare == 0 ? ObjectUtils.compare(program, otherActivity.getProgram(), true) : compare;
            return compare == 0 ? ObjectUtils.compare(project, otherActivity.getProject(), true) : compare;
        }
        return super.compareTo(other);
    }

}
