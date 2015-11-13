package com.zuehlke.pgadmissions.rest.representation.resource;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceContext;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Set;

import static com.zuehlke.pgadmissions.PrismConstants.HYPHEN;
import static com.zuehlke.pgadmissions.PrismConstants.SPACE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static java.util.Arrays.asList;

public class ResourceRepresentationConnection implements Comparable<ResourceRepresentationConnection> {

    private ResourceRepresentationIdentity institution;

    private ResourceRepresentationIdentity department;

    private DocumentRepresentation backgroundImage;

    private Set<PrismResourceContext> contexts;

    public ResourceRepresentationIdentity getInstitution() {
        return institution;
    }

    public void setInstitution(ResourceRepresentationIdentity institution) {
        this.institution = institution;
    }

    public ResourceRepresentationIdentity getDepartment() {
        return department;
    }

    public void setDepartment(ResourceRepresentationIdentity department) {
        this.department = department;
    }

    public DocumentRepresentation getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(DocumentRepresentation backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Set<PrismResourceContext> getContexts() {
        return contexts;
    }

    public void setContexts(Set<PrismResourceContext> contexts) {
        this.contexts = contexts;
    }

    public ResourceRepresentationConnection withInstitution(ResourceRepresentationSimple institution) {
        setInstitution(institution);
        return this;
    }

    public ResourceRepresentationConnection withDepartment(ResourceRepresentationSimple department) {
        setDepartment(department);
        return this;
    }

    public ResourceRepresentationConnection withBackgroundImage(DocumentRepresentation backgroundImage) {
        this.backgroundImage = backgroundImage;
        return this;
    }

    public Integer getId() {
        return department == null ? institution.getId() : department.getId();
    }

    public PrismScope getScope() {
        return department == null ? INSTITUTION : DEPARTMENT;
    }

    public String getDisplayName() {
        String institutionName = institution == null ? null : institution.getName();
        String departmentName = department == null ? null : department.getName();
        return Joiner.on(SPACE + HYPHEN + SPACE).skipNulls().join(asList(institutionName, departmentName));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(institution, department);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ResourceRepresentationConnection other = (ResourceRepresentationConnection) object;
        return Objects.equal(institution, other.getInstitution()) && Objects.equal(department, other.getDepartment());
    }

    @Override
    public int compareTo(ResourceRepresentationConnection other) {
        int compare = ObjectUtils.compare(institution, other.getInstitution());
        return compare == 0 ? ObjectUtils.compare(department, other.getDepartment(), true) : compare;
    }

}
