package com.zuehlke.pgadmissions.dto.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceIdentityDTO {

    private PrismScope scope;

    private Integer id;

    private String name;

    private Integer institutionLogoImageId;

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

    public Integer getInstitutionLogoImageId() {
        return institutionLogoImageId;
    }

    public void setInstitutionLogoImageId(Integer institutionLogoImageId) {
        this.institutionLogoImageId = institutionLogoImageId;
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

}
