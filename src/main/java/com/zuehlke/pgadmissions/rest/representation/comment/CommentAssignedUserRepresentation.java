package com.zuehlke.pgadmissions.rest.representation.comment;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.rest.representation.UserExtendedRepresentation;

public class CommentAssignedUserRepresentation {

    private UserExtendedRepresentation user;

    private PrismRole role;

    public UserExtendedRepresentation getUser() {
        return user;
    }

    public void setUser(UserExtendedRepresentation user) {
        this.user = user;
    }

    public PrismRole getRole() {
        return role;
    }

    public void setRole(PrismRole role) {
        this.role = role;
    }
}
