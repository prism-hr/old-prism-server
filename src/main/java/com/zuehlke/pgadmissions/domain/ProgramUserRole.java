package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.google.common.base.Objects;

@Entity(name = "PROGRAM_USER_ROLE")
public class ProgramUserRole implements Serializable {

    private static final long serialVersionUID = -7617088792206791972L;

    @EmbeddedId
    ProgramUserRolePrimaryKey id;

    public ProgramUserRole(Program program, RegisteredUser user, Role role) {
        setId(program, user, role);
    }

    public ProgramUserRolePrimaryKey getId() {
        return id;
    }

    public void setId(Program program, RegisteredUser user, Role role) {
        id.setProgram(program);
        id.setUser(user);
        id.setRole(role);
    }

    @Embeddable
    public static class ProgramUserRolePrimaryKey implements Serializable {

        private static final long serialVersionUID = 1205515771211781643L;

        @Column(name = "program_id")
        protected Program program;

        @Column(name = "registered_user_id")
        protected RegisteredUser user;

        @Column(name = "application_role_id")
        protected Role role;

        public ProgramUserRolePrimaryKey() {
        }

        public ProgramUserRolePrimaryKey(Program program, RegisteredUser user, Role role) {
            this.program = program;
            this.user = user;
            this.role = role;
        }

        public Program getProgram() {
            return program;
        }

        public void setProgram(Program program) {
            this.program = program;
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
            return Objects.hashCode(program, user, role);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ProgramUserRolePrimaryKey other = (ProgramUserRolePrimaryKey) obj;
            return Objects.equal(program.getId(), other.getProgram().getId()) && Objects.equal(user.getId(), other.getUser().getId())
                    && Objects.equal(role.getId(), other.getRole().getId());
        }

    }

}
