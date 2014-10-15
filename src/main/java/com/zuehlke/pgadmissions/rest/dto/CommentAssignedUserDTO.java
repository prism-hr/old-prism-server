package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;

public class CommentAssignedUserDTO {

    @NotNull
    private UserDTO user;

    @NotNull
    private PrismRole role;

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

}
