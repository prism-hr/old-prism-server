package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ProgramMappingTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldSaveAndLoadProgram() {

        Program program = new Program();
        program.setCode("abcD");
        program.setTitle("Program's title");
        assertNull(program.getId());

        sessionFactory.getCurrentSession().save(program);
        assertNotNull(program.getId());
        Integer id = program.getId();

        Program reloadedProgram = (Program) sessionFactory.getCurrentSession().get(Program.class, id);
        assertSame(program, reloadedProgram);

        flushAndClearSession();

        reloadedProgram = (Program) sessionFactory.getCurrentSession().get(Program.class, id);
        assertNotSame(program, reloadedProgram);
        assertEquals(program.getId(), reloadedProgram.getId());
        assertEquals("abcD", reloadedProgram.getCode());
        assertEquals("Program's title", reloadedProgram.getTitle());
    }

    @Test
    public void shouldSaveAndLoadProgramWithInstances() {
        Program program = new Program();
        program.setCode("abcD");
        program.setTitle("Program's title");

        save(program);
        ProgramInstance instanceOne = new ProgramInstanceBuilder().applicationDeadline(new Date()).sequence(1).studyOption("1", "Full-time").program(program)
                .applicationStartDate(new Date()).academicYear("2013").enabled(true).build();
        ProgramInstance instanceTwo = new ProgramInstanceBuilder().applicationDeadline(new Date()).sequence(1).studyOption("1", "Full-time").program(program)
                .applicationStartDate(new Date()).academicYear("2013").enabled(true).build();
        save(instanceOne, instanceTwo);
        flushAndClearSession();

        Integer id = program.getId();

        Program reloadedProgram = (Program) sessionFactory.getCurrentSession().get(Program.class, id);
        assertEquals(2, reloadedProgram.getInstances().size());

        assertTrue(listContainsId(instanceOne, reloadedProgram.getInstances()));
        assertTrue(listContainsId(instanceTwo, reloadedProgram.getInstances()));
    }

    @Test
    public void shouldLoadProgramsWithApprovers() {
        Program program = new Program();
        program.setCode("abcD");

        program.setTitle("Program's title");
        save(program);

        RegisteredUser approverOne = new RegisteredUserBuilder().programsOfWhichApprover(program).firstName("Jane").lastName("Doe").email("email@test.com")
                .username("usernameOne").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .build();

        RegisteredUser approverTwo = new RegisteredUserBuilder().programsOfWhichApprover(program).firstName("Jane").lastName("Doe").email("email@test.com")
                .username("usernameTwo").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .build();

        save(approverOne, approverTwo);
        flushAndClearSession();

        Program reloadedProgramOne = (Program) sessionFactory.getCurrentSession().get(Program.class, program.getId());

        assertEquals(2, reloadedProgramOne.getApprovers().size());
        assertTrue(listContainsId(approverOne, reloadedProgramOne.getApprovers()));
        assertTrue(listContainsId(approverTwo, reloadedProgramOne.getApprovers()));
    }

    @Test
    public void shouldLoadProgramsWithAdministrators() {
        Program program = new Program();
        program.setCode("abcD");

        program.setTitle("Program's title");
        save(program);

        RegisteredUser adminOne = new RegisteredUserBuilder().programsOfWhichAdministrator(program).firstName("Jane").lastName("Doe").email("email@test.com")
                .username("usernameOne").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .build();

        RegisteredUser adminTwo = new RegisteredUserBuilder().programsOfWhichAdministrator(program).firstName("Jane").lastName("Doe").email("email@test.com")
                .username("usernameTwo").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
                .build();

        save(adminOne, adminTwo);
        flushAndClearSession();

        Program reloadedProgramOne = (Program) sessionFactory.getCurrentSession().get(Program.class, program.getId());

        assertEquals(2, reloadedProgramOne.getAdministrators().size());
        assertTrue(listContainsId(adminOne, reloadedProgramOne.getAdministrators()));
        assertTrue(listContainsId(adminTwo, reloadedProgramOne.getAdministrators()));
    }

    private boolean listContainsId(ProgramInstance instance, List<ProgramInstance> instances) {
        for (ProgramInstance entry : instances) {
            if (entry.getId().equals(instance.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (entry.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }
}
