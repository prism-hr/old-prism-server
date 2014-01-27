package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class RoleBuilder {

    private Authority id;
    private Boolean doSendUpdateNotification;

    public RoleBuilder id(Authority id) {
        this.id = id;
        return this;
    }
    
    public RoleBuilder doSendUpdateNotification(Boolean doSendUpdateNotification) {
        this.doSendUpdateNotification = doSendUpdateNotification;
        return this;
    }

    public Role build() {
        Role role = new Role();
        role.setId(id);
        role.setDoSendUpdateNotification(doSendUpdateNotification);
        return role;
    }
    
}