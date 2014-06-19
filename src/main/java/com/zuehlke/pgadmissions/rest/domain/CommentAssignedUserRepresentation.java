package com.zuehlke.pgadmissions.rest.domain;

import com.zuehlke.pgadmissions.domain.enums.PrismRole;

public class CommentAssignedUserRepresentation {

    private UserRepresentation user;

    private PrismRole role;

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public PrismRole getRole() {
        return role;
    }

    public void setRole(PrismRole role) {
        this.role = role;
    }
}
