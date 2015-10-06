package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class ResourceRelationInvitationRepresentation {

    private UserRepresentationSimple user;

    private ResourceRepresentationActivity resource;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public ResourceRepresentationActivity getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationActivity resource) {
        this.resource = resource;
    }

    public ResourceRelationInvitationRepresentation withUser(final UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public ResourceRelationInvitationRepresentation withResource(final ResourceRepresentationActivity resource) {
        this.resource = resource;
        return this;
    }


}
