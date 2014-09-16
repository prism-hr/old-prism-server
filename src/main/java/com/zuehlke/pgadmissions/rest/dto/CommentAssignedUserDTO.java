package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;

public class CommentAssignedUserDTO {

    private UserDTO user;

    private PrismRole role;
    
    private PrismRoleTransitionType roleTransitionType;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
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
    
}
