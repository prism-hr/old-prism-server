package com.zuehlke.pgadmissions.rest.dto.comment;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

public class CommentAssignedUserDTO {

    @NotNull
    @Valid
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

    public CommentAssignedUserDTO withUser(UserDTO user) {
        this.user = user;
        return this;
    }

    public CommentAssignedUserDTO withRole(PrismRole role) {
        this.role = role;
        return this;
    }

}
