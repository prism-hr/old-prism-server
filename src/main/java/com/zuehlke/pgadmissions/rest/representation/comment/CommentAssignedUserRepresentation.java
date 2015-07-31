package com.zuehlke.pgadmissions.rest.representation.comment;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class CommentAssignedUserRepresentation {

    private UserRepresentationSimple user;

    private PrismRole role;
    
    private PrismRoleTransitionType roleTransitionType;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public PrismRole getRole() {
        return role;
    }

    public void setRole(PrismRole role) {
        this.role = role;
    }

    public final PrismRoleTransitionType getRoleTransitionType() {
        return roleTransitionType;
    }

    public final void setRoleTransitionType(PrismRoleTransitionType roleTransitionType) {
        this.roleTransitionType = roleTransitionType;
    }
    
    public CommentAssignedUserRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }
    
    public CommentAssignedUserRepresentation withRole(PrismRole role) {
        this.role = role;
        return this;
    }
    
    public CommentAssignedUserRepresentation withRoleTransitionType(PrismRoleTransitionType roleTransitionType) {
        this.roleTransitionType = roleTransitionType;
        return this;
    }
    
}
