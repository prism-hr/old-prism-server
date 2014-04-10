package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.google.common.base.Objects;

@Entity(name = "PROJECT_USER_ROLE")
public class ProjectUserRole implements UserRole {

    @EmbeddedId
    ProjectUserRolePrimaryKey id;

    public ProjectUserRole(Project project, RegisteredUser user, Role role) {
        this.setId(project, user, role);
    }

    public ProjectUserRolePrimaryKey getId() {
        return id;
    }

    public void setId(Project project, RegisteredUser user, Role role) {
        id.setProject(project);
        id.setUser(user);
        id.setRole(role);
    }

    @Embeddable
    public static class ProjectUserRolePrimaryKey implements Serializable {

        private static final long serialVersionUID = 332028564987581578L;

        @Column(name = "project_id")
        protected Project project;

        @Column(name = "registered_user_id")
        protected RegisteredUser user;

        @Column(name = "application_role_id")
        protected Role role;

        public ProjectUserRolePrimaryKey() {
        }

        public ProjectUserRolePrimaryKey(Project project, RegisteredUser user, Role role) {
            this.project = project;
            this.user = user;
            this.role = role;
        }

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }

        public RegisteredUser getUser() {
            return user;
        }

        public void setUser(RegisteredUser user) {
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
            return Objects.hashCode(project, user, role);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ProjectUserRolePrimaryKey other = (ProjectUserRolePrimaryKey) obj;
            return Objects.equal(project.getId(), other.getProject().getId()) && Objects.equal(user.getId(), other.getUser().getId())
                    && Objects.equal(role.getId(), other.getRole().getId());
        }

    }

}
