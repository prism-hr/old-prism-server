package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

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
		assertEquals(program, reloadedProgram);

		assertEquals("abcD", reloadedProgram.getCode());
		
		assertEquals("Program's title", reloadedProgram.getTitle());

	}
	@Test
	public void shouldSaveAndLoadProgramWithInstances() {

		Program program = new Program();
		program.setCode("abcD");		
		program.setTitle("Program's title");
		
		save(program);
		ProgramInstance instanceOne = new ProgramInstanceBuilder().applicationDeadline(new Date()).sequence(1).studyOption(StudyOption.FULL_TIME).program(program).toProgramInstance();
		ProgramInstance instanceTwo = new ProgramInstanceBuilder().applicationDeadline(new Date()).sequence(1).studyOption(StudyOption.FULL_TIME).program(program).toProgramInstance();
		save(instanceOne, instanceTwo);
		flushAndClearSession();
		
		Integer id = program.getId();

		Program reloadedProgram = (Program) sessionFactory.getCurrentSession().get(Program.class, id);
		assertEquals(2, reloadedProgram.getInstances().size());
		assertTrue(reloadedProgram.getInstances().containsAll(Arrays.asList(instanceOne, instanceTwo)));

	}
	@Test
	public void shouldLoadProgramsWithApprovers() {
		Program program = new Program();
		program.setCode("abcD");
		
		program.setTitle("Program's title");
		save(program);

		RegisteredUser approverOne = new RegisteredUserBuilder().programsOfWhichApprover(program).firstName("Jane").lastName("Doe").email("email@test.com")
				.username("usernameOne").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
				.toUser();

		RegisteredUser approverTwo = new RegisteredUserBuilder().programsOfWhichApprover(program).firstName("Jane").lastName("Doe").email("email@test.com")
				.username("usernameTwo").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
				.toUser();

		save(approverOne, approverTwo);
		flushAndClearSession();

		Program reloadedProgramOne = (Program) sessionFactory.getCurrentSession().get(Program.class, program.getId());
		assertEquals(2, reloadedProgramOne.getApprovers().size());
		assertTrue(reloadedProgramOne.getApprovers().containsAll(Arrays.asList(approverOne, approverTwo)));

	}
	
	@Test
	public void shouldLoadProgramsWithAdministrators() {
		Program program = new Program();
		program.setCode("abcD");
		
		program.setTitle("Program's title");
		save(program);

		RegisteredUser adminOne = new RegisteredUserBuilder().programsOfWhichAdministrator(program).firstName("Jane").lastName("Doe").email("email@test.com")
				.username("usernameOne").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
				.toUser();

		RegisteredUser adminTwo = new RegisteredUserBuilder().programsOfWhichAdministrator(program).firstName("Jane").lastName("Doe").email("email@test.com")
				.username("usernameTwo").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
				.toUser();

		save(adminOne, adminTwo);
		flushAndClearSession();

		Program reloadedProgramOne = (Program) sessionFactory.getCurrentSession().get(Program.class, program.getId());
		assertEquals(2, reloadedProgramOne.getAdministrators().size());
		assertTrue(reloadedProgramOne.getAdministrators().containsAll(Arrays.asList(adminOne, adminTwo)));

	}
	
	@Test
	public void shouldLoadProgramsWithReviewers() {
		Program program = new Program();
		program.setCode("abcD");
		
		program.setTitle("Program's title");
		save(program);

		RegisteredUser reviewerOne = new RegisteredUserBuilder().programsOfWhichReviewer(program).firstName("Jane").lastName("Doe").email("email@test.com")
				.username("usernameOne").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
				.toUser();

		RegisteredUser reviewerTwo = new RegisteredUserBuilder().programsOfWhichReviewer(program).firstName("Jane").lastName("Doe").email("email@test.com")
				.username("usernameTwo").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false)
				.toUser();

		save(reviewerOne, reviewerTwo);
		flushAndClearSession();

		Program reloadedProgramOne = (Program) sessionFactory.getCurrentSession().get(Program.class, program.getId());
		assertEquals(2, reloadedProgramOne.getReviewers().size());
		assertTrue(reloadedProgramOne.getReviewers().containsAll(Arrays.asList(reviewerOne, reviewerTwo)));

	}
}
