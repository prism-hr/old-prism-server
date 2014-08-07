package com.zuehlke.pgadmissions.rest.representation.application;

import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

public class ApplicationSuggestedSupervisorRepresentation {

    private UserRepresentation user;

    private boolean aware;

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public boolean isAware() {
        return aware;
    }

    public void setAware(boolean aware) {
        this.aware = aware;
    }
    
}
