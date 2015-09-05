package com.zuehlke.pgadmissions.rest.dto.advert;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

public class AdvertTargetResourceDTO extends AdvertTargetDTO {

    private PrismScope scope;

    private UserDTO user;

    public PrismScope getScope() {
        return scope;
    }

    public void setScope(PrismScope scope) {
        this.scope = scope;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

}
