package uk.co.alumeni.prism.rest.representation.resource;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.stream.Collectors.toList;
import static uk.co.alumeni.prism.PrismConstants.HYPHEN;
import static uk.co.alumeni.prism.PrismConstants.SPACE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.ORGANIZATION;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.setProperty;

import java.util.Set;

import jersey.repackaged.com.google.common.collect.Sets;

import org.apache.commons.lang3.ObjectUtils;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

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
        return Joiner.on(SPACE + HYPHEN + SPACE).skipNulls().join(getResourceFamily().stream() //
                .filter(resource -> resource == null ? false : resource.getScope().getScopeCategory().equals(ORGANIZATION)) //
                .map(resourceParent -> getResourceName(resourceParent)).collect(toList()));
    }

    public String getPositionDisplayName() {
        return Joiner.on(SPACE + HYPHEN + SPACE).skipNulls().join(getResourceFamily().stream() //
                .filter(resource -> resource == null ? false : resource.getScope().getScopeCategory().equals(OPPORTUNITY)) //
                .map(resourceOrganization -> getResourceName(resourceOrganization)).collect(toList()));
    }

    public String getDisplayName() {
        return Joiner.on(SPACE + HYPHEN + SPACE).skipNulls().join(emptyToNull(getOrganizationDisplayName()), emptyToNull(getPositionDisplayName()));
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
        return Objects.equal(institution, other.getInstitution()) && Objects.equal(department, other.getDepartment())
                && Objects.equal(program, other.getProgram()) && Objects.equal(project, other.getProject());
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
