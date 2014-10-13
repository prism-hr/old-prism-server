package com.zuehlke.pgadmissions.rest.representation.comment;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

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
