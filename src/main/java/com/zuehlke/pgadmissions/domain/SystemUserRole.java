package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.google.common.base.Objects;

@Entity(name = "SYSTEM_USER_ROLE")
public class SystemUserRole implements UserRole {

    @EmbeddedId
    SystemUserRolePrimaryKey id;

    public SystemUserRole(User user, Role role) {
        setId(user, role);
    }

    public SystemUserRolePrimaryKey getId() {
        return id;
    }

    public void setId(User user, Role role) {
        id.setUser(user);
        id.setRole(role);
    }

    @Embeddable
    public static class SystemUserRolePrimaryKey implements Serializable {

        private static final long serialVersionUID = -978454472437229221L;

        @Column(name = "registered_user_id")
        protected User user;

        @Column(name = "application_role_id")
        protected Role role;

        public SystemUserRolePrimaryKey() {
        }

        public SystemUserRolePrimaryKey(User user, Role role) {
            this.user = user;
            this.role = role;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(user, role);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SystemUserRolePrimaryKey other = (SystemUserRolePrimaryKey) obj;
            return Objects.equal(user.getId(), other.getUser().getId()) && Objects.equal(role.getId(), other.getRole().getId());
        }

    }

}
