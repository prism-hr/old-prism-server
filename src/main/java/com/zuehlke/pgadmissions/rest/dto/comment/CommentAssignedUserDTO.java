package com.zuehlke.pgadmissions.rest.dto.comment;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.rest.dto.AssignedUserDTO;

public class CommentAssignedUserDTO {

    @NotNull
    @Valid
    private AssignedUserDTO user;

    @NotNull
    private PrismRole role;

    public AssignedUserDTO getUser() {
        return user;
    }

    public void setUser(AssignedUserDTO user) {
        this.user = user;
    }

    public PrismRole getRole() {
        return role;
    }

    public void setRole(PrismRole role) {
        this.role = role;
    }

}
