package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

public class ResourceForWhichUserCanCreateChildDTO {

    private ResourceParent resource;
    
    private Boolean patnerMode;

    public ResourceParent getResource() {
        return resource;
    }

    public void setResource(ResourceParent resource) {
        this.resource = resource;
    }

    public Boolean getPatnerMode() {
        return patnerMode;
    }

    public void setPatnerMode(Boolean patnerMode) {
        this.patnerMode = patnerMode;
    }
    
}
