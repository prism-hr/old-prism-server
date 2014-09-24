package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ProgramTest {

    @Test
    public void shouldCreateProgram() {
        Program program = new ProgramBuilder().id(1).administrators(new RegisteredUser()).approver(new RegisteredUser()).code("code").title("title").build();
        Assert.assertNotNull(program.getCode());
        Assert.assertNotNull(program.getTitle());
        Assert.assertNotNull(program.getAdministrators());
        Assert.assertNotNull(program.getApprovers());
        Assert.assertNotNull(program.getId());
    }

    @Test
    public void shouldReturnTrueIfUserIsApproverOfProgram() {
        RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.APPROVER).build()).build();
        Program program = new ProgramBuilder().id(1).approver(approver).build();
        assertTrue(program.isApprover(approver));
    }

    @Test
    public void shouldReturnFalseIfUserIsNotApproverOfProgram() {
        RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.APPROVER).build()).build();
        Program program = new ProgramBuilder().id(1).build();
        assertFalse(program.isApprover(approver));
    }

    @Test
    public void shouldReturnTrueIfUserIsAdminOfProgram() {
        RegisteredUser admin = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        Program program = new ProgramBuilder().id(1).administrators(admin).build();
        assertTrue(program.isAdministrator(admin));
    }

    @Test
    public void shouldReturnFalseIfUserIsNotAdminOfProgram() {
        RegisteredUser admin = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        Program program = new ProgramBuilder().id(1).build();
        assertFalse(program.isAdministrator(admin));
    }

}