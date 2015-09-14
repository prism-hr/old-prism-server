package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.workflow.user.UserProgramReassignmentProcessor;

@Entity
@Table(name = "user_program", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "program_id" }) })
public class UserProgram implements UniqueEntity, UserAssignment<UserProgramReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "imported_id", nullable = false)
    private Program program;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public UserProgram withUser(User user) {
        this.user = user;
        return this;
    }

    public UserProgram withProgram(Program program) {
        this.program = program;
        return this;
    }

    @Override
    public Class<UserProgramReassignmentProcessor> getUserReassignmentProcessor() {
        return UserProgramReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("user", user).addProperty("program", program);
    }

}
