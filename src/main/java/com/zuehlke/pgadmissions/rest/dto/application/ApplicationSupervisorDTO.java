package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.rest.dto.AssignedUserDTO;

public class ApplicationSupervisorDTO {

    private Integer id;

    @NotNull
    @Valid
    private AssignedUserDTO user;

    private Boolean acceptedSupervision;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public AssignedUserDTO getUser() {
        return user;
    }

    public void setUser(AssignedUserDTO user) {
        this.user = user;
    }

    public Boolean getAcceptedSupervision() {
        return acceptedSupervision;
    }

    public void setAcceptedSupervision(Boolean acceptedSupervision) {
        this.acceptedSupervision = acceptedSupervision;
    }

}
