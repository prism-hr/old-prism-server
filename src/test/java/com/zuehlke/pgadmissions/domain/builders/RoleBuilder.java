package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class RoleBuilder {

    private Authority id;

    public RoleBuilder id(Authority id) {
        this.id = id;
        return this;
    }

    public Role build() {
        Role role = new Role();
        role.setId(id);
        return role;
    }
}
