package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.google.common.base.Objects;

@Entity(name = "INSTITUTION_USER_ROLE")
public class InstitutionUserRole implements UserRole {

    @EmbeddedId
    private InstitutionUserRolePrimaryKey id;

    public InstitutionUserRole(Institution institution, User user, Role role) {
        setId(institution, user, role);
    }

    public InstitutionUserRolePrimaryKey getId() {
        return id;
    }

    public void setId(Institution institution, User user, Role role) {
        id.setInstitution(institution);
        id.setUser(user);
        id.setRole(role);
    }

    @Embeddable
    public static class InstitutionUserRolePrimaryKey implements Serializable {

        private static final long serialVersionUID = -8686962234876765526L;

        @Column(name = "institution_id")
        protected Institution institution;

        @Column(name = "registered_user_id")
        protected User user;

        @Column(name = "application_role_id")
        protected Role role;

        public InstitutionUserRolePrimaryKey() {
        }

        public InstitutionUserRolePrimaryKey(Institution institution, User user, Role role) {
            this.institution = institution;
            this.user = user;
            this.role = role;
        }

        public Institution getInstitution() {
            return institution;
        }

        public void setInstitution(Institution institution) {
            this.institution = institution;
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
            return Objects.hashCode(institution, user, role);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final InstitutionUserRolePrimaryKey other = (InstitutionUserRolePrimaryKey) obj;
            return Objects.equal(institution.getId(), other.getInstitution().getId()) && Objects.equal(user.getId(), other.getUser().getId())
                    && Objects.equal(role.getId(), other.getRole().getId());
        }

    }

}
