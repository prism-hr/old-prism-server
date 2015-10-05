package com.zuehlke.pgadmissions.dto;

import static org.apache.commons.lang3.ObjectUtils.compare;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceIdentityDTO implements Comparable<ResourceIdentityDTO> {

    private PrismScope scope;

    private Integer id;

    private String name;

    private Integer logoImageId;

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

    public Integer getLogoImageId() {
        return logoImageId;
    }

    public void setLogoImageId(Integer logoImageId) {
        this.logoImageId = logoImageId;
    }

    public ResourceIdentityDTO withScope(PrismScope scope) {
        this.scope = scope;
        return this;
    }

    public ResourceIdentityDTO withId(Integer id) {
        this.id = id;
        return this;
    }

    public ResourceIdentityDTO withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public int compareTo(ResourceIdentityDTO other) {
        int compare = compare(other.getScope().ordinal(), scope.ordinal());
        return compare == 0 ? compare(name, other.getName()) : compare;
    }

}
