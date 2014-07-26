package com.zuehlke.pgadmissions.rest.representation;


import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

import java.util.List;

public class ResourceRepresentation {

    private Integer id;

    private String code;

    private PrismState state;

    private List<PrismAction> actions;

    private List<CommentRepresentation> comments;

    private List<UserRolesRepresentation> users;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PrismState getState() {
        return state;
    }

    public void setState(PrismState state) {
        this.state = state;
    }

    public List<PrismAction> getActions() {
        return actions;
    }

    public void setActions(List<PrismAction> actions) {
        this.actions = actions;
    }

    public List<CommentRepresentation> getComments() {
        return comments;
    }

    public void setComments(List<CommentRepresentation> comments) {
        this.comments = comments;
    }

    public List<UserRolesRepresentation> getUsers() {
        return users;
    }

    public void setUsers(List<UserRolesRepresentation> users) {
        this.users = users;
    }

    public static class UserRolesRepresentation {

        private Integer id;

        private String firstName;

        private String lastName;

        private String email;

        private List<RoleRepresentation> roles;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public List<RoleRepresentation> getRoles() {
            return roles;
        }

        public void setRoles(List<RoleRepresentation> roles) {
            this.roles = roles;
        }
    }

    public static class RoleRepresentation {

        private PrismRole id;

        private Boolean value;

        public RoleRepresentation() {
        }

        public RoleRepresentation(PrismRole id, Boolean value) {
            this.id = id;
            this.value = value;
        }

        public PrismRole getId() {
            return id;
        }

        public void setId(PrismRole id) {
            this.id = id;
        }

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }
    }
}


