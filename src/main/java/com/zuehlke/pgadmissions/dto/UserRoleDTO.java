package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.user.User;

public class UserRoleDTO {

    private User user;
    
    private PrismRole role;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PrismRole getRole() {
        return role;
    }

    public void setRole(PrismRole role) {
        this.role = role;
    }
    
}
