package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ProgramMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadProgram(){
	
		Program program = new Program();
		program.setCode("abcD");
		program.setDescription("I am a program :)");
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
		
		assertEquals("abcD",reloadedProgram.getCode());
		assertEquals("I am a program :)",reloadedProgram.getDescription());
		assertEquals("Program's title",reloadedProgram.getTitle());
		
	}
	
	@Test
	public void shouldSaveAndLoadProgramsWithApprovers(){
		RegisteredUser approverOne = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("usernameOne").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();
		
		RegisteredUser approverTwo = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("usernameTwo").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();

		save(approverOne, approverTwo);
		flushAndClearSession();
		
		Program programOne = new Program();
		programOne.setCode("abcD");
		programOne.setDescription("I am a program :)");
		programOne.setTitle("Program's title");
		programOne.getApprovers().add(approverOne);
		programOne.getApprovers().add(approverTwo);
		
		Program programTwo = new Program();
		programTwo.setCode("DOES NOT EXIST");
		programTwo.setDescription("I am a program :)");
		programTwo.setTitle("Program's title");
		programTwo.getApprovers().add(approverOne);
		programTwo.getApprovers().add(approverTwo);
		
		save(programOne, programTwo);
		flushAndClearSession();
		
		
		Program reloadedProgramOne = (Program) sessionFactory.getCurrentSession().get(Program.class, programOne.getId());
		assertEquals(2, reloadedProgramOne.getApprovers().size());
		assertTrue(reloadedProgramOne.getApprovers().containsAll(Arrays.asList(approverOne, approverTwo)));
		
		
		Program reloadedProgramTwo = (Program) sessionFactory.getCurrentSession().get(Program.class, programTwo.getId());
		assertEquals(2, reloadedProgramTwo.getApprovers().size());
		assertTrue(reloadedProgramTwo.getApprovers().containsAll(Arrays.asList(approverOne, approverTwo)));
	
	}
}
