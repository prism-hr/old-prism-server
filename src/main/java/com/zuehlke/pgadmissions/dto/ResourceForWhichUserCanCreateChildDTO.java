package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

public class ResourceForWhichUserCanCreateChildDTO {

    private ResourceParent resource;

    private Boolean partnerMode;

    public ResourceParent getResource() {
        return resource;
    }

    public void setResource(ResourceParent resource) {
        this.resource = resource;
    }

    public Boolean getPartnerMode() {
        return partnerMode;
    }

    public void setPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
    }

    public ResourceForWhichUserCanCreateChildDTO withResource(ResourceParent resource) {
        this.resource = resource;
        return this;
    }

    public ResourceForWhichUserCanCreateChildDTO withPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
        return this;
    }

}
