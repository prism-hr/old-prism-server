package com.zuehlke.pgadmissions.rest.representation.resource;

import static org.apache.commons.lang3.ObjectUtils.compare;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;

public class ResourceRepresentationIdentity implements Comparable<ResourceRepresentationIdentity> {

    private PrismScope scope;

    private Integer id;

    private String name;

    private DocumentRepresentation logoImage;

    public PrismScope getScope() {
        return scope;
    }

    public void setScope(PrismScope scope) {
        this.scope = scope;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DocumentRepresentation getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(DocumentRepresentation logoImage) {
        this.logoImage = logoImage;
    }

    public ResourceRepresentationIdentity withScope(PrismScope scope) {
        this.scope = scope;
        return this;
    }

    public ResourceRepresentationIdentity withId(Integer id) {
        this.id = id;
        return this;
    }

    public ResourceRepresentationIdentity withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public int compareTo(ResourceRepresentationIdentity other) {
        int compare = compare(other.getScope().ordinal(), scope.ordinal());
        return compare == 0 ? compare(name, other.getName()) : compare;
    }

}
