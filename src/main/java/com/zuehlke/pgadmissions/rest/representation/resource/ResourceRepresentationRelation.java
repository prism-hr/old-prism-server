package com.zuehlke.pgadmissions.rest.representation.resource;

import static com.zuehlke.pgadmissions.PrismConstants.HYPHEN;
import static com.zuehlke.pgadmissions.PrismConstants.SPACE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScopeCategory.ORGANIZATION;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

import jersey.repackaged.com.google.common.collect.Sets;

public class ResourceRepresentationRelation extends ResourceRepresentationSimple {

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

    public ResourceRepresentationRelation withScope(PrismScope scope) {
        setScope(scope);
        return this;
    }

    public ResourceRepresentationRelation withId(Integer id) {
        setId(id);
        return this;
    }

    public ResourceRepresentationRelation withCode(String code) {
        setCode(code);
        return this;
    }

    public ResourceRepresentationRelation withProject(ResourceRepresentationSimple project) {
        setProject(project);
        return this;
    }

    public ResourceRepresentationRelation withProgram(ResourceRepresentationSimple program) {
        setProgram(program);
        return this;
    }

    public ResourceRepresentationRelation withDepartment(ResourceRepresentationSimple department) {
        setDepartment(department);
        return this;
    }

    public ResourceRepresentationRelation withInstitution(ResourceRepresentationSimple institution) {
        setInstitution(institution);
        return this;
    }

    public String getOrganizationDisplayName() {
        return Joiner.on(SPACE + HYPHEN + SPACE).skipNulls().join(getResourceFamily().stream().filter(r -> r == null ? false : r.getScope().getScopeCategory().equals(ORGANIZATION))
                .map(rp -> getResourceName(rp)).collect(Collectors.toList()));
    }

    public String getPositionDisplayName() {
        return Joiner.on(SPACE + HYPHEN + SPACE).skipNulls().join(getResourceFamily().stream().filter(r -> r == null ? false : r.getScope().getScopeCategory().equals(OPPORTUNITY))
                .map(ro -> getResourceName(ro)).collect(Collectors.toList()));
    }

    public String getDisplayName() {
        return Joiner.on(SPACE + HYPHEN + SPACE).skipNulls().join(getOrganizationDisplayName(), getPositionDisplayName());
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
        ResourceRepresentationRelation other = (ResourceRepresentationRelation) object;
        return Objects.equal(institution, other.getInstitution()) && Objects.equal(department, other.getDepartment()) && Objects.equal(program, other.getProgram())
                && Objects.equal(project, other.getProject());
    }

    @Override
    public int compareTo(ResourceRepresentationIdentity other) {
        if (ResourceRepresentationRelation.class.isAssignableFrom(other.getClass())) {
            ResourceRepresentationRelation otherActivity = (ResourceRepresentationRelation) other;
            int compare = institution.compareTo(otherActivity.getInstitution());
            compare = compare == 0 ? ObjectUtils.compare(department, otherActivity.getDepartment(), true) : compare;
            compare = compare == 0 ? ObjectUtils.compare(program, otherActivity.getProgram(), true) : compare;
            return compare == 0 ? ObjectUtils.compare(project, otherActivity.getProject(), true) : compare;
        }
        return super.compareTo(other);
    }

    private String getResourceName(ResourceRepresentationSimple resource) {
        return resource == null ? null : resource.getName();
    }

    private Set<ResourceRepresentationSimple> getResourceFamily() {
        return Sets.newLinkedHashSet(Lists.newArrayList(institution, department, program, project, this));
    }

}