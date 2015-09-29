package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;

public class ResourceRelationOutcomeDTO {

    private ResourceParent resourceChild;

    private ResourceParent resourceParent;

    private User user;

    public ResourceRelationOutcomeDTO(ResourceParent resourceChild, ResourceParent resourceParent, User user) {
        this.resourceChild = resourceChild;
        this.resourceParent = resourceParent;
        this.user = user;
    }

    public ResourceParent getResourceChild() {
        return resourceChild;
    }

    public ResourceParent getResourceParent() {
        return resourceParent;
    }

    public User getUser() {
        return user;
    }

}
