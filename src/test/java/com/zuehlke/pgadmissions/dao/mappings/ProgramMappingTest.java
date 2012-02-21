package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;

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
}
