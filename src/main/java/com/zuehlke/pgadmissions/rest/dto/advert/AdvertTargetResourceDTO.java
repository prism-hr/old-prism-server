package com.zuehlke.pgadmissions.rest.dto.advert;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserSimpleDTO;

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

    public void setUser(UserSimpleDTO user) {
        this.user = user;
    }

    public AdvertTargetResourceDTO withScope(PrismScope scope) {
        this.scope = scope;
        return this;
    }

    public AdvertTargetResourceDTO withId(Integer id) {
        setId(id);
        return this;
    }

    public AdvertTargetResourceDTO withUser(UserDTO user) {
        this.user = user;
        return this;
    }

}
