package com.zuehlke.pgadmissions.rest.representation.advert;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationActivity;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class AdvertTargetRepresentation {

    private UserRepresentationSimple user;

    private ResourceRepresentationActivity resource;

    private boolean accepted;

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

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    
}
