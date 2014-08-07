package com.zuehlke.pgadmissions.rest.representation;

import java.util.List;

public class RegisteredUserRepresentation extends UserExtendedRepresentation {

    private List<RoleAssignmentRepresentation> roles;

    public List<RoleAssignmentRepresentation> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleAssignmentRepresentation> roles) {
        this.roles = roles;
    }
}
    
