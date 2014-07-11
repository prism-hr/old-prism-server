package com.zuehlke.pgadmissions.rest.dto.application;

import com.zuehlke.pgadmissions.rest.dto.UserDTO;

public class ApplicationSupervisorDTO {

    private UserDTO user;

    private Boolean aware;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Boolean getAware() {
        return aware;
    }

    public void setAware(Boolean aware) {
        this.aware = aware;
    }
}
